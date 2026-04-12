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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object HideFavNum : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.Music.HIDE_FAV_NUM.get())
    }

    override fun onHook() {
        // 首页推荐
        "com.tencent.qqmusiclite.model.shelfcard.Card".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getSongFavNum"
            }?.hook {
                result(null)
            }
        }
        // 二级页
        "com.tencent.qqmusiclite.ui.SongItemNewKt".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "needShowFavNum"
            }?.hook {
                result(false)
            }
        }
        // 播放页
        "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
            val viewModel = resolve().firstFieldOrNull {
                name = "viewModel"
            }?.toTyped<Any>()
            val getViewLifecycleOwner = resolve().firstMethodOrNull {
                name = "getViewLifecycleOwner"
                superclass()
            }?.toTyped<Any>()
            val clzPlayerSongViewModel = "com.tencent.qqmusiclite.activity.player.song.PlayerSongViewModel".toClassOrNull()
            val getFavorNumLiveData = clzPlayerSongViewModel?.resolve()?.firstMethodOrNull {
                name = "getFavorNumLiveData"
            }?.toTyped<Any>()
            val getFavorNumCacheLiveData = clzPlayerSongViewModel?.resolve()?.firstMethodOrNull {
                name = "getFavorNumCacheLiveData"
            }?.toTyped<Any>()
            val removeObservers = "androidx.lifecycle.MutableLiveData".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "removeObservers"
                superclass()
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "viewModelDataSet"
            }?.hook {
                val ori = proceed()
                val viewLifecycleOwner = getViewLifecycleOwner?.invoke(thisObject) ?: return@hook result(ori)
                val vm = viewModel?.get(thisObject) ?: return@hook result(ori)
                val favorNumLiveData = getFavorNumLiveData?.invoke(vm) ?: return@hook result(ori)
                removeObservers?.invoke(favorNumLiveData, viewLifecycleOwner)
                val favorNumCacheLiveData = getFavorNumCacheLiveData?.invoke(vm) ?: return@hook result(ori)
                removeObservers?.invoke(favorNumCacheLiveData, viewLifecycleOwner)
                result(ori)
            }
        }
        // 歌词页
        "com.tencent.qqmusiclite.activity.player.lyric.PlayerLyricFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "observeFavNumLiveData"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "removeObserveFavNumLiveData"
            }?.hook {
                result(null)
            }
        }
    }
}