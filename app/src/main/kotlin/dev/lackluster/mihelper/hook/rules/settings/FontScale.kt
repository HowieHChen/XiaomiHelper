/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
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
import dev.lackluster.mihelper.hook.utils.toTyped

object FontScale : StaticHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true
    
    @Volatile
    private var scaleConfig: FontScaleConfig? = null
    
    private val clzLargeFontUtils by "com.android.settings.display.LargeFontUtils".lazyClassOrNull()
    private val fldFontScale by lazy {
        clzLargeFontUtils?.resolve()?.firstFieldOrNull {
            name = "FONT_SCALE"
            modifiers(Modifiers.STATIC)
        }
    }
    private val fldUiModeFontScaleMapping by lazy {
        clzLargeFontUtils?.resolve()?.firstFieldOrNull {
            name = "UI_MODE_FONT_SCALE_MAPPING"
            modifiers(Modifiers.STATIC)
        }
    }
    private val fldUiModeMapping by lazy {
        clzLargeFontUtils?.resolve()?.firstFieldOrNull {
            name = "sUI_MODE_MAPPING"
            modifiers(Modifiers.STATIC)
        }
    }
    private val clzPageLayoutFragment by "com.android.settings.display.PageLayoutFragment".lazyClassOrNull()
    private val fldPageLayoutMapping by lazy {
        clzPageLayoutFragment?.resolve()?.firstFieldOrNull {
            name = "PAGE_LAYOUT_MAPPING"
            modifiers(Modifiers.STATIC)
        }?.toTyped<MutableMap<Int, Float>>()
    }
    private val fldCurrentFontScale by lazy {
        clzPageLayoutFragment?.resolve()?.firstFieldOrNull {
            name = "mCurrentFontScale"
        }?.toTyped<Float>()
    }
    private val metGetProgress by lazy {
        clzPageLayoutFragment?.resolve()?.firstMethodOrNull {
            name = "getProgress"
        }
    }
    
    private val oriFontScale by lazy { 
        fldFontScale?.get()
    }
    private val oriUiModeFontScaleMapping by lazy { 
        (fldUiModeFontScaleMapping?.get() as? Map<*, *>)?.toMutableMap()
    }
    private val oriUiModeMapping by lazy { 
        (fldUiModeMapping?.get() as? Map<*, *>)?.toMutableMap()
    }
    private val oriPageLayoutMapping by lazy {
        fldPageLayoutMapping?.get(null)?.toMutableMap()
    }


    override fun onInit() {
        registerDisposeCleanup(
            Preferences.System.ENABLE_FONT_SCALE.observe {
                updateSelfState(it)
            }
        )
    }

    private fun onScalePreferenceChanged() = synchronized(session.lock) {
        if (!isActive) return@synchronized

        updateScaleConfig()

        if (isRunning) {
            applyScaleConfig()
        }
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

    private fun applyScaleConfig() {
        val config = scaleConfig ?: return
        fldFontScale?.set(
            floatArrayOf(
                config.small,
                config.medium,
                config.large,
                config.huge,
                config.godzilla,
                config.size170,
                config.size200
            )
        )
        fldUiModeFontScaleMapping?.set(
            mutableMapOf(
                UI_MODE_TYPE_SCALE_EXTRA_SMALL to config.small,
                UI_MODE_TYPE_SCALE_SMALL to config.small,
                UI_MODE_TYPE_SCALE_MEDIUM to config.medium,
                UI_MODE_TYPE_SCALE_LARGE to config.large,
                UI_MODE_TYPE_SCALE_HUGE to config.huge,
                UI_MODE_TYPE_SCALE_GODZILLA to config.godzilla,
                UI_MODE_TYPE_SCALE_170 to config.size170,
                UI_MODE_TYPE_SCALE_200 to config.size200,
            )
        )
        fldUiModeMapping?.set(
            mutableMapOf(
                config.small to UI_MODE_TYPE_SCALE_SMALL,
                config.medium to UI_MODE_TYPE_SCALE_MEDIUM,
                config.large to UI_MODE_TYPE_SCALE_LARGE,
                config.huge to UI_MODE_TYPE_SCALE_HUGE,
                config.godzilla to UI_MODE_TYPE_SCALE_GODZILLA,
                config.size170 to UI_MODE_TYPE_SCALE_170,
                config.size200 to UI_MODE_TYPE_SCALE_200,
            )
        )
        fldPageLayoutMapping?.get(null)?.apply {
            clear()
            putAll(
                mapOf(
                    0 to config.small,
                    1 to config.medium,
                    2 to config.large,
                    3 to config.huge,
                    4 to config.godzilla,
                    5 to config.size170,
                    6 to config.size200,
                )
            )
        }
    }

    override fun onStart() {
        oriFontScale
        oriUiModeFontScaleMapping
        oriUiModeMapping
        oriPageLayoutMapping
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
        applyScaleConfig()
        metGetProgress?.hook {
            val mCurrentFontScale = fldCurrentFontScale?.get(thisObject)
            val config = scaleConfig ?: return@hook result(proceed())
            val index = when (mCurrentFontScale) {
                config.small -> 0
                config.medium -> 1
                config.large -> 2
                config.huge -> 3
                config.godzilla -> 4
                config.size170 -> 5
                config.size200 -> 6
                else -> -1
            }
            if (index != -1) {
                result(index)
            } else {
                result(proceed())
            }
        }
    }

    override fun onStop() {
        oriFontScale?.let { fldFontScale?.set(it) }
        oriUiModeFontScaleMapping?.let { fldUiModeFontScaleMapping?.set(it) }
        oriUiModeMapping?.let { fldUiModeMapping?.set(it) }
        oriPageLayoutMapping?.let { fldPageLayoutMapping?.set(null, it.toMutableMap()) }
    }
}