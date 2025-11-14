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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.big_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.date_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.horizontal_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_date_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.pad_clock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiClock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import kotlin.math.abs

object MiuiClockFontWeight : YukiBaseHooker() {
    // Padding
    private val valuePaddingStart = Prefs.getFloat(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_START_VAL, 0.0f)
    private val valuePaddingEnd = Prefs.getFloat(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_END_VAL, 0.0f)
    private val modifyPadding =
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_HORIZON, false)
    // Font Weight
    private val defFWVal = if (Device.isPad) 460 else 500
    private val valueClockFW = Prefs.getInt(FontWeight.CLOCK_WEIGHT, defFWVal)
    private val valuePadClockFW = Prefs.getInt(FontWeight.PAD_CLOCK_WEIGHT, defFWVal)
    private val valueBigTimeFW = Prefs.getInt(FontWeight.BIG_TIME_WEIGHT, 305)
    private val valueDateTimeFW = Prefs.getInt(FontWeight.DATE_TIME_WEIGHT, 400)
    private val valueCCDateFW = Prefs.getInt(FontWeight.CC_DATE_WEIGHT, 400)
    private val valueHorizontalTimeFW = Prefs.getInt(FontWeight.HORIZONTAL_TIME_WEIGHT, defFWVal)
    private val modifyClockFW =
        Prefs.getBoolean(FontWeight.CLOCK, false) && valueClockFW in 1..1000
    private val modifyPadClockFW =
        Prefs.getBoolean(FontWeight.PAD_CLOCK, false) && valuePadClockFW in 1..1000
    private val modifyBigTimeFW =
        Prefs.getBoolean(FontWeight.BIG_TIME, false) && valueBigTimeFW in 1..1000
    private val modifyDateTimeFW =
        Prefs.getBoolean(FontWeight.DATE_TIME, false) && valueDateTimeFW in 1..1000
    private val modifyCCDateFW =
        Prefs.getBoolean(FontWeight.CC_DATE, false) && valueCCDateFW in 1..1000
    private val modifyHorizontalTimeFW =
        Prefs.getBoolean(FontWeight.HORIZONTAL_TIME, false) && valueHorizontalTimeFW in 1..1000
    private val realClockFW = if (modifyClockFW) valueClockFW else defFWVal
    private val realPadClockFW = if (modifyClockFW) valuePadClockFW else defFWVal
    private val realBigTimeFW = if (modifyBigTimeFW) valueBigTimeFW else 305
    private val realDateTimeFW = if (modifyDateTimeFW) valueDateTimeFW else 400
    private val realCCDateFW = if (modifyCCDateFW) valueCCDateFW else 400
    private val realHorizontalTimeFW = if (modifyHorizontalTimeFW) valueHorizontalTimeFW else defFWVal
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
    private val needHookFontWeight =
        modifyClockFW || modifyPadClockFW || modifyBigTimeFW || modifyDateTimeFW || modifyCCDateFW || modifyHorizontalTimeFW

    override fun onHook() {
        if (!needHookFontWeight && !modifyPadding) return
        clzMiuiClock?.apply {
            resolve().firstConstructorOrNull {
                parameterCount = 3
            }?.hook {
                after {
                    val textView = this.instance<TextView>()
                    if (needHookFontWeight) {
                        when (textView.id) {
                            clock ->
                                if (modifyClockFW) textView.typeface = typefaceClockFW
                            pad_clock ->
                                if (modifyPadClockFW) textView.typeface = typefacePadClockFW
                            big_time ->
                                if (modifyBigTimeFW) textView.typeface = typefaceBigTimeFW
                            date_time ->
                                if (modifyDateTimeFW) textView.typeface = typefaceDateTimeFW
                            normal_control_center_date_view ->
                                if (modifyCCDateFW) textView.typeface = typefaceCCDateFW
                            horizontal_time ->
                                if (modifyHorizontalTimeFW) textView.typeface = typefaceHorizontalTimeFW
                        }
                    }
                    if (modifyPadding) {
                        if (Device.isPad) {
                            when (textView.id) {
                                clock -> {
                                    textView.apply {
                                        updatePaddingRelative(
                                            start = valuePaddingStart.dp(context),
                                        )
                                    }
                                }
                                pad_clock -> {
                                    textView.apply {
                                        updatePaddingRelative(
                                            end = valuePaddingEnd.dp(context),
                                        )
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
                }
            }
        }
        if (modifyClockFW || modifyBigTimeFW) {
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController".toClassOrNull()?.apply {
                val typefaceBigTime = resolve().firstFieldOrNull {
                    name = "MI_PRO_TYPEFACE"
                    modifiers(Modifiers.STATIC)
                } ?: return@apply
                val typefaceClock = resolve().firstFieldOrNull {
                    name = "sMiproTypeface"
                    modifiers(Modifiers.STATIC)
                } ?: return@apply
                val typefaces = resolve().firstFieldOrNull {
                    name = "typefaces"
                    modifiers(Modifiers.STATIC)
                } ?: return@apply
                typefaceBigTime.copy().set(typefaceBigTimeFW)
                typefaceClock.copy().set(typefaceClockFW)
                val sampleCount = abs(realBigTimeFW - realClockFW) / 10
                val sampleStep = (realBigTimeFW - realClockFW) / sampleCount
                val samples = ArrayList<Typeface>()
                samples.add(typefaceClockFW)
                for (i in 1 until sampleCount) {
                    samples.add(getTypeface(realClockFW + sampleStep * i))
                }
                samples.add(typefaceBigTimeFW)
                typefaces.copy().set(samples)
            }
        }
        if (modifyDateTimeFW) {
            "com.android.systemui.qs.MiuiNotificationHeaderView".toClassOrNull()?.apply {
                val mDateView = resolve().firstFieldOrNull {
                    name = "mDateView"
                }
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateResources")
                    }
                }?.hook {
                    after {
                        mDateView?.copy()?.of(this.instance)?.get<TextView>()?.typeface = typefaceDateTimeFW
                    }
                }
            }
        }
        if (modifyCCDateFW) {
            "com.android.systemui.controlcenter.shade.ControlCenterHeaderController".toClassOrNull()?.apply {
                val dateView = resolve().firstFieldOrNull {
                    name = "dateView"
                }
                resolve().method {
                    name {
                        it == "onDensityOrFontScaleChanged" || it.startsWith("onMiuiThemeChanged")
                    }
                }.hookAll {
                    after {
                        dateView?.copy()?.of(this.instance)?.get<TextView>()?.typeface = typefaceCCDateFW
                    }
                }
            }
        }
    }
}