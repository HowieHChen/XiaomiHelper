package dev.lackluster.mihelper.hook.drawable

import android.animation.ArgbEvaluator
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig

abstract class MediaControlBgDrawable(
    protected var artwork: Drawable,
    protected var colorConfig: MediaViewColorConfig,
    protected val useAnim: Boolean = true
) : Drawable() {
    protected var background: ColorDrawable = ColorDrawable()
    protected val argbEvaluator = ArgbEvaluator()
    protected var nextArtwork: Drawable? = null

    protected var albumState: AnimationState = AnimationState.DONE
    protected var albumStartTimeMillis: Long = 0
    protected val albumDuration = 333L

    protected var resizeState: AnimationState = AnimationState.DONE
    protected var resizeStartTimeMillis: Long = 0
    protected val resizeDuration = 234L
    protected var sourceSize: Int = 0
    protected var currentSize: Int = 0
    protected var targetSize: Int = 0

    protected var skipAnimOnce: Boolean = false

    abstract fun updateAlbumCover(artwork: Drawable, colorConfig: MediaViewColorConfig, skipAnim: Boolean = false)

    override fun setAlpha(p0: Int) {
        artwork.alpha = p0
        background.alpha = p0
        invalidateSelf()
    }

    override fun setColorFilter(p0: ColorFilter?) {
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return background.opacity
    }
}