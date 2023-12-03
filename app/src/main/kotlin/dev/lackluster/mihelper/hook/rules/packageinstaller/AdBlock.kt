package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlock : YukiBaseHooker() {
    private val ads by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("ads_enable", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    private val recommend by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("app_store_recommend", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    private val scan by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("virus_scan_install", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_AD_BLOCK) {
            ads?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceToFalse()
            }
            recommend?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceToFalse()
            }
            scan?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceToFalse()
            }
        }
    }
}