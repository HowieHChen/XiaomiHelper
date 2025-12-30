package dev.lackluster.mihelper.hook.rules.systemui.media.bg

import android.content.Context
import android.graphics.drawable.Drawable
import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig

interface BgProcessor {
    fun convertToColorConfig(
        artwork: Drawable,
        neutral1: List<Int>,
        neutral2: List<Int>,
        accent1: List<Int>,
        accent2: List<Int>
    ) : MediaViewColorConfig

    fun processAlbumCover(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig,
        context: Context,
        width: Int,
        height: Int
    ): Drawable

    fun createBackground(artwork: Drawable, colorConfig: MediaViewColorConfig): MediaControlBgDrawable
}