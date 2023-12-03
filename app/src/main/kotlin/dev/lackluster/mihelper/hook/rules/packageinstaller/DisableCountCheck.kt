package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DisableCountCheck : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_NO_COUNT_CHECK) {
            "com.miui.packageInstaller.model.RiskControlRules".toClassOrNull()
                ?.method {
                    name = "getCurrentLevel"
                }?.hook {
                    replaceTo(0)
                }
        }
    }
}