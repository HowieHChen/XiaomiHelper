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

import android.graphics.Paint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object StyleCustomHookEntry : YukiBaseHooker() {
    // background: 0 -> Default; 1 -> Enhanced; 2 -> Advanced textures; 3 -> Blurred cover; 4 -> AndroidNewStyle; 5 -> AndroidOldStyle
    private val background = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val playerTwoCircleViewClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.PlayerTwoCircleView".toClass()
    }

    override fun onHook() {
        when (background) {
            1 -> loadHooker(CoverArtStyle)
            2 -> loadHooker(AdvancedTexturesStyle)
            3 -> loadHooker(BlurredCoverStyle)
            4 -> loadHooker(RadialGradientStyle)
            5 -> loadHooker(LinearGradientStyle)
            else -> return
        }
        playerTwoCircleViewClass.apply {
            method {
                name = "onDraw"
            }.hook {
                before {
                    (this.instance.current().field { name = "mPaint1" }.any() as Paint).alpha = 0
                    (this.instance.current().field { name = "mPaint2" }.any() as Paint).alpha = 0
                    this.instance.current().field { name = "mRadius" }.set(0.0f)
                    // this.result = null
                }
            }
            method {
                name = "setBackground"
            }.hook {
                before {
                    result = null
                }
            }
            method {
                name = "setPaintColor"
            }.hook {
                before {
                    result = null
                }
            }
        }
    }


}