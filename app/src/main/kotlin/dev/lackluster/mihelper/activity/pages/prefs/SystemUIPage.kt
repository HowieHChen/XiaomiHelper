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

package dev.lackluster.mihelper.activity.pages.prefs

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.SeekBarData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref

@BMPage(Pages.SYSTEM_UI, hideMenu = false)
class SystemUIPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_systemui)
    }

    override fun onCreate() {
        val notificationCountBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_systemui_status_bar),
            CategoryData(hideLine = true)
        ) {
            TextPreference(
                DescData(titleId = R.string.systemui_statusbar_clock),
                TextData(),
                onClickListener = {
                    showFragment(Pages.STATUS_BAR_CLOCK)
                }
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_statusbar_notif_count),
                SwitchData(
                    key = Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT,
                    dataBindingSend = notificationCountBinding.bindingSend
                )
            )
            SeekBarPreference(
                DescData(titleId = R.string.systemui_statusbar_notif_count_icon),
                SeekBarData(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT_ICON, 0, 15, 3, true),
                dataBindingRecv = notificationCountBinding.binding.getRecv(1)
            )
            TextPreference(
                DescData(titleId = R.string.systemui_statusbar_icon),
                TextData(),
                onClickListener = {
                    showFragment(Pages.ICON_TUNER)
                }
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_statusbar_tap_to_sleep),
                SwitchData(Pref.Key.SystemUI.StatusBar.DOUBLE_TAP_TO_SLEEP)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_systemui_lock_screen),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.systemui_lock_hide_unlock_tip),
                SwitchData(Pref.Key.SystemUI.LockScreen.HIDE_UNLOCK_TIP)
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_lock_hide_disturb),
                SwitchData(Pref.Key.SystemUI.LockScreen.HIDE_DISTURB)
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_lock_double_tap),
                SwitchData(Pref.Key.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.systemui_lock_block_editor,
                    summaryId = R.string.systemui_lock_block_editor_tips
                ),
                SwitchData(Pref.Key.SystemUI.LockScreen.BLOCK_EDITOR)
            )
            DropDownPreference(
                DescData(titleId = R.string.systemui_lock_carrier_text),
                DropDownData(
                    key = Pref.Key.SystemUI.LockScreen.CARRIER_TEXT,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.systemui_lock_carrier_text_default), 0),
                        DropDownData.SpinnerItemData(getString(R.string.systemui_lock_carrier_text_carrier), 1),
                        DropDownData.SpinnerItemData(getString(R.string.systemui_lock_carrier_text_clock), 2)
                    )
                )
            )
        }
        val notificationRedirectBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_SETTINGS, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_systemui_notification_center),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.systemui_notif_redirect_settings,
                    summaryId = R.string.systemui_notif_redirect_settings_tips
                ),
                SwitchData(
                    key = Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_SETTINGS,
                    dataBindingSend = notificationRedirectBinding.bindingSend
                )
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.systemui_notif_redirect_dialog,
                    summaryId = R.string.systemui_notif_redirect_dialog_tips
                ),
                SwitchData(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_DIALOG),
                dataBindingRecv = notificationRedirectBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_notif_disable_whitelist),
                SwitchData(Pref.Key.SystemUI.NotifCenter.NOTIF_NO_WHITELIST)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.systemui_notif_freeform,
                    summaryId = R.string.systemui_notif_freeform_tips
                ),
                SwitchData(Pref.Key.SystemUI.NotifCenter.NOTIF_FREEFORM)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.systemui_notif_clock_color_fix,
                    summaryId = R.string.systemui_notif_clock_color_fix_tips
                ),
                SwitchData(Pref.Key.SystemUI.NotifCenter.CLOCK_COLOR_FIX)
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_notif_theme_blur),
                SwitchData(Pref.Key.SystemUI.NotifCenter.ADVANCED_TEXTURE)
            )
            TextPreference(
                DescData(titleId = R.string.systemui_notif_media_control_style),
                TextData(
                    valueAdapter = {
                        val style = MIUIActivity.safeSP.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
                        when (style) {
                            1 -> getString(R.string.media_style_custom_enhance)
                            2 -> getString(R.string.media_style_custom_texture)
                            3 -> getString(R.string.media_style_custom_blur)
                            4 -> getString(R.string.media_style_custom_android_new)
                            5 -> getString(R.string.media_style_custom_android_old)
                            else -> getString(R.string.media_style_custom_default)
                        }
                    }
                ),
                onClickListener = {
                    showFragment(Pages.MEDIA_CONTROL)
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_systemui_control_center),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.systemui_control_hide_carrier_one),
                SwitchData(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_ONE)
            )
            SwitchPreference(
                DescData(titleId = R.string.systemui_control_hide_carrier_two),
                SwitchData(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_TWO)
            )
        }
    }
}