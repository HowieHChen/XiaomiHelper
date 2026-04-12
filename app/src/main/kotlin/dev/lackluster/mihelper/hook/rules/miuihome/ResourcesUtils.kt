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

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.ContextAwareHooker
import dev.lackluster.mihelper.hook.base.ContextScope

object ResourcesUtils : ContextAwareHooker() {
    override val targetPackage: String
        get() = Scope.MIUI_HOME
    
    var ic_task_small_window = 0
        private set
    var ic_task_small_window_pad = 0
        private set
    var ic_task_add_pair = 0
        private set
    var start_new_window = 0
        private set
    var small_window = 0
        private set
    var accessibility_recent_task_memory_info = 0
        private set
    var status_bar_recent_memory_giga = 0
        private set
    var status_bar_recent_memory_info1 = 0
        private set
    var status_bar_recent_memory_info2 = 0
        private set
    var status_bar_recent_memory_mega = 0
        private set

    override fun ContextScope.onReady() {
        ic_task_small_window = "ic_task_small_window".toDrawableId()
        ic_task_small_window_pad = "ic_task_small_window_pad".toDrawableId()
        ic_task_add_pair = "ic_task_add_pair".toDrawableId()
        start_new_window = "start_new_window".toStringId()
        small_window = "small_window".toStringId()
        accessibility_recent_task_memory_info = "accessibility_recent_task_memory_info".toStringId()
        status_bar_recent_memory_giga = "status_bar_recent_memory_giga".toStringId()
        status_bar_recent_memory_info1 = "status_bar_recent_memory_info1".toStringId()
        status_bar_recent_memory_info2 = "status_bar_recent_memory_info2".toStringId()
        status_bar_recent_memory_mega = "status_bar_recent_memory_mega".toStringId()
    }
}