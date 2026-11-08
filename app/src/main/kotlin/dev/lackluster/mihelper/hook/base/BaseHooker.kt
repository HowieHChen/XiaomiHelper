package dev.lackluster.mihelper.hook.base

import com.highcapable.kavaref.extension.toClass
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.MethodResolver
import dev.lackluster.mihelper.hook.utils.DexKit
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Executable

private class HookScopeImpl(chain: XposedInterface.Chain) : HookScope, XposedInterface.Chain by chain

/**
 * 一个实例只经历一次 init/dispose，其间可以经历多次 start/stop。
 * 配置热更新只更新业务数据；功能启停改变当前实例的运行状态；模块热重载则销毁旧代并创建新代实例。
 * 生命周期和注册操作共用 Session 锁，业务回调不能等待另一个必须获取此锁才能结束的任务。
 */
abstract class BaseHooker {
    lateinit var module: XposedModule
        private set
    lateinit var classLoader: ClassLoader
        private set
    lateinit var hookParam: HookParam
        private set
    internal lateinit var session: HookSession
        private set
    internal lateinit var bindingKey: String
        private set

    private val children = linkedSetOf<BaseHooker>()
    // 两份列表分别属于「一次启用」和「整个实例」；关闭功能时保留配置订阅，才能监听再次开启。
    private val stopCleanups = mutableListOf<() -> Unit>()
    private val disposeCleanups = mutableListOf<() -> Unit>()
    private var initialized = false
    private var initializing = false
    private var reloadReady = false
    private var selfEnabled = true
    private var parentEnabled = false
    private var starting = false
    @Volatile private var disposed = false
    @Volatile private var stopping = false
    @Volatile var isRunning: Boolean = false
        private set
    // 每次 start 都递增；仅检查 isRunning 无法识别「关闭后又开启」时迟到的旧异步结果。
    @Volatile internal var activationToken: Long = 0
        private set

    open val requireDexKit: Boolean = false
    open val hookerName: String get() = javaClass.simpleName
    /** 跨代状态按挂载路径定位；导出状态的规则宜显式指定稳定 ID。默认类名由 R8 规则保留。 */
    open val hookerId: String get() = javaClass.name
    // 必须由业务审查清理、重新绑定能力后主动声明；统一基类不代表旧规则自动支持动态操作。
    open val supportsRuntimeToggle: Boolean = false
    open val supportsHotReload: Boolean = false
    val isMainProcess: Boolean get() = hookParam.isMainProcess
    val isHooked: Boolean get() = session.hasManagedHooks(this)
    val isActive: Boolean get() = !stopping && !disposed && session.isActive
    private val effectiveEnabled: Boolean get() = selfEnabled && parentEnabled

    open fun onInit() {}
    // 兼容未迁移的业务：框架统一调用 onStart，旧 onHook 覆写仍然生效。
    @Suppress("DEPRECATION")
    open fun onStart() { onHook() }
    @Deprecated("Override onStart instead")
    open fun onHook() {}
    open fun onStop() {}
    open fun onDispose() {}
    open fun onSaveReloadState(): Any? = null
    open fun onRestoreReloadState(state: Any?) {}
    open fun onReloadReady() {}

    fun updateSelfState(enabled: Boolean): StateUpdateResult = synchronized(session.lock) {
        if (!isActive) return@synchronized StateUpdateResult.DISPOSED
        if (starting) return@synchronized StateUpdateResult.BUSY
        if (selfEnabled == enabled) return@synchronized StateUpdateResult.UNCHANGED
        // onInit 中只决定初始开关；运行中则先检查整个受影响子树，拒绝时不改变当前状态。
        if (initialized && !canTransition(enabled && parentEnabled)) return@synchronized StateUpdateResult.RESTART_REQUIRED
        selfEnabled = enabled
        if (initialized) reconcile()
        StateUpdateResult.APPLIED
    }

    fun updateParentState(enabled: Boolean): StateUpdateResult = synchronized(session.lock) {
        if (!isActive) return@synchronized StateUpdateResult.DISPOSED
        if (starting) return@synchronized StateUpdateResult.BUSY
        if (parentEnabled == enabled) return@synchronized StateUpdateResult.UNCHANGED
        if (initialized && !canTransition(selfEnabled && enabled)) return@synchronized StateUpdateResult.RESTART_REQUIRED
        parentEnabled = enabled
        if (initialized) reconcile()
        StateUpdateResult.APPLIED
    }

    private fun canTransition(target: Boolean): Boolean {
        return !(target != isRunning && !supportsRuntimeToggle) && children.all { it.canTransition(target && it.selfEnabled) }
    }

    private fun reconcile() {
        if (effectiveEnabled && !isRunning) start()
        if (!effectiveEnabled && isRunning) {
            // 先停子节点再停父节点；一个子节点清理失败，也必须继续清理其余节点和父节点。
            val failures = CleanupFailures()
            children.toList().asReversed().forEach { child ->
                failures.attempt { child.parentEnabled = false; child.reconcile() }
            }
            failures.attempt { stop(keepHooks = false) }
            failures.throwIfAny()
        } else {
            children.toList().forEach { it.parentEnabled = effectiveEnabled; it.reconcile() }
        }
    }

