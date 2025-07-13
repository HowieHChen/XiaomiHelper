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

package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object NotifFreeform : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.NOTIF_FREEFORM) {
            "com.android.systemui.statusbar.notification.row.MiuiExpandableNotificationRow".toClassOrNull()?.apply {
                method {
                    name = "updateMiniWindowBar"
                }.hook {
                    after {
                        this.instance.current().field {
                            name = "mCanSlide"
                        }.setTrue()
                    }
                }
            }
            "com.android.systemui.statusbar.notification.row.ExpandableNotificationRowInjector".toClassOrNull()?.apply {
                method {
                    name = "updateMiniWindowBar"
                }.hook {
                    after {
                        this.instance.current().field {
                            name = "canSlide"
                        }.setTrue()
                    }
                }
            }
        }
    }
}