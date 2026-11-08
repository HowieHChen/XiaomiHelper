package dev.lackluster.mihelper.hook.base

import android.os.Bundle
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Executable
import java.util.IdentityHashMap

/**
 * 管理当前目标进程中「一代」模块的根节点、宿主记录和 Hook 句柄，不跨进程共享。
 * 所有挂载、启停和退役操作共用 [lock]；业务配置刷新不需要销毁或重建 Session。
 */
class HookSession(val module: XposedModule) {
    internal val lock = Any()
    @Volatile var isActive: Boolean = true
        private set
    var isReload: Boolean = false
        private set
    private val roots = linkedMapOf<String, BaseHooker>()
    private val hosts = linkedMapOf<String, HostRecord>()
    private val handles = linkedSetOf<OwnedHandle>()
    // 新代暂存框架交来的旧句柄；成功接管就移出，重建结束仍未接管的说明新版已删除或禁用该 Hook。
    private val oldHandles = linkedSetOf<XposedInterface.HookHandle>()
    private var restoredStates: Map<*, *> = emptyMap<Any, Any>()
    private var disposed = false
    private var buildDepth = 0
    internal val isBuilding: Boolean get() = buildDepth != 0

    // 同时保存默认与 Ready 阶段的加载器；安装根规则沿用默认加载器，避免本次改造改变业务时机。
    data class HostRecord(
        val key: String,
        val classLoader: ClassLoader,
        val param: HookParam,
        val readyClassLoader: ClassLoader? = null,
    )

    fun recordHost(host: HostRecord) = synchronized(lock) {
        check(isActive)
        hosts[host.key] = host
    }

    fun recordedHosts(): List<HostRecord> = synchronized(lock) { hosts.values.toList() }

    fun attachRoot(hooker: BaseHooker, host: HostRecord) = synchronized(lock) {
        check(isActive)
        val key = "${host.key}/${hooker.hookerId}"
        hooker.bind(this, host.classLoader, host.param, key)
        if (roots[key] === hooker) return@synchronized
        check(key !in roots) { "Duplicate root: $key" }
        roots[key] = hooker
        buildDepth++
        try {
            hooker.performInit()
            hooker.activateInitially(true)
            if (isReload) hooker.notifyReloadReady()
        } catch (failure: Throwable) {
            roots.remove(key)
            try { hooker.disposeTree(false) } catch (cleanup: Throwable) { failure.addSuppressed(cleanup) }
            throw failure
        } finally {
            buildDepth--
        }
    }

    fun reloadBlockers(): List<String> = synchronized(lock) {
        roots.values.flatMap { it.unsupportedReloadNodes() }
    }

    /**
     * 旧代交接：先全树检查，再冻结框架注册入口、导出并校验状态，最后执行不可逆的 stop/dispose。
     * save 负责交给 libxposed；此步骤失败时尚未清理资源，可以恢复 Session 的可用状态。
     * 冻结只约束框架入口和后续回调，业务仍需自行停止已运行的异步任务，不能假定宿主整体被暂停。
     */
    fun retireForReload(save: (Any) -> Unit): Boolean = synchronized(lock) {
        if (!isActive || reloadBlockers().isNotEmpty()) return@synchronized false
        isActive = false
        try {
            val states = linkedMapOf<String, Any?>()
            roots.values.forEach { it.saveState(states) }
            // 不能直接传 HostRecord/HookParam：它们由旧模块加载器定义。拆成系统容器与中立值传递。
            val snapshot = hashMapOf<String, Any?>(
                "version" to 1,
                "hosts" to ArrayList(hosts.values.map { host ->
                    arrayListOf(host.key, host.classLoader, host.param.processName, host.param.packageName,
                        host.param.isSystemServer, host.param.isFirstPackage, host.param.isPackageReady,
                        host.param.appInfo, host.readyClassLoader)
                }),
                "hookers" to HashMap(states),
            )
            validateReloadState(snapshot, module.javaClass.classLoader)
            save(snapshot)
        } catch (failure: Throwable) {
            isActive = true // 这里只回退冻结状态；尚未进入资源销毁，因此不存在业务资源重建。
            throw failure
        }
        // 此后失败不再自动恢复旧业务。句柄暂留给新代接管，任务、订阅等外部资源仍必须清理。
        disposeLocked(keepHooks = true)
        true
    }

