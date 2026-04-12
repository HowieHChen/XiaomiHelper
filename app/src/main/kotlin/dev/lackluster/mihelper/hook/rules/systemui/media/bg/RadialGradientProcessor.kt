package dev.lackluster.mihelper.hook.rules.systemui.media.bg

import android.content.Context
import android.graphics.drawable.Drawable
import dev.lackluster.mihelper.hook.rules.systemui.media.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.drawable.RadialGradientDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.toSquare
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig

class RadialGradientProcessor(
    private val useAnim: Boolean = true
) : BgProcessor {

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