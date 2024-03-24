/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/home/gesture/QuickBack.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.app.ActivityManager
import android.app.ActivityOptions
import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.recents_quick_switch_left_enter
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.recents_quick_switch_left_exit
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.recents_quick_switch_right_enter
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.recents_quick_switch_right_exit
import dev.lackluster.mihelper.utils.factory.hasEnable


object QuickBack : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.QUICK_BACK) {
            "com.miui.home.recents.GestureStubView".toClass().method {
                name = "isDisableQuickSwitch"
            }.hook {
                replaceToFalse()
            }
            "com.miui.home.recents.GestureStubView".toClass().method {
                name = "getNextTask"
                param(ContextClass, BooleanType, IntType)
            }.hook {
                before {
                    val context = this.args(0).any() as? Context ?: return@before
                    var runningTask: ActivityManager.RunningTaskInfo? = null
                    val recentsModel = XposedHelpers.callStaticMethod("com.miui.home.recents.RecentsModel".toClass(), "getInstance", context)
                    val taskLoader = XposedHelpers.callMethod(recentsModel, "getTaskLoader")
                    val createLoadPlan = XposedHelpers.callMethod(taskLoader, "createLoadPlan", context)
                    XposedHelpers.callMethod(taskLoader, "preloadTasks", createLoadPlan, -1)
                    val taskStack = XposedHelpers.callMethod(createLoadPlan, "getTaskStack")
                    var activityOptions: ActivityOptions? = null
                    if (
                        taskStack == null || XposedHelpers.callMethod(taskStack, "getTaskCount") as Int == 0 ||
                        (XposedHelpers.callMethod(recentsModel, "getRunningTask" ) as ActivityManager.RunningTaskInfo?)?.also {
                            runningTask = it
                        } == null
                    ) {
                        this.result = null
                        return@before
                    }
                    val stackTasks = XposedHelpers.callMethod(taskStack, "getStackTasks") as ArrayList<*>
                    val size = stackTasks.size
                    var task: Any? = null
                    var i2 = 0
                    while (true) {
                        if (i2 >= size - 1) {
                            break
                        } else if (XposedHelpers.getObjectField(
                                XposedHelpers.getObjectField(stackTasks[i2], "key"),
                                "id"
                            ) as Int == runningTask!!.id
                        ) {
                            task = stackTasks[i2 + 1]
                            break
                        } else {
                            i2++
                        }
                    }
                    if (task == null && size >= 1 && "com.miui.home" == runningTask!!.baseActivity!!.packageName) {
                        task = stackTasks[0]
                    }
                    if (task != null && XposedHelpers.getObjectField(task, "icon") == null) {
                        XposedHelpers.setObjectField(
                            task, "icon", XposedHelpers.callMethod(
                                taskLoader, "getAndUpdateActivityIcon",
                                XposedHelpers.getObjectField(task, "key"),
                                XposedHelpers.getObjectField(task, "taskDescription"),
                                context.resources, true
                            )
                        )
                    }
                    if (!this.args(1).boolean() || task == null) {
                        this.result = task
                        return@before
                    }
                    val i = this.args(2).int()
                    if (i == 0) {
                        activityOptions = ActivityOptions.makeCustomAnimation(
                            context,
                            recents_quick_switch_left_enter,
                            recents_quick_switch_left_exit
                        )
                    } else if (i == 1) {
                        activityOptions = ActivityOptions.makeCustomAnimation(
                            context,
                            recents_quick_switch_right_enter,
                            recents_quick_switch_right_exit
                        )
                    }
                    val iActivityManager = XposedHelpers.callStaticMethod(
                        "android.app.ActivityManagerNative".toClass(),
                        "getDefault"
                    )
                    if (iActivityManager != null) {
                        try {
                            if (XposedHelpers.getObjectField(
                                    XposedHelpers.getObjectField(task, "key"),
                                    "windowingMode"
                                ) as Int == 3
                            ) {
                                if (activityOptions == null) {
                                    activityOptions = ActivityOptions.makeBasic()
                                }
                                activityOptions!!.javaClass.getMethod(
                                    "setLaunchWindowingMode",
                                    Int::class.java
                                ).invoke(activityOptions, 4)
                            }
                            XposedHelpers.callMethod(
                                iActivityManager, "startActivityFromRecents",
                                XposedHelpers.getObjectField(
                                    XposedHelpers.getObjectField(task, "key"),
                                    "id"
                                ),
                                activityOptions?.toBundle()
                            )
                            this.result = task
                            return@before
                        } catch (e: Exception) {
                            YLog.error("$e")
                            this.result = task
                            return@before
                        }
                    }
                    this.result = task
                }
            }
        }
    }
}