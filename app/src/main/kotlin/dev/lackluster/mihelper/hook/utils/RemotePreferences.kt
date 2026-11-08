package dev.lackluster.mihelper.hook.utils

import android.content.SharedPreferences
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.data.preference.Preferences
import io.github.libxposed.api.XposedModule
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

object RemotePreferences {
    @Volatile private var preferences: SharedPreferences? = null
    @Volatile private var generation = 0L

    private val observerRoutingTable = ConcurrentHashMap<String, CopyOnWriteArraySet<() -> Unit>>()
    private val globalListener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == null) return@OnSharedPreferenceChangeListener
        observerRoutingTable[changedKey]?.forEach { action ->
            action.invoke()
        }
    }
    private var isGlobalListenerRegistered = AtomicBoolean(false)

    @Synchronized
    fun init(module: XposedModule) {
        close()
        preferences = module.getRemotePreferences(Preferences.NAME)
    }

    /**
     * 整代退役时不仅清空业务订阅，还必须注销宿主持有的全局监听器，释放对旧模块的引用。
     * 先切换 generation，使已取出的旧订阅闭包在开始执行前也能发现自己失效。
     */
    @Synchronized
    fun close() {
        generation++
        val previous = preferences
        preferences = null
        observerRoutingTable.clear()
        if (isGlobalListenerRegistered.getAndSet(false)) {
            previous?.unregisterOnSharedPreferenceChangeListener(globalListener)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getPref(key: PreferenceKey<T>): T {
        val remotePrefs = preferences ?: return key.default

        return when (key.default) {
            is Boolean -> remotePrefs.getBoolean(key.name, key.default as Boolean) as T
            is Int -> remotePrefs.getInt(key.name, key.default as Int) as T
            is Long -> remotePrefs.getLong(key.name, key.default as Long) as T
            is Float -> remotePrefs.getFloat(key.name, key.default as Float) as T
            is String -> (remotePrefs.getString(key.name, key.default as String) ?: key.default) as T
            is Set<*> -> {
                val defSet = (key.default as? Set<String>) ?: emptySet()
                (remotePrefs.getStringSet(key.name, defSet) ?: defSet) as T
            }
            else -> key.default
        }
    }

    fun <T : Any> PreferenceKey<T>.get(): T = getPref(this)

    fun <T : Any> PreferenceKey<T>.lazyGet(): Lazy<T> = lazy {
        getPref(this)
    }

    fun <T : Any> PreferenceKey<T>.observe(
        fireImmediately: Boolean = true,
        action: (T) -> Unit
    ): () -> Unit {
        val version: Long
        val observersForThisKey: CopyOnWriteArraySet<() -> Unit>
        val wrappedAction: () -> Unit
        synchronized(this@RemotePreferences) {
            val remotePrefs = preferences ?: return {}
            version = generation
            if (isGlobalListenerRegistered.compareAndSet(false, true)) {
                try {
                    remotePrefs.registerOnSharedPreferenceChangeListener(globalListener)
                } catch (failure: Throwable) {
                    isGlobalListenerRegistered.set(false)
                    throw failure
                }
            }
            wrappedAction = {
                // 防止 close 后又 init 时旧订阅误用新 preferences；已进入 action 的调用仍需业务自行协调。
                if (generation == version && preferences != null) action(this.get())
            }
            observersForThisKey = observerRoutingTable.getOrPut(this.name) { CopyOnWriteArraySet() }
            observersForThisKey.add(wrappedAction)
        }
        try {
            // 不持订阅锁调用业务代码，避免其触发 Hooker 生命周期时与 Session 锁形成锁顺序反转。
            if (fireImmediately) wrappedAction()
        } catch (failure: Throwable) {
            observersForThisKey.remove(wrappedAction)
            throw failure
        }

        return {
            observersForThisKey.remove(wrappedAction)
            observerRoutingTable.compute(this.name) { _, current ->
                // 旧取消函数只移除自己那组空订阅，不能删掉相同 key 后来新建的订阅集合。
                if (current === observersForThisKey && current.isEmpty()) null else current
            }
        }
    }
}
