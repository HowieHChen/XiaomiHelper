/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxFreeForm <https://github.com/YifePlayte/MaxFreeForm/blob/main/app/src/main/java/com/yifeplayte/maxfreeform/hook/hooks/singlepackage/android/UnlockFreeformQuantityLimit.kt>
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

package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object AllowMoreFreeform : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true
    private val metGetMaxMiuiFreeFormStackCount by lazy {
        "com.android.server.wm.MiuiFreeFormStackDisplayStrategy".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getMaxMiuiFreeFormStackCount"
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.System.ALLOW_MORE_FREEFORM.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
        metGetMaxMiuiFreeFormStackCount?.hook { result(255) }
    }
}