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
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner

@BMPage("page_systemui_icon_tuner")
class IconTunerPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_status_bar_icon_tuner)
    }
    override fun onCreate() {
        val hideIconMode: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.icon_tuner_hide_selection_default)
            it[1] = getString(R.string.icon_tuner_hide_selection_show_all)
            it[2] = getString(R.string.icon_tuner_hide_selection_show_statusbar)
            it[3] = getString(R.string.icon_tuner_hide_selection_show_qs)
            it[4] = getString(R.string.icon_tuner_hide_selection_hidden)
        }
        TitleText(textId = R.string.ui_title_icon_tuner_mobile)
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_sim_one),
            SwitchV(IconTurner.HIDE_SIM_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_sim_two),
            SwitchV(IconTurner.HIDE_SIM_TWO)
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_mobile_no_sim),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.NO_SIM,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NO_SIM, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NO_SIM, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NO_SIM, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NO_SIM, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NO_SIM, 4)
                }
            }
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_mobile_activity),
            SwitchV(IconTurner.HIDE_MOBILE_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_mobile_type),
            SwitchV(IconTurner.HIDE_MOBILE_TYPE)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_hd_small),
            SwitchV(IconTurner.HIDE_HD_SMALL)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_hd_large),
            SwitchV(IconTurner.HIDE_HD_LARGE)
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_mobile_hd_new),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.HD_NEW,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HD_NEW, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HD_NEW, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HD_NEW, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HD_NEW, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HD_NEW, 4)
                }
            }
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_mobile_hide_hd_no_service),
            SwitchV(IconTurner.HIDE_HD_NO_SERVICE)
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_wifi)
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_wifi_wifi),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.WIFI,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIFI, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIFI, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIFI, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIFI, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIFI, 4)
                }
            }
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_wifi_hide_wifi_activity),
            SwitchV(IconTurner.HIDE_WIFI_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_wifi_hide_wifi_type),
            SwitchV(IconTurner.HIDE_WIFI_TYPE)
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_wifi_hotspot),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.HOTSPOT,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HOTSPOT, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HOTSPOT, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HOTSPOT, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HOTSPOT, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HOTSPOT, 4)
                }
            }
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_battery)
        val batteryIndicatorStyle: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.icon_tuner_battery_style_default)
            it[1] = getString(R.string.icon_tuner_battery_style_both)
            it[2] = getString(R.string.icon_tuner_battery_style_icon)
            it[3] = getString(R.string.icon_tuner_battery_style_percentage)
            it[4] = getString(R.string.icon_tuner_battery_style_hidden)
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
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_battery_style),
            SpinnerV(
                batteryIndicatorStyle[MIUIActivity.safeSP.getInt(
                    IconTurner.BATTERY_STYLE,
                    0
                )].toString()
            ) {
                add(batteryIndicatorStyle[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_STYLE, 0)
                    batteryStyleBinding.bindingSend.send(0)
                }
                add(batteryIndicatorStyle[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_STYLE, 1)
                    batteryStyleBinding.bindingSend.send(1)
                }
                add(batteryIndicatorStyle[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_STYLE, 2)
                    batteryStyleBinding.bindingSend.send(2)
                }
                add(batteryIndicatorStyle[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_STYLE, 3)
                    batteryStyleBinding.bindingSend.send(3)
                }
                add(batteryIndicatorStyle[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_STYLE, 4)
                    batteryStyleBinding.bindingSend.send(4)
                }
            }
        )
        val batteryPercentageStyle: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.icon_tuner_battery_percentage_symbol_style_default)
            it[1] = getString(R.string.icon_tuner_battery_percentage_symbol_style_uni)
            it[2] = getString(R.string.icon_tuner_battery_percentage_symbol_style_hidden)
        }
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_battery_percentage_symbol_style),
            SpinnerV(
                batteryPercentageStyle[MIUIActivity.safeSP.getInt(
                    IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE,
                    0
                )].toString()
            ) {
                add(batteryPercentageStyle[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, 0)
                }
                add(batteryPercentageStyle[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, 1)
                }
                add(batteryPercentageStyle[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, 2)
                }
            },
            dataBindingRecv = batteryStyleBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.icon_tuner_battery_swap_battery_percent),
            SwitchV(IconTurner.SWAP_BATTERY_PERCENT),
            dataBindingRecv = batteryStyleBinding.binding.getRecv(0)
        )
        TextWithSwitch(
            TextV(textId = R.string.icon_tuner_battery_hide_charge),
            SwitchV(IconTurner.HIDE_CHARGE)
        )
        val batteryPercentSizeBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.icon_tuner_battery_battery_percent_size),
            SwitchV(
                key = IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE,
                dataBindingSend = batteryPercentSizeBinding.bindingSend)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.icon_tuner_battery_percent_size,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.icon_tuner_battery_percent_size)
                        setMessage(
                            "${activity.getString(R.string.common_default)}: 0.0\n${activity.getString(R.string.icon_tuner_battery_percent_size_default)}"
                        )
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getFloat(IconTurner.BATTERY_PERCENTAGE_TEXT_SIZE, 0f)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        IconTurner.BATTERY_PERCENTAGE_TEXT_SIZE,
                                        getEditText().toFloat()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPercentSizeBinding.binding.getRecv(1)
        )
