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
import dev.lackluster.mihelper.hook.utils.toTyped

object HideTopTab : StaticHooker() {
    private const val TOP_TAB_HOME_ID = 1
    private const val TOP_TAB_KEGE_ID = 2
    private const val TOP_TAB_LONG_AUDIO_ID = 3
    private const val TOP_TAB_QUICK_PLAY_ID = 4
    private val hideKaraoke by Preferences.Music.HIDE_KARAOKE.lazyGet()
    private val hideLongAudio by Preferences.Music.HIDE_LONG_AUDIO.lazyGet()
    private val hideDiscover by Preferences.Music.HIDE_DISCOVER.lazyGet()
    private val clzTopTab by "com.tencent.qqmusiclite.data.dto.shelfcard2.TopTab".lazyClassOrNull()
    private val fldTabId by lazy {
        clzTopTab?.resolve()?.firstFieldOrNull {
            name = "id"
        }?.toTyped<Int>()
    }

    override fun onInit() {
        updateSelfState(hideKaraoke || hideLongAudio || hideDiscover)
    }

    override fun onHook() {
        "com.tencent.qqmusiclite.fragment.home.BaseHomeFragment".toClassOrNull()?.apply {
            if (hideLongAudio) {
                val mIsLongAudioEnable = resolve().firstFieldOrNull {
                    name = "mIsLongAudioEnable"
                }?.toTyped<Boolean>()
                resolve().firstMethodOrNull {
                    name = "setupViewPager"
                }?.hook {
                    mIsLongAudioEnable?.set(thisObject, false)
                    result(proceed())
                }
            }
            resolve().firstMethodOrNull {
                name = "getTabs"
            }?.hook {
                val ori = proceed()
                val list = ori as? List<*>
                if (list != null) {
                    val filtered = list.filter {
                        val id = fldTabId?.get(it) ?: return@filter true
                        when (id) {
                            TOP_TAB_HOME_ID -> true
                            TOP_TAB_KEGE_ID -> !hideKaraoke
                            TOP_TAB_LONG_AUDIO_ID -> !hideLongAudio
                            TOP_TAB_QUICK_PLAY_ID -> !hideDiscover
                            else -> true
                        }
                    }.toMutableList()
                    result(filtered)
                } else {
                    result(ori)
                }
            }
        }
    }
}