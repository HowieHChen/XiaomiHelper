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
import android.graphics.drawable.Icon
import android.media.session.PlaybackState.CustomAction
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
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
    private val allowScalePkgList by lazy {
        listOf(
            "com.miui.player"
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
                    if (pkgName in allowScalePkgList) {
                        return@after
                    }
                    val customAction = this.args(0).any() as? CustomAction? ?: return@after
                    val mediaDataManager = this.instance.current().field { name = "this\$0" }.any() ?: return@after
                    val context = mediaDataManager.current().field { name = "context" }.any() as Context
                    mediaAction.current().field { name = "icon" }.set(
                        Icon.createWithResource(pkgName, customAction.icon).loadDrawable(context)
                    )
                }
            }
        }
    }
}