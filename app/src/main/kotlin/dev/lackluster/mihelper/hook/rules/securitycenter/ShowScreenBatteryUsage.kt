package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object ShowScreenBatteryUsage : YukiBaseHooker() {
    private val powerRankClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("not support screenPowerSplit", StringMatchType.Equals)
                addUsingString("PowerRankHelperHolder", StringMatchType.Equals)
            }
        }.first()
    }
    private val powerRankMethod1 by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("ishtar", StringMatchType.Equals)
                addUsingString("nuwa", StringMatchType.Equals)
                addUsingString("fuxi", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    private val powerRankMethod2 by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass = powerRankClass.name
                returnType = "boolean"
                paramCount = 0
            }
        }
    }
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_SCREEN_BATTERY) {
            val powerRankMethod1Instance = powerRankMethod1?.getMethodInstance(appClassLoader ?: return@hasEnable) ?: return@hasEnable
            val powerRankMethod2Instance = powerRankMethod2.map { it.getMethodInstance(appClassLoader ?: return@hasEnable) }.toList()
            powerRankMethod2Instance.forEach {
                it.hook {
                    before {
                        when(this.method) {
                            powerRankMethod1Instance -> this.result = true
                            else -> this.result = false
                        }
                    }
                }
            }
        }
    }
}