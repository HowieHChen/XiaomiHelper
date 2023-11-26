package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object LockScore : YukiBaseHooker() {
    private val scoreMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("getMinusPredictScore", StringMatchType.Contains)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_LOCK_SCORE) {
            "com.miui.securityscan.ui.main.MainContentFrame".toClass()
                .method {
                    name = "onClick"
                    param(ViewClass)
                }
                .hook {
                    replaceTo(null)
                }
            scoreMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    replaceTo(0)
                }
        }
    }
}