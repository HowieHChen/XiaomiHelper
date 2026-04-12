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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlocker : StaticHooker() {
    private val clzMessageItem by lazy {
        DexKit.findClassesWithCache("message_item") {
            matcher {
                addUsingString("Unknown type of the message: ", StringMatchType.Equals)
            }
        }
    }
    private val metIsType11 by lazy {
        DexKit.findMethodWithCache("is_type_11") {
            matcher {
                returnType = "boolean"
                addUsingNumber(11)
            }
            searchClasses = clzMessageItem.mapNotNull { DexKit.withBridge { getClassData(it.className) } }
        }
    }

    override fun onInit() {
        Preferences.MMS.AD_BLOCKER.get().also {
            updateSelfState(it)
        }.ifTrue {
            clzMessageItem
            metIsType11
        }
    }

    override fun onHook() {
        metIsType11?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        "com.miui.smsextra.ui.BottomMenu".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "allowMenuMode"
            }?.hook {
                result(false)
            }
        }
        "com.miui.smsextra.ui.UnderstandButton".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "requestADInAdvance"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                returnType = Boolean::class
                name {
                    it == "needRequestAD" || it == "requestAD"
                }
            }?.hook {
                result(false)
            }
        }
    }
}