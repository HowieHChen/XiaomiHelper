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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.view.CometSeekBar
import dev.lackluster.mihelper.hook.view.SquigglySeekBar
import dev.lackluster.mihelper.hook.view.ThumbStyle
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField

object CustomProgressBar : YukiBaseHooker() {
    const val KEY_REAL_PROGRESS_BAR = "KEY_REAL_PROGRESS_BAR"

    private val ncThumbStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE, 0)
    private val ncProgressStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val ncProgressWidth = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH, 6.0f)
    private val ncProgressRound = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_ROUND, false) && ncProgressStyle == 1
    private val ncProgressComet = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_COMET, false) && ncProgressStyle == 1

    private val diThumbStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.ELM_THUMB_STYLE, 0)
    private val diProgressStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val diProgressWidth = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_WIDTH, 6.0f)
    private val diProgressRound = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_ROUND, false) && diProgressStyle == 1
    private val diProgressComet = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_COMET, false) && diProgressStyle == 1

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

    private val clzHyperProgressSeekBar by lazy {
        "miuix.miuixbasewidget.widget.HyperProgressSeekBar".toClassOrNull()
    }
    private val clzSeekBarObserver by lazy {
        $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl$seekBarObserver$1".toClassOrNull()
    }
    private val clzProgress by lazy {
        $$"com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel$Progress".toClassOrNull()
    }
    private val clzSeekBarViewModel by lazy {
        "com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel".toClassOrNull()
    }
    private val fldProgressSeekBarMinHeight by lazy {
        clzHyperProgressSeekBar?.resolve()?.firstFieldOrNull {
            name = "mProgressSeekBarMinHeight"
        }
    }
    private val fldProgressHeight by lazy {
        clzHyperProgressSeekBar?.resolve()?.firstFieldOrNull {
            name = "mProgressHeight"
        }
    }
    private val fldListening by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "listening"
        }?.self
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
    private val fldDuration by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "duration"
        }?.self
    }
    private val fldElapsedTime by lazy {
        clzProgress?.resolve()?.firstFieldOrNull {
            name = "elapsedTime"
        }?.self
    }
    private val fldFalsingManager by lazy {
        clzSeekBarViewModel?.resolve()?.firstFieldOrNull {
            name = "falsingManager"
        }?.self
    }
    private val ctorSeekBarChangeListener by lazy {
        $$"com.android.systemui.media.controls.ui.viewmodel.SeekBarViewModel$SeekBarChangeListener".toClassOrNull()
            ?.resolve()?.firstConstructorOrNull {
                parameterCount = 2
            }
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
    private val diElapsedTimeView by lazy {
        getMediaViewHolderField("elapsedTimeView", true)
    }
    private val diTotalTimeView by lazy {
        getMediaViewHolderField("totalTimeView", true)
    }
    private val diSeekBar by lazy {
        getMediaViewHolderField("seekBar", true)
    }

    override fun onHook() {
        if (ncProgressStyle == 0) {
            if (clzHyperProgressSeekBar != null) {
                clzMiuiMediaViewHolder?.apply {
                    resolve().firstConstructor().hook {
                        after {
                            val seekBar = ncSeekBar?.get(this.instance) as? SeekBar ?: return@after
                            if (clzHyperProgressSeekBar?.isInstance(seekBar) == true) {
                                val context = seekBar.context
                                var height = ncProgressWidth.dp(context)
                                if (height % 2 != 0) {
                                    height -= 1
                                }
                                fldProgressSeekBarMinHeight?.copy()?.of(seekBar)?.set(height)
                                fldProgressHeight?.copy()?.of(seekBar)?.set(height)
                            }
                        }
                    }
                }
            }
        } else {
            clzMiuiMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        getRealSeekBar(this.instance, false)
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
                    before {
                        val mediaViewHolder = fldHolder?.get(fldOuter?.get(this.instance)) ?: return@before
                        val vmProgress = this.args(0).any() ?: return@before
                        onProgressChanged(mediaViewHolder, vmProgress, false)
                        this.result = null
                    }
                }
            }
            clzMiuiMediaViewControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.self
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldSeekBarViewModel = resolve().firstFieldOrNull {
                    name = "seekBarViewModel"
                }?.self
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.self
                val fldEnableFullAod = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mEnableFullAod"
                    }?.self
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.self
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        getRealSeekBar(mediaViewHolder, false)?.setOnSeekBarChangeListener(null)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        val seekBarViewModel = fldSeekBarViewModel?.get(this.instance) ?: return@after
                        val falsingManager = fldFalsingManager?.get(seekBarViewModel)
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(mediaViewHolder, false)?.setOnSeekBarChangeListener(it)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onFullAodStateChanged"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        val toFullAod = this.args(0).boolean()
                        getRealSeekBar(mediaViewHolder, false)?.visibility = if (toFullAod) View.GONE else View.VISIBLE
                    }
                }
                resolve().firstMethodOrNull {
                    name = "updateForegroundColors"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        val context = fldContext?.get(this.instance) as? Context ?: return@after
                        val fullAodControllerLazy = fldFullAodController?.get(this.instance)
                        val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        val enableFullAod = fldEnableFullAod?.get(fullAodController) == true
                        val isDark = enableFullAod || context.isSystemInDarkMode
                        getRealSeekBar(mediaViewHolder, false)?.progressTintList = ColorStateList.valueOf(
                            if (isDark) Color.WHITE else Color.BLACK
                        )
                    }
                }
            }
        }
        if (diProgressStyle == 0) {
            if (clzHyperProgressSeekBar != null) {
                clzMiuiIslandMediaViewHolder?.apply {
                    resolve().firstConstructor().hook {
                        after {
                            val seekBar = diSeekBar?.get(this.instance) as? SeekBar ?: return@after
                            if (clzHyperProgressSeekBar?.isInstance(seekBar) == true) {
                                val context = seekBar.context
                                var height = diProgressWidth.dp(context)
                                if (height % 2 != 0) {
                                    height -= 1
                                }
                                fldProgressSeekBarMinHeight?.copy()?.of(seekBar)?.set(height)
                                fldProgressHeight?.copy()?.of(seekBar)?.set(height)
                            }
                        }
                    }
                }
            }
        } else {
            clzMiuiIslandMediaViewHolder?.apply {
                resolve().firstConstructor().hook {
                    after {
                        getRealSeekBar(this.instance, true)
                    }
                }
            }
            clzMiuiIslandMediaViewBinderImpl?.apply {
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldDummyHolder = resolve().firstFieldOrNull {
                    name = "dummyHolder"
                }?.self
                val fldSeekBarViewModel = resolve().firstFieldOrNull {
                    name = "seekBarViewModel"
                }?.self
                resolve().firstMethodOrNull {
                    name {
                        it.contains("seekBarChanged")
                    }
                    modifiers(Modifiers.STATIC)
                    parameterCount = 3
                }?.hook {
                    before {
                        val mediaViewHolder = this.args(2).any() ?: return@before
                        val vmProgress = this.args(1).any() ?: return@before
                        onProgressChanged(mediaViewHolder, vmProgress, true)
                        this.result = null
                    }
                }
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        val dummyMediaViewHolder = fldDummyHolder?.get(this.instance) ?: return@after
                        getRealSeekBar(mediaViewHolder, true)?.setOnSeekBarChangeListener(null)
                        getRealSeekBar(dummyMediaViewHolder, true)?.setOnSeekBarChangeListener(null)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val mediaViewHolder = fldHolder?.get(this.instance) ?: return@after
                        val dummyMediaViewHolder = fldDummyHolder?.get(this.instance) ?: return@after
                        val seekBarViewModel = fldSeekBarViewModel?.get(this.instance) ?: return@after
                        val falsingManager = fldFalsingManager?.get(seekBarViewModel)
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(mediaViewHolder, true)?.setOnSeekBarChangeListener(it)
                        }
                        ctorSeekBarChangeListener?.createAsType<SeekBar.OnSeekBarChangeListener>(seekBarViewModel, falsingManager)?.let {
                            getRealSeekBar(dummyMediaViewHolder, true)?.setOnSeekBarChangeListener(it)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onProgressChanged(mediaViewHolder: Any, vmProgress: Any, isDynamicIsland: Boolean) {
        val seekBar = getRealSeekBar(mediaViewHolder, isDynamicIsland) ?: return
        val fieldElapsedTimeView = if (isDynamicIsland) diElapsedTimeView else ncElapsedTimeView
        val fieldTotalTimeView = if (isDynamicIsland) diTotalTimeView else ncTotalTimeView
        val elapsedTimeView = fieldElapsedTimeView?.get(mediaViewHolder) as? TextView
        val totalTimeView = fieldTotalTimeView?.get(mediaViewHolder) as? TextView
        val listening = fldListening?.get(vmProgress) == true
        val seekAvailable = fldSeekAvailable?.get(vmProgress) == true
        val playing = fldPlaying?.get(vmProgress) == true
        val scrubbing = fldScrubbing?.get(vmProgress) == true
        val enabled = fldEnabled?.get(vmProgress) == true
        val duration = fldDuration?.get(vmProgress) as? Int ?: 0
        val elapsedTime = fldElapsedTime?.get(vmProgress) as? Int
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
        mediaViewHolder.getAdditionalInstanceField<SeekBar>(KEY_REAL_PROGRESS_BAR)?.let {
            return it
        }
        val style = if (isDynamicIsland) diProgressStyle else ncProgressStyle
        val fldSeekBar = if (isDynamicIsland) diSeekBar else ncSeekBar
        val seekBar = (fldSeekBar?.get(mediaViewHolder) as? SeekBar)
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
        mediaViewHolder.setAdditionalInstanceField(KEY_REAL_PROGRESS_BAR, realSeekBar)
        return realSeekBar
    }
}