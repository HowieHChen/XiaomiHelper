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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType
import java.util.ArrayList

object DisableRiskAppNotification : StaticHooker() {
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

    override fun onInit() {
        Preferences.SecurityCenter.DISABLE_RISK_APP_NOTIF.get().also { 
            updateSelfState(it)
        }.ifTrue {
            pkg
            riskClz
            getAntiFraudPackages
            setAntiFraudPackages
            actionAntiFraudAutoScanApps
            actionAntiFraudAutoScanSingleApp
        }
    }
    
    override fun onHook() {
        val pkgInstance = pkg.map { it.getMethodInstance(classLoader) }.toList()
        pkgInstance.hookAll {
            result(null)
        }
        riskClz?.getInstance(classLoader)?.apply {
            resolve().method {
                returnType(Void.TYPE)
                parameterCount = 2
                parameters(Context::class, ArrayList::class)
            }.forEach {
                if (!pkgInstance.contains(it.self)) {
                    it.hook {
                        result(null)
                    }
                }
            }
        }
        getAntiFraudPackages?.getMethodInstance(classLoader)?.hook {
            result(ArrayList<String>())
        }
        setAntiFraudPackages?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        actionAntiFraudAutoScanApps?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        actionAntiFraudAutoScanSingleApp?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}