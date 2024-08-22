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
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object ShowScreenBatteryUsage : YukiBaseHooker() {
    private val powerRankClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("not support screenPowerSplit", StringMatchType.Equals)
                addUsingString("PowerRankHelperHolder", StringMatchType.Equals)
            }
        }.single()
    }
    private val powerRankMethod1 by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("ishtar", StringMatchType.Equals)
                addUsingString("nuwa", StringMatchType.Equals)
                addUsingString("fuxi", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val powerRankMethod2 by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass = powerRankClass.name
                returnType = "boolean"
                paramCount = 0
            }
        }
    }
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY) {
            if (appClassLoader == null) return@hasEnable
            val powerRankMethod1Instance = powerRankMethod1?.getMethodInstance(appClassLoader!!) ?: return@hasEnable
            val powerRankMethod2Instance = powerRankMethod2.map { it.getMethodInstance(appClassLoader!!) }.toList()
            powerRankMethod2Instance.forEach {
                it.hook {
                    before {
                        when(this.method) {
                            powerRankMethod1Instance -> this.result = true
                            else -> this.result = false
                        }
                    }
                }
            }
        }
    }
}