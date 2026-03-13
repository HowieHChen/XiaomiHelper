/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references WOMMO <https://github.com/YifePlayte/WOMMO/blob/5800d005303eafca50ff0d0f20b941dfcab322e0/app/src/main/java/com/yifeplayte/wommo/hook/hooks/singlepackage/aiengine/ChangeBrowserForAIEngine.kt>
 * Copyright (C) 2026 YifePlayte

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

package dev.lackluster.mihelper.hook.rules.aiengine

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object CopyWebsite : YukiBaseHooker() {
//    private const val SMART_PASS_WORD_XIAOMI_BROWSER = 11

    override fun onHook() {
        hasEnable(Pref.Key.AIEngine.COPY_LINK_CUSTOM_BROWSER) {
            "com.xiaomi.aicr.copydirect.util.SmartPasswordUtils".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "jumpToXiaoMiBrowser"
                }?.hook {
                    replaceUnit {
                        val context = this.args(0).cast<Context>() ?: return@replaceUnit
                        val url = this.args(1).cast<String>()?.let {
                            if (it.startsWith("http://") || it.startsWith("https://")) it
                            else "https://$it"
                        } ?: return@replaceUnit
                        Intent(Intent.ACTION_VIEW, url.toUri()).let { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}