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
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AutoFlashlightOn : YukiBaseHooker() {
    private val operateFlashlight by lazy {
        "miui.systemui.flashlight.MiFlashlightManager".toClassOrNull()
            ?.resolve()
            ?.firstMethodOrNull {
                name = "asyncOperate"
            }
            ?.self
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.AUTO_FLASH_ON) {
            "miui.systemui.flashlight.MiFlashlightActivity".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "onCreate"
                    superclass()
                }?.hook {
                    after {
                        val activity = this.instance<Activity>()
                        val fromKeyguard = activity.intent.getBooleanExtra("from_keyguard_shortcut", false)
                        if (fromKeyguard) {
                            val flashlightManager = this.instance.asResolver().firstFieldOrNull {
                                name = "flashlightManager"
                            }?.get() ?: return@after
                            val miFlashlightLayout = this.instance.asResolver().firstMethodOrNull {
                                name = "getFlashlightLayout"
                            }?.invoke<View>() ?: return@after
                            operateFlashlight?.let { method ->
                                miFlashlightLayout.postDelayed(700) {
                                    method.invoke(flashlightManager, true, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}