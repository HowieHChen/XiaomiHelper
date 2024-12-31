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

package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner

@BMPage(Pages.ICON_TUNER, hideMenu = false)
class IconTunerPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_status_bar_icon_tuner)
    }
    override fun onCreate() {
        val hideIconEntries = arrayOf(
            DropDownData.SpinnerItemData(getString(R.string.icon_tuner_hide_selection_default), 0),
            DropDownData.SpinnerItemData(getString(R.string.icon_tuner_hide_selection_show_all), 1),
            DropDownData.SpinnerItemData(getString(R.string.icon_tuner_hide_selection_show_statusbar), 2),
            DropDownData.SpinnerItemData(getString(R.string.icon_tuner_hide_selection_show_qs), 3),
            DropDownData.SpinnerItemData(getString(R.string.icon_tuner_hide_selection_hidden), 4),
        )
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_mobile),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_sim_one),
                SwitchData(IconTurner.HIDE_SIM_ONE)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_sim_two),
                SwitchData(IconTurner.HIDE_SIM_TWO)
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_mobile_no_sim),
                DropDownData(
                    key = IconTurner.NO_SIM,
                    entries = hideIconEntries
                )
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_mobile_activity),
                SwitchData(IconTurner.HIDE_MOBILE_ACTIVITY)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_mobile_type),
                SwitchData(IconTurner.HIDE_MOBILE_TYPE)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_hd_small),
                SwitchData(IconTurner.HIDE_HD_SMALL)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_hd_large),
                SwitchData(IconTurner.HIDE_HD_LARGE)
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hd_new),
                DropDownData(
                    key = IconTurner.HD_NEW,
                    entries = hideIconEntries
                )
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_mobile_hide_hd_no_service),
                SwitchData(IconTurner.HIDE_HD_NO_SERVICE)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_wifi),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_wifi_wifi),
                DropDownData(
                    key = IconTurner.WIFI,
                    entries = hideIconEntries
                )
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_wifi_hide_wifi_activity),
                SwitchData(IconTurner.HIDE_WIFI_ACTIVITY)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_wifi_hide_wifi_type),
                SwitchData(IconTurner.HIDE_WIFI_STANDARD)
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_wifi_hotspot),
                DropDownData(
                    key = IconTurner.HOTSPOT,
                    entries = hideIconEntries
                )
            )
        }
        val batteryStyleBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(IconTurner.BATTERY_STYLE, 0)
        }) { view: View, flag: Int, data: Any ->
            val selectionIndex = data as Int
            when (flag) {
                0 -> { // Battery percentage symbol
                    view.visibility = if (selectionIndex == 2 || selectionIndex == 4) View.GONE else View.VISIBLE
                }
            }
        }
        val batteryPercentSizeBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        val batteryPaddingBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(IconTurner.BATTERY_MODIFY_PADDING, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_battery),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_battery_style),
                DropDownData(
                    key = IconTurner.BATTERY_STYLE,
                    mode = 0,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_style_default), 0, R.drawable.ic_battery_style_default),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_style_both), 1, R.drawable.ic_battery_style_both),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_style_icon), 2, R.drawable.ic_battery_style_icon),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_style_percentage), 3, R.drawable.ic_battery_style_digit),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_style_hidden), 4, R.drawable.ic_battery_style_hidden)
                    ),
                    dataBindingSend = batteryStyleBinding.bindingSend
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_battery_percentage_symbol_style),
                DropDownData(
                    key = IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE,
                    mode = 0,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_percentage_symbol_style_default), 0, R.drawable.ic_battery_percentage_style_default),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_percentage_symbol_style_uni), 1, R.drawable.ic_battery_percentage_style_digit),
                        DropDownData.SpinnerItemData(getString(R.string.icon_tuner_battery_percentage_symbol_style_hidden), 2, R.drawable.ic_battery_percentage_style_hidden)
                    )
                ),
                dataBindingRecv = batteryStyleBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_battery_swap_battery_percent),
                SwitchData(IconTurner.SWAP_BATTERY_PERCENT),
                dataBindingRecv = batteryStyleBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_battery_hide_charge),
                SwitchData(IconTurner.HIDE_CHARGE)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_battery_battery_percent_size),
                SwitchData(
                    key = IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE,
                    dataBindingSend = batteryPercentSizeBinding.bindingSend)
            )
            EditTextPreference(
                DescData(titleId = R.string.icon_tuner_battery_percent_size),
                EditTextData(
                    key = IconTurner.BATTERY_PERCENTAGE_TEXT_SIZE,
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = 0.0f,
                    hintText = "0.0",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.icon_tuner_battery_percent_size
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = batteryPercentSizeBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_battery_layout_custom),
                SwitchData(
                    key = IconTurner.BATTERY_MODIFY_PADDING,
                    dataBindingSend = batteryPaddingBinding.bindingSend)
            )
            EditTextPreference(
                DescData(titleId = R.string.icon_tuner_battery_padding_left),
                EditTextData(
                    key = IconTurner.BATTERY_PADDING_LEFT,
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = 0.0f,
                    hintText = "0.0",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.icon_tuner_battery_layout_custom,
                            summaryId = R.string.icon_tuner_battery_padding_left
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
            )
            EditTextPreference(
                DescData(titleId = R.string.icon_tuner_battery_padding_right),
                EditTextData(
                    key = IconTurner.BATTERY_PADDING_RIGHT,
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = 0.0f,
                    hintText = "0.0",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.icon_tuner_battery_layout_custom,
                            summaryId = R.string.icon_tuner_battery_padding_right
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_connectivity),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_flight_mode),
                DropDownData(
                    key = IconTurner.FLIGHT_MODE,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_gps),
                DropDownData(
                    key = IconTurner.GPS,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_bluetooth),
                DropDownData(
                    key = IconTurner.BLUETOOTH,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_bluetooth_battery),
                DropDownData(
                    key = IconTurner.BLUETOOTH_BATTERY,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_nfc),
                DropDownData(
                    key = IconTurner.NFC,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_vpn),
                DropDownData(
                    key = IconTurner.VPN,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_connect_net_speed),
                DropDownData(
                    key = IconTurner.NET_SPEED,
                    entries = hideIconEntries
                )
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_device),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_car),
                DropDownData(
                    key = IconTurner.CAR,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_pad),
                DropDownData(
                    key = IconTurner.PAD,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_pc),
                DropDownData(
                    key = IconTurner.PC,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_phone),
                DropDownData(
                    key = IconTurner.PHONE,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_sound_box),
                DropDownData(
                    key = IconTurner.SOUND_BOX,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_sound_box_group),
                DropDownData(
                    key = IconTurner.SOUND_BOX_GROUP,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_sound_box_screen),
                DropDownData(
                    key = IconTurner.SOUND_BOX_SCREEN,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_stereo),
                DropDownData(
                    key = IconTurner.STEREO,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_tv),
                DropDownData(
                    key = IconTurner.TV,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_device_wireless_headset),
                DropDownData(
                    key = IconTurner.WIRELESS_HEADSET,
                    entries = hideIconEntries
                )
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_icon_tuner_other),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.icon_tuner_other_swap_mobile_wifi),
                SwitchData(IconTurner.SWAP_MOBILE_WIFI)
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_other_alarm),
                DropDownData(
                    key = IconTurner.ALARM,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_other_headset),
                DropDownData(
                    key = IconTurner.HEADSET,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_other_volume),
                DropDownData(
                    key = IconTurner.VOLUME,
                    entries = hideIconEntries
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.icon_tuner_other_zen),
                DropDownData(
                    key = IconTurner.ZEN,
                    entries = hideIconEntries
                )
            )
        }
    }
}