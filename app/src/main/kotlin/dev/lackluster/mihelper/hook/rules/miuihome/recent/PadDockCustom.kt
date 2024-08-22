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

package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.view.MotionEvent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.abs

object PadDockCustom : YukiBaseHooker() {
    private val dockDuration by lazy {
        Prefs.getInt(Pref.Key.MiuiHome.PAD_DOCK_TIME_DURATION, 180)
    }
    private val dockSafeHeight by lazy {
        Prefs.getInt(Pref.Key.MiuiHome.PAD_DOCK_SAFE_AREA_HEIGHT, 300)
    }

    override fun onHook() {
        if (dockDuration != 180 && Device.isPad) {
            "com.miui.home.recents.DockGestureHelper".toClass().method {
                name = "dispatchTouchEvent"
            }.hook {
                replaceUnit {
                    val motionEvent = this.args(0).any() as MotionEvent
                    val dockController = this.instance.current().method { name = "getDockController" }.call() ?: return@replaceUnit
                    if (motionEvent.actionMasked == 3 || motionEvent.actionMasked == 1) {
                        this.instance.current().field { name = "mTransitionYStyle" }.any()
                            ?.current()?.method { name = "cancel"; superClass() }?.call()
                        this.instance.current().field { name = "mIsDockTransitionAnimStart" }.setFalse()
                    }
                    val isFloatingDockShowing = dockController.current().method { name = "isFloatingDockShowing" }.boolean()
                    if (!isFloatingDockShowing) {
                        if (motionEvent.eventTime - motionEvent.downTime >= dockDuration) {
                            if (motionEvent.actionMasked == 3 || motionEvent.actionMasked == 1) {
                                dockController.current().method { name = "dispatchUpEvent" }.call(
                                    motionEvent,
                                    this.instance.current().field { name = "mTouchTracker" }.any()
                                        ?.current()?.method { name = "getUpType" }?.int()
                                )
                            } else {
                                this.instance.current().method { name = "animationTransitionDock" }.call(motionEvent, dockController)
                                dockController.current().method { name = "addMovement" }.call(motionEvent)
                                dockController.current().method { name = "updateLeaveSafeAreaStatus" }.call(
                                    motionEvent.rawX, motionEvent.rawY, true
                                )
                            }
                        } else {
                            dockController.current().method { name = "addMovement" }.call(motionEvent)
                            dockController.current().method { name = "updateLeaveSafeAreaStatus" }.call(
                                motionEvent.rawX, motionEvent.rawY, false
                            )
                        }
                    }
                    if (!this.instance.current().field { name = "isStartedGesture" }.boolean()) {
                        if (
                            motionEvent.actionMasked == 2 && (
                                    dockController.current().method { name = "isLeaveSafeArea" }.boolean() ||
                                            this.instance.current().field { name = "mTouchTracker" }.any()
                                                ?.current()?.method { name = "isTaskStartMove" }?.boolean(motionEvent.rawY) == true
                                    )
                        ) {
                            this.instance.current().method { name = "startGestureModeGesture" }.call(0)
                        } else if (
                            (motionEvent.actionMasked == 1 || motionEvent.actionMasked == 3) &&
                            this.instance.current().field { name = "mTouchTracker" }.any()
                                ?.current()?.method { name = "getUpType" }?.int() == 5
                        ) {
                            this.instance.current().method { name = "startGestureModeGesture" }.call(1)
                        }
                    }
                    if (this.instance.current().field { name = "isStartedGesture" }.boolean()) {
                        this.instance.current().field { name = "mGestureInputHelper" }.any()
                            ?.current()?.method { name = "dispatchGestureModeTouchEvent"; superClass() }?.call(motionEvent)
                    }
                }
            }
        }
        if (dockSafeHeight != 300 && Device.isPad) {
            "com.miui.home.launcher.dock.DockTouchHelper".toClass().method {
                name = "updateLeaveSafeAreaStatus"
            }.hook {
                replaceUnit {
                    val rawX = this.args(0).float()
                    val rawY = this.args(1).float()
                    val flag = this.args(2).boolean()
                    val mDownX = this.instance.current().field { name = "mDownX" }.int()
                    val mDownY = this.instance.current().field { name = "mDownY" }.int()
                    this.instance.current().field { name = "mSafeAreaOffsetX" }.set(abs(rawY.toInt() - mDownY) * 1.2f)
                    if (this.instance.current().field { name = "isLeaveSafeArea" }.boolean()) {
                        return@replaceUnit
                    }
                    if (abs(rawX - mDownX) > this.instance.current().field { name = "mSafeAreaOffsetX" }.float()) {
                        this.instance.current().field { name = "isLeaveSafeArea" }.setTrue()
                        this.instance.current().field { name = "mLeaveSafeAreaOffsetY" }.set(abs(mDownY - rawY))
                        this.instance.current().field { name = "isLeaveSafeAreaFromY" }.setFalse()
                        this.instance.current().field { name = "mLeaveSafeAreaX" }.set(rawX)
                        return@replaceUnit
                    }
                    val mDockAppearancePosition = this.instance.current().field { name = "mDockAppearancePosition" }.int()
                    if (abs(rawY.toInt() - mDownY) > mDockAppearancePosition + dockSafeHeight) {
                        this.instance.current().field { name = "isLeaveSafeArea" }.setTrue()
                        this.instance.current().field { name = "mLeaveSafeAreaOffsetY" }.set(mDockAppearancePosition)
                        this.instance.current().field { name = "isLeaveSafeAreaFromY" }.setTrue()
                        this.instance.current().field { name = "mLeaveSafeAreaX" }.set(rawX)
                        return@replaceUnit
                    } else if (
                        !flag || this.instance.current().field { name = "isLeaveSafeArea" }.boolean() ||
                        this.instance.current().field { name = "mIsDockStartMoving" }.boolean() || abs(rawY.toInt() - mDownY) <= 40
                    ) {
                        return@replaceUnit
                    } else {
                        if (this.instance.current().field { name = "mDockStateMachine" }.any()
                                ?.current()?.method { name = "isFloatingDockShowing" }?.boolean() != true
                            && this.instance.current().field { name = "mNeedDockDoMove" }.boolean()
                        ) {
                            this.instance.current().field { name = "mDockStateMachine" }.any()?.current()?.method {
                                name = "sendMessage"
                                paramCount = 1
                                superClass()
                            }?.call(0)
                        }
                        this.instance.current().field { name = "mIsDockStartMoving" }.setTrue()
                    }
                }
            }
        }
    }
}