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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.pad_clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.big_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.horizontal_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.date_time
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt

object StatusBarClock : YukiBaseHooker() {
    private const val MIUI_CLOCK_CLZ = "com.android.systemui.statusbar.views.MiuiClock"
    private const val MIUI_STATUS_BAR_CLOCK_CLZ = "com.android.systemui.statusbar.views.MiuiStatusBarClock"
    private val clockGeekMode by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK, false)
    }
    private val clockGeekPattern by lazy {
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT)
    }
    private val clockGeekPatternHorizon by lazy {
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON)
    }
    private val clockGeekPatternPad by lazy {
        Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD)
    }
    private val clockShowAMPM by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM, false)
    }
    private val clockShowLeadingZero by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO, false)
    }
    private val clockShowSecond by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS, false)
    }
    private val clockFormatName12 by lazy {
        if (clockShowSecond) {
            if (clockShowAMPM) { "fmt_time_12hour_minute_second_pm" }
            else { "fmt_time_12hour_minute_second" }
        }
        else {
            if (clockShowAMPM) { "fmt_time_12hour_minute_pm" }
            else { "fmt_time_12hour_minute" }
        }
    }
    private val clockFormatName12Id by lazy {
        if (clockShowSecond) {
            if (clockShowAMPM) { ResourcesUtils.fmt_time_12hour_minute_second_pm }
            else { ResourcesUtils.fmt_time_12hour_minute_second }
        }
        else {
            if (clockShowAMPM) { ResourcesUtils.fmt_time_12hour_minute_pm }
            else { ResourcesUtils.fmt_time_12hour_minute }
        }
    }
    private val clockFormatName24 by lazy {
        if (clockShowSecond) { "fmt_time_24hour_minute_second" }
        else { "fmt_time_24hour_minute" }
    }
    private val clockFormatName24Id by lazy {
        if (clockShowSecond) { ResourcesUtils.fmt_time_24hour_minute_second }
        else { ResourcesUtils.fmt_time_24hour_minute }
    }
    private val clockPaddingCustom by lazy {
        Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM, false)
    }
    private val clockPaddingLeft by lazy {
        Prefs.getInt(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT, 0)
    }
    private val clockPaddingRight by lazy {
        Prefs.getInt(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT, 0)
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        val needUpdatePerSecond = (!clockGeekMode && clockShowSecond) ||
                (clockGeekMode && "$clockGeekPattern $clockGeekPatternHorizon".contains("s"))
        if (clockPaddingCustom || needUpdatePerSecond) {
            MIUI_CLOCK_CLZ.toClass().constructor {
                paramCount = 3
            }.hook {
                after {
                    val miuiClock = this.instance as TextView
                    if (this.args(2).int() == -1) {
                        miuiClock.id = clock
                    }
                    if (clockPaddingCustom) {
                        val scale = miuiClock.context.resources.displayMetrics.density
                        if (miuiClock.id == pad_clock) {
                            miuiClock.setPadding(
                                miuiClock.paddingLeft,
                                miuiClock.paddingTop,
                                (clockPaddingRight * scale).roundToInt(),
                                miuiClock.paddingBottom
                            )
                        } else {
                            miuiClock.setPadding(
                                (clockPaddingLeft * scale).roundToInt(),
                                miuiClock.paddingTop,
                                (clockPaddingRight * scale).roundToInt(),
                                miuiClock.paddingBottom
                            )
                        }
                    }
                    val refreshPerSecond = if (clockGeekMode) {
                        clockGeekPattern?.contains("s") == true && miuiClock.id in setOf(clock, big_time) ||
                                clockGeekPatternHorizon?.contains("s") == true && miuiClock.id in setOf(horizontal_time, date_time) ||
                                clockGeekPatternPad?.contains("s") == true && miuiClock.id in setOf(pad_clock)
                    } else {
                        clockShowSecond && miuiClock.id in setOf(clock, pad_clock, big_time, horizontal_time, date_time)
                    }
                    if (refreshPerSecond) {
                        val r = Runnable {
                            miuiClock.current().method {
                                name = "updateTime"
                                superClass()
                            }.call()
                        }
                        class T : TimerTask() {
                            override fun run() {
                                Handler(miuiClock.context.mainLooper).post(r)
                            }
                        }
                        Timer().scheduleAtFixedRate(
                            T(), 1000 - System.currentTimeMillis() % 1000, 1000
                        )
                    }
                }
            }
        }
        if (clockGeekMode) {
            MIUI_CLOCK_CLZ.toClass().method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateGeek(this)
                }
            }
            MIUI_STATUS_BAR_CLOCK_CLZ.toClass().method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateGeek(this)
                }
            }
        } else {
            if (clockShowAMPM || clockShowLeadingZero || clockShowSecond) {
                MIUI_CLOCK_CLZ.toClass().method {
                    name = "updateTime"
                }.hook {
                    before {
                        handleUpdateTime(this)
                    }
                }
                MIUI_STATUS_BAR_CLOCK_CLZ.toClass().method {
                    name = "updateTime"
                }.hook {
                    before {
                        handleUpdateTime(this)
                    }
                }
            }
        }
        hasEnable(Pref.Key.SystemUI.NotifCenter.CLOCK_COLOR_FIX) {
            "com.android.systemui.controlcenter.phone.widget.NotificationShadeFakeStatusBarClock".toClass().method {
                name = "updateHeaderColor"
            }.hook {
                replaceUnit {
                    val bigTimeColor = this.instance.current().field {
                        name = "bigTimeColor"
                    }.any() as Color
                    val tintColor = Color.valueOf(this.instance.current().field {
                        name = "mTint"
                    }.int())
                    val lightColor = Color.valueOf(this.instance.current().field {
                        name = "mLightColor"
                    }.int())
                    val darkColor = Color.valueOf(this.instance.current().field {
                        name = "mDarkColor"
                    }.int())
                    val whiteFraction = this.instance.current().field {
                        name = "mWhiteFraction"
                    }.float()
                    val areas = this.instance.current().field {
                        name = "mAreas"
                    }.any() as ArrayList<*>
                    val darkIntensity = this.instance.current().field {
                        name = "mDarkIntensity"
                    }.float()
                    val useTint = this.instance.current().field {
                        name = "mUseTint"
                    }.boolean()
                    val inTintColor = Color.argb(
                        Math.linearInterpolate(tintColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                        Math.linearInterpolate(tintColor.red(), bigTimeColor.red(), whiteFraction),
                        Math.linearInterpolate(tintColor.green(), bigTimeColor.green(), whiteFraction),
                        Math.linearInterpolate(tintColor.blue(), bigTimeColor.blue(), whiteFraction)
                    )
                    val inLightColor = Color.argb(
                        Math.linearInterpolate(lightColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                        Math.linearInterpolate(lightColor.red(), bigTimeColor.red(), whiteFraction),
                        Math.linearInterpolate(lightColor.green(), bigTimeColor.green(), whiteFraction),
                        Math.linearInterpolate(lightColor.blue(), bigTimeColor.blue(), whiteFraction)
                    )
                    val inDarkColor = Color.argb(
                        Math.linearInterpolate(darkColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                        Math.linearInterpolate(darkColor.red(), bigTimeColor.red(), whiteFraction),
                        Math.linearInterpolate(darkColor.green(), bigTimeColor.green(), whiteFraction),
                        Math.linearInterpolate(darkColor.blue(), bigTimeColor.blue(), whiteFraction)
                    )
                    this.instance.current().method {
                        name = "getBigTime"
                    }.call()?.current()?.method {
                        name = "onDarkChanged"
                    }?.call(areas, darkIntensity, inTintColor, inLightColor, inDarkColor, useTint)
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun handleUpdateTime(param: HookParam) {
        val miuiClock = param.instance as TextView
        if (miuiClock.id in setOf(clock, big_time, horizontal_time)) {
            val miuiStatusBarClockController = param.instance.current().field {
                name = "mMiuiStatusBarClockController"
                superClass()
            }.any() ?: return
            val calendar = miuiStatusBarClockController.current().field {
                name = "mCalendar"
            }.any() ?: return
            val context = miuiClock.context
            val is24 = miuiStatusBarClockController.current().field {
                name = "mIs24"
            }.boolean()
            val hourStr = if (is24) { "H" } else { "h" }
            var fmtId = if (miuiClock.id in setOf(clock, big_time)) {
                if (is24) { clockFormatName24Id } else { clockFormatName12Id }
            }
            else {
                if (is24) { ResourcesUtils.status_bar_clock_date_time_format } else { ResourcesUtils.status_bar_clock_date_time_format_12 }
            }
            if (fmtId == 0) {
                val fmtName = if (miuiClock.id in setOf(clock, big_time)) {
                    if (is24) { clockFormatName24 } else { clockFormatName12 }
                }
                else {
                    if (is24) { "status_bar_clock_date_time_format" } else { "status_bar_clock_date_time_format_12" }
                }
                fmtId = context.resources.getIdentifier(fmtName, "string", context.packageName)
            }
            var fmtString = context.getString(fmtId)
            if (clockShowLeadingZero) {
                fmtString = fmtString.replaceFirst(Regex("[Hh]+:"), "${hourStr}${hourStr}:")
            }
            if (clockShowSecond) {
                fmtString = if (miuiClock.id in setOf(clock, big_time)) {
                    fmtString.replaceFirst(Regex(":s\$"), ":ss")
                } else {
                    fmtString.replaceFirst(Regex(":mm"), ":mm:ss")
                }
                calendar.current().method {
                    name = "setTimeInMillis"
                }.call(System.currentTimeMillis())
            }
            if (fmtString.isNotBlank()) {
                val dateTime = calendar.current().method {
                    name = "format"
                    paramCount = 2
                }.string(context, fmtString)
                miuiClock.text = dateTime
                param.result = null
            }
        }
    }

    private fun handleUpdateGeek(param: HookParam) {
        val miuiClock = param.instance as TextView
        val miuiStatusBarClockController = param.instance.current().field {
            name = "mMiuiStatusBarClockController"
            superClass()
        }.any() ?: return
        val calendar = miuiStatusBarClockController.current().field {
            name = "mCalendar"
        }.any() ?: return
        val context = miuiClock.context
        val fmtString = when (miuiClock.id) {
            in setOf(clock, big_time) -> {
                clockGeekPattern
            }
            in setOf(horizontal_time, date_time) -> {
                clockGeekPatternHorizon
            }
            in setOf(pad_clock) -> {
                clockGeekPatternPad
            }
            else -> {
                return
            }
        }
        if (fmtString?.contains("s") == true) {
            calendar.current().method {
                name = "setTimeInMillis"
            }.call(System.currentTimeMillis())
        }
        if (fmtString?.isNotBlank() == true) {
            val dateTime = calendar.current().method {
                name = "format"
                paramCount = 2
            }.string(context, fmtString)
            miuiClock.text = dateTime
            param.result = null
        }
    }
}