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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object BackGestureHaptic : YukiBaseHooker() {
    private const val TIME_OUT_BLOCKER_KEY = "BLOCKER_ID_FOR_HAPTIC_GESTURE_BACK"
    private val clzHapticFeedbackCompatV2 by lazy {
        "com.miui.home.common.hapticfeedback.HapticFeedbackCompatV2".toClassOrNull()
    }
    private val clzTimeOutBlocker by lazy {
        "com.miui.home.common.utils.TimeOutBlocker".toClassOrNull()
    }
    private val metGetHandler by lazy {
        "com.miui.home.common.multithread.BackgroundThread".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getHandler"
            modifiers(Modifiers.STATIC)
        }
    }
    private val metStartCountDown by lazy {
        clzTimeOutBlocker?.resolve()?.firstMethodOrNull {
            name = "startCountDown"
            modifiers(Modifiers.STATIC)
        }
    }
    private val metIsBlocked by lazy {
        clzTimeOutBlocker?.resolve()?.firstMethodOrNull {
            name = "isBlocked"
            modifiers(Modifiers.STATIC)
        }
    }
    private val metPerformExtHapticFeedback by lazy {
        "miuix.util.HapticFeedbackCompat".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "performExtHapticFeedback"
            parameters(Int::class)
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.BACK_HAPTIC) {
            if (clzHapticFeedbackCompatV2 == null) return@hasEnable
            clzHapticFeedbackCompatV2?.apply {
                val mHapticHelper = resolve().firstFieldOrNull {
                    name = "mHapticHelper"
                }
                resolve().firstMethodOrNull {
                    name = "performGestureReadyBack"
                }?.hook {
                    after {
                        metGetHandler?.copy()?.invoke()?.let { handler ->
                            metStartCountDown?.copy()?.invoke(handler, 140L, TIME_OUT_BLOCKER_KEY)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "performGestureBackHandUp"
                }?.hook {
                    before {
                        val isBlocked = metIsBlocked?.invoke<Boolean>(TIME_OUT_BLOCKER_KEY)
                        if (isBlocked == true) {
                            this.result = null
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("lambda") && it.contains("performGestureReadyBack")
                    }
                }?.hook {
                    replaceUnit {
                        mHapticHelper?.copy()?.of(this.instance)?.get()?.let {
                            metPerformExtHapticFeedback?.copy()?.of(it)?.invoke(0)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("lambda") && it.contains("performGestureBackHandUp")
                    }
                }?.hook {
                    replaceUnit {
                        mHapticHelper?.copy()?.of(this.instance)?.get()?.let {
                            metPerformExtHapticFeedback?.copy()?.of(it)?.invoke(1)
                        }
                    }
                }
            }
            "com.miui.home.recents.GestureStubView".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "injectBackKeyEvent"
                    parameters(Boolean::class)
                }?.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
            }
        }
    }
}