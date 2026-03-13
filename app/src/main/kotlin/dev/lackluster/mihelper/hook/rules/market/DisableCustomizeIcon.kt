package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisableCustomizeIcon : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.DISABLE_CUSTOMIZE_ICON) {
            "com.xiaomi.market.customize_icon.CustomizeIconDataEditor".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSystemSupportCustomizeIcon"
                }?.hook {
                    replaceToFalse()
                }
            }
            $$"com.xiaomi.market.customize_icon.CustomizeIconDataEditor$Companion".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSystemSupportCustomizeIcon"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}