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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.dp2px
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs

object CarrierText : YukiBaseHooker() {
    private val miuiClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClass()
    }
    private val carrierTextType = Prefs.getInt(Pref.Key.SystemUI.LockScreen.CARRIER_TEXT, 0)

    override fun onHook() {
        if (carrierTextType != 0) {
            "com.android.systemui.statusbar.phone.MiuiKeyguardStatusBarView".toClass().method {
                name = "onFinishInflate"
            }.hook {
                after {
                    val view = this.instance as View
                    val carrierText = view.findViewById<TextView>(ResourcesUtils.keyguard_carrier_text) ?: return@after
                    carrierText.visibility = View.VISIBLE
                    if (carrierTextType == 2) {
                        val context = view.context
                        val parent = carrierText.parent as? ViewGroup ?: return@after
                        val clockContainer = LinearLayout(context).apply {
                            clipChildren = false
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        }
                        val padClock = LayoutInflater.from(context).inflate(ResourcesUtils.pad_clock_xml, null) as TextView
                        val clockView = miuiClockClass.constructor {
                            paramCount = 3
                        }.get().call(context, null, -1) as TextView
                        clockView.setTextAppearance(ResourcesUtils.TextAppearance_StatusBar_Clock)
                        clockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, padClock.textSize)
                        clockView.typeface = padClock.typeface
                        clockView.gravity = Gravity.CENTER or Gravity.START
                        clockView.isSingleLine = true
                        val clockMarginStart = ResourcesUtils.status_bar_padding_extra_start.takeIf { it != 0 }?.let {
                            context.resources.getDimensionPixelSize(it)
                        } ?: 0
                        val clockMarginEnd = ResourcesUtils.status_bar_clock_margin_end.takeIf { it != 0 }?.let {
                            context.resources.getDimensionPixelSize(it)
                        } ?: dp2px(context, 4.0f)
                        clockContainer.addView(
                            clockView,
                            ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            ).apply {
                                marginStart = clockMarginStart
                                marginEnd = clockMarginEnd
                            }
                        )
                        if (Device.isPad)
                            clockContainer.addView(
                                padClock,
                                ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                ).apply {
                                    marginStart = clockMarginStart
                                    marginEnd = clockMarginEnd
                                }
                            )
                        if (parent is FrameLayout) {
                            parent.addView(
                                clockContainer,
                                parent.indexOfChild(carrierText)
                            )
                            parent.removeView(carrierText)
                        }
                    }
                }
            }
        }
        if (carrierTextType == 2) {
            miuiClockClass.constructor {
                paramCount = 3
            }.hook {
                after {
                    val miuiClock = this.instance as TextView
                    if (this.args(2).int() == -1) {
                        miuiClock.id = ResourcesUtils.clock
                    }
                }
            }
        }
    }

}