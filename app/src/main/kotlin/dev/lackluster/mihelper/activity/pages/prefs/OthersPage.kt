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
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage("page_others")
class OthersPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_others)
    }

    override fun onCreate() {
        val customSearchEngine: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.search_engine_default)
            it[1] = getString(R.string.search_engine_baidu)
            it[2] = getString(R.string.search_engine_sogou)
            it[3] = getString(R.string.search_engine_bing)
            it[4] = getString(R.string.search_engine_google)
            it[5] = getString(R.string.search_engine_custom)
        }
        TitleText(textId = R.string.ui_title_others_browser)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_browser_debug_mode,
                tipsId = R.string.others_browser_debug_mode_tips
            ),
            SwitchV(Pref.Key.Browser.DEBUG_MODE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_browser_switch_env,
                tipsId = R.string.others_browser_switch_env_tips
            ),
            SwitchV(Pref.Key.Browser.SWITCH_ENV)
        )
        TextWithSwitch(
            TextV(textId = R.string.others_browser_disable_update),
            SwitchV(Pref.Key.Browser.BLOCK_UPDATE)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_download)
        TextWithSwitch(
            TextV(textId = R.string.others_download_fuck_xl),
            SwitchV(Pref.Key.Download.FUCK_XL)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_gallery)
        TextWithSwitch(
            TextV(textId = R.string.others_gallery_unlimited_crop),
            SwitchV(Pref.Key.Gallery.UNLIMITED_CROP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_gallery_path_optim,
                tipsId = R.string.others_gallery_path_optim_tips
            ),
            SwitchV(Pref.Key.Gallery.PATH_OPTIM)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_joyose)
        TextWithSwitch(
            TextV(textId = R.string.others_joyose_no_cloud_control),
            SwitchV(Pref.Key.Joyose.BLOCK_CLOUD_CONTROL)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_miai)
        TextWithSwitch(
            TextV(textId = R.string.others_miai_hide_watermark),
            SwitchV(Pref.Key.MiAi.HIDE_WATERMARK)
        )
        val miAiUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiAi.SEARCH_USE_BROWSER, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        val miAiCustomSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiAi.SEARCH_USE_BROWSER, false) &&
                    (MIUIActivity.safeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0) == 5)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextWithSwitch(
            TextV(textId = R.string.search_use_browser),
            SwitchV(Pref.Key.MiAi.SEARCH_USE_BROWSER) {
                miAiUseBrowserBinding.binding.Send().send(it)
                miAiCustomSearchBinding.binding.Send().send(it
                        && (MIUIActivity.safeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0) == 5))
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.search_engine), SpinnerV(
                customSearchEngine[MIUIActivity.safeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0)].toString()
            ) {
                add(customSearchEngine[0].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 0)
                    miAiCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[1].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 1)
                    miAiCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[2].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 2)
                    miAiCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[3].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 3)
                    miAiCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[4].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 4)
                    miAiCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[5].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_ENGINE, 5)
                    miAiCustomSearchBinding.binding.Send().send(true)
                }
            }, dataBindingRecv = miAiUseBrowserBinding.binding.getRecv(1))
        TextWithArrow(
            TextV(textId = R.string.search_engine_custom_url, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(R.string.search_engine_custom_url)
                    setEditText(
                        MIUIActivity.safeSP.getString(Pref.Key.MiAi.SEARCH_URL, ""),
                        "https://example.com/s?q=%s"
                    )
                    setLButton(textId = R.string.button_cancel) {
                        dismiss()
                    }
                    setRButton(textId = R.string.button_ok) {
                        if (getEditText().isBlank()) {
                            MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_URL, "")
                            dismiss()
                        } else if (getEditText().contains("%s")) {
                            MIUIActivity.safeSP.putAny(Pref.Key.MiAi.SEARCH_URL, getEditText())
                            dismiss()
                        } else {
                            Toast.makeText(activity, getString(R.string.search_engine_custom_url_toast), Toast.LENGTH_SHORT).show()
                        }
                    }
                }.show()
            }), dataBindingRecv = miAiCustomSearchBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_package)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_package_update_system_app,
                tipsId = R.string.others_package_update_system_app_tips
            ),
            SwitchV(Pref.Key.PackageInstaller.UPDATE_SYSTEM_APP)
        )
