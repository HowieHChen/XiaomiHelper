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
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.GhostThumb
import dev.lackluster.mihelper.hook.drawable.SquigglyProgress
import dev.lackluster.mihelper.hook.drawable.VerticalBarThumb
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_progress_bar
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaNotificationControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.view.CometSeekBar

object CustomElement : YukiBaseHooker() {
    private val ncAlbum = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ALBUM, 0)
    private val ncAlbumShadow = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ALBUM_SHADOW, true)
    private val albumFlip = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ALBUM_FLIP, true)

    private val ncModifyTextSize = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    private val ncTitleSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val ncArtistSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val ncTimeSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE, 12.0f)
    private val ncThumbStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE, 0)
    private val ncProgressStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val ncProgressWidth = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH, 4.0f)
    private val ncProgressRound = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_ROUND, false) && ncProgressStyle == 1
    private val ncProgressComet = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_COMET, false) && ncProgressStyle == 1
    private val ncThumbCropFix = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.FIX_THUMB_CROPPED, false) && ncProgressStyle != 1
    private val ncCustomThumbStyle by lazy {
        when (ncThumbStyle) {
            1 -> if (ncProgressRound) CometSeekBar.ThumbStyle.RoundRect else CometSeekBar.ThumbStyle.Hidden
            2 -> CometSeekBar.ThumbStyle.VerticalBar
            else -> CometSeekBar.ThumbStyle.Circle
        }
    }

    private val diModifyTextSize = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_TEXT_SIZE, false)
    private val diTitleSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val diArtistSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val diTimeSize = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_TIME_SIZE, 12.0f)
    private val diThumbStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.ELM_THUMB_STYLE, 0)
    private val diProgressStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val diProgressWidth = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_WIDTH, 4.0f)
    private val diProgressRound = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_ROUND, false) && diProgressStyle == 1
    private val diProgressComet = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_COMET, false) && diProgressStyle == 1
    private val diThumbCropFix = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.FIX_THUMB_CROPPED, false) && diProgressStyle != 1
    private val diCustomThumbStyle by lazy {
        when (diThumbStyle) {
            1 -> if (diProgressRound) CometSeekBar.ThumbStyle.RoundRect else CometSeekBar.ThumbStyle.Hidden
            2 -> CometSeekBar.ThumbStyle.VerticalBar
            else -> CometSeekBar.ThumbStyle.Circle
        }
    }

    private var ncThumb1: Drawable? = null
    private var ncThumb2: Drawable? = null

    private var diThumb1: Drawable? = null
    private var diThumb2: Drawable? = null

    private val clzSeekBarObserver by lazy {
        $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl$seekBarObserver$1".toClassOrNull()
    }
    private val clzProgress by lazy {
        $$"com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel$Progress".toClassOrNull()
    }
    private val fldSeekAvailable by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "seekAvailable"
        }?.self
    }
    private val fldPlaying by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "playing"
        }?.self
    }
    private val fldScrubbing by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "scrubbing"
        }?.self
    }
    private val fldEnabled by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "enabled"
        }?.self
    }

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
    private val ncSeekBar by lazy {
        getMediaViewHolderField("seekBar", false)
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
    private val diSeekBar by lazy {
        getMediaViewHolderField("seekBar", true)
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
        if (ncModifyTextSize || ncThumbStyle != 0 || ncThumbCropFix) {
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
                        if (ncModifyTextSize) {
                            val titleText = ncTitleText?.get(mediaViewHolder) as? TextView
                            val artistText = ncArtistText?.get(mediaViewHolder) as? TextView
                            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                            artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                        }
                    }
                }
            }
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        val mediaViewHolder = this.instance
                        if (ncModifyTextSize) {
                            val titleText = ncTitleText?.get(mediaViewHolder) as? TextView
                            val artistText = ncArtistText?.get(mediaViewHolder) as? TextView
                            val elapsedTimeView = ncElapsedTimeView?.get(mediaViewHolder) as? TextView
                            val totalTimeView = ncTotalTimeView?.get(mediaViewHolder) as? TextView
                            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTitleSize)
                            artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncArtistSize)
                            elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                            totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ncTimeSize)
                        }
                        val seekBar = (ncSeekBar?.get(mediaViewHolder) as? SeekBar) ?: return@after
                        if (ncProgressStyle != 1 && ncThumbStyle != 0) {
                            val context = seekBar.context
                            when (ncThumbStyle) {
                                1 -> {
                                    if (ncThumb1 == null) {
                                        val thumbSize = 14.dp(context)
                                        ncThumb1 = GhostThumb(thumbSize, thumbSize)
                                    }
                                    seekBar.thumb = ncThumb1
                                }
                                2 -> {
                                    if (ncThumb2 == null) {
                                        ncThumb2 = VerticalBarThumb(context.resources.displayMetrics.density)
                                    }
                                    seekBar.thumb = ncThumb2
                                }
                            }
                        }
                        if (ncProgressStyle != 1 && ncThumbCropFix && ncThumbStyle != 1) {
                            seekBar.updatePadding(left = seekBar.thumbOffset, right = seekBar.thumbOffset)
                        }
                    }
                }
            }
        }
        if (ncProgressStyle == 1) {
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        replaceSeekBar(this.instance, false)
                    }
                }
            }
        }
        if (ncProgressStyle == 2) {
            clzMiuiMediaViewControllerImpl?.apply {
                resolve().firstMethodOrNull {
                    name = "attach"
                    parameterCount = 1
                }?.hook {
                    after {
                        val mediaViewHolder = this.args(0).any() ?: return@after
                        val seekBar = (ncSeekBar?.get(mediaViewHolder) as? SeekBar) ?: return@after
                        val context = seekBar.context
                        seekBar.progressDrawable = SquigglyProgress().apply {
                            waveLength = 20.dpFloat(context)
                            lineAmplitude = 1.5.dpFloat(context)
                            phaseSpeed = 8.dpFloat(context)
                            strokeWidth = 2.dpFloat(context)
                        }
                    }
                }
            }
            clzSeekBarObserver?.apply {
                val fldOuter = resolve().firstFieldOrNull {
                    name = "this$0"
                }?.self
                val fldHolder = clzMiuiMediaViewControllerImpl?.resolve()?.firstFieldOrNull {
                    name = "holder"
                }?.self
                resolve().firstMethodOrNull {
                    name = "onChanged"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(fldOuter?.get(this.instance)) ?: return@after
                        val seekBar = (ncSeekBar?.get(mediaViewHolder) as? SeekBar) ?: return@after
                        val drawable = seekBar.progressDrawable
                        if (drawable !is SquigglyProgress) return@after
                        val progress = this.args(0).any() ?: return@after
                        val seekAvailable = fldSeekAvailable?.get(progress) == true
                        val playing = fldPlaying?.get(progress) == true
                        val scrubbing = fldScrubbing?.get(progress) == true
                        val enabled = fldEnabled?.get(progress) == true
                        if (!enabled) {
                            drawable.animate = false
                        } else {
                            drawable.animate = playing && !scrubbing
                            drawable.transitionEnabled = !seekAvailable
                        }
                    }
                }
            }
        }
        if (diModifyTextSize || diThumbStyle != 0 || diThumbCropFix) {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        val mediaViewHolder = this.instance
                        if (diModifyTextSize) {
                            val titleText = diTitleText?.get(mediaViewHolder) as? TextView
                            val artistText = diArtistText?.get(mediaViewHolder) as? TextView
                            val elapsedTimeView = diElapsedTimeView?.get(mediaViewHolder) as? TextView
                            val totalTimeView = diTotalTimeView?.get(mediaViewHolder) as? TextView
                            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTitleSize)
                            artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diArtistSize)
                            elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                            totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, diTimeSize)
                        }
                        val seekBar = (diSeekBar?.get(mediaViewHolder) as? SeekBar) ?: return@after
                        if (diProgressStyle != 1 && diThumbStyle != 0) {
                            val context = seekBar.context
                            when (diThumbStyle) {
                                1 -> {
                                    if (diThumb1 == null) {
                                        val thumbSize = 14.dp(context)
                                        diThumb1 = GhostThumb(thumbSize, thumbSize)
                                    }
                                    seekBar.thumb = diThumb1
                                }
                                2 -> {
                                    if (diThumb2 == null) {
                                        diThumb2 = VerticalBarThumb(context.resources.displayMetrics.density)
                                    }
                                    seekBar.thumb = diThumb2
                                }
                            }
                        }
                        if (diProgressStyle != 1 && diThumbCropFix && diThumbStyle != 1) {
                            seekBar.updatePadding(left = seekBar.thumbOffset, right = seekBar.thumbOffset)
                        }
                    }
                }
            }
        }
        if (diProgressStyle == 1) {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        replaceSeekBar(this.instance, true)
                    }
                }
            }
        }
    }

    private fun replaceSeekBar(mediaViewHolder: Any, isDynamicIsland: Boolean) {
        val fldSeekBar = if (isDynamicIsland) diSeekBar else ncSeekBar
        val seekBar = (fldSeekBar?.get(mediaViewHolder) as? SeekBar)
        val parent = seekBar?.parent as? ViewGroup ?: return
        val context = seekBar.context
        val index = (parent.indexOfChild(seekBar) + 1).coerceIn(0, parent.childCount)
        val height = if (isDynamicIsland) diProgressWidth.dp(context) else ncProgressWidth.dp(context)
        val comet = if (isDynamicIsland) diProgressComet else ncProgressComet
        val thumb = if (isDynamicIsland) diCustomThumbStyle else ncCustomThumbStyle
        val cometSeekBar = CometSeekBar(context).apply {
            id = media_progress_bar
            layoutParams = seekBar.layoutParams?.apply {
                (this as? ViewGroup.MarginLayoutParams)?.updateMargins(top = 0, bottom = 0)
            }
            progressHeight = height
            cometEffect = comet
            thumbStyle = thumb
        }
        parent.addView(cometSeekBar, index)
        parent.removeView(seekBar)
        fldSeekBar.set(mediaViewHolder, cometSeekBar)
    }
}