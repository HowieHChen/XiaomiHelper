package dev.lackluster.mihelper.utils

import android.os.Build

object Device {
    val isPad by lazy {
        try {
            Class.forName("miui.os.Build").getDeclaredField("IS_TABLET").get(null) as Boolean
        }
        catch (e: Exception) {
            false
        }
    }
    val isInternationalBuild by lazy {
        try {
            Class.forName("miui.os.Build").getDeclaredField("IS_INTERNATIONAL_BUILD").get(null) as Boolean
        }
        catch (e: Exception) {
            false
        }
    }
    val androidVersion by lazy {
        Build.VERSION.SDK_INT
    }
}