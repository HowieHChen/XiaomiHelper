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

import android.content.DialogInterface
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.android.DialogInterfaceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs

object ChainStart : YukiBaseHooker() {
    private val mode = Prefs.getInt(Pref.Key.SecurityCenter.LINK_START, 0)
    private val confirmStartActivity by lazy {
        DexKit.dexKitBridge.getClassData("com.miui.wakepath.ui.ConfirmStartActivity")
    }
    private val initDialogMethod by lazy {
        DexKit.findMethodWithCache("link_start_init") {
            matcher {
                addUsingString("restrictForChain")
                addUsingString("CallerPkgName")
                addUsingString("CalleePkgName")
            }
            searchClasses = confirmStartActivity?.let { listOf(it) }
        }
    }
    private val showDialogMethod by lazy {
        DexKit.findMethodWithCache("link_start_show") {
            matcher {
                paramCount = 1
                paramTypes("miuix.appcompat.app.AlertDialog")
            }
            searchClasses = confirmStartActivity?.let { listOf(it) }
        }
    }

    override fun onHook() {
        if (mode != 0 && appClassLoader != null) {
            if (mode == 1) {
                showDialogMethod?.getMethodInstance(appClassLoader!!)?.hook {
                    before {
                        val dialog = this.args(0).any() ?: return@before
                        this.instance.current().method {
                            name = "onClick"
                            param(DialogInterfaceClass, IntType)
                        }.call(dialog, DialogInterface.BUTTON_POSITIVE)
                        this.result = null
                    }
                }
            } else if (mode == 2) {
                initDialogMethod?.getMethodInstance(appClassLoader!!)?.hook {
                    after {
                        this.instance.current().field {
                            type = Boolean
                            modifiers { isProtected }
                            superClass(true)
                        }.setTrue()
                    }
                }
            }
        }
    }
}