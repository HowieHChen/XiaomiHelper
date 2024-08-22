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
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.ShellUtils

@BMPage(Pages.SYSTEM_FRAMEWORK, hideMenu = false)
class SystemFrameworkPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.page_android)
    }

    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_android_freeform),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.android_freeform_restriction,
                    summaryId = R.string.android_freeform_restriction_tips
                ),
                SwitchData(Pref.Key.Android.DISABLE_FREEFORM_RESTRICT)
            )
            SwitchPreference(
                DescData(titleId = R.string.android_freeform_allow_more),
                SwitchData(Pref.Key.Android.ALLOW_MORE_FREEFORM)
            )
            SwitchPreference(
                DescData(titleId = R.string.android_freeform_multi_task),
                SwitchData(Pref.Key.Android.MULTI_TASK)
            )
            SwitchPreference(
                DescData(titleId = R.string.android_freeform_hide_topbar),
                SwitchData(Pref.Key.Android.HIDE_WINDOW_TOP_BAR)
            )
        }
        val disableFixedOrientationBinding =
            GetDataBinding({
                MIUIActivity.safeSP.getBoolean(Pref.Key.Android.BLOCK_FIXED_ORIENTATION, false)
            }) { view, _, data ->
                view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_android_screen_rotation),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(
                    titleId = R.string.android_rotation_switch_suggestions,
                    summaryId = R.string.android_rotation_switch_suggestions_tips
                ),
                DropDownData(
                    key = Pref.Key.Android.ROTATE_SUGGEST,
                    defValue = checkRotateSuggest(),
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.common_default), 0),
                        DropDownData.SpinnerItemData(getString(R.string.common_disabled), 1),
                        DropDownData.SpinnerItemData(getString(R.string.common_enabled), 2),
                    ),
                    onItemSelectedListener = { _, value ->
                        try {
                            when(value) {
                                0 -> {
                                    ShellUtils.tryExec("settings delete secure show_rotation_suggestions", useRoot = true, checkSuccess = true)
                                }
                                1 -> {
                                    ShellUtils.tryExec("settings put secure show_rotation_suggestions 0", useRoot = true, checkSuccess = true)
                                }
                                2 -> {
                                    ShellUtils.tryExec("settings put secure show_rotation_suggestions 1", useRoot = true, checkSuccess = true)
                                }
                            }
                            Toast.makeText(activity, R.string.dialog_done, Toast.LENGTH_LONG).show()
                        } catch (tout: Throwable) {
                            MIUIActivity.safeSP.putAny(Pref.Key.Android.ROTATE_SUGGEST, 0)
                            Toast.makeText(
                                activity,
                                getString(R.string.android_rotation_switch_suggestions_failed) + "(${tout.message})",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.android_rotation_disable_fixed_orientation,
                    summaryId = R.string.android_rotation_disable_fixed_orientation_tips
                ),
                SwitchData(
                    key = Pref.Key.Android.BLOCK_FIXED_ORIENTATION,
                    dataBindingSend = disableFixedOrientationBinding.bindingSend
                )
            )
            TextPreference(
                DescData(
                    titleId = R.string.android_rotation_disable_fixed_orientation_scope,
                    summaryId = R.string.android_rotation_disable_fixed_orientation_scope_tips
                ),
                TextData(),
                dataBindingRecv = disableFixedOrientationBinding.getRecv(1),
                onClickListener = {
                    showFragment(Pages.DISABLE_FIXED_ORIENTATION)
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_android_others),
            CategoryData()
        ) {
            EditTextPreference(
                DescData(
                    titleId = R.string.android_others_wallpaper_scale,
                    summaryId = R.string.android_others_wallpaper_scale_tips
                ),
                EditTextData(
                    key = Pref.Key.Android.WALLPAPER_SCALE_RATIO,
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = 1.1f,
                    hintText = "1.1",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.android_others_wallpaper_scale,
                            summary = "${activity.getString(R.string.common_range)}: 1.0-2.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float) in 1.0f..2.0f
                    }
                )
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.android_others_force_dark,
                    summaryId = R.string.android_others_force_dark_tips
                ),
                SwitchData(Pref.Key.Android.BLOCK_FORCE_DARK_WHITELIST)
            )
        }
    }

    private fun checkRotateSuggest(): Int {
        return try {
            (ShellUtils.tryExec(
                "settings get secure show_rotation_suggestions", useRoot = true, checkSuccess = true
            ).successMsg.toIntOrNull() ?: -1) + 1
        }
        catch (tout : Throwable) {
            Toast.makeText(
                activity,
                getString(R.string.android_rotation_switch_suggestions_failed) + "(${tout.message})",
                Toast.LENGTH_LONG
            ).show()
            return 0
        }
    }
}