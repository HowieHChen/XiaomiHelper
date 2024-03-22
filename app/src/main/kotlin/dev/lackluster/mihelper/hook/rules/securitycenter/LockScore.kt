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
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object LockScore : YukiBaseHooker() {
    private val scoreMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("getMinusPredictScore", StringMatchType.Contains)
            }
        }.singleOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.LOCK_SCORE) {
            if (appClassLoader == null) return@hasEnable
            "com.miui.securityscan.ui.main.MainContentFrame".toClass().method {
                name = "onClick"
                param(ViewClass)
            }.hook {
                replaceTo(null)
            }
            scoreMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo(0)
            }
        }
    }
}