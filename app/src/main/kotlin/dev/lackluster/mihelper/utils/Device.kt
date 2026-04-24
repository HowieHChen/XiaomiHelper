package dev.lackluster.mihelper.utils

import android.os.Build
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers

object Device {
    private const val TAG = "Device"

    private val clzBuild by lazy {
        try {
            Class.forName("miui.os.Build")
        } catch (e: Exception) {
            MLog.e(TAG, e) { "miui.os.Build class not found" }
            null
        }
    }

    val isPad by lazy {
        clzBuild?.resolve()?.firstFieldOrNull {
            name = "IS_TABLET"
            modifiers(Modifiers.STATIC)
        }?.get<Boolean>() ?: false
    }
    val isInternationalBuild by lazy {
        clzBuild?.resolve()?.firstFieldOrNull {
            name = "IS_INTERNATIONAL_BUILD"
            modifiers(Modifiers.STATIC)
        }?.get<Boolean>() ?: false
    }
    val isGlobal by lazy {
        clzBuild?.resolve()?.firstFieldOrNull {
            name = "IS_GLOBAL_BUILD"
            modifiers(Modifiers.STATIC)
        }?.get<Boolean>() ?: false
    }

    val androidVersion by lazy {
        Build.VERSION.SDK_INT
    }
}