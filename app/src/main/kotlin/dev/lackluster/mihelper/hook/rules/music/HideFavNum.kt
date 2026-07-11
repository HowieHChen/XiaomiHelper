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

object HideFavNum : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.Music.HIDE_FAV_NUM.get())
    }

    override fun onHook() {
        // 首页推荐
        "com.tencent.qqmusiclite.data.mapper.ShelfCard2Mapper".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "extractExtra"
            }?.hook {
                val ori = proceed()
                if (ori is LinkedHashMap<*, *>) {
                    ori.remove("song_fav_num")
                    ori.remove("song_fav_show")
                }
                result(ori)
            }
        }
        // 播放页
        $$"com.tencent.qqmusiclite.data.repo.favor.GetSurpriseRepo$FavorNumData".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getFavNum"
            }?.hook {
                result(null)
            }
        }
        // 二级页
        "com.tencent.qqmusiclite.usecase.favorSong.SongFavNumUseCase".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "fetch"
            }?.hook {
                result(emptyMap<String, String>())
            }
        }
    }
}