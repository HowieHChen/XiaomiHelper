package dev.lackluster.mihelper.hook.rules.joyose

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableCloudControl : YukiBaseHooker() {
    private val cloudControlMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("job exist, sync local...", StringMatchType.Equals)
                returnType = "void"
            }
        }.singleOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.JOYOSE_NO_CLOUD_CONTROL) {
            cloudControlMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    before {
                        this.result = null
                    }
                }
        }
    }
}