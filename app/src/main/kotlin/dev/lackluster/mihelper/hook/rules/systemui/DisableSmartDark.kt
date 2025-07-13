package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisableSmartDark : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.StatusBar.DISABLE_SMART_DARK) {
            "com.android.systemui.statusbar.policy.SmartDarkObserver".toClassOrNull()?.apply {
                constructor().hook {
                    after {
                        this.instance.current().field {
                            name = "mSmartDark"
                        }.setFalse()
                    }
                }
            }
            "com.android.systemui.statusbar.policy.SmartDarkObserver$1".toClassOrNull()?.apply {
                method {
                    name = "onConfigChanged"
                }.remedys {
                    method {
                        name = "onChange"
                    }
                }.hook {
                    intercept()
                }
            }
        }
    }
}