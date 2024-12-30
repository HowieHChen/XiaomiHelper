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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import cn.fkj233.ui.activity.dp2px
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.utils.SpringInterpolator
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import java.util.ArrayList

object CarrierText : YukiBaseHooker() {
    private const val CUSTOM_VIEW_ID = 0x00111111
    private val darkCarrierTextClass by lazy {
        "com.android.systemui.statusbar.views.DarkCarrierText".toClass()
    }
    private val mCarrierTextControllerField by lazy {
        "com.android.keyguard.CarrierText".toClass().field {
            name = "mCarrierTextController"
        }
    }
    private val miuiClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClass()
    }
    private val getTintMethod by lazy {
        "com.android.systemui.plugins.DarkIconDispatcher".toClass().method {
            name = "getTint"
            modifiers { isStatic }
        }.get()
    }
    private val isInAreasMethod by lazy {
        "com.android.systemui.plugins.DarkIconDispatcher".toClass().method {
            name = "isInAreas"
            param(Collection::class.java, ViewClass)
            modifiers { isStatic }
        }.get()
    }
    private val carrierTextType = Prefs.getInt(Pref.Key.SystemUI.LockScreen.CARRIER_TEXT, 0)
    private val onDarkChangedMethod by lazy {
        "com.android.systemui.plugins.DarkIconDispatcher\$DarkReceiver".toClass().method {
            name = "onDarkChanged"
            paramCount = 6
        }.give()
    }

    override fun onHook() {
        if (carrierTextType != 0) {
            val targetClass = if (carrierTextType == 1) darkCarrierTextClass else miuiClockClass
            targetClass.apply {
                method {
                    name = "onDarkChanged"
                    paramCount = 6
                }.hook {
                    before {
                        val textView = this.instance<TextView>()
                        if (textView.tag == null) return@before
                        val useTint = this.args(5).boolean()
                        if (useTint && textView.tag != "FROM_FULL_AOD") {
                            this.result = null
                            return@before
                        }
                        val tintAreas = this.args(0).any()
                        val darkIntensity = this.args(1).float()
                        val tintColor = this.args(2).int()
                        val lightColor = this.args(3).int()
                        val darkColor = this.args(4).int()
                        XposedHelpers.setAdditionalInstanceField(textView, "mTintAreas", tintAreas)
                        XposedHelpers.setAdditionalInstanceField(textView, "mDarkIntensity", darkIntensity)
                        XposedHelpers.setAdditionalInstanceField(textView, "mTintColor", tintColor)
                        val mUseTint = XposedHelpers.getAdditionalInstanceField(textView, "mUseTint") as? Boolean
                        val mLightColor = XposedHelpers.getAdditionalInstanceField(textView, "mLightColor") as? Int
                        val mDarkColor = XposedHelpers.getAdditionalInstanceField(textView, "mDarkColor") as? Int
                        if (mUseTint != useTint || mLightColor != lightColor || mDarkColor != darkColor) {
                            XposedHelpers.setAdditionalInstanceField(textView, "mUseTint", useTint)
                            XposedHelpers.setAdditionalInstanceField(textView, "mLightColor", lightColor)
                            XposedHelpers.setAdditionalInstanceField(textView, "mDarkColor", darkColor)
                            XposedHelpers.setAdditionalInstanceField(textView, "mDark", 0)
                        }
                        if (useTint) {
                            textView.setTextColor(getTintMethod.int(tintAreas, textView, tintColor))
                        } else {
                            val mDark = XposedHelpers.getAdditionalInstanceField(textView, "mDark")
                            val i = if (isInAreasMethod.boolean(tintAreas, textView) && darkIntensity > 0) {
                                2
                            } else {
                                1
                            }
                            if (mDark != i) {
                                XposedHelpers.setAdditionalInstanceField(textView, "mDark", i)
                                textView.setTextColor(
                                    if (i == 2) darkColor
                                    else lightColor
                                )
                            }
                        }
                        this.result = null
                    }
                }
            }
            "com.android.systemui.statusbar.phone.MiuiKeyguardStatusBarView".toClass().apply {
                method {
                    name = "animateFullAod"
                }.ignored().hook {
                    after {
                        val view = this.instance as View
                        val toLock = this.args(0).boolean()
                        val clockView = view.findViewById<TextView>(CUSTOM_VIEW_ID) ?: return@after
                        val animFlag: Boolean
                        if (!toLock && XposedHelpers.getAdditionalInstanceField(clockView, "mDark") != 1) {
                            XposedHelpers.setAdditionalInstanceField(clockView, "mAnimToAod", true)
                            animFlag = false
                            // doAnimateColor(false)
                        } else if (toLock && XposedHelpers.getAdditionalInstanceField(clockView, "mAnimToAod") == true) {
                            animFlag = true
                            // doAnimateColor(true)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mAnimToAod", false)
                        } else {
                            return@after
                        }
                        val spring = SpringInterpolator(0.95f, 0.35f)
                        (if (animFlag) ValueAnimator.ofFloat(0.0f, 1.0f)
                        else ValueAnimator.ofFloat(1.0f, 0.0f)).apply {
                            interpolator = spring
                            duration = spring.duration
                            addListener(
                                onStart = {
                                    clockView.tag = "FROM_FULL_AOD"
                                },
                                onEnd = {
                                    clockView.tag = CUSTOM_VIEW_ID
                                }
                            )
                            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                                private var mLastV = -1.0f
                                override fun onAnimationUpdate(p0: ValueAnimator) {
                                    val value = (p0.animatedValue as? Float ?: return).coerceIn(0.0f, 1.0f)
                                    if (value == mLastV) return
                                    mLastV = value
                                    if (value != 0.0f && value != 1.0f) {
                                        val f = (XposedHelpers.getAdditionalInstanceField(clockView, "mDarkIntensity") as? Float ?: 0.0f) - value
                                        if (f <= 0.01f && f >= -0.01f) {
                                            return
                                        }
                                    }
                                    val mTintAreas = XposedHelpers.getAdditionalInstanceField(clockView, "mTintAreas")
                                    val mLightColor = XposedHelpers.getAdditionalInstanceField(clockView, "mLightColor") as? Int
                                    val mDarkColor = XposedHelpers.getAdditionalInstanceField(clockView, "mDarkColor") as? Int
                                    onDarkChangedMethod?.invoke(
                                        clockView,
                                        mTintAreas,
                                        value,
                                        ArgbEvaluator().evaluate(value, mLightColor, mDarkColor) as Int,
                                        mLightColor,
                                        mDarkColor,
                                        true
                                    )
                                }
                            })
                        }.start()
                    }
                }
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        val view = this.instance as View
                        val carrierText = view.findViewById<TextView>(ResourcesUtils.keyguard_carrier_text) ?: return@after
                        val context = view.context
                        val parent = carrierText.parent as? ViewGroup ?: return@after
                        if (carrierTextType == 1) {
                            val darkCarrierText = darkCarrierTextClass.constructor {
                                paramCount = 1
                            }.get().call(context) as? TextView
                            val mCarrierTextController = mCarrierTextControllerField.get(carrierText).any()
                            if (darkCarrierText == null || mCarrierTextController == null) {
                                YLog.warn("mCarrierTextController is null")
                                carrierText.visibility = View.VISIBLE
                                return@after
                            }
                            darkCarrierText.setTextAppearance(ResourcesUtils.TextAppearance_StatusBar_Clock)
                            darkCarrierText.ellipsize = TextUtils.TruncateAt.MARQUEE
                            darkCarrierText.gravity = Gravity.CENTER_VERTICAL
                            darkCarrierText.maxWidth = dp2px(context, 100.0f)
                            darkCarrierText.isSingleLine = true
                            darkCarrierText.marqueeRepeatLimit = 1
                            darkCarrierText.textDirection = View.TEXT_DIRECTION_LOCALE
                            darkCarrierText.isSelected = true
                            darkCarrierText.isHorizontalFadingEdgeEnabled = true
                            darkCarrierText.id = CUSTOM_VIEW_ID
                            darkCarrierText.tag = CUSTOM_VIEW_ID
                            mCarrierTextControllerField.get(darkCarrierText).set(mCarrierTextController)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mTintAreas", ArrayList<Rect>())
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mDarkIntensity", 0.0f)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mTintColor", 0)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mUseTint", false)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mLightColor", 0)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mDarkColor", 0)
                            XposedHelpers.setAdditionalInstanceField(darkCarrierText, "mDark", 0)
                            if (parent is FrameLayout) {
                                parent.addView(
                                    darkCarrierText,
                                    parent.indexOfChild(carrierText)
                                )
                                parent.removeView(carrierText)
                            }
                        } else if (carrierTextType == 2) {
                            val clockContainer = LinearLayout(context).apply {
                                clipChildren = false
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            }
                            val padClock = LayoutInflater.from(context).inflate(ResourcesUtils.pad_clock_xml, null) as TextView
                            val clockView = miuiClockClass.constructor {
                                paramCount = 2
                            }.get().call(context, null) as TextView
                            clockView.setTextAppearance(ResourcesUtils.TextAppearance_StatusBar_Clock)
                            clockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, padClock.textSize)
                            clockView.typeface = padClock.typeface
                            clockView.gravity = Gravity.CENTER or Gravity.START
                            clockView.isSingleLine = true
                            clockView.id = CUSTOM_VIEW_ID
                            clockView.tag = CUSTOM_VIEW_ID
                            XposedHelpers.setAdditionalInstanceField(clockView, "mTintAreas", ArrayList<Rect>())
                            XposedHelpers.setAdditionalInstanceField(clockView, "mDarkIntensity", 0.0f)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mTintColor", 0)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mUseTint", false)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mLightColor", 0)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mDarkColor", 0)
                            XposedHelpers.setAdditionalInstanceField(clockView, "mDark", 1)
                            val clockMarginStart = ResourcesUtils.status_bar_padding_extra_start.takeIf { it != 0 }?.let {
                                context.resources.getDimensionPixelSize(it)
                            } ?: 0
                            val clockMarginEnd = ResourcesUtils.status_bar_clock_margin_end.takeIf { it != 0 }?.let {
                                context.resources.getDimensionPixelSize(it)
                            } ?: dp2px(context, 4.0f)
                            clockContainer.addView(
                                clockView,
                                ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                ).apply {
                                    marginStart = clockMarginStart
                                    marginEnd = clockMarginEnd
                                }
                            )
                            if (Device.isPad)
                                clockContainer.addView(
                                    padClock,
                                    ViewGroup.MarginLayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    ).apply {
                                        marginStart = clockMarginStart
                                        marginEnd = clockMarginEnd
                                    }
                                )
                            if (parent is FrameLayout) {
                                parent.addView(
                                    clockContainer,
                                    parent.indexOfChild(carrierText)
                                )
                                parent.removeView(carrierText)
                            }
                        }
                    }
                }
                method {
                    name = "updateIconsAndTextColors"
                }.hook {
                    before {
                        val textView = this.instance<View>().findViewById<TextView>(CUSTOM_VIEW_ID)
                        textView.id = ResourcesUtils.clock
                    }
                    after {
                        val textView = this.instance<View>().findViewById<TextView>(ResourcesUtils.clock)
                        textView.id = CUSTOM_VIEW_ID
                    }
                }
            }
        }
    }

}