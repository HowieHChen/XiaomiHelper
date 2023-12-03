package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object PadAllFeature :YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_PAD_ALL_FEATURE, extraCondition = { Device.isPad }) {
            "com.miui.home.settings.MiuiHomeSettings".toClass().apply {
                method {
                    name = "needHideMinusScreen"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "shouldHidePersonalAssistantSettings"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "personalAssistantSettingsCanBeResolved"
                }.hook {
                    replaceToTrue()
                }
                method {
                    name = "isSupportPA"
                }.hook {
                    replaceToTrue()
                }
            }
            "com.miui.home.launcher.DeviceConfig".toClass().apply {
                method {
                    name = "needHideThemeManager"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isSystemSupportHotSeatsBlur"
                }.hook {
                    replaceToTrue()
                }
                method {
                    name = "checkSystemIsSupportHotSeatsBlur"
                }.hook {
                    replaceToTrue()
                }
            }
        }
    }
}