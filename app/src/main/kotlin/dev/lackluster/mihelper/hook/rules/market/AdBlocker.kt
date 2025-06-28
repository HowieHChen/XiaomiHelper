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
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    private val pagerTabsInfoClass by lazy {
        "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.Market.AD_BLOCKER) {
            "com.xiaomi.market.business_ui.main.MarketTabActivity".toClassOrNull()?.apply {
                method {
                    name = "tryShowRecommend"
                }.ignored().hook {
                    intercept()
                }
                method {
                    name = "tryShowRecallReCommend"
                }.ignored().hook {
                    intercept()
                }
                method {
                    name = "fetchSearchHotList"
                }.ignored().hook {
                    intercept()
                }
            }
            "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()?.apply {
                constructor().hookAll {
                    after {
                        filterTabs(this.instance)
                    }
                }
                method {
                    name = "fromJSON"
                }.ignored().hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                method {
                    name = "fromTabInfo"
                }.ignored().hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                method {
                    name = "fromNativeTabs"
                }.ignored().hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                method {
                    name = "setAbNormals"
                }.ignored().hook {
                    intercept()
                }
            }
            "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()?.apply {
                for (methodName in setOf(
                    "isBrowserMarketAdOff",
                    "isBrowserSourceFileAdOff",
                    "supportShowCompat64bitAlert",
                )) {
                    method {
                        name = methodName
                    }.ignored().hook {
                        replaceToTrue()
                    }
                }
                for (methodName in setOf(
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
                    method {
                        name = methodName
                    }.ignored().hook {
                        replaceToFalse()
                    }
                }
            }
        }
    }

    private fun filterTabs(pagerTabsInfo: Any) {
        pagerTabsInfoClass?.let {
            val urlsField = it.field { name = "urls" }.get(pagerTabsInfo)
            val titlesField = it.field { name = "titles" }.get(pagerTabsInfo)
            val tagsField = it.field { name = "tags" }.get(pagerTabsInfo)
            val abNormalsField = it.field { name = "abNormals" }.get(pagerTabsInfo)
            val urls = urlsField.list<String>().toMutableList()
            val titles = titlesField.list<Map<String, String>>().toMutableList()
            val tags = tagsField.list<String>().toMutableList()
            val abNormals = abNormalsField.list<Boolean>().toMutableList()
            val removeIndex = arrayListOf<Int>()
            if (urls.isNotEmpty() && urls.size == titles.size && urls.size == tags.size && urls.size == abNormals.size) {
                titles.forEachIndexed { index, map ->
                    if (map.getOrDefault("cn", "cn") == map.getOrDefault("en", "en")) {
                        removeIndex.add(index)
                    }
                }
                removeIndex.sortDescending()
                removeIndex.forEach {
                    urls.removeAt(it)
                    titles.removeAt(it)
                    tags.removeAt(it)
                    abNormals.removeAt(it)
                }
            }
            abNormals.replaceAll { false }
            urlsField.set(urls)
            titlesField.set(titles)
            tagsField.set(tags)
            abNormalsField.set(abNormals)
        }
    }
}