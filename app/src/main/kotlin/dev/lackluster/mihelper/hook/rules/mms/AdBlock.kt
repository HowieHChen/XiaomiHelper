package dev.lackluster.mihelper.hook.rules.mms

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlock : YukiBaseHooker() {
    private val cloudControlMethod by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("Unknown type of the message: ", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.MMS_AD_BLOCK) {
            runCatching {
                cloudControlMethod?.getInstance(appClassLoader ?: return@runCatching )
                    ?.method {
                        name = "j"
                    }?.ignored()
                    ?.hook {
                        replaceToFalse()
                    }
            }
            "com.miui.smsextra.ui.BottomMenu".toClass()
                .method {
                    name = "allowMenuMode"
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}