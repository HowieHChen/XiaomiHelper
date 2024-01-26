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

package dev.lackluster.mihelper.hook.rules.systemui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.HardwareRenderer
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.roundToInt

object CustomMusicControl : YukiBaseHooker() {
    private val style = Prefs.getInt(PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE, 0)
    private var mediaArtwork: Icon? = null
//    private val meshFloats = arrayOf(
//        floatArrayOf(0.0f, 0.0f, 0.2f, 0.0f, 0.4f, 0.0f, 0.6f, 0.0f, 0.8f, 0.0f, 1.0f, 0.0f, 0.0f, 0.2f, -0.0933f, 0.4f, 0.4f, 0.2f, 0.6f, 0.2f, 0.3653f, 0.1335f, 1.0f, 0.2f, 0.0f, 0.4f, 0.4232f, 0.359f, 0.3429f, 0.5349f, 0.6f, 0.4f, 0.832f, 0.4148f, 1.0f, 0.4f, 0.0f, 0.6f, 0.2f, 0.6f, 0.2293f, 0.7775f, 0.7829f, 0.5595f, 0.6514f, 0.7302f, 1.0f, 0.6f, 0.0f, 0.8f, 0.2f, 0.8f, 0.28f, 0.9195f, 0.4773f, 0.8f, 0.8f, 0.8f, 1.0f, 0.8f, 0.0f, 1.0f, 0.6514f, 1.1073f, 0.4f, 1.0f, 1.0f, 1.0317f, 1.0f, 1.1302f, 1.0f, 1.0f),
//        floatArrayOf(0.0f, 0.0f, 0.2f, 0.0f, 0.4f, 0.0f, 0.6f, 0.0f, 0.8f, 0.0f, 1.0f, 0.0f, 0.0f, 0.2f, 0.3265f, 0.3839f, 0.4f, 0.2f, 0.462f, 0.3424f, 0.683f, 0.2797f, 1.0f, 0.2f, 0.0f, 0.4f, 0.2f, 0.4f, 0.4f, 0.4f, 0.6f, 0.4903f, 0.6574f, 0.4903f, 1.1357f, 0.4f, -0.1173f, 0.4597f, 0.3771f, 0.4384f, 0.6415f, 0.5947f, 0.8254f, 0.6935f, 0.9334f, 0.5862f, 1.0f, 0.6f, -0.0437f, 0.6533f, 0.2f, 0.6618f, 0.683f, 0.7362f, 0.8139f, 0.833f, 0.9104f, 0.8085f, 1.0f, 0.8f, 0.0f, 1.0f, 0.2f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f, 0.8f, 1.0f, 1.0f, 1.0f),
//        floatArrayOf(0.0f, 0.0f, 0.2f, 0.0f, 0.4f, 0.0f, 0.7465f, -0.0935f, 0.9702f, -0.0872f, 1.5935f, -0.0308f, -0.1675f, 0.2878f, 0.7185f, 0.3087f, 0.5952f, 0.0728f, 0.7823f, 0.0815f, 0.9318f, 0.301f, 1.1369f, 0.3756f, 0.0f, 0.4f, 0.3295f, 0.4607f, 0.7823f, 0.3087f, 0.7465f, 0.365f, 0.9514f, 0.4305f, 1.1514f, 0.4424f, 0.0f, 0.6f, 0.2f, 0.6f, 0.3295f, 0.4424f, 0.5703f, 0.5f, 0.7887f, 0.4847f, 1.0f, 0.6f, 0.0f, 0.8f, 0.2414f, 0.7926f, 0.0418f, 0.7303f, 0.5952f, 0.4688f, 0.9433f, 0.6929f, 1.0f, 0.8f, 0.0f, 1.0f, 0.2f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f, 0.8f, 1.0f, 1.0f, 1.0f),
//        floatArrayOf(-0.1739f, -0.0461f, 0.0712f, -0.0699f, 0.4773f, -0.0551f, 0.5871f, -0.0342f, 0.8f, 0.0f, 1.0f, 0.0f, -0.1192f, 0.0943f, 0.1034f, 0.0661f, 0.3712f, 0.1801f, 0.6161f, 0.2997f, 0.8f, 0.2f, 1.0f, 0.2f, -0.2158f, 0.2997f, 0.1034f, 0.1515f, 0.3712f, 0.2244f, 0.6676f, 0.3435f, 0.8f, 0.3911f, 1.2928f, 0.4824f, 0.0f, 0.6f, 0.4225f, 0.5539f, 0.8283f, 0.5345f, 0.6676f, 0.4601f, 0.9739f, 0.4542f, 1.4767f, 0.5345f, 0.0f, 0.8f, 0.2f, 0.8f, 0.6512f, 0.8179f, 0.6f, 0.8f, 1.2928f, 0.7271f, 1.6892f, 0.9235f, 0.0f, 1.0f, 0.2f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f, 0.8f, 1.0f, 1.0f, 1.0f),
//        floatArrayOf(-0.2351f, -0.0967f, 0.2135f, -0.1414f, 0.9221f, -0.0908f, 0.9221f, -0.0685f, 1.3027f, 0.0253f, 1.2351f, 0.1786f, -0.3768f, 0.1851f, 0.2f, 0.2f, 0.6615f, 0.3146f, 0.9543f, 0.0f, 0.6969f, 0.1911f, 1.0f, 0.2f, 0.0f, 0.4f, 0.2f, 0.4f, 0.0776f, 0.2318f, 0.6f, 0.4f, 0.6615f, 0.3851f, 1.0f, 0.4f, 0.0f, 0.6f, 0.1291f, 0.6f, 0.4f, 0.6f, 0.4f, 0.4304f, 0.4264f, 0.5792f, 1.2029f, 0.8188f, -0.1192f, 1.0f, 0.6f, 0.8f, 0.4264f, 0.8104f, 0.6f, 0.8f, 0.8f, 0.8f, 1.0f, 0.8f, 0.0f, 1.0f, 0.0776f, 1.0283f, 0.4f, 1.0f, 0.6f, 1.0f, 0.8f, 1.0f, 1.1868f, 1.0283f)
//    )
    override fun onHook() {
        if (style != 0) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClass()
                .method {
                    name = "bindPlayer"
                }
                .hook {
                    before {
                        mediaArtwork = (this.args(0).any() ?:return@before).current().field { name = "artwork" }.any() as Icon
                    }
                }
            "com.android.systemui.statusbar.notification.mediacontrol.PlayerTwoCircleView".toClass().apply {
                method {
                    name = "onDraw"
                }.hook {
                    before {
                        (this.instance.current().field { name = "mPaint1" }.any() as Paint).alpha = 0
                        (this.instance.current().field { name = "mPaint2" }.any() as Paint).alpha = 0
                        this.instance.current().field { name = "mRadius" }.set(0.0f)
                    }
                }
                method {
                    name = "setBackground"
                }.hook {
                    replaceUnit {
                        if (mediaArtwork == null) {
                            return@replaceUnit
                        }
                        val backgroundColors = this.args(0).any() as IntArray
                        val imageView = this.instance as ImageView
                        var artworkLayer = mediaArtwork?.loadDrawable(imageView.context) ?: return@replaceUnit
                        if (style == 1) {
                            val artworkBitmap = artworkLayer.toBitmap()
                            val scaledBitmap = Bitmap.createScaledBitmap(artworkBitmap, 30, 30, true)
                            val tmpBitmap = processArtwork(scaledBitmap, imageView.width, imageView.height)
                            imageView.setImageDrawable(BitmapDrawable(imageView.resources, tmpBitmap))
                            return@replaceUnit
                        }

                        val maskLayer = GradientDrawable()
                        maskLayer.setSize(300, 300)
                        maskLayer.shape = GradientDrawable.RECTANGLE
                        maskLayer.gradientType = GradientDrawable.RADIAL_GRADIENT
                        maskLayer.gradientRadius = 150f
                        if (style == 2) {
                            maskLayer.colors = intArrayOf(
                                backgroundColors[0] and 0x00ffffff or 0x40000000,
                                backgroundColors[1] // and 0x00ffffff or 0x7F000000
                            )
                        }
                        else if (style == 3){
                            val artworkBitmap = artworkLayer.toBitmap()
                            val tmpBitmap = Bitmap.createScaledBitmap(artworkBitmap, 300, 300, true)
                            val canvas = Canvas(tmpBitmap.copy(Bitmap.Config.ARGB_8888, true))
                            canvas.drawColor(0x7F000000)
                            artworkLayer = BitmapDrawable(imageView.resources, tmpBitmap.blur(15f, 2f))
                            maskLayer.colors = intArrayOf(
                                backgroundColors[0] and 0x00ffffff or 0x20000000,
                                backgroundColors[1] and 0x00ffffff or 0x7F000000
                            )
                        }
                        imageView.setImageDrawable(LayerDrawable(arrayOf(
                            artworkLayer,
                            maskLayer
                        )))
                    }
                }
                method {
                    name = "setPaintColor"
                }.hook {
                    replaceUnit {
                        if (mediaArtwork == null) {
                            return@replaceUnit
                        }
                        mediaArtwork = null
                    }
                }
            }
        }
    }
    private fun processArtwork(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val f = 24.0f
        val scaledWidth = (targetWidth * 1.3f / f).roundToInt()
        val scaledHeight = (targetHeight * 1.3f / f).roundToInt()
        val createBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(createBitmap)
        val maxSide = (maxOf(scaledWidth, scaledHeight) * 1.3f).roundToInt().toFloat()
        val scaleRatio = maxSide / bitmap.height
        val f2 = scaledWidth.toFloat()
        val f3 = (-(maxSide - f2)) / 2.0f
        val f4 = scaledHeight.toFloat()
        val f5 = (-(maxSide - f4)) / 2.0f

        val random = (0 .. 120).random()

        val floatValue = Math.linearInterpolate(0.0f, -360.0f, (random % 120).toFloat() / 120)
        val matrix = Matrix()
        matrix.setScale(scaleRatio, scaleRatio)
        val f6 = maxSide / 2.0f
        matrix.postRotate(floatValue, f6, f6)
        matrix.postTranslate(f3, f5)
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(2.5f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        val paint = Paint(7)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(bitmap, matrix, paint)

        val floatValue2 = Math.linearInterpolate(0.0f, 360.0f, (random % 90).toFloat() / 90)
        val matrix2 = Matrix()
        matrix2.setScale(scaleRatio, scaleRatio)
        matrix2.postRotate(floatValue2, f6, f6)
        matrix2.postTranslate(f3, f5)
        matrix2.postTranslate((-0.95f) * f2, f4 * (-0.7f))
        canvas.drawBitmap(bitmap, matrix2, paint)

        val floatValue3 = Math.linearInterpolate(0.0f, 360.0f, (random % 70).toFloat() / 70)
        val matrix3 = Matrix()
        matrix3.setScale(scaleRatio, scaleRatio)
        matrix3.postRotate(floatValue3, f6, f6)
        matrix3.postTranslate(f3, f5)
        matrix3.postTranslate((-0.5f) * f2, f4 * 0.7f)
        matrix3.postRotate(floatValue3, f2 / 2.0f, f4 / 2.0f)
        canvas.drawBitmap(bitmap, matrix3, paint)

        val paint2 = Paint(7)
        paint2.style = Paint.Style.FILL
        paint2.color = Color.parseColor("#4d000000") // 30% Black
        canvas.drawPaint(paint2)

        val paint3 = Paint(7)
        paint3.style = Paint.Style.FILL
        paint3.color = Color.parseColor("#1affffff") // 10% White
        canvas.drawPaint(paint3)

        val bitmap2 = createBitmap.blur(25f, 1f)
        val width = bitmap2.width.toFloat()
        val height2 = bitmap.height.toFloat()
        val matrix4 = Matrix()
        matrix4.setScale(f, f)
        val matrix5 = Matrix(matrix4)
        matrix5.preTranslate((-(width - (width / 1.3f))) / 2.0f, (-(height2 - (height2 / 1.3f))) / 2.0f)
        val createBitmap2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(createBitmap2)
        canvas2.drawBitmap(bitmap2, matrix5, Paint(7))
        return createBitmap2
    }

    private fun Bitmap.blur(radius: Float, ty: Float): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(
            this,
            (width / ty).toInt(), (height / ty).toInt(), false
        ) //先缩放图片，增加模糊速度

        val imageReader = ImageReader.newInstance(
            scaledBitmap.width, scaledBitmap.height,
            PixelFormat.RGBA_8888, 1,
            HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )
        val renderNode = RenderNode("BlurEffect")
        val hardwareRenderer = HardwareRenderer()

        hardwareRenderer.setSurface(imageReader.surface)
        hardwareRenderer.setContentRoot(renderNode)
        renderNode.setPosition(0, 0, imageReader.width, imageReader.height)
        val blurRenderEffect = RenderEffect.createBlurEffect(
            radius, radius,
            Shader.TileMode.MIRROR
        )
        renderNode.setRenderEffect(blurRenderEffect)

        val renderCanvas = renderNode.beginRecording()
        renderCanvas.drawBitmap(scaledBitmap, 0f, 0f, null)
        renderNode.endRecording()
        hardwareRenderer.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()

        val image = imageReader.acquireNextImage() ?: throw RuntimeException("No Image")
        val hardwareBuffer = image.hardwareBuffer ?: throw RuntimeException("No HardwareBuffer")
        val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
            ?: throw RuntimeException("Create Bitmap Failed")

        hardwareBuffer.close()
        image.close()
        imageReader.close()
        renderNode.discardDisplayList()
        hardwareRenderer.destroy()
        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }
}