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

import android.app.Activity
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    private val clzPagerTabsInfo by lazy {
        "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()
    }
    private val clzSearchHistoryComponent by lazy {
        "com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent".toClassOrNull()
    }
    private val clzListAppComponent by lazy {
        "com.xiaomi.market.common.component.componentbeans.ListAppComponent".toClassOrNull()
    }
    private val clzAladdinDownloadBottomLowComponent by lazy {
        "com.xiaomi.market.common.component.componentbeans.AladdinDownloadBottomLowComponent".toClassOrNull()
    }
    private val clzRecommendCollectionComponent by lazy {
        "com.xiaomi.market.common.component.componentbeans.RecommendCollectionComponent".toClassOrNull()
    }
    private val valueOfDetailType by lazy {
        "com.xiaomi.market.business_ui.detail.DetailType".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "valueOf"
            parameters(String::class)
            modifiers(Modifiers.STATIC)
        }?.self
    }
    private val enumDetailTypeV3 by lazy {
        valueOfDetailType?.invoke(null, "V3")
    }
    private val enumDetailTypeV4 by lazy {
        valueOfDetailType?.invoke(null, "V4")
    }
    private val enumPageCollapseStateExpand by lazy {
        $$"com.xiaomi.market.ui.UpdateListRvAdapter$PageCollapseState".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "valueOf"
            parameters(String::class)
            modifiers(Modifiers.STATIC)
        }?.invoke("Expand")
    }
    private val fldUrls by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "urls"
        }
    }
    private val fldTitles by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "titles"
        }
    }
    private val fldTags by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "tags"
        }
    }
    private val fldAbNormals by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "abNormals"
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Market.AD_BLOCKER) {
            // 搜索建议页
            "com.xiaomi.market.business_ui.search.NativeSearchSugFragment".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "getRequestParams"
                }?.hook {
                    after {
                        val baseParametersForH5ToNative = this.result<Map<String, Any?>>()?.toMutableMap() ?: return@after
                        baseParametersForH5ToNative["adFlag"] = 0
                        this.result = baseParametersForH5ToNative
                    }
                }
            }
            // 搜索结果页
            "com.xiaomi.market.business_ui.search.NativeSearchResultFragment".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "parseResponseData"
                }?.hook {
                    after {
                        val parsedComponents = this.result<List<Any>>()?.toMutableList() ?: return@after
                        parsedComponents.retainAll {
                            clzListAppComponent?.isInstance(it) == true ||
                                    clzAladdinDownloadBottomLowComponent?.isInstance(it) == true ||
                                    clzRecommendCollectionComponent?.isInstance(it) == true
                        }
                        this.result = parsedComponents
                    }
                }
            }
            // 搜索页
            "com.xiaomi.market.business_ui.search.NativeSearchGuideFragment".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "parseResponseData"
                }?.hook {
                    after {
                        val parsedComponents = this.result<List<Any>>()?.toMutableList() ?: return@after
                        parsedComponents.retainAll {
                            clzSearchHistoryComponent?.isInstance(it) == true
                        }
                        this.result = parsedComponents
                    }
                }
                resolve().firstMethodOrNull {
                    name = "isLoadMoreEndGone"
                }?.hook {
                    replaceToTrue()
                }
            }
            // 更新页
            "com.xiaomi.market.ui.UpdateListRvAdapter".toClassOrNull()?.apply {
                val forceExpanded = resolve().firstFieldOrNull {
                    name = "forceExpanded"
                }
                val foldButtonVisible = resolve().firstFieldOrNull {
                    name = "foldButtonVisible"
                }
                val pageCollapseState = resolve().firstFieldOrNull {
                    name = "pageCollapseState"
                }
                resolve().firstConstructor().hook {
                    after {
                        forceExpanded?.copy()?.of(this.instance)?.set(true)
                        foldButtonVisible?.copy()?.of(this.instance)?.set(false)
                        pageCollapseState?.copy()?.of(this.instance)?.set(enumPageCollapseStateExpand)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "generateRecommendGroupItems"
                }?.hook {
                    intercept()
                }
            }
            // 下载页
            "com.xiaomi.market.ui.DownloadListFragment".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "parseRecommendGroupResult"
                }?.hook {
                    replaceTo(null)
                }
            }
            // 底部 Tab
            "com.xiaomi.market.widget.TabView".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "setNumber"
                }?.hook {
                    intercept()
                }
                resolve().firstMethodOrNull {
                    name = "showNewMessageTag"
                }?.hook {
                    intercept()
                }
            }
            // 首页启动弹窗
            "com.xiaomi.market.business_ui.main.MarketTabActivity".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "tryShowRecommend"
                }?.hook {
                    intercept()
                }
                resolve().firstMethodOrNull {
                    name = "tryShowRecallReCommend"
                }?.hook {
                    intercept()
                }
                resolve().firstMethodOrNull {
                    name = "fetchSearchHotList"
                }?.hook {
                    intercept()
                }
            }
            "com.xiaomi.market.ui.FloatWebActivity".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "onCreate"
                    superclass()
                }?.hook {
                    after {
                        this.instance<Activity>().finish()
                    }
                }
            }
            // 首页二楼
            "com.xiaomi.market.common.component.quick_item.QuickSecondHelper".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "shouldHideSecond"
                }?.hook {
                    replaceToTrue()
                }
            }
            $$"com.xiaomi.market.common.analytics.onetrack.ExperimentManager$Companion".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isEnableQuickSecond"
                }?.hook {
                    replaceToFalse()
                }
            }
            // 过滤子标签页
            "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()?.apply {
                resolve().constructor {
                    parameterCount {
                        it == 0 || it == 1
                    }
                }.hookAll {
                    after {
                        filterTabs(this.instance)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "fromJSON"
                }?.hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "fromTabInfo"
                }?.hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "fromNativeTabs"
                }?.hook {
                    after {
                        this.result?.let { filterTabs(it) }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setAbNormals"
                }?.hook {
                    intercept()
                }
            }
            // 应用详情页
            "com.xiaomi.market.ui.detail.BaseDetailActivity".toClassOrNull()?.apply {
                val detailType = resolve().firstFieldOrNull {
                    name = "detailType"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "initParams"
                }?.hook {
                    after {
                        val fldDetailType = detailType?.copy()?.of(this.instance)
                        if (fldDetailType?.get() == enumDetailTypeV3) {
                            fldDetailType?.set(enumDetailTypeV4)
                        }
                    }
                }
            }
            "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()?.apply {
                val showOpenScreenAd = resolve().firstFieldOrNull {
                    name = "showOpenScreenAd"
                }
                val showAssemble = resolve().firstFieldOrNull {
                    name = "showAssemble"
                }
                constructors.filter {
                    it.parameterCount > 0
                }.minByOrNull {
                    it.parameterCount
                }?.hook {
                    after {
                        showOpenScreenAd?.copy()?.of(this.instance)?.set(false)
                        showAssemble?.copy()?.of(this.instance)?.set(false)
                    }
                }
                setOf(
                    "showRecommend",
                    "showTopBanner",
                    "showTopVideo",
                ).forEach { metName ->
                    resolve().firstMethodOrNull {
                        name = metName
                    }?.hook {
                        replaceToFalse()
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getLayoutType"
                }?.hook {
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
        clzPagerTabsInfo?.let {
            val fieldUrls = fldUrls?.copy()?.of(pagerTabsInfo)
            val fieldTitles = fldTitles?.copy()?.of(pagerTabsInfo)
            val fieldTags = fldTags?.copy()?.of(pagerTabsInfo)
            val fieldAbNormals = fldAbNormals?.copy()?.of(pagerTabsInfo)
            val urls = fieldUrls?.get<List<String>>()?.toMutableList() ?: return@let
            val titles = fieldTitles?.get<List<Map<String, String>>>()?.toMutableList() ?: return@let
            val tags = fieldTags?.get<List<String>>()?.toMutableList() ?: return@let
            val abNormals = fieldAbNormals?.get<List<Boolean>>()?.toMutableList() ?: return@let
            val removeIndex = mutableSetOf<Int>()
            if (urls.isNotEmpty() && urls.size == titles.size && urls.size == tags.size && urls.size == abNormals.size) {
                urls.forEachIndexed { index, url ->
                    if (url.startsWith("http")) {
                        removeIndex.add(index)
                    }
                }
                tags.forEachIndexed { index, tag ->
                    if (tag.startsWith("native_app_assemble_home")) {
                        removeIndex.add(index)
                    }
                }
                removeIndex.sortedDescending().forEach { index ->
                    urls.removeAt(index)
                    titles.removeAt(index)
                    tags.removeAt(index)
                    abNormals.removeAt(index)
                }
            }
            abNormals.replaceAll { false }
            fieldUrls.set(urls)
            fieldTitles.set(titles)
            fieldTags.set(tags)
            fieldAbNormals.set(abNormals)
        }
    }
}