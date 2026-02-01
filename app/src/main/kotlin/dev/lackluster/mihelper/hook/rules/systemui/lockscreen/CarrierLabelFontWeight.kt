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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.utils.Prefs

object CarrierLabelFontWeight : YukiBaseHooker() {
    private val valueLockScreenCarrierFW = Prefs.getInt(FontWeight.LOCK_SCREEN_CARRIER_WEIGHT, 430)
    private val modifyLockScreenCarrierFW =
        Prefs.getBoolean(FontWeight.LOCK_SCREEN_CARRIER, false) && valueLockScreenCarrierFW in 1..1000
    private val typefaceLockScreenCarrierFW by lazy {
        getTypeface(valueLockScreenCarrierFW)
    }
    private val clzMiuiCarrierTextLayout by lazy {
        "com.android.systemui.controlcenter.shade.MiuiCarrierTextLayout".toClassOrNull()
    }
    private val fldLeftCarrierTextView by lazy {
        clzMiuiCarrierTextLayout?.resolve()?.firstFieldOrNull {
            name = "leftCarrierTextView"
        }
    }
    private val fldRightCarrierTextView by lazy {
        clzMiuiCarrierTextLayout?.resolve()?.firstFieldOrNull {
            name = "rightCarrierTextView"
        }
    }
    private val metGetCarrierTextView by lazy {
        "com.android.systemui.controlcenter.shade.ControlCenterCarrierText".toClassOrNull()
            ?.resolve()?.firstMethodOrNull {
                name = "getCarrierTextView"
            }
    }

    override fun onHook() {
        if (modifyLockScreenCarrierFW) {
            clzMiuiKeyguardStatusBarView?.apply {
                val mCarrierLabel = resolve().optional(true).firstFieldOrNull {
                    name = "mCarrierLabel"
                }
                val mCarrierTextLayout = resolve().optional(true).firstFieldOrNull {
                    name = "mCarrierTextLayout"
                }
                resolve().method {
                    name {
                        it == "onDensityOrFontScaleChanged" || it.startsWith("onMiuiThemeChanged")
                    }
                }.hookAll {
                    after {
                        mCarrierLabel?.copy()?.of(this.instance)?.get<TextView>()?.typeface = typefaceLockScreenCarrierFW
                        mCarrierTextLayout?.copy()?.of(this.instance)?.let {
                            listOfNotNull(
                                fldLeftCarrierTextView?.copy()?.of(it)?.get(),
                                fldRightCarrierTextView?.copy()?.of(it)?.get()
                            ).forEach { carrierLabel ->
                                metGetCarrierTextView?.copy()?.of(carrierLabel)?.invoke<TextView>()?.typeface = typefaceLockScreenCarrierFW
                            }
                        }
                    }
                }
            }
        }
    }
}