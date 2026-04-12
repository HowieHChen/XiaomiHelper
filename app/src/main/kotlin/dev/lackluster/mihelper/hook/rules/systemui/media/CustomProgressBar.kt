/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.updateMargins
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.rules.systemui.media.view.CometSeekBar
import dev.lackluster.mihelper.hook.rules.systemui.media.view.SquigglySeekBar
import dev.lackluster.mihelper.hook.rules.systemui.media.view.ThumbStyle
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode

internal object CustomProgressBar : StaticHooker() {
    var Any.realSeekBar by extraOf<SeekBar>("KEY_REAL_PROGRESS_BAR")

    private val ncThumbStyle by Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.get(false).lazyGet()
    private val ncProgressStyle by Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.get(false).lazyGet()
    private val ncProgressWidth by Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.get(false).lazyGet()
    private val ncProgressRound by lazy {
        Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.get(false).get() && ncProgressStyle == 1
    }
    private val ncProgressComet by lazy {
        Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.get(false).get() && ncProgressStyle == 1
    }

    private val diThumbStyle by Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.get(true).lazyGet()
    private val diProgressStyle by Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.get(true).lazyGet()
    private val diProgressWidth by Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.get(true).lazyGet()
    private val diProgressRound by lazy {
        Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.get(true).get() && diProgressStyle == 1
    }
    private val diProgressComet by lazy {
        Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.get(true).get() && diProgressStyle == 1
    }

    private val ncCustomThumbStyle by lazy {
        when (ncThumbStyle) {
            1 -> if (ncProgressRound) ThumbStyle.RoundRect else ThumbStyle.Hidden
            2 -> ThumbStyle.VerticalBar
            else -> ThumbStyle.Circle
        }
    }
    private val diCustomThumbStyle by lazy {
        when (diThumbStyle) {
            1 -> if (diProgressRound) ThumbStyle.RoundRect else ThumbStyle.Hidden
            2 -> ThumbStyle.VerticalBar
            else -> ThumbStyle.Circle
        }
    }

    private val clzHyperProgressSeekBar by "miuix.miuixbasewidget.widget.HyperProgressSeekBar".lazyClassOrNull()
    private val clzSeekBarObserver by $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl$seekBarObserver$1".lazyClassOrNull()
    private val clzProgress by $$"com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel$Progress".lazyClassOrNull()
    private val clzSeekBarViewModel by "com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel".lazyClassOrNull()

