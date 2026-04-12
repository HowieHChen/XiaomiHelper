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

import android.annotation.SuppressLint
import android.util.TypedValue
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaNotificationControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object CustomElement : StaticHooker() {
    private val ncAlbum by Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.get(false).lazyGet()
    private val ncAlbumShadow by Preferences.SystemUI.MediaControl.NotifCenter.ELM_ALBUM_SHADOW.lazyGet()
    private val albumFlip by Preferences.SystemUI.MediaControl.Shared.ELM_ALBUM_FLIP.lazyGet()

    private val ncModifyTextSize by Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.get(false).lazyGet()
    private val ncTitleSize by Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.get(false).lazyGet()
    private val ncArtistSize by Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.get(false).lazyGet()
    private val ncTimeSize by Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.get(false).lazyGet()

    private val diModifyTextSize by Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.get(true).lazyGet()
    private val diTitleSize by Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.get(true).lazyGet()
    private val diArtistSize by Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.get(true).lazyGet()
    private val diTimeSize by Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.get(true).lazyGet()

    private val ncTitleText by lazy {
        getMediaViewHolderField("titleText", false)?.toTyped<TextView>()
    }
    private val ncArtistText by lazy {
        getMediaViewHolderField("artistText", false)?.toTyped<TextView>()
    }
    private val ncElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", false)?.toTyped<TextView>()
    }
    private val ncTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", false)?.toTyped<TextView>()
    }
    private val diTitleText by lazy {
        getMediaViewHolderField("titleText", true)?.toTyped<TextView>()
    }
    private val diArtistText by lazy {
        getMediaViewHolderField("artistText", true)?.toTyped<TextView>()
    }
    private val diElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", true)?.toTyped<TextView>()
    }
    private val diTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", true)?.toTyped<TextView>()
    }

    override fun onInit() {
        updateSelfState(true)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        if (!ncAlbumShadow && ncAlbum != 2) {
            "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "applyViewShadowForMediaAlbum"
                }?.hook {
                    result(null)
                }
            }
        }
        if (!albumFlip) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaAlbumAnimationUtils".toClassOrNull()?.apply {
                val metOnFlip =
                    $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaAlbumAnimationUtils$OnFlipListener".toClassOrNull()
                        ?.resolve()?.firstMethodOrNull {
                            name = "onFlip"
                        }?.toTyped<Unit>()
                resolve().firstMethodOrNull {
                    name = "startFlipAnimation"
                }?.hook {
                    val onFlipListener = this.args.last()
                    if (onFlipListener != null && metOnFlip != null) {
                        metOnFlip.invoke(onFlipListener)
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
            }
        }
        if (ncModifyTextSize) {
            clzMiuiMediaNotificationControllerImpl?.apply {
                val fldMediaViewHolder = resolve().firstFieldOrNull {
                    name = "mediaViewHolder"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateLayout")
                    }
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldMediaViewHolder?.get(thisObject)
                    if (mediaViewHolder != null) {
                        val titleText = ncTitleText?.get(mediaViewHolder)
                        val artistText = ncArtistText?.get(mediaViewHolder)
                        titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                        artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                    }
                    result(ori)
                }
            }
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    val mediaViewHolder = thisObject
                    val titleText = ncTitleText?.get(mediaViewHolder)
                    val artistText = ncArtistText?.get(mediaViewHolder)
                    val elapsedTimeView = ncElapsedTimeView?.get(mediaViewHolder)
                    val totalTimeView = ncTotalTimeView?.get(mediaViewHolder)
                    titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                    artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                    elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                    totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                    result(ori)
                }
            }
        }
        if (diModifyTextSize) {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    val mediaViewHolder = thisObject
                    val titleText = diTitleText?.get(mediaViewHolder)
                    val artistText = diArtistText?.get(mediaViewHolder)
                    val elapsedTimeView = diElapsedTimeView?.get(mediaViewHolder)
                    val totalTimeView = diTotalTimeView?.get(mediaViewHolder)
                    titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTitleSize)
                    artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diArtistSize)
                    elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                    totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                    result(ori)
                }
            }
        }
    }
}