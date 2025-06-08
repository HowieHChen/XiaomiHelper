package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner
import dev.lackluster.mihelper.utils.Prefs

object IgnoreSysHideIcon : YukiBaseHooker() {
    private val ignoreSystem = Prefs.getBoolean(IconTurner.IGNORE_SYS_HIDE, false)
    private val hidePrivacy = Prefs.getBoolean(IconTurner.HIDE_PRIVACY, false)

    override fun onHook() {
        if (ignoreSystem || hidePrivacy) {
            "com.android.systemui.statusbar.policy.StatusBarIconObserver".toClassOrNull()?.apply {
                method {
                    name = "isIconBlocked"
                }.hook {
                    before {
                        val slot = this.args(0).string()
                        if (slot == "privacy") {
                            this.result = hidePrivacy
                        } else if (ignoreSystem) {
                            this.result = false
                        }
                    }
                }
                if (ignoreSystem) {
                    method {
                        name = "loadStatusBarIcon"
                    }.hook {
                        replaceTo("")
                    }
                }
            }
        }
    }
}