package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object RemoveReport : YukiBaseHooker() {
    private val reportMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("android.intent.action.VIEW", StringMatchType.Equals)
                addUsingString("com.xiaomi.market", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_REMOVE_REPORT) {
            reportMethod?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                replaceToFalse()
            }
        }
    }
}