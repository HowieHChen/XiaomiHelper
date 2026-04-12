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

package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object RemoveFreeformRestriction : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.System.DISABLE_FREEFORM_RESTRICT.get())
    }

    override fun onHook() {
        "android.app.ActivityTaskManager".toClassOrNull()?.apply {
            resolve().optional().firstMethodOrNull {
                name = "supportsSplitScreen"
            }?.hook { result(true) }
        }
        "com.android.server.wm.Task".toClassOrNull()?.apply {
            resolve().optional().method {
                name = "isResizeable"
            }.hookAll { result(true) }
        }
        "com.android.server.wm.ActivityTaskManagerService".toClassOrNull()?.apply {
            val fldDevEnableNonResizableMultiWindow = resolve().firstFieldOrNull {
                name = "mDevEnableNonResizableMultiWindow"
            }?.toTyped<Boolean>()
            resolve().optional().firstMethodOrNull {
                name = "retrieveSettings"
            }?.hook {
                val ori = proceed()
                fldDevEnableNonResizableMultiWindow?.set(thisObject, true)
                result(ori)
            }
        }
        $$"com.android.server.wm.WindowManagerService$SettingsObserver".toClassOrNull()?.apply {
            resolve().optional().firstMethodOrNull {
                name = "updateDevEnableNonResizableMultiWindow"
            }?.hook { result(null) }
        }
        "android.util.MiuiMultiWindowAdapter".toClassOrNull()?.apply {
            val emptyList = mutableListOf<String>()
            for (fieldName in setOf(
                "FREEFORM_BLACK_LIST",
                "ABNORMAL_FREEFORM_BLACK_LIST",
                "START_FROM_FREEFORM_BLACK_LIST_ACTIVITY",
                "FOREGROUND_PIN_APP_BLACK_LIST",
            )) {
                resolve().optional().firstFieldOrNull {
                    name = fieldName
                    modifiers(Modifiers.STATIC)
                }?.set(emptyList)
            }
            for (methodName in setOf(
                "getFreeformBlackList",
                "getFreeformBlackListFromCloud",
                "getAbnormalFreeformBlackList",
                "getAbnormalFreeformBlackListFromCloud",
                "getStartFromFreeformBlackList",
                "getStartFromFreeformBlackListFromCloud",
                "getForegroundPinAppBlackList",
                "getForegroundPinAppBlackListFromCloud",
            )) {
                resolve().optional().firstMethodOrNull {
                    name = methodName
                }?.hook { result(emptyList) }
            }
        }
        "android.util.MiuiMultiWindowUtils".toClassOrNull()?.apply {
            for (methodName in setOf(
                "isForceResizeable",
                "supportFreeform"
            )) {
                resolve().optional().firstMethodOrNull {
                    name = methodName
                }?.hook { result(true) }
            }
        }
    }
}