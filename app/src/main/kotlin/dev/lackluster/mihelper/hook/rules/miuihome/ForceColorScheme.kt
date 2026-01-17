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

package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ForceColorScheme : YukiBaseHooker() {
    private val forceColorSchemeStatusBar = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_STATUS_BAR, 0).convertToColorMode()
    private val forceColorSchemeTextIcon = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_TEXT_ICON, 0).convertToColorMode()
    private val clzWallpaperUtils by lazy {
        "com.miui.home.launcher.WallpaperUtils".toClassOrNull()
    }
    private val clzWallpaperUtil by lazy {
        "com.miui.home.isolate.wallpaper.WallpaperUtil".toClassOrNull()
    }

    override fun onHook() {
        if (forceColorSchemeStatusBar + forceColorSchemeTextIcon > -2) {
            if (forceColorSchemeTextIcon > -1) {
                clzWallpaperUtils?.apply {
                    resolve().firstMethodOrNull {
                        name = "setCurrentSearchBarAreaColorMode"
                    }?.hook {
                        before {
                            this.args(0).set(forceColorSchemeTextIcon)
                        }
                    }
                }
                clzWallpaperUtil?.apply {
                    resolve().firstMethodOrNull {
                        name = "setCurrentWallpaperColorMode"
                    }?.hook {
                        before {
                            this.args(0).set(forceColorSchemeTextIcon)
                        }
                    }
                }
            }
            if (forceColorSchemeStatusBar > -1) {
                clzWallpaperUtils?.apply {
                    resolve().firstMethodOrNull {
                        name = "setCurrentStatusBarAreaColorMode"
                    }?.hook {
                        before {
                            this.args(0).set(forceColorSchemeStatusBar)
                        }
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