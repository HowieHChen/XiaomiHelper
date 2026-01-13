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

package dev.lackluster.mihelper.hook.rules.taplus

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object CustomSearch : YukiBaseHooker() {
    private val searchEngine = Prefs.getInt(Pref.Key.Taplus.SEARCH_ENGINE, 0)
    private val searchEngineUrl = Prefs.getString(Pref.Key.Taplus.SEARCH_URL, "")
    private val searchUrlValues = arrayOf(
        "",
        "https://www.baidu.com/s?wd=%s",
        "https://www.sogou.com/web?query=%s",
        "https://www.bing.com/search?q=%s",
        "https://www.google.com/search?q=%s",
    )
    override fun onHook() {
        hasEnable(Pref.Key.Taplus.SEARCH_USE_BROWSER) {
            "com.miui.contentextension.utils.AppsUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "openGlobalSearch"
            }?.hook {
                before {
                    val context = this.args(0).cast<Context>() ?: return@before
                    val queryString = this.args(1).cast<String>() ?: return@before
                    var searchUrl =
                        when (searchEngine) {
                            in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",queryString)
                            5 -> searchEngineUrl?.replaceFirst("%s",queryString)
                            else -> ""
                        }
                    val intent = Intent().apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (searchEngine == 0 || searchUrl.isNullOrBlank()) {
                        intent.action = Intent.ACTION_WEB_SEARCH
                        intent.putExtra("query", queryString)
                    } else {
                        if (!searchUrl.startsWith("https://") && !searchUrl.startsWith("http://")) {
                            searchUrl = "https://$searchUrl"
                        }
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(searchUrl)
                    }
                    context.startActivity(intent)
                    this.result = null
                }
            }
        }
    }
}