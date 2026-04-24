package dev.lackluster.mihelper.app.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.manager.XposedServiceManager
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RemotePreferenceStore(
    private val xposedManager: XposedServiceManager
) {
    private val remotePrefs: SharedPreferences?
        get() = xposedManager.currentService?.getRemotePreferences(Preferences.NAME)

    private val _globalReloadEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val globalReloadEvent = _globalReloadEvent.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            xposedManager.serviceFlow.collect { service ->
                if (service != null) {
                    _globalReloadEvent.emit(Unit)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: PreferenceKey<T>): T {
        val prefs = remotePrefs ?: return key.default

        return when (key.default) {
            is Boolean -> prefs.getBoolean(key.name, key.default as Boolean) as T
            is Int -> prefs.getInt(key.name, key.default as Int) as T
            is Long -> prefs.getLong(key.name, key.default as Long) as T
            is Float -> prefs.getFloat(key.name, key.default as Float) as T
            is String -> prefs.getString(key.name, key.default as String) as T
            is Set<*> -> {
                val defSet = (key.default as? Set<String>)?.toMutableSet() ?: mutableSetOf()
                prefs.getStringSet(key.name, defSet) as T
            }
            else -> key.default
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> put(key: PreferenceKey<T>, value: T) {
        remotePrefs?.edit {
            if (value is Set<*>) {
                putStringSet(key.name, value as Set<String>)
            } else {
                when (value) {
                    is Boolean -> putBoolean(key.name, value)
                    is Int -> putInt(key.name, value)
                    is Long -> putLong(key.name, value)
                    is Float -> putFloat(key.name, value)
                    is String -> putString(key.name, value)
                }
            }
        }
    }

    fun setAll(map: Map<String, Any>) {
        remotePrefs?.edit(true) {
            map.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is String -> putString(key, value)
                    is Set<*> -> {
                        putStringSet(key, value.filterIsInstance<String>().toSet())
                    }
                }
            }
        }
    }

    fun getAll(): Map<String, *>? = remotePrefs?.all

    fun clearAll() = remotePrefs?.edit(true) {
        clear()
    }
}