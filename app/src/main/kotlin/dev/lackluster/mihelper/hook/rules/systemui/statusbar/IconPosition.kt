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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.utils.Prefs

object IconPosition : YukiBaseHooker() {
    private val mode = Prefs.getInt(IconTuner.ICON_POSITION, 0)
    private val slotsCustom by lazy {
        (Prefs.getStringSet(IconTuner.ICON_POSITION_VAL, mutableSetOf()).mapNotNull { str ->
            str.split(":").takeIf { it.size == 2 }
        }.sortedBy {
            it[0].toInt()
        }.map { it[1] }.takeIf {
            it.isNotEmpty()
        } ?: Constants.STATUS_BAR_ICONS_DEFAULT).toTypedArray()
    }

    override fun onHook() {
        if (mode == 0) return
        "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()?.apply {
            resolve().firstConstructorOrNull {
                parameters(Array<String>::class)
            }?.hook {
                before {
                    if (mode == 1) {
                        this.args(0).set(Constants.STATUS_BAR_ICONS_SWAP.toTypedArray())
                    } else if (mode == 2) {
                        this.args(0).set(slotsCustom)
                    }
                }
            }
        }
    }
}