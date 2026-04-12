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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object SystemAppWifiSettings : StaticHooker() {
    private var Any.realIsSystemApp by extraOf("KEY_REAL_IS_SYSTEM_APP", false)

    private val method by lazy {
        DexKit.findMethodWithCache("system_app_wifi") {
            matcher {
                addUsingString("com.qti.qcc", StringMatchType.Equals)
                returnType = "boolean"
                paramCount = 0
            }
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.CTRL_SYSTEM_APP_WIFI.get().also {
            updateSelfState(it)
        }.ifTrue {
            method
        }
    }

    override fun onHook() {
        "com.miui.networkassistant.ui.fragment.ShowAppDetailFragment".toClassOrNull()?.apply {
            val fldAppInfo = resolve().firstFieldOrNull {
                name = "mAppInfo"
            }?.toTyped<Any>()
            val fldIsSystemApp = "com.miui.networkassistant.model.AppInfo".toClassOrNull()
                ?.resolve()?.firstFieldOrNull {
                    name = "isSystemApp"
                }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "initFirewallData"
            }?.hook {
                val appInfo = fldAppInfo?.get(thisObject)
                val isSystemApp = appInfo?.let { fldIsSystemApp?.get(it) }
                if (isSystemApp != null) {
                    appInfo.realIsSystemApp = isSystemApp
                    fldIsSystemApp?.set(appInfo, false)
                }
                val ori = proceed()
                if (isSystemApp != null) {
                    val realIsSystem = appInfo.realIsSystemApp ?: false
                    fldIsSystemApp?.set(appInfo, realIsSystem)
                }
                result(ori)
            }
        }
        "com.miui.networkassistant.service.FirewallService".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setSystemAppWifiRuleAllow"
            }?.hook {
                result(null)
            }
        }
        method?.getMethodInstance(classLoader)?.hook {
            result(true)
        }
    }
}