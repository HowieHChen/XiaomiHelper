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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.accessibility_recent_task_memory_info
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_info1
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_info2
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object ShowRealMemory : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.SHOW_RECENT_REAL_MEMORY.get())
    }

    override fun onHook() {
        fun Long.formatSize(context: Context): String = Formatter.formatFileSize(context, this)

        "com.miui.home.recents.views.RecentsContainer".toClassOrNull()?.apply {
            val fldClearAnimView = resolve().firstFieldOrNull {
                name = "mClearAnimView"
            }?.toTyped<View>()
            val fldTxtMemoryInfo1 = resolve().firstFieldOrNull {
                name = "mTxtMemoryInfo1"
            }?.toTyped<TextView>()
            val fldTxtMemoryInfo2 = resolve().firstFieldOrNull {
                name = "mTxtMemoryInfo2"
            }?.toTyped<TextView>()
            val fldSeparatorForMemoryInfo = resolve().firstFieldOrNull {
                name = "mSeparatorForMemoryInfo"
            }?.toTyped<View>()
            resolve().firstMethodOrNull {
                name = "refreshMemoryInfo"
            }?.hook {
                val txtMemoryInfo1 = fldTxtMemoryInfo1?.get(thisObject)
                val txtMemoryInfo2 = fldTxtMemoryInfo2?.get(thisObject)
                val context = (thisObject as? View)?.context
                if (context != null) {
                    val memoryInfo = ActivityManager.MemoryInfo()
                    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.getMemoryInfo(memoryInfo)
                    val totalMem = memoryInfo.totalMem.formatSize(context)
                    val availMem = memoryInfo.availMem.formatSize(context)
                    txtMemoryInfo1?.text = context.getString(status_bar_recent_memory_info1, availMem, totalMem)
                    txtMemoryInfo2?.text = context.getString(status_bar_recent_memory_info2, availMem, totalMem)
                    fldClearAnimView?.get(thisObject)?.let { clearAnimView ->
                        clearAnimView.contentDescription = context.getString(accessibility_recent_task_memory_info, availMem, totalMem)
                    }
                    fldSeparatorForMemoryInfo?.get(thisObject)?.let { separatorForMemoryInfo ->
                        separatorForMemoryInfo.visibility =
                            if (TextUtils.isEmpty(txtMemoryInfo1?.text) || TextUtils.isEmpty(txtMemoryInfo2?.text)) View.GONE else View.VISIBLE
                    }
                }
                result(null)
            }
        }
    }
}