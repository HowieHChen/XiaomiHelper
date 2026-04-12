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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.PairCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.utils.CommonGesture
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.v

object StatusBarTapGesture : StaticHooker() {
    private var View.gestureDetector by extraOf<GestureDetector>("SIMPLE_GESTURE_DETRCTOR")

    private val doubleTapGesture by Preferences.SystemUI.StatusBar.DOUBLE_TAP_GESTURE.lazyGet()
    private val singleTapGesture by Preferences.SystemUI.StatusBar.SINGLE_TAP_GESTURE.lazyGet()

    private val clzStatusBarClickTool by "com.miui.systemui.statusbar.StatusBarClickTool".lazyClassOrNull()
    private val fldApplication by lazy {
        "com.android.systemui.SystemUIApplication".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "sContext"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Context>()
    }

    override fun onInit() {
        updateSelfState(doubleTapGesture != 0 || singleTapGesture != 0)
    }

    override fun onHook() {
        if (clzStatusBarClickTool != null) {
            var lastClickTime = 0L
            "com.miui.systemui.statusbar.StatusBarClickTool".toClassOrNull()?.apply {
                val fldCommandQueue = resolve().firstFieldOrNull {
                    name = "commandQueue"
                }?.toTyped<Any>()
                val fldDisableState = "".toClassOrNull()?.let {
                    resolve().firstFieldOrNull {
                        name = "disableState"
                    }?.toTyped<Any>()
                }
                resolve().firstMethodOrNull {
                    name = "invokeInputManager"
                }?.hook {
                    val isDoubleTap = getArg(0) as? Boolean ?: false
                    val action = if (isDoubleTap) doubleTapGesture else singleTapGesture
                    if (action != 0) {
                        val disableState = fldCommandQueue?.get(thisObject)?.let { queue ->
                            fldDisableState?.get(queue)?.let {
                                ReadonlyStateFlowCompat<Any>().of(it).getInternalMutableStateFlowCompat().getValue()
                            }?.let {
                                PairCompat.getFirst(it) as? Int
                            }
                        }
                        if (
                            disableState != null &&
                            disableState and 0x800000 != 0 && disableState and 0x100000 != 0 && disableState and 0x20000 != 0
                        ) {
                            v { "disable all, ignore click" }
                            return@hook result(null)
                        }
                        val currentTime = SystemClock.elapsedRealtime()
                        if (currentTime - lastClickTime < 600L) {
                            v { "clicked too fast, ignore" }
                            return@hook result(null)
                        }
                        lastClickTime = currentTime

                        val context = fldApplication?.get(null)?.applicationContext
                        if (context != null) {
                            CommonGesture.doAction(context, action)
                        }
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
            }
        } else {
            "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView".toClass().apply {
                resolve().firstMethodOrNull {
                    name = "onFinishInflate"
                }?.hook {
                    val view = thisObject as? ViewGroup
                    if (view != null && view.gestureDetector == null) {
                        val detector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
                            override fun onDown(e: MotionEvent): Boolean {
                                return true
                            }

                            override fun onDoubleTap(e: MotionEvent): Boolean {
                                CommonGesture.doAction(view.context.applicationContext, doubleTapGesture)
                                return true
                            }

                            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                CommonGesture.doAction(view.context.applicationContext, singleTapGesture)
                                return true
                            }
                        })
                        view.gestureDetector = detector
                        @SuppressLint("ClickableViewAccessibility")
                        view.setOnTouchListener { _, event ->
                            detector.onTouchEvent(event)
                            false
                        }
                    }
                    result(proceed())
                }
            }
        }
    }
}