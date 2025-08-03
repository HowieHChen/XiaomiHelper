package dev.lackluster.mihelper.hook.rules.systemui.media.bg

import android.content.Context
import android.graphics.drawable.Drawable
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.drawable.RadialGradientDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.toSquare
import dev.lackluster.mihelper.utils.Prefs

class RadialGradientProcessor : BgProcessor {
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
        return artwork.toSquare(context.resources, false, colorConfig.bgEndColor)
    }

    override fun createBackground(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig
    ): MediaControlBgDrawable {
        return RadialGradientDrawable(
            artwork,
            colorConfig,
            useAnim
        )
    }
}