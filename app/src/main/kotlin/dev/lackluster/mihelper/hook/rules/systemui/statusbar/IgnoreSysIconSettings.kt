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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.utils.Prefs

object IgnoreSysIconSettings : YukiBaseHooker() {
    private val ignoreSystem = Prefs.getBoolean(IconTuner.IGNORE_SYS_SETTINGS, false)
    private val hidePrivacy = Prefs.getBoolean(IconTuner.HIDE_PRIVACY, false)

    override fun onHook() {
        if (ignoreSystem || hidePrivacy) {
            "com.android.systemui.statusbar.policy.StatusBarIconObserver".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isIconBlocked"
                }?.hook {
                    before {
                        val slot = this.args(0).string()
                        if (slot == "privacy") {
                            this.result = hidePrivacy
                        } else if (ignoreSystem) {
                            this.result = false
                        }
                    }
                }
                if (ignoreSystem) {
                    resolve().firstMethodOrNull {
                        name = "loadStatusBarIcon"
                    }?.hook {
                        replaceTo("")
                    }
                }
            }
        }
        if (ignoreSystem) {
            "com.android.systemui.statusbar.policy.NetworkSpeedController".toClassOrNull()?.apply {
                val mShowNetworkSpeed = resolve().firstFieldOrNull {
                    name = "mShowNetworkSpeed"
                }
                resolve().firstMethodOrNull {
                    name {
                        it.contains("mupdateVisibility")
                    }
                }?.hook {
                    before {
                        val networkSpeedController = this.args(0).any()
                        val tag = this.args(1).string()
                        if (tag == "show") {
                            mShowNetworkSpeed?.copy()?.of(networkSpeedController)?.set(true)
                        }
                    }
                }
            }
        }
    }
}