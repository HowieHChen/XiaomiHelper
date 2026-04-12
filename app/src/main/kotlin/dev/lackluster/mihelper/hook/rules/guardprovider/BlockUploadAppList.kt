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

package dev.lackluster.mihelper.hook.rules.guardprovider

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object BlockUploadAppList : StaticHooker() {
    private val detect by lazy {
        DexKit.findMethodWithCache("get_all_un_system_apps") {
            matcher {
                returnType = "java.lang.String"
                addUsingString("AntiDefraudAppManager", StringMatchType.Equals)
                addUsingString("https://flash.sec.miui.com/detect/app", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.GuardProvider.BLOCK_UPLOAD_APP.get().also {
            updateSelfState(it)
        }.ifTrue {
            detect
        }
    }

    override fun onHook() {
        detect?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        "com.miui.guardprovider.manager.SecurityService".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                parameterCount = 0
                returnType(Boolean::class)
            }?.hook {
                result(true)
            }
        }
    }
}