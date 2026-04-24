/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

import android.view.View
import android.view.WindowManager
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import kotlin.math.abs
import kotlin.math.min

object PredictiveBackProgress : StaticHooker() {
    private val clzGestureStubView by "com.miui.home.recents.GestureStubView".lazyClassOrNull()
    private val clzGestureBackArrowView by "com.miui.home.recents.GestureBackArrowView".lazyClassOrNull()
    
    private val metOnSwipeProgress by lazy {
        clzGestureBackArrowView?.resolve()?.firstMethodOrNull {
            name = "onSwipeProgress"
            parameters(Float::class)
        }?.toTyped<Unit>()
    }
    private val metOnBackProgressed by lazy {
        "com.miui.home.recents.OnBackInvokedCallbackController".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "onBackProgressed"
        }?.toTyped<Unit>()
    }
    private val metGetInstanceBackMotionEventProvider by lazy {
        "android.window.BackMotionEventProvider".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getInstance"
            parameterCount = 7
            modifiers(Modifiers.STATIC)
        }?.toTyped<Any>()
    }

    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.FIX_PREDICTIVE_BACK_PROG.get())
    }

    override fun onHook() {
        $$"com.miui.home.recents.GestureStubView$3".toClassOrNull()?.apply {
            val fldGestureStubView = resolve().firstFieldOrNull {
                type("com.miui.home.recents.GestureStubView")
            }?.toTyped<View>()
            val fldCurrX = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mCurrX"
            }?.toTyped<Float>()
            val fldCurrY = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mCurrY"
            }?.toTyped<Float>()
            val fldDownX = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mDownX"
            }?.toTyped<Float>()
            val fldGestureBackArrowView = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mGestureBackArrowView"
            }?.toTyped<View>()
            val fldGestureStubPos = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mGestureStubPos"
            }?.toTyped<Int>()
            val fldOnBackInvokedCallbackController = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mOnBackInvokedCallbackController"
            }?.toTyped<Any>()
            val fldWindowManager = clzGestureStubView?.resolve()?.firstFieldOrNull {
                name = "mWindowManager"
            }?.toTyped<WindowManager>()
            resolve().firstMethodOrNull {
                name = "onSwipeProcess"
            }?.hook {
                val progress = getArg(0) as? Float
                val gestureStubView = fldGestureStubView?.get(thisObject) ?: return@hook result(proceed())
                val onBackInvokedCallbackController = fldOnBackInvokedCallbackController?.get(gestureStubView)
                val mCurrX = fldCurrX?.get(gestureStubView)
                val mCurrY = fldCurrY?.get(gestureStubView)
                val mDownX = fldDownX?.get(gestureStubView)
                val mGestureStubPos = fldGestureStubPos?.get(gestureStubView)
                val windowManager = fldWindowManager?.get(gestureStubView)
                if (
                    progress != null && onBackInvokedCallbackController != null && windowManager != null &&
                    mCurrX != null && mCurrY != null && mDownX != null && mGestureStubPos != null
                ) {
                    fldGestureBackArrowView?.get(gestureStubView)?.let { gestureBackArrowView ->
                        metOnSwipeProgress?.invoke(gestureBackArrowView, progress)
                    }
                    val fullyStretchedThreshold = windowManager.maximumWindowMetrics.let { windowMetrics ->
                        min(
                            windowMetrics.bounds.width().toFloat(), // screen width
                            windowMetrics.density * 412.0f // R.dimen.navigation_edge_action_progress_threshold
                        )
                    }
                    metGetInstanceBackMotionEventProvider?.invoke(
                        null,
                        mCurrX,
                        mCurrY,
                        (abs(mCurrX - mDownX) / fullyStretchedThreshold).coerceIn(0.0f, 1.0f),
                        0.0f,
                        0.0f,
                        if (mGestureStubPos == 0) 0 else 1,
                        null
                    )?.let { backMotionEvent ->
                        metOnBackProgressed?.invoke(onBackInvokedCallbackController, backMotionEvent)
                    }
                    result(null)
                } else {
                    result(proceed())
                }
            }
        }
    }
}