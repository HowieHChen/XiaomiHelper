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

package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AlwaysShowTime : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ALWAYS_SHOW_TIME) {
            try {
                "com.miui.home.launcher.Workspace".toClass().method {
                    name = "isScreenHasClockGadget"
                }.ignored().onNoSuchMethod {
                    throw it
                }
            } catch (_: Throwable) {
                "com.miui.home.launcher.Workspace".toClass().method {
                    name = "isScreenHasClockWidget"
                }.ignored().onNoSuchMethod {
                    throw it
                }
            } catch (_: Throwable) {
                "com.miui.home.launcher.Workspace".toClass().method {
                    name = "isClockWidget"
                }
            }.hook {
                replaceToFalse()
            }
        }
    }
}