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
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped

object SystemAppWifiSettings : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true
    private var Any.realIsSystemApp by extraOf("KEY_REAL_IS_SYSTEM_APP", false)

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.CTRL_SYSTEM_APP_WIFI.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
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
    }
}