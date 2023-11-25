package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableUpdateCheck : YukiBaseHooker() {
    private val miMarketUpdateClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("MarketUpdateAgent", StringMatchType.Equals)
                addUsingString("packageName", StringMatchType.Equals)
            }
        }
    }
    private val miMarketDoInBackground by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                name = "doInBackground"
                returnType = "java.lang.Integer"
            }
            searchClasses = miMarketUpdateClass
        }.firstOrNull()
    }
    private val miMarketOnPostExecute by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                name = "onPostExecute"
            }
            searchClasses = miMarketUpdateClass
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.BROWSER_NO_UPDATE) {
            miMarketDoInBackground?.getMethodInstance(appClassLoader?:return@hasEnable)
                ?.hook {
                    replaceTo(1)
                }
                ?.result {
                    onHookingFailure {
                        YLog.warn("Failed to hook ${PrefKey.BROWSER_NO_UPDATE}\n${it}")
                    }
                }
            miMarketOnPostExecute?.getMethodInstance(appClassLoader?:return@hasEnable)
                ?.hook {
                    replaceTo(null)
                }
                ?.result {
                    onHookingFailure {
                        YLog.warn("Failed to hook ${PrefKey.BROWSER_NO_UPDATE}\n${it}")
                    }
                }
        }
    }
}