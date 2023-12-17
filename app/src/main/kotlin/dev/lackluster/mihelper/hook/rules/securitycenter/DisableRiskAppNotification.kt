package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableRiskAppNotification : YukiBaseHooker() {
    private val pkg by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("riskPkgList", StringMatchType.Equals)
                addUsingString("key_virus_pkg_list", StringMatchType.Equals)
                addUsingString("show_virus_notification", StringMatchType.Equals)
            }
        }
    }
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_NO_RISK_APP_NOTIFICATION) {
            val pkgInstance = pkg.map { it.getMethodInstance(appClassLoader?:return@hasEnable) }.toList()
            pkgInstance.hookAll {
                intercept()
            }
        }
    }
}