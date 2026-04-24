/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxFreeForm <https://github.com/YifePlayte/MaxFreeForm/blob/main/app/src/main/java/com/yifeplayte/maxfreeform/hook/hooks/singlepackage/home/UnlockEnterSmallWindow.kt>
 * Copyright (C) 2023 YifePlayte

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

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import java.util.concurrent.CopyOnWriteArraySet

object AllowMoreFreeformForHome : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.System.ALLOW_MORE_FREEFORM.get())
    }

    override fun onHook() {
        "com.miui.home.launcher.RecentsAndFSGestureUtils".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "canTaskEnterMiniSmallWindow"
            }?.hook {
                result(true)
            }
            resolve().firstMethodOrNull {
                name = "canTaskEnterSmallWindow"
                parameterCount = 4
                parameters(Context::class, String::class, Int::class, Int::class)
            }?.hook {
                result(true)
            }
        }
        "com.miui.home.smallwindow.SmallWindowStateHelperUseManager".toClassOrNull()?.apply {
            val fldMiniSmallWindowInfoSet = resolve().firstFieldOrNull {
                name = "mMiniSmallWindowInfoSet"
            }?.toTyped<CopyOnWriteArraySet<*>>()
            resolve().firstMethodOrNull {
                name = "canEnterMiniSmallWindow"
            }?.hook {
                result(
                    fldMiniSmallWindowInfoSet?.get(thisObject).isNullOrEmpty()
                )
            }
        }
    }
}