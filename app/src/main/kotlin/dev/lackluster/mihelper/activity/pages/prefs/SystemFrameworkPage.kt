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
import dev.lackluster.mihelper.utils.ShellUtils

@BMPage("page_android")
class SystemFrameworkPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.page_android)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_android_freeform)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_freeform_restriction,
                tipsId = R.string.android_freeform_restriction_tips
            ),
            SwitchV(Pref.Key.Android.DISABLE_FREEFORM_RESTRICT)
        )
        TextWithSwitch(
            TextV(textId = R.string.android_freeform_allow_more),
            SwitchV(Pref.Key.Android.ALLOW_MORE_FREEFORM)
        )
        TextWithSwitch(
            TextV(textId = R.string.android_freeform_multi_task),
            SwitchV(Pref.Key.Android.MULTI_TASK)
        )
        TextWithSwitch(
            TextV(textId = R.string.android_freeform_hide_topbar),
            SwitchV(Pref.Key.Android.HIDE_WINDOW_TOP_BAR)
        )
        Line()
        TitleText(textId = R.string.ui_title_android_screen_rotation)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.android_rotation_switch_suggestions,
                tipsId = R.string.android_rotation_switch_suggestions_tips,
                onClickListener = {
                    val next =
                        try {
                            1 - (ShellUtils.tryExec(
                                "settings get secure show_rotation_suggestions", useRoot = true, checkSuccess = true
                            ).successMsg.toIntOrNull() ?: 0)
                        }
                        catch (tout : Throwable) {
                            Toast.makeText(
                                activity,
                                getString(R.string.android_rotation_switch_suggestions_failed) + "(${tout.message})",
                                Toast.LENGTH_LONG
                            ).show()
                            return@TextSummaryV
                        }
                    MIUIDialog(activity) {
                        setTitle(R.string.dialog_warning)
                        setMessage(
                            if (next == 0) { R.string.android_rotation_switch_suggestions_before_false }
                            else { R.string.android_rotation_switch_suggestions_before_true }
                        )
                        setLButton(R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(R.string.button_ok) {
                            try {
                                ShellUtils.tryExec("settings put secure show_rotation_suggestions $next", useRoot = true, checkSuccess = true)
                                Toast.makeText(
                                    activity,
                                    if (next == 0) { getString(R.string.android_rotation_switch_suggestions_done_false) }
                                    else { getString(R.string.android_rotation_switch_suggestions_done_true) },
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            catch (tout : Throwable) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.android_rotation_switch_suggestions_failed) + "(${tout.message})",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            finally {
                                dismiss()
                            }
                        }
                    }.show()
                }
            )
        )
        val disableFixedOrientationBinding =
            GetDataBinding({
                MIUIActivity.safeSP.getBoolean(PrefKey.ANDROID_NO_FIXED_ORIENTATION, false)
            }) { view, _, data ->
                view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_rotation_disable_fixed_orientation,
                tipsId = R.string.android_rotation_disable_fixed_orientation_tips
            ),
            SwitchV(
                key = Pref.Key.Android.BLOCK_FIXED_ORIENTATION,
                dataBindingSend = disableFixedOrientationBinding.bindingSend
            )
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.android_rotation_disable_fixed_orientation_scope,
                tipsId = R.string.android_rotation_disable_fixed_orientation_scope_tips
            ) {
                showFragment("disable_fixed_orientation")
            },
            dataBindingRecv = disableFixedOrientationBinding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_android_others)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.android_others_wallpaper_scale,
                tipsId = R.string.android_others_wallpaper_scale_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.android_others_wallpaper_scale)
                        setMessage(
                            "${activity.getString(R.string.common_default)}: 1.2, ${activity.getString(R.string.common_current)}: ${
                                MIUIActivity.safeSP.getFloat(Pref.Key.Android.WALLPAPER_SCALE_RATIO, 1.2f)
                            }"
                        )
                        setEditText("", "${activity.getString(R.string.common_range)}: 1.0-2.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        Pref.Key.Android.WALLPAPER_SCALE_RATIO,
                                        getEditText().toFloat()
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
                }
            )
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_others_force_dark,
                tipsId = R.string.android_others_force_dark_tips
            ),
            SwitchV(Pref.Key.Android.BLOCK_FORCE_DARK_WHITELIST)
        )
    }
}