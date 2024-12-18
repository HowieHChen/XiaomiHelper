package dev.lackluster.mihelper.hook.rules.misettings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    private val initPreferenceMethod by lazy {
        DexKit.findMethodWithCache("custom_high_refresh_rate") {
            matcher {
                addUsingString("btn_preferce_category", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.PowerKeeper.UNLOCK_CUSTOM_REFRESH) {
            if (appClassLoader == null) return@hasEnable
            initPreferenceMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    this.args(0).setTrue()
                }
            }
            "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity".toClassOrNull()?.apply {
                field {
                    type = BooleanClass
                    modifiers {
                        isStatic && isFinal
                    }
                }.get().setTrue()
            }
        }
    }
}