    /** 在新代重建根节点前调用；匿名旧 Hook 无法稳定匹配，采用先卸载后重装，会有短暂空窗。 */
    fun beginReload(state: Any?, previousHooks: List<XposedInterface.HookHandle>) = synchronized(lock) {
        check(isActive && roots.isEmpty() && !isReload)
        // 先接管所有旧句柄，即使状态解析失败，入口的失败清理也能将它们全部卸载。
        oldHandles.addAll(previousHooks)
        val snapshot = state as? Map<*, *> ?: error("Missing reload state")
        check(snapshot["version"] == 1) { "Unsupported reload state version" }
        restoredStates = snapshot["hookers"] as? Map<*, *> ?: error("Missing hooker states")
        val savedHosts = snapshot["hosts"] as? List<*> ?: error("Missing hosts")
        savedHosts.forEach { value ->
            val row = value as? List<*> ?: error("Invalid host record")
            check(row.size == 9)
            val host = HostRecord(
                key = row[0] as String,
                classLoader = row[1] as ClassLoader,
                param = HookParam(row[2] as String, row[3] as String, row[4] as Boolean,
                    row[5] as Boolean, row[6] as Boolean, row[7] as android.content.pm.ApplicationInfo?),
                readyClassLoader = row[8] as ClassLoader?,
            )
            hosts[host.key] = host
        }
        isReload = true
        oldHandles.filter { it.id == null }.toList().forEach { old ->
            old.unhook()
            oldHandles.remove(old)
        }
    }

    fun finishReload() = synchronized(lock) {
        val failures = CleanupFailures()
        oldHandles.toList().forEach { old -> failures.attempt { old.unhook(); oldHandles.remove(old) } }
        // 不清空中立状态：插件节点可能在这次回调结束后才被发现，仍需按路径恢复自己的状态。
        failures.throwIfAny()
    }

    internal fun stateFor(key: String): Any? = restoredStates[key]

    fun dispose() = synchronized(lock) {
        isActive = false
        disposeLocked(keepHooks = false)
    }

    private fun disposeLocked(keepHooks: Boolean) {
        if (disposed && keepHooks) return
        disposed = true
        val failures = CleanupFailures()
        roots.values.toList().asReversed().forEach { root -> failures.attempt { root.disposeTree(keepHooks) } }
        if (!keepHooks) {
            // 兜底清理本代残留及未接管的旧句柄，包含 managed=false；避免失败后留下半套新旧实现。
            handles.toList().forEach { handle -> failures.attempt { handle.unhook() } }
            oldHandles.toList().forEach { handle -> failures.attempt { handle.unhook(); oldHandles.remove(handle) } }
        }
        roots.clear()
        hosts.clear()
        restoredStates = emptyMap<Any, Any>()
        failures.throwIfAny()
    }

    internal fun hasManagedHooks(owner: BaseHooker): Boolean = synchronized(lock) {
        handles.any { it.owner === owner && it.managed }
    }

    internal fun removeHooks(owner: BaseHooker, activationOnly: Boolean, includeUnmanaged: Boolean = false) {
        val failures = CleanupFailures()
        handles.filter { it.owner === owner && (!activationOnly || it.activation) && (it.managed || includeUnmanaged) }
            .forEach { handle -> failures.attempt { handle.unhook() } }
        failures.throwIfAny()
    }

