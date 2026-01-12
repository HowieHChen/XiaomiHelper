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
import dev.lackluster.mihelper.utils.Prefs

object HideTopTab : YukiBaseHooker() {
    private const val TOP_TAB_HOME_ID = 1
    private const val TOP_TAB_KEGE_ID = 2
    private const val TOP_TAB_LONG_AUDIO_ID = 3
    private const val TOP_TAB_QUICK_PLAY_ID = 4
    private val hideKaraoke = Prefs.getBoolean(Pref.Key.Music.HIDE_KARAOKE, false)
    private val hideLongAudio = Prefs.getBoolean(Pref.Key.Music.HIDE_LONG_AUDIO, false)
    private val hideDiscover = Prefs.getBoolean(Pref.Key.Music.HIDE_DISCOVER, false)
    private val clzTopTab by lazy {
        "com.tencent.qqmusiclite.data.dto.shelfcard2.TopTab".toClassOrNull()
    }
    private val fldTabId by lazy {
        clzTopTab?.resolve()?.firstFieldOrNull {
            name = "id"
        }?.self?.apply {
            isAccessible = true
        }
    }

    override fun onHook() {
        if (hideKaraoke || hideLongAudio || hideDiscover) {
            "com.tencent.qqmusiclite.fragment.home.BaseHomeFragment".toClassOrNull()?.apply {
                if (hideLongAudio) {
                    val mIsLongAudioEnable = resolve().firstFieldOrNull {
                        name = "mIsLongAudioEnable"
                    }
                    resolve().firstMethodOrNull {
                        name = "setupViewPager"
                    }?.hook {
                        before {
                            mIsLongAudioEnable?.copy()?.of(this.instance)?.set(false)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getTabs"
                }?.hook {
                    after {
                        val list = (this.result as List<*>).toMutableList()
                        this.result = list.filter {
                            val id = (fldTabId?.get(it) as? Int) ?: return@filter true
                            when (id) {
                                TOP_TAB_HOME_ID -> true
                                TOP_TAB_KEGE_ID -> !hideKaraoke
                                TOP_TAB_LONG_AUDIO_ID -> !hideLongAudio
                                TOP_TAB_QUICK_PLAY_ID -> !hideDiscover
                                else -> true
                            }
                        }.toMutableList()
                    }
                }
            }
        }
    }
}