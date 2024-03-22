/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references StarVoyager <https://github.com/hosizoraru/StarVoyager/blob/star/app/src/main/kotlin/star/sky/voyager/hook/hooks/market/RemoveAd.kt>
 * Copyright (C) 2023 hosizoraru

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.AD_BLOCKER) {
            val appDetailV3Cls = "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()
            val detailSplashAdManagerCls = "com.xiaomi.market.ui.splash.DetailSplashAdManager".toClassOrNull()
            val splashManagerCls = "com.xiaomi.market.ui.splash.SplashManager".toClassOrNull()
            runCatching {
                for (method in setOf(
                    "isBrowserMarketAdOff",
                    "isBrowserSourceFileAdOff",
                    "supportShowCompat64bitAlert",
                )) {
                    appDetailV3Cls?.method {
                        name = method
                    }?.ignored()?.hook {
                        replaceToTrue()
                    }
                }
            }
            runCatching {
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
                    }?.ignored()?.hook {
                        replaceToFalse()
                    }
                }
            }
            runCatching {
                for (method in setOf(
                    "canRequestSplashAd",
                    "isRequesting",
                    "isOpenFromMsa",
                )) {
                    detailSplashAdManagerCls?.method {
                        name = method
                    }?.ignored()?.hook {
                        replaceToFalse()
                    }
                }
            }
            runCatching {
                detailSplashAdManagerCls?.method {
                    name = "tryToRequestSplashAd"
                }?.ignored()?.hook {
                    before {
                        this.result = null
                    }
                }
            }
            runCatching {
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
}