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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlow0
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.utils.Prefs

object WifiIcon : YukiBaseHooker() {
    private var hideActivity = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_WIFI_ACTIVITY, false)
    private val hideStandard = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_WIFI_STANDARD, false)
    private val activityRight = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.WIFI_ACTIVITY_RIGHT, false)

    override fun onHook() {
        if (!hideActivity && !hideStandard) return
        "com.android.systemui.statusbar.pipeline.wifi.ui.viewmodel.WifiViewModel".toClassOrNull()?.apply {
            val activityInOutRes = resolve().firstFieldOrNull {
                name = "activityInOutRes"
            }
            val wifiStandard = resolve().firstFieldOrNull {
                name = "wifiStandard"
            }
            val inoutLeft = resolve().firstFieldOrNull {
                name = "inoutLeft"
            }
            resolve().firstConstructor().hook {
                after {
                    if (hideActivity) {
                        activityInOutRes?.copy()?.of(this.instance)?.set(
                            readonlyStateFlow0
                        )
                    }
                    if (hideStandard) {
                        wifiStandard?.copy()?.of(this.instance)?.set(
                            readonlyStateFlow0
                        )
                    }
                    if (activityRight && hideStandard && !hideActivity) {
                        inoutLeft?.copy()?.of(this.instance)?.set(
                            readonlyStateFlowFalse
                        )
                    }
                }
            }
        }
    }
}