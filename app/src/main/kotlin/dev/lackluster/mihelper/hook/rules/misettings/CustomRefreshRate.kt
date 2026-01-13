package dev.lackluster.mihelper.hook.rules.misettings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    private val initPreferenceMethod by lazy {
        DexKit.findMethodWithCache("custom_high_refresh_rate") {
            matcher {
                addUsingString("btn_preferce_category", StringMatchType.Equals)
                paramCount = 1
                paramTypes("boolean")
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
                resolve().firstFieldOrNull {
                    type("java.lang.Boolean")
                    modifiers {
                        it.contains(Modifiers.STATIC) && it.contains(Modifiers.FINAL)
                    }
                }?.set(true)
            }
        }
    }
}