    private fun start() {
        check(isActive && initialized)
        starting = true
        activationToken++
        // 先进入运行态，使 onStart 内注册的 Hook 被归入本次启用周期；失败时清理部分安装结果。
        isRunning = true
        try {
            onStart()
        } catch (failure: Throwable) {
            try { stop(false) } catch (cleanup: Throwable) { failure.addSuppressed(cleanup) }
            throw failure
        } finally {
            starting = false
        }
    }

    private fun stop(keepHooks: Boolean) {
        if (!isRunning) return
        stopping = true
        isRunning = false
        val failures = CleanupFailures()
        failures.attempt { onStop() }
        failures.drain(stopCleanups)
        // 普通 stop 卸载业务 Hook；换代时暂留句柄供新代原子接管，旧回调由有效性检查透传。
        if (!keepHooks) failures.attempt { session.removeHooks(this, activationOnly = true) }
        stopping = false
        failures.throwIfAny()
    }

    /** 只登记本次启用结束时执行的动作；若资源在停止后才到达，则立即清理，不留到下次启用。 */
    protected fun registerStopCleanup(action: () -> Unit) = synchronized(session.lock) {
        if (!isActive || !isRunning) action() else { stopCleanups.add(action); Unit }
    }

    /** 登记实例最终销毁时的清理，例如取消配置订阅；普通 stop 不执行此列表。 */
    protected fun registerDisposeCleanup(action: () -> Unit) = synchronized(session.lock) {
        if (!isActive) action() else { disposeCleanups.add(action); Unit }
    }

    internal fun bind(owner: HookSession, loader: ClassLoader, param: HookParam, key: String) {
        // 不能直接重绑 ClassLoader：实例的 lazy 反射缓存可能仍指向旧类。替换必须 detach 后创建新实例。
        check(!disposed) { "$hookerName has been disposed; create a new instance" }
        if (this::session.isInitialized) {
            check(session === owner && classLoader === loader && bindingKey == key && hookParam == param) {
                "$hookerName is already bound; create a new instance for a different host"
            }
            return
        }
        session = owner
        module = owner.module
        classLoader = loader
        hookParam = param
        bindingKey = key
    }

    internal fun performInit() {
        if (initialized) return
        check(!initializing && isActive) { "Recursive or retired initialization: $bindingKey" }
        initializing = true
        try {
            // 先恢复状态，再初始化；业务可以用恢复的宿主引用建立本代自己的资源和子节点。
            if (session.isReload) onRestoreReloadState(session.stateFor(bindingKey))
            if (requireDexKit) DexKit.retain(hookParam)
            try { onInit() } finally { if (requireDexKit) DexKit.release() }
            initialized = true
        } finally { initializing = false }
    }

    internal fun activateInitially(enabled: Boolean) {
        parentEnabled = enabled
        reconcile()
    }

    internal fun notifyReloadReady() {
        if (!reloadReady) {
            onReloadReady()
            reloadReady = true
        }
        children.toList().forEach { it.notifyReloadReady() }
    }

    fun attach(hooker: BaseHooker, customClassLoader: ClassLoader? = null, param: HookParam? = null) = synchronized(session.lock) {
        check(isActive) { "Cannot attach to a retired hooker" }
        val key = "$bindingKey/${hooker.hookerId}"
        hooker.bind(session, customClassLoader ?: classLoader, param ?: hookParam, key)
        if (hooker in children) return@synchronized
        check(children.none { it.hookerId == hooker.hookerId }) { "Duplicate child id: $key" }
        children.add(hooker)
        try {
            hooker.performInit()
            hooker.activateInitially(effectiveEnabled)
            if (session.isReload && !session.isBuilding) hooker.notifyReloadReady()
        } catch (failure: Throwable) {
            children.remove(hooker)
            try { hooker.disposeTree(false) } catch (cleanup: Throwable) { failure.addSuppressed(cleanup) }
            throw failure
        }
    }

    /**
     * 最终销毁一个直接子节点及其子树，清理登记资源和全部已跟踪 Hook（包括 managed=false）。
     * 非直接子节点或重复移除返回 false。清理失败仍移除节点并抛出异常，不表示清理已全部成功。
     * 此操作不要求支持反复启停；旧实例不可复用，新实例可在移除后使用相同 hookerId。
     */
    fun detach(hooker: BaseHooker): Boolean = synchronized(session.lock) {
        val child = children.firstOrNull { it === hooker } ?: return@synchronized false
        child.checkDetachable()
        try {
            child.disposeTree(keepHooks = false)
        } finally {
            children.remove(child)
        }
        true
    }

    private fun checkDetachable() {
        // Session 锁允许同线程重入，仍需拒绝在 init/start/stop 尚未结束时销毁子树。
        check(!initializing && !starting && !stopping) { "Cannot detach during a lifecycle transition: $bindingKey" }
        children.toList().forEach { it.checkDetachable() }
    }

