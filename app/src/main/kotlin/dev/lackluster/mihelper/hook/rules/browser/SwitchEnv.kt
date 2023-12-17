package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SwitchEnv : YukiBaseHooker() {
    private val envGetMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                returnType = "java.lang.String"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("0", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    private val envSetMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                returnType = "void"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("3", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.BROWSER_SWITCH_ENV) {
            envGetMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceTo("1")
            }
            envSetMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                before {
                    this.args(0).set("1")
                }
            }
        }
    }
}