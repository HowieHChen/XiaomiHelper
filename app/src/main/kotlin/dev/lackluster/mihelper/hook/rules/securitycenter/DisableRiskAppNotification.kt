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

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.util.ArrayList

object DisableRiskAppNotification : YukiBaseHooker() {
    private val riskClz by lazy {
        DexKit.findClassWithCache("risk_app_notification") {
            matcher {
                addUsingString("riskPkgList", StringMatchType.Equals)
                addUsingString("key_virus_pkg_list", StringMatchType.Equals)
                addUsingString("show_virus_notification", StringMatchType.Equals)
            }
        }
    }
    private val pkg by lazy {
        DexKit.findMethodsWithCache("block_risk_app_notification") {
            matcher {
                addUsingString("riskPkgList", StringMatchType.Equals)
                addUsingString("key_virus_pkg_list", StringMatchType.Equals)
                addUsingString("show_virus_notification", StringMatchType.Equals)
            }
        }
    }
    private val actionAntiFraudAutoScanApps by lazy {
        DexKit.findMethodWithCache("action_anti_fraud_auto_scan_apps") {
            matcher {
                addUsingString("action_anti_fraud_auto_scan_apps", StringMatchType.Equals)
                paramTypes("android.content.Context")
            }
        }
    }
    private val actionAntiFraudAutoScanSingleApp by lazy {
        DexKit.findMethodWithCache("action_anti_fraud_auto_scan_single_app") {
            matcher {
                addUsingString("action_anti_fraud_auto_scan_single_app", StringMatchType.Equals)
                paramTypes("android.content.Context", "java.lang.String")
            }
        }
    }
    private val getAntiFraudPackages by lazy {
        DexKit.findMethodWithCache("get_anti_fraud_packages") {
            matcher {
                addUsingString("anti_fraud_packages", StringMatchType.EndsWith)
                paramCount = 0
            }
        }
    }
    private val setAntiFraudPackages by lazy {
        DexKit.findMethodWithCache("set_anti_fraud_packages") {
            matcher {
                addUsingString("anti_fraud_packages", StringMatchType.EndsWith)
                paramCount = 1
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.DISABLE_RISK_APP_NOTIF) {
            if (appClassLoader == null) return@hasEnable
            val pkgInstance = pkg.map { it.getMethodInstance(appClassLoader!!) }.toList()
            pkgInstance.hookAll {
                intercept()
            }
            riskClz?.getInstance(appClassLoader!!)?.apply {
                resolve().method {
                    returnType(Void.TYPE)
                    parameterCount = 2
                    parameters(Context::class, ArrayList::class)
                }.forEach {
                    if (!pkgInstance.contains(it.self)) {
                        it.hook {
                            intercept()
                        }
                    }
                }
            }
            getAntiFraudPackages?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo(ArrayList<String>())
            }
            setAntiFraudPackages?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            actionAntiFraudAutoScanApps?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            actionAntiFraudAutoScanSingleApp?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
        }
    }
}