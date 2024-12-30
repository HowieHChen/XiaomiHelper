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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.ControlCenter
import dev.lackluster.mihelper.utils.Prefs

object HideCarrierLabel : YukiBaseHooker() {
    private val hideSimOne by lazy {
        Prefs.getBoolean(ControlCenter.HIDE_CARRIER_ONE, false)
    }
    private val hideSimTwo by lazy {
        Prefs.getBoolean(ControlCenter.HIDE_CARRIER_TWO, false)
    }

    override fun onHook() {
        if (hideSimOne || hideSimTwo) {
            "com.android.systemui.statusbar.policy.MiuiCarrierTextControllerImpl".toClass().method {
                name = "updateCarrierText"
            }.hook {
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