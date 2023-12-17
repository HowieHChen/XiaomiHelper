package dev.lackluster.mihelper.hook.rules.updater

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DisableValidation : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.UPDATER_DISABLE_VALIDATION) {
            "miui.util.FeatureParser".toClass()
                .method {
                    name = "hasFeature"
                    paramCount = 2
                }
                .hook {
                    before {
                        if (this.args(0).string() == "support_ota_validate") {
                            this.result = false
                        }
                    }
                }
        }
    }
}