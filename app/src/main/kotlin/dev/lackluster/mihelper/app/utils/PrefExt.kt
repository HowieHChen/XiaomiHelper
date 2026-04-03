package dev.lackluster.mihelper.app.utils

import dev.lackluster.hyperx.core.SafeSP
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey

@Suppress("UNCHECKED_CAST")
fun <T : Any> getPref(key: PreferenceKey<T>): T = when (key.default) {
    is Boolean -> SafeSP.getBoolean(key.name, key.default as Boolean) as T
    is Int -> SafeSP.getInt(key.name, key.default as Int) as T
    is Long -> SafeSP.getLong(key.name, key.default as Long) as T
    is Float -> SafeSP.getFloat(key.name, key.default as Float) as T
    is String -> SafeSP.getString(key.name, key.default as String) as T
    is Set<*> -> {
        val defSet = (key.default as? Set<String>)?.toMutableSet() ?: mutableSetOf()
        SafeSP.getStringSet(key.name, defSet) as T
    }
    else -> key.default
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> putPref(key: PreferenceKey<T>, value: T) {
    if (value is Set<*>) {
        SafeSP.putStringSet(key.name, value as Set<String>)
    } else {
        SafeSP.putAny(key.name, value as Any)
    }
}