//        TextWithSwitch(
//            TextV(textId = R.string.others_package_more_info),
//            SwitchV(Pref.Key.PackageInstaller.MORE_INFO)
//        )
        Line()
        TitleText(textId = R.string.ui_title_others_screen_recorder)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_screen_recorder_save_to_movies,
                tipsId = R.string.others_screen_recorder_save_to_movies_tips
            ),
            SwitchV(Pref.Key.ScreenRecorder.SAVE_TO_MOVIES)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_screenshot)
        TextWithSwitch(
            TextV(textId = R.string.others_screenshot_save_as_png),
            SwitchV(Pref.Key.Screenshot.SAVE_AS_PNG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_screenshot_save_to_picture,
                tipsId = R.string.others_screenshot_save_to_picture_tips
            ),
            SwitchV(Pref.Key.Screenshot.SAVE_TO_PICTURE)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_settings)
        TextWithSwitch(
            TextV(textId = R.string.others_settings_show_google),
            SwitchV(Pref.Key.Settings.SHOE_GOOGLE)
        )
        TextWithSwitch(
            TextV(textId = R.string.others_settings_unlock_voip_assistant),
            SwitchV(Pref.Key.Settings.UNLOCK_VOIP_ASSISTANT)
        )
        TextWithSwitch(
            TextV(textId = R.string.others_settings_unlock_custom_refresh),
            SwitchV(Pref.Key.Settings.UNLOCK_CUSTOM_REFRESH)
        )
        TextWithSwitch(
            TextV(textId = R.string.others_settings_unlock_net_mode),
            SwitchV(Pref.Key.Settings.UNLOCK_NET_MODE_SETTINGS)
        )
        if (Device.isPad) {
            TextWithSwitch(
                TextV(textId = R.string.others_settings_unlock_taplus_for_pad),
                SwitchV(Pref.Key.Settings.UNLOCK_TAPLUS_FOR_PAD)
            )
        }
        Line()
        TitleText(textId = R.string.ui_title_others_taplus)
        val taplusUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.Taplus.SEARCH_USE_BROWSER, false)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        val taplusCustomSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.Taplus.SEARCH_USE_BROWSER, false) &&
                    (MIUIActivity.safeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0) == 5)
        }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextWithSwitch(
            TextV(textId = R.string.search_use_browser),
            SwitchV(Pref.Key.Taplus.SEARCH_USE_BROWSER) {
                taplusUseBrowserBinding.binding.Send().send(it)
                taplusCustomSearchBinding.binding.Send().send(it
                        && (MIUIActivity.safeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0) == 5))
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.search_engine), SpinnerV(
                customSearchEngine[MIUIActivity.safeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0)].toString()
            ) {
                add(customSearchEngine[0].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 0)
                    taplusCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[1].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 1)
                    taplusCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[2].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 2)
                    taplusCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[3].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 3)
                    taplusCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[4].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 4)
                    taplusCustomSearchBinding.binding.Send().send(false)
                }
                add(customSearchEngine[5].toString()) {
                    MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_ENGINE, 5)
                    taplusCustomSearchBinding.binding.Send().send(true)
                }
            }, dataBindingRecv = taplusUseBrowserBinding.binding.getRecv(1))
        TextWithArrow(
            TextV(textId = R.string.search_engine_custom_url, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(R.string.search_engine_custom_url)
                    setEditText(
                        MIUIActivity.safeSP.getString(Pref.Key.Taplus.SEARCH_URL, ""),
                        "https://example.com/s?q=%s"
                    )
                    setLButton(textId = R.string.button_cancel) {
                        dismiss()
                    }
                    setRButton(textId = R.string.button_ok) {
                        if (getEditText().isBlank()) {
                            MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_URL, "")
                            dismiss()
                        } else if (getEditText().contains("%s")) {
                            MIUIActivity.safeSP.putAny(Pref.Key.Taplus.SEARCH_URL, getEditText())
                            dismiss()
                        } else {
                            Toast.makeText(activity, getString(R.string.search_engine_custom_url_toast), Toast.LENGTH_SHORT).show()
                        }
                    }
                }.show()
            }), dataBindingRecv = taplusCustomSearchBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_others_updater)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_updater_disable_validation,
                tipsId = R.string.others_updater_disable_validation_tips
            ),
            SwitchV(Pref.Key.Updater.DISABLE_VALIDATION)
        )
    }
}