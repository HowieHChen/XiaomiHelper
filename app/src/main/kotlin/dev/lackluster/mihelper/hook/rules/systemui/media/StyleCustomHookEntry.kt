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

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.TransitionDrawable
import android.hardware.HardwareBuffer
import android.media.ImageReader
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.sqrt

object StyleCustomHookEntry : YukiBaseHooker() {
    // background: 0 -> Default; 1 -> Enhanced; 2 -> Advanced textures; 3 -> Blurred cover; 4 -> AndroidNewStyle; 5 -> AndroidOldStyle
    private val background = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val playerTwoCircleViewClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.PlayerTwoCircleView".toClass()
    }

    override fun onHook() {
        when (background) {
            1 -> loadHooker(EnhancedStyle)
            2 -> loadHooker(AdvancedTexturesStyle)
            3 -> loadHooker(BlurredCoverStyle)
            4 -> loadHooker(AndroidNewStyle)
            5 -> loadHooker(AndroidOldStyle)
            else -> return
        }
        playerTwoCircleViewClass.apply {
            method {
                name = "onDraw"
            }.hook {
                before {
                    (this.instance.current().field { name = "mPaint1" }.any() as Paint).alpha = 0
                    (this.instance.current().field { name = "mPaint2" }.any() as Paint).alpha = 0
                    this.instance.current().field { name = "mRadius" }.set(0.0f)
                    // this.result = null
                }
            }
            method {
                name = "setBackground"
            }.hook {
                before {
                    result = null
                }
            }
            method {
                name = "setPaintColor"
            }.hook {
                before {
                    result = null
                }
            }
        }
    }

    fun scaleTransitionDrawableLayer(
        transitionDrawable: TransitionDrawable, layer: Int, targetWidth: Int, targetHeight: Int
    ) {
        val drawable = transitionDrawable.getDrawable(layer) ?: return

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        if (width == 0 || height == 0 || targetWidth == 0 || targetHeight == 0) {
            return
        }
        val scale = if (width / height.toFloat() > targetWidth / targetHeight.toFloat()) {
            // Drawable is wider than target view, scale to match height
            targetHeight / height.toFloat()
        } else {
            // Drawable is taller than target view, scale to match width
            targetWidth / width.toFloat()
        }
        transitionDrawable.setLayerSize(layer, (scale * width).toInt(), (scale * height).toInt())
    }

    fun Bitmap.brightness(): Float {
        var totalBrightness = 0f
        val totalPixels = this.width * this.height

        for (x in 0 until this.width) {
            for (y in 0 until this.height) {
                val pixel = this.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val brightness =
                    sqrt(0.299f * red * red + 0.587f * green * green + 0.114f * blue * blue)
                totalBrightness += brightness
            }
        }

        return totalBrightness / totalPixels
    }

    fun Bitmap.hardwareBlur(radius: Float): Bitmap {
        val imageReader = ImageReader.newInstance(
            this.width, this.height,
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
        renderCanvas.drawBitmap(this, 0f, 0f, null)
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