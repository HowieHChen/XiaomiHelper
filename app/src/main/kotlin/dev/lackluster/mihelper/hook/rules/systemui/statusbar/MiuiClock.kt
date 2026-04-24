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
import android.os.Handler
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.HookResult
import dev.lackluster.mihelper.hook.base.HookScope
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.big_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.date_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.horizontal_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_date_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.pad_clock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiClock
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object MiuiClock : StaticHooker() {
    private val showAMPM by Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_AMPM.lazyGet()
    private val showLeadingZero by Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_LEADING_ZERO.lazyGet()
    private val showSecond by Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_SECONDS.lazyGet()

    private val geek by Preferences.SystemUI.StatusBar.Clock.ENABLE_GEEK_MODE.lazyGet()
    private val geekPatternClock by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_CLOCK.lazyGet()
    private val geekPatternPadClock by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_PAD_CLOCK.lazyGet()
    private val geekPatternBigTime by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_BIG_TIME.lazyGet()
    private val geekPatternDateTime by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_DATE_TIME.lazyGet()
    private val geekPatternCCDateView by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_CC_DATE.lazyGet()
    private val geekPatternHorizonTime by Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_HORIZON_TIME.lazyGet()

    private val fixedWidth by Preferences.SystemUI.StatusBar.Clock.FIXED_WIDTH.lazyGet()

    private val refreshEverySecond by lazy {
        if (geek) {
            listOf(
                geekPatternClock, geekPatternPadClock, geekPatternBigTime,
                geekPatternDateTime, geekPatternCCDateView, geekPatternHorizonTime
            ).joinToString().contains("s")
        } else {
            showSecond
        }
    }
    private val fmtStringCache by lazy {
        ConcurrentHashMap<Int, String>()
    }
    private val refreshBySecondCache by lazy {
        ConcurrentHashMap<Int, Boolean>()
    }
    private val clzMiuiStatusBarClockController by "com.android.systemui.statusbar.policy.MiuiStatusBarClockController".lazyClass()
    private val clzCalendar by "miuix.pickerwidget.date.Calendar".lazyClass()
    private val fldMiuiStatusBarClockController by lazy {
        clzMiuiClock?.resolve()?.firstFieldOrNull {
            name = "mMiuiStatusBarClockController"
        }?.toTyped<Any>()
    }
    private val fldCalendar by lazy {
        clzMiuiStatusBarClockController.resolve().firstFieldOrNull {
            name = "mCalendar"
        }?.toTyped<Any>()
    }
    private val fldIs24 by lazy {
        clzMiuiStatusBarClockController.resolve().firstFieldOrNull {
            name = "mIs24"
        }?.toTyped<Boolean>()
    }
    private val metFormat by lazy {
        clzCalendar.resolve().firstMethod {
            name = "format"
            parameterCount = 2
        }.toTyped<String>()
    }

    override fun onInit() {
        updateSelfState(geek || showAMPM || showLeadingZero || showSecond || fixedWidth)
    }

    override fun onHook() {
        if (refreshEverySecond) {
            "com.android.keyguard.KeyguardUpdateMonitor".toClassOrNull()?.apply {
                val mHandler = resolve().firstFieldOrNull {
                    name = "mHandler"
                }?.toTyped<Handler>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    val handler = mHandler?.get(thisObject)
                    if (handler != null) {
                        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                            {
                                handler.sendEmptyMessage(301)
                            },
                            1000 - System.currentTimeMillis() % 1000,
                            1000,
                            TimeUnit.MILLISECONDS
                        )
                    }
                    result(ori)
                }
            }
        }
        clzMiuiClock?.apply {
            resolve().firstMethodOrNull {
                name = "updateTime"
            }?.hook {
                if (geek) {
                    updateTimeGeek()
                } else {
                    updateTime()
                }
            }
        }
    }

    private fun HookScope.updateTime(): HookResult {
        val clockView = thisObject as? TextView
        if (clockView == null || clockView.id !in setOf(clock, big_time, horizontal_time)) return result(proceed())
        val context = clockView.context
        val controller = fldMiuiStatusBarClockController?.get(thisObject)
        val calendar = controller?.let { fldCalendar?.get(it) }
        val is24 = controller?.let { fldIs24?.get(it) }
        if (calendar == null || is24 == null) return result(proceed())
        val fmtString = getFmtString(context, clockView.id, is24)
        if (fmtString.isNotBlank()) {
            val dateTime = metFormat.invoke(calendar, context, fmtString)
            clockView.text = dateTime
            if (
                fixedWidth && refreshEverySecond && needRefreshBySecond(context, clockView.id, is24) &&
                ((System.currentTimeMillis() / 1000) % 60) == 0L
            ) {
                clockView.minWidth = ceil(clockView.paint.measureText(dateTime)).toInt() + clockView.paddingLeft + clockView.paddingRight
            }
            return result(null)
        } else {
            return result(proceed())
        }
    }

    private fun HookScope.updateTimeGeek(): HookResult {
        val clockView = thisObject as? TextView
        val context = clockView?.context
        val controller = fldMiuiStatusBarClockController?.get(thisObject)
        val calendar = controller?.let { fldCalendar?.get(it) }
        val is24 = controller?.let { fldIs24?.get(it) }
        if (context == null || calendar == null || is24 == null) return result(proceed())
        val fmtString = when (clockView.id) {
            clock -> geekPatternClock
            pad_clock -> geekPatternPadClock
            big_time -> geekPatternBigTime
            date_time -> geekPatternDateTime
            normal_control_center_date_view -> geekPatternCCDateView
            horizontal_time -> geekPatternHorizonTime
            else -> null
        } ?: return result(proceed())
        val dateTime = metFormat.invoke(calendar, context, fmtString)
        clockView.text = dateTime
        if (
            fixedWidth && refreshEverySecond && needRefreshBySecond(context, clockView.id, is24) &&
            ((System.currentTimeMillis() / 1000) % 60) == 0L
        ) {
            clockView.minWidth = ceil(clockView.paint.measureText(dateTime)).toInt() + clockView.paddingLeft + clockView.paddingRight
        }
        return result(null)
    }

    private fun getFmtString(context: Context, clockId: Int, is24: Boolean): String {
        val key = Objects.hash(clockId, is24, context.resources.configuration.locales.get(0).language)
        return fmtStringCache.getOrPut(key) {
            var fmtString = when (clockId) {
                horizontal_time -> {
                    if (is24) {
                        ResourcesUtils.status_bar_clock_date_time_format
                    } else {
                        ResourcesUtils.status_bar_clock_date_time_format_12
                    }
                }
                else -> { // clock, big_time
                    if (is24) {
                        ResourcesUtils.fmt_time_24hour_minute
                    } else {
                        if (showAMPM) { ResourcesUtils.fmt_time_12hour_minute_pm }
                        else { ResourcesUtils.fmt_time_12hour_minute }
                    }
                }
            }.let { context.getString(it) }
            if (showSecond) {
                fmtString = fmtString
                    .replaceFirst(Regex(":mm"), ":mm:ss")
            }
            if (showLeadingZero) {
                val hourStr = if (is24) { "H" } else { "h" }
                fmtString = fmtString
                    .replaceFirst(Regex("[Hh]+:"), "${hourStr}${hourStr}:")
                    .replaceFirst(Regex(":ms$"), ":ss")
                    .replaceFirst(Regex(":s+$"), ":ss")
            }
            return@getOrPut fmtString
        }
    }

    private fun needRefreshBySecond(context: Context, clockId: Int, is24: Boolean): Boolean {
        val key = Objects.hash(clockId, is24, context.resources.configuration.locales.get(0).language)
        return refreshBySecondCache.getOrPut(key) {
            val fmtString = if (geek) {
                when (clockId) {
                    clock -> geekPatternClock
                    pad_clock -> geekPatternPadClock
                    big_time -> geekPatternBigTime
                    date_time -> geekPatternDateTime
                    normal_control_center_date_view -> geekPatternCCDateView
                    horizontal_time -> geekPatternHorizonTime
                    else -> null
                } ?: ""
            } else {
                getFmtString(context, clockId, is24)
            }
            fmtString.contains("s")
        }
    }
}