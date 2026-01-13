/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.settings

import android.app.Activity
import android.provider.Settings
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object QuickPermission : YukiBaseHooker() {
    private val perOverlay = Prefs.getBoolean(Pref.Key.Settings.QUICK_PER_OVERLAY, false)
    private val perInstallSource = Prefs.getBoolean(Pref.Key.Settings.QUICK_PER_INSTALL_SOURCE, false)

    override fun onHook() {
        if (perOverlay || perInstallSource) {
            "com.android.settings.SettingsActivity".toClass().apply {
                val fldInitialFragmentName = resolve().firstFieldOrNull {
                    name = "initialFragmentName"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "redirectTabletActivity"
                    parameterCount = 1
                }?.hook {
                     before {
                         val intent = this.instance<Activity>().intent
                         if (intent?.data == null || intent.data?.scheme != "package") return@before
                         if (perOverlay && intent.action == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                             fldInitialFragmentName?.copy()?.of(this.instance)?.set(
                                 "com.android.settings.applications.appinfo.DrawOverlayDetails"
                             )
                         }
                         if (perInstallSource && intent.action == Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) {
                             fldInitialFragmentName?.copy()?.of(this.instance)?.set(
                                 "com.android.settings.applications.appinfo.ExternalSourcesDetails"
                             )
                         }
                     }
                }
            }
        }
    }
}