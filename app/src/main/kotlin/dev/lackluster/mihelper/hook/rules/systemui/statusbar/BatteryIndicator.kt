/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2024 HowieHChen, howie.dev@outlook.com

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

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.roundToInt

object BatteryIndicator : YukiBaseHooker() {
    private val miuiBatteryMeterViewClz by lazy {
        "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClass()
    }
    // 0 -> Default; 1 -> Icon & Percentage; 2 -> Icon only; 3 -> Percentage only; 4 -> Hidden
    private val batteryStyle by lazy {
        Prefs.getInt(IconTurner.BATTERY_STYLE, 0)
    }
    // 0 -> Default; 1 -> Percentage number style; 2 -> Hidden
    private val percentageSymbolStyle by lazy {
        Prefs.getInt(IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, 0)
    }
    private val hideChargeIcon by lazy {
        Prefs.getBoolean(IconTurner.HIDE_CHARGE, false)
    }
    private val modifyPercentageTextSize by lazy {
        Prefs.getBoolean(IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE, false)
    }
    private val batteryPercentTextSize by lazy {
        Prefs.getFloat(IconTurner.BATTERY_PERCENTAGE_TEXT_SIZE, 13.454498f)
    }
    private val batteryPercentTNum by lazy {
        Prefs.getBoolean(IconTurner.BATTERY_PERCENTAGE_TNUM, false)
    }
    private val swapIconAndPercentage by lazy {
        Prefs.getBoolean(IconTurner.SWAP_BATTERY_PERCENT, false)
    }
    private val modifyIndicatorPadding by lazy {
        Prefs.getBoolean(IconTurner.BATTERY_MODIFY_PADDING, false)
    }
    private val batteryPaddingLeft by lazy {
        Prefs.getFloat(IconTurner.BATTERY_PADDING_LEFT, 0.0f)
    }
    private val batteryPaddingRight by lazy {
        Prefs.getFloat(IconTurner.BATTERY_PADDING_RIGHT, 0.0f)
    }
    override fun onHook() {
        miuiBatteryMeterViewClz.apply {
            method {
                name = "updateAll\$1"
            }.remedys {
                method {
                    name = "updateAll"
                }
            }.hook {
                after {
                    val mBatteryIconView = this.instance.current().field {
                        name = "mBatteryIconView"
                    }.any() as? ImageView ?: return@after
                    // mBatteryStyle: 0 -> Graphical; 1 -> Percentage (in the icon); 2 -> Top bar; 3 -> Percentage (next to the icon)
//                    val mBatteryStyle = this.instance.current().field {
//                        name = "mBatteryStyle"
//                    }.int()
                    val mBatteryPercentView = this.instance.current().field {
                        name = "mBatteryPercentView"
                    }.any() as? TextView ?: return@after // mBatteryStyle == 3
                    val mBatteryPercentMarkView = this.instance.current().field {
                        name = "mBatteryPercentMarkView"
                    }.any() as? TextView ?: return@after // mBatteryStyle == 3
                    // Battery icon container
//                    val mBatteryDigitalView = this.instance.current().field {
//                        name = "mBatteryDigitalView"
//                    }.any() as? FrameLayout ?: return@after
                    // Visibility of battery icon
                    if (batteryStyle == 1 || batteryStyle == 2) {
                        mBatteryIconView.visibility = View.VISIBLE
                    } else if (batteryStyle == 3 || batteryStyle == 4) {
                        mBatteryIconView.visibility = View.GONE
                    }
                    // Battery percentage
                    if (batteryStyle in setOf(0, 1, 3)) {
                        if (modifyPercentageTextSize) {
                            mBatteryPercentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, batteryPercentTextSize)
                        }
                        if (batteryPercentTNum) {
                            mBatteryPercentView.fontFeatureSettings = "tnum"
                        }
                        when (percentageSymbolStyle) {
                            1 -> {
                                mBatteryPercentMarkView.layoutParams = mBatteryPercentView.layoutParams
                                mBatteryPercentMarkView.typeface = mBatteryPercentView.typeface
                                mBatteryPercentMarkView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBatteryPercentView.textSize)
                                mBatteryPercentMarkView.setPadding(0,0,0,0)
                            }
                            2 -> {
                                mBatteryPercentMarkView.textSize = 0.0f
                            }
                        }
                    }
                    if (batteryStyle == 4) {
                        return@after
                    }
                    val miuiBatteryMeterView = this.instance as LinearLayout
                    if (swapIconAndPercentage && (batteryStyle == 0 || batteryStyle == 1)) {
                        miuiBatteryMeterView.removeView(mBatteryPercentView)
                        miuiBatteryMeterView.removeView(mBatteryPercentMarkView)
                        miuiBatteryMeterView.addView(mBatteryPercentMarkView, 0)
                        miuiBatteryMeterView.addView(mBatteryPercentView, 0)
                    }
                    if (modifyIndicatorPadding) {
                        val density = miuiBatteryMeterView.context.resources.displayMetrics.density
                        miuiBatteryMeterView.setPadding(
                            (batteryPaddingLeft * density).roundToInt(),//batteryView.paddingLeft,
                            miuiBatteryMeterView.paddingTop,
                            (batteryPaddingRight * density).roundToInt(),//batteryView.paddingRight,
                            miuiBatteryMeterView.paddingBottom
                        )
                    }
                }
            }
            method {
                name = "updateChargeAndText"
            }.hook {
                after {
                    if (hideChargeIcon) {
                        // mBatteryStyle == 0 || mBatteryStyle == 3
                        (this.instance.current().field {
                            name = "mBatteryChargingInView"
                        }.any() as? ImageView)?.visibility = View.GONE
                        // mBatteryStyle == 1 || mBatteryStyle == 2
                        (this.instance.current().field {
                            name = "mBatteryChargingView"
                        }.any() as? ImageView)?.visibility = View.GONE
                    }
                    if (batteryStyle != 0) {
                        val mBatteryPercentView = this.instance.current().field {
                            name = "mBatteryPercentView"
                        }.any() as? TextView ?: return@after // mBatteryStyle == 3
                        val mBatteryPercentMarkView = this.instance.current().field {
                            name = "mBatteryPercentMarkView"
                        }.any() as? TextView ?: return@after // mBatteryStyle == 3
                        // Visibility of battery percentage
                        if (batteryStyle == 1 || batteryStyle == 3) {
                            mBatteryPercentView.visibility = View.VISIBLE
                            mBatteryPercentMarkView.visibility = if (percentageSymbolStyle == 2) View.GONE else View.VISIBLE
                        } else if (batteryStyle == 2 || batteryStyle == 4) {
                            mBatteryPercentView.visibility = View.GONE
                            mBatteryPercentMarkView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}