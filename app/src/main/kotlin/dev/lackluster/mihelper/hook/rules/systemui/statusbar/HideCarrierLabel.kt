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

import android.content.Context
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.lock_screen_carrier_airplane_mode_on
import dev.lackluster.mihelper.utils.Prefs

object HideCarrierLabel : YukiBaseHooker() {
    private val controlCenterHideSimOne = Prefs.getBoolean(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_ONE, false)
    private val controlCenterHideSimTwo = Prefs.getBoolean(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_TWO, false)
    private val controlCenterHideCarrierHD = Prefs.getBoolean(Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_HD, false)
    private val lockscreenHideSimOne = Prefs.getBoolean(Pref.Key.SystemUI.LockScreen.HIDE_CARRIER_ONE, false)
    private val lockscreenHideSimTwo = Prefs.getBoolean(Pref.Key.SystemUI.LockScreen.HIDE_CARRIER_TWO, false)

    private val clzControlCenterCarrierText by lazy {
        "com.android.systemui.controlcenter.shade.ControlCenterCarrierText".toClassOrNull()
    }
    private val clzMiuiCarrierTextController by lazy {
        "com.android.systemui.statusbar.policy.MiuiCarrierTextController".toClassOrNull()
    }
    private val mContext by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mContext"
        }
    }
    private val mAirplane by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mAirplane"
        }
    }
    private val mCustomCarrier by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mCustomCarrier"
        }
    }
    private val mCarrier by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mCarrier"
        }
    }
    private val mSimError by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mSimError"
        }
    }
    private val mCardDisable by lazy {
        clzMiuiCarrierTextController?.resolve()?.firstFieldOrNull {
            name = "mCardDisable"
        }
    }

    override fun onHook() {
        if (controlCenterHideCarrierHD) {
            clzControlCenterCarrierText?.apply {
                resolve().firstMethodOrNull {
                    name = "updateHDText"
                }?.hook {
                    before {
                        this.args(0).set(false) // hd
                        this.args(1).set(false) // plus
                    }
                }
            }
        }
        if (controlCenterHideSimOne || controlCenterHideSimTwo) {
            "com.android.systemui.controlcenter.shade.ControlCenterCarrierText\$mCarrierTextCallback$1".toClassOrNull()?.apply {
                val carrierTextView = resolve().firstFieldOrNull {
                    type("com.android.systemui.controlcenter.shade.ControlCenterCarrierText")
                }
                resolve().firstMethodOrNull {
                    name = "onCarrierTextChanged"
                }?.hook {
                    before {
                        val id = carrierTextView?.copy()?.of(this.instance)?.get<View>()?.id ?: return@before
                        if (
                            controlCenterHideSimOne && id == ResourcesUtils.normal_control_center_carrier_view ||
                            controlCenterHideSimTwo && id == ResourcesUtils.normal_control_center_carrier_second_view
                        ) {
                            this.args(2).set("")
                        }
                    }
                }
            }
        }
        if (lockscreenHideSimOne || lockscreenHideSimTwo) {
            clzMiuiKeyguardStatusBarView?.apply {
                val mDep = resolve().firstFieldOrNull {
                    name = "mDep"
                }
                val carrierTextController = "com.android.systemui.statusbar.phone.KeyguardStatusBarViewControllerInject".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "carrierTextController"
                    }
                resolve().firstMethodOrNull {
                    name = "onCarrierTextChanged"
                    parameters(Int::class, String::class)
                }?.hook {
                    before {
                        if (lockscreenHideSimOne && lockscreenHideSimTwo) {
                            this.args(1).set("")
                        } else {
                            val dep = mDep?.copy()?.of(this.instance)?.get() ?: return@before
                            val carrierController = carrierTextController?.copy()?.of(dep)?.get() ?: return@before
                            val airplaneMode = mAirplane?.copy()?.of(carrierController)?.get<Boolean>() == true
                            if (airplaneMode) {
                                val context = mContext?.copy()?.of(carrierController)?.get<Context>()
                                this.args(1).set(context?.getString(lock_screen_carrier_airplane_mode_on))
                            } else {
                                val simError = mSimError?.copy()?.of(carrierController)?.get<BooleanArray>()
                                val cardDisable = mCardDisable?.copy()?.of(carrierController)?.get<BooleanArray>()
                                val customCarrier = mCustomCarrier?.copy()?.of(carrierController)?.get<Array<String?>>()
                                val carrier = mCarrier?.copy()?.of(carrierController)?.get<Array<String?>>()
                                val showSlotIndex = if (lockscreenHideSimOne) 1 else 0
                                this.args(1).set(
                                    if (
                                        (simError == null || simError.size <= showSlotIndex || simError[showSlotIndex]) ||
                                        (cardDisable == null || cardDisable.size <= showSlotIndex || cardDisable[showSlotIndex])
                                    ) {
                                        ""
                                    } else if (
                                        customCarrier != null && customCarrier.size > showSlotIndex &&
                                        !customCarrier[showSlotIndex].isNullOrEmpty()
                                    ) {
                                        customCarrier[showSlotIndex]
                                    } else if (
                                        carrier != null && carrier.size > showSlotIndex &&
                                        !carrier[showSlotIndex].isNullOrEmpty()
                                    ) {
                                        carrier[showSlotIndex]
                                    } else {
                                        ""
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}