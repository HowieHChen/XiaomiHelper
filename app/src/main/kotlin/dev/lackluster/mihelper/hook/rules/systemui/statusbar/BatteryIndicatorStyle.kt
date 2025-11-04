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

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.REAL_STYLE_ICON_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.REAL_STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.REAL_STYLE_LINE
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.REAL_STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_DEFAULT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_ICON_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_LINE
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_HIDDEN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.TAG_POSITION_CONTROL_CENTER
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiBatteryMeterView
import dev.lackluster.mihelper.utils.Prefs
import androidx.core.view.isNotEmpty

object BatteryIndicatorStyle : YukiBaseHooker() {
    private const val KEY_STYLE = "KEY_STYLE"
    private const val KEY_STYLE_REAL = "KEY_STYLE_REAL"
    private val batteryStyle = Prefs.getInt(Pref.Key.SystemUI.IconTuner.BATTERY_STYLE, 0)
    private val batteryStyleCC = Prefs.getInt(Pref.Key.SystemUI.IconTuner.BATTERY_STYLE_CC, 0)

    override fun onHook() {
        if (batteryStyle == STYLE_DEFAULT && batteryStyleCC == STYLE_DEFAULT) return
        clzMiuiBatteryMeterView?.apply {
            val mLayoutFromTag = resolve().firstFieldOrNull {
                name = "mLayoutFromTag"
            }
            val mBatteryDigitalView = resolve().firstFieldOrNull {
                name = "mBatteryDigitalView"
            }
            val mBatteryPercentContainer = resolve().firstFieldOrNull {
                name = "mBatteryPercentContainer"
            }
            val mToAod = resolve().firstFieldOrNull {
                name = "mToAod"
            }
            resolve().firstMethodOrNull {
                name = "setLayoutFromTag"
            }?.hook {
                after {
                    val style: Int
                    val allowLineStyle: Boolean
                    when (mLayoutFromTag?.copy()?.of(this.instance)?.get<Int>()) {
                        TAG_POSITION_CONTROL_CENTER -> {
                            style = batteryStyleCC
                            allowLineStyle = false
                        }
                        else -> {
                            style = batteryStyle
                            allowLineStyle = true
                        }
                    }
                    val realStyle = mapToRealStyle(style, allowLineStyle)
                    if (style != STYLE_DEFAULT) {
                        attachStyleInfo(this.instance, style, realStyle)
                    }
                    if (style == STYLE_TEXT_ONLY) {
                        val batteryDigitalView = mBatteryDigitalView?.copy()?.of(this.instance)?.get<ViewGroup>()
                        val batteryPercentContainer = mBatteryPercentContainer?.copy()?.of(this.instance)?.get<ViewGroup>()
                        if (batteryDigitalView != null && batteryPercentContainer?.isNotEmpty() == true) {
                            val children = List(batteryPercentContainer.childCount) {
                                batteryPercentContainer[it]
                            }
                            val context = batteryDigitalView.context
                            batteryDigitalView.removeAllViews()
                            batteryPercentContainer.removeAllViews()
                            val params = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            batteryDigitalView.addView(
                                LinearLayout(context).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    layoutDirection = LinearLayout.LAYOUT_DIRECTION_LTR
                                    layoutParams = params
                                    children.forEachIndexed { index, view ->
                                        addView(view, index, params)
                                    }
                                },
                                params
                            )
                            this.instance<View>().requestLayout()
                        }
                    }
                    if (style == STYLE_HIDDEN) {
                        this.instance<View>().visibility = View.GONE
                    }
                }
            }
            resolve().firstMethodOrNull {
                name = "onBatteryStyleChanged"
            }?.hook {
                before {
                    val realStyle = getRealStyle(this.instance)
                    realStyle?.let {
                        this.args(0).set(it)
                    }
                    if (getStyle(this.instance) == STYLE_TEXT_ONLY) {
                        mToAod?.copy()?.of(this.instance)?.set(false)
                    }
                }
            }
            if (batteryStyle == STYLE_HIDDEN || batteryStyleCC == STYLE_HIDDEN) {
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateVisibility")
                    }
                }?.hook {
                    before {
                        if (getStyle(this.instance) == STYLE_HIDDEN) {
                            this.result = null
                        }
                    }
                }
            }
        }
    }

    private fun mapToRealStyle(style: Int, allowLineStyle: Boolean): Int {
        return when (style) {
            STYLE_ICON_ONLY -> REAL_STYLE_ICON_ONLY
            STYLE_TEXT_IN -> REAL_STYLE_TEXT_IN
            STYLE_LINE -> if (allowLineStyle) REAL_STYLE_LINE else REAL_STYLE_ICON_ONLY
            STYLE_TEXT_OUT -> REAL_STYLE_TEXT_OUT
            STYLE_TEXT_ONLY -> REAL_STYLE_TEXT_IN
            else -> REAL_STYLE_ICON_ONLY
        }
    }

    private fun attachStyleInfo(instance: Any, style: Int, realStyle: Int) {
        XposedHelpers.setAdditionalInstanceField(instance, KEY_STYLE, style)
        XposedHelpers.setAdditionalInstanceField(instance, KEY_STYLE_REAL, realStyle)
    }

    private fun getStyle(instance: Any): Int {
        return XposedHelpers.getAdditionalInstanceField(instance, KEY_STYLE) as? Int ?: STYLE_DEFAULT
    }

    private fun getRealStyle(instance: Any): Int? {
        return XposedHelpers.getAdditionalInstanceField(instance, KEY_STYLE_REAL) as? Int
    }
}