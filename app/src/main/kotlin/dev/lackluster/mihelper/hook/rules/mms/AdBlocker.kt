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

package dev.lackluster.mihelper.hook.rules.mms

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlocker : YukiBaseHooker() {
    private val cloudControlMethod by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("Unknown type of the message: ", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    override fun onHook() {
        hasEnable(Pref.Key.MMS.AD_BLOCKER) {
            if (appClassLoader == null) return@hasEnable
            runCatching {
                cloudControlMethod?.getInstance(appClassLoader!!)?.method {
                    name = "j"
                }?.ignored()?.hook {
                    replaceToFalse()
                }
            }
            "com.miui.smsextra.ui.BottomMenu".toClass().method {
                name = "allowMenuMode"
            }.hook {
                replaceToFalse()
            }
        }
    }
}