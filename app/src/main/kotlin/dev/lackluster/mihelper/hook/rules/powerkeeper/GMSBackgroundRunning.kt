package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

/**
 * @link https://github.com/kooritea/fcmfix/blob/master/app/src/main/java/com/kooritea/fcmfix/xposed/PowerkeeperFix.java
 */
object GMSBackgroundRunning : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.PowerKeeper.GMS_BG_RUNNING.get())
    }

    override fun onHook() {
        "com.miui.powerkeeper.utils.GmsObserver".toClassOrNull()?.apply {
            resolve().optional(true).firstMethodOrNull {
                name = "updateGoogleReletivesWakelock"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = false
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "isGmsControlEnabled"
            }?.hook {
                result(false)
            }
        }
        $$"com.miui.powerkeeper.provider.SimpleSettings$Misc".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getBoolean"
                parameterCount = 3
            }?.hook {
                val key = getArg(1) as? String
                if (key == "gms_control") {
                    result(false)
                } else {
                    result(proceed())
                }
            }
        }
    }
}