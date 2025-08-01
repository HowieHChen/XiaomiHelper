package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.HardwareRenderer
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
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IconClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.MediaViewColorConfig

object MediaControlBgFactory : YukiBaseHooker() {
    val defaultColorConfig = MediaViewColorConfig(
        Color.WHITE,
        Color.WHITE,
        Color.BLACK,
        Color.BLACK
    )
    val PlayerTwoCircleViewClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.PlayerTwoCircleView".toClassOrNull()
    }
    val MiuiMediaControlPanelClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClassOrNull()
    }
    val MiuiMediaViewControllerImplClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl".toClassOrNull()
    }
    val ColorSchemeClass by lazy {
        "com.android.systemui.monet.ColorScheme".toClass()
    }
    val conColorScheme2 by lazy {
        ColorSchemeClass.constructor {
            paramCount = 2
        }.ignored().give()
    }
    val conColorScheme3 by lazy {
        ColorSchemeClass.constructor {
            paramCount = 3
        }.ignored().give()
    }
    val fldTonalPaletteAllShades by lazy {
        "com.android.systemui.monet.TonalPalette".toClass().field {
            name = "allShades"
        }.give()
    }
    val fldColorSchemeNeutral1 by lazy {
        ColorSchemeClass.field {
            name = "mNeutral1"
        }.remedys {
            field {
                name = "neutral1"
            }
        }.give()
    }
    val fldColorSchemeNeutral2 by lazy {
        ColorSchemeClass.field {
            name = "mNeutral2"
        }.remedys {
            field {
                name = "neutral2"
            }
        }.give()
    }
    val fldColorSchemeAccent1 by lazy {
        ColorSchemeClass.field {
            name = "mAccent1"
        }.remedys {
            field {
                name = "accent1"
            }
        }.give()
    }
    val fldColorSchemeAccent2 by lazy {
        ColorSchemeClass.field {
            name = "mAccent2"
        }.remedys {
            field {
                name = "accent2"
            }
        }.give()
    }
    val enumStyleContent by lazy {
        "com.android.systemui.monet.Style".toClass().method {
            name = "valueOf"
            param(StringClass)
            modifiers { isStatic }
        }.get().call("CONTENT")
    }

    private val metIconGetBitmap by lazy {
        IconClass?.method {
            name = "getBitmap"
        }?.give()
    }


    override fun onHook() {
        PlayerTwoCircleViewClass
        MiuiMediaControlPanelClass
        MiuiMediaViewControllerImplClass
        ColorSchemeClass
        conColorScheme2
        conColorScheme3
        fldTonalPaletteAllShades
        fldColorSchemeNeutral1
        fldColorSchemeNeutral2
        fldColorSchemeAccent1
        fldColorSchemeAccent2
        enumStyleContent
        metIconGetBitmap
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

    fun Context.getWallpaperColor(icon: Icon?): WallpaperColors? {
        val iconType = icon?.type ?: return null
        if (iconType != Icon.TYPE_BITMAP && iconType != Icon.TYPE_ADAPTIVE_BITMAP) {
            val drawable = icon.loadDrawable(this) ?: return null
            return WallpaperColors.fromDrawable(drawable)
        } else {
            val bitmap = metIconGetBitmap?.invoke(icon) as? Bitmap
            return if (bitmap?.isRecycled == false) {
                WallpaperColors.fromBitmap(bitmap)
            } else {
                null
            }
        }
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