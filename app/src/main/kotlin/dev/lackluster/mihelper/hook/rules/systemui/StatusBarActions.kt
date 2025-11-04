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

package dev.lackluster.mihelper.hook.rules.systemui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants.ACTION_HOME
import dev.lackluster.mihelper.data.Constants.ACTION_NOTIFICATIONS
import dev.lackluster.mihelper.data.Constants.ACTION_QUICK_SETTINGS
import dev.lackluster.mihelper.data.Constants.ACTION_RECENTS
import dev.lackluster.mihelper.data.Constants.PER_MIUI_INTERNAL_API
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Prefs

object StatusBarActions : YukiBaseHooker() {
    private val actionReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p0 == null || p1?.action == null) return
                val intent = when(p1.action) {
                    ACTION_NOTIFICATIONS -> Intent("action_panels_operation").putExtra("operation", "reverse_notifications_panel")
                    ACTION_QUICK_SETTINGS -> Intent("action_panels_operation").putExtra("operation", "reverse_quick_settings_panel")
                    ACTION_HOME -> Intent("SYSTEM_ACTION_HOME")
                    ACTION_RECENTS -> Intent("SYSTEM_ACTION_RECENTS")
                    else -> return
                }.apply {
                    setPackage(Scope.SYSTEM_UI)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                p0.sendBroadcast(intent)
            }
        }
    }

    override fun onHook() {
        if (
            Prefs.getBoolean(Pref.Key.MiuiHome.QUICK_SWITCH, false) ||
            Prefs.getInt(Pref.Key.MiuiHome.LINE_GESTURE_LONG_PRESS, 0) != 0 ||
            Prefs.getInt(Pref.Key.MiuiHome.LINE_GESTURE_DOUBLE_TAP, 0) != 0
        ) {
            "com.android.systemui.accessibility.SystemActions".toClass()
                .resolve().firstMethodOrNull {
                    name = "start"
                }?.hook {
                    after {
                        val mContext = this.instance.asResolver().firstFieldOrNull {
                            name = "mContext"
                        }?.get<Context>() ?: return@after
                        val intentFilter = IntentFilter()
                        intentFilter.addAction(ACTION_NOTIFICATIONS)
                        intentFilter.addAction(ACTION_QUICK_SETTINGS)
                        intentFilter.addAction(ACTION_HOME)
                        intentFilter.addAction(ACTION_RECENTS)
                        mContext.registerReceiver(
                            actionReceiver,
                            intentFilter,
                            PER_MIUI_INTERNAL_API,
                            null,
                            Context.RECEIVER_EXPORTED
                        )
                    }
                }
        }
    }
}