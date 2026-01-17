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

package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideClearButton : YukiBaseHooker() {
    private val memInfoClear = Prefs.getBoolean(Pref.Key.MiuiHome.RECENT_MEM_INFO_CLEAR, false)
    private val clzRecentsContainer by lazy {
        "com.miui.home.recents.views.RecentsContainer".toClassOrNull()
    }
    private val metCleanInRecents by lazy {
        clzRecentsContainer?.resolve()?.firstMethodOrNull {
            name = "cleanInRecents"
        }?.self?.apply { makeAccessible() }
    }
    private val metIsClearContainerVisible by lazy {
        clzRecentsContainer?.resolve()?.firstMethodOrNull {
            name = "isClearContainerVisible"
        }?.self?.apply { makeAccessible() }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_HIDE_CLEAR_BUTTON) {
            clzRecentsContainer?.apply {
                val fldClearAnimView = resolve().firstFieldOrNull {
                    name = "mClearAnimView"
                }
                val fldTxtMemoryInfo1 = resolve().firstFieldOrNull {
                    name = "mTxtMemoryInfo1"
                }
                val fldTxtMemoryInfo2 = resolve().firstFieldOrNull {
                    name = "mTxtMemoryInfo2"
                }
                val fldSeparatorForMemoryInfo = resolve().firstFieldOrNull {
                    name = "mSeparatorForMemoryInfo"
                }
                val fldTxtMemoryContainer = resolve().firstFieldOrNull {
                    name = "mTxtMemoryContainer"
                }
                resolve().firstMethodOrNull {
                    name = "onFinishInflate"
                }?.hook {
                    after {
                        fldClearAnimView?.copy()?.of(this.instance)?.get<View>()?.apply {
                            isClickable = false
                            isLongClickable = false
                            setOnClickListener(null)
                            setOnLongClickListener(null)
                            visibility = View.INVISIBLE
                        }
                        if (memInfoClear) {
                            val instance = this.instance
                            fldTxtMemoryInfo1?.copy()?.of(this.instance)?.get<TextView>()?.apply {
                                isClickable = false
                            }
                            fldTxtMemoryInfo2?.copy()?.of(this.instance)?.get<TextView>()?.apply {
                                isClickable = false
                            }
                            fldSeparatorForMemoryInfo?.copy()?.of(this.instance)?.get<View>()?.apply {
                                isClickable = false
                            }
                            fldTxtMemoryContainer?.copy()?.of(this.instance)?.get<ViewGroup>()?.apply {
                                isLongClickable = true
                                setOnLongClickListener {
                                    val isClearContainerVisible = metIsClearContainerVisible?.invoke(instance) == true
                                    if (isClearContainerVisible) {
                                        metCleanInRecents?.invoke(instance)
                                    }
                                    true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}