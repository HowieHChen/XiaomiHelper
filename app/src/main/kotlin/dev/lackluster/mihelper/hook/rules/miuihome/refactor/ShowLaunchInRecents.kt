/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2024 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.utils.Prefs

object ShowLaunchInRecents : YukiBaseHooker() {
    private val navStubViewClz by lazy {
        "com.miui.home.recents.NavStubView".toClass()
    }
    private val overviewStateClz by lazy {
        "com.miui.home.recents.OverviewState".toClass()
    }
    private val launchScale =
        Prefs.getFloat(Refactor.SHOW_LAUNCH_IN_RECENTS_SCALE, HomeRefactor.SHOW_LAUNCH_IN_RECENTS_SCALE)

    override fun onHook() {
        navStubViewClz.method {
            name = "changeAlphaScaleForFsGesture"
            paramCount = 2
        }.hook {
            before {
                this.args(0).set(1.0f)
            }
        }
        // Pad
        "com.miui.home.recents.GestureTouchEventTracker".toClassOrNull()?.apply {
            method {
                name = "changeAlphaScaleForFsGesture"
                paramCount = 2
            }.hook {
                before {
                    this.args(0).set(1.0f)
                }
            }
        }
        overviewStateClz.apply {
            method {
                name = "getShortcutMenuLayerAlpha"
            }.hook {
                replaceTo(1.0f)
            }
            method {
                name = "getShortcutMenuLayerScale"
            }.hook {
                replaceTo(launchScale)
            }
        }
//        navStubViewClz.method {
//            name = "appTouchResolution"
//        }.hook {
//            after {
//                val motionEvent = this.args(0).any() as MotionEvent
//                YLog.info("motionEvent :${
//                    motionEvent.action
//                }")
//            }
//        }
    }
}