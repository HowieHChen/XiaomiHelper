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
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.getResID

object ResourcesUtils : YukiBaseHooker() {
    private const val PKG_NAME = Scope.MIUI_HOME
    private var isInitialized = false
    var recents_quick_switch_left_enter = 0
    var recents_quick_switch_left_exit = 0
    var recents_quick_switch_right_enter = 0
    var recents_quick_switch_right_exit = 0
    var ic_task_small_window = 0
    var ic_task_small_window_pad = 0
    var ic_task_add_pair = 0
    var ic_shortcut_menu_small_window_icon = 0
    var ic_start_new_window = 0
    var start_new_window = 0
    var small_window = 0
    var accessibility_recent_task_memory_info = 0
    var status_bar_recent_memory_giga = 0
    var status_bar_recent_memory_info1 = 0
    var status_bar_recent_memory_info2 = 0
    var status_bar_recent_memory_mega = 0

    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    recents_quick_switch_left_enter = this.getResID("recents_quick_switch_left_enter", "anim", PKG_NAME)
                    recents_quick_switch_left_exit = this.getResID("recents_quick_switch_left_exit", "anim", PKG_NAME)
                    recents_quick_switch_right_enter = this.getResID("recents_quick_switch_right_enter", "anim", PKG_NAME)
                    recents_quick_switch_right_exit = this.getResID("recents_quick_switch_right_exit", "anim", PKG_NAME)
                    ic_task_small_window = this.getResID("ic_task_small_window", "drawable", PKG_NAME)
                    ic_task_small_window_pad = this.getResID("ic_task_small_window_pad", "drawable", PKG_NAME)
                    ic_task_add_pair = this.getResID("ic_task_add_pair", "drawable", PKG_NAME)
                    ic_shortcut_menu_small_window_icon = this.getResID("shortcut_menu_small_window_icon", "drawable", PKG_NAME)
                    ic_start_new_window = this.getResID("start_new_window", "drawable", PKG_NAME)
                    start_new_window = this.getResID("start_new_window", "string", PKG_NAME)
                    small_window = this.getResID("small_window", "string", PKG_NAME)
                    accessibility_recent_task_memory_info = this.getResID("accessibility_recent_task_memory_info", "string", PKG_NAME)
                    status_bar_recent_memory_giga = this.getResID("status_bar_recent_memory_giga", "string", PKG_NAME)
                    status_bar_recent_memory_info1 = this.getResID("status_bar_recent_memory_info1", "string", PKG_NAME)
                    status_bar_recent_memory_info2 = this.getResID("status_bar_recent_memory_info2", "string", PKG_NAME)
                    status_bar_recent_memory_mega = this.getResID("status_bar_recent_memory_mega", "string", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}