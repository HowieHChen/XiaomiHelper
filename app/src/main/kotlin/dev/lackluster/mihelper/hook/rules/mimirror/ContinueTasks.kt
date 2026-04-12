/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.mimirror

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object ContinueTasks : StaticHooker() {
    private val subScreen by lazy {
        DexKit.findMethodWithCache("pref_all_app_sub_screen") {
            matcher {
                addUsingString("support_all_app_sub_screen", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }

    override fun onInit() {
        Preferences.MiMirror.CONTINUE_ALL_TASKS.get().also {
            updateSelfState(it)
        }.ifTrue {
            subScreen
        }
    }

    override fun onHook() {
        subScreen?.getMethodInstance(classLoader)?.hook {
            result(true)
        }
    }
}