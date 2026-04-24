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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object CarrierLabelFontWeight : StaticHooker() {
    private val valueLockScreenCarrierFW by Preferences.SystemUI.StatusBar.Font.LOCK_SCREEN_CARRIER_WEIGHT.lazyGet()
    private val modifyLockScreenCarrierFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_LOCK_SCREEN_CARRIER.get() && valueLockScreenCarrierFW in 1..1000
    }
    private val typefaceLockScreenCarrierFW by lazy {
        getTypeface(valueLockScreenCarrierFW)
    }
    private val clzMiuiCarrierTextLayout by "com.android.systemui.controlcenter.shade.MiuiCarrierTextLayout".lazyClassOrNull()
    private val fldLeftCarrierTextView by lazy {
        clzMiuiCarrierTextLayout?.resolve()?.firstFieldOrNull {
            name = "leftCarrierTextView"
        }?.toTyped<Any>()
    }
    private val fldRightCarrierTextView by lazy {
        clzMiuiCarrierTextLayout?.resolve()?.firstFieldOrNull {
            name = "rightCarrierTextView"
        }?.toTyped<Any>()
    }
    private val metGetCarrierTextView by lazy {
        "com.android.systemui.controlcenter.shade.ControlCenterCarrierText".toClassOrNull()
            ?.resolve()?.firstMethodOrNull {
                name = "getCarrierTextView"
            }?.toTyped<TextView>()
    }

    override fun onInit() {
        updateSelfState(modifyLockScreenCarrierFW)
    }

    override fun onHook() {
        clzMiuiKeyguardStatusBarView?.apply {
            val mCarrierLabel = resolve().optional(true).firstFieldOrNull {
                name = "mCarrierLabel"
            }?.toTyped<TextView>()
            val mCarrierTextLayout = resolve().optional(true).firstFieldOrNull {
                name = "mCarrierTextLayout"
            }?.toTyped<Any>()
            resolve().method {
                name {
                    it == "onDensityOrFontScaleChanged" || it.startsWith("onMiuiThemeChanged")
                }
            }.hookAll {
                val ori = proceed()
                mCarrierLabel?.get(thisObject)?.typeface = typefaceLockScreenCarrierFW
                mCarrierTextLayout?.get(thisObject)?.let {
                    listOfNotNull(
                        fldLeftCarrierTextView?.get(it),
                        fldRightCarrierTextView?.get(it)
                    ).forEach { carrierLabel ->
                        metGetCarrierTextView?.invoke(carrierLabel)?.typeface = typefaceLockScreenCarrierFW
                    }
                }
                result(ori)
            }
        }
    }
}