package dev.lackluster.mihelper.hook.rules.systemui.compat

import android.content.res.Resources

@Suppress("DEPRECATION")
class ResourcesWrapper(
    original: Resources,
    private val booleanOverrides: Map<Int, Boolean>
) : Resources(original.assets, original.displayMetrics, original.configuration) {
    override fun getBoolean(id: Int): Boolean {
        return booleanOverrides[id] ?: super.getBoolean(id)
    }
}