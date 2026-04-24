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
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_DEFAULT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_DIGITAL
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.PERCENT_MARK_STYLE_HIDDEN
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.TextAppearance_StatusBar_Battery_Percent
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiBatteryMeterView
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat

object BatteryIndicator : StaticHooker() {
    private val hideChargeOut by Preferences.SystemUI.StatusBar.IconDetail.HIDE_BATTERY_CHARGE_OUT.lazyGet()
    private val percentMarkStyle by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_MARK_STYLE.lazyGet()
    // Padding
    private val valuePaddingStart by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_START_VAL.lazyGet()
    private val valuePaddingEnd by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_END_VAL.lazyGet()
    private val modifyPadding by Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PADDING_HORIZON.lazyGet()
    // Percentage Text Size
    private val valuePercentInSize by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_IN_SIZE_VAL.lazyGet()
    private val valuePercentOutSize by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_OUT_SIZE_VAL.lazyGet()
    private val modifyPercentInSize by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_IN_SIZE.get() && valuePercentInSize > 0
    }
    private val modifyPercentOutSize by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_OUT_SIZE.get() && valuePercentOutSize > 0
    }
    // Font Weight
    private val valuePercentInFW by Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_IN_WEIGHT.lazyGet()
    private val valuePercentOutFW by Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_OUT_WEIGHT.lazyGet()
    private val valuePercentMarkFW by Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_MARK_WEIGHT.lazyGet()
    private val modifyPercentInFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_IN.get() && valuePercentInFW in 1..1000
    }
    private val modifyPercentOutFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_OUT.get() && valuePercentOutFW in 1..1000
    }
    private val modifyPercentMarkFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_MARK.get() && valuePercentMarkFW in 1..1000
    }
    private val typefacePercentInFW by lazy {
        getTypeface(valuePercentInFW)
    }
    private val typefacePercentOutFW by lazy {
        getTypeface(valuePercentOutFW)
    }
    private val typefacePercentMarkFW by lazy {
        getTypeface(valuePercentMarkFW)
    }

    private val mBatteryPercentMarkView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryPercentMarkView"
        }?.toTyped<TextView>()
    }
    private val mBatteryPercentView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryPercentView"
        }?.toTyped<TextView>()
    }
    private val mBatteryChargingView by lazy {
        clzMiuiBatteryMeterView?.resolve()?.firstFieldOrNull {
            name = "mBatteryChargingView"
        }?.toTyped<View>()
    }

    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        if (percentMarkStyle != PERCENT_MARK_STYLE_DEFAULT || modifyPercentOutFW || modifyPercentMarkFW || modifyPercentOutSize) {
            clzMiuiBatteryMeterView?.resolve()?.firstMethodOrNull {
                name = "updateAll"
            }?.hook {
                val ori = proceed()
                val batteryPercentView = mBatteryPercentView?.get(thisObject) ?: return@hook result(ori)
                val batteryPercentMarkView = mBatteryPercentMarkView?.get(thisObject) ?: return@hook result(ori)
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
                result(ori)
            }
        }
        if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL || modifyPercentOutFW || modifyPercentMarkFW) {
            clzMiuiBatteryMeterView?.resolve()?.firstMethodOrNull {
                name = "onChanged"
            }?.hook {
                val ori = proceed()
                val batteryPercentView = mBatteryPercentView?.get(thisObject) ?: return@hook result(ori)
                val batteryPercentMarkView = mBatteryPercentMarkView?.get(thisObject) ?: return@hook result(ori)
                if (percentMarkStyle == PERCENT_MARK_STYLE_DIGITAL) {
                    batteryPercentMarkView.typeface = batteryPercentView.typeface
                }
                if (modifyPercentOutFW) {
                    batteryPercentView.typeface = typefacePercentOutFW
                }
                if (modifyPercentMarkFW) {
                    batteryPercentMarkView.typeface = typefacePercentMarkFW
                }
                result(ori)
            }
        }
        if (modifyPercentInFW || modifyPercentInSize) {
            "com.android.systemui.statusbar.views.MiuiHollowBatteryMeterIconView".toClassOrNull()?.apply {
                val textPaint = resolve().firstFieldOrNull {
                    name = "textPaint"
                }?.toTyped<Paint>()
                val hollowTextPaint = resolve().firstFieldOrNull {
                    name = "hollowTextPaint"
                }?.toTyped<Paint>()
                resolve().firstMethodOrNull {
                    name = "updateResources"
                }?.hook {
                    val ori = proceed()
                    val context = (thisObject as? View)?.context ?: return@hook result(ori)
                    val paintText = textPaint?.get(thisObject) ?: return@hook result(ori)
                    val paintHollowText = hollowTextPaint?.get(thisObject) ?: return@hook result(ori)
                    if (modifyPercentInFW) {
                        paintText.typeface = typefacePercentInFW
                        paintHollowText.typeface = typefacePercentInFW
                    }
                    if (modifyPercentInSize) {
                        paintText.textSize = valuePercentInSize.dpFloat(context)
                        paintHollowText.textSize = valuePercentInSize.dpFloat(context)
                    }
                    result(ori)
                }
            }
        }
        if (modifyPadding || hideChargeOut) {
            clzMiuiBatteryMeterView?.resolve()?.firstConstructor {
                parameterCount = 3
            }?.hook {
                val ori = proceed()
                if (modifyPadding) {
                    (thisObject as? View)?.apply {
                        updatePaddingRelative(
                            start = valuePaddingStart.dp(context),
                            end = valuePaddingEnd.dp(context),
                        )
                    }
                }
                if (hideChargeOut) {
                    mBatteryChargingView?.get(thisObject)?.apply {
                        layoutParams = LinearLayout.LayoutParams(0, 0)
                    }
                }
                result(ori)
            }
        }
    }
}