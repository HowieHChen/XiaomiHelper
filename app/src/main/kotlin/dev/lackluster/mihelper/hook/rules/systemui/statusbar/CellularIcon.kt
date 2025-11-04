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
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowTrue
import dev.lackluster.mihelper.utils.Prefs

object CellularIcon : YukiBaseHooker() {
    private val ignoreSysSettings = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.IGNORE_SYS_SETTINGS, false)

    private val hideActivity = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ACTIVITY, false)
    private val hideType = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_TYPE, false)
    private val hideVoWifi = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VO_WIFI, false)
    private val hideVolte = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VOLTE, false)
    private val hideVolteNoService = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VOLTE_NO_SERVICE, false)
    private val hideSpeechHD = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_SPEECH_HD, false)

    private val hideRoamGlobal = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM_GLOBAL, false)
    private val hideRoam = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM, false)
    private val hideSmallRoam = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_SMALL_ROAM, false)

    override fun onHook() {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MiuiCellularIconVM".toClassOrNull()?.apply {
            if (
                hideActivity || hideType || hideVoWifi || hideVolte || hideVolteNoService || hideSpeechHD
            ) {
                val inOutVisible = resolve().firstFieldOrNull {
                    name = "inOutVisible"
                }
                val mobileTypeVisible = resolve().firstFieldOrNull {
                    name = "mobileTypeVisible"
                }
                val mobileTypeImageVisible = resolve().firstFieldOrNull {
                    name = "mobileTypeImageVisible"
                }
                val vowifiVisible = resolve().firstFieldOrNull {
                    name = "vowifiVisible"
                }
                val speechHd = resolve().firstFieldOrNull {
                    name = "speechHd"
                }
                val volteNoService = resolve().firstFieldOrNull {
                    name = "volteNoService"
                }
                val volteVisibleGlobal = resolve().firstFieldOrNull {
                    name = "volteVisibleGlobal"
                }
                resolve().firstConstructor().hook {
                    after {
                        val instance = this.instance
                        if (hideActivity) {
                            inOutVisible?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                        if (hideType) {
                            mobileTypeVisible?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                            mobileTypeImageVisible?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                        if (hideVoWifi) {
                            vowifiVisible?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                        if (hideVolte) {
                            volteVisibleGlobal?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                        if (hideVolteNoService) {
                            volteNoService?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                        if (hideSpeechHD) {
                            speechHd?.copy()?.of(instance)?.set(readonlyStateFlowFalse)
                        }
                    }
                }
            }
            if (!hideRoamGlobal && hideRoam) {
                resolve().firstMethodOrNull {
                    name = "getMobileRoamVisible"
                }?.hook {
                    replaceTo(readonlyStateFlowFalse)
                }
            }
            if (!hideRoamGlobal && hideSmallRoam) {
                resolve().firstMethodOrNull {
                    name = "getSmallRoamVisible"
                }?.hook {
                    replaceTo(readonlyStateFlowFalse)
                }
            }
        }
        if (hideRoamGlobal || ignoreSysSettings) {
            "com.android.systemui.statusbar.policy.StatusBarIconObserver".toClassOrNull()?.apply {
                val roamSettingBlock = resolve().firstFieldOrNull {
                    name = "roamSettingBlock"
                }
                resolve().firstConstructor().hook {
                    after {
                        roamSettingBlock?.copy()?.of(this.instance)?.set(
                            if (hideRoamGlobal) {
                                readonlyStateFlowTrue
                            } else {
                                readonlyStateFlowFalse
                            }
                        )
                    }
                }
            }
        }
    }
}