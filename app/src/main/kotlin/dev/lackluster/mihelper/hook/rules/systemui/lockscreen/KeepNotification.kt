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

package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.content.res.Resources
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.ResourcesWrapper
import dev.lackluster.mihelper.utils.factory.hasEnable

object KeepNotification : YukiBaseHooker() {
    private val booleanOverrides by lazy {
        mapOf(
            ResourcesUtils.kept_notifications_on_keyguard to true
        )
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.KEEP_NOTIFICATION) {
            "com.android.systemui.MiuiOperatorCustomizedPolicy".toClassOrNull()?.apply {
                val fldShowKeyguardNotifications = resolve().firstFieldOrNull {
                    name = "mShowKeyguardNotifications"
                }
                resolve().firstConstructor().hook {
                    after {
                        fldShowKeyguardNotifications?.copy()?.of(this.instance)?.set(true)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "updateMiuiOperatorConfig"
                }?.hook {
                    after {
                        fldShowKeyguardNotifications?.copy()?.of(this.instance)?.set(true)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getResourcesForOperation"
                }?.hook {
                    after {
                        val ori = this.result<Resources>()
                        if (ori != null && ori !is ResourcesWrapper) {
                            this.result = ResourcesWrapper(ori, booleanOverrides)
                        }
                    }
                }
            }
        }
    }
}