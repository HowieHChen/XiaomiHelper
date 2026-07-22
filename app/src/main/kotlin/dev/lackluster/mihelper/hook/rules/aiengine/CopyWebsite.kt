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

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.graphics.drawable.toBitmap
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType
import com.highcapable.kavaref.extension.classOf

object CopyWebsite : StaticHooker() {
    private const val COPY_WEBSITE_TYPE = 11
    private const val COPY_DIRECT_NOTIFICATION_ID = 111

    private val websiteIntent by lazy {
        DexKit.findMethodWithCache("aiengine_website_intent") {
            matcher {
                returnType = "android.content.Intent"
                paramCount = 1
                paramTypes("java.lang.String")
                addUsingString("clipboard_open", StringMatchType.Equals)
                addUsingString(Scope.BROWSER, StringMatchType.Equals)
            }
        }
    }
    private val websiteIntentOld by lazy {
        DexKit.findMethodWithCache("aiengine_website_intent_old") {
            matcher {
                returnType = "void"
                paramCount = 2
                addUsingString("clipboard_open", StringMatchType.Equals)
                addUsingString(Scope.BROWSER, StringMatchType.Equals)
            }
        }
    }

    private val browserInstallCheck by lazy {
        DexKit.findMethodsWithCache("aiengine_browser_install_check") {
            matcher {
                returnType = "boolean"
                paramCount = 2
                paramTypes("android.content.Context", "java.lang.String")
                addUsingString("isInstallForApp:", StringMatchType.StartsWith)
            }
        }
    }
    private val websiteNotification by lazy {
        DexKit.findMethodWithCache("aiengine_website_notification") {
            matcher {
                returnType = "void"
                paramCount = 7
                paramTypes(
                    "android.content.Context", "java.lang.String", "int", "long", "long",
                    "java.lang.String", "java.lang.String"
                )
                addUsingString("phrase_channel_id", StringMatchType.Equals)
                addUsingString("CopyDirect NotificationUtils", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.AiEngine.OPEN_LINK_WITH_CUSTOM_BROWSER.get().also {
            updateSelfState(it)
        }.ifTrue {
            websiteIntent
            websiteIntentOld
            browserInstallCheck
            websiteNotification
        }
    }

    override fun onHook() {
        websiteIntent?.getMethodInstance(classLoader)?.hook {
            val url = getArg(0) as? String ?: return@hook result(proceed())
            val normalizedUrl =
                if (url.startsWith("http://") || url.startsWith("https://")) url
                else "https://$url"
            result(
                Intent(Intent.ACTION_VIEW, normalizedUrl.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("open_source", "clipboard_open")
                }
            )
        }
        websiteIntentOld?.getMethodInstance(classLoader)?.hook {
            val activity = getArg(0) as? Context
            val url = getArg(1) as? String
            if (activity == null || url.isNullOrBlank()) {
                return@hook result(proceed())
            }
            val normalizedUrl =
                if (url.startsWith("http://") || url.startsWith("https://")) url
                else "https://$url"
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, normalizedUrl.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("open_source", "clipboard_open")
                }
            )
            result(null)
        }
        browserInstallCheck.map {
            it.getMethodInstance(classLoader)
        }.hookAll {
            if (getArg(1) == Scope.BROWSER) {
                result(true)
            } else {
                result(proceed())
            }
        }
        websiteNotification?.getMethodInstance(classLoader)?.hook {
            val originalResult = proceed()
            if ((getArg(2) as? Int) == COPY_WEBSITE_TYPE) {
                (getArg(0) as? Context)?.let(::refreshWebsiteNotificationIcon)
            }
            result(originalResult)
        }
    }

    @SuppressLint("NotificationPermission")
    private fun refreshWebsiteNotificationIcon(context: Context) {
        val notificationManager = context.getSystemService(classOf<NotificationManager>()) ?: return
        val notification = notificationManager.activeNotifications.firstOrNull {
            it.id == COPY_DIRECT_NOTIFICATION_ID && it.packageName == context.packageName
        }?.notification ?: return
        val icon = browserIcon(context) ?: return

        val focusPictures = notification.extras.getBundle("miui.focus.pics") ?: Bundle()
        focusPictures.putParcelable("miui.focus.pic_image", icon)
        focusPictures.putParcelable("miui.land.pic_image", icon)
        notification.extras.putBundle("miui.focus.pics", focusPictures)
        notification.extras.putParcelable("miui.appIcon", icon)
        notificationManager.notify(COPY_DIRECT_NOTIFICATION_ID, notification)
    }

    private fun browserIcon(context: Context): Icon? {
        val packageManager = context.packageManager
        val candidates = listOfNotNull(defaultBrowserPackage(context), Scope.BROWSER).distinct()
        for (packageName in candidates) {
            val icon = runCatching { packageManager.getApplicationIcon(packageName) }.getOrNull() ?: continue
            val bitmap = runCatching {
                icon.toBitmap(128, 128, Bitmap.Config.ARGB_8888)
            }.getOrNull() ?: continue
            return Icon.createWithBitmap(bitmap)
        }
        return null
    }

    private fun defaultBrowserPackage(context: Context): String? {
        return context.packageManager.resolveActivity(
            Intent(Intent.ACTION_VIEW, "https://example.com".toUri()),
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName?.takeUnless { it == "android" }
    }
}