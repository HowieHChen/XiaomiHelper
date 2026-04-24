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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device

object RemoveReport : StaticHooker(){
    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.REMOVE_REPORT.get() && !Device.isPad)
    }

    override fun onHook() {
        "com.miui.home.launcher.uninstall.BaseUninstallDialog".toClassOrNull()?.apply {
            val fldDialogView = resolve().firstFieldOrNull {
                name = "mDialogView"
            }?.toTyped<Any>()
            val fldReport = "com.miui.home.launcher.uninstall.UninstallDialogViewContainer".toClassOrNull()
                ?.resolve()?.firstFieldOrNull {
                    name = "mReport"
                }?.toTyped<TextView>()
            resolve().firstMethodOrNull {
                name = "init"
            }?.hook {
                val ori = proceed()
                fldDialogView?.get(thisObject)?.let { dialogView ->
                    fldReport?.get(dialogView)?.visibility = View.GONE
                }
                result(ori)
            }
        }
    }
}