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
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableRiskAppNotification : YukiBaseHooker() {
    private val pkg by lazy {
        DexKit.findMethodsWithCache("block_risk_app_notification") {
            matcher {
                addUsingString("riskPkgList", StringMatchType.Equals)
                addUsingString("key_virus_pkg_list", StringMatchType.Equals)
                addUsingString("show_virus_notification", StringMatchType.Equals)
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
        }
    }
}