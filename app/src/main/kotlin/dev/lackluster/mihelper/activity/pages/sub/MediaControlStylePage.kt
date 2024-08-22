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
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.SeekBarData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.MediaControl

@BMPage(Pages.MEDIA_CONTROL, hideMenu = false)
class MediaControlStylePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_media_control_style)
    }

    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_media_general),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.media_general_unlock_action),
                SwitchData(MediaControl.UNLOCK_ACTION)
            )
            SwitchPreference(
                DescData(titleId = R.string.media_general_squiggly_progress),
                SwitchData(MediaControl.SQUIGGLY_PROGRESS)
            )
            SwitchPreference(
                DescData(titleId = R.string.media_general_hide_app_icon),
                SwitchData(MediaControl.HIDE_APP_ICON)
            )
        }
        val mediaControlStyleBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(MediaControl.BACKGROUND_STYLE, 0)
        }) { view, flags, data ->
            when (flags) {
                // mediaControlStyle[3]
                1 -> view.visibility = if ((data as Int) == 3) View.VISIBLE else View.GONE
                // mediaControlStyle[3~5]
                2 -> view.visibility = if ((data as Int) in setOf(3, 4, 5)) View.VISIBLE else View.GONE
                // mediaControlStyle[5]
                3 -> view.visibility = if ((data as Int) == 5) View.VISIBLE else View.GONE
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_media_style),
            CategoryData()
        ) {
            DropDownPreference(
                DescData(titleId = R.string.systemui_notif_media_control_style),
                DropDownData(
                    key = MediaControl.BACKGROUND_STYLE,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_default), 0),
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_enhance), 1),
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_texture), 2),
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_blur), 3),
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_android_new), 4),
                        DropDownData.SpinnerItemData(getString(R.string.media_style_custom_android_old), 5),
                    ),
                    dataBindingSend = mediaControlStyleBinding.bindingSend
                )
            )
            SwitchPreference(
                DescData(titleId = R.string.media_style_android_anim),
                SwitchData(MediaControl.USE_ANIM),
                dataBindingRecv = mediaControlStyleBinding.binding.getRecv(2)
            )
            SeekBarPreference(
                DescData(titleId = R.string.media_style_blur_radius),
                SeekBarData(MediaControl.BLUR_RADIUS, 1, 20, 10, true),
                dataBindingRecv = mediaControlStyleBinding.binding.getRecv(1)
            )
            SwitchPreference(
                DescData(titleId = R.string.media_style_android_reverse),
                SwitchData(MediaControl.ALLOW_REVERSE),
                dataBindingRecv = mediaControlStyleBinding.binding.getRecv(3)
            )
        }
    }
}