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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RecentCardAnim : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_CARD_ANIM) {
            val swipeHelperForRecentsCls = "com.miui.home.recents.views.SwipeHelperForRecents".toClass()
            val taskStackViewLayoutStyleHorizontalCls = "com.miui.home.recents.TaskStackViewLayoutStyleHorizontal".toClass()
            val verticalSwipeCls = "com.miui.home.recents.views.VerticalSwipe".toClass()
            val sineEaseInOutConstructor = "miuix.view.animation.SineEaseInOutInterpolator".toClass().constructor().get()
            val getScreenHeightMethod = "com.miui.home.launcher.DeviceConfig".toClass().method {
                name = "getScreenHeight"
                modifiers { isStatic }
            }.get()
            val getAsScreenHeightWhenDismissMethod = verticalSwipeCls.method {
                name = "getAsScreenHeightWhenDismiss"
                modifiers { isStatic }
            }.get()

            swipeHelperForRecentsCls.method {
                name = "isScaleSmallEnoughForDismiss"
            }.hook {
                replaceAny {
                    this.instance.current().method {
                        name = "isSwipedFarEnoughForDismiss"
                        superClass()
                    }.boolean()
                }
            }
            taskStackViewLayoutStyleHorizontalCls.method {
                name = "createScaleDismissAnimation"
                param(ViewClass, FloatType)
            }.hook {
                replaceAny {
                    val view = this.args(0).any() as View
                    val getScreenHeight = getScreenHeightMethod.int()
                    val easeInOutInterpolator = sineEaseInOutConstructor.newInstance<Interpolator>()
                    ObjectAnimator.ofFloat(
                        view,
                        View.TRANSLATION_Y,
                        view.translationY,
                        -getScreenHeight * 1.15f
                    ).apply {
                        interpolator = easeInOutInterpolator
                        duration = 300L
                    }
                }
            }
            verticalSwipeCls.apply {
                constructor().hook {
                    after {
                        this.instance.current().field {
                            name = "mCurScale"
                        }.set(1.0f)
                    }
                }
                method {
                    name = "calculate"
                    param(FloatType)
                }.hook {
                    replaceUnit {
                        val f = this.args(0).float()
                        val alpha: Float
                        val transY: Float
                        if (f <= 0.0f) {
                            val asScreenHeightWhenDismiss = getAsScreenHeightWhenDismissMethod.int()
                            alpha = (1.0f + f / asScreenHeightWhenDismiss).coerceIn(0.0f, 1.0f)
                            transY = f
                        } else {
                            val mCanLockTaskView = this.instance.current().field {
                                name = "mCanLockTaskView"
                            }.boolean()
                            alpha = 1.0f
                            transY = f / if (mCanLockTaskView) 3.0f else 6.0f
                        }
                        this.instance.current().field {
                            name = "mCurAlpha"
                        }.set(alpha)
                        this.instance.current().field {
                            name = "mCurTransY"
                        }.set(transY)
                    }
                }
            }
        }
    }
}