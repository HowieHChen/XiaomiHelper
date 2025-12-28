/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.getResID

object ResourcesUtils : YukiBaseHooker() {
    private const val PKG_NAME = Scope.SYSTEM_UI
    private var isInitialized = false
    // Status bar clock
    var clock = 0 // Time (status bar)
    var pad_clock = 0 // Date (status bar, pad)
    var big_time = 0 // Time (notification shade)
    var date_time = 0 // Date (notification shade)
    var normal_control_center_date_view = 0 // Date (control center)
    var horizontal_time = 0 // Time and date (notification shade, landscape)
    var horizontal_date_time = 0 // ?
    var miui_notification_menu_more_setting = 0
    var fmt_time_12hour_minute_second_pm = 0
    var fmt_time_12hour_minute_second = 0
    var fmt_time_12hour_minute_pm = 0
    var fmt_time_12hour_minute = 0
    var fmt_time_24hour_minute_second = 0
    var fmt_time_24hour_minute = 0
    var status_bar_clock_date_time_format = 0
    var status_bar_clock_date_time_format_12 = 0
    // Status bar
    var mobile_type_single = 0
    var mobile_signal_container = 0
    var TextAppearance_StatusBar_Battery_Percent = 0
    var TextAppearance_StatusBar_NetWorkSpeedNumber = 0
    // Media control panel
    var notification_element_blend_shade_colors = 0
    var notification_element_blend_colors = 0
    var notification_item_bg_radius = 0
    var media_bg = 0
    var media_bg_view = 0
    var album_art = 0
    var icon = 0
    var header_title = 0
    var header_artist = 0
    var media_seamless = 0
    var media_seamless_button = 0
    var media_seamless_image = 0
    var actions = 0
    var action0 = 0
    var action1 = 0
    var action2 = 0
    var action3 = 0
    var action4 = 0
    var media_progress_bar = 0
    var media_elapsed_time = 0
    var media_total_time = 0
    // Lockscreen
    var pad_clock_xml = 0
    var keyguard_carrier_text = 0
    var TextAppearance_StatusBar_Clock = 0
    var status_bar_padding_extra_start = 0
    var status_bar_clock_margin_end = 0
    var normal_control_center_carrier_view = 0
    var normal_control_center_carrier_second_view = 0
    var normal_control_center_carrier_vertical_separator = 0
    var lock_screen_carrier_airplane_mode_on = 0

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    clock = this.getResID("clock", "id", PKG_NAME)
                    pad_clock = this.getResID("pad_clock", "id", PKG_NAME)
                    big_time = this.getResID("big_time", "id", PKG_NAME)
                    date_time = this.getResID("date_time", "id", PKG_NAME)
                    normal_control_center_date_view = this.getResID("normal_control_center_date_view", "id", PKG_NAME)
                    horizontal_time = this.getResID("horizontal_time", "id", PKG_NAME)
                    horizontal_date_time = this.getResID("horizontal_date_time", "id", PKG_NAME)
                    miui_notification_menu_more_setting = this.getResID("miui_notification_menu_more_setting", "string", PKG_NAME)
                    fmt_time_12hour_minute_second_pm = this.getResID("fmt_time_12hour_minute_second_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute_second = this.getResID("fmt_time_12hour_minute_second", "string", PKG_NAME)
                    fmt_time_12hour_minute_pm = this.getResID("fmt_time_12hour_minute_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute = this.getResID("fmt_time_12hour_minute", "string", PKG_NAME)
                    fmt_time_24hour_minute_second = this.getResID("fmt_time_24hour_minute_second", "string", PKG_NAME)
                    fmt_time_24hour_minute = this.getResID("fmt_time_24hour_minute", "string", PKG_NAME)
                    status_bar_clock_date_time_format = this.getResID("status_bar_clock_date_time_format", "string", PKG_NAME)
                    status_bar_clock_date_time_format_12 = this.getResID("status_bar_clock_date_time_format_12", "string", PKG_NAME)
                    mobile_type_single = this.getResID("mobile_type_single", "id", PKG_NAME)
                    mobile_signal_container = this.getResID("mobile_signal_container", "id", PKG_NAME)
                    TextAppearance_StatusBar_Battery_Percent = this.getResID("TextAppearance.StatusBar.Battery.Percent", "style", PKG_NAME)
                    TextAppearance_StatusBar_NetWorkSpeedNumber = this.getResID("TextAppearance.StatusBar.NetWorkSpeedNumber", "style", PKG_NAME)
                    notification_element_blend_shade_colors = this.getResID("notification_element_blend_shade_colors", "array", PKG_NAME)
                    notification_element_blend_colors = this.getResID("notification_element_blend_colors", "array", PKG_NAME)
                    notification_item_bg_radius = this.getResID("notification_item_bg_radius", "dimen", PKG_NAME)
                    pad_clock_xml = this.getResID("pad_clock", "layout", PKG_NAME)
                    keyguard_carrier_text = this.getResID("keyguard_carrier_text", "id", PKG_NAME)
                    TextAppearance_StatusBar_Clock = this.getResID("TextAppearance.StatusBar.Clock", "style", PKG_NAME)
                    status_bar_padding_extra_start = this.getResID("status_bar_padding_extra_start", "dimen", PKG_NAME)
                    status_bar_clock_margin_end = this.getResID("status_bar_clock_margin_end", "dimen", PKG_NAME)
                    media_bg = this.getResID("media_bg", "id", PKG_NAME)
                    media_bg_view = this.getResID("media_bg_view", "id", PKG_NAME)
                    album_art = this.getResID("album_art", "id", PKG_NAME)
                    icon = this.getResID("icon", "id", PKG_NAME)
                    header_title = this.getResID("header_title", "id", PKG_NAME)
                    header_artist = this.getResID("header_artist", "id", PKG_NAME)
                    media_seamless = this.getResID("media_seamless", "id", PKG_NAME)
                    media_seamless_button = this.getResID("media_seamless_button", "id", PKG_NAME)
                    media_seamless_image = this.getResID("media_seamless_image", "id", PKG_NAME)
                    actions = this.getResID("actions", "id", PKG_NAME)
                    action0 = this.getResID("action0", "id", PKG_NAME)
                    action1 = this.getResID("action1", "id", PKG_NAME)
                    action2 = this.getResID("action2", "id", PKG_NAME)
                    action3 = this.getResID("action3", "id", PKG_NAME)
                    action4 = this.getResID("action4", "id", PKG_NAME)
                    media_progress_bar = this.getResID("media_progress_bar", "id", PKG_NAME)
                    media_elapsed_time = this.getResID("media_elapsed_time", "id", PKG_NAME)
                    media_total_time = this.getResID("media_total_time", "id", PKG_NAME)
                    normal_control_center_carrier_view = this.getResID("normal_control_center_carrier_view", "id", PKG_NAME)
                    normal_control_center_carrier_second_view = this.getResID("normal_control_center_carrier_second_view", "id", PKG_NAME)
                    normal_control_center_carrier_vertical_separator = this.getResID("normal_control_center_carrier_vertical_separator", "id", PKG_NAME)
                    lock_screen_carrier_airplane_mode_on = this.getResID("lock_screen_carrier_airplane_mode_on", "string", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}