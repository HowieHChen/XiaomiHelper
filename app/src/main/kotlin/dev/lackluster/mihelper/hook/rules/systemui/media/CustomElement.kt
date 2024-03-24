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


package dev.lackluster.mihelper.hook.rules.systemui.media

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object CustomElement : YukiBaseHooker() {
    private val hideAppIcon = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.HIDE_APP_ICON, false)
    private val squigglyProgress = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.SQUIGGLY_PROGRESS, false)
    override fun onHook() {
        if(hideAppIcon || squigglyProgress) {
            "com.android.systemui.media.controls.models.player.MediaViewHolder\$Companion".toClass().method {
                name = "create"
                modifiers { isStatic }
            }.hook {
                after {
                    val mediaViewHolder = this.result ?: return@after
                    if (hideAppIcon) {
                        val appIcon = mediaViewHolder.current().field { name = "appIcon" }.any() as? ImageView?
                        (appIcon?.parent as? ViewGroup?)?.removeView(appIcon)
                    }
                    if (squigglyProgress) {
                        val seekBar = mediaViewHolder.current().field { name = "seekBar" }.any() as? SeekBar?
                        seekBar?.progressDrawable = "com.android.systemui.media.controls.ui.SquigglyProgress".toClass().constructor().get().call() as Drawable
                    }
                }
            }
        }
    }
}