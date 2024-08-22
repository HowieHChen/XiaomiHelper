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

package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object FakePremium :YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.FAKE_PREMIUM) {
            val deviceLevelUtilsClass = "com.miui.home.launcher.common.DeviceLevelUtils".toClassOrNull()
            val deviceConfigClass = "com.miui.home.launcher.DeviceConfig".toClassOrNull()
            "com.miui.home.launcher.common.CpuLevelUtils".toClassOrNull()
                ?.method {
                    name = "getQualcommCpuLevel"
                    paramCount = 1
                }?.ignored()
                ?.hook {
                    replaceTo(2)
                }
            "miuix.animation.utils.DeviceUtils".toClassOrNull()
                ?.method {
                    name = "getQualcommCpuLevel"
                    paramCount = 1
                }?.ignored()
                ?.hook {
                    replaceTo(2)
                }
            deviceLevelUtilsClass?.apply {
                method {
                    name = "isUseSimpleAnim"
                }.ignored().hook {
                    replaceToFalse()
                }
                method {
                    name = "getDeviceLevel"
                }.ignored().hook {
                    replaceTo(2)
                }
                method {
                    name = "getDeviceLevelOfCpuAndGpu"
                }.ignored().hook {
                    replaceTo(2)
                }
                method {
                    name = "isLowLevelOrLiteDevice"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            deviceConfigClass?.apply {
                method {
                    name = "isSupportCompleteAnimation"
                }.ignored().hook {
                    replaceToTrue()
                }
                method {
                    name = "isMiuiLiteVersion"
                }.ignored().hook {
                    replaceToFalse()
                }

            }
            "com.miui.home.launcher.util.noword.NoWordSettingHelperKt".toClassOrNull()
                ?.method {
                    name = "isNoWordAvailable"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "android.os.SystemProperties".toClassOrNull()
                ?.method {
                    name = "getBoolean"
                    param(StringClass, BooleanType)
                }?.ignored()
                ?.hookAll {
                    before {
                        if (this.args(0).string() == "ro.config.low_ram.threshold_gb") {
                            this.result = false
                        }
                        if (this.args(0).string() == "ro.miui.backdrop_sampling_enabled") {
                            this.result = true
                        }
                    }
                }
            "com.miui.home.launcher.common.Utilities".toClassOrNull()
                ?.method {
                    name = "canLockTaskView"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "com.miui.home.launcher.MIUIWidgetUtil".toClassOrNull()
                ?.method {
                    name = "isMIUIWidgetSupport"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "com.miui.home.launcher.MiuiHomeLog".toClassOrNull()
                ?.method {
                    name = "log"
                    param(StringClass, StringClass)
                }?.ignored()
                ?.hook {
                    intercept()
                }
            "com.xiaomi.onetrack.OneTrack".toClassOrNull()
                ?.method {
                    name = "isDisable"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }

            "com.miui.home.launcher.common.BlurUtilities".toClass().apply {
                method {
                    name = "isBackgroundBlurSupported"
                }.hook {
                    replaceToTrue()
                }
                field {
                    name = "IS_BACKGROUND_BLUR_ENABLED"
                    modifiers { isStatic }
                }.get().setTrue()
            }
            if (!Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)) {
                "com.miui.home.launcher.common.BlurUtilities".toClass().apply {
                    method {
                        name = "setBackgroundBlurEnabled"
                        modifiers { isStatic }
                    }.hook {
                        intercept()
                    }
                    method {
                        name = "isBlurSupported"
                    }.hook {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}