    internal fun install(
        owner: BaseHooker, executable: Executable, managed: Boolean, activation: Boolean, token: Long,
        id: String?, priority: Int, exceptionMode: XposedInterface.ExceptionMode,
        hooker: XposedInterface.Hooker,
    ): XposedInterface.HookHandle {
        check(isActive)
        val builder = module.hook(executable).setPriority(priority).setExceptionMode(exceptionMode)
        if (id != null) {
            check(module.apiVersion >= XposedInterface.API_102) { "Hook IDs require API 102" }
            builder.setId(id)
        }
        // 必须在替换前读取旧句柄元数据：intercept 成功后，同方法同 ID 的旧句柄可能已失效。
        val replacedOld = if (id == null) emptyList() else oldHandles.filter { it.executable == executable && it.id == id }
        val replacedCurrent = if (id == null) emptyList() else handles.filter { it.executable == executable && it.id == id }
        val raw = builder.intercept(hooker)
        if (id != null) {
            // 同 ID 替换同时覆盖跨代接管和本代重复注册。这里只作废记录，不能再 unhook 误伤新安装。
            oldHandles.removeAll(replacedOld.toSet())
            replacedCurrent.forEach { it.invalidate() }
        }
        return OwnedHandle(raw, owner, managed, activation, token).also { handles.add(it) }
    }

    private inner class OwnedHandle(
        private val raw: XposedInterface.HookHandle,
        val owner: BaseHooker,
        val managed: Boolean,
        val activation: Boolean,
        val token: Long,
    ) : XposedInterface.HookHandle {
        private var valid = true
        // 缓存元数据，避免调用方在替换后查询旧包装句柄时，再访问已经失效的底层句柄。
        private val target = raw.executable
        private val key = if (module.apiVersion >= XposedInterface.API_102) raw.id else null
        override fun getExecutable(): Executable = target
        override fun getId(): String? = key
        fun invalidate() { valid = false; handles.remove(this) }
        override fun unhook() = synchronized(lock) {
            if (valid) { raw.unhook(); invalidate() }
        }
        override fun replaceHook(hooker: XposedInterface.Hooker): XposedInterface.HookHandle = synchronized(lock) {
            check(isActive && owner.isActive && valid) { "Retired or invalid hook handle" }
            val guarded = XposedInterface.Hooker { chain ->
                if (owner.callbackAllowed(activation, token)) hooker.intercept(chain) else chain.proceed()
            }
            val replacement = raw.replaceHook(guarded)
            // 只有底层替换成功才更新管理记录；新句柄继承原归属和启用周期，仍受 stop/dispose 管理。
            invalidate()
            OwnedHandle(replacement, owner, managed, activation, token).also { handles.add(it) }
        }
    }
}

/**
 * 跨代状态的诊断检查：拒绝旧模块及其子加载器创建的对象，并递归检查常见容器、Class 和 ClassLoader。
 * 不是完整的对象图验证器；不会遍历任意宿主对象字段，业务必须保证其中没有隐藏的旧模块引用。
 */
internal fun validateReloadState(value: Any?, moduleLoader: ClassLoader?) {
    val visited = IdentityHashMap<Any, Boolean>()
    fun isModuleLoader(loader: ClassLoader?): Boolean {
        var current = loader
        while (current != null) {
            if (current === moduleLoader) return true
            current = current.parent
        }
        return false
    }
    fun visit(item: Any?) {
        if (item == null || visited.put(item, true) != null) return
        require(!isModuleLoader(item.javaClass.classLoader)) { "Reload state contains old module object: ${item.javaClass.name}" }
        if (item is ClassLoader) require(!isModuleLoader(item)) { "Cannot save module ClassLoader" }
        if (item is Class<*>) require(!isModuleLoader(item.classLoader)) { "Cannot save module Class" }
        when (item) {
            is Map<*, *> -> item.forEach { (key, entry) -> visit(key); visit(entry) }
            is Collection<*> -> item.forEach { visit(it) }
            is Array<*> -> item.forEach { visit(it) }
            is Bundle -> item.keySet().forEach { @Suppress("DEPRECATION") visit(item.get(it)) }
        }
    }
    visit(value)
}
