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

import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs

object FolderColumns : YukiBaseHooker() {
    private val value by lazy {
        Prefs.getInt(Pref.Key.MiuiHome.FOLDER_COLUMNS, 3)
    }
    private val defValue by lazy {
        if (Device.isPad) 4 else 3
    }
    private val noPadding by lazy {
        Prefs.getBoolean(Pref.Key.MiuiHome.FOLDER_NO_PADDING, false)
    }
    override fun onHook() {
        if (value == defValue && noPadding) {
            "com.miui.home.launcher.Folder".toClass().method {
                name = "bind"
            }.giveAll().hookAll {
                after {
                    val mContent = this.instance.current().field {
                        name = "mContent"
                    }.any() as GridView
                    mContent.setPadding(0, 0, 0, 0)
                    val layoutParams = mContent.layoutParams
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    mContent.layoutParams = layoutParams
                }
            }
        }
        else if (value != defValue) {
            "com.miui.home.launcher.Folder".toClass().method {
                name = "bind"
            }.giveAll().hookAll {
                after {
                    val columns: Int = value
                    val mContent = this.instance.current().field {
                        name = "mContent"
                    }.any() as GridView
                    mContent.numColumns = columns
                    if (noPadding) {
                        mContent.setPadding(0, 0, 0, 0)
                        val layoutParams = mContent.layoutParams
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        mContent.layoutParams = layoutParams
                    }
                    if (columns > defValue) {
                        val mBackgroundView = this.instance.current().field {
                            name = "mBackgroundView"
                        }.any() as LinearLayout
                        mBackgroundView.setPadding(
                            mBackgroundView.paddingLeft / 3,
                            mBackgroundView.paddingTop,
                            mBackgroundView.paddingRight / 3,
                            mBackgroundView.paddingBottom
                        )
                    }
                }
            }
        }
    }
}