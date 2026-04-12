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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object QuickPermission : StaticHooker() {
    private val overlay by Preferences.Settings.QUICK_PER_OVERLAY.lazyGet()
    private val installSource by Preferences.Settings.QUICK_PER_INSTALL_SOURCE.lazyGet()

    override fun onInit() {
        updateSelfState(overlay || installSource)
    }

    override fun onHook() {
        if (overlay || installSource) {
            "com.android.settings.SettingsActivity".toClass().apply {
                val fldInitialFragmentName = resolve().firstFieldOrNull {
                    name = "initialFragmentName"
                    superclass()
                }?.toTyped<String>()
                resolve().firstMethodOrNull {
                    name = "redirectTabletActivity"
                    parameterCount = 1
                }?.hook {
                    val intent = (thisObject as? Activity)?.intent
                    if (intent?.data?.scheme == "package") {
                        if (overlay && intent.action == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                            fldInitialFragmentName?.set(thisObject,
                                "com.android.settings.applications.appinfo.DrawOverlayDetails"
                            )
                        }
                        if (installSource && intent.action == Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) {
                            fldInitialFragmentName?.set(thisObject,
                                "com.android.settings.applications.appinfo.ExternalSourcesDetails"
                            )
                        }
                    }
                    result(proceed())
                }
            }
        }
    }
}