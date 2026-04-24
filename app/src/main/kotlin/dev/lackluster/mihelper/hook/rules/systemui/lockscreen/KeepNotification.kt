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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.ResourcesWrapper
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object KeepNotification : StaticHooker() {
    private val booleanOverrides by lazy {
        mapOf(
            ResourcesUtils.kept_notifications_on_keyguard to true
        )
    }

    override fun onInit() {
        updateSelfState(Preferences.SystemUI.LockScreen.KEEP_NOTIFICATION.get())
    }

    override fun onHook() {
        "com.android.systemui.MiuiOperatorCustomizedPolicy".toClassOrNull()?.apply {
            val fldShowKeyguardNotifications = resolve().firstFieldOrNull {
                name = "mShowKeyguardNotifications"
            }?.toTyped<Boolean>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                fldShowKeyguardNotifications?.set(thisObject, true)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "updateMiuiOperatorConfig"
            }?.hook {
                val ori = proceed()
                fldShowKeyguardNotifications?.set(thisObject, true)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "getResourcesForOperation"
            }?.hook {
                val ori = proceed()
                val res = ori as? Resources
                if (res != null && ori !is ResourcesWrapper) {
                    result(ResourcesWrapper(ori, booleanOverrides))
                } else {
                    result(ori)
                }
            }
        }
    }
}