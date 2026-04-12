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
import com.highcapable.kavaref.KavaRef.Companion.resolve
import androidx.core.net.toUri
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet

object CustomSearch : StaticHooker() {
    private val searchEngine by Preferences.Taplus.SEARCH_ENGINE.lazyGet()
    private val searchEngineUrl by Preferences.Taplus.CUSTOM_SEARCH_URL.lazyGet()
    private val searchUrlValues = arrayOf(
        "",
        "https://www.baidu.com/s?wd=%s",
        "https://www.sogou.com/web?query=%s",
        "https://www.bing.com/search?q=%s",
        "https://www.google.com/search?q=%s",
    )

    override fun onInit() {
        updateSelfState(Preferences.Taplus.SEARCH_USE_BROWSER.get())
    }

    override fun onHook() {
        "com.miui.contentextension.utils.AppsUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "openGlobalSearch"
        }?.hook {
            val context = getArg(0) as? Context
            val query = getArg(1) as? String
            if (context != null && query != null) {
                var searchUrl =
                    when (searchEngine) {
                        in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",query)
                        5 -> searchEngineUrl.replaceFirst("%s",query)
                        else -> ""
                    }
                val intent = Intent().apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (searchEngine == 0 || searchUrl.isBlank()) {
                    intent.action = Intent.ACTION_WEB_SEARCH
                    intent.putExtra("query", query)
                } else {
                    if (!searchUrl.startsWith("https://") && !searchUrl.startsWith("http://")) {
                        searchUrl = "https://$searchUrl"
                    }
                    intent.action = Intent.ACTION_VIEW
                    intent.data = searchUrl.toUri()
                }
                context.startActivity(intent)
                result(null)
            } else {
                result(proceed())
            }
        }
    }
}