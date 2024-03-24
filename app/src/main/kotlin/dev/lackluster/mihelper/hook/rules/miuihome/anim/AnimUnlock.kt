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

package dev.lackluster.mihelper.hook.rules.miuihome.anim

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object AnimUnlock : YukiBaseHooker(){
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ANIM_UNLOCK) {
            val animClz =
                if (Device.isPad) "com.miui.home.launcher.compat.UserPresentAnimationCompatV12Spring".toClass()
                else "com.miui.home.launcher.compat.UserPresentAnimationCompatV12Phone".toClass()
            animClz.method {
                name = "getSpringAnimator"
                paramCount = 6
            }.hook {
                before {
                    this.args(4).set(0.5f)
                    this.args(5).set(0.5f)
                }
            }
        }
    }
}