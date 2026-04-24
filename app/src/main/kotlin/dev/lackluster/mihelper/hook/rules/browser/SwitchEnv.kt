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

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object SwitchEnv : StaticHooker() {
    private val envGetMethod by lazy {
        DexKit.findMethodWithCache("env_get") {
            matcher {
                returnType = "java.lang.String"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("0", StringMatchType.Equals)
            }
        }
    }
    private val envSetMethod by lazy {
        DexKit.findMethodWithCache("env_set") {
            matcher {
                returnType = "void"
                addUsingString("environment_flag_file", StringMatchType.Equals)
                addUsingString("environment_flag", StringMatchType.Equals)
                addUsingString("3", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.Browser.SWITCH_ENV.get().also {
            updateSelfState(it)
        }.ifTrue {
            envGetMethod
            envSetMethod
        }
    }

    override fun onHook() {
        envGetMethod?.getMethodInstance(classLoader)?.hook {
            result("1")
        }
        envSetMethod?.getMethodInstance(classLoader)?.hook {
            val newArgs = args.toTypedArray()
            newArgs[0] = "1"
            result(proceed(newArgs))
        }
    }
}