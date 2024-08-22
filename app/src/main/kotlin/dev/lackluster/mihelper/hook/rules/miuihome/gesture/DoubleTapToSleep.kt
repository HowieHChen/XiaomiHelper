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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.MotionEventClass
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DoubleTapToSleep : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.DOUBLE_TAP_TO_SLEEP) {
            "com.miui.home.launcher.Workspace".toClass().constructor().giveAll().hookAll {
                after {
                    var mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(this.instance, "mDoubleTapControllerEx")
                    if (mDoubleTapControllerEx != null) return@after
                    mDoubleTapControllerEx = DoubleTapController((this.args(0).any() as Context))
                    XposedHelpers.setAdditionalInstanceField(
                        this.instance,
                        "mDoubleTapControllerEx",
                        mDoubleTapControllerEx
                    )
                }
            }
            "com.miui.home.launcher.Workspace".toClass().method {
                name = "dispatchTouchEvent"
                param(MotionEventClass)
            }.hook {
                before {
                    val mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(this.instance, "mDoubleTapControllerEx") as DoubleTapController
                    if (!mDoubleTapControllerEx.isDoubleTapEvent(this.args(0).any() as MotionEvent)) return@before
                    val mCurrentScreenIndex = this.instance.current().field {
                        name = "mCurrentScreenIndex"
                        superClass()
                    }.int()
                    val cellLayout = this.instance.current().method {
                        name = "getCellLayout"
                        superClass()
                    }.call(mCurrentScreenIndex)
                    if (cellLayout != null)
                        if (
                            cellLayout.current().method {
                                name = "lastDownOnOccupiedCell"
                            }.boolean()
                        ) return@before
                    if (
                        this.instance.current().method {
                            name = "isInNormalEditingMode"
                        }.boolean()
                    ) return@before
                    val context = this.instance.current().method {
                        name = "getContext"
                        superClass()
                    }.call() as Context
                    context.sendBroadcast(
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