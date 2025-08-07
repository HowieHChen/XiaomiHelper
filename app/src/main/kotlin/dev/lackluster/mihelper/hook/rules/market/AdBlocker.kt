/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    private val pagerTabsInfoClass by lazy {
        "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()
    }
    private val searchHistoryComponentClass by lazy {
        "com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent".toClassOrNull()
    }
    private val listAppComponentClass by lazy {
        "com.xiaomi.market.common.component.componentbeans.ListAppComponent".toClassOrNull()
    }
    private val aladdinDownloadBottomLowComponentClass by lazy {
        "com.xiaomi.market.common.component.componentbeans.AladdinDownloadBottomLowComponent".toClassOrNull()
    }
    private val valueOfDetailType by lazy {
        "com.xiaomi.market.business_ui.detail.DetailType".toClassOrNull()?.method {
            name = "valueOf"
            param(StringClass)
            modifiers { isStatic }
        }?.get()
    }
    private val detailTypeV3 by lazy {
        valueOfDetailType?.call("V3")
    }
    private val detailTypeV4 by lazy {
        valueOfDetailType?.call("V4")
    }
    private val pageCollapseStateExpand by lazy {
        "com.xiaomi.market.ui.UpdateListRvAdapter\$PageCollapseState".toClassOrNull()?.method {
            name = "valueOf"
            param(StringClass)
            modifiers { isStatic }
        }?.get()?.call("Expand")
    }

    override fun onHook() {
        hasEnable(Pref.Key.Market.AD_BLOCKER) {
            // 搜索建议页
            "com.xiaomi.market.business_ui.search.NativeSearchSugFragment".toClassOrNull()?.apply {
                method {
                    name = "getRequestParams"
                }.hook {
                    after {
                        val baseParametersForH5ToNative = this.result<Map<String, Any?>>()?.toMutableMap() ?: return@after
                        baseParametersForH5ToNative.put("adFlag", 0)
                        this.result = baseParametersForH5ToNative
                    }
                }
            }
            // 搜索结果页
            "com.xiaomi.market.business_ui.search.NativeSearchResultFragment".toClassOrNull()?.apply {
                method {
                    name = "parseResponseData"
                }.hook {
                    after {
                        val parsedComponents = this.result<List<Any>>()?.toMutableList() ?: return@after
                        parsedComponents.retainAll {
                            listAppComponentClass?.isInstance(it) == true ||
                                    aladdinDownloadBottomLowComponentClass?.isInstance(it) == true
                        }
                        this.result = parsedComponents
                    }
                }
            }
            // 搜索页
            "com.xiaomi.market.business_ui.search.NativeSearchGuideFragment".toClassOrNull()?.apply {
                method {
                    name = "parseResponseData"
                }.hook {
                    after {
                        val parsedComponents = this.result<List<Any>>()?.toMutableList() ?: return@after
                        parsedComponents.retainAll {
                            searchHistoryComponentClass?.isInstance(it) == true
                        }
                        this.result = parsedComponents
                    }
                }
                method {
                    name = "isLoadMoreEndGone"
                }.hook {
                    replaceToTrue()
                }
            }
            // 更新页
            "com.xiaomi.market.ui.UpdateListRvAdapter".toClassOrNull()?.apply {
                constructor().hook {
                    after {
                        this.instance.current(true).field {
                            name = "forceExpanded"
                        }.setTrue()
                        this.instance.current(true).field {
                            name = "foldButtonVisible"
                        }.setFalse()
                        this.instance.current(true).field {
                            name = "pageCollapseState"
                        }.set(pageCollapseStateExpand)
                    }
                }
                method {
                    name = "generateRecommendGroupItems"
                }.ignored().hook {
                    intercept()
                }
            }
            // 下载页
            "com.xiaomi.market.ui.DownloadListFragment".toClassOrNull()?.apply {
                method {
                    name = "parseRecommendGroupResult"
                }.ignored().hook {
                    replaceTo(null)
                }
            }
            // 首页启动弹窗
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
            // 首页二楼
            "com.xiaomi.market.common.component.quick_item.QuickSecondHelper".toClassOrNull()?.apply {
                method {
                    name = "isEnableQuickSecond"
                }.hook {
                    replaceToFalse()
                }
            }
            // 过滤子标签页
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
            // 应用详情页
            "com.xiaomi.market.ui.detail.BaseDetailActivity".toClassOrNull()?.apply {
                method {
                    name = "initParams"
                }.hook {
                    after {
                        val detailType = this.instance.current().field {
                            name = "detailType"
                            superClass()
                        }.any()
                        if (detailType == detailTypeV3) {
                            this.instance.current().field {
                                name = "detailType"
                                superClass()
                            }.set(detailTypeV4)
                        }
                    }
                }
            }
            "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()?.apply {
                constructors.filter {
                    it.parameterCount > 0
                }.minByOrNull {
                    it.parameterCount
                }?.hook {
                    after {
                        this.instance.current().field { name = "showOpenScreenAd" }.setFalse()
                        this.instance.current().field { name = "showAssemble" }.setFalse()
                    }
                }
                for (methodName in setOf(
                    "showRecommend",
                    "showTopBanner",
                    "showTopVideo",
                )) {
                    method {
                        name = methodName
                    }.ignored().hook {
                        replaceToFalse()
                    }
                }
                method {
                    name = "getLayoutType"
                }.hook {
                    after {
                        if (this.result == "bottomMultiButton") {
                            this.result = "bottomSingleButton"
                        }
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
                urls.forEachIndexed { index, url ->
                    if (url.startsWith("http")) {
                        removeIndex.add(index)
                    }
                }
                removeIndex.sortDescending()
                removeIndex.forEach { index ->
                    urls.removeAt(index)
                    titles.removeAt(index)
                    tags.removeAt(index)
                    abNormals.removeAt(index)
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