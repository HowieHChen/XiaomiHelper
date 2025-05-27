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
import android.util.TypedValue
import android.widget.SeekBar
import android.widget.TextView
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
    private val modifyTextSize = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    private val titleSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE, 18.0f)
    private val artistSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    private val timeSize = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE, 12.0f)
    private val thumbStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE, 0)
    private val progressStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    private val progressWidth = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH, 4.0f)

    private var progress: Drawable? = null
    private var thumb1: Drawable? = null
    private var thumb2: Drawable? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        if (modifyTextSize || thumbStyle != 0 ) {
            "com.android.systemui.media.controls.ui.view.MediaViewHolder\$Companion".toClassOrNull()?.apply {
                method {
                    name = "create"
                }.hook {
                    after {
                        val mediaViewHolder = this.result ?: return@after
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
                        if (thumbStyle != 0) {
                            val seekBar = mediaViewHolder.current().field { name = "seekBar" }.cast<SeekBar>() ?: return@after
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

                    }
                }
            }
        }
        if (progressStyle != 0) {
            "com.android.systemui.media.controls.ui.binder.SeekBarObserver".toClassOrNull()?.apply {
                constructor().hook {
                    after {
                        val mediaViewHolder = this.instance.current().field {
                            name = "holder"
                        }.any() ?: return@after
                        val seekBar = mediaViewHolder.current().field { name = "seekBar" }.cast<SeekBar>() ?: return@after
                        val context = seekBar.context
                        if (progressStyle == 1) {
                            val width = progressWidth.dp(context)
                            this.instance.current().field {
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
                            val mediaViewHolder = this.instance.current().field {
                                name = "holder"
                            }.any() ?: return@after
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
//        if(hideAppIcon || squigglyProgress) {
//            "com.android.systemui.media.controls.models.player.MediaViewHolder\$Companion".toClass().method {
//                name = "create"
//                modifiers { isStatic }
//            }.hook {
//                after {
//                    val mediaViewHolder = this.result ?: return@after
//                    val a: TextView
//                    a.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1.0f)
//                    if (hideAppIcon) {
//                        val appIcon = mediaViewHolder.current().field { name = "appIcon" }.any() as? ImageView?
//                        (appIcon?.parent as? ViewGroup?)?.removeView(appIcon)
//                    }
//                    if (squigglyProgress) {
//                        val seekBar = mediaViewHolder.current().field { name = "seekBar" }.any() as? SeekBar?
//                        seekBar?.progressDrawable = "com.android.systemui.media.controls.ui.SquigglyProgress".toClass().constructor().get().call() as Drawable
//                    }
//                }
//            }
//        }
    }
}