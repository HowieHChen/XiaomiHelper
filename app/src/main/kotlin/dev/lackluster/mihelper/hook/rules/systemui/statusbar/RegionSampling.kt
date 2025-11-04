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

object RegionSampling : YukiBaseHooker() {
    private val mode = Prefs.getInt(Pref.Key.SystemUI.StatusBar.REGION_SAMPLING, 0)

    override fun onHook() {
        if (mode == 0) return
        "com.miui.systemui.common.ui.data.repository.MiuiConfigurationRepositoryImpl".toClassOrNull()?.apply {
            val isNightMode = resolve().firstFieldOrNull {
                name = "isNightMode"
            }
            resolve().firstConstructor().hook {
                after {
                    if (mode == 1) {
                        isNightMode?.copy()?.of(this.instance)?.set(readonlyStateFlowTrue)
                    } else if (mode == 2) {
                        isNightMode?.copy()?.of(this.instance)?.set(readonlyStateFlowFalse)
                    }
                }
            }
        }
        "com.android.systemui.statusbar.phone.LightBarControllerImplInjector".toClassOrNull()?.apply {
            val useRegionSampling = resolve().firstFieldOrNull {
                name = "useRegionSampling"
            }
            resolve().firstConstructor().hook {
                after {
                    if (mode == 1) {
                        useRegionSampling?.copy()?.of(this.instance)?.set(true)
                    } else if (mode == 2) {
                        useRegionSampling?.copy()?.of(this.instance)?.set(false)
                    }
                }
            }
        }
    }
}