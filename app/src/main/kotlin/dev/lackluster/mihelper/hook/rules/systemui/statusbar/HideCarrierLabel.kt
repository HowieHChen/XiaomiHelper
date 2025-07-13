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
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.ControlCenter
import dev.lackluster.mihelper.utils.Prefs

object HideCarrierLabel : YukiBaseHooker() {
    private val hideSimOne = Prefs.getBoolean(ControlCenter.HIDE_CARRIER_ONE, false)
    private val hideSimTwo = Prefs.getBoolean(ControlCenter.HIDE_CARRIER_TWO, false)
    private val hideCarrierHD = Prefs.getBoolean(ControlCenter.HIDE_CARRIER_HD, false)
    private val miuiCarrierTextLayoutClass by lazy {
        "com.android.systemui.controlcenter.shade.MiuiCarrierTextLayout".toClassOrNull()
    }
    private val isNewCarrierTextLayout by lazy {
        miuiCarrierTextLayoutClass != null
    }

    override fun onHook() {
        if (hideCarrierHD && isNewCarrierTextLayout) {
            "com.android.keyguard.CarrierText".toClassOrNull()?.apply {
                method {
                    name = "updateHDDrawable"
                }.ignored().hook {
                    before {
                        this.args(0).set(0)
                    }
                }
            }
            "com.android.systemui.statusbar.ui.viewmodel.CarrierTextInjector".toClassOrNull()?.apply {
                method {
                    name = "updateHDDrawable"
                }.ignored().hook {
                    before {
                        this.args(0).set(0)
                    }
                }
            }
        }
        if (hideSimOne || hideSimTwo) {
            if (isNewCarrierTextLayout) {
                miuiCarrierTextLayoutClass?.apply {
                    method {
                        name = "onMeasure"
                    }.hook {
                        before {
                            val leftCarrierTextView = this.instance.current().field {
                                name = "leftCarrierTextView"
                            }.cast<TextView>()
                            if (hideSimOne) {
                                leftCarrierTextView?.visibility = View.GONE
                            }
                            val rightCarrierTextView = this.instance.current().field {
                                name = "rightCarrierTextView"
                            }.cast<TextView>()
                            if (hideSimTwo) {
                                rightCarrierTextView?.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                "com.android.systemui.statusbar.policy.MiuiCarrierTextControllerImpl".toClassOrNull()?.method {
                    name = "updateCarrierText"
                }?.hook {
                    before {
                        val mCardDisable = this.instance.current().field { name = "mCardDisable" }.any() as BooleanArray
                        val mPhoneCount = this.instance.current().field { name = "mPhoneCount" }.int()
                        if (hideSimOne && mPhoneCount >= 1) {
                            XposedHelpers.setAdditionalInstanceField(this.instance, "mOriCardDisable0", mCardDisable[0])
                            mCardDisable[0] = true
                        }
                        if (hideSimTwo && mPhoneCount >= 2) {
                            XposedHelpers.setAdditionalInstanceField(this.instance, "mOriCardDisable1", mCardDisable[1])
                            mCardDisable[1] = true
                        }
                    }
                    after {
                        val mCardDisable = this.instance.current().field { name = "mCardDisable" }.any() as BooleanArray
                        val mPhoneCount = this.instance.current().field { name = "mPhoneCount" }.int()
                        if (hideSimOne && mPhoneCount >= 1) {
                            (XposedHelpers.getAdditionalInstanceField(this.instance, "mOriCardDisable0") as? Boolean)?.let {
                                mCardDisable[0] = it
                            }
                        }
                        if (hideSimTwo && mPhoneCount >= 2) {
                            (XposedHelpers.getAdditionalInstanceField(this.instance, "mOriCardDisable1") as? Boolean)?.let {
                                mCardDisable[1] = it
                            }
                        }
                    }
                }
            }
        }
    }
}