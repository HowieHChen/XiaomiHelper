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
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiBatteryMeterView
import androidx.core.view.isNotEmpty
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped

object BatteryIndicatorStyle : StaticHooker() {
    private var View.nowStyle by extraOf<Int>("KEY_STYLE")
    private var View.realStyle by extraOf<Int>("KEY_STYLE_REAL")

    private val batteryStyleBar by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_BAR.lazyGet()
    private val batteryStyleCC by Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_CC.lazyGet()

    override fun onInit() {
        updateSelfState(batteryStyleBar != STYLE_DEFAULT || batteryStyleCC != STYLE_DEFAULT)
    }

    override fun onHook() {
        clzMiuiBatteryMeterView?.apply {
            val mLayoutFromTag = resolve().firstFieldOrNull {
                name = "mLayoutFromTag"
            }?.toTyped<Int>()
            val mBatteryDigitalView = resolve().firstFieldOrNull {
                name = "mBatteryDigitalView"
            }?.toTyped<ViewGroup>()
            val mBatteryPercentContainer = resolve().firstFieldOrNull {
                name = "mBatteryPercentContainer"
            }?.toTyped<ViewGroup>()
            val mToAod = resolve().firstFieldOrNull {
                name = "mToAod"
            }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "setLayoutFromTag"
            }?.hook {
                val ori = proceed()
                val style: Int
                val allowLineStyle: Boolean
                val view = thisObject as? View ?: return@hook result(ori)
                val position = mLayoutFromTag?.get(thisObject)
                when (position) {
                    TAG_POSITION_CONTROL_CENTER -> {
                        style = batteryStyleCC
                        allowLineStyle = false
                    }
                    else -> {
                        style = batteryStyleBar
                        allowLineStyle = true
                    }
                }
                val realStyle = mapToRealStyle(style, allowLineStyle)
                if (style != STYLE_DEFAULT) {
                    view.nowStyle = style
                    view.realStyle = realStyle
                }
                if (style == STYLE_TEXT_ONLY) {
                    val batteryDigitalView = mBatteryDigitalView?.get(thisObject)
                    val batteryPercentContainer = mBatteryPercentContainer?.get(thisObject)
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
                        view.requestLayout()
                    }
                }
                if (style == STYLE_HIDDEN) {
                    (thisObject as? View)?.visibility = View.GONE
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onBatteryStyleChanged"
            }?.hook {
                val view = thisObject as? View ?: return@hook result(proceed())
                val newArgs = args.toTypedArray()
                val realStyle = view.realStyle
                if (realStyle != null) {
                    newArgs[0] = realStyle
                }
                if (view.nowStyle == STYLE_TEXT_ONLY) {
                    mToAod?.set(thisObject, false)
                }
                result(proceed(newArgs))
            }
            if (batteryStyleBar == STYLE_HIDDEN || batteryStyleCC == STYLE_HIDDEN) {
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateVisibility")
                    }
                }?.hook {
                    val view = thisObject as? View
                    if (view?.nowStyle == STYLE_HIDDEN) {
                        result(null)
                    } else {
                        result(proceed())
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
}