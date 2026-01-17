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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object KeepNotification : YukiBaseHooker() {
    private val mSbn by lazy {
        "com.android.systemui.statusbar.notification.collection.NotificationEntry".toClassOrNull()
            ?.resolve()?.firstFieldOrNull {
                name = "mSbn"
            }?.self?.apply { makeAccessible() }
    }
    private val mHasShownAfterUnlock by lazy {
        "com.android.systemui.statusbar.notification.ExpandedNotification".toClassOrNull()
            ?.resolve()?.firstFieldOrNull {
                name = "mHasShownAfterUnlock"
            }?.self?.apply { makeAccessible() }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.KEEP_NOTIFICATION) {
            setOf(
                "com.android.systemui.statusbar.notification.interruption.MiuiKeyguardNotificationVisibilityProvider",
                "com.android.systemui.statusbar.notification.interruption.KeyguardNotificationVisibilityProviderImpl",
            ).forEach { className ->
                className.toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "shouldHideNotification"
                    }?.hook {
                        before {
                            val notificationEntry = this.args(0).any() ?: return@before
                            mSbn?.get(notificationEntry)?.let { sbn ->
                                mHasShownAfterUnlock?.set(sbn, false)
                            }
                        }
                    }
                }
            }
        }
    }
}