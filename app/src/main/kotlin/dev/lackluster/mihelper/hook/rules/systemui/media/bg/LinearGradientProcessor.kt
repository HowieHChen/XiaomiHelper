package dev.lackluster.mihelper.hook.rules.systemui.media.bg

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.LinearGradientDrawable
import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.brightness
import dev.lackluster.mihelper.utils.Prefs

class LinearGradientProcessor : BgProcessor {
    private val allowReverse = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE, false)
    private val useAnim = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.USE_ANIM, true)

    override fun convertToColorConfig(
        artwork: Drawable,
        neutral1: List<Int>,
        neutral2: List<Int>,
        accent1: List<Int>,
        accent2: List<Int>
    ): MediaViewColorConfig {
        // 获取 Bitmap
        val artworkBitmapE = createBitmap(artwork.intrinsicWidth, artwork.intrinsicHeight)
        val canvasE = Canvas(artworkBitmapE)
        artwork.setBounds(0, 0, artwork.intrinsicWidth, artwork.intrinsicHeight)
        artwork.draw(canvasE)

        // 缩小图片
        val tmpBitmap = artworkBitmapE.scale(132, 132)
        val tmpBitmapXS = artworkBitmapE.scale(tmpBitmap.width / 2, tmpBitmap.height / 2)
        val textPrimary: Int
        val backgroundPrimary: Int
        if (allowReverse && tmpBitmapXS.brightness() >= 192) {
            textPrimary = accent1[8]
            backgroundPrimary = accent1[3]
        } else {
            textPrimary = accent1[2]
            backgroundPrimary = accent1[8]
        }
        return MediaViewColorConfig(
            textPrimary,
            textPrimary,
            backgroundPrimary,
            backgroundPrimary
        )
    }

    override fun processAlbumCover(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig,
        context: Context,
        width: Int,
        height: Int
    ): Drawable {
        return artwork
    }

    override fun createBackground(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig
    ): MediaControlBgDrawable {
        return LinearGradientDrawable(
            artwork,
            colorConfig,
            useAnim
        )
    }
}