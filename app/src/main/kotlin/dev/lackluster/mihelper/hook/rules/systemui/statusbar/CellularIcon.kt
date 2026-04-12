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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowTrue
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object CellularIcon : StaticHooker() {
    private val ignoreSysSettings by Preferences.SystemUI.StatusBar.IconTuner.IGNORE_SYS_SETTINGS.lazyGet()

    private val hideActivity by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ACTIVITY.lazyGet()
    private val hideType by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_TYPE.lazyGet()
    private val hideVoWifi by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VO_WIFI.lazyGet()
    private val hideVolte by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE.lazyGet()
    private val hideVolteNoService by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE_NO_SERVICE.lazyGet()
    private val hideSpeechHD by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SPEECH_HD.lazyGet()

    private val hideRoamGlobal by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ROAM_GLOBAL.lazyGet()
    private val hideLargeRoam by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_LARGE_ROAM.lazyGet()
    private val hideSmallRoam by Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SMALL_ROAM.lazyGet()

    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MiuiCellularIconVM".toClassOrNull()?.apply {
            if (
                hideActivity || hideType || hideVoWifi || hideVolte || hideVolteNoService || hideSpeechHD
            ) {
                val inOutVisible = resolve().firstFieldOrNull {
                    name = "inOutVisible"
                }?.toTyped<Any>()
                val mobileTypeVisible = resolve().firstFieldOrNull {
                    name = "mobileTypeVisible"
                }?.toTyped<Any>()
                val mobileTypeImageVisible = resolve().firstFieldOrNull {
                    name = "mobileTypeImageVisible"
                }?.toTyped<Any>()
                val vowifiVisible = resolve().firstFieldOrNull {
                    name = "vowifiVisible"
                }?.toTyped<Any>()
                val speechHd = resolve().firstFieldOrNull {
                    name = "speechHd"
                }?.toTyped<Any>()
                val volteNoService = resolve().firstFieldOrNull {
                    name = "volteNoService"
                }?.toTyped<Any>()
                val volteVisibleGlobal = resolve().firstFieldOrNull {
                    name = "volteVisibleGlobal"
                }?.toTyped<Any>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    if (hideActivity) {
                        inOutVisible?.set(thisObject, readonlyStateFlowFalse)
                    }
                    if (hideType) {
                        mobileTypeVisible?.set(thisObject, readonlyStateFlowFalse)
                        mobileTypeImageVisible?.set(thisObject, readonlyStateFlowFalse)
                    }
                    if (hideVoWifi) {
                        vowifiVisible?.set(thisObject, readonlyStateFlowFalse)
                    }
                    if (hideVolte) {
                        volteVisibleGlobal?.set(thisObject, readonlyStateFlowFalse)
                    }
                    if (hideVolteNoService) {
                        volteNoService?.set(thisObject, readonlyStateFlowFalse)
                    }
                    if (hideSpeechHD) {
                        speechHd?.set(thisObject, readonlyStateFlowFalse)
                    }
                    result(ori)
                }
            }
            if (!hideRoamGlobal && hideLargeRoam) {
                resolve().firstMethodOrNull {
                    name = "getMobileRoamVisible"
                }?.hook {
                    result(readonlyStateFlowFalse)
                }
            }
            if (!hideRoamGlobal && hideSmallRoam) {
                resolve().firstMethodOrNull {
                    name = "getSmallRoamVisible"
                }?.hook {
                    result(readonlyStateFlowFalse)
                }
            }
        }
        if (hideRoamGlobal || ignoreSysSettings) {
            "com.android.systemui.statusbar.policy.StatusBarIconObserver".toClassOrNull()?.apply {
                val roamSettingBlock = resolve().firstFieldOrNull {
                    name = "roamSettingBlock"
                }?.toTyped<Any>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    roamSettingBlock?.set(
                        thisObject,
                        if (hideRoamGlobal) readonlyStateFlowTrue else readonlyStateFlowFalse
                    )
                    result(ori)
                }
            }
        }
    }
}