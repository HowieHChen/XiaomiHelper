/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/library/hook/src/main/java/com/sevtinge/hyperceiler/hook/module/hook/systemui/other/MonetThemeOverlay.java>
 * Copyright (C) 2023-2025 HyperCeiler Contributions

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

package dev.lackluster.mihelper.hook.rules.systemui

import androidx.core.graphics.toColorInt
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object MonetOverlay : StaticHooker() {
    private val overlayColor by lazy {
        try {
            Preferences.SystemUI.NotifCenter.MONET_OVERLAY_COLOR.get().toColorInt()
        } catch (_: Exception) {
            "#FF3482FF".toColorInt()
        }
    }

    override fun onInit() {
        updateSelfState(Preferences.SystemUI.NotifCenter.ENABLE_MONET_OVERLAY.get())
    }

    override fun onHook() {
        "com.android.systemui.theme.ThemeOverlayController".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "createOverlays"
                parameters(Int::class)
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = overlayColor
                result(proceed(newArgs))
            }
        }
    }
}