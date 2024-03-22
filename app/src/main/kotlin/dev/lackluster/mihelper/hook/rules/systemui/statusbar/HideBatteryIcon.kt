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

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import kotlin.math.roundToInt

object HideBatteryIcon : YukiBaseHooker() {
    private val hideBattery by lazy {
        Prefs.getBoolean(IconTurner.HIDE_BATTERY, false)
    }
    private val hideBatteryPercent by lazy {
        Prefs.getBoolean(IconTurner.HIDE_BATTERY_PERCENT, false)
    }
    private val batteryPercentMarkSize by lazy {
        Prefs.getFloat(IconTurner.BATTERY_PERCENT_SIZE, 0f)
    }
    private val batteryPaddingLeft by lazy {
        Prefs.getInt(IconTurner.BATTERY_PADDING_LEFT, 0)
    }
    private val batteryPaddingRight by lazy {
        Prefs.getInt(IconTurner.BATTERY_PADDING_RIGHT, 0)
    }
    override fun onHook() {
        "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClass().method {
            name = "updateAll"
        }.hook {
            after {
                val batteryPercentView = this.instance.current().field {
                    name = "mBatteryPercentView"
                }.any() as? TextView ?: return@after
                val batteryPercentMarkView = this.instance.current().field {
                    name = "mBatteryPercentMarkView"
                }.any() as? TextView ?: return@after
                // Hide Battery Icon
                if (hideBattery) {
                    (this.instance.current().field {
                        name = "mBatteryIconView"
                    }.any() as? ImageView)?.visibility = View.GONE
                    if (
                        this.instance.current().field {
                            name = "mBatteryStyle"
                        }.int() == 1
                    ) {
                        (this.instance.current().field {
                            name = "mBatteryDigitalView"
                        }.any() as? FrameLayout)?.visibility = View.GONE
                    }
                }
                // Modify the font size of the battery percentage numbers
                hasEnable(IconTurner.CHANGE_BATTERY_PERCENT_SIZE, extraCondition = { batteryPercentMarkSize > 0}) {
                    batteryPercentView.setTextSize(0, batteryPercentMarkSize)
                }
                if (hideBatteryPercent) {
                    // Hide Percentage Symbol
                    (this.instance.current().field {
                        name = "mBatteryPercentMarkView"
                    }.any() as? TextView)?.textSize = 0f
                } else if (Prefs.getBoolean(IconTurner.CHANGE_BATTERY_PERCENT_MARK, false)) {
                    // Align the text size of the percentage sign with the number
                    batteryPercentMarkView.layoutParams = batteryPercentView.layoutParams
                    batteryPercentMarkView.typeface = batteryPercentView.typeface
                    batteryPercentMarkView.setTextSize(0, batteryPercentView.textSize)
                    batteryPercentMarkView.setPadding(0,0,0,0)
                }
                if (
                    Prefs.getBoolean(IconTurner.SWAP_BATTERY_PERCENT, false) &&
                    !hideBattery
                ) {
                    val batteryView = this.instance as LinearLayout
                    batteryView.removeView(batteryPercentView)
                    batteryView.removeView(batteryPercentMarkView)
                    batteryView.addView(batteryPercentMarkView, 0)
                    batteryView.addView(batteryPercentView, 0)
                }
                hasEnable(IconTurner.BATTERY_CUSTOM_LAYOUT) {
                    val batteryView = this.instance as LinearLayout
                    val scale = batteryView.context.resources.displayMetrics.density
                    batteryView.setPadding(
                        (batteryPaddingLeft * scale).roundToInt(),//batteryView.paddingLeft,
                        batteryView.paddingTop,
                        (batteryPaddingRight * scale).roundToInt(),//batteryView.paddingRight,
                        batteryView.paddingBottom
                    )
                }
            }
        }
        hasEnable(IconTurner.HIDE_CHARGE) {
            "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClass().method {
                name = "updateChargeAndText"
            }.hook {
                after {
                    (this.instance.current().field {
                        name = "mBatteryChargingInView"
                    }.any() as? ImageView)?.visibility = View.GONE
                    (this.instance.current().field {
                        name = "mBatteryChargingView"
                    }.any() as? ImageView)?.visibility = View.GONE
                }
            }
        }
    }
}