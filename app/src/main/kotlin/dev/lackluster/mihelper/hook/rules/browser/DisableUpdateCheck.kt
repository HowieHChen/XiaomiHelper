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
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableUpdateCheck : YukiBaseHooker() {
    private val miMarketUpdateClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("MarketUpdateAgent", StringMatchType.Equals)
                addUsingString("packageName", StringMatchType.Equals)
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

    override fun onHook() {
        hasEnable(Pref.Key.Browser.BLOCK_UPDATE) {
            if (appClassLoader == null) return@hasEnable
            miMarketDoInBackground?.getMethodInstance(appClassLoader!!)?.let {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        param?.result = 1 as Any?
                    }
                })
            }
            miMarketOnPostExecute?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo(null)
            }
        }
    }
}