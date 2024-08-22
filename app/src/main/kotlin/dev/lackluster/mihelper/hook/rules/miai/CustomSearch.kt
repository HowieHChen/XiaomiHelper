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

package dev.lackluster.mihelper.hook.rules.miai

import android.content.Intent
import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomSearch : YukiBaseHooker() {
    private val openBrowser by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("sendIntentInner fail because empty type", StringMatchType.Equals)
                returnType = "int"
            }
        }.singleOrNull()
    }
    private val searchEngine = Prefs.getInt(Pref.Key.MiAi.SEARCH_ENGINE, 0)
    private val searchEngineUrl = Prefs.getString(Pref.Key.MiAi.SEARCH_URL, "")
    private val searchUrlValues = arrayOf(
        "",
        "https://www.baidu.com/s?wd=%s",
        "https://www.sogou.com/web?query=%s",
        "https://www.bing.com/search?q=%s",
        "https://www.google.com/search?q=%s",
    )
    private val queryStringRegex by lazy {
        Regex(pattern = "https://.*?&word=(.*?)&bd_ck=")
    }
    override fun onHook() {
        hasEnable(Pref.Key.MiAi.SEARCH_USE_BROWSER) {
            if (appClassLoader == null) return@hasEnable
            openBrowser?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    if (this.args(1).string() == "activity" && this.args(3).string() == "com.android.browser") {
                        val intent = this.args(0).any() as Intent
                        val queryString = queryStringRegex.find(intent.dataString.toString())?.groupValues?.get(1)
                        if (queryString.isNullOrBlank()) {
                            return@before
                        }
                        var searchUrl =
                            when (searchEngine) {
                                in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",queryString)
                                5 -> searchEngineUrl?.replaceFirst("%s",queryString)
                                else -> ""
                            }
                        val newIntent = Intent()
                        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        if (searchEngine == 0 || searchUrl.isNullOrBlank()) {
                            newIntent.action = Intent.ACTION_WEB_SEARCH
                            newIntent.putExtra("query", queryString)
                        }
                        else {
                            if (!searchUrl.startsWith("https://") && !searchUrl.startsWith("http://")) {
                                searchUrl = "https://$searchUrl"
                            }
                            newIntent.action = Intent.ACTION_VIEW
                            newIntent.data = Uri.parse(searchUrl)
                        }
                        val intentUri = newIntent.toUri(Intent.URI_INTENT_SCHEME)
                        this.args(0).set(newIntent)
                        this.args(2).set(intentUri)
                        // this.args(3).set("")
                    }
                }
            }
        }
    }
}