package dev.lackluster.mihelper.utils

import de.robv.android.xposed.XSharedPreferences
import dev.lackluster.mihelper.BuildConfig

object Prefs {
    const val NAME = "config"
    private val xPrefs by lazy {
        XSharedPreferences(BuildConfig.APPLICATION_ID, NAME)
    }

    fun getXSP(prefName: String = NAME): XSharedPreferences {
        return xPrefs
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
}

