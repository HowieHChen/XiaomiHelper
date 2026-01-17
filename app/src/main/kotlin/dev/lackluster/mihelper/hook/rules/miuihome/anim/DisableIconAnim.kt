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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisableIconAnim : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ANIM_FOLDER_ICON_DARKEN) {
            "com.miui.home.folder.PreViewTouchDelegate".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "onTouchDown"
                }?.hook {
                    intercept()
                }
            }
        }
        hasEnable(Pref.Key.MiuiHome.ANIM_FOLDER_ZOOM) {
            "com.miui.home.folder.FolderIcon".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "folmeDown"
                }?.hook {
                    intercept()
                }
            }
        }
        "com.miui.home.launcher.ShortcutIcon".toClassOrNull()?.apply {
            hasEnable(Pref.Key.MiuiHome.ANIM_ICON_DARKEN) {
                val fldEnableTouchMask = resolve().firstFieldOrNull {
                    name = "mEnableTouchMask"
                }
                resolve().firstConstructorOrNull {
                    parameterCount = 4
                }?.hook {
                    after {
                        fldEnableTouchMask?.copy()?.of(this.instance)?.set(false)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "enableDrawTouchMask"
                }?.hook {
                    intercept()
                }
            }
            hasEnable(Pref.Key.MiuiHome.ANIM_ICON_ZOOM) {
                resolve().firstMethodOrNull {
                    name = "folmeDown"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}