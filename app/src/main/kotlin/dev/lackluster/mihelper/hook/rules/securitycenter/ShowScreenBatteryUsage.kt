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

package dev.lackluster.mihelper.hook.rules.securitycenter

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object ShowScreenBatteryUsage : StaticHooker() {
    private val showSystem by Preferences.SecurityCenter.BATTERY_SHOW_SYSTEM.lazyGet()
    
    private val powerRankClass by lazy {
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("not support screenPowerSplit", StringMatchType.Equals)
                    addUsingString("PowerRankHelperHolder", StringMatchType.Equals)
                }
            }
        }
    }
    private val powerRankMethod1 by lazy {
        DexKit.findMethodWithCache("precise_battery") {
            matcher {
                returnType = "boolean"
                paramCount = 0
                addUsingString("ishtar", StringMatchType.Equals)
                addUsingString("nuwa", StringMatchType.Equals)
                addUsingString("fuxi", StringMatchType.Equals)
            }
            searchClasses = powerRankClass
        }
    }
    private val powerRankMethod2 by lazy {
        DexKit.findMethodsWithCache("sdk_ge_33") {
            matcher {
                returnType = "boolean"
                paramCount = 0
            }
            searchClasses = powerRankClass
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.BATTERY_SHOW_SCREEN.get().also { 
            updateSelfState(it)
        }.ifTrue {
            powerRankMethod1
            powerRankMethod2
        }
    }
    
    override fun onHook() {
        val powerRankMethod1Instance = powerRankMethod1?.getMethodInstance(classLoader)
        powerRankMethod1Instance?.hook {
            result(!showSystem)
        }
        powerRankMethod2.map { it.getMethodInstance(classLoader) }.forEach {
            if (it != powerRankMethod1Instance) {
                it.hook {
                    result(false)
                }
            }
        }
    }
}