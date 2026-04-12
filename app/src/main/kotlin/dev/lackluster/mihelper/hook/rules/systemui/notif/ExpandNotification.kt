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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object ExpandNotification : StaticHooker() {
    private val autoExpand by Preferences.SystemUI.NotifCenter.AUTO_EXPAND_NOTIF.lazyGet()
    private val ignoreFocusNotif by Preferences.SystemUI.NotifCenter.EXPAND_IGNORE_FOCUS.lazyGet()

    private val clzRowAppearanceCoordinator by "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator".lazyClassOrNull()
    private val clzPipelineEntry by "com.android.systemui.statusbar.notification.collection.PipelineEntry".lazyClassOrNull()
    private val getRepresentativeEntry by lazy {
        clzPipelineEntry?.resolve()?.firstMethodOrNull {
            name = "getRepresentativeEntry"
        }?.toTyped<Any>()
    }
    private val mSbn by lazy {
        "com.android.systemui.statusbar.notification.collection.NotificationEntry".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "mSbn"
        }?.toTyped<Any>()
    }
    private val mIsFocusNotification by lazy {
        "com.android.systemui.statusbar.notification.ExpandedNotification".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "mIsFocusNotification"
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        updateSelfState(autoExpand != 0)
    }

    override fun onHook() {
        clzRowAppearanceCoordinator?.apply {
            val fldAutoExpandFirstNotification = resolve().firstFieldOrNull {
                name = "mAutoExpandFirstNotification"
            }?.toTyped<Boolean>()
            val fldAlwaysExpandNonGroupedNotification = resolve().firstFieldOrNull {
                name = "mAlwaysExpandNonGroupedNotification"
            }?.toTyped<Boolean>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                when (autoExpand) {
                    1 -> {
                        fldAutoExpandFirstNotification?.set(thisObject, true)
                    }
                    2 -> {
                        fldAlwaysExpandNonGroupedNotification?.set(thisObject, true)
                    }
                }
                result(ori)
            }
        }
        if (autoExpand == 1 && ignoreFocusNotif && mSbn != null && mIsFocusNotification != null && clzPipelineEntry != null) {
            $$"com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator$attach$1".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("onBeforeRenderList")
                    }
                }?.hook {
                    val newArgs = args.toTypedArray()
                    (newArgs[0] as? List<*>)?.filter { entry ->
                        if (clzPipelineEntry?.isInstance(entry) == true) {
                            val notificationEntry = getRepresentativeEntry?.invoke(entry) ?: return@filter true
                            val sbn = mSbn?.get(notificationEntry) ?: return@filter true
                            val isFocusNotification = mIsFocusNotification?.get(sbn) ?: false
                            if (isFocusNotification) return@filter false
                        }
                        return@filter true
                    }?.let {
                        newArgs[0] = it
                    }
                    result(proceed(newArgs))
                }
            }
        }
    }
}