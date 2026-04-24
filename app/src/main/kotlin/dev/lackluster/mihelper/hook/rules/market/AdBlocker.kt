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

@file:Suppress("UNCHECKED_CAST")

package dev.lackluster.mihelper.hook.rules.market

import android.app.Activity
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.toTyped

object AdBlocker : StaticHooker() {
    private val clzPagerTabsInfo by "com.xiaomi.market.ui.PagerTabsInfo".lazyClassOrNull()
    private val clzSearchHistoryComponent by "com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent".lazyClassOrNull()
    private val clzListAppComponent by "com.xiaomi.market.common.component.componentbeans.ListAppComponent".lazyClassOrNull()
    private val clzAladdinDownloadBottomLowComponent by "com.xiaomi.market.common.component.componentbeans.AladdinDownloadBottomLowComponent".lazyClassOrNull()
    private val clzRecommendCollectionComponent by "com.xiaomi.market.common.component.componentbeans.RecommendCollectionComponent".lazyClassOrNull()

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
        }?.toTyped<List<String>>()
    }
    private val fldTitles by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "titles"
        }?.toTyped<List<Map<String, String>>>()
    }
    private val fldTags by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "tags"
        }?.toTyped<List<String>>()
    }
    private val fldAbNormals by lazy {
        clzPagerTabsInfo?.resolve()?.firstFieldOrNull {
            name = "abNormals"
        }?.toTyped<List<Boolean>>()
    }

    override fun onInit() {
        updateSelfState(Preferences.Market.AD_BLOCKER.get())
    }

    override fun onHook() {
        // 搜索建议页
        "com.xiaomi.market.business_ui.search.NativeSearchSugFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getRequestParams"
            }?.hook {
                val ori = proceed()
                val baseParametersForH5ToNative = (ori as? Map<String, Any?>)?.toMutableMap()
                if (baseParametersForH5ToNative != null) {
                    baseParametersForH5ToNative["adFlag"] = 0
                    result(baseParametersForH5ToNative)
                } else {
                    result(ori)
                }
            }
        }
        // 搜索结果页
        "com.xiaomi.market.business_ui.search.NativeSearchResultFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "parseResponseData"
            }?.hook {
                val ori = proceed()
                val parsedComponents = (ori as? List<Any?>)?.toMutableList()
                if (parsedComponents != null) {
                    parsedComponents.retainAll {
                        clzListAppComponent?.isInstance(it) == true ||
                                clzAladdinDownloadBottomLowComponent?.isInstance(it) == true ||
                                clzRecommendCollectionComponent?.isInstance(it) == true
                    }
                    result(parsedComponents)
                } else {
                    result(ori)
                }
            }
        }
        // 搜索页
        "com.xiaomi.market.business_ui.search.NativeSearchGuideFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "parseResponseData"
            }?.hook {
                val ori = proceed()
                val parsedComponents = (ori as? List<Any?>)?.toMutableList()
                if (parsedComponents != null) {
                    parsedComponents.retainAll {
                        clzSearchHistoryComponent?.isInstance(it) == true
                    }
                    result(parsedComponents)
                } else {
                    result(ori)
                }
            }
            resolve().firstMethodOrNull {
                name = "isLoadMoreEndGone"
            }?.hook {
                result(true)
            }
        }
        // 更新页
        "com.xiaomi.market.ui.UpdateListRvAdapter".toClassOrNull()?.apply {
            val forceExpanded = resolve().firstFieldOrNull {
                name = "forceExpanded"
            }?.toTyped<Boolean>()
            val foldButtonVisible = resolve().firstFieldOrNull {
                name = "foldButtonVisible"
            }?.toTyped<Boolean>()
            val pageCollapseState = resolve().firstFieldOrNull {
                name = "pageCollapseState"
            }?.toTyped<Any>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                forceExpanded?.set(thisObject, true)
                foldButtonVisible?.set(thisObject, false)
                pageCollapseState?.set(thisObject, enumPageCollapseStateExpand)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "generateRecommendGroupItems"
            }?.hook {
                result(null)
            }
        }
        // 下载页
        "com.xiaomi.market.ui.DownloadListFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "parseRecommendGroupResult"
            }?.hook {
                result(null)
            }
        }
        // 底部 Tab
        "com.xiaomi.market.widget.TabView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setNumber"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "showNewMessageTag"
            }?.hook {
                result(null)
            }
        }
        // 首页启动弹窗
        "com.xiaomi.market.business_ui.main.MarketTabActivity".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "tryShowRecommend"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "tryShowRecallReCommend"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "fetchSearchHotList"
            }?.hook {
                result(null)
            }
        }
        "com.xiaomi.market.ui.FloatWebActivity".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "onCreate"
                superclass()
            }?.hook {
                val ori = proceed()
                (thisObject as? Activity)?.finish()
                result(ori)
            }
        }
        // 首页二楼
        "com.xiaomi.market.common.component.quick_item.QuickSecondHelper".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "shouldHideSecond"
            }?.hook {
                result(true)
            }
        }
        $$"com.xiaomi.market.common.analytics.onetrack.ExperimentManager$Companion".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isEnableQuickSecond"
            }?.hook {
                result(false)
            }
        }
        // 过滤子标签页
        "com.xiaomi.market.ui.PagerTabsInfo".toClassOrNull()?.apply {
            resolve().constructor {
                parameterCount {
                    it == 0 || it == 1
                }
            }.hookAll {
                val ori = proceed()
                filterTabs(thisObject)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "fromJSON"
            }?.hook {
                val ori = proceed()
                filterTabs(ori)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "fromTabInfo"
            }?.hook {
                val ori = proceed()
                filterTabs(ori)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "fromNativeTabs"
            }?.hook {
                val ori = proceed()
                filterTabs(ori)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "setAbNormals"
            }?.hook {
                result(null)
            }
        }
        // 应用详情页
        "com.xiaomi.market.ui.detail.BaseDetailActivity".toClassOrNull()?.apply {
            val detailType = resolve().firstFieldOrNull {
                name = "detailType"
                superclass()
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "initParams"
            }?.hook {
                val ori = proceed()
                if (enumDetailTypeV3 == detailType?.get(thisObject)) {
                    detailType?.set(thisObject, enumDetailTypeV4)
                }
                result(ori)
            }
        }
        "com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3".toClassOrNull()?.apply {
            val showOpenScreenAd = resolve().firstFieldOrNull {
                name = "showOpenScreenAd"
            }?.toTyped<Boolean>()
            val showAssemble = resolve().firstFieldOrNull {
                name = "showAssemble"
            }?.toTyped<Boolean>()
            constructors.filter {
                it.parameterCount > 0
            }.minByOrNull {
                it.parameterCount
            }?.hook {
                val ori = proceed()
                showOpenScreenAd?.set(thisObject, false)
                showAssemble?.set(thisObject, false)
                result(ori)
            }
            setOf(
                "showRecommend",
                "showTopBanner",
                "showTopVideo",
                "isSourceFileShowAdStyle",
            ).forEach { metName ->
                resolve().firstMethodOrNull {
                    name = metName
                }?.hook {
                    result(false)
                }
            }
            resolve().firstMethodOrNull {
                name = "getLayoutType"
            }?.hook {
                val ori = proceed()
                if (ori == "bottomMultiButton") {
                    result("bottomSingleButton")
                } else {
                    result(ori)
                }
            }
        }
        // 浏览器下载弹窗
        "com.xiaomi.market.business_ui.directmail.BottomMiniSourceFileFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "needShowAdList"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "ensureRiskLayout"
            }?.hook {
                result(null)
            }
        }
        "com.xiaomi.market.ui.detail.AppDetailCardActivity".toClassOrNull()?.apply {
            val metGetType = "com.xiaomi.market.business_ui.detail.DetailType".toClassOrNull()?.let {
                it.resolve().firstMethodOrNull {
                    name = "valueOf"
                    parameters(String::class)
                }
            }?.toTyped<Any>()
            val typeBlacklist = listOf(
                metGetType?.invoke(null, "BOTTOM_WITH_SOURCE_FILE")
            )
            val typeOnlySourceFile = metGetType?.invoke(null, "ONLY_SOURCE_FILE_DOWNLOAD")
            resolve().firstMethodOrNull {
                name = "showDetailFragment"
            }?.hook {
                val newArgs = args.toTypedArray()
                d { newArgs[0].toString() }
                if (newArgs[0] in typeBlacklist) {
                    newArgs[0] = typeOnlySourceFile
                }
                result(proceed(newArgs))
            }
        }
    }

    private fun filterTabs(pagerTabsInfo: Any) {
        clzPagerTabsInfo?.let {
            val urls = fldUrls?.get(pagerTabsInfo)?.toMutableList() ?: return@let
            val titles = fldTitles?.get(pagerTabsInfo)?.toMutableList() ?: return@let
            val tags = fldTags?.get(pagerTabsInfo)?.toMutableList() ?: return@let
            val abNormals = fldAbNormals?.get(pagerTabsInfo)?.toMutableList() ?: return@let
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
            fldUrls?.set(pagerTabsInfo, urls)
            fldTitles?.set(pagerTabsInfo, titles)
            fldTags?.set(pagerTabsInfo, tags)
            fldAbNormals?.set(pagerTabsInfo, abNormals)
        }
    }
}