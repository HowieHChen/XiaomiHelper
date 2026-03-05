package dev.lackluster.mihelper.hook.rules.systemui.plugin

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object IslandWhitelist : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.ISLAND_WHITELIST) {
            "miui.systemui.notification.focus.SignatureChecker".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "checkSignatures"
                    parameters(String::class)
                }?.hook {
                    replaceToTrue()
                }
            }
            "miui.systemui.notification.NotificationSettingsManager".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "canShowFocus"
                }?.hook {
                    replaceToTrue()
                }
                resolve().firstMethodOrNull {
                    name = "canCustomFocus"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
        hasEnable(Pref.Key.SystemUI.Plugin.ISLAND_MEDIA_WHITELIST) {
            "miui.systemui.notification.NotificationSettingsManager".toClassOrNull()?.apply {
                resolve().optional(true).firstMethodOrNull {
                    name = "mediaIslandSupportMiniWindow"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
    }
}