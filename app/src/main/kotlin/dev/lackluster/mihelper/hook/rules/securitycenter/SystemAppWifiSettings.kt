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
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SystemAppWifiSettings : YukiBaseHooker() {
    private val method by lazy {
        DexKit.findMethodWithCache("system_app_wifi") {
            matcher {
                addUsingString("com.qti.qcc", StringMatchType.Equals)
                returnType = "boolean"
                paramCount = 0
            }
        }
    }
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.CTRL_SYSTEM_APP_WIFI) {
            if (appClassLoader == null) return@hasEnable
            "com.miui.networkassistant.ui.fragment.ShowAppDetailFragment".toClassOrNull()?.apply {
                method {
                    name = "initFirewallData"
                }.hook {
                    before {
                        val appInfo = this.instance.current().field {
                            name = "mAppInfo"
                        }.any() ?: return@before
                        val appInfoField = appInfo.current().field {
                            name = "isSystemApp"
                        }
                        XposedHelpers.setAdditionalInstanceField(
                            appInfo,
                            "realIsSystemApp",
                            appInfoField.boolean()
                        )
                        appInfoField.setFalse()
                    }
                    after {
                        val appInfo = this.instance.current().field {
                            name = "mAppInfo"
                        }.any() ?: return@after
                        val realIsSystemApp = XposedHelpers.getAdditionalInstanceField(
                            appInfo,
                            "realIsSystemApp"
                        ) as? Boolean ?: false
                        appInfo.current().field {
                            name = "isSystemApp"
                        }.set(realIsSystemApp)
                    }
                }
            }
            "com.miui.networkassistant.service.FirewallService".toClassOrNull()?.apply {
                method {
                    name = "setSystemAppWifiRuleAllow"
                }.hook {
                    intercept()
                }
            }
            method?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToTrue()
            }
        }
    }
}