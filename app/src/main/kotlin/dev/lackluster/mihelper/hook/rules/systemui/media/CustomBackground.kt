package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.hardware.HardwareBuffer
import android.media.ImageReader
import androidx.core.graphics.get
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object CustomBackground : YukiBaseHooker() {
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle
    private val backgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    val useAnim = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.USE_ANIM, true)
    val allowReverse = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE, false)
    val blurRadius = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BLUR_RADIUS, 10).coerceIn(1, 20)

    val miuiMediaControlPanelClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClassOrNull()
    }
    val miuiMediaViewControllerImplClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl".toClassOrNull()
    }
    val bindMethod by lazy {
        miuiMediaControlPanelClass?.method {
            name = "bindPlayer"
        }?.give() ?: miuiMediaViewControllerImplClass?.method {
            name = "bindMediaData"
        }?.give()
    }
    val bindIsArtworkUpdateField by lazy {
        miuiMediaControlPanelClass?.field {
            name = "mIsArtworkUpdate"
            superClass()
        }?.give() ?: miuiMediaViewControllerImplClass?.field {
            name = "isArtWorkUpdate"
        }?.give()
    }
    val bindMediaViewHolderField by lazy {
        miuiMediaControlPanelClass?.field {
            name = "mMediaViewHolder"
            superClass()
        }?.give() ?: miuiMediaViewControllerImplClass?.field {
            name = "holder"
        }?.give()
    }
    val bindContextField by lazy {
        miuiMediaControlPanelClass?.field {
            name = "mContext"
            superClass()
        }?.give() ?: miuiMediaViewControllerImplClass?.field {
            name = "context"
        }?.give()
    }
    val colorSchemeClass by lazy {
        "com.android.systemui.monet.ColorScheme".toClass()
    }
    val contentStyle by lazy {
        "com.android.systemui.monet.Style".toClass().enumConstants?.get(6)
    }
    val colorSchemeConstructor1 by lazy {
        colorSchemeClass.constructor {
            paramCount = 3
        }.give()
    }
    val colorSchemeConstructor2 by lazy {
        colorSchemeClass.constructor {
            paramCount = 2
        }.give()
    }
    val mNeutral1Field by lazy {
        colorSchemeClass.field {
            name = "mNeutral1"
        }.remedys {
            field {
                name = "neutral1"
            }
        }.give()
    }
    val mNeutral2Field by lazy {
        colorSchemeClass.field {
            name = "mNeutral2"
        }.remedys {
            field {
                name = "neutral2"
            }
        }.give()
    }
    val mAccent1Field by lazy {
        colorSchemeClass.field {
            name = "mAccent1"
        }.remedys {
            field {
                name = "accent1"
            }
        }.give()
    }
    val mAccent2Field by lazy {
        colorSchemeClass.field {
            name = "mAccent2"
        }.remedys {
            field {
                name = "accent2"
            }
        }.give()
    }

    override fun onHook() {
        if (backgroundStyle != 0) {
            var oldVersion = false
            miuiMediaControlPanelClass?.apply {
                method {
                    name = "setPlayerBg"
                }.ignored().onNoSuchMethod {
                    oldVersion = true
                }.hook {
                    intercept()
                }
                method {
                    name = "setForegroundColors"
                }.ignored().onNoSuchMethod {
                    oldVersion = true
                }.hook {
                    intercept()
                }
            }
            miuiMediaViewControllerImplClass?.apply {
                method {
                    name = "updateForegroundColors"
                }.hook {
                    intercept()
                }
                method {
                    name = "updateMediaBackground"
                }.hook {
                    intercept()
                }
            }
            "com.android.systemui.media.controls.ui.controller.MediaViewController".toClassOrNull()?.apply {
                method {
                    name = "resetLayoutResource"
                }.ignored().hook {
                    intercept()
                }
            }
            if (oldVersion) {
                "com.android.systemui.statusbar.notification.mediacontrol.PlayerTwoCircleView".toClassOrNull()?.apply {
                    constructor {
                        paramCount = 4
                    }.ignored().hook {
                        after {
                            this.instance.current().field { name = "mPaint1" }.cast<Paint>()?.alpha = 0
                            this.instance.current().field { name = "mPaint2" }.cast<Paint>()?.alpha = 0
                            this.instance.current().field { name = "mRadius" }.set(0.0f)
                        }
                    }
                    method {
                        name = "setBackground"
                    }.ignored().hook {
                        before {
                            this.result = null
                        }
                    }
                    method {
                        name = "setPaintColor"
                    }.ignored().hook {
                        before {
                            this.result = null
                        }
                    }
                }
            }
        }
        when (backgroundStyle) {
            1 -> loadHooker(CoverArtStyle)
            2 -> loadHooker(BlurredCoverStyle)
            3 -> loadHooker(RadialGradientStyle)
            4 -> loadHooker(LinearGradientStyle)
        }

    }

//    fun scaleTransitionDrawableLayer(
//        transitionDrawable: TransitionDrawable, layer: Int, targetWidth: Int, targetHeight: Int
//    ) {
//        val drawable = transitionDrawable.getDrawable(layer) ?: return
//
//        val width = drawable.intrinsicWidth
//        val height = drawable.intrinsicHeight
//        if (width == 0 || height == 0 || targetWidth == 0 || targetHeight == 0) {
//            return
//        }
//        val scale = if (width / height.toFloat() > targetWidth / targetHeight.toFloat()) {
//            // Drawable is wider than target view, scale to match height
//            targetHeight / height.toFloat()
//        } else {
//            // Drawable is taller than target view, scale to match width
//            targetWidth / width.toFloat()
//        }
//        transitionDrawable.setLayerSize(layer, (scale * width).toInt(), (scale * height).toInt())
//    }

    fun Context.getWallpaperColor(icon: Icon?): WallpaperColors? {
        val iconType = icon?.type ?: return null
        if (iconType != Icon.TYPE_BITMAP && iconType != Icon.TYPE_ADAPTIVE_BITMAP) {
            val drawable = icon.loadDrawable(this) ?: return null
            return WallpaperColors.fromDrawable(drawable)
        } else {
            val bitmap = icon.current().method {
                name = "getBitmap"
            }.invoke<Bitmap>()
            return if (bitmap?.isRecycled == false) {
                WallpaperColors.fromBitmap(bitmap)
            } else {
                null
            }
        }
    }

    fun Context.getScaledBackground(icon: Icon?, width: Int, height: Int): Drawable? {
        val loadDrawable = icon?.loadDrawable(this) ?: return null
        val rect = Rect(0, 0, width, height)
        if (rect.width() > width || rect.height() > height) {
            rect.offset(
                ((width - rect.width()) / 2.0f).toInt(),
                ((height - rect.height()) / 2.0f).toInt()
            )
        }
        loadDrawable.bounds = rect
        return loadDrawable
    }

    fun Bitmap.brightness(): Float {
        var totalBrightness = 0f
        val totalPixels = this.width * this.height / 25

        for (x in 0 until this.width / 5) {
            for (y in 0 until this.height step 5) {
                val pixel = this[x, y]
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val brightness =
                    0.299f * red + 0.587f * green + 0.114f * blue
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