    internal fun unsupportedReloadNodes(): List<String> = buildList {
        // 禁用节点也可能在 onInit 注册了监听器或 Hook，因此不能只检查正在运行的节点。
        if (!supportsHotReload) add(bindingKey)
        children.forEach { addAll(it.unsupportedReloadNodes()) }
    }

    internal fun saveState(destination: MutableMap<String, Any?>) {
        destination[bindingKey] = onSaveReloadState()
        children.toList().forEach { it.saveState(destination) }
    }

    internal fun disposeTree(keepHooks: Boolean) {
        if (disposed) return
        // 在用户清理回调之前关闭注册入口，避免清理过程或迟到回调重新挂载子节点、安装 Hook。
        disposed = true
        val failures = CleanupFailures()
        children.toList().asReversed().forEach { child -> failures.attempt { child.disposeTree(keepHooks) } }
        failures.attempt { stop(keepHooks) }
        failures.attempt { onDispose() }
        failures.drain(disposeCleanups)
        if (!keepHooks) failures.attempt { session.removeHooks(this, activationOnly = false, includeUnmanaged = true) }
        children.clear()
        failures.throwIfAny()
    }

    /** 在 onStart 捕获，异步结果写宿主前用 isCurrentActivation 复查；检查不能代替任务取消。 */
    protected fun captureActivation(): Long = activationToken
    protected fun isCurrentActivation(token: Long): Boolean = isActive && isRunning && activationToken == token

    internal fun callbackAllowed(activation: Boolean, token: Long): Boolean =
        isActive && (!activation || (isRunning && activationToken == token))

    fun Executable.hook(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ): XposedInterface.HookHandle = synchronized(session.lock) {
        check(isActive) { "Cannot register a hook after retirement" }
        // onInit 安装的 Hook 随实例销毁；运行期间安装的 Hook 随本次启用停止。
        val activation = isRunning
        val token = activationToken
        session.install(this@BaseHooker, this, managed, activation, token, id, priority, exceptionMode) { chain ->
            // 透传仍会经过后续 Hook，不是绕过其他模块直接调用原方法；执行中的旧回调不会被强行终止。
            if (!callbackAllowed(activation, token)) chain.proceed() else HookScopeImpl(chain).callback().value
        }
    }

    fun <T : Any> MethodResolver<T>.hook(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ) = self.hook(managed, id, priority, exceptionMode, callback)

    fun <T : Any> ConstructorResolver<T>.hook(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ) = self.hook(managed, id, priority, exceptionMode, callback)

    @JvmName("hookAllMethods")
    fun <T : Any> Iterable<MethodResolver<T>?>.hookAll(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ) = mapNotNull { it?.hook(managed, id, priority, exceptionMode, callback) }

    @JvmName("hookAllExecutable")
    fun Iterable<Executable?>.hookAll(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ) = mapNotNull { it?.hook(managed, id, priority, exceptionMode, callback) }

    @JvmName("hookAllConstructors")
    fun <T : Any> Iterable<ConstructorResolver<T>?>.hookAll(
        managed: Boolean = true, id: String? = null,
        priority: Int = XposedInterface.PRIORITY_DEFAULT,
        exceptionMode: XposedInterface.ExceptionMode = XposedInterface.ExceptionMode.DEFAULT,
        callback: HookScope.() -> HookResult,
    ) = mapNotNull { it?.hook(managed, id, priority, exceptionMode, callback) }

    fun String.toClass(initialize: Boolean = false): Class<Any> = toClass(loader = classLoader, initialize = initialize)
    fun String.toClassOrNull(initialize: Boolean = false): Class<Any>? = toClassOrNull(loader = classLoader, initialize = initialize)
    fun String.lazyClass(initialize: Boolean = false): Lazy<Class<Any>> = lazy { toClass(initialize) }
    fun String.lazyClassOrNull(initialize: Boolean = false): Lazy<Class<Any>?> = lazy { toClassOrNull(initialize) }
}

enum class StateUpdateResult { APPLIED, UNCHANGED, RESTART_REQUIRED, BUSY, DISPOSED }

// 仅保留源码兼容名称，不再承担两套不同的启停策略。
typealias StaticHooker = BaseHooker
typealias DynamicHooker = BaseHooker

internal class CleanupFailures {
    private var failure: Throwable? = null
    fun attempt(action: () -> Unit) {
        try { action() } catch (t: Throwable) {
            val first = failure
            if (first == null) failure = t else if (first !== t) first.addSuppressed(t)
        }
    }
    fun drain(actions: MutableList<() -> Unit>) {
        // 后登记的资源先释放；执行前清空列表，防止重入或重复销毁再次执行同一批动作。
        val snapshot = actions.toList().asReversed()
        actions.clear()
        snapshot.forEach { attempt(it) }
    }
    fun throwIfAny() { failure?.let { throw it } }
}
