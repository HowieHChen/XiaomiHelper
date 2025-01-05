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
import dev.lackluster.mihelper.utils.Prefs
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

object StatusBarClock : YukiBaseHooker() {
    private const val CUSTOM_VIEW_ID = 0x00111111
    private val miuiClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClass()
    }
    private val miuiStatusBarClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiStatusBarClock".toClass()
    }
    private val clockGeekMode = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK, false)
    private val clockGeekPattern = Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT)
    private val clockGeekPatternHorizon = Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON)
    private val clockGeekPatternPad = Prefs.getString(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD, Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD)
    private val clockShowAMPM = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM, false)
    private val clockShowLeadingZero = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO, false)
    private val clockShowSecond = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS, false)
    private val clockPaddingCustom = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM, false)
    private val clockPaddingLeft = Prefs.getFloat(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT, 0.0f)
    private val clockPaddingRight = Prefs.getFloat(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT, 0.0f)
    private val clockFixedWidth = Prefs.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_FIXED_WIDTH, false)

    private val clockFormatName12Id by lazy {
        if (clockShowSecond) {
            if (clockShowAMPM) { ResourcesUtils.fmt_time_12hour_minute_second_pm }
            else { ResourcesUtils.fmt_time_12hour_minute_second }
        } else {
            if (clockShowAMPM) { ResourcesUtils.fmt_time_12hour_minute_pm }
            else { ResourcesUtils.fmt_time_12hour_minute }
        }
    }
    private val clockFormatName24Id by lazy {
        if (clockShowSecond) { ResourcesUtils.fmt_time_24hour_minute_second }
        else { ResourcesUtils.fmt_time_24hour_minute }
    }

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        val needUpdatePerSecond = (!clockGeekMode && clockShowSecond) ||
                (clockGeekMode && "$clockGeekPattern $clockGeekPatternHorizon".contains("s"))
        if (needUpdatePerSecond) {
            "com.android.keyguard.KeyguardUpdateMonitor".toClass().apply {
                constructor().hook {
                    after {
                        val mHandler = this.instance.current().field {
                            name = "mHandler"
                        }.cast<Handler>()
                        val scheduledExecutorService = Executors.newScheduledThreadPool(1)
                        scheduledExecutorService.scheduleAtFixedRate(
                            {
                                mHandler?.sendEmptyMessage(301)
                            },
                            1000 - System.currentTimeMillis() % 1000,
                            1000,
                            TimeUnit.MILLISECONDS
                        )
                    }
                }
            }
        }
        if (clockPaddingCustom) {
            miuiClockClass.constructor {
                paramCount = 3
            }.hook {
                after {
                    val miuiClock = this.instance as TextView
                    if (this.args(2).int() == -1 && miuiClock.id == -1) {
                        miuiClock.id = CUSTOM_VIEW_ID
                    }
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
            }
        }
        if (clockGeekMode) {
            miuiClockClass.method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateGeek(this)
                }
            }
            miuiStatusBarClockClass.method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateGeek(this)
                }
            }
        } else if (clockShowAMPM || clockShowLeadingZero || clockShowSecond) {
            miuiClockClass.method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateTime(this)
                }
            }
            miuiStatusBarClockClass.method {
                name = "updateTime"
            }.hook {
                before {
                    handleUpdateTime(this)
                }
            }
        }
    }

    private fun handleUpdateTime(param: HookParam) {
        val miuiClock = param.instance as TextView
        if (miuiClock.id in setOf(clock, big_time, horizontal_time, CUSTOM_VIEW_ID)) {
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
            val fmtId = if (miuiClock.id in setOf(clock, big_time, CUSTOM_VIEW_ID)) {
                if (is24) { clockFormatName24Id } else { clockFormatName12Id }
            } else {
                if (is24) { ResourcesUtils.status_bar_clock_date_time_format } else { ResourcesUtils.status_bar_clock_date_time_format_12 }
            }
            var fmtString = context.getString(fmtId)
            if (clockShowLeadingZero) {
                fmtString = fmtString
                    .replaceFirst(Regex("[Hh]+:"), "${hourStr}${hourStr}:")
                    .replaceFirst(Regex(":ms$"), ":ss")
                    .replaceFirst(Regex(":s+$"), ":ss")
            }
            if (fmtString.isNotBlank()) {
                val dateTime = calendar.current().method {
                    name = "format"
                    paramCount = 2
                }.string(context, fmtString)
                miuiClock.text = dateTime
                if (clockShowSecond && clockFixedWidth && dateTime.endsWith(":00")) {
                    miuiClock.minWidth = ceil(miuiClock.paint.measureText(dateTime)).toInt() + miuiClock.paddingLeft + miuiClock.paddingRight
                }
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
            in setOf(clock, big_time, CUSTOM_VIEW_ID) -> {
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