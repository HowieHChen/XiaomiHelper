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

package dev.lackluster.mihelper.hook.rules.milink

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object ContinueTasks : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.MiMirror.CONTINUE_ALL_TASKS.get())
    }

    override fun onHook() {
        "com.xiaomi.mirror.synergy.MiuiSynergySdk".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isSupportSendApp"
                parameterCount = 3
            }?.hook {
                proceed()
                result(true)
            }
            resolve().firstMethodOrNull {
                name = "isSupportSendAppToPhone"
                parameterCount = 2
            }?.hook {
                proceed()
                result(true)
            }
        }
    }
}