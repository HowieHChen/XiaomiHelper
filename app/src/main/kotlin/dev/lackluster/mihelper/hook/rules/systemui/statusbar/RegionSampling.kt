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

object RegionSampling : StaticHooker() {
    private val mode by Preferences.SystemUI.StatusBar.REGION_SAMPLING.lazyGet()

    override fun onInit() {
        updateSelfState(mode != 0)
    }

    override fun onHook() {
        "com.miui.systemui.common.ui.data.repository.MiuiConfigurationRepositoryImpl".toClassOrNull()?.apply {
            val isNightMode = resolve().firstFieldOrNull {
                name = "isNightMode"
            }?.toTyped<Any>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                if (mode == 1) {
                    isNightMode?.set(thisObject, readonlyStateFlowTrue)
                } else if (mode == 2) {
                    isNightMode?.set(thisObject, readonlyStateFlowFalse)
                }
                result(ori)
            }
        }
        "com.android.systemui.statusbar.phone.LightBarControllerImplInjector".toClassOrNull()?.apply {
            val useRegionSampling = resolve().firstFieldOrNull {
                name = "useRegionSampling"
            }?.toTyped<Boolean>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                if (mode == 1) {
                    useRegionSampling?.set(thisObject, true)
                } else if (mode == 2) {
                    useRegionSampling?.set(thisObject, false)
                }
                result(ori)
            }
        }
    }
}