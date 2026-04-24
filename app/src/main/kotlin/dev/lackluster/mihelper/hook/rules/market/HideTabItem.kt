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
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.getResId

object HideTabItem : StaticHooker() {
    private val tabBlur by Preferences.Market.TAB_BLUR.lazyGet()
    private val hideTab by Preferences.Market.ENABLE_FILTER_TAB.lazyGet()
    private val ignoreRestrict by Preferences.Market.FILTER_TAB_IGNORE_RESTRICT.lazyGet()
    private val showTabHome by lazy { !Preferences.Market.HIDE_TAB_HOME.get() }
    private val showTabGame by lazy { !Preferences.Market.HIDE_TAB_GAME.get() }
    private val showTabRank by lazy { !Preferences.Market.HIDE_TAB_RANK.get() }
    private val showTabAgent by lazy { !Preferences.Market.HIDE_TAB_AGENT.get() }
    private val showTabAppAssemble by lazy { !Preferences.Market.HIDE_TAB_APP_ASSEMBLE.get() }
    private val showTabMiniGame by lazy { !Preferences.Market.HIDE_TAB_MINI_GAME.get() }
    private val showTabMine by lazy { !Preferences.Market.HIDE_TAB_MINE.get() }
    private val showTabOthers by lazy { !Preferences.Market.HIDE_TAB_OTHERS.get() }

    private val clzTabInfo by "com.xiaomi.market.model.TabInfo".lazyClassOrNull()

    private var hideTabContainer = false

    override fun onInit() {
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
        updateSelfState(hideTabContainer || tabBlur || hideTab)
    }

    override fun onHook() {
        if (hideTabContainer || tabBlur) {
            "com.xiaomi.market.util.Client".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSupportBlur"
                }?.hook {
                    result(true)
                }
            }
        }
        if (hideTab) {
            if (hideTabContainer) {
                var tabContainerId = 0
                "com.xiaomi.market.ui.DoubleTabProxyActivityWrapper".toClassOrNull()?.apply {
                    val fldActivity = resolve().firstFieldOrNull {
                        name = "mActivity"
                        superclass()
                    }?.toTyped<Activity>()
                    resolve().firstMethodOrNull {
                        name = "setTabContainer"
                    }?.hook {
                        val ori = proceed()
                        fldActivity?.get(thisObject)?.let {
                            if (tabContainerId == 0) {
                                tabContainerId = it.getResId("tab_container_layout", "id", Scope.MARKET)
                            }
                            it.findViewById<View>(tabContainerId)?.visibility = View.GONE
                        }
                        result(ori)
                    }
                }
            }
            clzTabInfo?.apply {
                val tabFldTag = resolve().firstFieldOrNull {
                    name = "tag"
                    type = String::class
                }?.toTyped<String>()
                resolve().firstMethodOrNull {
                    name = "fromJSON"
                    parameterCount = 1
                }?.hook {
                    val ori = proceed()
                    val list = (ori as? List<*>)
                    if (list != null) {
                        val filtered = list.filter {
                            val tag = tabFldTag?.get(it) ?: return@filter true
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
                        result(filtered)
                    } else {
                        result(ori)
                    }
                }
            }
        }
    }
}