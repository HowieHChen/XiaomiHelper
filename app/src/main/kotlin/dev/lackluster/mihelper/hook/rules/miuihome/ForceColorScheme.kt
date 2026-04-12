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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object ForceColorScheme : StaticHooker() {
    private val forceColorSchemeStatusBar by lazy {
        Preferences.MiuiHome.FORCE_COLOR_STATUS_BAR.get().convertToColorMode()
    }
    private val forceColorSchemeTextIcon by lazy {
        Preferences.MiuiHome.FORCE_COLOR_TEXT_ICON.get().convertToColorMode()
    }
    private val clzWallpaperUtils by "com.miui.home.launcher.WallpaperUtils".lazyClassOrNull()
    private val clzWallpaperUtil by "com.miui.home.isolate.wallpaper.WallpaperUtil".lazyClassOrNull()

    private const val FG_DEFAULT = -1
    private const val FG_LIGHT = 0
    private const val FG_DARK = 2

    override fun onInit() {
        updateSelfState(forceColorSchemeTextIcon > -1 || forceColorSchemeStatusBar > -1)
    }

    override fun onHook() {
        if (forceColorSchemeTextIcon > -1) {
            clzWallpaperUtils?.apply {
                resolve().firstMethodOrNull {
                    name = "setCurrentSearchBarAreaColorMode"
                }?.hook {
                    val newArgs = args.toTypedArray()
                    newArgs[0] = forceColorSchemeTextIcon
                    result(proceed(newArgs))
                }
            }
            clzWallpaperUtil?.apply {
                resolve().firstMethodOrNull {
                    name = "setCurrentWallpaperColorMode"
                }?.hook {
                    val newArgs = args.toTypedArray()
                    newArgs[0] = forceColorSchemeTextIcon
                    result(proceed(newArgs))
                }
            }
        }
        if (forceColorSchemeStatusBar > -1) {
            clzWallpaperUtils?.apply {
                resolve().firstMethodOrNull {
                    name = "hasLightBgForStatusBar"
                }?.hook {
                    when (forceColorSchemeStatusBar) {
                        FG_LIGHT -> result(false)
                        FG_DARK -> result(true)
                        else -> result(proceed())
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setCurrentStatusBarAreaColorMode"
                }?.hook {
                    val newArgs = args.toTypedArray()
                    newArgs[0] = forceColorSchemeStatusBar
                    result(proceed(newArgs))
                }
            }
        }
    }

    private fun Int.convertToColorMode(): Int {
        return when(this) {
            1 -> FG_LIGHT
            2 -> FG_DARK
            else -> FG_DEFAULT
        }
    }
}