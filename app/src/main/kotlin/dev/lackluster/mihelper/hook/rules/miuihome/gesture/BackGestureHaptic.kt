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

package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object BackGestureHaptic : StaticHooker() {
    private const val TIME_OUT_BLOCKER_KEY = "BLOCKER_ID_FOR_HAPTIC_GESTURE_BACK"

    private val backGestureHaptic by Preferences.MiuiHome.BACK_GESTURE_HAPTIC.lazyGet()

    private val clzHapticFeedbackCompatV2 by "com.miui.home.common.hapticfeedback.HapticFeedbackCompatV2".lazyClassOrNull()
    private val clzTimeOutBlocker by "com.miui.home.common.utils.TimeOutBlocker".lazyClassOrNull()
    private val metGetHandler by lazy {
        "com.miui.home.common.multithread.BackgroundThread".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getHandler"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Any>()
    }
    private val metStartCountDown by lazy {
        clzTimeOutBlocker?.resolve()?.firstMethodOrNull {
            name = "startCountDown"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Unit>()
    }
    private val metIsBlocked by lazy {
        clzTimeOutBlocker?.resolve()?.firstMethodOrNull {
            name = "isBlocked"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Boolean>()
    }
    private val metPerformExtHapticFeedback by lazy {
        "miuix.util.HapticFeedbackCompat".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "performExtHapticFeedback"
            parameters(Int::class)
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        updateSelfState(backGestureHaptic != 0)
    }

    override fun onHook() {
        when (backGestureHaptic) {
            1 -> {
                if (clzHapticFeedbackCompatV2 == null) return
                clzHapticFeedbackCompatV2?.apply {
                    val mHapticHelper = resolve().firstFieldOrNull {
                        name = "mHapticHelper"
                    }?.toTyped<Any>()
                    resolve().firstMethodOrNull {
                        name = "performGestureReadyBack"
                    }?.hook {
                        val ori = proceed()
                        metGetHandler?.invoke(null)?.let { handler ->
                            metStartCountDown?.invoke(null, handler, 140L, TIME_OUT_BLOCKER_KEY)
                        }
                        result(ori)
                    }
                    resolve().firstMethodOrNull {
                        name = "performGestureBackHandUp"
                    }?.hook {
                        val isBlocked = metIsBlocked?.invoke(null, TIME_OUT_BLOCKER_KEY) ?: false
                        if (isBlocked) {
                            result(null)
                        } else {
                            result(proceed())
                        }
                    }
                    resolve().firstMethodOrNull {
                        name {
                            it.startsWith("lambda") && it.contains("performGestureReadyBack")
                        }
                    }?.hook {
                        mHapticHelper?.get(thisObject)?.let {
                            metPerformExtHapticFeedback?.invoke(it, 0)
                        }
                        result(null)
                    }
                    resolve().firstMethodOrNull {
                        name {
                            it.startsWith("lambda") && it.contains("performGestureBackHandUp")
                        }
                    }?.hook {
                        mHapticHelper?.get(thisObject)?.let {
                            metPerformExtHapticFeedback?.invoke(it, 1)
                        }
                        result(null)
                    }
                }
                "com.miui.home.recents.GestureStubView".toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "injectBackKeyEvent"
                        parameters(Boolean::class)
                    }?.hook {
                        val newArgs = args.toTypedArray()
                        newArgs[0] = true
                        result(proceed(newArgs))
                    }
                }
            }
            2 -> {
                setOf(
                    "com.miui.home.common.hapticfeedback.HapticFeedbackCompatLinear",
                    "com.miui.home.common.hapticfeedback.HapticFeedbackCompatNormal",
                    "com.miui.home.common.hapticfeedback.HapticFeedbackCompatV2",
                ).forEach { className ->
                    className.toClassOrNull()?.apply {
                        resolve().firstMethodOrNull {
                            name = "performGestureBackHandUp"
                        }?.hook {
                            result(null)
                        }
                        resolve().firstMethodOrNull {
                            name = "performGestureReadyBack"
                        }?.hook {
                            result(null)
                        }
                    }
                }
            }
        }
    }
}