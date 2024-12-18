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

package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableRiskCheck : YukiBaseHooker() {
    private val verifyEnable by lazy {
        DexKit.findMethodWithCache("secure_verify_enable") {
            matcher {
                addUsingString("secure_verify_enable", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val openSafeMode by lazy {
        DexKit.findMethodWithCache("open_safety_model") {
            matcher {
                addUsingString("installerOpenSafetyModel", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val closeSafeMode by lazy {
        DexKit.findMethodWithCache("close_safety_model") {
            matcher {
                addUsingString("installerCloseSafetyModel", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val singleAuth by lazy {
        DexKit.findMethodWithCache("single_auth") {
            matcher {
                addUsingString("installerSingleAuth", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.DISABLE_RISK_CHECK) {
            if (appClassLoader == null) return@hasEnable
            verifyEnable?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            openSafeMode?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            closeSafeMode?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            singleAuth?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
        }
    }
}