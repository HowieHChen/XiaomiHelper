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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideMyPageElement : YukiBaseHooker() {
    override fun onHook() {
        "com.tencent.qqmusiclite.fragment.my.MyViewModel".toClassOrNull()?.apply {
            hasEnable(Pref.Key.Music.MY_HIDE_BANNER) {
                resolve().firstMethodOrNull {
                    name = "getMyBannerCard"
                }?.hook {
                    intercept()
                }
            }
            hasEnable(Pref.Key.Music.MY_HIDE_REC_PLAYLIST) {
                resolve().firstMethodOrNull {
                    name = "requestRecommendSongs"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}