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

package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ExpandNotification : YukiBaseHooker() {
    private val expand = Prefs.getInt(Pref.Key.SystemUI.NotifCenter.EXPAND_NOTIFICATION, 0)
    private val ignoreFocusNotification by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.EXPAND_IGNORE_FOCUS, false)
    }
    private val clzRowAppearanceCoordinator by lazy {
        "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator".toClassOrNull()
    }
    private val clzPipelineEntry by lazy {
        "com.android.systemui.statusbar.notification.collection.PipelineEntry".toClassOrNull()
    }
    private val getRepresentativeEntry by lazy {
        clzPipelineEntry
            ?.resolve()
            ?.firstMethodOrNull {
                name = "getRepresentativeEntry"
            }
            ?.self
    }
    private val mSbn by lazy {
        "com.android.systemui.statusbar.notification.collection.NotificationEntry".toClassOrNull()
            ?.resolve()
            ?.firstFieldOrNull {
                name = "mSbn"
            }
            ?.self
    }
    private val mIsFocusNotification by lazy {
        "com.android.systemui.statusbar.notification.ExpandedNotification".toClassOrNull()
            ?.resolve()
            ?.firstFieldOrNull {
                name = "mIsFocusNotification"
            }
            ?.self
    }

    override fun onHook() {
        if (expand != 0) {
            clzRowAppearanceCoordinator?.apply {
                resolve().firstConstructor().hook {
                    after {
                        when (expand) {
                            1 -> {
                                this.instance.asResolver().firstFieldOrNull {
                                    name = "mAutoExpandFirstNotification"
                                }?.set(true)
                            }
                            2 -> {
                                this.instance.asResolver().firstFieldOrNull {
                                    name = "mAlwaysExpandNonGroupedNotification"
                                }?.set(true)
                            }
                        }
                    }
                }
            }
        }
        if (expand == 1 && ignoreFocusNotification && mSbn != null && mIsFocusNotification != null && clzPipelineEntry != null) {
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator\$attach$1".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "onBeforeRenderList$1"
                }?.hook {
                    before {
                        this.args(0).list<Any?>().filter { entry ->
                            if (clzPipelineEntry?.isInstance(entry) == true) {
                                getRepresentativeEntry?.invoke(entry)?.let { notificationEntry ->
                                    mSbn?.get(notificationEntry)?.let { sbn ->
                                        val isFocusNotification = mIsFocusNotification?.getBoolean(sbn) == true
                                        if (isFocusNotification) {
                                            return@filter false
                                        }
                                    }
                                }
                            }
                            return@filter true
                        }.let {
                            this.args(0).set(it)
                        }
                    }
                }
            }
        }
    }
}