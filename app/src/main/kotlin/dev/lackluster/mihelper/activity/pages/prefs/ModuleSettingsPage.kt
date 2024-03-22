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
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.BackupUtils

@BMPage("page_module", hideMenu = true)
class ModuleSettingsPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_module)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_module_general)
        TextWithSwitch(
            TextV(textId = R.string.module_main_switch),
            SwitchV(Pref.Key.Module.ENABLED)
        )
        TextWithSwitch(
            TextV(textId = R.string.module_hide_icon),
            SwitchV(
                key = Pref.Key.Module.HIDE_ICON,
                onClickListener = {
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
        val showInSettingsBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.Module.SHOW_IN_SETTINGS, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextWithSwitch(
            TextV(textId = R.string.module_show_in_settings),
            SwitchV(
                key = Pref.Key.Module.SHOW_IN_SETTINGS,
                dataBindingSend = showInSettingsBinding.bindingSend
            )
        )
        val settingsIconStyle: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.module_settings_icon_style_default)
            it[1] = getString(R.string.module_settings_icon_style_android)
        }
        TextWithSpinner(
            TextV(textId = R.string.module_settings_icon_style),
            SpinnerV(
                settingsIconStyle[MIUIActivity.safeSP.getInt(Pref.Key.Module.SETTINGS_ICON_STYLE, 0)].toString()
            ) {
                add(settingsIconStyle[0].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_STYLE, 0)
                }
                add(settingsIconStyle[1].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_STYLE, 1)
                }
            },
            dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
        )
        val settingsIconColor: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.module_settings_icon_color_grey)
            it[1] = getString(R.string.module_settings_icon_color_red)
            it[2] = getString(R.string.module_settings_icon_color_green)
            it[3] = getString(R.string.module_settings_icon_color_blue)
            it[4] = getString(R.string.module_settings_icon_color_purple)
            it[5] = getString(R.string.module_settings_icon_color_yellow)
        }
        TextWithSpinner(
            TextV(textId = R.string.module_settings_icon_style),
            SpinnerV(
                settingsIconColor[MIUIActivity.safeSP.getInt(Pref.Key.Module.SETTINGS_ICON_COLOR, 0)].toString()
            ) {
                add(settingsIconColor[0].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 0)
                }
                add(settingsIconColor[1].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 1)
                }
                add(settingsIconColor[2].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 2)
                }
                add(settingsIconColor[3].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 3)
                }
                add(settingsIconColor[4].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 4)
                }
                add(settingsIconColor[5].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_ICON_COLOR, 5)
                }
            },
            dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
        )
        val settingsName: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.module_settings_name_helper)
            it[1] = getString(R.string.module_settings_name_advanced)
        }
        TextWithSpinner(
            TextV(textId = R.string.module_settings_name),
            SpinnerV(
                settingsName[MIUIActivity.safeSP.getInt(Pref.Key.Module.SETTINGS_NAME, 0)].toString()
            ) {
                add(settingsName[0].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_NAME, 0)
                }
                add(settingsName[1].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Module.SETTINGS_NAME, 1)
                }
            },
            dataBindingRecv = showInSettingsBinding.binding.getRecv(0)
        )
        Line()
        TitleText(textId = R.string.ui_title_module_backup)
        TextWithArrow(
            TextV(
                textId = R.string.module_backup,
                onClickListener = {
                    BackupUtils.backup(activity)
                }
            )
        )
        TextWithArrow(
            TextV(
                textId = R.string.module_restore,
                onClickListener = {
                    BackupUtils.restore(activity)
                }
            )
        )
    }
}
