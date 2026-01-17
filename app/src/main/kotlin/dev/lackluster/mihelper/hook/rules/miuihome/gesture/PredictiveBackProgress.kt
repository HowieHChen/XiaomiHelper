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
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable
import kotlin.math.abs
import kotlin.math.min

object PredictiveBackProgress : YukiBaseHooker() {
    private val clzGestureStubView by lazy {
        "com.miui.home.recents.GestureStubView".toClassOrNull()
    }
    private val clzGestureBackArrowView by lazy {
        "com.miui.home.recents.GestureBackArrowView".toClassOrNull()
    }
    private val metOnSwipeProgress by lazy {
        clzGestureBackArrowView?.resolve()?.firstMethodOrNull {
            name = "onSwipeProgress"
            parameters(Float::class)
        }?.self?.apply { makeAccessible() }
    }
    private val metOnBackProgressed by lazy {
        "com.miui.home.recents.OnBackInvokedCallbackController".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "onBackProgressed"
        }?.self?.apply { makeAccessible() }
    }
    private val metGetInstanceBackMotionEventProvider by lazy {
        "android.window.BackMotionEventProvider".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getInstance"
            parameterCount = 7
            modifiers(Modifiers.STATIC)
        }
    }

    override fun onHook() {
        hasEnable (Pref.Key.MiuiHome.FIX_PREDICTIVE_BACK_PROG) {
            $$"com.miui.home.recents.GestureStubView$3".toClassOrNull()?.apply {
                val fldGestureStubView = resolve().firstFieldOrNull {
                    type("com.miui.home.recents.GestureStubView")
                }?.self?.apply { makeAccessible() }
                val fldCurrX = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mCurrX"
                }?.self?.apply { makeAccessible() }
                val fldCurrY = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mCurrY"
                }?.self?.apply { makeAccessible() }
                val fldDownX = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mDownX"
                }?.self?.apply { makeAccessible() }
                val fldGestureBackArrowView = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mGestureBackArrowView"
                }?.self?.apply { makeAccessible() }
                val fldGestureStubPos = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mGestureStubPos"
                }?.self?.apply { makeAccessible() }
                val fldOnBackInvokedCallbackController = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mOnBackInvokedCallbackController"
                }?.self?.apply { makeAccessible() }
                val fldWindowManager = clzGestureStubView?.resolve()?.firstFieldOrNull {
                    name = "mWindowManager"
                }?.self?.apply { makeAccessible() }
                resolve().firstMethodOrNull {
                    name = "onSwipeProcess"
                }?.hook {
                    before {
                        val progress = this.args(0).float()
                        val gestureStubView = fldGestureStubView?.get(this.instance) as? View ?: return@before
                        val onBackInvokedCallbackController = fldOnBackInvokedCallbackController?.get(gestureStubView) ?: return@before
                        val mCurrX = fldCurrX?.getFloat(gestureStubView) ?: return@before
                        val mCurrY = fldCurrY?.getFloat(gestureStubView) ?: return@before
                        val mDownX = fldDownX?.getFloat(gestureStubView) ?: return@before
                        val mGestureStubPos = fldGestureStubPos?.getInt(gestureStubView) ?: return@before
                        val windowManager = fldWindowManager?.get(gestureStubView) as? WindowManager ?: return@before
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
                        this.result = null
                    }
                }
            }
        }
    }
}