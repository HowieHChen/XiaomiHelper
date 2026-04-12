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

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.ContextAwareHooker
import dev.lackluster.mihelper.hook.base.ContextScope

object ResourcesUtils : ContextAwareHooker() {
    override val targetPackage: String
        get() = Scope.SECURITY_CENTER

    var warning_info = 0
        private set
    var accept = 0
        private set
    var button_text_accept = 0
        private set
    var usb_adb_input_apply_step_1 = 0
        private set
    var usb_adb_input_apply_step_2 = 0
        private set
    var usb_adb_input_apply_step_3 = 0
        private set

    override fun ContextScope.onReady() {
        warning_info = "warning_info".toId()
        accept = "accept".toId()
        button_text_accept = "button_text_accept".toStringId()
        usb_adb_input_apply_step_1 = "usb_adb_input_apply_step_1".toStringId()
        usb_adb_input_apply_step_2 = "usb_adb_input_apply_step_2".toStringId()
        usb_adb_input_apply_step_3 = "usb_adb_input_apply_step_3".toStringId()
    }
}