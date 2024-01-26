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
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DevMode : YukiBaseHooker() {
    private val debugMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                name = "getDebugMode"
                returnType = "boolean"
                addUsingString("pref_key_debug_mode", StringMatchType.StartsWith)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.BROWSER_DEBUG) {
            debugMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)
                ?.hook {
                    replaceToTrue()
                }
                ?.result {
                    onHookingFailure {
                        YLog.warn("Failed to hook ${PrefKey.BROWSER_DEBUG}\n${it}")
                    }
                }
        }
    }
}