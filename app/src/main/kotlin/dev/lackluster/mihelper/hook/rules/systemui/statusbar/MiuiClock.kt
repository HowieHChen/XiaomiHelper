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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.big_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.date_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.horizontal_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_date_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.pad_clock
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiClock
import dev.lackluster.mihelper.utils.Prefs
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object MiuiClock : YukiBaseHooker() {
    private val showAMPM = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM, false)
    private val showLeadingZero = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO, false)
    private val showSecond = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS, false)
    private val geek = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK, false)
    private val geekPatternClock =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_CLOCK, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_CLOCK)
    private val geekPatternPadClock =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD_CLOCK, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD_CLOCK)
    private val geekPatternBigTime =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_BIG_TIME, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_BIG_TIME)
    private val geekPatternDateTime =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_DATE_TIME, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_DATE_TIME)
    private val geekPatternCCDateView =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_CC_DATE, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_CC_DATE)
    private val geekPatternHorizonTime =
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON_TIME, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON_TIME)
    private val fixedWidth = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_FIXED_WIDTH, false)

    private val needHookUpdate =
        geek || showAMPM || showLeadingZero || showSecond || fixedWidth
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
    private val clzMiuiStatusBarClockController by lazy {
        "com.android.systemui.statusbar.policy.MiuiStatusBarClockController".toClass()
    }
    private val clzCalendar by lazy {
        "miuix.pickerwidget.date.Calendar".toClass()
    }
    private val fldMiuiStatusBarClockController by lazy {
        clzMiuiClock?.resolve()?.firstFieldOrNull {
            name = "mMiuiStatusBarClockController"
        }
    }
    private val fldCalendar by lazy {
        clzMiuiStatusBarClockController.resolve().firstFieldOrNull {
            name = "mCalendar"
        }
    }
    private val fldIs24 by lazy {
        clzMiuiStatusBarClockController.resolve().firstFieldOrNull {
            name = "mIs24"
        }
    }
    private val metFormat by lazy {
        clzCalendar.resolve().firstMethod {
            name = "format"
            parameterCount = 2
        }
    }

    override fun onHook() {
        if (!needHookUpdate) return
        if (refreshEverySecond) {
            "com.android.keyguard.KeyguardUpdateMonitor".toClassOrNull()?.apply {
                val mHandler = resolve().firstFieldOrNull {
                    name = "mHandler"
                }
                resolve().firstConstructor().hook {
                    after {
                        val handler = mHandler?.copy()?.of(this.instance)?.get<Handler>() ?: return@after
                        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                            {
                                handler.sendEmptyMessage(301)
                            },
                            1000 - System.currentTimeMillis() % 1000,
                            1000,
                            TimeUnit.MILLISECONDS
                        )
                    }
                }
            }
        }
        clzMiuiClock?.apply {
            resolve().firstMethodOrNull {
                name = "updateTime"
            }?.hook {
                if (geek) {
                    before {
                        updateTimeGeek(this)
                    }
                } else {
                    before {
                        updateTime(this)
                    }
                }
            }
        }
    }

    private fun updateTime(param: HookParam) {
        val clockView = param.instance<TextView>()
        if (clockView.id !in setOf(clock, big_time, horizontal_time)) return
        val context = clockView.context
        val controller = fldMiuiStatusBarClockController?.copy()?.of(param.instance)?.get() ?: return
        val calendar = fldCalendar?.copy()?.of(controller)?.get() ?: return
        val is24 = fldIs24?.copy()?.of(controller)?.get<Boolean>() ?: return
        val fmtString = getFmtString(context, clockView.id, is24)
        if (fmtString.isNotBlank()) {
            val dateTime = metFormat.copy().of(calendar).invoke<String>(context, fmtString)
            clockView.text = dateTime
            if (
                fixedWidth && refreshEverySecond && needRefreshBySecond(context, clockView.id, is24) &&
                ((System.currentTimeMillis() / 1000) % 60) == 0L
            ) {
                clockView.minWidth = ceil(clockView.paint.measureText(dateTime)).toInt() + clockView.paddingLeft + clockView.paddingRight
            }
            param.result = null
        }
    }

    private fun updateTimeGeek(param: HookParam) {
        val clockView = param.instance<TextView>()
        val context = clockView.context
        val controller = fldMiuiStatusBarClockController?.copy()?.of(param.instance)?.get() ?: return
        val calendar = fldCalendar?.copy()?.of(controller)?.get() ?: return
        val is24 = fldIs24?.copy()?.of(controller)?.get<Boolean>() ?: return
        val fmtString = when (clockView.id) {
            clock -> geekPatternClock
            pad_clock -> geekPatternPadClock
            big_time -> geekPatternBigTime
            date_time -> geekPatternDateTime
            normal_control_center_date_view -> geekPatternCCDateView
            horizontal_time -> geekPatternHorizonTime
            else -> return
        } ?: return
        val dateTime = metFormat.copy().of(calendar).invoke<String>(context, fmtString)
        clockView.text = dateTime
        if (
            fixedWidth && refreshEverySecond && needRefreshBySecond(context, clockView.id, is24) &&
            ((System.currentTimeMillis() / 1000) % 60) == 0L
        ) {
            clockView.minWidth = ceil(clockView.paint.measureText(dateTime)).toInt() + clockView.paddingLeft + clockView.paddingRight
        }
        param.result = null
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