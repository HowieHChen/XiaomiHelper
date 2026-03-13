/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/ab88528abf965fd7415a5e34a050140c10e305de/library/libhook/src/main/java/com/sevtinge/hyperceiler/libhook/rules/systemui/plugin/systemui/HideEditButton.java>
 * Copyright (C) 2023-2026 HyperCeiler Contributions

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

package dev.lackluster.mihelper.hook.rules.systemui.plugin

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideEditButton : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.CONTROL_CENTER_HIDE_EDIT) {
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "available"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}