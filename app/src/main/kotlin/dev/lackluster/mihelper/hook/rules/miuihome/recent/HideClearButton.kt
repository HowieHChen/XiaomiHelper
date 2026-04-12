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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object HideClearButton : StaticHooker() {
    private val memInfoClear by Preferences.MiuiHome.RECENT_MEM_INFO_CLEAR.lazyGet()
    private val clzRecentsContainer by lazy {
        "com.miui.home.recents.views.RecentsContainer".toClassOrNull()
    }
    private val metCleanInRecents by lazy {
        clzRecentsContainer?.resolve()?.firstMethodOrNull {
            name = "cleanInRecents"
        }?.toTyped<Unit>()
    }
    private val metIsClearContainerVisible by lazy {
        clzRecentsContainer?.resolve()?.firstMethodOrNull {
            name = "isClearContainerVisible"
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.HIDE_RECENT_CLEAR_BUTTON.get())
    }

    override fun onHook() {
        clzRecentsContainer?.apply {
            val fldClearAnimView = resolve().firstFieldOrNull {
                name = "mClearAnimView"
            }?.toTyped<View>()
            val fldTxtMemoryInfo1 = resolve().firstFieldOrNull {
                name = "mTxtMemoryInfo1"
            }?.toTyped<TextView>()
            val fldTxtMemoryInfo2 = resolve().firstFieldOrNull {
                name = "mTxtMemoryInfo2"
            }?.toTyped<TextView>()
            val fldSeparatorForMemoryInfo = resolve().firstFieldOrNull {
                name = "mSeparatorForMemoryInfo"
            }?.toTyped<View>()
            val fldTxtMemoryContainer = resolve().firstFieldOrNull {
                name = "mTxtMemoryContainer"
            }?.toTyped<ViewGroup>()
            resolve().firstMethodOrNull {
                name = "onFinishInflate"
            }?.hook {
                val ori = proceed()
                fldClearAnimView?.get(thisObject)?.apply {
                    isClickable = false
                    isLongClickable = false
                    setOnClickListener(null)
                    setOnLongClickListener(null)
                    visibility = View.INVISIBLE
                }
                if (memInfoClear) {
                    fldTxtMemoryInfo1?.get(thisObject)?.apply {
                        isClickable = false
                    }
                    fldTxtMemoryInfo2?.get(thisObject)?.apply {
                        isClickable = false
                    }
                    fldSeparatorForMemoryInfo?.get(thisObject)?.apply {
                        isClickable = false
                    }
                    fldTxtMemoryContainer?.get(thisObject)?.apply {
                        isLongClickable = true
                        setOnLongClickListener {
                            val isClearContainerVisible = metIsClearContainerVisible?.invoke(thisObject) ?: false
                            if (isClearContainerVisible) {
                                metCleanInRecents?.invoke(thisObject)
                            }
                            true
                        }
                    }
                }
                result(ori)
            }
        }
    }
}