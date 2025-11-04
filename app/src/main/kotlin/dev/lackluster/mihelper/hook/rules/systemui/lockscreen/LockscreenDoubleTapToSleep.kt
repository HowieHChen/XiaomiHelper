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
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable
import kotlin.math.abs

object LockscreenDoubleTapToSleep : YukiBaseHooker() {
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
                        XposedHelpers.setAdditionalInstanceField(view, "currentTouchTime", 0L)
                        XposedHelpers.setAdditionalInstanceField(view, "currentTouchX", 0f)
                        XposedHelpers.setAdditionalInstanceField(view, "currentTouchY", 0f)
                        view.setOnTouchListener { v, motionEvent ->
                            if (motionEvent.action != MotionEvent.ACTION_DOWN) return@setOnTouchListener false
                            var currentTouchTime = XposedHelpers.getAdditionalInstanceField(v, "currentTouchTime") as? Long ?: 0L
                            var currentTouchX = XposedHelpers.getAdditionalInstanceField(v, "currentTouchX") as? Float ?: 0f
                            var currentTouchY = XposedHelpers.getAdditionalInstanceField(v, "currentTouchY") as? Float ?: 0f
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
                            XposedHelpers.setAdditionalInstanceField(v, "currentTouchTime", currentTouchTime)
                            XposedHelpers.setAdditionalInstanceField(v, "currentTouchX", currentTouchX)
                            XposedHelpers.setAdditionalInstanceField(v, "currentTouchY", currentTouchY)
                            v.performClick()
                            return@setOnTouchListener false
                        }
                    }
                }
        }
    }
}