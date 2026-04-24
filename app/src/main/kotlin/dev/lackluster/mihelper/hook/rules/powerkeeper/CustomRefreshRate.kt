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

package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : StaticHooker() {
    private val parseCustomModeMethod by lazy {
        DexKit.findMethodsWithCache("custom_refresh_rate") {
            matcher {
                addUsingString("custom_mode_switch", StringMatchType.Equals)
                addUsingString("fucSwitch", StringMatchType.Equals)
            }
        }
    }
    private val clzDisplayFrameSetting by "com.miui.powerkeeper.statemachine.DisplayFrameSetting".lazyClassOrNull()

    override fun onInit() {
        Preferences.PowerKeeper.UNLOCK_CUSTOM_REFRESH.get().also {
            updateSelfState(it)
        }.ifTrue {
            parseCustomModeMethod
        }
    }

    override fun onHook() {
        val metParseCustomModeSwitchFromDb = clzDisplayFrameSetting?.resolve()?.firstMethodOrNull {
            name = "parseCustomModeSwitchFromDb"
        }
        if (metParseCustomModeSwitchFromDb != null) {
            val fldIsCustomFpsSwitch = clzDisplayFrameSetting?.resolve()?.firstFieldOrNull {
                name = "mIsCustomFpsSwitch"
            }?.toTyped<String>()
            metParseCustomModeSwitchFromDb.hook {
                val ori = proceed()
                fldIsCustomFpsSwitch?.set(thisObject, "true")
                result(ori)
            }
        } else {
            parseCustomModeMethod.map {
                it.getMethodInstance(classLoader)
            }.hookAll {
                val ori = proceed()
                thisObject.asResolver().firstFieldOrNull {
                    name = "mIsCustomFpsSwitch"
                }?.set("true")
                result(ori)
            }
        }
    }
}