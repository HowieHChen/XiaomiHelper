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

package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DoubleTapToSleep : YukiBaseHooker() {
    private const val DOUBLE_TAP_CONTROLLER = "mDoubleTapControllerEx"
    private val workspaceClass by lazy {
        "com.miui.home.launcher.Workspace".toClass()
    }
    private val cellLayoutClass by lazy {
        "com.miui.home.launcher.CellLayout".toClass()
    }
    private val getCurrentCellLayoutMethod by lazy {
        workspaceClass.method {
            name = "getCurrentCellLayout"
            superClass()
        }.give()
    }
    private val isInNormalEditingModeMethod by lazy {
        workspaceClass.method {
            name = "isInNormalEditingMode"
            superClass()
        }.give()
    }
    private val lastDownOnOccupiedCellMethod by lazy {
        cellLayoutClass.method {
            name = "lastDownOnOccupiedCell"
        }.give()
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.DOUBLE_TAP_TO_SLEEP) {
            workspaceClass.apply {
                constructor {
                    paramCount = 3
                }.hook {
                    after {
                        val context = this.args(0).cast<Context>() ?: return@after
                        if (XposedHelpers.getAdditionalInstanceField(this.instance, DOUBLE_TAP_CONTROLLER) == null) {
                            XposedHelpers.setAdditionalInstanceField(this.instance, DOUBLE_TAP_CONTROLLER, DoubleTapController(context))
                        }
                    }
                }
                method {
                    name = "dispatchTouchEvent"
                }.hook {
                    before {
                        val mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(this.instance, DOUBLE_TAP_CONTROLLER) as? DoubleTapController ?: return@before
                        val motionEvent = this.args(0).cast<MotionEvent>() ?: return@before
                        if (!mDoubleTapControllerEx.isDoubleTapEvent(motionEvent)) return@before
                        val currentCellLayout = getCurrentCellLayoutMethod?.invoke(this.instance)
                        if (
                            currentCellLayout == null ||
                            (lastDownOnOccupiedCellMethod?.invoke(currentCellLayout) as? Boolean) != false ||
                            (isInNormalEditingModeMethod?.invoke(this.instance) as? Boolean) != false
                        ) {
                            return@before
                        }
                        (this.instance as? View)?.context?.sendBroadcast(
                            Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE")
                                .putExtra(
                                    "com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID",
                                    10
                                )
                        )
                    }
                }
            }
        }
    }
}