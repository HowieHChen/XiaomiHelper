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

package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.getResID

object ResourcesUtils : YukiBaseHooker() {
    private const val PKG_NAME = Scope.SECURITY_CENTER
    private var isInitialized = false
    var warning_info = 0
    var accept = 0
    var button_text_accept = 0
    var usb_adb_input_apply_step_1 = 0
    var usb_adb_input_apply_step_2 = 0
    var usb_adb_input_apply_step_3 = 0

    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    warning_info = this.getResID("warning_info", "id", PKG_NAME)
                    accept = this.getResID("accept", "id", PKG_NAME)
                    button_text_accept = this.getResID("button_text_accept", "string", PKG_NAME)
                    usb_adb_input_apply_step_1 = this.getResID("usb_adb_input_apply_step_1", "string", PKG_NAME)
                    usb_adb_input_apply_step_2 = this.getResID("usb_adb_input_apply_step_2", "string", PKG_NAME)
                    usb_adb_input_apply_step_3 = this.getResID("usb_adb_input_apply_step_3", "string", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}