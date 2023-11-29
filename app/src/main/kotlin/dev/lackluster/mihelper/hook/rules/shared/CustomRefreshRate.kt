package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.POWER_CUSTOM_REFRESH) {
            when(packageName) {
                Scope.POWER_KEEPER -> {
                    DexKit.dexKitBridge.findMethod {
                        matcher {
                            addUsingString("custom_mode_switch", StringMatchType.Equals)
                            addUsingString("fucSwitch", StringMatchType.Equals)
                        }
                    }.filter { it.isMethod }.map { it.getMethodInstance(appClassLoader ?: return@hasEnable) }.hookAll {
                        before {
                            this.instance.current().field {
                                name = "mIsCustomFpsSwitch"
                            }.set(true.toString())
                        }
                    }
                }
                Scope.MI_SETTINGS -> {
                    DexKit.dexKitBridge.findMethod {
                        matcher {
                            addUsingString("btn_preferce_category", StringMatchType.Equals)
                        }
                    }.firstOrNull()?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                        before {
                            this.args(0).setTrue()
                        }
                    }
                    "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity".toClass()
                        .field {
                            modifiers {
                                isStatic && isFinal
                            }
                        }.get().setTrue()
                }
            }
        }
    }
}