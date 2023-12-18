package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideSafeModePopup : YukiBaseHooker() {
    private val showPopup by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_HIDE_SAFE_MODE_POPUP) {
            showPopup?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                intercept()
            }
        }
    }
}