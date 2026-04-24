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

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object RemoveReport : StaticHooker() {
    private val reportMethod by lazy {
        DexKit.findMethodWithCache("remove_report") {
            matcher {
                addUsingString("android.intent.action.VIEW", StringMatchType.Equals)
                addUsingString("com.xiaomi.market", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.REMOVE_REPORT.get().also {
            updateSelfState(it)
        }.ifTrue {
            reportMethod
        }
    }

    override fun onHook() {
        reportMethod?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
    }
}