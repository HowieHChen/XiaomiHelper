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
import cn.fkj233.ui.activity.data.SeekBarData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.hyperx.app.AlertDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage(Pages.MIUI_HOME, hideMenu = false)
class MiuiHomePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_miui_home)
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
        val refactorBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val minusBlurTypeBinding = GetDataBinding({
            0
        }) { view, flags, _ ->
            val refactor = MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
            val blurType = MIUIActivity.safeSP.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, 0)
            when (flags) {
                1 -> {
                    view.visibility = if (!refactor && blurType == 1) View.VISIBLE else View.GONE
                }
                2 -> {
                    view.visibility = if (!refactor && blurType == 2) View.VISIBLE else View.GONE
                }
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_exclusive),
            CategoryData(hideLine = true)
        ) {
            TextPreference(
                DescData(
                    titleId = R.string.home_exclusive_refactor,
                    summaryId = R.string.home_exclusive_refactor_tips
                ),
                TextData(
                    valueAdapter = {
                        getString(if (MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)) {
                            R.string.common_on
                        } else {
                            R.string.common_off
                        })
                    }
                ),
                onClickListener = {
                    if (MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)) {
                        showFragment(Pages.HOME_REFACTOR)
                    } else {
                        AlertDialog.Builder(activity)
                            .setTitle(R.string.home_exclusive_refactor_dialog_title)
                            .setMessage(R.string.home_exclusive_refactor_dialog_msg)
                            .setCancelable(false)
                            .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.button_ok) { dialog, _ ->
                                MIUIActivity.safeSP.putAny(Pref.Key.MiuiHome.REFACTOR, true)
                                showFragment(Pages.HOME_REFACTOR)
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_gesture),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_gesture_double_tap),
                SwitchData(Pref.Key.MiuiHome.DOUBLE_TAP_TO_SLEEP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_gesture_quick_switch,
                    summaryId = R.string.home_gesture_quick_switch_tips
                ),
                SwitchData(Pref.Key.MiuiHome.QUICK_SWITCH)
            )
        }
        val refactorWallpaperBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.REFACTOR, false) &&
                    MIUIActivity.safeSP.getBoolean(Pref.Key.MiuiHome.Refactor.SYNC_WALLPAPER_SCALE, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_anim),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_anim_unlock,
                    summaryId = R.string.home_anim_unlock_tips
                ),
                SwitchData(Pref.Key.MiuiHome.ANIM_UNLOCK)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_anim_zoom_sync,
                    summaryId = R.string.home_anim_zoom_sync_tips
                ),
                SwitchData(Pref.Key.MiuiHome.ANIM_WALLPAPER_ZOOM_SYNC),
                dataBindingRecv = refactorWallpaperBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_anim_icon_zoom),
                SwitchData(Pref.Key.MiuiHome.ANIM_ICON_ZOOM)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_anim_icon_darken),
                SwitchData(Pref.Key.MiuiHome.ANIM_ICON_DARKEN)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_folder),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_folder_adapt_icon_size,
                    summaryId = R.string.home_folder_adapt_icon_size_tips
                ),
                SwitchData(Pref.Key.MiuiHome.FOLDER_ADAPT_SIZE),
                dataBindingRecv = padBinding.binding.getRecv(1)
            )
            val folderColumnsDef = if (Device.isPad) 4 else 3
            SeekBarPreference(
                DescData(titleId = R.string.home_folder_layout_size),
                SeekBarData(Pref.Key.MiuiHome.FOLDER_COLUMNS, 2, 7, folderColumnsDef, true)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_folder_layout_fix),
                SwitchData(Pref.Key.MiuiHome.FOLDER_NO_PADDING)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_folder_advanced_textures),
                SwitchData(Pref.Key.MiuiHome.FOLDER_BLUR)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_icon),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_icon_unblock_google),
                SwitchData(Pref.Key.MiuiHome.ICON_UNBLOCK_GOOGLE)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_icon_perfect_icon,
                    summaryId = R.string.home_icon_perfect_icon_tips
                ),
                SwitchData(Pref.Key.MiuiHome.ICON_PERFECT)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_icon_corner4large,
                    summaryId = R.string.home_icon_corner4large_tips
                ),
                SwitchData(Pref.Key.MiuiHome.ICON_CORNER4LARGE),
                dataBindingRecv = padBinding.binding.getRecv(1)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_recent),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_recent_pad_show_memory),
                SwitchData(Pref.Key.MiuiHome.PAD_RECENT_SHOW_MEMORY),
                dataBindingRecv = padBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_recent_show_real_memory),
                SwitchData(Pref.Key.MiuiHome.RECENT_SHOW_REAL_MEMORY)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_recent_wallpaper_darken,
                    summaryId = R.string.home_recent_wallpaper_darken_tips
                ),
                SwitchData(Pref.Key.MiuiHome.RECENT_WALLPAPER_DARKEN),
                dataBindingRecv = refactorBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_recent_dismiss_anim),
                SwitchData(Pref.Key.MiuiHome.RECENT_CARD_ANIM)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_recent_disable_fake_navbar,
                    summaryId = R.string.home_recent_disable_fake_navbar_tips
                ),
                SwitchData(Pref.Key.MiuiHome.RECENT_DISABLE_FAKE_NAVBAR)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_recent_dock_time,
                    summaryId = R.string.home_recent_dock_time_tips
                ),
                EditTextData(
                    key = Pref.Key.MiuiHome.PAD_DOCK_TIME_DURATION,
                    valueType = EditTextData.ValueType.INT,
                    defValue = 180,
                    hintText = "180(ms)",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_recent_dock_time,
                            summaryId = R.string.home_recent_dock_time_tips
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Int) > 0
                    }
                ),
                dataBindingRecv = padBinding.binding.getRecv(0)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_recent_docke_safe_height,
                    summaryId = R.string.home_recent_docke_safe_height_tips
                ),
                EditTextData(
                    key = Pref.Key.MiuiHome.PAD_DOCK_SAFE_AREA_HEIGHT,
                    valueType = EditTextData.ValueType.INT,
                    defValue = 300,
                    hintText = "300",
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_recent_docke_safe_height,
                            summaryId = R.string.home_recent_docke_safe_height_tips
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Int) > 0
                    }
                ),
                dataBindingRecv = padBinding.binding.getRecv(0)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_widget),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_widget_anim),
                SwitchData(Pref.Key.MiuiHome.WIDGET_ANIM)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_widget_resizable),
                SwitchData(Pref.Key.MiuiHome.WIDGET_RESIZABLE)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_shortcut),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_shortcut_freeform),
                SwitchData(Pref.Key.MiuiHome.SHORTCUT_FREEFORM)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_shortcut_instance),
                SwitchData(Pref.Key.MiuiHome.SHORTCUT_INSTANCE)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_personal_asist),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.home_minus_restore_setting),
                SwitchData(Pref.Key.MiuiHome.MINUS_RESTORE_SETTING)
            )
            DropDownPreference(
                DescData(titleId = R.string.home_minus_blur_type),
                DropDownData(
                    key = Pref.Key.MiuiHome.MINUS_BLUR_TYPE,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.home_minus_blur_type_def), 0),
                        DropDownData.SpinnerItemData(getString(R.string.home_minus_blur_type_sys), 1),
                        DropDownData.SpinnerItemData(getString(R.string.home_minus_blur_type_texture), 2),
                    ),
                    dataBindingSend = minusBlurTypeBinding.bindingSend
                ),
                dataBindingRecv = refactorBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.home_minus_fold_style),
                SwitchData(Pref.Key.MiuiHome.MINUS_FOLD_STYLE),
                dataBindingRecv = minusBlurTypeBinding.binding.getRecv(1)
            )
            TextPreference(
                DescData(titleId = R.string.home_minus_page_minus_blur),
                TextData(),
                onClickListener = {
                    showFragment(Pages.MINUS_BLUR)
                },
                dataBindingRecv = minusBlurTypeBinding.binding.getRecv(2)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_others),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_others_always_show_clock,
                ),
                SwitchData(Pref.Key.MiuiHome.ALWAYS_SHOW_TIME)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_others_fake_premium,
                    summaryId = R.string.home_others_fake_premium_tips
                ),
                SwitchData(Pref.Key.MiuiHome.FAKE_PREMIUM)
            )
        }
    }
}