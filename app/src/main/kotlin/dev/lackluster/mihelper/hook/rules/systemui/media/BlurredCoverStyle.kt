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

@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import android.os.Trace
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaHookEntry.hardwareBlur
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaHookEntry.scaleTransitionDrawableLayer
import dev.lackluster.mihelper.utils.Prefs
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable
import dev.lackluster.mihelper.hook.drawable.RadialGradientDrawable


object BlurredCoverStyle : YukiBaseHooker() {
    private val useAnim = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.USE_ANIM, true)
    private val blurRadius = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BLUR_RADIUS, 10).coerceIn(1, 20)
    private val miuiMediaControlPanelClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClass()
    }
    private val colorSchemeClass by lazy {
        "com.android.systemui.monet.ColorScheme".toClass()
    }
    private val contentStyle by lazy {
        "com.android.systemui.monet.Style".toClass().enumConstants?.get(6)
    }
    private var mArtworkBoundId = 0
    private var mArtworkNextBindRequestId = 0
    private var mPrevArtwork: Drawable? = null
    private var mIsArtworkBound = false
    private var mPrevTextPrimaryColor = Color.WHITE
    private var mCurrentTextPrimaryColor = Color.WHITE
    private var animatingColorTransition: AnimatingColorTransition? = null
    private var lastWidth = 0
    private var lastHeight = 0

    override fun onHook() {
        miuiMediaControlPanelClass.method {
            name = "bindPlayer"
        }.hook {
            after {
                val data = this.args(0).any() ?: return@after
                val key = this.args(1).string()
                val updateBackground = this.instance.current().field {
                    name = "mIsArtworkUpdate"
                    superClass()
                }.boolean()
                val mMediaViewHolder = this.instance.current().field {
                    name = "mMediaViewHolder"
                    superClass()
                }.any() ?: return@after
                val mContext = this.instance.current().field {
                    name = "mContext"
                    superClass()
                }.any() as? Context ?: return@after

                val traceCookie = data.hashCode()
                val traceName = "MediaControlPanel#bindArtworkAndColors<$key>"
                Trace.beginAsyncSection(traceName, traceCookie)

                val reqId = mArtworkNextBindRequestId++
                if (updateBackground) {
                    mIsArtworkBound = false
                }
                val artworkIcon = data.current().field { name = "artwork" }.any() as? Icon?

                val mediaBg = mMediaViewHolder.current(true).field { name = "mediaBg" }.any() as ImageView
                val titleText = mMediaViewHolder.current(true).field { name = "titleText" }.any() as TextView
                val artistText = mMediaViewHolder.current(true).field { name = "artistText" }.any() as TextView
                val seamlessIcon = mMediaViewHolder.current(true).field { name = "seamlessIcon" }.any() as ImageView
                val action0 = mMediaViewHolder.current(true).field { name = "action0" }.any() as ImageButton
                val action1 = mMediaViewHolder.current(true).field { name = "action1" }.any() as ImageButton
                val action2 = mMediaViewHolder.current(true).field { name = "action2" }.any() as ImageButton
                val action3 = mMediaViewHolder.current(true).field { name = "action3" }.any() as ImageButton
                val action4 = mMediaViewHolder.current(true).field { name = "action4" }.any() as ImageButton
                val actionNext = mMediaViewHolder.current(true).field { name = "actionNext" }.any() as ImageButton
                val actionPlayPause = mMediaViewHolder.current(true).field { name = "actionPlayPause" }.any() as ImageButton
                val actionPrev = mMediaViewHolder.current(true).field { name = "actionPrev" }.any() as ImageButton
                val seekBar = mMediaViewHolder.current(true).field { name = "seekBar" }.any() as SeekBar
                val elapsedTimeView = mMediaViewHolder.current(true).field { name = "elapsedTimeView" }.any() as TextView
                val totalTimeView = mMediaViewHolder.current(true).field { name = "totalTimeView" }.any() as TextView
                val scrubbingElapsedTimeView = mMediaViewHolder.current(true).field { name = "scrubbingElapsedTimeView" }.any() as TextView
                val scrubbingTotalTimeView = mMediaViewHolder.current(true).field { name = "scrubbingTotalTimeView" }.any() as TextView
                val albumView = mMediaViewHolder.current(true).field { name = "albumView" }.any() as ImageView

                val artworkLayer = artworkIcon?.loadDrawable(mContext) ?: return@after
                val artworkBitmap = createBitmap(artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight)
                val canvas = Canvas(artworkBitmap)
                artworkLayer.setBounds(0, 0, artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight)
                artworkLayer.draw(canvas)
                val resizedBitmap = artworkBitmap.scale(300, 300)
                val radius = 45f
                val newBitmap = createBitmap(resizedBitmap.width, resizedBitmap.height)
                val canvas1 = Canvas(newBitmap)
                val paint = Paint()
                val rect = Rect(0, 0, resizedBitmap.width, resizedBitmap.height)
                val rectF = RectF(rect)
                paint.isAntiAlias = true
                canvas1.drawARGB(0, 0, 0, 0)
                paint.color = Color.BLACK
                canvas1.drawRoundRect(rectF, radius, radius, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas1.drawBitmap(resizedBitmap, rect, rect, paint)
                albumView.setImageDrawable(newBitmap.toDrawable(mContext.resources))

                // Capture width & height from views in foreground for artwork scaling in background
                val width: Int
                val height: Int
                if (mediaBg.measuredWidth == 0 || mediaBg.measuredHeight == 0) {
                    if (lastWidth == 0 || lastHeight == 0) {
                        width = artworkLayer.intrinsicWidth
                        height = artworkLayer.intrinsicHeight
                    } else {
                        width = lastWidth
                        height = lastHeight
                    }
                } else {
                    width = mediaBg.measuredWidth
                    height = mediaBg.measuredHeight
                    lastWidth = width
                    lastHeight = height
                }
//                if (width == 0 || height == 0) {
//                    Trace.endAsyncSection(traceName, traceCookie)
//                    return@after
//                }

                val packageName = data.current().field {
                    name = "packageName"
                }.string()
                val mPrevTextPrimaryColorStateList = ColorStateList.valueOf(mCurrentTextPrimaryColor)
                titleText.setTextColor(mCurrentTextPrimaryColor)
                artistText.setTextColor(mCurrentTextPrimaryColor)
                seamlessIcon.imageTintList = mPrevTextPrimaryColorStateList
                action0.imageTintList = mPrevTextPrimaryColorStateList
                action1.imageTintList = mPrevTextPrimaryColorStateList
                action2.imageTintList = mPrevTextPrimaryColorStateList
                action3.imageTintList = mPrevTextPrimaryColorStateList
                action4.imageTintList = mPrevTextPrimaryColorStateList
                actionNext.imageTintList = mPrevTextPrimaryColorStateList
                actionPlayPause.imageTintList = mPrevTextPrimaryColorStateList
                actionPrev.imageTintList = mPrevTextPrimaryColorStateList
                seekBar.thumb.setTintList(mPrevTextPrimaryColorStateList)
                seekBar.progressTintList = mPrevTextPrimaryColorStateList
                scrubbingElapsedTimeView.setTextColor(mCurrentTextPrimaryColor)
                scrubbingTotalTimeView.setTextColor(mCurrentTextPrimaryColor)
                elapsedTimeView.setTextColor(mCurrentTextPrimaryColor)
                totalTimeView.setTextColor(mCurrentTextPrimaryColor)

                AsyncTask.THREAD_POOL_EXECUTOR.execute {
                    // Album art
                    val mutableColorScheme: Any?
                    val artwork: Drawable?
                    val isArtworkBound: Boolean
                    val wallpaperColors = this.instance.current().method {
                        name = "getWallpaperColor"
                        superClass()
                    }.call(artworkIcon) as? WallpaperColors?
                    if (wallpaperColors != null) {
                        mutableColorScheme = colorSchemeClass.constructor {
                            paramCount = 3
                        }.get().call(wallpaperColors, true, contentStyle)
                        artwork = this.instance.current().method {
                            name = "getScaledBackground"
                            superClass()
                        }.call(artworkIcon, height, height) as Drawable? ?: Color.TRANSPARENT.toDrawable()
                        isArtworkBound = true
                    } else {
                        // If there's no artwork, use colors from the app icon
                        artwork = Color.TRANSPARENT.toDrawable()
                        isArtworkBound = false
                        try {
                            val icon = mContext.packageManager.getApplicationIcon(packageName)
                            mutableColorScheme = colorSchemeClass.constructor {
                                paramCount = 3
                            }.get().call(WallpaperColors.fromDrawable(icon), true, contentStyle)
                        } catch (_: Exception) {
                            YLog.warn("application not found!")
                            Trace.endAsyncSection(traceName, traceCookie)
                            return@execute
                        }
                    }
                    var textPrimary = Color.WHITE
                    var textSecondary = Color.BLACK
                    var bgStartColor = Color.BLACK
                    var bgEndColor = Color.BLACK
                    var colorSchemeChanged = false
                    if (mutableColorScheme != null) {
                        val neutral1 = mutableColorScheme.current().field {
                            name = "mNeutral1"
                        }.any()!!.current().field {
                            name = "allShades"
                        }.list<Int?>()
                        val neutral2 = mutableColorScheme.current().field {
                            name = "mNeutral2"
                        }.any()!!.current().field {
                            name = "allShades"
                        }.list<Int?>()
                        val accent1 = mutableColorScheme.current().field {
                            name = "mAccent1"
                        }.any()!!.current().field {
                            name = "allShades"
                        }.list<Int?>()
                        val accent2 = mutableColorScheme.current(true).field {
                            name = "mAccent2"
                        }.any()!!.current().field {
                            name = "allShades"
                        }.list<Int?>()
                        textPrimary = neutral1[1]!!
                        textSecondary = neutral2[3]!!
                        bgStartColor = accent2[9]!!
                        bgEndColor = accent1[9]!!
                        colorSchemeChanged = textPrimary != mPrevTextPrimaryColor
                        mPrevTextPrimaryColor = textPrimary
                    }
                    val blurredBitmap = RadialGradientDrawable(artwork, bgStartColor, bgEndColor).toBitmap().hardwareBlur(height.toFloat() / 100 * blurRadius)

                    mediaBg.post(Runnable {
                        if (reqId < mArtworkBoundId) {
                            Trace.endAsyncSection(traceName, traceCookie)
                            return@Runnable
                        }
                        mArtworkBoundId = reqId

                        if (colorSchemeChanged) {
                            if (useAnim) {
                                if (animatingColorTransition == null) {
                                    animatingColorTransition = AnimatingColorTransition(applyColor = {
                                        mCurrentTextPrimaryColor = it
                                        val currentColorStateList = ColorStateList.valueOf(it)
                                        titleText.setTextColor(it)
                                        artistText.setTextColor(it)
                                        seamlessIcon.imageTintList = currentColorStateList
                                        action0.imageTintList = currentColorStateList
                                        action1.imageTintList = currentColorStateList
                                        action2.imageTintList = currentColorStateList
                                        action3.imageTintList = currentColorStateList
                                        action4.imageTintList = currentColorStateList
                                        actionNext.imageTintList = currentColorStateList
                                        actionPlayPause.imageTintList = currentColorStateList
                                        actionPrev.imageTintList = currentColorStateList
                                        seekBar.thumb.setTintList(currentColorStateList)
                                        seekBar.progressTintList = currentColorStateList
                                        scrubbingElapsedTimeView.setTextColor(it)
                                        scrubbingTotalTimeView.setTextColor(it)
                                        elapsedTimeView.setTextColor(it)
                                        totalTimeView.setTextColor(it)
                                    })
                                }
                                animatingColorTransition!!.animateToNewColor(textPrimary)
                            } else {
                                mCurrentTextPrimaryColor = textPrimary
                                val textPrimaryColorStateList = ColorStateList.valueOf(textPrimary)
                                titleText.setTextColor(textPrimary)
                                artistText.setTextColor(textSecondary)
                                seamlessIcon.imageTintList = textPrimaryColorStateList
                                action0.imageTintList = textPrimaryColorStateList
                                action1.imageTintList = textPrimaryColorStateList
                                action2.imageTintList = textPrimaryColorStateList
                                action3.imageTintList = textPrimaryColorStateList
                                action4.imageTintList = textPrimaryColorStateList
                                actionNext.imageTintList = textPrimaryColorStateList
                                actionPlayPause.imageTintList = textPrimaryColorStateList
                                actionPrev.imageTintList = textPrimaryColorStateList
                                seekBar.thumb.setTintList(textPrimaryColorStateList)
                                seekBar.progressTintList = textPrimaryColorStateList
                                scrubbingElapsedTimeView.setTextColor(textPrimary)
                                scrubbingTotalTimeView.setTextColor(textPrimary)
                                elapsedTimeView.setTextColor(textPrimary)
                                totalTimeView.setTextColor(textPrimary)
                            }
                        }

                        val finalBackground = blurredBitmap.toDrawable(mContext.resources)
                        // Bind the album view to the artwork or a transition drawable
                        mediaBg.setPadding(0, 0, 0, 0)
                        if (updateBackground || (!mIsArtworkBound && isArtworkBound)) {
                            if (mPrevArtwork == null) {
                                mediaBg.setImageDrawable(finalBackground)
                            } else {
                                // Since we throw away the last transition, this'll pop if you backgrounds
                                // are cycled too fast (or the correct background arrives very soon after
                                // the metadata changes).
                                val transitionDrawable = TransitionDrawable(
                                    arrayOf(mPrevArtwork!!, finalBackground)
                                )
                                scaleTransitionDrawableLayer(transitionDrawable, 0, width, height)
                                scaleTransitionDrawableLayer(transitionDrawable, 1, width, height)
                                transitionDrawable.setLayerGravity(0, Gravity.CENTER)
                                transitionDrawable.setLayerGravity(1, Gravity.CENTER)
                                transitionDrawable.isCrossFadeEnabled = !isArtworkBound

                                mediaBg.setImageDrawable(transitionDrawable)
                                transitionDrawable.startTransition(if (isArtworkBound) 333 else 80)
                            }
                            mPrevArtwork = finalBackground
                            mIsArtworkBound = isArtworkBound
                        }
                        Trace.endAsyncSection(traceName, traceCookie)
                    })
                }
            }
        }
    }
}