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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object FontScale : StaticHooker() {
    private val fontScaleSmall by Preferences.System.FONT_SCALE_SMALL.lazyGet()
    private val fontScaleMedium by Preferences.System.FONT_SCALE_MEDIUM.lazyGet()
    private val fontScaleLarge by Preferences.System.FONT_SCALE_LARGE.lazyGet()
    private val fontScaleHuge by Preferences.System.FONT_SCALE_HUGE.lazyGet()
    private val fontScaleGodzilla by Preferences.System.FONT_SCALE_GODZILLA.lazyGet()
    private val fontScale170 by Preferences.System.FONT_SCALE_170.lazyGet()
    private val fontScale200 by Preferences.System.FONT_SCALE_200.lazyGet()

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
        }
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

    override fun onInit() {
        updateSelfState(Preferences.System.ENABLE_FONT_SCALE.get())
    }
    
    override fun onHook() {
        fldFontScale?.set(
            floatArrayOf(
                fontScaleSmall,
                fontScaleMedium,
                fontScaleLarge,
                fontScaleHuge,
                fontScaleGodzilla,
                fontScale170,
                fontScale200
            )
        )
        fldUiModeFontScaleMapping?.set(
            mutableMapOf(
                UI_MODE_TYPE_SCALE_EXTRA_SMALL to fontScaleSmall,
                UI_MODE_TYPE_SCALE_SMALL to fontScaleSmall,
                UI_MODE_TYPE_SCALE_MEDIUM to fontScaleMedium,
                UI_MODE_TYPE_SCALE_LARGE to fontScaleLarge,
                UI_MODE_TYPE_SCALE_HUGE to fontScaleHuge,
                UI_MODE_TYPE_SCALE_GODZILLA to fontScaleGodzilla,
                UI_MODE_TYPE_SCALE_170 to fontScale170,
                UI_MODE_TYPE_SCALE_200 to fontScale200,
            )
        )
        fldUiModeMapping?.set(
            mutableMapOf(
                fontScaleSmall to UI_MODE_TYPE_SCALE_SMALL,
                fontScaleMedium to UI_MODE_TYPE_SCALE_MEDIUM,
                fontScaleLarge to UI_MODE_TYPE_SCALE_LARGE,
                fontScaleHuge to UI_MODE_TYPE_SCALE_HUGE,
                fontScaleGodzilla to UI_MODE_TYPE_SCALE_GODZILLA,
                fontScale170 to UI_MODE_TYPE_SCALE_170,
                fontScale200 to UI_MODE_TYPE_SCALE_200,
            )
        )

        fldPageLayoutMapping?.set(
            mutableMapOf(
                0 to fontScaleSmall,
                1 to fontScaleMedium,
                2 to fontScaleLarge,
                3 to fontScaleHuge,
                4 to fontScaleGodzilla,
                5 to fontScale170,
                6 to fontScale200,
            )
        )
        metGetProgress?.hook {
            val mCurrentFontScale = fldCurrentFontScale?.get(thisObject)
            val index = when (mCurrentFontScale) {
                fontScaleSmall -> 0
                fontScaleMedium -> 1
                fontScaleLarge -> 2
                fontScaleHuge -> 3
                fontScaleGodzilla -> 4
                fontScale170 -> 5
                fontScale200 -> 6
                else -> -1
            }
            if (index != -1) {
                result(index)
            } else {
                result(proceed())
            }
        }
    }
}