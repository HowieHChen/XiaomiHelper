/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.plugin

import android.app.Activity
import android.view.View
import androidx.core.view.postDelayed
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object AutoFlashlightOn : StaticHooker() {
    private val operateFlashlight by lazy {
        "miui.systemui.flashlight.MiFlashlightManager".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "asyncOperate"
            }?.toTyped<Unit>()
    }

    override fun onInit() {
        updateSelfState(Preferences.SystemUI.Plugin.LOCKSCREEN_AUTO_FLASH_ON.get())
    }

    override fun onHook() {
        "miui.systemui.flashlight.MiFlashlightActivity".toClassOrNull()?.apply {
            val fldFlashlightManager = resolve().firstFieldOrNull {
                name = "flashlightManager"
            }?.toTyped<Any>()
            val metGetFlashlightLayout = resolve().firstMethodOrNull {
                name = "getFlashlightLayout"
            }?.toTyped<View>()
            resolve().firstMethodOrNull {
                name = "onCreate"
                superclass()
            }?.hook {
                val ori = proceed()
                val activity = thisObject as? Activity
                val fromKeyguard = activity?.intent?.getBooleanExtra("from_keyguard_shortcut", false) != false
                if (fromKeyguard) {
                    val flashlightManager = fldFlashlightManager?.get(thisObject)
                    val miFlashlightLayout = metGetFlashlightLayout?.invoke(thisObject)
                    if (operateFlashlight != null && flashlightManager != null && miFlashlightLayout != null) {
                        miFlashlightLayout.postDelayed(700) {
                            operateFlashlight?.invoke(flashlightManager, true, null)
                        }
                    }
                }
                result(ori)
            }
        }
    }
}