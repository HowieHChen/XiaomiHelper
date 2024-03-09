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

package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SwitchEnv : YukiBaseHooker() {
    private val envGetMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                returnType = "java.lang.String"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("0", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val envSetMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                returnType = "void"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("3", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.BROWSER_SWITCH_ENV) {
            envGetMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                replaceTo("1")
            }
            envSetMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                before {
                    this.args(0).set("1")
                }
            }
        }
    }
}