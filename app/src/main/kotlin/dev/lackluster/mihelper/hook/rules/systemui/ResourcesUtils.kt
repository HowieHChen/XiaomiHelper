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

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.ContextAwareHooker
import dev.lackluster.mihelper.hook.base.ContextScope

object ResourcesUtils : ContextAwareHooker() {
    override val targetPackage: String
        get() = Scope.SYSTEM_UI
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
    var megabyte_per_second = 0
    var kilobyte_per_second = 0
    var stat_sys_alarm = 0
    var stat_sys_gps_on = 0
    var stat_sys_quiet_mode = 0
    var stat_sys_ringer_silent = 0
    var stat_sys_ringer_vibrate = 0
    var stat_sys_signal_0 = 0
    var notification_icon_area = 0
    var status_bar_view_state_tag = 0
    var status_bar_icon_height = 0
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
    var media_control_bg_radius = 0
    var tiny_media_session_view = 0
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
    var kept_notifications_on_keyguard = 0

    override fun ContextScope.onReady() {
        clock = "clock".toId()
        pad_clock = "pad_clock".toId()
        big_time = "big_time".toId()
        date_time = "date_time".toId()
        normal_control_center_date_view = "normal_control_center_date_view".toId()
        horizontal_time = "horizontal_time".toId()
        horizontal_date_time = "horizontal_date_time".toId()
        miui_notification_menu_more_setting = "miui_notification_menu_more_setting".toStringId()
        fmt_time_12hour_minute_second_pm = "fmt_time_12hour_minute_second_pm".toStringId()
        fmt_time_12hour_minute_second = "fmt_time_12hour_minute_second".toStringId()
        fmt_time_12hour_minute_pm = "fmt_time_12hour_minute_pm".toStringId()
        fmt_time_12hour_minute = "fmt_time_12hour_minute".toStringId()
        fmt_time_24hour_minute_second = "fmt_time_24hour_minute_second".toStringId()
        fmt_time_24hour_minute = "fmt_time_24hour_minute".toStringId()
        status_bar_clock_date_time_format = "status_bar_clock_date_time_format".toStringId()
        status_bar_clock_date_time_format_12 = "status_bar_clock_date_time_format_12".toStringId()
        mobile_type_single = "mobile_type_single".toId()
        mobile_signal_container = "mobile_signal_container".toId()
        TextAppearance_StatusBar_Battery_Percent = "TextAppearance.StatusBar.Battery.Percent".toStyleId()
        TextAppearance_StatusBar_NetWorkSpeedNumber = "TextAppearance.StatusBar.NetWorkSpeedNumber".toStyleId()
        megabyte_per_second = "megabyte_per_second".toStringId()
        kilobyte_per_second = "kilobyte_per_second".toStringId()
        stat_sys_alarm = "stat_sys_alarm".toDrawableId()
        stat_sys_gps_on = "stat_sys_gps_on".toDrawableId()
        stat_sys_quiet_mode = "stat_sys_quiet_mode".toDrawableId()
        stat_sys_ringer_silent = "stat_sys_ringer_silent".toDrawableId()
        stat_sys_ringer_vibrate = "stat_sys_ringer_vibrate".toDrawableId()
        stat_sys_signal_0 = "stat_sys_signal_0".toDrawableId()
        notification_icon_area = "notification_icon_area".toId()
        status_bar_view_state_tag = "status_bar_view_state_tag".toId()
        status_bar_icon_height = "status_bar_icon_height".toDimenId()
        notification_element_blend_shade_colors = "notification_element_blend_shade_colors".toArrayId()
        notification_element_blend_colors = "notification_element_blend_colors".toArrayId()
        notification_item_bg_radius = "notification_item_bg_radius".toDimenId()
        pad_clock_xml = "pad_clock".toLayoutId()
        keyguard_carrier_text = "keyguard_carrier_text".toId()
        TextAppearance_StatusBar_Clock = "TextAppearance.StatusBar.Clock".toStyleId()
        status_bar_padding_extra_start = "status_bar_padding_extra_start".toDimenId()
        status_bar_clock_margin_end = "status_bar_clock_margin_end".toDimenId()
        media_bg = "media_bg".toId()
        media_bg_view = "media_bg_view".toId()
        album_art = "album_art".toId()
        icon = "icon".toId()
        header_title = "header_title".toId()
        header_artist = "header_artist".toId()
        media_seamless = "media_seamless".toId()
        media_seamless_button = "media_seamless_button".toId()
        media_seamless_image = "media_seamless_image".toId()
        actions = "actions".toId()
        action0 = "action0".toId()
        action1 = "action1".toId()
        action2 = "action2".toId()
        action3 = "action3".toId()
        action4 = "action4".toId()
        media_progress_bar = "media_progress_bar".toId()
        media_elapsed_time = "media_elapsed_time".toId()
        media_total_time = "media_total_time".toId()
        media_control_bg_radius = "media_control_bg_radius".toDimenId()
        tiny_media_session_view = "tiny_media_session_view".toId()
        normal_control_center_carrier_view = "normal_control_center_carrier_view".toId()
        normal_control_center_carrier_second_view = "normal_control_center_carrier_second_view".toId()
        normal_control_center_carrier_vertical_separator = "normal_control_center_carrier_vertical_separator".toId()
        lock_screen_carrier_airplane_mode_on = "lock_screen_carrier_airplane_mode_on".toStringId()
        kept_notifications_on_keyguard = "kept_notifications_on_keyguard".toBoolId()
    }
}