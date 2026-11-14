/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/library/hook/src/main/java/com/sevtinge/hyperceiler/hook/module/rules/remotecontroller/DisableAd.java>
 * Copyright (C) 2023-2025 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.remotecontroller

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object AdBlocker : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.RemoteController.AD_BLOCKER.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
        "com.xiaomi.mitv.phone.remotecontroller.common.activity.BaseActivity".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setActionMark"
                parameterCount = 2
            }?.hook {
                result(null)
            }
        }
        "com.duokan.phone.remotecontroller.operation.SHBusinessManager".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isUserClosedBanner"
            }?.hook {
               result(true)
            }
        }
    }
}