//        val swapBatteryBinding = GetDataBinding({
//            MIUIActivity.safeSP.getBoolean(IconTurner.HIDE_BATTERY, false)
//        }) { view, _, data ->
//            view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
//        }
//        val batteryPercentMarkBinding = GetDataBinding({
//            MIUIActivity.safeSP.getBoolean(IconTurner.HIDE_BATTERY_PERCENT_SYMBOL, false)
//        }) { view, _, data ->
//            view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
//        }
//        TextWithSwitch(
//            TextV(textId = R.string.icon_tuner_battery_hide_battery),
//            SwitchV(
//                key = IconTurner.HIDE_BATTERY,
//                dataBindingSend = swapBatteryBinding.bindingSend)
//        )
//        TextWithSwitch(
//            TextV(textId = R.string.icon_tuner_battery_hide_battery_mark),
//            SwitchV(
//                key = IconTurner.HIDE_BATTERY_PERCENT_SYMBOL,
//                dataBindingSend = batteryPercentMarkBinding.bindingSend)
//        )
        val batteryPaddingBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(IconTurner.BATTERY_MODIFY_PADDING, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.icon_tuner_battery_layout_custom),
            SwitchV(
                key = IconTurner.BATTERY_MODIFY_PADDING,
                dataBindingSend = batteryPaddingBinding.bindingSend)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.icon_tuner_battery_padding_left,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.icon_tuner_battery_padding_left)
                        setMessage("${activity.getString(R.string.common_default)}: 0.0")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getFloat(IconTurner.BATTERY_PADDING_LEFT, 0.0f)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        IconTurner.BATTERY_PADDING_LEFT,
                                        getEditText().toFloat()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.icon_tuner_battery_padding_right,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.icon_tuner_battery_padding_right)
                        setMessage("${activity.getString(R.string.common_default)}: 0.0")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getFloat(IconTurner.BATTERY_PADDING_RIGHT, 0.0f)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        IconTurner.BATTERY_PADDING_RIGHT,
                                        getEditText().toFloat()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
        )
//        TextSummaryWithSwitch(
//            TextSummaryV(textId = R.string.icon_tuner_battery_uni_battery_mark),
//            SwitchV(IconTurner.CHANGE_BATTERY_PERCENT_SYMBOL),
//            dataBindingRecv = batteryPercentMarkBinding.binding.getRecv(0)
//        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_connectivity)
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_flight_mode),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.FLIGHT_MODE,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.FLIGHT_MODE, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.FLIGHT_MODE, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.FLIGHT_MODE, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.FLIGHT_MODE, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.FLIGHT_MODE, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_gps),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.GPS,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.GPS, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.GPS, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.GPS, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.GPS, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.GPS, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_bluetooth),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.BLUETOOTH,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_bluetooth_battery),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.BLUETOOTH_BATTERY,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH_BATTERY, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH_BATTERY, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH_BATTERY, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH_BATTERY, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.BLUETOOTH_BATTERY, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_nfc),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.NFC,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NFC, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NFC, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NFC, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NFC, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NFC, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_vpn),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.VPN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VPN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VPN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VPN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VPN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VPN, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_connect_net_speed),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.NET_SPEED,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NET_SPEED, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NET_SPEED, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NET_SPEED, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NET_SPEED, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.NET_SPEED, 4)
                }
            }
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_device)
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_car),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.CAR,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.CAR, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.CAR, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.CAR, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.CAR, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.CAR, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_pad),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.PAD,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PAD, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PAD, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PAD, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PAD, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PAD, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_pc),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.PC,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PC, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PC, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PC, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PC, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PC, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_phone),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.PHONE,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PHONE, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PHONE, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PHONE, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PHONE, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.PHONE, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_sound_box),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.SOUND_BOX,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_sound_box_group),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.SOUND_BOX_GROUP,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_GROUP, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_GROUP, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_GROUP, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_GROUP, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_GROUP, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_sound_box_screen),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.SOUND_BOX_SCREEN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_SCREEN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_SCREEN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_SCREEN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_SCREEN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.SOUND_BOX_SCREEN, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_stereo),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.STEREO,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.STEREO, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.STEREO, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.STEREO, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.STEREO, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.STEREO, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_tv),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.TV,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.TV, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.TV, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.TV, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.TV, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.TV, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_device_wireless_headset),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.WIRELESS_HEADSET,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIRELESS_HEADSET, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIRELESS_HEADSET, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIRELESS_HEADSET, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIRELESS_HEADSET, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.WIRELESS_HEADSET, 4)
                }
            }
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_other)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.icon_tuner_other_swap_mobile_wifi),
            SwitchV(IconTurner.SWAP_MOBILE_WIFI)
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_other_alarm),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.ALARM,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ALARM, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ALARM, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ALARM, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ALARM, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ALARM, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_other_headset),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.HEADSET,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HEADSET, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HEADSET, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HEADSET, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HEADSET, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.HEADSET, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_other_volume),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.VOLUME,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VOLUME, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VOLUME, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VOLUME, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VOLUME, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.VOLUME, 4)
                }
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.icon_tuner_other_zen),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    IconTurner.ZEN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ZEN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ZEN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ZEN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ZEN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(IconTurner.ZEN, 4)
                }
            }
        )
    }
}