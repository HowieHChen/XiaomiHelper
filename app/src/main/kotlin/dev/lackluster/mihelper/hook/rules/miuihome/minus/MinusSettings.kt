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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object MinusSettings : StaticHooker() {
    private val fldCanSwitchMinusScreen by lazy {
        "com.miui.home.launcher.LauncherAssistantCompat".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "CAN_SWITCH_MINUS_SCREEN"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Boolean>()
    }
    private val fldIsInternationalBuild by lazy {
        "miui.os.Build".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "IS_INTERNATIONAL_BUILD"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.RESTORE_MINUS_SETTING.get())
    }

    override fun onHook() {
        "com.miui.home.launcher.DeviceConfig".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isUseGoogleMinusScreen"
            }?.hook {
                fldCanSwitchMinusScreen?.set(null, true)
                result(proceed())
            }
        }
        "com.miui.home.launcher.LauncherAssistantCompat".toClassOrNull()?.apply {
            val metGetCurrentPersonalAssistant = "com.miui.home.launcher.common.Utilities".toClassOrNull()
                ?.resolve()?.firstMethodOrNull {
                    name = "getCurrentPersonalAssistant"
                    modifiers(Modifiers.STATIC)
                }?.toTyped<String>()
            resolve().firstMethodOrNull {
                name = "newInstance"
                parameterCount = 1
            }?.hook {
                val real = fldIsInternationalBuild?.get(null)
                fldIsInternationalBuild?.set(
                    null,
                    metGetCurrentPersonalAssistant?.invoke(null) == "personal_assistant_google"
                )
                val ori = proceed()
                fldIsInternationalBuild?.set(null, real)
                result(ori)
            }
        }
        "com.miui.home.launcher.Launcher".toClassOrNull()?.apply {
            resolve().firstConstructorOrNull()?.hook {
                val real = fldIsInternationalBuild?.get(null)
                fldIsInternationalBuild?.set(null, true)
                val ori = proceed()
                fldIsInternationalBuild?.set(null, real)
                result(ori)
            }
        }
        "com.miui.home.settings.MiuiHomeSettings".toClassOrNull()?.apply {
            val fldSwitchPersonalAssistant = resolve().firstFieldOrNull {
                name = "mSwitchPersonalAssistant"
            }?.toTyped<Any>()
            val metGetPreferenceScreen = resolve().firstMethodOrNull {
                name = "getPreferenceScreen"
                superclass()
            }?.toTyped<Any>()
            val metAddPreference = "androidx.preference.PreferenceScreen".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "addPreference"
                superclass()
            }?.toTyped<Boolean>()
            val clzValuePreference = "com.miui.home.settings.preference.ValuePreference".toClassOrNull()
            val metSetVisible = clzValuePreference?.resolve()?.firstMethodOrNull {
                name = "setVisible"
                superclass()
            }?.toTyped<Unit>()
            val metSetIntent = clzValuePreference?.resolve()?.firstMethodOrNull {
                name = "setIntent"
                superclass()
            }?.toTyped<Unit>()
            val metSetOnPreferenceChangeListener = clzValuePreference?.resolve()?.firstMethodOrNull {
                name = "setOnPreferenceChangeListener"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "onCreatePreferences"
                parameters(Bundle::class, String::class)
            }?.hook {
                val ori = proceed()
                val switchPersonalAssistant = fldSwitchPersonalAssistant?.get(thisObject)
                if (switchPersonalAssistant != null) {
                    metSetIntent?.invoke(
                        switchPersonalAssistant,
                        Intent("com.miui.home.action.LAUNCHER_PERSONAL_ASSISTANT_SETTING")
                    )
                    metSetOnPreferenceChangeListener?.invoke(
                        switchPersonalAssistant,
                        thisObject
                    )
                    metGetPreferenceScreen?.invoke(thisObject)?.let { preferenceScreen ->
                        metAddPreference?.invoke(preferenceScreen, switchPersonalAssistant)
                    }
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onResume"
            }?.hook {
                val ori = proceed()
                fldSwitchPersonalAssistant?.get(thisObject)?.let { switchPersonalAssistant ->
                    metSetVisible?.invoke(switchPersonalAssistant, true)
                }
                result(ori)
            }
        }
    }
}