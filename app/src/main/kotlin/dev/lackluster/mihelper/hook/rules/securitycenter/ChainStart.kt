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

import android.view.View
import android.widget.Button
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable


object ChainStart : YukiBaseHooker() {

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.CHAIN_START) {
            "com.miui.wakepath.ui.ConfirmStartActivity".toClassOrNull()?.apply {
                val metOnClick = resolve().firstMethodOrNull {
                    name = "onClick"
                    parameters(View::class)
                }
                val fldDialog = resolve().firstFieldOrNull {
                    type("miuix.appcompat.app.AlertDialog")
                    superclass()
                }
                val metGetButton = "miuix.appcompat.app.AlertDialog".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "getButton"
                }
                resolve().firstMethodOrNull {
                    name = "onDialogCreated"
                }?.hook {
                    before {
                        val mDialog = fldDialog?.copy()?.of(this.instance)?.get() ?: return@before
                        val buttonAlways = metGetButton?.copy()?.of(mDialog)?.invoke<Button>(2)
                        if (buttonAlways != null) {
                            buttonAlways.id = 2
                            metOnClick?.copy()?.of(this.instance)?.invoke(buttonAlways)
                        } else {
                            metGetButton?.copy()?.of(mDialog)?.invoke<Button>(3)?.let { buttonOnce ->
                                buttonOnce.id = 3
                                metOnClick?.copy()?.of(this.instance)?.invoke(buttonOnce)
                            }
                        }
                    }
                }
            }
        }
    }
}