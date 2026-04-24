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

package dev.lackluster.mihelper.hook.rules.miuihome.anim

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped

object DisableIconAnim : StaticHooker() {
    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        Preferences.MiuiHome.DISABLE_FOLDER_DARKEN_ANIM.ifTrue {
            "com.miui.home.folder.PreViewTouchDelegate".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "onTouchDown"
                }?.hook {
                    result(null)
                }
            }
        }
        Preferences.MiuiHome.DISABLE_FOLDER_ZOOM_ANIM.ifTrue {
            "com.miui.home.folder.FolderIcon".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "folmeDown"
                }?.hook {
                    result(null)
                }
            }
        }
        "com.miui.home.launcher.ShortcutIcon".toClassOrNull()?.apply {
            Preferences.MiuiHome.DISABLE_ICON_DARKEN_ANIM.ifTrue {
                val fldEnableTouchMask = resolve().firstFieldOrNull {
                    name = "mEnableTouchMask"
                }?.toTyped<Boolean>()
                resolve().firstConstructorOrNull {
                    parameterCount = 4
                }?.hook {
                    val ori = proceed()
                    fldEnableTouchMask?.set(thisObject, false)
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "enableDrawTouchMask"
                }?.hook {
                    result(null)
                }
            }
            Preferences.MiuiHome.DISABLE_ICON_ZOOM_ANIM.ifTrue {
                resolve().firstMethodOrNull {
                    name = "folmeDown"
                }?.hook {
                    result(null)
                }
            }
        }
    }
}