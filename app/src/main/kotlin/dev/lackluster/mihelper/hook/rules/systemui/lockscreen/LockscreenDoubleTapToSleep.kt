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
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.hasEnable
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField
import kotlin.math.abs

object LockscreenDoubleTapToSleep : YukiBaseHooker() {
    private const val KEY_CURRENT_TOUCH_TIME = "KEY_CURRENT_TOUCH_TIME"
    private const val KEY_CURRENT_TOUCH_X = "KEY_CURRENT_TOUCH_X"
    private const val KEY_CURRENT_TOUCH_Y = "KEY_CURRENT_TOUCH_Y"

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP) {
            "com.android.systemui.shade.NotificationsQuickSettingsContainer".toClass()
                .resolve()
                .firstMethodOrNull {
                    name = "onFinishInflate"
                }
                ?.hook {
                    before {
                        val view = this.instance as View
                        view.setAdditionalInstanceField(KEY_CURRENT_TOUCH_TIME, 0L)
                        view.setAdditionalInstanceField(KEY_CURRENT_TOUCH_X, 0f)
                        view.setAdditionalInstanceField(KEY_CURRENT_TOUCH_Y, 0f)
                        view.setOnTouchListener { v, motionEvent ->
                            if (motionEvent.action != MotionEvent.ACTION_DOWN) return@setOnTouchListener false
                            var currentTouchTime = v.getAdditionalInstanceField(KEY_CURRENT_TOUCH_TIME, 0L) ?: 0L
                            var currentTouchX = v.getAdditionalInstanceField(KEY_CURRENT_TOUCH_X, 0f) ?: 0f
                            var currentTouchY = v.getAdditionalInstanceField(KEY_CURRENT_TOUCH_Y, 0f) ?: 0f
                            val lastTouchTime = currentTouchTime
                            val lastTouchX = currentTouchX
                            val lastTouchY = currentTouchY

                            currentTouchTime = System.currentTimeMillis()
                            currentTouchX = motionEvent.x
                            currentTouchY = motionEvent.y

                            if (currentTouchTime - lastTouchTime < 250L
                                && abs(currentTouchX - lastTouchX) < 100f
                                && abs(currentTouchY - lastTouchY) < 100f
                            ) {
                                val keyguardMgr = v.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                                if (keyguardMgr.isKeyguardLocked) {
                                    XposedHelpers.callMethod(
                                        v.context.getSystemService(Context.POWER_SERVICE),
                                        "goToSleep",
                                        SystemClock.uptimeMillis()
                                    )
                                }
                                currentTouchTime = 0L
                                currentTouchX = 0f
                                currentTouchY = 0f
                            }
                            v.setAdditionalInstanceField(KEY_CURRENT_TOUCH_TIME, currentTouchTime)
                            v.setAdditionalInstanceField(KEY_CURRENT_TOUCH_X, currentTouchX)
                            v.setAdditionalInstanceField(KEY_CURRENT_TOUCH_Y, currentTouchY)
                            v.performClick()
                            return@setOnTouchListener false
                        }
                    }
                }
        }
    }
}