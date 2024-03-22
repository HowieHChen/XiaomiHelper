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
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("page_miui_home", hideMenu = false)
class MiuiHomePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_miui_home)
    }

    override fun onCreate() {
        val refactorBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TitleText(textId = R.string.ui_title_home_exclusive)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_exclusive_refactor,
                tipsId = R.string.home_exclusive_refactor_tips,
                onClickListener = {
                    if (MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)) {
                        showFragment("home_refactor")
                    }
                    else {
                        MIUIDialog(activity) {
                            setTitle(R.string.home_exclusive_refactor_dialog_title)
                            setMessage(R.string.home_exclusive_refactor_dialog_msg)
                            setLButton(textId = R.string.button_cancel) {
                                dismiss()
                            }
                            setRButton(textId = R.string.button_ok) {
                                showFragment("home_refactor")
                                dismiss()
                            }
                        }.show()
                    }
                }
            )
        )
        Line()
        TitleText(textId = R.string.ui_title_home_gesture)

        Line()
        TitleText(textId = R.string.ui_title_home_anim)

        Line()
        TitleText(textId = R.string.ui_title_home_folder)

        Line()
        TitleText(textId = R.string.ui_title_home_icon)
        TextWithSwitch(
            TextV(textId = R.string.home_icon_unblock_google),
            SwitchV(Pref.Key.MiuiHome.ICON_UNBLOCK_GOOGLE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_perfect_icon,
                tipsId = R.string.home_icon_perfect_icon_tips
            ),
            SwitchV(Pref.Key.MiuiHome.ICON_PERFECT)
        )
        if (!Device.isPad) {
            TextSummaryWithSwitch(
                TextSummaryV(
                    textId = R.string.home_icon_corner4large,
                    tipsId = R.string.home_icon_corner4large_tips
                ),
                SwitchV(Pref.Key.MiuiHome.ICON_CORNER4LARGE)
            )
        }
        Line()
        TitleText(textId = R.string.ui_title_home_recent)
        if (Device.isPad) {
            TextWithSwitch(
                TextV(textId = R.string.home_recent_pad_show_memory),
                SwitchV(Pref.Key.MiuiHome.PAD_RECENT_SHOW_MEMORY)
            )
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.home_recent_show_real_memory),
            SwitchV(Pref.Key.MiuiHome.RECENT_SHOW_REAL_MEMORY)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_wallpaper_darken,
                tipsId = R.string.home_recent_wallpaper_darken_tips
            ),
            SwitchV(Pref.Key.MiuiHome.RECENT_WALLPAPER_DARKEN),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        TextWithSwitch(
            TextV(textId = R.string.home_recent_dismiss_anim),
            SwitchV(Pref.Key.MiuiHome.RECENT_CARD_ANIM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_disable_fake_navbar,
                tipsId = R.string.home_recent_disable_fake_navbar_tips
            ),
            SwitchV(Pref.Key.MiuiHome.RECENT_DISABLE_FAKE_NAVBAR)
        )
        if (Device.isPad) {
            TextSummaryWithArrow(
                TextSummaryV(
                    textId = R.string.home_recent_dock_time,
                    tipsId = R.string.home_recent_dock_time_tips,
                    onClickListener = {
                        MIUIDialog(activity) {
                            setTitle(R.string.home_recent_dock_time)
                            setMessage(
                                "${activity.getString(R.string.common_default)}: 180 (ms), ${activity.getString(R.string.dialog_current_value)}: ${
                                    MIUIActivity.safeSP.getInt(Pref.Key.MiuiHome.PAD_DOCK_TIME_DURATION, 180)
                                } (ms)"
                            )
                            setEditText("", "${
                                MIUIActivity.safeSP.getInt(Pref.Key.MiuiHome.PAD_DOCK_TIME_DURATION, 180).takeIf { it != 180 } ?: ""
                            }")
                            setLButton(textId = R.string.button_cancel) {
                                dismiss()
                            }
                            setRButton(textId = R.string.button_ok) {
                                if (getEditText().isNotEmpty()) {
                                    runCatching {
                                        MIUIActivity.safeSP.putAny(
                                            Pref.Key.MiuiHome.PAD_DOCK_TIME_DURATION,
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
                )
            )
            TextSummaryWithArrow(
                TextSummaryV(
                    textId = R.string.home_recent_docke_safe_height,
                    tipsId = R.string.home_recent_docke_safe_height_tips,
                    onClickListener = {
                        MIUIDialog(activity) {
                            setTitle(R.string.home_recent_docke_safe_height)
                            setMessage(
                                "${activity.getString(R.string.common_default)}: 300, ${activity.getString(R.string.dialog_current_value)}: ${
                                    MIUIActivity.safeSP.getInt(Pref.Key.MiuiHome.PAD_DOCK_SAFE_AREA_HEIGHT, 300)
                                }"
                            )
                            setEditText("", "${
                                MIUIActivity.safeSP.getInt(Pref.Key.MiuiHome.PAD_DOCK_SAFE_AREA_HEIGHT, 300).takeIf { it != 180 } ?: ""
                            }")
                            setLButton(textId = R.string.button_cancel) {
                                dismiss()
                            }
                            setRButton(textId = R.string.button_ok) {
                                if (getEditText().isNotEmpty()) {
                                    runCatching {
                                        MIUIActivity.safeSP.putAny(
                                            Pref.Key.MiuiHome.PAD_DOCK_SAFE_AREA_HEIGHT,
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
                )
            )
        }
        Line()
        TitleText(textId = R.string.ui_title_home_widget)
        TextWithSwitch(
            TextV(textId = R.string.home_widget_anim),
            SwitchV(Pref.Key.MiuiHome.WIDGET_ANIM)
        )
        TextWithSwitch(
            TextV(textId = R.string.home_widget_resizable),
            SwitchV(Pref.Key.MiuiHome.WIDGET_RESIZABLE)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_personal_asist)

        Line()
        TitleText(textId = R.string.ui_title_home_shortcut)

        Line()
        TitleText(textId = R.string.ui_title_home_others)
        TextWithSwitch(
            TextV(
                textId = R.string.home_others_always_show_clock,
            ),
            SwitchV(Pref.Key.MiuiHome.ALWAYS_SHOW_TIME)
        )
    }
}