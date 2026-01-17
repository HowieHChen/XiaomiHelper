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

package dev.lackluster.mihelper.hook.rules.personalassist

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ForceColorScheme : YukiBaseHooker() {
    private val forceColorSchemeMinus = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_MINUS, 0).convertToColorMode()

    override fun onHook() {
        if (forceColorSchemeMinus > -1) {
            "com.miui.personalassistant.utils.wallpaper.WallpaperUtils".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "setCurrentWallpaperColorMode"
                }?.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setCurrentStatusBarAreaColorMode"
                }?.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setCurrentWallPaperHeadColorMode"
                }?.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
                    }
                }
            }
        }
    }

    private fun Int.convertToColorMode(): Int {
        return when(this) {
            1 -> 0
            2 -> 2
            else -> -1
        }
    }
}