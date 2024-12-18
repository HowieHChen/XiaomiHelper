package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

/**
 * @link https://github.com/kooritea/fcmfix/blob/master/app/src/main/java/com/kooritea/fcmfix/xposed/PowerkeeperFix.java
 */
object GMSBackgroundRunning : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.PowerKeeper.GMS_BG_RUNNING) {
            "com.miui.powerkeeper.utils.GmsObserver".toClassOrNull()?.apply {
                method {
                    name = "isGmsControlEnabled"
                }.hook {
                    replaceToFalse()
                }
            }
            "com.miui.powerkeeper.millet.MilletConfig".toClassOrNull()?.apply {
                field {
                    name = "isGlobal"
                }.get().setTrue()
            }
            "com.miui.powerkeeper.provider.SimpleSettings\$Misc".toClassOrNull()?.apply {
                method {
                    name = "getBoolean"
                    paramCount = 3
                }.hook {
                    before {
                        if (this.args(1).string() == "gms_control") {
                            this.result = false
                        }
                    }
                }
            }
            "com.miui.powerkeeper.millet.MilletPolicy".toClassOrNull()?.apply {
                constructor {
                    param(ContextClass)
                }.hook {
                    before {
                        val mSystemBlackListFiled = this.instance.current().field {
                            name = "mSystemBlackList"
                        }
                        val mSystemBlackList = mSystemBlackListFiled.list<String>().toMutableList()
                        if (mSystemBlackList.isNotEmpty()) {
                            mSystemBlackList.remove("com.google.android.gms")
                            mSystemBlackListFiled.set(mSystemBlackList)
                        }
                        val whiteAppsField = this.instance.current().field {
                            name = "whiteApps"
                        }
                        val whiteApps = whiteAppsField.list<String>().toMutableList()
                        if (whiteApps.isNotEmpty()) {
                            whiteApps.remove("com.google.android.gms")
                            whiteApps.remove("com.google.android.ext.services")
                            whiteAppsField.set(whiteApps)
                        }
                        val mDataWhiteListField = this.instance.current().field {
                            name = "mDataWhiteList"
                        }
                        val mDataWhiteList = mDataWhiteListField.list<String>().toMutableList()
                        if (mDataWhiteList.isNotEmpty()) {
                            mDataWhiteList.add("com.google.android.gms")
                            mDataWhiteListField.set(mDataWhiteList)
                        }
                    }
                }
            }
        }
    }
}