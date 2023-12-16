package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object BlurAdvancedEffect : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_BLUR_ADVANCE) {
            "com.miui.home.launcher.common.BlurUtilities".toClass().apply {
                method {
                    name = "isBackgroundBlurSupported"
                }.hook {
                    replaceToTrue()
                }
                method {
                    name = "isBlurSupported"
                }.hook {
                    replaceToTrue()
                }
                method {
                    name = "setBackgroundBlurEnabled"
                }.hook {
                    replaceUnit {
                        this.instance.current().field {
                            name = "IS_BACKGROUND_BLUR_ENABLED"
                        }.setTrue()
                    }
                }
            }
        }
    }
}