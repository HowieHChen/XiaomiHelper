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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object IgnoreSysIconSettings : StaticHooker() {
    private val ignoreSystem by Preferences.SystemUI.StatusBar.IconTuner.IGNORE_SYS_SETTINGS.lazyGet()
    private val hidePrivacy by Preferences.SystemUI.StatusBar.IconTuner.HIDE_PRIVACY.lazyGet()
    private val showNetSpeed by lazy {
        Preferences.SystemUI.StatusBar.IconTuner.NET_SPEED.get() != 4
    }

    override fun onInit() {
        updateSelfState(ignoreSystem || hidePrivacy)
    }

    override fun onHook() {
        "com.android.systemui.statusbar.policy.StatusBarIconObserver".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isIconBlocked"
            }?.hook {
                val slot = getArg(0) as? String
                if (slot == "privacy") {
                    result(hidePrivacy)
                } else if (ignoreSystem) {
                    result(false)
                } else {
                    result(proceed())
                }
            }
            if (ignoreSystem) {
                resolve().firstMethodOrNull {
                    name = "loadStatusBarIcon"
                }?.hook {
                    result("")
                }
            }
        }
        if (ignoreSystem) {
            "com.android.systemui.statusbar.policy.NetworkSpeedController".toClassOrNull()?.apply {
                val mShowNetworkSpeed = resolve().firstFieldOrNull {
                    name = "mShowNetworkSpeed"
                }?.toTyped<Boolean>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    mShowNetworkSpeed?.set(thisObject, showNetSpeed)
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name {
                        it.contains("mupdateVisibility")
                    }
                }?.hook {
                    val networkSpeedController = getArg(0)
                    val tag = getArg(1) as? String
                    if (tag == "show" && networkSpeedController != null) {
                        mShowNetworkSpeed?.set(networkSpeedController, showNetSpeed)
                    }
                    result(proceed())
                }
            }
        }
    }
}