package dev.lackluster.mihelper.hook.rules.systemui.media.bg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.drawable.RadialMaskedDrawable
import dev.lackluster.mihelper.hook.drawable.TransitionDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.hardwareBlur
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.max

class BlurredCoverProcessor : BgProcessor {
    private val blurRadius = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BLUR_RADIUS, 10).coerceIn(1, 20)
    private val useAnim = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.USE_ANIM, true)

    override fun convertToColorConfig(
        artwork: Drawable,
        neutral1: List<Int>,
        neutral2: List<Int>,
        accent1: List<Int>,
        accent2: List<Int>
    ): MediaViewColorConfig {
        return MediaViewColorConfig(
            neutral1[1],
            neutral2[3],
            accent2[9],
            accent1[9]
        )
    }

    override fun processAlbumCover(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig,
        context: Context,
        width: Int,
        height: Int
    ): Drawable {
        val bitmap = RadialMaskedDrawable(artwork, colorConfig.bgStartColor, colorConfig.bgEndColor)
            .toBitmap()
            .hardwareBlur(height.toFloat() / 100 * blurRadius)
        val finalSize = max(bitmap.width, bitmap.height)
        val newBitmap = createBitmap(finalSize, finalSize)
        val canvas = Canvas(newBitmap)
        val deltaW = (bitmap.width - finalSize) / 2f
        val deltaH = (bitmap.height - finalSize) / 2f
        canvas.drawBitmap(bitmap, -deltaW, -deltaH, Paint())
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
        return newBitmap.toDrawable(context.resources)
    }

    override fun createBackground(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig
    ): MediaControlBgDrawable {
        return TransitionDrawable(artwork, colorConfig, useAnim)
    }
}