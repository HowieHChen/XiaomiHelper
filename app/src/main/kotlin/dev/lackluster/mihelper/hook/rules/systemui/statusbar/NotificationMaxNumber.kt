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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.KotlinFlowHelper.MutableStateFlow
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable


object NotificationMaxNumber : YukiBaseHooker() {
    private val maxIcon by lazy {
        Prefs.getInt(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT_ICON, 3)
    }
    private val notificationIconObserverClass by lazy {
        "com.android.systemui.statusbar.policy.NotificationIconObserver".toClass()
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT) {
            val mMaxFiled = notificationIconObserverClass.field {
                name = "mMax"
                type = "kotlinx.coroutines.flow.StateFlowImpl"
            }.remedys {
                field {
                    name = "maxIconFlow"
                    type = "kotlinx.coroutines.flow.Flow"
                }
            }.give()
            if (mMaxFiled != null) {
                notificationIconObserverClass.constructor().hookAll {
                    after {
                        mMaxFiled.set(this.instance, MutableStateFlow(maxIcon as Int?))
                    }
                }
                "com.android.systemui.statusbar.policy.NotificationIconObserver\$notificationIconObserver$1".toClassOrNull()?.apply {
                    method {
                        name = "onChange"
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}