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

package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_miui_home", hideMenu = false)
class MiuiHomePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.scope_miui_home)
    }
    override fun onCreate() {
        val isPadBinding = GetDataBinding({
            Device.isPad
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TitleText(textId = R.string.ui_title_home_behavior)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_always_show_clock,
            ),
            SwitchV(PrefKey.HOME_ALWAYS_SHOW_TIME)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_double_tap,
            ),
            SwitchV(PrefKey.HOME_DOUBLE_TAP_TO_SLEEP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_enable4pad,
            ),
            SwitchV(PrefKey.HOME_PAD_ALL_FEATURE),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_fake_premium,
                tipsId = R.string.home_behavior_fake_premium_tips
            ),
            SwitchV(PrefKey.HOME_FAKE_PREMIUM)
        )
        val refactorBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val blurBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.HOME_BLUR_ALL, false) &&
                    !MIUIActivity.safeSP.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_blur_advance,
                tipsId = R.string.home_behavior_blur_advance_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_ADVANCE),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_all_blur,
                tipsId = R.string.home_behavior_all_blur_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_ALL, dataBindingSend = blurBinding.bindingSend),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        if (Device.isPad) {
            TextSummaryWithSwitch(
                TextSummaryV(
                    textId = R.string.home_behavior_all_blur_enhance,
                    tipsId = R.string.home_behavior_all_blur_enhance_tips
                ),
                SwitchV(PrefKey.HOME_BLUR_ENHANCE),
                dataBindingRecv = blurBinding.binding.getRecv(1)
            )
        }
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_behavior_refactor,
                tipsId = R.string.home_behavior_refactor_tips,
                onClickListener = {
                    if (MIUIActivity.safeSP.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)) {
                        showFragment("home_refactor")
                    }
                    else {
                        MIUIDialog(activity) {
                            setTitle(R.string.home_behavior_refactor_dialog_title)
                            setMessage(R.string.home_behavior_refactor_dialog_msg)
                            setLButton(textId = R.string.button_cancel) {
                                dismiss()
                            }
                            setRButton(textId = R.string.button_ok) {
                                showFragment("home_refactor")
                                dismiss()
                            }
                        }.show()
                    }
                })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_behavior_blur_radius,
                tipsId = R.string.home_behavior_blur_radius_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_behavior_blur_radius)
                        setMessage(getString(R.string.home_behavior_blur_radius_msg) + " (${activity.getString(R.string.common_default)}: 100, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_BLUR_RADIUS, 100)
                        })")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-150")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_BLUR_RADIUS,
                                        getEditText().toInt().coerceIn(0, 150)
                                    )
                                }.onFailure {
                                    Toast.makeText(
                                        activity,
                                        activity.getString(R.string.common_invalid_input),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.cleaner_home_remove_report,
            ),
            SwitchV(PrefKey.HOME_REMOVE_REPORT),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_anim)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_anim_unlock,
                tipsId = R.string.home_anim_unlock_tips
            ),
            SwitchV(PrefKey.HOME_ANIM_UNLOCK)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_anim_zoom_sync,
                tipsId = R.string.home_anim_zoom_sync_tips
            ),
            SwitchV(PrefKey.HOME_WALLPAPER_ZOOM_SYNC),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_icon)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_unblock_google,
            ),
            SwitchV(PrefKey.HOME_ICON_UNBLOCK_GOOGLE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_corner4large,
                tipsId = R.string.home_icon_corner4large_tips
            ),
            SwitchV(PrefKey.HOME_ICON_CORNER4LARGE),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_perfect_icon,
                tipsId = R.string.home_icon_perfect_icon_tips
            ),
            SwitchV(PrefKey.HOME_ICON_PERFECT_ICON)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_recent)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_pad_show_memory,
            ),
            SwitchV(PrefKey.HOME_PAD_SHOW_MEMORY),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_show_real_memory,
            ),
            SwitchV(PrefKey.HOME_SHOW_REAL_MEMORY)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_wallpaper_darken,
                tipsId = R.string.home_recent_wallpaper_darken_tips
            ),
            SwitchV(PrefKey.HOME_WALLPAPER_DARKEN),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_dismiss_anim,
            ),
            SwitchV(PrefKey.HOME_RECENT_ANIM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_disable_fake_navbar,
                tipsId = R.string.home_disable_fake_navbar_tips
            ),
            SwitchV(PrefKey.HOME_DISABLE_FAKE_NAVBAR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_recent_dock_time,
                tipsId = R.string.home_recent_dock_time_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_recent_dock_time)
                        setMessage(
                            "${activity.getString(R.string.common_default)}: 180 (ms), ${activity.getString(R.string.dialog_current_value)}: ${
                                MIUIActivity.safeSP.getInt(PrefKey.HOME_PAD_DOCK_TIME_DURATION, 180)
                            } (ms)"
                        )
                        setEditText("", "${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_PAD_DOCK_TIME_DURATION, 180).takeIf { it != 180 } ?: ""
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_PAD_DOCK_TIME_DURATION,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
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
                                MIUIActivity.safeSP.getInt(PrefKey.HOME_PAD_DOCK_SAFE_AREA_HEIGHT, 300)
                            }"
                        )
                        setEditText("", "${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_PAD_DOCK_SAFE_AREA_HEIGHT, 300).takeIf { it != 180 } ?: ""
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_PAD_DOCK_SAFE_AREA_HEIGHT,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_widget)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_widget_anim,
            ),
            SwitchV(PrefKey.HOME_WIDGET_ANIM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_widget_resizable,
            ),
            SwitchV(PrefKey.HOME_WIDGET_RESIZABLE)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_personal_asist)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_minus_restore_setting,
            ),
            SwitchV(PrefKey.HOME_MINUS_RESTORE_SETTING)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_minus_fold_style,
            ),
            SwitchV(PrefKey.HOME_MINUS_FOLD_STYLE),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.personal_assist_blur,
            ),
            SwitchV(PrefKey.PERSON_ASSIST_BLUR),
            dataBindingRecv = refactorBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_folder)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_folder_adapt_icon_size,
                tipsId = R.string.home_folder_adapt_icon_size_tips
            ),
            SwitchV(PrefKey.HOME_FOLDER_ADAPT_SIZE)
        )
        val folderColumnsDef = if (Device.isPad) 4 else 3
        TextWithSeekBar(
            TextV(textId = R.string.home_folder_layout_size),
            SeekBarWithTextV(PrefKey.HOME_FOLDER_COLUMNS, 2, 7, folderColumnsDef)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_folder_layout_fix,
            ),
            SwitchV(PrefKey.HOME_FOLDER_NO_PADDING)
        )
    }
}