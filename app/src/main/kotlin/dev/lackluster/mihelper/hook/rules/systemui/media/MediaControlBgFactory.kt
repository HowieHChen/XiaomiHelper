package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.get
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig
import java.util.WeakHashMap
import kotlin.math.max
import kotlin.math.min

object MediaControlBgFactory : YukiBaseHooker() {
    val defaultColorConfig = MediaViewColorConfig(
        Color.WHITE,
        Color.WHITE,
        Color.BLACK,
        Color.BLACK
    )
    val clzMediaData by lazy {
        "com.android.systemui.media.controls.shared.model.MediaData".toClassOrNull()
    }
    private val clzColorScheme by lazy {
        "com.android.systemui.monet.ColorScheme".toClass()
    }
    val ctorColorScheme by lazy {
        clzColorScheme.resolve().firstConstructorOrNull {
            parameterCount = 3
            parameters(WallpaperColors::class, Boolean::class, Int::class)
        }?.self
    }
    val fldTonalPaletteAllShades by lazy {
        "com.android.systemui.monet.TonalPalette".toClass().resolve().firstFieldOrNull {
            name = "allShades"
        }?.self
    }
    val fldColorSchemeNeutral1 by lazy {
        clzColorScheme.resolve().firstFieldOrNull {
            name = "mNeutral1"
        }?.self
    }
    val fldColorSchemeNeutral2 by lazy {
        clzColorScheme.resolve().firstFieldOrNull {
            name = "mNeutral2"
        }?.self
    }
    val fldColorSchemeAccent1 by lazy {
        clzColorScheme.resolve().firstFieldOrNull {
            name = "mAccent1"
        }?.self
    }
    val fldColorSchemeAccent2 by lazy {
        clzColorScheme.resolve().firstFieldOrNull {
            name = "mAccent2"
        }?.self
    }
    val fldColorSchemeAccent3 by lazy {
        clzColorScheme.resolve().firstFieldOrNull {
            name = "mAccent3"
        }?.self
    }
    val enumStyleContent by lazy {
        "com.android.systemui.monet.Style".toClass().resolve().firstMethodOrNull {
            name = "valueOf"
            parameters(String::class)
            modifiers(Modifiers.STATIC)
        }?.invoke("CONTENT")
    }

    private val metIconGetBitmap by lazy {
        Icon::class.resolve().firstMethodOrNull {
            name = "getBitmap"
        }?.self?.apply {
            isAccessible = true
        }
    }

    private val artworkColorMap = WeakHashMap<Icon, WallpaperColors>()

    override fun onHook() {
        clzColorScheme
        ctorColorScheme
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

    fun Context.getCachedWallpaperColor(icon: Icon?): WallpaperColors? {
        val iconType = icon?.type ?: return null
        if (iconType != Icon.TYPE_BITMAP && iconType != Icon.TYPE_ADAPTIVE_BITMAP) {
            val drawable = icon.loadDrawable(this) ?: return null
            val colors: WallpaperColors?
            synchronized(artworkColorMap) {
                colors = artworkColorMap.getOrPut(icon) {
                    WallpaperColors.fromDrawable(drawable)
                }
            }
            return colors
        } else {
            val bitmap = metIconGetBitmap?.invoke(icon) as? Bitmap
            if (bitmap?.isRecycled == false) {
                val colors: WallpaperColors?
                synchronized(artworkColorMap) {
                    colors = artworkColorMap.getOrPut(icon) {
                        WallpaperColors.fromBitmap(bitmap)
                    }
                }
                return colors
            } else {
                return null
            }
        }
    }

    fun releaseCachedWallpaperColor() {
        synchronized(artworkColorMap) {
            artworkColorMap.clear()
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

    fun Drawable.toSquare(resources: Resources, fill: Boolean, backgroundColor: Int): Drawable {
        if (intrinsicWidth == intrinsicHeight || intrinsicWidth <= 0 || intrinsicHeight <= 0) {
             return this
        } else {
            val finalSize =
                if (fill) min(intrinsicWidth, intrinsicHeight)
                else max(intrinsicWidth, intrinsicHeight)
            val bitmap = createBitmap(finalSize, finalSize)
            val canvas = Canvas(bitmap)
            canvas.drawColor(backgroundColor)
            val deltaW = (intrinsicWidth - finalSize) / 2
            val deltaH = (intrinsicHeight - finalSize) / 2
            this.setBounds(-deltaW, -deltaH, finalSize + deltaW, finalSize + deltaH)
            this.draw(canvas)
            return bitmap.toDrawable(resources)
        }
    }
}