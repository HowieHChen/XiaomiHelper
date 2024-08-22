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

package dev.lackluster.mihelper.hook.rules.miuihome.icon

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object IconCornerForLarge : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ICON_CORNER4LARGE, extraCondition = { !Device.isPad }) {
            "com.miui.home.launcher.bigicon.BigIconUtil".toClass().method {
                name = "getCroppedFromCorner"
                paramCount = 4
            }.hook {
                before {
                    this.args(0).set(2)
                    this.args(1).set(2)
                }
            }
            "com.miui.home.launcher.maml.MaMlHostView".toClass().method {
                name = "getCornerRadius"
            }.hook {
                before {
                    this.result = this.instance.current().field {
                        name = "mEnforcedCornerRadius"
                        superClass()
                    }.float()
                }
            }
            setOf(
                "com.miui.home.launcher.maml.MaMlHostView",
                "com.miui.home.launcher.LauncherAppWidgetHostView"
            ).forEach {
                it.toClass().method {
                    name = "computeRoundedCornerRadius"
                    paramCount = 1
                }.hook {
                    before {
                        this.result = this.instance.current().field {
                            name = "mEnforcedCornerRadius"
                            superClass()
                        }.float()
                    }
                }
            }
        }
    }
}