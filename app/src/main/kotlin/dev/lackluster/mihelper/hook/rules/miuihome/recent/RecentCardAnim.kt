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

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Interpolator
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import kotlin.math.abs

object RecentCardAnim : StaticHooker() {
    private val metGetScreenHeight by lazy {
        "com.miui.home.common.device.DeviceConfigs".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getScreenHeight"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Int>()
    }
    private val ctorPhysicBasedInterpolator by lazy {
        "com.miui.home.launcher.anim.PhysicBasedInterpolator".toClassOrNull()?.resolve()?.firstConstructorOrNull {
            parameterCount = 2
        }?.toTyped()
    }

    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.OPT_RECENT_CARD_ANIM.get())
    }

    override fun onHook() {
        "com.miui.home.recents.views.SwipeHelperForRecents".toClassOrNull()?.apply {
            val mCurrView = resolve().firstFieldOrNull {
                name = "mCurrView"
            }?.toTyped<View>()
            resolve().firstMethodOrNull {
                name = "isScaleSmallEnoughForDismiss"
            }?.hook {
                val result = mCurrView?.get(thisObject)?.let {
                    abs(it.translationY) > (it.measuredHeight * 0.8f)
                } ?: false
                result(result)
            }
        }
        "com.miui.home.recents.TaskStackViewLayoutStyleHorizontal".toClassOrNull()?.apply {
            resolve().optional(true).firstMethodOrNull {
                name = "createSwipeAnimation"
                parameters(View::class, Float::class)
            }?.hook {
                val view = getArg(0) as? View
                if (view != null) {
                    createSwipeAnimation(view, 450L)?.let {
                        return@hook result(it)
                    }
                }
                result(proceed())
            }
            resolve().optional(true).firstMethodOrNull {
                name = "createScaleDismissAnimation"
                parameters(View::class, Float::class)
            }?.hook {
                val view = getArg(0) as? View
                if (view != null) {
                    createSwipeAnimation(view, 450L)?.let {
                        return@hook result(it)
                    }
                }
                result(proceed())
            }
        }
        "com.miui.home.recents.TaskStackViewLayoutStyleStack".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "createSwipeAnimation"
            }?.hook {
                val view = getArg(0) as? View
                if (view != null) {
                    createSwipeAnimation(view, 550L)?.let {
                        return@hook result(it)
                    }
                }
                result(proceed())
            }
            resolve().firstMethodOrNull {
                name = "createCleanDismissAnimation"
            }?.hook {
                val view = getArg(0) as? View
                if (view != null) {
                    createSwipeAnimation(view, 550L)?.let {
                        return@hook result(it)
                    }
                }
                result(proceed())
            }
        }
        "com.miui.home.recents.views.VerticalSwipe".toClassOrNull()?.apply {
            val mCanLockTaskView = resolve().firstFieldOrNull {
                name = "mCanLockTaskView"
            }?.toTyped<Boolean>()
            val mCurAlpha = resolve().firstFieldOrNull {
                name = "mCurAlpha"
            }?.toTyped<Float>()
            val mCurScale = resolve().firstFieldOrNull {
                name = "mCurScale"
            }?.toTyped<Float>()
            val mCurTransY = resolve().firstFieldOrNull {
                name = "mCurTransY"
            }?.toTyped<Float>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                mCurAlpha?.set(thisObject, 1.0f)
                mCurScale?.set(thisObject, 1.0f)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "calculate"
                parameters(Float::class)
            }?.hook {
                val f = getArg(0) as? Float ?: 0.0f
                val transY: Float
                if (f <= 0.0f) {
                    transY = f
                } else {
                    val canLockTaskView = mCanLockTaskView?.get(thisObject) ?: false
                    transY = f / if (canLockTaskView) 3.0f else 6.0f
                }
                mCurTransY?.set(thisObject, transY)
                result(null)
            }
        }
        "com.miui.home.recents.views.VerticalSwipeForStack".toClassOrNull()?.apply {
            val mCanLockTaskView = resolve().firstFieldOrNull {
                name = "mCanLockTaskView"
                superclass()
            }?.toTyped<Boolean>()
            val mCurAlpha = resolve().firstFieldOrNull {
                name = "mCurAlpha"
                superclass()
            }?.toTyped<Float>()
            val mCurScale = resolve().firstFieldOrNull {
                name = "mCurScale"
                superclass()
            }?.toTyped<Float>()
            val mCurTransY = resolve().firstFieldOrNull {
                name = "mCurTransY"
                superclass()
            }?.toTyped<Float>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                mCurAlpha?.set(thisObject, 1.0f)
                mCurScale?.set(thisObject, 1.0f)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "calculate"
                parameters(Float::class)
            }?.hook {
                val f = getArg(0) as? Float ?: 0.0f
                val transY: Float
                if (f <= 0.0f) {
                    transY = f
                } else {
                    val canLockTaskView = mCanLockTaskView?.get(thisObject) ?: false
                    transY = f / if (canLockTaskView) 3.0f else 6.0f
                }
                mCurTransY?.set(thisObject, transY)
                result(null)
            }
        }
    }

    private fun createSwipeAnimation(view: View, duration: Long): ObjectAnimator? {
        val getScreenHeight = metGetScreenHeight?.invoke(null)?.toFloat() ?: return null
        val physicBasedInterpolator = ctorPhysicBasedInterpolator?.newInstance(0.72f, 0.72f) as? Interpolator ?: return null
        return ObjectAnimator.ofFloat(
            view,
            View.TRANSLATION_Y,
            view.translationY,
            -getScreenHeight
        ).apply {
            this.interpolator = physicBasedInterpolator
            this.duration = duration
        }
    }
}