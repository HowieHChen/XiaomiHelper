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

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateMarginsRelative
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
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_clock_margin_end
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_clock_margin_new
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiClock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.dp
import java.util.WeakHashMap
import kotlin.math.abs
import kotlin.math.roundToInt

object MiuiClockFontWeight : StaticHooker() {
    // Padding
    private val valuePaddingStart by Preferences.SystemUI.StatusBar.Clock.PADDING_START_VAL.lazyGet()
    private val valuePaddingEnd by Preferences.SystemUI.StatusBar.Clock.PADDING_END_VAL.lazyGet()
    private val modifyPadding by Preferences.SystemUI.StatusBar.Clock.CUSTOM_HORIZON_PADDING.lazyGet()
    // Status Bar Clock Font Size
    private val valueStatusBarClockSize by Preferences.SystemUI.StatusBar.Clock.STATUS_BAR_CLOCK_SIZE_VAL.lazyGet()
    private val modifyStatusBarClockSize by lazy {
        Preferences.SystemUI.StatusBar.Clock.CUSTOM_STATUS_BAR_CLOCK_SIZE.get() && valueStatusBarClockSize > 0
    }
    private val originalClockVerticalMargins = WeakHashMap<TextView, Pair<Int, Int>>()
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
        updateSelfState(needHookFontWeight || modifyPadding || modifyStatusBarClockSize)
    }

    private fun TextView.updateClockMargin(
        start: Int? = null,
        end: Int? = null,
        postIfUnavailable: Boolean = true,
    ) {
        d { "updateClockMargin ${this.id} ${this.layoutParams}" }
        val params = layoutParams as? ViewGroup.MarginLayoutParams
        if (params == null) {
            if (postIfUnavailable) {
                post {
                    updateClockMargin(start, end, postIfUnavailable = false)
                }
            }
            return
        }
        params.updateMarginsRelative(
            start = start ?: params.marginStart,
            end = end ?: params.marginEnd,
        )
        layoutParams = params
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
                    val context = textView.context
                    if (Device.isPad) {
                        when (textView.id) {
                            clock -> {
                                val originalMargin = if (status_bar_clock_margin_new > 0) {
                                    context.resources.getDimensionPixelSize(status_bar_clock_margin_new)
                                } else {
                                    3.dp(context)
                                }
                                val marginStart = (originalMargin + valuePaddingStart.dp(textView.context)).coerceAtLeast(0)
                                textView.updateClockMargin(start = marginStart)
                            }
                            pad_clock -> {
                                val originalMargin = if (status_bar_clock_margin_end > 0) {
                                    context.resources.getDimensionPixelSize(status_bar_clock_margin_end)
                                } else {
                                    4.dp(context)
                                }
                                val marginEnd = (originalMargin + valuePaddingEnd.dp(textView.context)).coerceAtLeast(0)
                                textView.updateClockMargin(end = marginEnd)
                            }
                        }
                    } else if (textView.id == clock) {
                        val originalMargin = if (status_bar_clock_margin_new > 0) {
                            context.resources.getDimensionPixelSize(status_bar_clock_margin_new)
                        } else {
                            3.dp(context)
                        }
                        val marginStart = (originalMargin + valuePaddingStart.dp(textView.context)).coerceAtLeast(0)
                        val marginEnd = (originalMargin + valuePaddingEnd.dp(textView.context)).coerceAtLeast(0)
                        textView.updateClockMargin(start = marginStart, end = marginEnd)
                    }
                }
                if (modifyStatusBarClockSize) {
                    if (textView.id == clock || textView.id == pad_clock) {
                        textView.applyCustomStatusBarClockSize()
                    }
                }
                result(ori)
            }
        }
        if (modifyStatusBarClockSize) {
            "com.android.systemui.FontSizeUtils".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "updateFontSize"
                    parameterCount = 2
                }?.hook {
                    val ori = proceed()
                    (getArg(0) as? TextView)?.let { textView ->
                        if (textView.id == clock || textView.id == pad_clock) {
                            textView.applyCustomStatusBarClockSize()
                        }
                    }
                    result(ori)
                }
            }
        }
        if (modifyClockFW || modifyBigTimeFW || modifyStatusBarClockSize) {
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController".toClassOrNull()?.apply {
                if (modifyClockFW || modifyBigTimeFW) {
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
                if (modifyStatusBarClockSize) {
                    val fldStatusBarClockSize = resolve().firstFieldOrNull {
                        name = "statusBarClockSize"
                    }?.toTyped<Int>()
                    val fldContext = resolve().firstFieldOrNull {
                        name = "context"
                    }?.toTyped<Context>()
                    resolve().firstMethodOrNull {
                        name = "updateTranslationY"
                    }?.hook {
                        val valueStatusBarClockSizePx = fldContext?.get(thisObject)?.let { valueStatusBarClockSize.dp(it) }
                        fldStatusBarClockSize?.set(thisObject, valueStatusBarClockSizePx)
                        result(proceed())
                    }
                    resolve().firstConstructor().hook {
                        val ori = proceed()
                        val valueStatusBarClockSizePx = fldContext?.get(thisObject)?.let { valueStatusBarClockSize.dp(it) }
                        fldStatusBarClockSize?.set(thisObject, valueStatusBarClockSizePx)
                        result(ori)
                    }
                }
            }
            if (modifyClockFW || modifyBigTimeFW) {
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


    private fun TextView.applyCustomStatusBarClockSize(postIfUnavailable: Boolean = true) {
        if (!modifyStatusBarClockSize || (id != clock && id != pad_clock)) return

        setTextSize(TypedValue.COMPLEX_UNIT_DIP, valueStatusBarClockSize)

        val params = layoutParams as? ViewGroup.MarginLayoutParams
        if (params == null) {
            if (postIfUnavailable) {
                post {
                    applyCustomStatusBarClockSize(postIfUnavailable = false)
                }
            }
            return
        }

        val fm = paint.fontMetrics
        val verticalOffset = (
                (fm.top + fm.bottom) - (fm.ascent + fm.descent)
                ).div(2f).roundToInt()

        val (originalTopMargin, originalBottomMargin) =
            originalClockVerticalMargins.getOrPut(this) {
                params.topMargin to params.bottomMargin
            }

        d { "verticalOffset $verticalOffset height ${params.height}" }

//        params.height = 20.dp(context)
        params.topMargin = originalTopMargin + verticalOffset
        params.bottomMargin = originalBottomMargin - verticalOffset
        layoutParams = params
    }
}
