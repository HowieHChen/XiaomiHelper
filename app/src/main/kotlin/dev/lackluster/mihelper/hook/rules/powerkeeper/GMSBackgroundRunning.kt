package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

/**
 * @link https://github.com/kooritea/fcmfix/blob/master/app/src/main/java/com/kooritea/fcmfix/xposed/PowerkeeperFix.java
 */
object GMSBackgroundRunning : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.PowerKeeper.GMS_BG_RUNNING) {
            "com.miui.powerkeeper.utils.GmsObserver".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isGmsControlEnabled"
                }?.hook {
                    replaceToFalse()
                }
            }
            $$"com.miui.powerkeeper.provider.SimpleSettings$Misc".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "getBoolean"
                    parameterCount = 3
                }?.hook {
                    before {
                        if (this.args(1).string() == "gms_control") {
                            this.result = false
                        }
                    }
                }
            }
        }
    }
}