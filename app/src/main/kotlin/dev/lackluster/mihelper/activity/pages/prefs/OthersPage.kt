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
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage(Pages.OTHERS)
class OthersPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_others)
    }

    override fun onCreate() {
        val padBinding = GetDataBinding({
            Device.isPad
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val customSearchEngineEntries = arrayOf(
            DropDownData.SpinnerItemData(getString(R.string.search_engine_default), 0),
            DropDownData.SpinnerItemData(getString(R.string.search_engine_baidu), 1),
            DropDownData.SpinnerItemData(getString(R.string.search_engine_sogou), 2),
            DropDownData.SpinnerItemData(getString(R.string.search_engine_bing), 3),
            DropDownData.SpinnerItemData(getString(R.string.search_engine_google), 4),
            DropDownData.SpinnerItemData(getString(R.string.search_engine_custom), 5),
        )
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_browser),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.others_browser_debug_mode,
                    summaryId = R.string.others_browser_debug_mode_tips
                ),
                SwitchData(Pref.Key.Browser.DEBUG_MODE)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.others_browser_switch_env,
                    summaryId = R.string.others_browser_switch_env_tips
                ),
                SwitchData(Pref.Key.Browser.SWITCH_ENV)
            )
            SwitchPreference(
                DescData(titleId = R.string.others_browser_disable_update),
                SwitchData(Pref.Key.Browser.BLOCK_UPDATE)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_download),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_download_fuck_xl),
                SwitchData(Pref.Key.Download.FUCK_XL)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_gallery),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_gallery_unlimited_crop),
                SwitchData(Pref.Key.Gallery.UNLIMITED_CROP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.others_gallery_path_optim,
                    summaryId = R.string.others_gallery_path_optim_tips
                ),
                SwitchData(Pref.Key.Gallery.PATH_OPTIM)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_joyose),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_joyose_no_cloud_control),
                SwitchData(Pref.Key.Joyose.BLOCK_CLOUD_CONTROL)
            )
        }
        val miAiUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiAi.SEARCH_USE_BROWSER, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        val miAiCustomSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0)
        }) { view, _, data ->
            view.visibility =
                if (MIUIActivity.safeSP.getBoolean(Pref.Key.MiAi.SEARCH_USE_BROWSER, false) && data as Int == 5) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_miai),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_miai_hide_watermark),
                SwitchData(Pref.Key.MiAi.HIDE_WATERMARK)
            )
            SwitchPreference(
                DescData(titleId = R.string.search_use_browser),
                SwitchData(Pref.Key.MiAi.SEARCH_USE_BROWSER) {
                    miAiUseBrowserBinding.binding.Send().send(it)
                    miAiCustomSearchBinding.binding.Send().send(
                        MIUIActivity.safeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0)
                    )
                }
            )
            DropDownPreference(
                DescData(titleId = R.string.search_engine),
                DropDownData(
                    key = Pref.Key.MiAi.SEARCH_ENGINE,
                    entries = customSearchEngineEntries,
                    dataBindingSend = miAiCustomSearchBinding.bindingSend
                ),
                dataBindingRecv = miAiUseBrowserBinding.binding.getRecv(0)
            )
            EditTextPreference(
                DescData(titleId = R.string.search_engine_custom_url),
                EditTextData(
                    key = Pref.Key.MiAi.SEARCH_URL,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = "",
                    hintText = "https://example.com/s?q=%s",
                    showValue = EditTextData.ValuePosition.SUMMARY_VIEW,
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.search_engine_custom_url
                        )
                    ),
                    isValueValid = { value ->
                        val string = value as String
                        return@EditTextData string.isEmpty() || string.contains("%s")
                    }
                ),
                dataBindingRecv = miAiCustomSearchBinding.binding.getRecv(0)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_package),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.others_package_update_system_app,
                    summaryId = R.string.others_package_update_system_app_tips
                ),
                SwitchData(Pref.Key.PackageInstaller.UPDATE_SYSTEM_APP)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_screen_recorder),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.others_screen_recorder_save_to_movies,
                    summaryId = R.string.others_screen_recorder_save_to_movies_tips
                ),
                SwitchData(Pref.Key.ScreenRecorder.SAVE_TO_MOVIES)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_screenshot),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_screenshot_save_as_png),
                SwitchData(Pref.Key.Screenshot.SAVE_AS_PNG)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.others_screenshot_save_to_picture,
                    summaryId = R.string.others_screenshot_save_to_picture_tips
                ),
                SwitchData(Pref.Key.Screenshot.SAVE_TO_PICTURE)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_settings),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_settings_show_google),
                SwitchData(Pref.Key.Settings.SHOE_GOOGLE)
            )
            SwitchPreference(
                DescData(titleId = R.string.others_settings_unlock_voip_assistant),
                SwitchData(Pref.Key.Settings.UNLOCK_VOIP_ASSISTANT)
            )
            SwitchPreference(
                DescData(titleId = R.string.others_settings_unlock_custom_refresh),
                SwitchData(Pref.Key.Settings.UNLOCK_CUSTOM_REFRESH)
            )
            SwitchPreference(
                DescData(titleId = R.string.others_settings_unlock_net_mode),
                SwitchData(Pref.Key.Settings.UNLOCK_NET_MODE_SETTINGS)
            )
            SwitchPreference(
                DescData(titleId = R.string.others_settings_unlock_taplus_for_pad),
                SwitchData(Pref.Key.Settings.UNLOCK_TAPLUS_FOR_PAD),
                dataBindingRecv = padBinding.binding.getRecv(0)
            )
        }
        val taplusUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.Taplus.SEARCH_USE_BROWSER, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        val taplusCustomSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0)
        }) { view, _, data ->
            view.visibility =
                if (MIUIActivity.safeSP.getBoolean(Pref.Key.Taplus.SEARCH_USE_BROWSER, false) && data as Int == 5) View.VISIBLE else View.GONE
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_taplus),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.search_use_browser),
                SwitchData(Pref.Key.Taplus.SEARCH_USE_BROWSER) {
                    taplusUseBrowserBinding.binding.Send().send(it)
                    taplusCustomSearchBinding.binding.Send().send(
                        MIUIActivity.safeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0)
                    )
                }
            )
            DropDownPreference(
                DescData(titleId = R.string.search_engine),
                DropDownData(
                    key = Pref.Key.Taplus.SEARCH_ENGINE,
                    entries = customSearchEngineEntries,
                    dataBindingSend = taplusCustomSearchBinding.bindingSend
                ),
                dataBindingRecv = taplusUseBrowserBinding.binding.getRecv(0)
            )
            EditTextPreference(
                DescData(titleId = R.string.search_engine_custom_url),
                EditTextData(
                    key = Pref.Key.Taplus.SEARCH_URL,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = "",
                    hintText = "https://example.com/s?q=%s",
                    showValue = EditTextData.ValuePosition.SUMMARY_VIEW,
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.search_engine_custom_url
                        )
                    ),
                    isValueValid = { value ->
                        val string = value as String
                        return@EditTextData string.isEmpty() || string.contains("%s")
                    }
                ),
                dataBindingRecv = taplusCustomSearchBinding.binding.getRecv(0)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_updater),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.others_updater_disable_validation,
                    summaryId = R.string.others_updater_disable_validation_tips
                ),
                SwitchData(Pref.Key.Updater.DISABLE_VALIDATION)
            )
        }
    }
}