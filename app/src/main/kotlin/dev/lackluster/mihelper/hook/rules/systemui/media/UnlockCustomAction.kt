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

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.ScaleDrawable
import android.media.session.PlaybackState.CustomAction
import android.view.Gravity
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object UnlockCustomAction : YukiBaseHooker() {
//    private val mHiddenCustomActionsList by lazy {
//        "com.android.systemui.statusbar.notification.NotificationSettingsManager\$Holder".toClass().field {
//            name = "INSTANCE"
//            modifiers { isStatic }
//        }.get().any()?.current()?.field {
//            name = "mHiddenCustomActionsList"
//        }?.list<String>() ?: listOf(
//            "com.netease.cloudmusic",
//            "com.netease.cloudmusic.lite",
//            "com.tencent.qqmusic",
//            "com.tencent.qqmusiclite",
//            "com.tencent.qqmusicpad",
//            "com.tencent.radio",
//            "com.apple.android.music",
//            "com.google.android.apps.youtube.music",
//            "com.spotify.music",
//        )
//    }
    private val skipPkgList by lazy {
        listOf(
            "com.miui.player",
        )
    }
    private val forceScalePkgList by lazy {
        listOf(
            "com.apple.android.music",
        )
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.MediaControl.UNLOCK_ACTION) {
            "com.android.systemui.media.controls.pipeline.MediaDataManager\$createActionsFromState\$customActions\$1".toClass().method {
                name = "invoke"
            }.hook {
                after {
                    val mediaAction = this.result ?: return@after
                    val pkgName = this.instance.current().field { name = "\$packageName" }.string()
//                    if (pkgName in mHiddenCustomActionsList) {
//                        val customAction = this.args(0).any() as? CustomAction? ?: return@after
//                        val mediaDataManager = this.instance.current().field { name = "this\$0" }.any() ?: return@after
//                        val context = mediaDataManager.current().field { name = "context" }.any() as Context
//                        mediaAction.current().field { name = "icon" }.set(
//                            Icon.createWithResource(pkgName, customAction.icon).loadDrawable(context)
//                        )
//                    }
                    if (pkgName in skipPkgList) {
                        return@after
                    }
                    val customAction = this.args(0).any() as? CustomAction? ?: return@after
                    val mediaDataManager = this.instance.current().field { name = "this\$0" }.any() ?: return@after
                    val context = mediaDataManager.current().field { name = "context" }.any() as Context
                    val drawable = Icon.createWithResource(pkgName, customAction.icon).loadDrawable(context) as Drawable
                    val maxSize = maxOf(drawable.intrinsicHeight, drawable.intrinsicWidth) / Resources.getSystem().displayMetrics.density
                    if (pkgName in forceScalePkgList || maxSize > 45) {
                        val scale = 1 - (24 / maxSize) * 44 / 40
                        YLog.info(scale.toString())
                        mediaAction.current().field { name = "icon" }.set(
                            ScaleDrawable(drawable, Gravity.CENTER, scale, scale).apply { level = 1 }
                        )
                    } else {
                        mediaAction.current().field { name = "icon" }.set(drawable)
                    }
                }
            }
        }
    }
}