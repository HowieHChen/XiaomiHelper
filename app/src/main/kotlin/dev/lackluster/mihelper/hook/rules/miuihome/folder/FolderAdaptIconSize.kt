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

package dev.lackluster.mihelper.hook.rules.miuihome.folder

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object FolderAdaptIconSize : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.FOLDER_ADAPT_SIZE) {
            "com.miui.home.launcher.folder.FolderIcon2x2".toClass().apply {
                method {
                    name = "createOrRemoveView"
                }.hook {
                    before {
                        val info = this.instance.current().field {
                            name = "mInfo"
                            superClass()
                        }.any() ?: return@before
                        val container = this.instance.current().method {
                            name = "getMPreviewContainer"
                            superClass()
                        }.call() ?: return@before
                        val infoCount = info.current().method {
                            name = "count"
                        }.int()
                        val mRealPvChildCount = container.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        if (infoCount == mRealPvChildCount) return@before
                        val num = Character.getNumericValue(container::class.java.simpleName.last())
                        if (mRealPvChildCount - num >= 3) {
                            return@before
                        }
                        container.current().method {
                            name = "setMItemsMaxCount"
                            superClass()
                        }.call(
                            if (infoCount <= num) num
                            else num + 3
                        )
                    }
                }
                method {
                    name = "addItemOnclickListener"
                }.hook {
                    before {
                        val container = this.instance.current().method {
                            name = "getMPreviewContainer"
                            superClass()
                        }.call() ?: return@before
                        val mRealPvChildCount = container.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        val num = Character.getNumericValue(container::class.java.simpleName.last())
                        this.instance.current().method {
                            name = "setMLargeIconNum"
                            superClass()
                        }.call(
                            if (mRealPvChildCount <= num) num
                            else num - 1
                        )
                    }
                }
            }
            "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_4".toClass().apply {
                method {
                    name = "preSetup2x2"
                }.hook {
                    before {
                        val mRealPvChildCount = this.instance.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        this.instance.current().method {
                            name = "setMLargeIconNum"
                            superClass()
                        }.call(
                            if (mRealPvChildCount <= 4) 4
                            else 3
                        )
                    }
                }
            }
            "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_9".toClass().apply {
                method {
                    name = "preSetup2x2"
                }.hook {
                    before {
                        val mRealPvChildCount = this.instance.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        this.instance.current().method {
                            name = "setMLargeIconNum"
                            superClass()
                        }.call(
                            if (mRealPvChildCount <= 9) 9
                            else 8
                        )
                    }
                }
            }
        }
    }
}