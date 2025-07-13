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
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.session.PlaybackState.CustomAction
import android.util.TypedValue
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.updatePadding
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.SquigglyProgress
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.dpFloat
import com.highcapable.yukihookapi.hook.factory.constructor

object CustomElement : YukiBaseHooker() {
    private val actionsResize = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ACTIONS_RESIZE, true)
    private val modifyTextSize = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    private val titleSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val artistSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val timeSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE, 12.0f)
    private val thumbStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE, 0)
    private val progressStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val progressWidth = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH, 4.0f)
    private val thumbCropFix = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.FIX_THUMB_CROPPED, false)

    private var progress: Drawable? = null
    private var thumb1: Drawable? = null
    private var thumb2: Drawable? = null

    private val mediaViewHolderClass by lazy {
        "com.android.systemui.media.controls.ui.view.MediaViewHolder".toClassOrNull()
            ?: "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewHolder".toClassOrNull()
            ?: "com.android.systemui.media.controls.models.player.MediaViewHolder".toClassOrNull()
    }
    private val seekBarObserverClass by lazy {
        "com.android.systemui.media.controls.ui.binder.SeekBarObserver".toClassOrNull()
            ?: "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl\$seekBarObserver$1".toClassOrNull()
            ?: "com.android.systemui.media.controls.models.player.SeekBarObserver".toClassOrNull()
    }
    private val customActionsClass by lazy {
        "com.android.systemui.media.controls.domain.pipeline.LegacyMediaDataManagerImpl\$createActionsFromState\$customActions$1".toClassOrNull()
            ?: "com.android.systemui.media.controls.pipeline.MediaDataManager\$createActionsFromState\$customActions$1".toClassOrNull()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        if (modifyTextSize || thumbStyle != 0 || thumbCropFix) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaNotificationControllerImpl".toClassOrNull()?.apply {
                method {
                    name = "updateLayout$6"
                }.hook {
                    after {
                        val mediaViewHolder = this.instance.current().field {
                            name = "mediaViewHolder"
                        }.any() ?: return@after
                        if (modifyTextSize) {
                            val titleText = mediaViewHolder.current().field { name = "titleText" }.cast<TextView>()
                            val artistText = mediaViewHolder.current().field { name = "artistText" }.cast<TextView>()
                            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize)
                            artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, artistSize)
                        }
                    }
                }
            }
            mediaViewHolderClass?.apply {
                constructor().hook {
                    after {
                        val mediaViewHolder = this.instance
                        if (modifyTextSize) {
                            val titleText = mediaViewHolder.current().field { name = "titleText" }.cast<TextView>()
                            val artistText = mediaViewHolder.current().field { name = "artistText" }.cast<TextView>()
                            val elapsedTimeView = mediaViewHolder.current().field { name = "elapsedTimeView" }.cast<TextView>()
                            val totalTimeView = mediaViewHolder.current().field { name = "totalTimeView" }.cast<TextView>()
                            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize)
                            artistText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, artistSize)
                            elapsedTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, timeSize)
                            totalTimeView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, timeSize)
                        }
                        val seekBar = mediaViewHolder.current().field { name = "seekBar" }.cast<SeekBar>() ?: return@after
                        if (thumbStyle != 0) {
                            val context = seekBar.context
                            when (thumbStyle) {
                                1 -> {
                                    if (thumb1 == null) {
                                        val moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                                        thumb1 = moduleContext.getDrawable(R.drawable.media_seekbar_thumb_none)
                                    }
                                    seekBar.thumb = thumb1
                                }
                                2 -> {
                                    if (thumb2 == null) {
                                        val moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                                        thumb2 = moduleContext.getDrawable(R.drawable.media_seekbar_thumb)
                                    }
                                    seekBar.thumb = thumb2
                                }
                            }
                        }
                        if (thumbCropFix && thumbStyle != 1) {
                            seekBar.updatePadding(left = seekBar.thumbOffset, right = seekBar.thumbOffset)
                        }
                    }
                }
            }
        }
        if (progressStyle != 0) {
            seekBarObserverClass?.apply {
                constructor().hook {
                    after {
                        val mediaViewHolder = this.instance.current(true).field {
                            name = "holder"
                        }.any() ?: this.instance.current(true).field {
                            name = "this$0"
                        }.any()?.current()?.field {
                            name = "holder"
                        }?.any() ?: return@after
                        val seekBar = mediaViewHolder.current().field { name = "seekBar" }.cast<SeekBar>() ?: return@after
                        val context = seekBar.context
                        if (progressStyle == 1) {
                            val width = progressWidth.dp(context)
                            this.instance.current(true).field {
                                name = "seekBarEnabledMaxHeight"
                            }.set(width)
                            seekBar.minHeight = width
                            seekBar.maxHeight = width
                            if (progress == null) {
                                val moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                                progress = moduleContext.getDrawable(R.drawable.media_seekbar_progress)
                            }
                            seekBar.progressDrawable = progress
                        } else if (progressStyle == 2) {
                            seekBar.progressDrawable = SquigglyProgress().apply {
                                waveLength = 20.dpFloat(context)
                                lineAmplitude = 1.5.dpFloat(context)
                                phaseSpeed = 8.dpFloat(context)
                                strokeWidth = 2.dpFloat(context)
                            }
                        }
                    }
                }
                if (progressStyle == 2) {
                    method {
                        name = "onChanged"
                    }.hook {
                        after {
                            val mediaViewHolder = this.instance.current(true).field {
                                name = "holder"
                            }.any() ?: this.instance.current(true).field {
                                name = "this$0"
                            }.any()?.current()?.field {
                                name = "holder"
                            }?.any() ?: return@after
                            val seekBar = mediaViewHolder.current().field { name = "seekBar" }.cast<SeekBar>()
                            val drawable = seekBar?.progressDrawable
                            if (drawable !is SquigglyProgress) return@after
                            val progress = this.args(0).any() ?: return@after
                            val seekAvailable = progress.current().field { name = "seekAvailable" }.boolean()
                            val playing = progress.current().field { name = "playing" }.boolean()
                            val scrubbing = progress.current().field { name = "scrubbing" }.boolean()
                            val enabled = progress.current().field { name = "enabled" }.boolean()
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
        }
        if (actionsResize) {
            customActionsClass?.apply {
                method {
                    name = "invoke"
                }.hook {
                    after {
                        val mediaAction = this.result ?: return@after
                        val pkgName = this.instance.current().field { name = "\$packageName" }.string()
                        val customAction = this.args(0).cast<CustomAction>() ?: return@after
                        val mediaDataManager = this.instance.current().field { name = "this$0" }.any() ?: return@after
                        val context = mediaDataManager.current().field { name = "context" }.any() as Context
                        val drawable = Icon.createWithResource(pkgName, customAction.icon).loadDrawable(context)
                        mediaAction.current().field { name = "icon" }.set(drawable)
//                        mediaAction.current().field { name = "icon" }.set(
//                            ScaleDrawable(drawable, Gravity.CENTER, 0.1f, 0.1f).apply { level = 1 }
//                        )
//                        YLog.info("customActions: $pkgName")
                    }
                }
            }
        }
    }
}