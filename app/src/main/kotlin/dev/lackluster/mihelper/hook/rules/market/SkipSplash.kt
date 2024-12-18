package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipSplash : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.SKIP_SPLASH) {
            "com.xiaomi.market.ui.splash.SplashManager".toClassOrNull()?.apply {
            method {
                    name = "tryAdSplash"
                }.hook {
                    intercept()
                }
                method {
                    name = "trySplashWhenApplicationForeground"
                }.hook {
                    intercept()
                }
            }
            "com.xiaomi.market.business_ui.main.MarketTabActivity".toClassOrNull()?.apply {
                method {
                    name = "trySplash"
                }.hook {
                    intercept()
                }
            }
        }
    }
}