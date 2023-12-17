package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SystemAppWifiSettings : YukiBaseHooker() {
    private val method by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("com.qti.qcc", StringMatchType.Equals)
                returnType = "boolean"
                paramCount = 0
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_SYSTEM_APP_WIFI) {
            method?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                replaceToTrue()
            }
        }
    }
}