package dev.lackluster.mihelper.utils

import android.content.Context
import cn.fkj233.ui.activity.MIUIActivity
import de.robv.android.xposed.XSharedPreferences
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.utils.factory.dp

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

    fun getPixelByStr(key: String, defStr: String = "0px", context: Context): Int {
        if (xPrefs.hasFileChanged()) {
            xPrefs.reload()
        }
        val value = getString(key, defStr) ?: defStr
        runCatching {
            if (value.endsWith("dp")) {
                return value.replace("dp", "").toInt().dp(context)
            } else {
                return value.replace("px", "").toInt()
            }
        }
        return 0
    }

    fun isPixelStrValid(value: String): Boolean {
        var valid = false
        runCatching {
            valid = if (value.endsWith("dp")) {
                value.replace("dp", "").toInt().dp(MIUIActivity.context) in 0..500
            } else if (value.endsWith("px")) {
                value.replace("px", "").toInt() in 0..500
            } else {
                value.toInt() in 0..500
            }
        }.onFailure {
            valid = false
        }
        return valid
    }
}

