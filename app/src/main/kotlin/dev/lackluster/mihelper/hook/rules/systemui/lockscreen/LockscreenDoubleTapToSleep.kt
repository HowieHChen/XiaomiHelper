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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import kotlin.math.abs

object LockscreenDoubleTapToSleep : StaticHooker() {
    private var View.touchTime by extraOf<Long>("KEY_CURRENT_TOUCH_TIME")
    private var View.touchX by extraOf<Float>("KEY_CURRENT_TOUCH_X")
    private var View.touchY by extraOf<Float>("KEY_CURRENT_TOUCH_Y")

    override fun onInit() {
        updateSelfState(Preferences.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP.get())
    }

    override fun onHook() {
        val clzNotificationsQuickSettingsContainer = "com.android.systemui.shade.NotificationsQuickSettingsContainer".toClass()
        val metGoToSleep = PowerManager::class.resolve().firstMethodOrNull {
            name = "goToSleep"
            parameters(Long::class)
        }?.toTyped<Unit>()
        clzNotificationsQuickSettingsContainer.resolve().firstMethodOrNull {
            name = "dispatchTouchEvent"
        }?.hook {
            val view = thisObject as? View
            val event = getArg(0) as? MotionEvent
            if (view == null || event == null) {
                return@hook result(proceed())
            }
            if (event.action == MotionEvent.ACTION_DOWN) {
                var currentTouchTime = view.touchTime ?: 0L
                var currentTouchX = view.touchX ?: 0f
                var currentTouchY = view.touchY ?: 0f
                val lastTouchTime = currentTouchTime
                val lastTouchX = currentTouchX
                val lastTouchY = currentTouchY
                currentTouchTime = System.currentTimeMillis()
                currentTouchX = event.x
                currentTouchY = event.y
                if (currentTouchTime - lastTouchTime < 250L
                    && abs(currentTouchX - lastTouchX) < 100f
                    && abs(currentTouchY - lastTouchY) < 100f
                ) {
                    val keyguardMgr = view.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    val powerManager = view.context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (keyguardMgr.isKeyguardLocked) {
                        metGoToSleep?.invoke(powerManager, SystemClock.uptimeMillis())
                    }
                    view.touchTime = 0L
                    view.touchX = 0f
                    view.touchY = 0f
                    return@hook result(true)
                }
                view.touchTime = currentTouchTime
                view.touchX = currentTouchX
                view.touchY = currentTouchY
            }
            result(proceed())
        }
    }
}