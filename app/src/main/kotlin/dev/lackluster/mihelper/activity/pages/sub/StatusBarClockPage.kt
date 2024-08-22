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
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage(Pages.STATUS_BAR_CLOCK, hideMenu = false)
class StatusBarClockPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_status_bar_clock)
    }
    override fun onCreate() {
        val clockGeekBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK, false)
        }) { view, flag, data ->
            if (flag == 0) {
                view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            } else {
                view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val clockPaddingBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_clock_general),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.clock_general_custom_layout),
                SwitchData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM,
                    dataBindingSend = clockPaddingBinding.bindingSend
                )
            )
            EditTextPreference(
                DescData(titleId = R.string.clock_general_padding_left),
                EditTextData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT,
                    valueType = EditTextData.ValueType.INT,
                    defValue = 0,
                    hintText = "0",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.clock_general_custom_layout,
                            summaryId = R.string.clock_general_padding_left
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
            )
            EditTextPreference(
                DescData(titleId = R.string.clock_general_padding_right),
                EditTextData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT,
                    valueType = EditTextData.ValueType.INT,
                    defValue = 0,
                    hintText = "0",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.clock_general_custom_layout,
                            summaryId = R.string.clock_general_padding_right
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.clock_general_geek),
                SwitchData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK,
                    dataBindingSend = clockGeekBinding.bindingSend
                )
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_clock_geek_mode),
            CategoryData(),
            dataBindingRecv = clockGeekBinding.binding.getRecv(0)
        ) {
            EditTextPreference(
                DescData(titleId = R.string.clock_geek_time_format_pattern),
                EditTextData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT,
                    hintText = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT,
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.clock_geek_time_format_pattern
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = clockGeekBinding.binding.getRecv(0)
            )
            if (Device.isPad) {
                EditTextPreference(
                    DescData(titleId = R.string.clock_geek_time_format_pattern_pad),
                    EditTextData(
                        key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD,
                        valueType = EditTextData.ValueType.STRING,
                        defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD,
                        hintText = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD,
                        dialogData = DialogData(
                            DescData(
                                titleId = R.string.clock_geek_time_format_pattern_pad
                            )
                        ),
                        isValueValid = { _ ->
                            true
                        }
                    ),
                    dataBindingRecv = clockGeekBinding.binding.getRecv(0)
                )
            }
            EditTextPreference(
                DescData(titleId = R.string.clock_geek_time_format_pattern_horizon),
                EditTextData(
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON,
                    hintText = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON,
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.clock_geek_time_format_pattern_horizon
                        )
                    ),
                    isValueValid = { _ ->
                        true
                    }
                ),
                dataBindingRecv = clockGeekBinding.binding.getRecv(0)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_clock_easy),
            CategoryData(),
            dataBindingRecv = clockGeekBinding.binding.getRecv(1)
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.clock_easy_show_ampm,
                    summaryId = R.string.clock_easy_show_ampm_tips
                ),
                SwitchData(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM),
                dataBindingRecv = clockGeekBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.clock_easy_show_leading_zero,
                    summaryId = R.string.clock_easy_show_leading_zero_tips
                ),
                SwitchData(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO),
                dataBindingRecv = clockGeekBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.clock_easy_show_seconds,
                    summaryId = R.string.clock_easy_show_seconds_tips
                ),
                SwitchData(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS),
                dataBindingRecv = clockGeekBinding.binding.getRecv(1)
            )
        }
    }
}