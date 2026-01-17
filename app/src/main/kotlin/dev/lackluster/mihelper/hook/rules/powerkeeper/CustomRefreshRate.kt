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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    private val parseCustomModeMethod by lazy {
        DexKit.findMethodsWithCache("custom_refresh_rate") {
            matcher {
                addUsingString("custom_mode_switch", StringMatchType.Equals)
                addUsingString("fucSwitch", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.PowerKeeper.UNLOCK_CUSTOM_REFRESH) {
            if (appClassLoader == null) return@hasEnable
            var hooked = false
            "com.miui.powerkeeper.statemachine.DisplayFrameSetting".toClassOrNull()?.apply {
                val fldIsCustomFpsSwitch = resolve().firstFieldOrNull {
                    name = "mIsCustomFpsSwitch"
                }
                resolve().firstMethodOrNull {
                    name = "parseCustomModeSwitchFromDb"
                }?.hook {
                    hooked = true
                    after {
                        fldIsCustomFpsSwitch?.copy()?.of(this.instance)?.set("true")
                    }
                }
            }
            if (!hooked) {
                parseCustomModeMethod.map { it.getMethodInstance(appClassLoader!!) }.hookAll {
                    after {
                        this.instance.asResolver().firstFieldOrNull {
                            name = "mIsCustomFpsSwitch"
                        }?.set("true")
                    }
                }
            }
        }
    }
}