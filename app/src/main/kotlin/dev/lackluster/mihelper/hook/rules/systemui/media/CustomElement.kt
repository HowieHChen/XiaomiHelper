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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaNotificationControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField

object CustomElement : YukiBaseHooker() {
    private val ncAlbum = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ALBUM, 0)
    private val ncAlbumShadow = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ALBUM_SHADOW, true)
    private val albumFlip = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ALBUM_FLIP, true)

    private val ncModifyTextSize = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    private val ncTitleSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val ncArtistSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val ncTimeSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE, 12.0f)

    private val diModifyTextSize = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_TEXT_SIZE, false)
    private val diTitleSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val diArtistSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val diTimeSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_TIME_SIZE, 12.0f)

    private val ncTitleText by lazy {
        getMediaViewHolderField("titleText", false)
    }
    private val ncArtistText by lazy {
        getMediaViewHolderField("artistText", false)
    }
    private val ncElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", false)
    }
    private val ncTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", false)
    }
    private val diTitleText by lazy {
        getMediaViewHolderField("titleText", true)
    }
    private val diArtistText by lazy {
        getMediaViewHolderField("artistText", true)
    }
    private val diElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", true)
    }
    private val diTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", true)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        if (!ncAlbumShadow && ncAlbum != 2) {
            "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "applyViewShadowForMediaAlbum"
                }?.hook {
                    intercept()
                }
            }
        }
        if (!albumFlip) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaAlbumAnimationUtils".toClassOrNull()?.apply {
                val metOnFlip =
                    $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaAlbumAnimationUtils$OnFlipListener".toClassOrNull()
                        ?.resolve()?.firstMethodOrNull {
                            name = "onFlip"
                        }?.self
                resolve().firstMethodOrNull {
                    name = "startFlipAnimation"
                }?.hook {
                    before {
                        val onFlipListener = this.args(2).any()
                        if (onFlipListener != null && metOnFlip != null) {
                            metOnFlip.invoke(onFlipListener)
                            this.result = null
                        }
                    }
                }
            }
        }
        if (ncModifyTextSize) {
            clzMiuiMediaNotificationControllerImpl?.apply {
                val fldMediaViewHolder = resolve().firstFieldOrNull {
                    name = "mediaViewHolder"
                }?.self
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("updateLayout")
                    }
                }?.hook {
                    after {
                        val mediaViewHolder = fldMediaViewHolder?.get(this.instance) ?: return@after
                        val titleText = ncTitleText?.get(mediaViewHolder) as? TextView
                        val artistText = ncArtistText?.get(mediaViewHolder) as? TextView
                        titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                        artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                    }
                }
            }
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        val mediaViewHolder = this.instance
                        val titleText = ncTitleText?.get(mediaViewHolder) as? TextView
                        val artistText = ncArtistText?.get(mediaViewHolder) as? TextView
                        val elapsedTimeView = ncElapsedTimeView?.get(mediaViewHolder) as? TextView
                        val totalTimeView = ncTotalTimeView?.get(mediaViewHolder) as? TextView
                        titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                        artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                        elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                        totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                    }
                }
            }
        }
        if (diModifyTextSize) {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        val mediaViewHolder = this.instance
                        val titleText = diTitleText?.get(mediaViewHolder) as? TextView
                        val artistText = diArtistText?.get(mediaViewHolder) as? TextView
                        val elapsedTimeView = diElapsedTimeView?.get(mediaViewHolder) as? TextView
                        val totalTimeView = diTotalTimeView?.get(mediaViewHolder) as? TextView
                        titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTitleSize)
                        artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diArtistSize)
                        elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                        totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                    }
                }
            }
        }
    }
}