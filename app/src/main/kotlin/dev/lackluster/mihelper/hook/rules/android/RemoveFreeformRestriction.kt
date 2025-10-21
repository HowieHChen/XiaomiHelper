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

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RemoveFreeformRestriction : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Android.DISABLE_FREEFORM_RESTRICT) {
            "android.app.ActivityTaskManager".toClassOrNull()?.apply {
                resolve()
                    .optional()
                    .firstMethodOrNull {
                        name = "supportsSplitScreen"
                    }
                    ?.hook {
                        replaceToTrue()
                    }
            }
            "com.android.server.wm.Task".toClassOrNull()?.apply {
                resolve()
                    .optional()
                    .method {
                        name = "isResizeable"
                    }
                    .hookAll {
                        replaceToTrue()
                    }
            }
            "com.android.server.wm.ActivityTaskManagerService".toClassOrNull()?.apply {
                resolve()
                    .optional()
                    .firstMethodOrNull {
                        name = "retrieveSettings"
                    }
                    ?.hook {
                        after {
                            this.instance.asResolver()
                                .firstFieldOrNull {
                                    name = "mDevEnableNonResizableMultiWindow"
                                }
                                ?.set(true)
                        }
                    }
            }
            "com.android.server.wm.WindowManagerService\$SettingsObserver".toClassOrNull()?.apply {
                resolve()
                    .optional()
                    .firstMethodOrNull {
                        name = "updateDevEnableNonResizableMultiWindow"
                    }
                    ?.hook {
                        intercept()
                    }
            }
            "android.util.MiuiMultiWindowAdapter".toClassOrNull()?.apply {
                for (fieldName in setOf(
                    "FREEFORM_BLACK_LIST",
                    "ABNORMAL_FREEFORM_BLACK_LIST",
                    "START_FROM_FREEFORM_BLACK_LIST_ACTIVITY",
                    "FOREGROUND_PIN_APP_BLACK_LIST",
                )) {
                    resolve()
                        .optional()
                        .firstFieldOrNull {
                            name = fieldName
                            modifiers(Modifiers.STATIC)
                        }
                        ?.set(mutableListOf<String>())
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
                    resolve()
                        .optional()
                        .firstMethodOrNull {
                            name = methodName
                        }
                        ?.hook {
                            replaceTo(mutableListOf<String>())
                        }
                }
            }
            "android.util.MiuiMultiWindowUtils".toClassOrNull()?.apply {
                for (methodName in setOf(
                    "isForceResizeable",
                    "supportFreeform"
                )) {
                    resolve()
                        .optional()
                        .firstMethodOrNull {
                            name = methodName
                        }
                        ?.hook {
                            replaceToTrue()
                        }
                }
            }
        }
    }
}