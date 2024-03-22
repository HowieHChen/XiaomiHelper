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

package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object ShowRealMemory : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_SHOW_REAL_MEMORY) {
            lateinit var context: Context
            var memoryInfo1StringId: Int? = null
            var memoryInfo2StringId: Int? = null
            fun Any.formatSize(): String = Formatter.formatFileSize(context, this as Long)
            val recentContainerClass =
                if (Device.isPad) "com.miui.home.recents.views.RecentsDecorations"
                else "com.miui.home.recents.views.RecentsContainer"
            recentContainerClass.toClass().constructor {
                paramCount = 2
            }.hook {
                after {
                    context = this.args(0).any() as Context
                    memoryInfo1StringId = context.resources.getIdentifier(
                        "status_bar_recent_memory_info1",
                        "string",
                        "com.miui.home"
                    )
                    memoryInfo2StringId = context.resources.getIdentifier(
                        "status_bar_recent_memory_info2",
                        "string",
                        "com.miui.home"
                    )
                }
            }
            recentContainerClass.toClass().method {
                name = "refreshMemoryInfo"
            }.hook {
                before {
                    this.result = null
                    val memoryInfo = ActivityManager.MemoryInfo()
                    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.getMemoryInfo(memoryInfo)
                    val totalMem = memoryInfo.totalMem.formatSize()
                    val availMem = memoryInfo.availMem.formatSize()
                    this.instance.current().field {
                        name = "mTxtMemoryInfo1"
                    }.cast<TextView>()?.text = context.getString(memoryInfo1StringId!!, availMem, totalMem)
                    this.instance.current().field {
                        name = "mTxtMemoryInfo2"
                    }.cast<TextView>()?.text = context.getString(memoryInfo2StringId!!, availMem, totalMem)
                }
            }
        }
    }
}