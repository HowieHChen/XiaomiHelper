package dev.lackluster.mihelper.data.preference

import dev.lackluster.hyperx.ui.preference.core.PreferenceKey

class DualPreferenceKey<T : Any>(
    val notif: PreferenceKey<T>,
    val island: PreferenceKey<T>
) {
    fun get(isDynamicIsland: Boolean): PreferenceKey<T> {
        return if (isDynamicIsland) island else notif
    }
}