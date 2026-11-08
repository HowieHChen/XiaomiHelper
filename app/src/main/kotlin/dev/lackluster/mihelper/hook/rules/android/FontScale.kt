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
import dev.lackluster.mihelper.data.model.FontScaleConfig
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object FontScale : StaticHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    @Volatile
    private var scaleConfig: FontScaleConfig? = null

    private val metGetFontScale by lazy {
        "android.content.res.MiuiConfiguration".toClass().resolve().firstMethodOrNull {
            name = "getFontScale"
        }
    }

    private fun onScalePreferenceChanged() = synchronized(session.lock) {
        if (!isActive) return@synchronized
        updateScaleConfig()
    }

    private fun updateScaleConfig() {
        scaleConfig = FontScaleConfig(
            small = Preferences.System.FONT_SCALE_SMALL.get(),
            medium = Preferences.System.FONT_SCALE_MEDIUM.get(),
            large = Preferences.System.FONT_SCALE_LARGE.get(),
            huge = Preferences.System.FONT_SCALE_HUGE.get(),
            godzilla = Preferences.System.FONT_SCALE_GODZILLA.get(),
            size170 = Preferences.System.FONT_SCALE_170.get(),
            size200 = Preferences.System.FONT_SCALE_200.get()
        )
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.System.ENABLE_FONT_SCALE.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
        registerStopCleanup(
            Preferences.System.FONT_SCALE_SMALL.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_MEDIUM.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_LARGE.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_HUGE.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_GODZILLA.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_170.observe(false) {
                onScalePreferenceChanged()
            }
        )
        registerStopCleanup(
            Preferences.System.FONT_SCALE_200.observe(false) {
                onScalePreferenceChanged()
            }
        )
        updateScaleConfig()
        metGetFontScale?.hook {
            val config = scaleConfig ?: return@hook result(proceed())
            val ratio = when (getArg(0) as? Int) {
                UI_MODE_TYPE_SCALE_EXTRA_SMALL -> config.small
                UI_MODE_TYPE_SCALE_SMALL -> config.small
                UI_MODE_TYPE_SCALE_MEDIUM -> config.medium
                UI_MODE_TYPE_SCALE_LARGE -> config.large
                UI_MODE_TYPE_SCALE_HUGE -> config.huge
                UI_MODE_TYPE_SCALE_GODZILLA -> config.godzilla
                UI_MODE_TYPE_SCALE_170 -> config.size170
                UI_MODE_TYPE_SCALE_200 -> config.size200
                else -> config.medium
            }
            result(ratio)
        }
    }
}