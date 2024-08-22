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

package dev.lackluster.mihelper.hook.rules.miuihome.minus

import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object MinusSettings : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.MINUS_RESTORE_SETTING) {
            val clazzUtilities = "com.miui.home.launcher.common.Utilities".toClass()
            val clazzLauncher = "com.miui.home.launcher.Launcher".toClass()
            val clazzMiuiHomeSettings = "com.miui.home.settings.MiuiHomeSettings".toClass()
            val isInternationalBuild = "miui.os.Build".toClass().field {
                name = "IS_INTERNATIONAL_BUILD"
                modifiers { isStatic }
            }.get()
            "com.miui.home.launcher.DeviceConfig".toClass().method {
                name = "isUseGoogleMinusScreen"
            }.hook {
                before {
                    "com.miui.home.launcher.LauncherAssistantCompat".toClass().field {
                        name = "CAN_SWITCH_MINUS_SCREEN"
                        modifiers { isStatic }
                    }.get().setTrue()
                }
            }
            "com.miui.home.launcher.LauncherAssistantCompat".toClass().method {
                name = "newInstance"
                param(clazzLauncher.name)
            }.hook {
                before {
                    val isPersonalAssistantGoogle = clazzUtilities.method {
                        name = "getCurrentPersonalAssistant"
                    }.get().string() == "personal_assistant_google"
                    isInternationalBuild.set(isPersonalAssistantGoogle)
                }
                after {
                    isInternationalBuild.setFalse()
                }
            }
            clazzLauncher.constructor().hook {
                before {
                    isInternationalBuild.setTrue()
                }
                after {
                    isInternationalBuild.setFalse()
                }
            }
            clazzMiuiHomeSettings.apply {
                method {
                    name = "onCreatePreferences"
                    param(BundleClass, StringClass)
                }.hook {
                    after {
                        val mSwitchPersonalAssistant = this.instance.current().field {
                            name = "mSwitchPersonalAssistant"
                        }.any() ?: return@after
                        mSwitchPersonalAssistant.current().method {
                            name = "setIntent"
                            superClass()
                        }.call(Intent("com.miui.home.action.LAUNCHER_PERSONAL_ASSISTANT_SETTING"))
                        mSwitchPersonalAssistant.current().method {
                            name = "setOnPreferenceChangeListener"
                            superClass()
                        }.call(this.instance)
                        this.instance.current().method {
                            name = "getPreferenceScreen"
                            superClass()
                        }.call()?.current()?.method {
                            name = "addPreference"
                            superClass()
                        }?.call(mSwitchPersonalAssistant)
                    }
                }
                method {
                    name = "onResume"
                }.hook {
                    after {
                        this.instance.current().field {
                            name = "mSwitchPersonalAssistant"
                        }.any()?.current()?.method {
                            name = "setVisible"
                            superClass()
                        }?.call(true)
                    }
                }
            }
        }
    }
}