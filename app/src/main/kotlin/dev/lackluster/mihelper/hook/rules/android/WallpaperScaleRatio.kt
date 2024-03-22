/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object WallpaperScaleRatio : YukiBaseHooker() {
    private const val DEFAULT_RATIO = 1.2f
    private val value by lazy {
        Prefs.getFloat(Pref.Key.Android.WALLPAPER_SCALE_RATIO, DEFAULT_RATIO)
    }
    override fun onHook() {
        if (value != DEFAULT_RATIO) {
            "com.android.server.wm.WallpaperController".toClass().constructor().hookAll {
                after {
                    this.instance.current().field {
                        name = "mMaxWallpaperScale"
                    }.set(value)
                }
            }
        }
    }
}