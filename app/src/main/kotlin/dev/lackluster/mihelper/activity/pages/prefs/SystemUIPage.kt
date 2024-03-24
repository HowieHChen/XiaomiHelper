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
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref

@BMPage("page_systemui", hideMenu = false)
class SystemUIPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_systemui)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_systemui_status_bar)
        TextWithArrow(
            TextV(
                textId = R.string.systemui_statusbar_clock,
                onClickListener = { showFragment("page_systemui_clock") }
            )
        )
        val notificationCountBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_statusbar_notif_count),
            SwitchV(
                key = Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT,
                dataBindingSend = notificationCountBinding.bindingSend
            )
        )
        TextWithSeekBar(
            TextV(textId = R.string.systemui_statusbar_notif_count_icon),
            SeekBarWithTextV(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT_ICON, 0, 15, 3),
            dataBindingRecv = notificationCountBinding.binding.getRecv(1)
        )
        TextWithArrow(
            TextV(
                textId = R.string.systemui_statusbar_icon,
                onClickListener = { showFragment("page_systemui_icon_tuner") }
            )
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_statusbar_tap_to_sleep),
            SwitchV(Pref.Key.SystemUI.StatusBar.DOUBLE_TAP_TO_SLEEP)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_lock_screen)
        TextWithSwitch(
            TextV(textId = R.string.systemui_lock_hide_unlock_tip),
            SwitchV(Pref.Key.SystemUI.LockScreen.HIDE_UNLOCK_TIP)
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_lock_hide_disturb),
            SwitchV(Pref.Key.SystemUI.LockScreen.HIDE_DISTURB)
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_lock_double_tap),
            SwitchV(Pref.Key.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_lock_block_editor,
                tipsId = R.string.systemui_lock_block_editor_tips
            ),
            SwitchV(Pref.Key.SystemUI.LockScreen.BLOCK_EDITOR)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_notification_center)
        val notificationRedirectBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_SETTINGS, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_settings,
                tipsId = R.string.systemui_notif_redirect_settings_tips
            ),
            SwitchV(
                key = Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_SETTINGS,
                dataBindingSend = notificationRedirectBinding.bindingSend
            )
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_dialog,
                tipsId = R.string.systemui_notif_redirect_dialog_tips
            ),
            SwitchV(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_DIALOG),
            dataBindingRecv = notificationRedirectBinding.binding.getRecv(1)
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_notif_disable_whitelist),
            SwitchV(Pref.Key.SystemUI.NotifCenter.NOTIF_NO_WHITELIST)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_freeform,
                tipsId = R.string.systemui_notif_freeform_tips
            ),
            SwitchV(Pref.Key.SystemUI.NotifCenter.NOTIF_FREEFORM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_clock_color_fix,
                tipsId = R.string.systemui_notif_clock_color_fix_tips
            ),
            SwitchV(Pref.Key.SystemUI.NotifCenter.CLOCK_COLOR_FIX)
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_notif_theme_blur),
            SwitchV(Pref.Key.SystemUI.NotifCenter.ADVANCED_TEXTURE)
        )
        TextWithArrow(
            TextV(
                textId = R.string.systemui_notif_media_control_style,
                onClickListener = { showFragment("page_media_control") }
            )
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_control_center)
        TextWithSwitch(
            TextV(textId = R.string.systemui_control_hide_carrier_one),
            SwitchV(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.systemui_control_hide_carrier_two),
            SwitchV(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_TWO)
        )
//        Line()
//        TitleText(textId = R.string.ui_title_systemui_others)
    }
}