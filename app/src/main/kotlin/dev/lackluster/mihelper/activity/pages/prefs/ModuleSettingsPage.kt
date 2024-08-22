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

import android.content.ComponentName
import android.content.pm.PackageManager
import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.hyperx.app.AlertDialog
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.BackupUtils

@BMPage(Pages.MODULE_SETTINGS, hideMenu = true)
class ModuleSettingsPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_module)
    }

    override fun onCreate() {
        val showInSettingsBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.Module.SHOW_IN_SETTINGS, false)
        }) { view, flag, data ->
            when (flag) {
                0 -> {
                    if (data is Boolean) {
                        view.visibility = if (data) View.VISIBLE else View.GONE
                    }
                }
                1 -> {
                    val visible = MIUIActivity.safeSP.getBoolean(Pref.Key.Module.SHOW_IN_SETTINGS, false) &&
                            (MIUIActivity.safeSP.getInt(Pref.Key.Module.SETTINGS_NAME, 0) == 2)
                    view.visibility = if (visible) View.VISIBLE else View.GONE
                }
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_module_general),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.module_main_switch),
                SwitchData(Pref.Key.Module.ENABLED)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.module_lite_mode,
                    summaryId = R.string.module_lite_mode_tips
                ),
                SwitchData(Pref.Key.Module.LITE_MODE)
            )
            SwitchPreference(
                DescData(titleId = R.string.module_hide_icon),
                SwitchData(
                    key = Pref.Key.Module.HIDE_ICON,
                    onCheckedChangeListener = {
                        activity.packageManager.setComponentEnabledSetting(
                            ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"),
                            if (it) {
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            } else {
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            },
                            PackageManager.DONT_KILL_APP
                        )
                    }
                )
            )
            SwitchPreference(
                DescData(titleId = R.string.module_show_in_settings),
                SwitchData(
                    key = Pref.Key.Module.SHOW_IN_SETTINGS,
                    dataBindingSend = showInSettingsBinding.bindingSend
                )
            )
            DropDownPreference(
                DescData(titleId = R.string.module_settings_icon_style),
                DropDownData(
                    key = Pref.Key.Module.SETTINGS_ICON_STYLE,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_style_default), 0, R.drawable.ic_header_hyper_helper_gray),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_style_android), 1, R.drawable.ic_header_android_green)
                    )
                ),
                dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
            )
            DropDownPreference(
                DescData(titleId = R.string.module_settings_icon_color),
                DropDownData(
                    key = Pref.Key.Module.SETTINGS_ICON_COLOR,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_gray), 0, R.drawable.ic_color_gray),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_red), 1, R.drawable.ic_color_red),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_green), 2, R.drawable.ic_color_green),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_blue), 3, R.drawable.ic_color_blue),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_purple), 4, R.drawable.ic_color_purple),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_icon_color_yellow), 5, R.drawable.ic_color_yellow)
                    )
                ),
                dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
            )
            DropDownPreference(
                DescData(titleId = R.string.module_settings_name),
                DropDownData(
                    key = Pref.Key.Module.SETTINGS_NAME,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_name_helper), 0),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_name_advanced), 1),
                        DropDownData.SpinnerItemData(getString(R.string.module_settings_name_custom), 2)
                    ),
                    dataBindingSend = showInSettingsBinding.bindingSend
                ),
                dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.module_settings_custom_name
                ),
                EditTextData(
                    key = Pref.Key.Module.SETTINGS_NAME_CUSTOM,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = "Hyper Helper",
                    hintText = "Hyper Helper",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.module_settings_custom_name
                        )
                    )
                ),
                dataBindingRecv = showInSettingsBinding.binding.getRecv(1)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_module_backup),
            CategoryData()
        ) {
            TextPreference(
                DescData(titleId = R.string.module_backup),
                TextData(),
                onClickListener = {
                    BackupUtils.backup(activity)
                }
            )
            TextPreference(
                DescData(titleId = R.string.module_restore),
                TextData(),
                onClickListener = {
                    BackupUtils.restore(activity)
                }
            )
            TextPreference(
                DescData(titleId = R.string.module_reset),
                TextData(),
                onClickListener = {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_warning)
                        .setMessage(R.string.module_reset_warning)
                        .setCancelable(false)
                        .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.button_ok) { dialog, _ ->
                            dialog.dismiss()
                            BackupUtils.reset(activity)
                        }
                        .show()
                }
            )
        }
    }
}
