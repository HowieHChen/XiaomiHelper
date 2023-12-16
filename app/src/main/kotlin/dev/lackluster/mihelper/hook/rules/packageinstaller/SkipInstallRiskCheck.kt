package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipInstallRiskCheck : YukiBaseHooker() {
    private val verifyEnable by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("secure_verify_enable", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    private val openSafeMode by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("installerOpenSafetyModel", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_SKIP_RISK_CHECK) {
            verifyEnable?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceToFalse()
            }
            openSafeMode?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceToFalse()
            }
        }
    }
}