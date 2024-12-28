package dev.lackluster.mihelper.hook.rules.market

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideAppSecurity : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.HIDE_APP_SECURITY) {
            "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView".toClassOrNull()?.apply {
                constructor().hookAll {
                    after {
                        (this.instance as? View)?.visibility = View.GONE
                    }
                }
                method {
                    name = "checkShown"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "checkSettingSwitch"
                }.hook {
                    replaceToFalse()
                }
            }
            "com.xiaomi.market.util.SettingsUtils".toClassOrNull()?.apply {
                method {
                    name = "isSupportAppSecurityCheck"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            "com.xiaomi.market.common.analytics.onetrack.ExperimentManager\$Companion".toClassOrNull()?.apply {
                method {
                    name = "isMineAppSecurityCheckOpen"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
        }
    }
}