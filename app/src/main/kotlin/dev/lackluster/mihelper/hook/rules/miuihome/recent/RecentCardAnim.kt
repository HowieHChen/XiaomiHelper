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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable
import kotlin.math.abs

object RecentCardAnim : YukiBaseHooker() {
    private val metGetScreenHeight by lazy {
        "com.miui.home.common.device.DeviceConfigs".toClass().resolve().firstMethod {
            name = "getScreenHeight"
            modifiers(Modifiers.STATIC)
        }
    }
    private val ctorPhysicBasedInterpolator by lazy {
        "com.miui.home.launcher.anim.PhysicBasedInterpolator".toClass().resolve().firstConstructor {
            parameterCount = 2
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_CARD_ANIM) {
            "com.miui.home.recents.views.SwipeHelperForRecents".toClassOrNull()?.apply {
                val mCurrView = resolve().firstFieldOrNull {
                    name = "mCurrView"
                }
                resolve().firstMethodOrNull {
                    name = "isScaleSmallEnoughForDismiss"
                }?.hook {
                    replaceAny {
                        mCurrView?.copy()?.of(this.instance)?.get<View>()?.let {
                            abs(it.translationY) > (it.measuredHeight * 0.8f)
                        } ?: false
                    }
                }
            }
            "com.miui.home.recents.TaskStackViewLayoutStyleHorizontal".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "createScaleDismissAnimation"
                    parameters(View::class, Float::class)
                }?.hook {
                    replaceAny {
                        val view = this.args(0).any() as View
                        val getScreenHeight = metGetScreenHeight.copy().invoke<Int>()?.toFloat() ?: 0.0f
                        val physicBasedInterpolator = ctorPhysicBasedInterpolator.copy().createAsType<Interpolator>(0.72f, 0.72f)
                        ObjectAnimator.ofFloat(
                            view,
                            View.TRANSLATION_Y,
                            view.translationY,
                            -getScreenHeight
                        ).apply {
                            interpolator = physicBasedInterpolator
                            duration = 450L
                        }
                    }
                }
            }
            "com.miui.home.recents.views.VerticalSwipe".toClassOrNull()?.apply {
                val mCanLockTaskView = resolve().firstFieldOrNull {
                    name = "mCanLockTaskView"
                }
                val mCurAlpha = resolve().firstFieldOrNull {
                    name = "mCurAlpha"
                }
                val mCurScale = resolve().firstFieldOrNull {
                    name = "mCurScale"
                }
                val mCurTransY = resolve().firstFieldOrNull {
                    name = "mCurTransY"
                }
                resolve().firstConstructor().hook {
                    after {
                        mCurAlpha?.copy()?.of(this.instance)?.set(1.0f)
                        mCurScale?.copy()?.of(this.instance)?.set(1.0f)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "calculate"
                    parameters(Float::class)
                }?.hook {
                    replaceUnit {
                        val f = this.args(0).float()
                        val transY: Float
                        if (f <= 0.0f) {
                            transY = f
                        } else {
                            val canLockTaskView = mCanLockTaskView?.copy()?.of(this.instance)?.get<Boolean>() == true
                            transY = f / if (canLockTaskView) 3.0f else 6.0f
                        }
                        mCurTransY?.copy()?.of(this.instance)?.set(transY)
                    }
                }
            }
        }
    }
}