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

package dev.lackluster.mihelper.hook.rules.miuihome

import android.view.View
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object RemoveReport : YukiBaseHooker(){
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.REMOVE_REPORT, extraCondition = { !Device.isPad }) {
            "com.miui.home.launcher.uninstall.BaseUninstallDialog".toClassOrNull()?.apply {
                val fldDialogView = resolve().firstFieldOrNull {
                    name = "mDialogView"
                }
                val fldReport = "com.miui.home.launcher.uninstall.UninstallDialogViewContainer".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mReport"
                    }
                resolve().firstMethodOrNull {
                    name = "init"
                }?.hook {
                    after {
                        fldDialogView?.copy()?.of(this.instance)?.get()?.let { dialogView ->
                            fldReport?.copy()?.of(dialogView)?.get<TextView>()?.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}