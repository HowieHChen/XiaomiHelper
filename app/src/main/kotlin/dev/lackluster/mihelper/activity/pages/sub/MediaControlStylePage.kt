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
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.MediaControl

@BMPage("page_media_control")
class MediaControlStylePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_media_control_style)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_media_general)
        TextWithSwitch(
            TextV(textId = R.string.media_general_unlock_action),
            SwitchV(MediaControl.UNLOCK_ACTION)
        )
        TextWithSwitch(
            TextV(textId = R.string.media_general_squiggly_progress),
            SwitchV(MediaControl.SQUIGGLY_PROGRESS)
        )
        TextWithSwitch(
            TextV(textId = R.string.media_general_hide_app_icon),
            SwitchV(MediaControl.HIDE_APP_ICON)
        )
        Line()
        TitleText(textId = R.string.ui_title_media_style)
        val mediaControlStyle: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.media_style_custom_default)
            it[1] = getString(R.string.media_style_custom_enhance)
            it[2] = getString(R.string.media_style_custom_texture)
            it[3] = getString(R.string.media_style_custom_blur)
            it[4] = getString(R.string.media_style_custom_android_new)
            it[5] = getString(R.string.media_style_custom_android_old)
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
        TextWithSpinner(
            TextV(textId = R.string.systemui_notif_media_control_style),
            SpinnerV(
                mediaControlStyle[
                    MIUIActivity.safeSP.getInt(MediaControl.BACKGROUND_STYLE, 0)
                ].toString()
            ) {
                add(mediaControlStyle[0].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 0)
                    mediaControlStyleBinding.binding.Send().send(0)
                }
                add(mediaControlStyle[1].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 1)
                    mediaControlStyleBinding.binding.Send().send(1)
                }
                add(mediaControlStyle[2].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 2)
                    mediaControlStyleBinding.binding.Send().send(2)
                }
                add(mediaControlStyle[3].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 3)
                    mediaControlStyleBinding.binding.Send().send(3)
                }
                add(mediaControlStyle[4].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 4)
                    mediaControlStyleBinding.binding.Send().send(4)
                }
                add(mediaControlStyle[5].toString()) {
                    MIUIActivity.safeSP.putAny(MediaControl.BACKGROUND_STYLE, 5)
                    mediaControlStyleBinding.binding.Send().send(5)
                }
            }
        )
        TextWithSwitch(
            TextV(textId = R.string.media_style_android_anim),
            SwitchV(MediaControl.USE_ANIM),
            dataBindingRecv = mediaControlStyleBinding.binding.getRecv(2)
        )
        TextWithSeekBar(
            TextV(textId = R.string.media_style_blur_radius),
            SeekBarWithTextV(MediaControl.BLUR_RADIUS, 1, 20, 10),
            dataBindingRecv = mediaControlStyleBinding.binding.getRecv(1)
        )
        TextWithSwitch(
            TextV(textId = R.string.media_style_android_reverse),
            SwitchV(MediaControl.ALLOW_REVERSE),
            dataBindingRecv = mediaControlStyleBinding.binding.getRecv(3)
        )
    }
}