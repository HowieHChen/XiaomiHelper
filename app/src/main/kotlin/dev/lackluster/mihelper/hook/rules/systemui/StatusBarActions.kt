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
import android.widget.Toast
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.Constants.ACTION_FLOATING_WINDOW
import dev.lackluster.mihelper.data.Constants.ACTION_HOME
import dev.lackluster.mihelper.data.Constants.ACTION_NOTIFICATIONS
import dev.lackluster.mihelper.data.Constants.ACTION_QUICK_SETTINGS
import dev.lackluster.mihelper.data.Constants.ACTION_RECENTS
import dev.lackluster.mihelper.data.Constants.ACTION_SCROLL_TO_TOP
import dev.lackluster.mihelper.data.Constants.PER_MIUI_INTERNAL_API
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.HostExecutor

object StatusBarActions : StaticHooker() {
    private val metStartSmallFreeformForControlCenter by lazy {
        "android.util.MiuiMultiWindowUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "startSmallFreeformForControlCenter"
            modifiers(Modifiers.STATIC)
            parameters(Context::class)
        }?.toTyped<String>()
    }
    private val clzMiuiInputManager by "miui.hardware.input.MiuiInputManager".lazyClassOrNull()
    private val miuiInputManagerInstance by lazy {
        clzMiuiInputManager?.resolve()?.firstMethodOrNull {
            name = "getInstance"
            modifiers(Modifiers.STATIC)
            parameterCount = 0
        }?.invoke()
    }
    private val metScrollToTop by lazy {
        clzMiuiInputManager?.resolve()?.optional(true)?.firstMethodOrNull {
            name = "scrollToTop"
            parameterCount = 0
        }?.toTyped<Unit>()
    }

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
    private val threadReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p0 == null || p1?.action == null) return
                when (p1.action) {
                    ACTION_FLOATING_WINDOW -> {
                        val context = p0.applicationContext
                        HostExecutor.execute(
                            tag = "ACTION_FLOATING_WINDOW",
                            backgroundTask = {
                                metStartSmallFreeformForControlCenter?.invoke(null, context)
                            },
                            runOnMain = true,
                            onResult = { resultString ->
                                if (resultString.isNotBlank()) {
                                    Toast.makeText(context, resultString, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    ACTION_SCROLL_TO_TOP -> {
                        HostExecutor.execute(
                            tag = "ACTION_SCROLL_TO_TOP",
                            backgroundTask = {
                                miuiInputManagerInstance?.let {
                                    metScrollToTop?.invoke(it)
                                }
                                d { "ACTION_SCROLL_TO_TOP miuiInputManagerInstance? ${miuiInputManagerInstance != null} metScrollToTop? ${metScrollToTop != null}" }
                                null
                            }
                        )
                    }
                }
            }
        }
    }


    override fun onInit() {
        updateSelfState(
            Preferences.MiuiHome.LINE_GESTURE_LONG_PRESS.get() != 0 ||
                    Preferences.MiuiHome.LINE_GESTURE_DOUBLE_TAP.get() != 0 ||
                    Preferences.SystemUI.StatusBar.DOUBLE_TAP_GESTURE.get() != 0 ||
                    Preferences.SystemUI.StatusBar.SINGLE_TAP_GESTURE.get() != 0
        )
        metStartSmallFreeformForControlCenter
        metScrollToTop
    }

    override fun onHook() {
        "com.android.systemui.accessibility.SystemActions".toClass().apply {
            val fldContext = resolve().firstFieldOrNull {
                name = "mContext"
            }?.toTyped<Context>()
            resolve().firstMethodOrNull {
                name = "start"
            }?.hook {
                val ori = proceed()
                val context = fldContext?.get(thisObject) ?: return@hook result(ori)
                val intentFilter = IntentFilter()
                intentFilter.addAction(ACTION_NOTIFICATIONS)
                intentFilter.addAction(ACTION_QUICK_SETTINGS)
                intentFilter.addAction(ACTION_HOME)
                intentFilter.addAction(ACTION_RECENTS)
                context.registerReceiver(
                    actionReceiver,
                    intentFilter,
                    PER_MIUI_INTERNAL_API,
                    null,
                    Context.RECEIVER_EXPORTED
                )
                val threadIntentFilter = IntentFilter()
                threadIntentFilter.addAction(ACTION_FLOATING_WINDOW)
                threadIntentFilter.addAction(ACTION_SCROLL_TO_TOP)
                context.registerReceiver(
                    threadReceiver,
                    threadIntentFilter,
                    PER_MIUI_INTERNAL_API,
                    null,
                    Context.RECEIVER_EXPORTED
                )
                result(ori)
            }
        }
    }
}