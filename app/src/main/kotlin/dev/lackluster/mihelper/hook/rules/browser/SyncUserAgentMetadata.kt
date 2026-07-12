/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com
 */

package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object SyncUserAgentMetadata : StaticHooker() {
    private const val MOBILE_METADATA_KEY = "MOBILE"

    override fun onInit() {
        updateSelfState(Preferences.Browser.SYNC_USER_AGENT_METADATA.get())
    }

    override fun onHook() {
        "org.chromium.android_webview.AwSettings".toClassOrNull()?.apply {
            val setUserAgentMetadata = resolve().firstMethodOrNull {
                name = "setUserAgentMetadataFromMap"
                parameters(Map::class)
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "setUserAgentString"
                parameters(String::class)
            }?.hook {
                val userAgent = getArg(0) as? String
                val originalResult = proceed()
                val metadataOverrides = when {
                    userAgent?.contains("Linux x86_64") == true -> {
                        mapOf(MOBILE_METADATA_KEY to false)
                    }
                    userAgent?.contains("Android") == true && userAgent.contains("Mobile") -> {
                        emptyMap()
                    }
                    else -> null
                }
                if (metadataOverrides != null) {
                    setUserAgentMetadata?.invoke(thisObject, metadataOverrides)
                }
                result(originalResult)
            }
        }
    }
}
