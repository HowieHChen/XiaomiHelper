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
import android.os.Bundle
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object MinusSettings : YukiBaseHooker() {
    private val fldCanSwitchMinusScreen by lazy {
        "com.miui.home.launcher.LauncherAssistantCompat".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "CAN_SWITCH_MINUS_SCREEN"
            modifiers(Modifiers.STATIC)
        }
    }
    private val fldIsInternationalBuild by lazy {
        "miui.os.Build".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "IS_INTERNATIONAL_BUILD"
            modifiers(Modifiers.STATIC)
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.MINUS_RESTORE_SETTING) {
            "com.miui.home.launcher.DeviceConfig".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isUseGoogleMinusScreen"
                }?.hook {
                    before {
                        fldCanSwitchMinusScreen?.set(true)
                    }
                }
            }
            "com.miui.home.launcher.LauncherAssistantCompat".toClassOrNull()?.apply {
                val metGetCurrentPersonalAssistant = "com.miui.home.launcher.common.Utilities".toClassOrNull()
                    ?.resolve()?.firstMethodOrNull {
                        name = "getCurrentPersonalAssistant"
                        modifiers(Modifiers.STATIC)
                    }
                resolve().firstMethodOrNull {
                    name = "newInstance"
                    parameterCount = 1
                }?.hook {
                    before {
                        fldIsInternationalBuild?.set(
                            metGetCurrentPersonalAssistant?.invoke<String>() == "personal_assistant_google"
                        )
                    }
                    after {
                        fldIsInternationalBuild?.set(false)
                    }
                }
            }
            "com.miui.home.launcher.Launcher".toClassOrNull()?.apply {
                resolve().firstConstructorOrNull()?.hook {
                    before {
                        fldIsInternationalBuild?.set(true)
                    }
                    after {
                        fldIsInternationalBuild?.set(false)
                    }
                }
            }
            "com.miui.home.settings.MiuiHomeSettings".toClassOrNull()?.apply {
                val fldSwitchPersonalAssistant = resolve().firstFieldOrNull {
                    name = "mSwitchPersonalAssistant"
                }
                val metGetPreferenceScreen = resolve().firstMethodOrNull {
                    name = "getPreferenceScreen"
                    superclass()
                }
                val metAddPreference = "androidx.preference.PreferenceScreen".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "addPreference"
                    superclass()
                }
                val clzValuePreference = "com.miui.home.settings.preference.ValuePreference".toClassOrNull()
                val metSetVisible = clzValuePreference?.resolve()?.firstMethodOrNull {
                    name = "setVisible"
                    superclass()
                }
                val metSetIntent = clzValuePreference?.resolve()?.firstMethodOrNull {
                    name = "setIntent"
                    superclass()
                }
                val metSetOnPreferenceChangeListener = clzValuePreference?.resolve()?.firstMethodOrNull {
                    name = "setOnPreferenceChangeListener"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "onCreatePreferences"
                    parameters(Bundle::class, String::class)
                }?.hook {
                    after {
                        val switchPersonalAssistant = fldSwitchPersonalAssistant?.copy()?.of(this.instance)?.get() ?: return@after
                        metSetIntent?.copy()?.of(switchPersonalAssistant)?.invoke(
                            Intent("com.miui.home.action.LAUNCHER_PERSONAL_ASSISTANT_SETTING")
                        )
                        metSetOnPreferenceChangeListener?.copy()?.of(switchPersonalAssistant)?.invoke(this.instance)
                        metGetPreferenceScreen?.copy()?.of(this.instance)?.invoke()?.let { preferenceScreen ->
                            metAddPreference?.copy()?.of(preferenceScreen)?.invoke(switchPersonalAssistant)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onResume"
                }?.hook {
                    after {
                        val switchPersonalAssistant = fldSwitchPersonalAssistant?.copy()?.of(this.instance)?.get() ?: return@after
                        metSetVisible?.copy()?.of(switchPersonalAssistant)?.invoke(true)
                    }
                }
            }
        }
    }
}