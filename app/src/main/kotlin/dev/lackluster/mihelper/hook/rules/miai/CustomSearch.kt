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
import androidx.core.net.toUri
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomSearch : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val openBrowser by lazy {
        DexKit.findMethodWithCache("open_browser") {
            matcher {
                addUsingString("sendIntentInner fail because empty type", StringMatchType.Equals)
                returnType = "int"
            }
        }
    }
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

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.MiAi.SEARCH_USE_BROWSER.observe {
                updateSelfState(it)
            }
        )
        openBrowser
    }

    override fun onStart() {
        openBrowser?.getMethodInstance(classLoader)?.hook {
            val intent = getArg(0) as? Intent
            if (
                (getArg(1) as? String)== "activity" &&
                (getArg(3) as? String) == "com.android.browser" &&
                intent != null
            ) {
                val searchEngine = Preferences.MiAi.SEARCH_ENGINE.get()
                val searchEngineUrl = Preferences.MiAi.CUSTOM_SEARCH_URL.get()
                val queryString = queryStringRegex.find(intent.dataString.toString())?.groupValues?.get(1)
                if (queryString.isNullOrBlank()) {
                    return@hook result(proceed())
                }
                var searchUrl =
                    when (searchEngine) {
                        in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",queryString)
                        5 -> searchEngineUrl.replaceFirst("%s",queryString)
                        else -> ""
                    }
                val newIntent = Intent()
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (searchEngine == 0 || searchUrl.isBlank()) {
                    newIntent.action = Intent.ACTION_WEB_SEARCH
                    newIntent.putExtra("query", queryString)
                } else {
                    if (!searchUrl.startsWith("https://") && !searchUrl.startsWith("http://")) {
                        searchUrl = "https://$searchUrl"
                    }
                    newIntent.action = Intent.ACTION_VIEW
                    newIntent.data = searchUrl.toUri()
                }
                val intentUri = newIntent.toUri(Intent.URI_INTENT_SCHEME)
                val newArgs = args.toTypedArray()
                newArgs[0] = newIntent
                newArgs[2] = intentUri
                result(proceed(newArgs))
            } else {
                result(proceed())
            }
        }
    }
}