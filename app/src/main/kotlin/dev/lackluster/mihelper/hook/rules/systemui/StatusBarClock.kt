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

package dev.lackluster.mihelper.hook.rules.systemui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt

object StatusBarClock : YukiBaseHooker() {
    private const val miuiClockClazz = "com.android.systemui.statusbar.views.MiuiClock"
    private val clockShowAMPM by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_CLOCK_SHOW_AMPM, false)
    }
    private val clockShowLeadingZero by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_CLOCK_SHOW_LEADING_ZERO, false)
    }
    private val clockShowSecond by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_CLOCK_SHOW_SECONDS, false)
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
    private val clockFormatName24 by lazy {
        if (clockShowSecond) { "fmt_time_24hour_minute_second" }
        else { "fmt_time_24hour_minute" }
    }
    private val clockPaddingCustom by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_CLOCK_CUSTOM, false)
    }
    private val clockPaddingLeft by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_LEFT, 0)
    }
    private val clockPaddingRight by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_RIGHT, 0)
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        if (clockPaddingCustom || clockShowSecond) {
            miuiClockClazz.toClass()
                .constructor {
                    paramCount = 3
                }
                .hook {
                    after {
                        val miuiClock = this.instance as TextView
                        val clockName = miuiClock.resources.getResourceEntryName(miuiClock.id)
                        if (clockPaddingCustom) {
                            val scale = miuiClock.context.resources.displayMetrics.density
                            miuiClock.setPadding(
                                (clockPaddingLeft * scale).roundToInt(),
                                miuiClock.paddingTop,
                                (clockPaddingRight * scale).roundToInt(),
                                miuiClock.paddingBottom
                            )
                        }
                        if (clockShowSecond && clockName in setOf("clock", "big_time", "horizontal_time")) {
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
        if (clockShowAMPM || clockShowLeadingZero || clockShowSecond) {
            miuiClockClazz.toClass()
                .method {
                    name = "updateTime"
                }
                .hook {
                    before {
                        handleUpdateTime(this)
                    }
                }
            "com.android.systemui.statusbar.views.MiuiStatusBarClock".toClass()
                .method {
                    name = "updateTime"
                }
                .hook {
                    before {
                        handleUpdateTime(this)
                    }
                }
        }
        hasEnable(PrefKey.STATUSBAR_CLOCK_COLOR_FIX) {
            "com.android.systemui.controlcenter.phone.widget.NotificationShadeFakeStatusBarClock".toClass()
                .method {
                    name = "updateHeaderColor"
                }
                .hook {
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
        val clockMode = param.instance.current().field {
            name = "mClockMode"
            superClass()
        }.int()
        if (clockMode == 0 || clockMode == 2) {
            val miuiClock = param.instance as TextView
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
            val fmtName = if (clockMode == 0) {
                if (is24) { clockFormatName24 } else { clockFormatName12 }
            }
            else {
                if (is24) { "status_bar_clock_date_time_format" } else { "status_bar_clock_date_time_format_12" }
            }
            var fmtString = context.getString(context.resources.getIdentifier(fmtName, "string", context.packageName))
            if (clockShowLeadingZero) {
                fmtString = fmtString.replaceFirst(Regex("${hourStr}+:"), "${hourStr}${hourStr}:")
            }
            if (clockShowSecond) {
                if (clockMode == 2) {
                    fmtString = fmtString.replaceFirst(Regex(":mm"), ":mm:ss")
                }
                else if (clockShowSecond) {
                    fmtString = fmtString.replaceFirst(Regex(":s\$"), ":ss")
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
}