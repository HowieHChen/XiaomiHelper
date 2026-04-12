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

package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_170
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_200
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_EXTRA_SMALL
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_GODZILLA
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_HUGE
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_LARGE
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_MEDIUM
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_SMALL
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object FontScale : StaticHooker() {
    private val fontScaleSmall = Preferences.System.FONT_SCALE_SMALL.get()
    private val fontScaleMedium = Preferences.System.FONT_SCALE_MEDIUM.get()
    private val fontScaleLarge = Preferences.System.FONT_SCALE_LARGE.get()
    private val fontScaleHuge = Preferences.System.FONT_SCALE_HUGE.get()
    private val fontScaleGodzilla = Preferences.System.FONT_SCALE_GODZILLA.get()
    private val fontScale170 = Preferences.System.FONT_SCALE_170.get()
    private val fontScale200 = Preferences.System.FONT_SCALE_200.get()

    private val metGetFontScale by lazy {
        "android.content.res.MiuiConfiguration".toClass().resolve().firstMethodOrNull {
            name = "getFontScale"
        }
    }

    override fun onInit() {
        updateSelfState(Preferences.System.ENABLE_FONT_SCALE.get())
    }

    override fun onHook() {
        metGetFontScale?.hook {
            val ratio = when (getArg(0) as? Int) {
                UI_MODE_TYPE_SCALE_EXTRA_SMALL -> fontScaleSmall
                UI_MODE_TYPE_SCALE_SMALL -> fontScaleSmall
                UI_MODE_TYPE_SCALE_MEDIUM -> fontScaleMedium
                UI_MODE_TYPE_SCALE_LARGE -> fontScaleLarge
                UI_MODE_TYPE_SCALE_HUGE -> fontScaleHuge
                UI_MODE_TYPE_SCALE_GODZILLA -> fontScaleGodzilla
                UI_MODE_TYPE_SCALE_170 -> fontScale170
                UI_MODE_TYPE_SCALE_200 -> fontScale200
                else -> fontScaleMedium
            }
            result(ratio)
        }
    }
}