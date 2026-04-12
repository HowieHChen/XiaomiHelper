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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object CopyWebsite : StaticHooker() {
    private const val SMART_PASS_WORD_XIAOMI_BROWSER = 11

    override fun onInit() {
        updateSelfState(Preferences.AiEngine.OPEN_LINK_WITH_CUSTOM_BROWSER.get())
    }

    override fun onHook() {
        "com.xiaomi.aicr.copydirect.util.SmartPasswordUtils".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "jumpToXiaoMiBrowser"
            }?.hook {
                val context = getArg(0) as? Context
                val url = getArg(1) as? String
                if (context != null && url != null) {
                    val wrappedUrl =
                        if (url.startsWith("http://") || url.startsWith("https://")) url
                        else "https://$url"
                    Intent(Intent.ACTION_VIEW, wrappedUrl.toUri()).let { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "isInstallForApp"
            }?.hook {
                val type = getArg(1) as? Int
                if (type == SMART_PASS_WORD_XIAOMI_BROWSER) {
                    result(true)
                } else {
                    result(proceed())
                }
            }
        }
    }
}