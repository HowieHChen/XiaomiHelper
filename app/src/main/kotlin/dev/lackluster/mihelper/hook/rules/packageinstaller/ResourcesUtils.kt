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

package dev.lackluster.mihelper.hook.rules.packageinstaller

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.ContextAwareHooker
import dev.lackluster.mihelper.hook.base.ContextScope

object ResourcesUtils : ContextAwareHooker() {
    var feedback: Int = 0
        private set
    var dialog_install_source = 0
        private set

    override val targetPackage: String
        get() = Scope.PACKAGE_INSTALLER

    override fun ContextScope.onReady() {
        feedback = "feedback".toId()
        dialog_install_source = "dialog_install_source".toStringId()
    }
}