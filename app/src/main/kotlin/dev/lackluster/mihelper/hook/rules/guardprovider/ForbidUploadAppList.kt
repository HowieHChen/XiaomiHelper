package dev.lackluster.mihelper.hook.rules.guardprovider

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object ForbidUploadAppList : YukiBaseHooker() {
    private val region by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("ro.miui.customized.region", StringMatchType.Equals)
            }
        }.singleOrNull()
    }

    private val detect by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("https://flash.sec.miui.com/detect/app", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.GUARD_FORBID_UPLOAD_APP) {
            region?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    replaceToFalse()
                }
            detect?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    before {
                        this.result = null
                    }
                }
        }
    }
}