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
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet

object HideMyPageElement : StaticHooker() {
    private val hideBanner by Preferences.Music.HIDE_MY_BANNER.lazyGet()
    private val hideRecommend by Preferences.Music.HIDE_MY_REC_PLAYLIST.lazyGet()

    override fun onInit() {
        updateSelfState(hideBanner || hideRecommend)
    }

    override fun onHook() {
        "com.tencent.qqmusiclite.fragment.my.MyViewModel".toClassOrNull()?.apply {
            if (hideBanner) {
                resolve().firstMethodOrNull {
                    name = "getMyBannerCard"
                }?.hook {
                    result(null)
                }
            }
            if (hideRecommend) {
                resolve().firstMethodOrNull {
                    name = "requestRecommendSongs"
                }?.hook {
                    result(null)
                }
            }
        }
    }
}