    private val fldProgressSeekBarMinHeight by lazy {
        clzHyperProgressSeekBar?.resolve()?.firstFieldOrNull {
            name = "mProgressSeekBarMinHeight"
        }?.toTyped<Int>()
    }
    private val fldProgressHeight by lazy {
        clzHyperProgressSeekBar?.resolve()?.firstFieldOrNull {
            name = "mProgressHeight"
        }?.toTyped<Int>()
    }
    private val fldListening by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "listening"
        }?.toTyped<Boolean>()
    }
    private val fldSeekAvailable by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "seekAvailable"
        }?.toTyped<Boolean>()
    }
    private val fldPlaying by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "playing"
        }?.toTyped<Boolean>()
    }
    private val fldScrubbing by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "scrubbing"
        }?.toTyped<Boolean>()
    }
    private val fldEnabled by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "enabled"
        }?.toTyped<Boolean>()
    }
    private val fldDuration by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "duration"
        }?.toTyped<Int>()
    }
    private val fldElapsedTime by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "elapsedTime"
        }?.toTyped<Int?>()
    }
    private val fldFalsingManager by lazy {
        clzSeekBarViewModel?.resolve()?.firstFieldOrNull {
            name = "falsingManager"
        }?.toTyped<Any>()
    }
    private val ctorSeekBarChangeListener by lazy {
        $$"com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel$SeekBarChangeListener".toClassOrNull()
            ?.resolve()?.firstConstructorOrNull {
                parameterCount = 2
            }
    }
    private val ncElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", false)?.toTyped<TextView>()
    }
    private val ncTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", false)?.toTyped<TextView>()
    }
    private val ncSeekBar by lazy {
        getMediaViewHolderField("seekBar", false)?.toTyped<SeekBar>()
    }
    private val diElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", true)?.toTyped<TextView>()
    }
    private val diTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", true)?.toTyped<TextView>()
    }
    private val diSeekBar by lazy {
        getMediaViewHolderField("seekBar", true)?.toTyped<SeekBar>()
    }

    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        if (ncProgressStyle == 0) {
            if (clzHyperProgressSeekBar != null) {
                clzMiuiMediaViewHolder?.apply {
                    resolve().firstConstructor().hook {
                        val ori = proceed()
                        val seekBar = ncSeekBar?.get(thisObject)
                        if (seekBar != null && clzHyperProgressSeekBar?.isInstance(seekBar) == true) {
                            val context = seekBar.context
                            var height = ncProgressWidth.dp(context)
                            if (height % 2 != 0) {
                                height -= 1
                            }
                            fldProgressSeekBarMinHeight?.set(seekBar, height)
                            fldProgressHeight?.set(seekBar, height)
                        }
                        result(ori)
                    }
                }
            }
        } else {
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    getRealSeekBar(thisObject, false)
                    result(ori)
                }
            }
            clzSeekBarObserver?.apply {
                val fldOuter = resolve().firstFieldOrNull {
                    name = "this$0"
                }?.toTyped<Any>()
                val fldHolder = clzMiuiMediaViewControllerImpl?.resolve()?.firstFieldOrNull {
                    name = "holder"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "onChanged"
                }?.hook {
                    val mediaViewHolder = fldOuter?.get(thisObject)?.let {
                        fldHolder?.get(it)
                    }
                    val vmProgress = getArg(0)
                    if (mediaViewHolder != null && vmProgress != null) {
                        onProgressChanged(mediaViewHolder, vmProgress, false)
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
            }
            clzMiuiMediaViewControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.toTyped<Context>()
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.toTyped<Any>()
                val fldSeekBarViewModel = resolve().firstFieldOrNull {
                    name = "seekBarViewModel"
                }?.toTyped<Any>()
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.toTyped<Any>()
                val fldEnableFullAod = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mEnableFullAod"
                    }?.toTyped<Boolean>()
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldHolder?.get(thisObject)
                    if (mediaViewHolder != null) {
                        getRealSeekBar(mediaViewHolder, false)?.setOnSeekBarChangeListener(null)
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldHolder?.get(thisObject)
                    val seekBarViewModel = fldSeekBarViewModel?.get(thisObject)
                    val falsingManager = seekBarViewModel?.let {
                        fldFalsingManager?.get(it)
                    }
                    if (mediaViewHolder != null && seekBarViewModel != null && falsingManager != null) {
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(mediaViewHolder, false)?.setOnSeekBarChangeListener(it)
                        }
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "onFullAodStateChanged"
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldHolder?.get(thisObject)
                    val toFullAod = getArg(0) as? Boolean
                    if (mediaViewHolder != null && toFullAod != null) {
                        getRealSeekBar(mediaViewHolder, false)?.visibility = if (toFullAod) View.GONE else View.VISIBLE
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "updateForegroundColors"
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldHolder?.get(thisObject)
                    val context = fldContext?.get(thisObject)
                    val fullAodControllerLazy = fldFullAodController?.get(thisObject)
                    val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                    if (mediaViewHolder != null && context != null && fullAodController != null) {
                        val enableFullAod = fldEnableFullAod?.get(fullAodController) ?: false
                        val isDark = enableFullAod || context.isSystemInDarkMode
                        getRealSeekBar(mediaViewHolder, false)?.progressTintList = ColorStateList.valueOf(
                            if (isDark) Color.WHITE else Color.BLACK
                        )
                    }
                    result(ori)
                }
            }
        }
        if (diProgressStyle == 0) {
            if (clzHyperProgressSeekBar != null) {
                clzMiuiIslandMediaViewHolder?.apply {
                    resolve().firstConstructor().hook {
                        val ori = proceed()
                        val seekBar = diSeekBar?.get(thisObject)
                        if (seekBar != null && clzHyperProgressSeekBar?.isInstance(seekBar) == true) {
                            val context = seekBar.context
                            var height = diProgressWidth.dp(context)
                            if (height % 2 != 0) {
                                height -= 1
                            }
                            fldProgressSeekBarMinHeight?.set(seekBar, height)
                            fldProgressHeight?.set(seekBar, height)
                        }
                        result(ori)
                    }
                }
            }
        } else {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    getRealSeekBar(thisObject, true)
                    result(ori)
                }
            }
            clzMiuiIslandMediaViewBinderImpl?.apply {
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.toTyped<Any>()
                val fldDummyHolder = resolve().firstFieldOrNull {
                    name = "dummyHolder"
                }?.toTyped<Any>()
                val fldSeekBarViewModel = resolve().firstFieldOrNull {
                    name = "seekBarViewModel"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name {
                        it.contains("seekBarChanged")
                    }
                    modifiers(Modifiers.STATIC)
                    parameterCount = 3
                }?.hook {
                    val mediaViewHolder = getArg(2)
                    val vmProgress = getArg(1)
                    if (mediaViewHolder != null && vmProgress != null) {
                        onProgressChanged(mediaViewHolder, vmProgress, true)
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    val ori = proceed()
                    fldHolder?.get(thisObject)?.let { mediaViewHolder ->
                        getRealSeekBar(mediaViewHolder, true)?.setOnSeekBarChangeListener(null)
                    }
                    fldDummyHolder?.get(thisObject)?.let { dummyMediaViewHolder ->
                        getRealSeekBar(dummyMediaViewHolder, true)?.setOnSeekBarChangeListener(null)
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    val ori = proceed()
                    val mediaViewHolder = fldHolder?.get(thisObject)
                    val dummyMediaViewHolder = fldDummyHolder?.get(thisObject)
                    val seekBarViewModel = fldSeekBarViewModel?.get(thisObject)
                    val falsingManager = seekBarViewModel?.let { fldFalsingManager?.get(it) }
                    if (mediaViewHolder != null && dummyMediaViewHolder != null && falsingManager != null) {
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(mediaViewHolder, true)?.setOnSeekBarChangeListener(it)
                        }
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(dummyMediaViewHolder, true)?.setOnSeekBarChangeListener(it)
                        }
                    }
                    result(ori)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onProgressChanged(mediaViewHolder: Any, vmProgress: Any, isDynamicIsland: Boolean) {
        val seekBar = getRealSeekBar(mediaViewHolder, isDynamicIsland) ?: return
        val fieldElapsedTimeView = if (isDynamicIsland) diElapsedTimeView else ncElapsedTimeView
        val fieldTotalTimeView = if (isDynamicIsland) diTotalTimeView else ncTotalTimeView
        val elapsedTimeView = fieldElapsedTimeView?.get(mediaViewHolder)
        val totalTimeView = fieldTotalTimeView?.get(mediaViewHolder)
        val listening = fldListening?.get(vmProgress) == true
        val seekAvailable = fldSeekAvailable?.get(vmProgress) == true
        val playing = fldPlaying?.get(vmProgress) == true
        val scrubbing = fldScrubbing?.get(vmProgress) == true
        val enabled = fldEnabled?.get(vmProgress) == true
        val duration = fldDuration?.get(vmProgress) ?: 0
        val elapsedTime = fldElapsedTime?.get(vmProgress)
        if (enabled) {
            totalTimeView?.text = DateUtils.formatElapsedTime(duration / 1000L)
            seekBar.isEnabled = seekAvailable
            seekBar.max = duration
            elapsedTime?.let {
                elapsedTimeView?.text = DateUtils.formatElapsedTime(it / 1000L)
                if (!scrubbing) {
                    seekBar.progress = it
                }
            }
            if (seekBar is SquigglySeekBar) {
                seekBar.animate = playing && !scrubbing && listening
                seekBar.transitionEnabled = !seekAvailable
            }
        } else {
            seekBar.apply {
                isEnabled = false
                progress = 0
                contentDescription = ""
            }
            elapsedTimeView?.text = "00:00"
            totalTimeView?.text = "00:00"
            if (seekBar is SquigglySeekBar) {
                seekBar.animate = false
            }
        }
    }

    private fun getRealSeekBar(mediaViewHolder: Any, isDynamicIsland: Boolean): SeekBar? {
        mediaViewHolder.realSeekBar?.let {
            return it
        }
        val style = if (isDynamicIsland) diProgressStyle else ncProgressStyle
        val fldSeekBar = if (isDynamicIsland) diSeekBar else ncSeekBar
        val seekBar = fldSeekBar?.get(mediaViewHolder)
        val parent = seekBar?.parent as? ViewGroup ?: return null
        val context = seekBar.context
        val index = (parent.indexOfChild(seekBar) + 1).coerceIn(0, parent.childCount)
        val height = if (isDynamicIsland) diProgressWidth.dp(context) else ncProgressWidth.dp(context)
        val comet = if (isDynamicIsland) diProgressComet else ncProgressComet
        val thumb = if (isDynamicIsland) diCustomThumbStyle else ncCustomThumbStyle
        val realSeekBar = if (style == 2) {
            SquigglySeekBar(context).apply {
                id = ResourcesUtils.media_progress_bar
                layoutParams = seekBar.layoutParams?.apply {
                    (this as? ViewGroup.MarginLayoutParams)?.updateMargins(top = 0, bottom = 0)
                }
                thumbStyle = thumb
                waveLength = 20.dpFloat(context)
                lineAmplitude = 1.5.dpFloat(context)
                phaseSpeed = 8.dpFloat(context)
                strokeWidth = 2.dpFloat(context)
            }
        } else {
            CometSeekBar(context).apply {
                id = ResourcesUtils.media_progress_bar
                layoutParams = seekBar.layoutParams?.apply {
                    (this as? ViewGroup.MarginLayoutParams)?.updateMargins(top = 0, bottom = 0)
                }
                progressHeight = height
                cometEffect = comet
                thumbStyle = thumb
            }
        }
        parent.addView(realSeekBar, index)
        parent.removeView(seekBar)
        mediaViewHolder.realSeekBar = realSeekBar
        return realSeekBar
    }
}