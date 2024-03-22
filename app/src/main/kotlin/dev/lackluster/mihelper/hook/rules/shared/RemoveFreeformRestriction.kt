/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxFreeForm <https://github.com/YifePlayte/MaxFreeForm/blob/main/app/src/main/java/com/yifeplayte/maxfreeform/hook/hooks/multipackage/RemoveSmallWindowRestrictions.kt>
 * Copyright (C) 2023 YifePlayte

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

package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RemoveFreeformRestriction : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Android.DISABLE_FREEFORM_RESTRICT) {
            val activityTaskManagerService = "com.android.server.wm.ActivityTaskManagerService".toClassOrNull()
            val settingsObserver = "com.android.server.wm.WindowManagerService\$SettingsObserver".toClassOrNull()
            val wmTask = "com.android.server.wm.Task".toClassOrNull()
            val miuiMultiWindowAdapter = "android.util.MiuiMultiWindowAdapter".toClassOrNull()
            val miuiMultiWindowUtils = "android.util.MiuiMultiWindowUtils".toClassOrNull()
            runCatching {
                "android.app.ActivityTaskManager".toClass().method {
                    name = "supportsSplitScreen"
                }.giveAll().hookAll {
                    replaceToTrue()
                }
            }
            runCatching {
                activityTaskManagerService?.method {
                    name = "retrieveSettings"
                }?.hookAll {
                    after {
                        this.instance.current().field {
                            name = "mDevEnableNonResizableMultiWindow"
                        }.setTrue()
                    }
                }
            }
            runCatching {
                settingsObserver?.apply {
                    method {
                        name = "updateDevEnableNonResizableMultiWindow"
                    }.hookAll {
                        after {
                            val this0 = this.instance.current().field {
                                name = "this\$0"
                            }.any() ?: return@after
                            val mAtmService = this0.current().field {
                                name = "mAtmService"
                            }.any() ?: return@after
                            mAtmService.current().field {
                                name = "mDevEnableNonResizableMultiWindow"
                            }.setTrue()
                        }
                    }
                    method {
                        name = "onChange"
                    }.hookAll {
                        after {
                            val this0 = this.instance.current().field {
                                name = "this\$0"
                            }.any() ?: return@after
                            val mAtmService = this0.current().field {
                                name = "mAtmService"
                            }.any() ?: return@after
                            mAtmService.current().field {
                                name = "mDevEnableNonResizableMultiWindow"
                            }.setTrue()
                        }
                    }
                }
            }
            runCatching {
                miuiMultiWindowUtils?.method {
                    name = "isForceResizeable"
                }?.giveAll()?.hookAll {
                    replaceToTrue()
                }
            }
            runCatching {
                for (methodName in setOf(
                    "getFreeformBlackList",
                    "getFreeformBlackListFromCloud",
                    "getAbnormalFreeformBlackList",
                    "getAbnormalFreeformBlackListFromCloud",
                    "getStartFromFreeformBlackList",
                    "getStartFromFreeformBlackListFromCloud",
                    "getForegroundPinAppBlackList",
                    "getForegroundPinAppBlackListFromCloud"
                )) {
                    miuiMultiWindowAdapter?.method {
                        name = methodName
                    }?.ignored()?.giveAll()?.hookAll {
                        replaceTo(mutableListOf<String>())
                    }
                }
            }
            runCatching {
                wmTask?.method {
                    name = "isResizeable"
                }?.hook {
                    replaceToTrue()
                }
            }
            runCatching {
                for (methodName in setOf(
                    "isForceResizeable",
                    "supportFreeform"
                )) {
                    miuiMultiWindowUtils?.method {
                        name = methodName
                    }?.ignored()?.giveAll()?.hookAll {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}