/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.lang.ClassCastException
import java.lang.ref.WeakReference


object NotificationMaxNumber : YukiBaseHooker() {
    private val maxIcon by lazy {
        Prefs.getInt(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT_ICON, 1)
    }
    private var showNotificationIcons = -1
    private var newVersion = true

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT) {
            "com.android.systemui.statusbar.phone.NotificationIconContainer".toClass().constructor().hook {
                after {
                    this.instance.current().field {
                        name = "mMaxStaticIcons"
                    }.set(maxIcon)
                }
            }
            "com.android.systemui.statusbar.phone.NotificationIconAreaController".toClass().constructor().hook {
                after {
                    val notificationIconObserver = "com.android.systemui.Dependency".toClass().method {
                        name = "get"
                        param(JavaClass)
                        modifiers { isStatic }
                    }.get().call("com.android.systemui.statusbar.policy.NotificationIconObserver".toClass()) ?: return@after
                    val mShowNotificationIcons = notificationIconObserver.current().field { name = "mShowNotificationIcons" }
                    showNotificationIcons =
                        try {
                            mShowNotificationIcons.int()
                        } catch (t: ClassCastException) {
                            newVersion = false
                            if (mShowNotificationIcons.boolean()) { 1 } else { 0 }
                        }
                    val notificationIconContainer = this.instance.current().field {
                        name = "mNotificationIcons"
                    }.any() ?: return@after
                    if (showNotificationIcons > 0) {
                        notificationIconContainer.current().field {
                            name = "mMaxStaticIcons"
                        }.set(maxIcon)
                    } else {
                        notificationIconContainer.current().field {
                            name = "mMaxStaticIcons"
                        }.set(0)
                    }
                    notificationIconContainer.current().method {
                        name = "resetViewStates"
                    }.call()
                    notificationIconContainer.current().method {
                        name = "calculateIconXTranslations"
                    }.call()
                    notificationIconContainer.current().method {
                        name = "applyIconStates"
                    }.call()
                }
            }
            "com.android.systemui.statusbar.policy.NotificationIconObserver\$2".toClass().method {
                name = "onChange"
            }.hook {
                replaceUnit {
                    val notificationIconObserver = this.instance.current().field {
                        name = "this\$0"
                    }.any() ?: return@replaceUnit
                    val context = notificationIconObserver.current().field {
                        name = "mContext"
                    }.any() as Context
                    val currentUserId = notificationIconObserver.current().field {
                        name = "mCurrentUserId"
                    }.int()
                    showNotificationIcons = (("android.provider.Settings\$System".toClassOrNull()?.method {
                        name = "getIntForUser"
                        param(ContentResolverClass, StringClass, IntType, IntType)
                        modifiers { isStatic }
                    }?.get()?.int(
                        context.contentResolver, "status_bar_show_notification_icon", 1, currentUserId)) ?: -1
                    )
                    val mShowNotificationIcons = notificationIconObserver.current().field { name = "mShowNotificationIcons" }
                    val mShowNotificationIconsNum =
                        try {
                            mShowNotificationIcons.int()
                        } catch (t: ClassCastException) {
                            newVersion = false
                            if (mShowNotificationIcons.boolean()) { 1 } else { 0 }
                        }
                    if (mShowNotificationIconsNum != showNotificationIcons) {
                        notificationIconObserver.current().field {
                            name = "mShowNotificationIcons"
                        }.set(
                            if (newVersion) { showNotificationIcons }
                            else { showNotificationIcons > 0 }
                        )
                        val callBacks = notificationIconObserver.current().field {
                            name = "mCallbacks"
                        }.any() as ArrayList<*>
                        var size = callBacks.size
                        while (true) {
                            size--
                            if (size >= 0) {
                                val notificationIconAreaController = (callBacks[size] as WeakReference<*>).get() ?: continue
                                val notificationIconContainer = notificationIconAreaController.current().field {
                                    name = "mNotificationIcons"
                                }.any() ?: continue
                                if (showNotificationIcons > 0) {
                                    notificationIconContainer.current().field {
                                        name = "mMaxStaticIcons"
                                    }.set(maxIcon)
                                } else {
                                    notificationIconContainer.current().field {
                                        name = "mMaxStaticIcons"
                                    }.set(0)
                                }
                                notificationIconContainer.current().method {
                                    name = "resetViewStates"
                                }.call()
                                notificationIconContainer.current().method {
                                    name = "calculateIconXTranslations"
                                }.call()
                                notificationIconContainer.current().method {
                                    name = "applyIconStates"
                                }.call()
                            } else {
                                break
                            }
                        }
                    }
                }
            }
        }
    }
}