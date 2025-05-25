/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/systemui/controlcenter/MediaControlPanelBackugroundMix.kt#L505-L523>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.AsyncTask
import android.os.Trace
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaHookEntry.brightness
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaHookEntry.hardwareBlur
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode
import kotlin.random.Random
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.TransitionDrawable
import dev.lackluster.mihelper.utils.Prefs


object CoverArtStyle : YukiBaseHooker() {
    private val useAnim = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.USE_ANIM, true)
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
    private var mArtworkDrawable: TransitionDrawable? = null
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
                // mBackgroundExecutor.execute {
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
                    var backgroundPrimary = Color.BLACK
                    var colorSchemeChanged = false
                    if (mutableColorScheme != null) {
                        val accent1 = mutableColorScheme.current().field {
                            name = "mAccent1"
                        }.any()!!.current().field {
                            name = "allShades"
                        }.list<Int?>()
                        textPrimary = accent1[2]!!
                        backgroundPrimary = accent1[8]!!
                        colorSchemeChanged = textPrimary != mPrevTextPrimaryColor
                        mPrevTextPrimaryColor = textPrimary
                    }
                    // 获取 Bitmap
                    val artworkBitmapE = createBitmap(artwork.intrinsicWidth, artwork.intrinsicHeight)
                    val canvasE = Canvas(artworkBitmapE)
                    artwork.setBounds(0, 0, artwork.intrinsicWidth, artwork.intrinsicHeight)
                    artwork.draw(canvasE)

                    // 缩小图片
                    val tmpBitmap = artworkBitmapE.scale(132, 132)
                    val tmpBitmapXS = artworkBitmapE.scale(tmpBitmap.width / 2, tmpBitmap.height / 2)

                    // 创建混合图
                    val bigBitmap = createBitmap(tmpBitmap.width * 2, tmpBitmap.height * 2)
                    val canvasE2 = Canvas(bigBitmap)

                    // 生成随机图
                    val rotImages = mutableListOf<Bitmap>()
                    for (i in 1..5) {

                        // 中心点随机旋转 90°
                        val rotateMatrix = Matrix()
                        val pivotX = tmpBitmap.width / 2f
                        val pivotY = tmpBitmap.height / 2f
                        val rotationAngle = Random.nextInt(4) * 90f
                        rotateMatrix.postRotate(rotationAngle, pivotX, pivotY)

                        // 随机进行翻转和镜像
                        val flipHorizontal = Random.nextBoolean()
                        val flipVertical = Random.nextBoolean()
                        rotateMatrix.postScale(
                            if (flipHorizontal) -1f else 1f,
                            if (flipVertical) -1f else 1f,
                            pivotX,
                            pivotY
                        )

                        val rotatedImage = if (i <= 4) {
                            Bitmap.createBitmap(
                                tmpBitmap,
                                0,
                                0,
                                tmpBitmap.width,
                                tmpBitmap.height,
                                rotateMatrix,
                                true
                            )
                        } else {
                            Bitmap.createBitmap(
                                tmpBitmapXS,
                                0,
                                0,
                                tmpBitmapXS.width,
                                tmpBitmapXS.height,
                                rotateMatrix,
                                true
                            )
                        }
                        rotImages.add(rotatedImage)
                    }

                    // 将随机图绘制到混合大图上
                    canvasE2.drawBitmap(rotImages[0], 0f, 0f, null) // 左上角
                    canvasE2.drawBitmap(rotImages[1], tmpBitmap.width.toFloat(), 0f, null) // 右上角
                    canvasE2.drawBitmap(
                        rotImages[2],
                        0f,
                        tmpBitmap.height.toFloat(),
                        null
                    ) // 左下角
                    canvasE2.drawBitmap(
                        rotImages[3],
                        tmpBitmap.width.toFloat(),
                        tmpBitmap.height.toFloat(),
                        null
                    ) // 右下角
                    canvasE2.drawBitmap(
                        rotImages[4],
                        tmpBitmap.width / 4f * 3f,
                        tmpBitmap.height / 4f * 3f,
                        null
                    ) // 中心

                    // 颜色处理
                    val brightness = bigBitmap.brightness()
                    val colorMatrix = brightness.colorMatrix()
                    val paintE = Paint()
                    paintE.colorFilter = ColorMatrixColorFilter(colorMatrix)
                    canvasE2.drawBitmap(bigBitmap, 0f, 0f, paintE)
                    canvasE2.drawColor(backgroundPrimary and 0x6FFFFFFF)

                    val backgroundColorMode = if (mContext.isSystemInDarkMode) 0 else 248
                    val backgroundColor = Color.argb(
                        20, backgroundColorMode, backgroundColorMode, backgroundColorMode
                    )

                    // 应用颜色过滤器
                    val paintOverlay = Paint()
                    paintOverlay.colorFilter =
                        PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP)

                    // 叠加颜色
                    canvasE2.drawBitmap(bigBitmap, 0f, 0f, null)
                    canvasE2.drawColor(backgroundColor)

                    val finalBackground = bigBitmap.hardwareBlur(40.0f).toDrawable(mContext.resources)
                    if (mArtworkDrawable == null) {
                        mArtworkDrawable = TransitionDrawable(finalBackground, backgroundPrimary, useAnim)
                    }
                    mArtworkDrawable?.setBounds(0, 0, width, height)

                    mediaBg.postDelayed(Runnable {
                    // mMainExecutor.execute(Runnable {
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
                                artistText.setTextColor(textPrimary)
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

                        // Bind the album view to the artwork or a transition drawable
                        mediaBg.setPadding(0, 0, 0, 0)
                        if (updateBackground || colorSchemeChanged || (!mIsArtworkBound && isArtworkBound)) {
                            mediaBg.setImageDrawable(mArtworkDrawable)
                            mArtworkDrawable?.setNewAlbum(finalBackground, backgroundPrimary)
                            mIsArtworkBound = isArtworkBound
                        }
                        Trace.endAsyncSection(traceName, traceCookie)
                    }, 300L)
                }
            }
        }
    }

    private fun Float.colorMatrix(): ColorMatrix {
        val colorMatrix = ColorMatrix()
        val adjustment = when (this) {
            in 0.0f.rangeUntil(50.0f) -> 40.0f
            in 50.0f.rangeUntil(100.0f) -> 20.0f
            in 100.0f.rangeUntil(200.0f) -> -20.0f
            in 200.0f..255.0f -> -40.0f
            else -> 0f
        }
        colorMatrix.set(
            floatArrayOf(
                1f, 0f, 0f, 0f, adjustment, // red
                0f, 1f, 0f, 0f, adjustment, // green
                0f, 0f, 1f, 0f, adjustment, // blue
                0f, 0f, 0f, 1f, 0f          // alpha
            )
        )
        return colorMatrix
    }
}