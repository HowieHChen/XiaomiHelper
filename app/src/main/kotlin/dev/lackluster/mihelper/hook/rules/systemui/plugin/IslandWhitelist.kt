/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.plugin

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object IslandWhitelist : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.ISLAND_WHITELIST) {
            "miui.systemui.notification.focus.SignatureChecker".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "checkSignatures"
                    parameters(String::class)
                }?.hook {
                    replaceToTrue()
                }
            }
            "miui.systemui.notification.NotificationSettingsManager".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "canShowFocus"
                }?.hook {
                    replaceToTrue()
                }
                resolve().firstMethodOrNull {
                    name = "canCustomFocus"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
        hasEnable(Pref.Key.SystemUI.Plugin.ISLAND_MEDIA_WHITELIST) {
            "miui.systemui.notification.NotificationSettingsManager".toClassOrNull()?.apply {
                resolve().optional(true).firstMethodOrNull {
                    name = "mediaIslandSupportMiniWindow"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
    }
}