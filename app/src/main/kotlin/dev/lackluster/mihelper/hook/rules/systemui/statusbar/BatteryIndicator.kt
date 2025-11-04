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

import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_DEFAULT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_DIGITAL
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_HIDDEN
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.TextAppearance_StatusBar_Battery_Percent
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiBatteryMeterView
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.fontPath
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat

object BatteryIndicator : YukiBaseHooker() {
    private val hideChargeOut = Prefs.getBoolean(IconTuner.HIDE_BATTERY_CHARGE_OUT, false)
    private val percentMarkStyle = Prefs.getInt(IconTuner.BATTERY_PERCENT_MARK_STYLE, 0)
    // Padding
    private val valuePaddingStart = Prefs.getFloat(IconTuner.BATTERY_PADDING_START_VAL, 0.0f)
    private val valuePaddingEnd = Prefs.getFloat(IconTuner.BATTERY_PADDING_END_VAL, 0.0f)
    private val modifyPadding =
        Prefs.getBoolean(IconTuner.BATTERY_PADDING_HORIZON, false)
    // Percentage Text Size
    private val valuePercentInSize = Prefs.getFloat(IconTuner.BATTERY_PERCENT_IN_SIZE_VAL, 9.599976f)
    private val valuePercentOutSize = Prefs.getFloat(IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL, 12.5f)
    private val modifyPercentInSize =
        Prefs.getBoolean(IconTuner.BATTERY_PERCENT_IN_SIZE, false) && valuePercentInSize > 0
    private val modifyPercentOutSize =
        Prefs.getBoolean(IconTuner.BATTERY_PERCENT_OUT_SIZE, false) && valuePercentOutSize > 0
    // Font Weight
    private val valuePercentInFW = Prefs.getInt(FontWeight.BATTERY_PERCENTAGE_IN_VAL, 620)
    private val valuePercentOutFW = Prefs.getInt(FontWeight.BATTERY_PERCENTAGE_OUT_VAL, 500)
    private val valuePercentMarkFW = Prefs.getInt(FontWeight.BATTERY_PERCENTAGE_MARK_VAL, 600)
    private val modifyPercentInFW =
        Prefs.getBoolean(FontWeight.BATTERY_PERCENTAGE_IN, false) && valuePercentInFW in 1..1000
    private val modifyPercentOutFW =
        Prefs.getBoolean(FontWeight.BATTERY_PERCENTAGE_OUT, false) && valuePercentOutFW in 1..1000
    private val modifyPercentMarkFW =
        Prefs.getBoolean(FontWeight.BATTERY_PERCENTAGE_MARK, false) && valuePercentMarkFW in 1..1000
    private val typefacePercentInFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valuePercentInFW").build()
    }
    private val typefacePercentOutFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valuePercentOutFW").build()
    }
    private val typefacePercentMarkFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valuePercentMarkFW").build()
    }

    private val mBatteryPercentMarkView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryPercentMarkView"
        }
    }
    private val mBatteryPercentView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryPercentView"
        }
    }
    private val mBatteryChargingView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryChargingView"
        }
    }

    override fun onHook() {
        if (percentMarkStyle != PERCENT_MARK_STYLE_DEFAULT || modifyPercentOutFW || modifyPercentMarkFW || modifyPercentOutSize) {
            clzMiuiBatteryMeterView?.resolve()?.firstMethodOrNull {
                name = "updateAll"
            }?.hook {
                after {
                    val batteryPercentView = mBatteryPercentView?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    val batteryPercentMarkView = mBatteryPercentMarkView?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL) {
                        batteryPercentMarkView.let {
                            it.setPadding(0,0,0,0)
                            it.setTextAppearance(TextAppearance_StatusBar_Battery_Percent)
                        }
                    } else if (percentMarkStyle == PERCENT_MARK_STYLE_HIDDEN) {
                        batteryPercentMarkView.visibility = View.GONE
                    }
                    if (modifyPercentOutFW) {
                        batteryPercentView.typeface = typefacePercentOutFW
                    }
                    if (modifyPercentMarkFW) {
                        batteryPercentMarkView.typeface = typefacePercentMarkFW
                    }
                    if (modifyPercentOutSize) {
                        batteryPercentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valuePercentOutSize)
                        if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL) {
                            batteryPercentMarkView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valuePercentOutSize)
                        }
                    }
                }
            }
        }
        if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL || modifyPercentOutFW || modifyPercentMarkFW) {
            clzMiuiBatteryMeterView?.resolve()?.firstMethodOrNull {
                name = "onChanged"
            }?.hook {
                after {
                    val batteryPercentView = mBatteryPercentView?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    val batteryPercentMarkView = mBatteryPercentMarkView?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL) {
                        batteryPercentMarkView.typeface = batteryPercentView.typeface
                    }
                    if (modifyPercentOutFW) {
                        batteryPercentView.typeface = typefacePercentOutFW
                    }
                    if (modifyPercentMarkFW) {
                        batteryPercentMarkView.typeface = typefacePercentMarkFW
                    }
                }
            }
        }
        if (modifyPercentInFW || modifyPercentInSize) {
            "com.android.systemui.statusbar.views.MiuiHollowBatteryMeterIconView".toClassOrNull()?.apply {
                val textPaint = resolve().firstFieldOrNull {
                    name = "textPaint"
                }
                val hollowTextPaint = resolve().firstFieldOrNull {
                    name = "hollowTextPaint"
                }
                resolve().firstMethodOrNull {
                    name = "updateResources"
                }?.hook {
                    after {
                        val context = this.instance<View>().context ?: return@after
                        val paintText = textPaint?.copy()?.of(this.instance)?.get<Paint>() ?: return@after
                        val paintHollowText = hollowTextPaint?.copy()?.of(this.instance)?.get<Paint>() ?: return@after
                        if (modifyPercentInFW) {
                            paintText.typeface = typefacePercentInFW
                            paintHollowText.typeface = typefacePercentInFW
                        }
                        if (modifyPercentInSize) {
                            paintText.textSize = valuePercentInSize.dpFloat(context)
                            paintHollowText.textSize = valuePercentInSize.dpFloat(context)
                        }
                    }
                }
            }
        }
        if (modifyPadding || hideChargeOut) {
            clzMiuiBatteryMeterView?.resolve()?.firstConstructor {
                parameterCount = 3
            }?.hook {
                after {
                    if (modifyPadding) {
                        this.instance<View>().apply {
                            updatePaddingRelative(
                                start = valuePaddingStart.dp(context),
                                end = valuePaddingEnd.dp(context),
                            )
                        }
                    }
                    if (hideChargeOut) {
                        mBatteryChargingView?.copy()?.of(this.instance)?.get<View>()?.apply {
                            layoutParams = LinearLayout.LayoutParams(0, 0)
                        }
                    }
                }
            }
        }
    }
}