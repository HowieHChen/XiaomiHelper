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

    override fun onHook() {
        hasEnable(Pref.Key.Market.AD_BLOCKER) {
            // 更新页
            "com.xiaomi.market.ui.UpdateListRvAdapter".toClassOrNull()?.apply {
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
            "com.xiaomi.market.business_ui.secondfloor.SecondFloorController".toClassOrNull()?.apply {
                method {
                    name = "create"
                }.hook {
                    intercept()
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
                titles.forEachIndexed { index, map ->
                    if (map.getOrDefault("cn", "cn") == map.getOrDefault("en", "en")) {
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