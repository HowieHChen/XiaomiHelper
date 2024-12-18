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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object ShowScreenBatteryUsage : YukiBaseHooker() {
    private val showSystem = Prefs.getBoolean(Pref.Key.SecurityCenter.SHOW_SYSTEM_BATTERY, false)
    private val powerRankClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("not support screenPowerSplit", StringMatchType.Equals)
                addUsingString("PowerRankHelperHolder", StringMatchType.Equals)
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
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY) {
            if (appClassLoader == null) return@hasEnable
            val powerRankMethod1Instance = powerRankMethod1?.getMethodInstance(appClassLoader!!) ?: return@hasEnable
            powerRankMethod1Instance.hook {
                replaceTo(!showSystem)
            }
            val powerRankMethod2Instance = powerRankMethod2.map { it.getMethodInstance(appClassLoader!!) }.toList()
            powerRankMethod2Instance.forEach {
                if (it != powerRankMethod1Instance) {
                    it.hook {
                        replaceToFalse()
                    }
                }
            }
        }
    }
}