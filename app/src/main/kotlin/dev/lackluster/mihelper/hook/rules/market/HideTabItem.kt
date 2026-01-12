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
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.getResID

object HideTabItem : YukiBaseHooker() {
    private val tabBlur = Prefs.getBoolean(Pref.Key.Market.TAB_BLUR, false)
    private val hideTab = Prefs.getBoolean(Pref.Key.Market.FILTER_TAB, false)
    private val ignoreRestrict = Prefs.getBoolean(Pref.Key.Market.FILTER_TAB_IGNORE_RESTRICT, false)
    private val showTabHome = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_HOME, false)
    private val showTabGame = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_GAME, false)
    private val showTabRank = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_RANK, false)
    private val showTabAgent = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_AGENT, false)
    private val showTabAppAssemble = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, false)
    private val showTabMiniGame = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_MINI_GAME, false)
    private val showTabMine = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_MINE, false)
    private val showTabOthers = !Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_OTHERS, false)

    private val clzTabInfo by lazy {
        "com.xiaomi.market.model.TabInfo".toClassOrNull()
    }

    override fun onHook() {
        val hideTabContainer: Boolean
        if (hideTab && ignoreRestrict) {
            var visibleTab = 0
            if (showTabHome) visibleTab++
            if (showTabGame) visibleTab++
            if (showTabRank) visibleTab++
            if (showTabAgent) visibleTab++
            if (showTabAppAssemble) visibleTab++
            if (showTabMiniGame) visibleTab++
            if (showTabMine) visibleTab++
            hideTabContainer = (visibleTab == 1)
        } else {
            hideTabContainer = false
        }
        if (hideTabContainer || tabBlur) {
            "com.xiaomi.market.util.Client".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSupportBlur"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
        if (hideTab) {
            if (hideTabContainer) {
                "com.xiaomi.market.ui.DoubleTabProxyActivityWrapper".toClassOrNull()?.apply {
                    val fldMActivity = resolve().firstFieldOrNull {
                        name = "mActivity"
                        superclass()
                    }
                    resolve().firstMethodOrNull {
                        name = "setTabContainer"
                    }?.hook {
                        after {
                            val mActivity = fldMActivity?.copy()?.of(this.instance)?.get<Activity>()
                            mActivity?.let {
                                val tabContainerId = it.getResID("tab_container_layout", "id", Scope.MARKET)
                                it.findViewById<View>(tabContainerId)?.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            clzTabInfo?.apply {
                val tabFldTag = resolve().firstFieldOrNull {
                    name = "tag"
                    type = String::class
                }
                resolve().firstMethodOrNull {
                    name = "fromJSON"
                    parameterCount = 1
                }?.hook {
                    after {
                        val list = (this.result as List<*>).toMutableList()
                        this.result = list.filter {
                            val tag = tabFldTag?.copy()?.of(it)?.get<String>() ?: return@filter true
                            if (tag.startsWith("native_market_home")) !ignoreRestrict || showTabHome
                            else if (tag.startsWith("native_market_game")) showTabGame
                            else if (tag.startsWith("native_market_rank")) showTabRank
                            else if (tag.startsWith("native_market_agent")) showTabAgent
                            else if (
                                tag.startsWith("native_app_assemble") || tag.startsWith("native_market_video")
                            ) showTabAppAssemble
                            else if (tag.startsWith("native_market_quick_game")) showTabMiniGame
                            else if (tag.startsWith("native_market_mine")) !ignoreRestrict || showTabMine
                            else showTabOthers
                        }.toList()
                    }
                }
            }
        }
    }
}