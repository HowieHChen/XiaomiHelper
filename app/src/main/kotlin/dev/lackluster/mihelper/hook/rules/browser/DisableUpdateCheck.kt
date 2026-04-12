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

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableUpdateCheck : StaticHooker() {
    private val miMarketUpdateClass by lazy {
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("MarketUpdateAgent", StringMatchType.Equals)
                    addUsingString("packageName", StringMatchType.Equals)
                }
            }
        }
    }
    private val miMarketDoInBackground by lazy {
        DexKit.findMethodWithCache("disable_update_1") {
            matcher {
                name = "doInBackground"
                returnType = "java.lang.Object"
            }
            searchClasses = miMarketUpdateClass
        }
    }
    private val miMarketOnPostExecute by lazy {
        DexKit.findMethodWithCache("disable_update_2") {
            matcher {
                name = "onPostExecute"
                paramTypes("java.lang.Object")
            }
            searchClasses = miMarketUpdateClass
        }
    }

    override fun onInit() {
        Preferences.Browser.BLOCK_UPDATE.get().also {
            updateSelfState(it)
        }.ifTrue {
            miMarketDoInBackground
            miMarketOnPostExecute
        }
    }

    override fun onHook() {
        miMarketDoInBackground?.getMethodInstance(classLoader)?.hook {
            result(1)
        }
        miMarketOnPostExecute?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}