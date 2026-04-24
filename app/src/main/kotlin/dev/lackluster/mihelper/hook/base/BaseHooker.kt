package dev.lackluster.mihelper.hook.base

import com.highcapable.kavaref.extension.toClass
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.MethodResolver
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.d
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Executable
import java.util.concurrent.CopyOnWriteArraySet

private class HookScopeImpl(
    chain: XposedInterface.Chain
) : HookScope, XposedInterface.Chain by chain

sealed class BaseHooker {
    lateinit var module: XposedModule
    lateinit var classLoader: ClassLoader
    lateinit var hookParam: HookParam

    private val hookHandles = CopyOnWriteArraySet<XposedInterface.HookHandle>()
    private val childHookers = CopyOnWriteArraySet<BaseHooker>()

    val isHooked: Boolean
        get() = hookHandles.isNotEmpty()

    open val requireDexKit: Boolean = false

    open val hookerName: String
        get() = this::class.java.simpleName

    val isMainProcess: Boolean
        get() = hookParam.isMainProcess

    private var isSelfEnabled: Boolean = true
    private var isParentEnabled: Boolean = false

    private val isEffectiveEnabled: Boolean
        get() = isSelfEnabled && isParentEnabled

    @Synchronized
    fun updateSelfState(enabled: Boolean) {
        val oldState = isEffectiveEnabled
        isSelfEnabled = enabled
        handleStateChange(oldState)
    }

    @Synchronized
    fun updateParentState(parentEnabled: Boolean) {
        val oldState = isEffectiveEnabled
        isParentEnabled = parentEnabled
        handleStateChange(oldState)
    }

    private fun handleStateChange(oldState: Boolean) {
        val newState = isEffectiveEnabled

        if (oldState != newState) {
            if (newState) {
                d { "Hook" }
                if (!isHooked) onHook()
            } else {
                when (this) {
                    is StaticHooker -> {
                        if (isHooked) d { "StaticHooker! Unhook skipped"}
                    }
                    is DynamicHooker -> {
                        d { "Unhook" }
                        hookHandles.forEach { it.unhook() }
                        hookHandles.clear()
                    }
                }
            }

            childHookers.forEach { it.updateParentState(newState) }
        }
    }

    open fun onInit() {}

    open fun onHook() {}

    internal fun performInit() {
        if (requireDexKit) DexKit.retain(hookParam)
        try {
            onInit()
        } finally {
            if (requireDexKit) DexKit.release()
        }
    }

    fun attach(
        hooker: BaseHooker,
        customClassLoader: ClassLoader? = null,
        param: HookParam? = null
    ) {
        if (childHookers.contains(hooker)) return

        hooker.module = this.module
        hooker.classLoader = customClassLoader ?: this.classLoader
        hooker.hookParam = param ?: this.hookParam

        childHookers.add(hooker)

        hooker.performInit()
        hooker.updateParentState(isEffectiveEnabled)
    }

    fun Executable.hook(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ): XposedInterface.HookHandle {
        val originalHandle = module.hook(this).intercept { chain ->
            val scope = HookScopeImpl(chain)
            val hookResult = scope.callback()
            hookResult.value
        }
        if (!managed) {
            return originalHandle
        }

        val managedHandle = object : XposedInterface.HookHandle {
            override fun getExecutable(): Executable = originalHandle.executable

            override fun unhook() {
                originalHandle.unhook()
                hookHandles.remove(this)
            }
        }

        hookHandles.add(managedHandle)
        return managedHandle
    }

    fun <T : Any> MethodResolver<T>.hook(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ) = this.self.hook(managed, callback)

    fun <T: Any> ConstructorResolver<T>.hook(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ) = this.self.hook(managed, callback)

    @JvmName("hookAllMethods")
    fun <T : Any> Iterable<MethodResolver<T>?>.hookAll(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ) = mapNotNull { it?.hook(managed, callback) }

    @JvmName("hookAllExecutable")
    fun Iterable<Executable?>.hookAll(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ) = mapNotNull { it?.hook(managed, callback) }

    @JvmName("hookAllConstructors")
    fun <T: Any> Iterable<ConstructorResolver<T>?>.hookAll(
        managed: Boolean = true,
        callback: HookScope.() -> HookResult
    ) = mapNotNull { it?.hook(managed, callback) }

    fun String.toClass(initialize: Boolean = false): Class<Any> {
        return this.toClass(loader = classLoader, initialize = initialize)
    }

    fun String.toClassOrNull(initialize: Boolean = false): Class<Any>? {
        return this.toClassOrNull(loader = classLoader, initialize = initialize)
    }

    fun String.lazyClass(initialize: Boolean = false): Lazy<Class<Any>> = lazy {
        this.toClass(loader = classLoader, initialize = initialize)
    }

    fun String.lazyClassOrNull(initialize: Boolean = false): Lazy<Class<Any>?> = lazy {
        this.toClassOrNull(loader = classLoader, initialize = initialize)
    }
}

abstract class StaticHooker : BaseHooker()

abstract class DynamicHooker : BaseHooker()