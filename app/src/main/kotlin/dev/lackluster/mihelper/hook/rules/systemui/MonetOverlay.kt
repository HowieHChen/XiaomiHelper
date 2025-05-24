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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import androidx.core.graphics.toColorInt

object MonetOverlay : YukiBaseHooker() {
    private val overlayColor by lazy {
        try {
            (Prefs.getString(Pref.Key.SystemUI.NotifCenter.MONET_OVERLAY_COLOR, "#FF3482FF") ?: "#FF3482FF").toColorInt()
        } catch (_: Exception) {
            "#FF3482FF".toColorInt()
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.MONET_OVERLAY) {
            "com.android.systemui.theme.ThemeOverlayController".toClassOrNull()?.apply {
                method {
                    name = "createOverlays"
                    param(IntType)
                }.hook {
                    before {
                        this.args(0).set(overlayColor)
                    }
                }
            }
        }
    }
}