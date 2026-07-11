/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.music

import android.view.ViewGroup
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object HideBottomTab : StaticHooker() {
    private val hideLongAudio by Preferences.Music.HIDE_TAB_LONG_AUDIO.lazyGet()
    private val hideQuickPlay by Preferences.Music.HIDE_TAB_QUICK_PLAY.lazyGet()
    private val hideFreeMode by Preferences.Music.HIDE_TAB_FREE_MODE.lazyGet()

    private val clzBottomNavigationViewBinding by "com.tencent.qqmusiclite.databinding.BottomNavigationViewBinding".lazyClassOrNull()
    private val fldMenuViewLongAudio by lazy {
        clzBottomNavigationViewBinding?.resolve()?.firstFieldOrNull {
            name = "menuViewLongAudio"
        }?.toTyped<Any>()
    }
    private val fldMenuViewQuickPlay by lazy {
        clzBottomNavigationViewBinding?.resolve()?.firstFieldOrNull {
            name = "menuViewQuickPlay"
        }?.toTyped<Any>()
    }
    private val fldMenuViewFreeMode by lazy {
        clzBottomNavigationViewBinding?.resolve()?.firstFieldOrNull {
            name = "menuViewFreeMode"
        }?.toTyped<Any>()
    }
    private val fldClRoot by lazy {
        "com.tencent.qqmusiclite.databinding.ViewBottomNavItemBinding".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "clRoot"
        }?.toTyped<ViewGroup>()
    }
    private val clzBottomNavigationView by "com.tencent.qqmusiclite.ui.BottomNavigationView".lazyClassOrNull()
    private val fldBinding by lazy {
        clzBottomNavigationView?.resolve()?.firstFieldOrNull {
            name = "binding"
        }?.toTyped<Any>()
    }
    private val fldMenuList by lazy {
        clzBottomNavigationView?.resolve()?.firstFieldOrNull {
            name = "menuList"
        }?.toTyped<List<Any>>()
    }
    private val fldMenuItemViewList by lazy {
        clzBottomNavigationView?.resolve()?.firstFieldOrNull {
            name = "menuItemViewList"
        }?.toTyped<List<Any>>()
    }

    override fun onInit() {
        updateSelfState(hideLongAudio || hideQuickPlay || hideFreeMode)
    }

    override fun onHook() {
        clzBottomNavigationView?.apply {
            resolve().firstMethodOrNull {
                name = "initMenuItemViews"
            }?.hook {
                val bottomBar = thisObject as? ViewGroup
                val menuItemViewList = fldMenuItemViewList?.get(thisObject)
                val menuList = fldMenuList?.get(thisObject)
                val binding = fldBinding?.get(thisObject)
                val bindingsToRemove = listOfNotNull(
                    if (hideLongAudio) fldMenuViewLongAudio?.get(binding) else null,
                    if (hideQuickPlay) fldMenuViewQuickPlay?.get(binding) else null,
                    if (hideFreeMode) fldMenuViewFreeMode?.get(binding) else null,
                )
                val indexToRemove = buildList {
                    bindingsToRemove.forEach {
                        menuItemViewList?.indexOf(it)?.let { index ->
                            add(index)
                        }
                    }
                }
                menuList?.filterIndexed { index, _ ->
                    index !in indexToRemove
                }?.toMutableList()?.let {
                    fldMenuList?.set(thisObject, it)
                }
                menuItemViewList?.filterIndexed { index, any ->
                    val hidden = index in indexToRemove
                    if (hidden) {
                        fldClRoot?.get(any)?.let {
                            bottomBar?.removeView(it)
                        }
                    }
                    !hidden
                }?.toMutableList()?.let {
                    fldMenuItemViewList?.set(thisObject, it)
                }
                result(proceed())
            }
        }
    }
}
