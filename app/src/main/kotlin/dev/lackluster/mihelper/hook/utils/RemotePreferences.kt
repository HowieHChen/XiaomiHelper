package dev.lackluster.mihelper.hook.utils

import android.content.SharedPreferences
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.data.preference.Preferences
import io.github.libxposed.api.XposedModule
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

object RemotePreferences {
    private lateinit var remotePrefs: SharedPreferences

    private val observerRoutingTable = ConcurrentHashMap<String, CopyOnWriteArraySet<() -> Unit>>()
    private val globalListener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == null) return@OnSharedPreferenceChangeListener
        observerRoutingTable[changedKey]?.forEach { action ->
            action.invoke()
        }
    }
    private var isGlobalListenerRegistered = AtomicBoolean(false)

    fun init(module: XposedModule) {
        remotePrefs = module.getRemotePreferences(Preferences.NAME)
    }

    private val isInitialized: Boolean
        get() = this::remotePrefs.isInitialized

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getPref(key: PreferenceKey<T>): T {
        if (!isInitialized) return key.default

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
        if (!isInitialized) return {}

        if (isGlobalListenerRegistered.compareAndSet(false, true)) {
            remotePrefs.registerOnSharedPreferenceChangeListener(globalListener)
        }

        val wrappedAction: () -> Unit = { action(this.get()) }
        val observersForThisKey = observerRoutingTable.getOrPut(this.name) { CopyOnWriteArraySet() }
        observersForThisKey.add(wrappedAction)

        if (fireImmediately) {
            action(this.get())
        }

        return {
            observersForThisKey.remove(wrappedAction)
            if (observersForThisKey.isEmpty()) {
                observerRoutingTable.remove(this.name)
            }
        }
    }
}