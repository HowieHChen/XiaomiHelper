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

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.accessibility_recent_task_memory_info
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_info1
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_info2
import dev.lackluster.mihelper.utils.factory.hasEnable

object ShowRealMemory : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_SHOW_REAL_MEMORY) {
            fun Long.formatSize(context: Context): String = Formatter.formatFileSize(context, this)

            "com.miui.home.recents.views.RecentsContainer".toClassOrNull()?.apply {
                val fldClearAnimView = resolve().firstFieldOrNull {
                    name = "mClearAnimView"
                }
                val fldTxtMemoryInfo1 = resolve().firstFieldOrNull {
                    name = "mTxtMemoryInfo1"
                }
                val fldTxtMemoryInfo2 = resolve().firstFieldOrNull {
                    name = "mTxtMemoryInfo2"
                }
                val fldSeparatorForMemoryInfo = resolve().firstFieldOrNull {
                    name = "mSeparatorForMemoryInfo"
                }
                resolve().firstMethodOrNull {
                    name = "refreshMemoryInfo"
                }?.hook {
                    before {
                        val txtMemoryInfo1 = fldTxtMemoryInfo1?.copy()?.of(this.instance)?.get<TextView>()
                        val txtMemoryInfo2 = fldTxtMemoryInfo2?.copy()?.of(this.instance)?.get<TextView>()
                        val context = this.instance<View>().context
                        val memoryInfo = ActivityManager.MemoryInfo()
                        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        activityManager.getMemoryInfo(memoryInfo)
                        val totalMem = memoryInfo.totalMem.formatSize(context)
                        val availMem = memoryInfo.availMem.formatSize(context)
                        txtMemoryInfo1?.text = context.getString(status_bar_recent_memory_info1, availMem, totalMem)
                        txtMemoryInfo2?.text = context.getString(status_bar_recent_memory_info2, availMem, totalMem)
                        fldClearAnimView?.copy()?.of(this.instance)?.get<View>()?.let { clearAnimView ->
                            clearAnimView.contentDescription = context.getString(accessibility_recent_task_memory_info, availMem, totalMem)
                        }
                        fldSeparatorForMemoryInfo?.copy()?.of(this.instance)?.get<View>()?.let { separatorForMemoryInfo ->
                            separatorForMemoryInfo.visibility =
                                if (TextUtils.isEmpty(txtMemoryInfo1?.text) || TextUtils.isEmpty(txtMemoryInfo2?.text)) View.GONE else View.VISIBLE
                        }
                        this.result = null
                    }
                }
            }
        }
    }
}