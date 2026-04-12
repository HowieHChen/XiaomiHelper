/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.big_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.date_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.horizontal_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_date_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.pad_clock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiClock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.dp
import kotlin.math.abs

object MiuiClockFontWeight : StaticHooker() {
    // Padding
    private val valuePaddingStart by Preferences.SystemUI.StatusBar.Clock.PADDING_START_VAL.lazyGet()
    private val valuePaddingEnd by Preferences.SystemUI.StatusBar.Clock.PADDING_END_VAL.lazyGet()
    private val modifyPadding by Preferences.SystemUI.StatusBar.Clock.CUSTOM_HORIZON_PADDING.lazyGet()
    // Font Weight
    private val valueClockFW by Preferences.SystemUI.StatusBar.Font.CLOCK_WEIGHT.lazyGet()
    private val valuePadClockFW by Preferences.SystemUI.StatusBar.Font.PAD_CLOCK_WEIGHT.lazyGet()
    private val valueBigTimeFW by Preferences.SystemUI.StatusBar.Font.BIG_TIME_WEIGHT.lazyGet()
    private val valueDateTimeFW by Preferences.SystemUI.StatusBar.Font.DATE_TIME_WEIGHT.lazyGet()
    private val valueCCDateFW by Preferences.SystemUI.StatusBar.Font.CC_DATE_WEIGHT.lazyGet()
    private val valueHorizontalTimeFW by Preferences.SystemUI.StatusBar.Font.HORIZONTAL_TIME_WEIGHT.lazyGet()
    private val modifyClockFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_CLOCK.get() && valueClockFW in 1..1000
    }
    private val modifyPadClockFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_PAD_CLOCK.get() && valuePadClockFW in 1..1000
    }
    private val modifyBigTimeFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_BIG_TIME.get() && valueBigTimeFW in 1..1000
    }
    private val modifyDateTimeFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_DATE_TIME.get() && valueDateTimeFW in 1..1000
    }
    private val modifyCCDateFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_CC_DATE.get() && valueCCDateFW in 1..1000
    }
    private val modifyHorizontalTimeFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_HORIZONTAL_TIME.get() && valueHorizontalTimeFW in 1..1000
    }

    private val realClockFW by lazy {
        if (modifyClockFW) valueClockFW else Preferences.SystemUI.StatusBar.Font.CLOCK_WEIGHT.default
    }
    private val realPadClockFW by lazy {
        if (modifyPadClockFW) valuePadClockFW else Preferences.SystemUI.StatusBar.Font.PAD_CLOCK_WEIGHT.default
    }
    private val realBigTimeFW by lazy {
        if (modifyBigTimeFW) valueBigTimeFW else Preferences.SystemUI.StatusBar.Font.BIG_TIME_WEIGHT.default
    }
    private val realDateTimeFW by lazy {
        if (modifyDateTimeFW) valueDateTimeFW else Preferences.SystemUI.StatusBar.Font.DATE_TIME_WEIGHT.default
    }
    private val realCCDateFW by lazy {
        if (modifyCCDateFW) valueCCDateFW else Preferences.SystemUI.StatusBar.Font.CC_DATE_WEIGHT.default
    }
    private val realHorizontalTimeFW by lazy {
        if (modifyHorizontalTimeFW) valueHorizontalTimeFW else Preferences.SystemUI.StatusBar.Font.HORIZONTAL_TIME_WEIGHT.default
    }

    private val typefaceClockFW by lazy {
        getTypeface(realClockFW)
    }
    private val typefacePadClockFW by lazy {
        getTypeface(realPadClockFW)
    }
    private val typefaceBigTimeFW by lazy {
        getTypeface(realBigTimeFW)
    }
    private val typefaceDateTimeFW by lazy {
        getTypeface(realDateTimeFW)
    }
    private val typefaceCCDateFW by lazy {
        getTypeface(realCCDateFW)
    }
    private val typefaceHorizontalTimeFW by lazy {
        getTypeface(realHorizontalTimeFW)
    }

    private val needHookFontWeight by lazy {
        modifyClockFW || modifyPadClockFW || modifyBigTimeFW || modifyDateTimeFW || modifyCCDateFW || modifyHorizontalTimeFW
    }

    override fun onInit() {
        updateSelfState(needHookFontWeight || modifyPadding)
    }

    override fun onHook() {
        clzMiuiClock?.apply {
            resolve().firstConstructorOrNull {
                parameterCount = 3
            }?.hook {
                val ori = proceed()
                val textView = thisObject as? TextView ?: return@hook result(ori)
                if (needHookFontWeight) {
                    when (textView.id) {
                        clock -> if (modifyClockFW) textView.typeface = typefaceClockFW
                        pad_clock -> if (modifyPadClockFW) textView.typeface = typefacePadClockFW
                        big_time -> if (modifyBigTimeFW) textView.typeface = typefaceBigTimeFW
                        date_time -> if (modifyDateTimeFW) textView.typeface = typefaceDateTimeFW
                        normal_control_center_date_view -> if (modifyCCDateFW) textView.typeface = typefaceCCDateFW
                        horizontal_time -> if (modifyHorizontalTimeFW) textView.typeface = typefaceHorizontalTimeFW
                    }
                }
                if (modifyPadding) {
                    if (Device.isPad) {
                        when (textView.id) {
                            clock -> {
                                textView.apply {
                                    updatePaddingRelative(start = valuePaddingStart.dp(context))
                                }
                            }
                            pad_clock -> {
                                textView.apply {
                                    updatePaddingRelative(end = valuePaddingEnd.dp(context))
                                }
                            }
                        }
                    } else if (textView.id == clock) {
                        textView.apply {
                            updatePaddingRelative(
                                start = valuePaddingStart.dp(context),
                                end = valuePaddingEnd.dp(context),
                            )
                        }
                    }
                }
                result(ori)
            }
        }
        if (modifyClockFW || modifyBigTimeFW) {
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController".toClassOrNull()?.apply {
                val typefaceBigTime = resolve().firstFieldOrNull {
                    name = "MI_PRO_TYPEFACE"
                    modifiers(Modifiers.STATIC)
                }?.toTyped<Typeface>()
                val typefaceClock = resolve().firstFieldOrNull {
                    name = "sMiproTypeface"
                    modifiers(Modifiers.STATIC)
                }?.toTyped<Typeface>()
                val typefaces = resolve().firstFieldOrNull {
                    name = "typefaces"
                    modifiers(Modifiers.STATIC)
                }?.toTyped<List<Typeface>>()
                typefaceBigTime?.set(null, typefaceBigTimeFW)
                typefaceClock?.set(null, typefaceClockFW)
                val sampleCount = abs(realBigTimeFW - realClockFW) / 10
                val sampleStep = (realBigTimeFW - realClockFW) / sampleCount
                val samples = ArrayList<Typeface>()
                samples.add(typefaceClockFW)
                for (i in 1 until sampleCount) {
                    samples.add(getTypeface(realClockFW + sampleStep * i))
                }
                samples.add(typefaceBigTimeFW)
                typefaces?.set(null, samples)
            }
            "com.android.systemui.statusbar.policy.MiuiStatusBarClockController".toClassOrNull()?.apply {
                val mClockListeners = resolve().firstFieldOrNull {
                    name = "mClockListeners"
                }?.toTyped<List<*>>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("onMiuiThemeChanged")
                    }
                }?.hook {
                    val ori = proceed()
                    mClockListeners?.get(thisObject)?.forEach { listener ->
                        if (listener is TextView) {
                            listener.typeface = typefaceClockFW
                        }
                    }
                    result(ori)
                }
            }
        }
        if (modifyDateTimeFW || modifyClockFW || modifyBigTimeFW) {
            "com.android.systemui.qs.MiuiNotificationHeaderView".toClassOrNull()?.apply {
                val mDateView = resolve().firstFieldOrNull {
                    name = "mDateView"
                }?.toTyped<TextView>()
                val usingMiPro = resolve().firstFieldOrNull {
                    name = "usingMiPro"
                }?.toTyped<Boolean>()
                val mBigTime = resolve().firstFieldOrNull {
                    name = "mBigTime"
                }?.toTyped<TextView>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateResources")
                    }
                }?.hook {
                    val ori = proceed()
                    if (modifyDateTimeFW) {
                        mDateView?.get(thisObject)?.typeface = typefaceDateTimeFW
                    }
                    if (modifyClockFW || modifyBigTimeFW) {
                        usingMiPro?.set(thisObject, true)
                        mBigTime?.get(thisObject)?.typeface = typefaceBigTimeFW
                    }
                    result(ori)
                }
            }
        }
        if (modifyCCDateFW) {
            "com.android.systemui.controlcenter.shade.ControlCenterHeaderController".toClassOrNull()?.apply {
                val dateView = resolve().firstFieldOrNull {
                    name = "dateView"
                }?.toTyped<TextView>()
                resolve().method {
                    name {
                        it == "onDensityOrFontScaleChanged" || it.startsWith("onMiuiThemeChanged")
                    }
                }.hookAll {
                    val ori = proceed()
                    dateView?.get(thisObject)?.typeface = typefaceCCDateFW
                    result(ori)
                }
            }
        }
    }
}