package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AdBlock : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.MARKET_AD_BLOCK) {
            val appDetailV3Cls =
                "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()
            val detailSplashAdManagerCls =
                "com.xiaomi.market.ui.splash.DetailSplashAdManager".toClassOrNull()
            val splashManagerCls =
                "com.xiaomi.market.ui.splash.SplashManager".toClassOrNull()

            for (method in setOf(
                "isBrowserMarketAdOff",
                "isBrowserSourceFileAdOff",
                "supportShowCompat64bitAlert",
            )) {
                appDetailV3Cls?.method {
                    name = method
                }?.hook {
                    replaceToTrue()
                }
            }

            for (method in setOf(
                "isInternalAd",
                "needShowAds",
                "needShowAdsWithSourceFile",
                "showComment",
                "showRecommend",
                "showTopBanner",
                "showTopVideo",
                "equals",
                "getShowOpenScreenAd",
                "hasGoldLabel",
                "isBottomButtonLayoutType",
                "isPersonalization",
                "isTopButtonLayoutType",
                "isTopSingleTabMultiButtonType",
                "needShowGrayBtn",
                "needShowPISafeModeStyle",
                "supportAutoLoadDeepLink",
                "supportShowCompatAlert",
                "supportShowCompatChildForbidDownloadAlert",
            )) {
                appDetailV3Cls?.method {
                    name = method
                }?.hook {
                    replaceToFalse()
                }
            }

            for (method in setOf(
                "canRequestSplashAd",
                "isRequesting",
                "isOpenFromMsa",
            )) {
                detailSplashAdManagerCls?.method {
                    name = method
                }?.hook {
                    replaceToFalse()
                }
            }
            detailSplashAdManagerCls?.method {
                name = "tryToRequestSplashAd"
            }?.hook {
                before {
                    this.result = null
                }
            }

            for (method in setOf(
                "canShowSplash",
                "needShowSplash",
                "needRequestFocusVideo",
                "isPassiveSplashAd",
            )) {
                splashManagerCls?.method {
                    name = method
                }?.ignored()?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}