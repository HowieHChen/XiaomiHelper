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
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage("page_systemui_clock")
class StatusBarClockPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_status_bar_clock)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_clock_general)
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
        TextWithSwitch(
            TextV(textId = R.string.clock_general_custom_layout),
            SwitchV(
                key = Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM,
                dataBindingSend = clockPaddingBinding.bindingSend
            )
        )
        TextWithArrow(
            TextV(
                textId = R.string.clock_general_padding_left,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.clock_general_padding_left)
                        setMessage("${activity.getString(R.string.common_default)}: 0")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getInt(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT,
                                        getEditText().toInt()
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
            dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
        )
        TextWithArrow(
            TextV(
                textId = R.string.clock_general_padding_right,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.clock_general_padding_right)
                        setMessage("${activity.getString(R.string.common_default)}: 0")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getInt(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT,
                                        getEditText().toInt()
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
            dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
        )
        TextWithSwitch(
            TextV(textId = R.string.clock_general_geek),
            SwitchV(
                key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK,
                dataBindingSend = clockGeekBinding.bindingSend
            )
        )
        Line()
        TitleText(
            textId = R.string.ui_title_clock_geek_mode,
            dataBindingRecv = clockGeekBinding.binding.getRecv(0)
        )
        TextWithArrow(
            TextV(
                textId = R.string.clock_geek_time_format_pattern,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.clock_geek_time_format_pattern)
                        setMessage("${activity.getString(R.string.common_default)}: ${Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT}")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT,
                                        getEditText()
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
            dataBindingRecv = clockGeekBinding.binding.getRecv(0)
        )
        if (Device.isPad) {
            TextWithArrow(
                TextV(
                    textId = R.string.clock_geek_time_format_pattern_pad,
                    onClickListener = {
                        MIUIDialog(activity) {
                            setTitle(R.string.clock_geek_time_format_pattern_pad)
                            setMessage("${activity.getString(R.string.common_default)}: ${Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD}")
                            setEditText("", "${activity.getString(R.string.common_current)}: ${
                                MIUIActivity.safeSP.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD)
                            }")
                            setLButton(textId = R.string.button_cancel) {
                                dismiss()
                            }
                            setRButton(textId = R.string.button_ok) {
                                if (getEditText().isNotEmpty()) {
                                    runCatching {
                                        MIUIActivity.safeSP.putAny(
                                            Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD,
                                            getEditText()
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
                dataBindingRecv = clockGeekBinding.binding.getRecv(0)
            )
        }
        TextWithArrow(
            TextV(
                textId = R.string.clock_geek_time_format_pattern_horizon,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.clock_geek_time_format_pattern_horizon)
                        setMessage("${activity.getString(R.string.common_default)}: ${Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON}")
                        setEditText("", "${activity.getString(R.string.common_current)}: ${
                            MIUIActivity.safeSP.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON,
                                        getEditText()
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
            dataBindingRecv = clockGeekBinding.binding.getRecv(0)
        )
        TitleText(
            textId = R.string.ui_title_clock_easy,
            dataBindingRecv = clockGeekBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.clock_easy_show_ampm,
                tipsId = R.string.clock_easy_show_ampm_tips
            ),
            SwitchV(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM),
            dataBindingRecv = clockGeekBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.clock_easy_show_leading_zero,
                tipsId = R.string.clock_easy_show_leading_zero_tips
            ),
            SwitchV(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO),
            dataBindingRecv = clockGeekBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.clock_easy_show_seconds,
                tipsId = R.string.clock_easy_show_seconds_tips
            ),
            SwitchV(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS),
            dataBindingRecv = clockGeekBinding.binding.getRecv(1)
        )
    }
}