package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DevMode : YukiBaseHooker() {
    private val debugMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                name = "getDebugMode"
                returnType = "boolean"
                addUsingString("pref_key_debug_mode", StringMatchType.StartsWith)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.BROWSER_DEBUG) {
            debugMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    replaceToTrue()
                }
                ?.result {
                    onHookingFailure {
                        YLog.warn("Failed to hook ${PrefKey.BROWSER_DEBUG}\n${it}")
                    }
                }
        }
    }
}