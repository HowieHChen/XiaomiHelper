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

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object ChainStart : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.SecurityCenter.LINK_START.get() == 1)
    }

    @SuppressLint("ResourceType")
    override fun onHook() {
        "com.miui.wakepath.ui.ConfirmStartActivity".toClassOrNull()?.apply {
            val metOnClick = resolve().firstMethodOrNull {
                name = "onClick"
                parameters(View::class)
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "onDialogCreated"
            }?.hook {
                val activity = thisObject as? Activity
                if (metOnClick != null && activity != null) {
                    metOnClick.invoke(
                        thisObject,
                        View(activity).apply {
                            id = 2
                        }
                    )
                    result(null)
                } else {
                    result(proceed())
                }
            }
        }
    }
}