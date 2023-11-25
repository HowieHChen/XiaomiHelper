package dev.lackluster.mihelper.utils

import de.robv.android.xposed.XSharedPreferences
import dev.lackluster.mihelper.BuildConfig

object Prefs {
    const val Name = "config"
    private var xPrefs = XSharedPreferences(BuildConfig.APPLICATION_ID, Name)

    fun getXSP(prefName: String = Name): XSharedPreferences {
        return xPrefs;
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        return xPrefs.getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        return xPrefs.getInt(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        return xPrefs.getFloat(key, defValue)
    }

    fun getString(key: String, defValue: String): String? {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        return xPrefs.getString(key, defValue)
    }

    fun getStringSet(key: String, defValue: MutableSet<String>): MutableSet<String> {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        return xPrefs.getStringSet(key, defValue) ?: defValue
    }

    inline fun hasEnable(
        key: String,
        default: Boolean = false,
        noinline extraCondition: (() -> Boolean)? = null,
        crossinline block: () -> Unit
    ) {
        val conditionResult = if (extraCondition != null) extraCondition() else true
        if (getBoolean(key, default) && conditionResult) {
            block()
        }
    }
}

