/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.guardprovider

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object BlockEnvCheck : YukiBaseHooker() {
    private val metCheckRoot by lazy {
        DexKit.findMethodWithCache("root_check") {
            matcher {
                returnType = "boolean"
                addUsingString("/system/bin/su", StringMatchType.Equals)
                addUsingString("/system/xbin/su", StringMatchType.Equals)
            }
        }
    }
    private val checkRootClass by lazy {
        DexKit.findClassesWithCache("su_check") {
            matcher {
                addUsingString("/data/local/su", StringMatchType.Equals)
                addUsingString("/system/bin/su", StringMatchType.Equals)
                addUsingString("/system/xbin/su", StringMatchType.Equals)
            }
        }
    }
    private val metCheckSuFile by lazy {
        DexKit.findMethodsWithCache("su_check") {
            matcher {
                returnType = "boolean"
                paramCount = 0
                modifiers(Modifier.STATIC)
            }
            searchInClass(checkRootClass.mapNotNull { DexKit.dexKitBridge.getClassData(it.className) })
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.GuardProvider.BLOCK_ENV_CHECK) {
            if (appClassLoader == null) return@hasEnable
            metCheckRoot?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metCheckSuFile.map {
                it.getMethodInstance(appClassLoader!!)
            }.hookAll {
                replaceToFalse()
            }
        }
    }
}