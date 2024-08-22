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
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import android.os.Trace
import android.view.Gravity
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.hook.rules.systemui.media.StyleCustomHookEntry.brightness
import dev.lackluster.mihelper.hook.rules.systemui.media.StyleCustomHookEntry.hardwareBlur
import dev.lackluster.mihelper.hook.rules.systemui.media.StyleCustomHookEntry.scaleTransitionDrawableLayer
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode
import kotlin.random.Random


object EnhancedStyle : YukiBaseHooker() {
    private val miuiMediaControlPanelClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClass()
    }
    private val colorSchemeClass by lazy {
        "com.android.systemui.monet.ColorScheme".toClass()
    }
    private val contentStyle by lazy {
        "com.android.systemui.monet.Style".toClass().enumConstants[6]
    }
    private var mArtworkBoundId = 0
    private var mArtworkNextBindRequestId = 0
    private var mPrevArtwork: Drawable? = null
    private var mIsArtworkBound = false
    private var mPrevTextPrimaryColor = Color.WHITE
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
                val albumView = mMediaViewHolder.current(true).field { name = "albumView" }.any() as ImageView

                val artworkLayer = artworkIcon?.loadDrawable(mContext) ?: return@after
                val artworkBitmap = Bitmap.createBitmap(artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(artworkBitmap)
                artworkLayer.setBounds(0, 0, artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight)
                artworkLayer.draw(canvas)
                val resizedBitmap = Bitmap.createScaledBitmap(artworkBitmap, 300, 300, true)
                val radius = 45f
                val newBitmap = Bitmap.createBitmap(resizedBitmap.width, resizedBitmap.height, Bitmap.Config.ARGB_8888)
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
                albumView.setImageDrawable(BitmapDrawable(mContext.resources, newBitmap))

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
                            paramCount = 2
                        }.get().call(wallpaperColors, contentStyle)
                        artwork = this.instance.current().method {
                            name = "getScaledBackground"
                            superClass()
                        }.call(artworkIcon, height, height) as Drawable? ?: ColorDrawable(Color.TRANSPARENT)
                        isArtworkBound = true
                    } else {
                        // If there's no artwork, use colors from the app icon
                        artwork = ColorDrawable(Color.TRANSPARENT)
                        isArtworkBound = false
                        try {
                            val icon = mContext.packageManager.getApplicationIcon(packageName)
                            mutableColorScheme = colorSchemeClass.constructor {
                                paramCount = 2
                            }.get().call(WallpaperColors.fromDrawable(icon), contentStyle)
                        } catch (e: Exception) {
                            YLog.warn("application not found!")
                            Trace.endAsyncSection(traceName, traceCookie)
                            return@execute
                        }
                    }
                    var backgroundPrimary = Color.BLACK
                    var colorSchemeChanged = false
                    if (mutableColorScheme != null) {
                        val accent1 =
                            mutableColorScheme.current().field { name = "accent1" }.any()!!
                                .current().field {
                                    name = "allShades"
                                }.list<Int?>()
                        val textPrimary = accent1[2]!!
                        backgroundPrimary = accent1[8]!!
                        colorSchemeChanged = textPrimary != mPrevTextPrimaryColor
                        mPrevTextPrimaryColor = textPrimary
                    }
                    // 获取 Bitmap
                    val artworkBitmapE = Bitmap.createBitmap(
                        artwork.intrinsicWidth,
                        artwork.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvasE = Canvas(artworkBitmapE)
                    artwork.setBounds(0, 0, artwork.intrinsicWidth, artwork.intrinsicHeight)
                    artwork.draw(canvasE)

                    // 缩小图片
                    val tmpBitmap = Bitmap.createScaledBitmap(artworkBitmapE, 132, 132, true)
                    val tmpBitmapXS = Bitmap.createScaledBitmap(
                        artworkBitmapE,
                        tmpBitmap.width / 2,
                        tmpBitmap.height / 2,
                        true
                    )

                    // 创建混合图
                    val bigBitmap = Bitmap.createBitmap(
                        tmpBitmap.width * 2,
                        tmpBitmap.height * 2,
                        Bitmap.Config.ARGB_8888
                    )
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

                    val finalBackground = BitmapDrawable(mContext.resources, bigBitmap.hardwareBlur(40.0f))

                    mediaBg.postDelayed(Runnable {
                    // mMainExecutor.execute(Runnable {
                        if (reqId < mArtworkBoundId) {
                            Trace.endAsyncSection(traceName, traceCookie)
                            return@Runnable
                        }
                        mArtworkBoundId = reqId

                        // Bind the album view to the artwork or a transition drawable
                        mediaBg.setPadding(0, 0, 0, 0)
                        if (updateBackground || colorSchemeChanged || (!mIsArtworkBound && isArtworkBound)) {
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
                    }, 300L)
                }
            }
        }
    }

    private fun Float.colorMatrix(): ColorMatrix {
        val colorMatrix = ColorMatrix()
        val adjustment = when {
            this < 50 -> 40f
            this < 100 -> 20f
            this > 200 -> -40f
            this > 150 -> -20f
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