package dev.lackluster.mihelper.hook.drawable

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.SystemClock
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.MediaViewColorConfig
import dev.lackluster.mihelper.utils.Math
import kotlin.math.abs

class TransitionDrawable(
    artwork: Drawable,
    colorConfig: MediaViewColorConfig,
    useAnim: Boolean = true
) : MediaControlBgDrawable(artwork, colorConfig, useAnim) {
    private var sourceColor: Int = colorConfig.bgStartColor
    private var currentColor: Int = colorConfig.bgStartColor
    private var targetColor: Int = colorConfig.bgStartColor

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val newSize = abs(bounds.width() - bounds.height()) / 2
        if (currentSize == 0) {
            currentSize = newSize
        }
        sourceSize = currentSize
        targetSize = newSize
        resizeState = AnimationState.STARTING
        invalidateSelf()
    }

    override fun draw(p0: Canvas) {
        val bounds = bounds
        if (bounds.isEmpty) return
        val skipAnim = !useAnim || skipAnimOnce
        if (skipAnimOnce) {
            skipAnimOnce = false
        }
        val now = SystemClock.elapsedRealtime()
        var alpha = 255
        when (albumState) {
            AnimationState.STARTING -> {
                if (skipAnim) {
                    albumState = AnimationState.DONE
                    currentColor = targetColor
                    artwork = nextArtwork ?: artwork
                    nextArtwork = null
                    alpha = 255
                } else {
                    albumStartTimeMillis = now
                    albumState = AnimationState.RUNNING
                }
            }
            AnimationState.RUNNING -> {
                if (albumStartTimeMillis >= 0) {
                    val normalized: Float = ((now - albumStartTimeMillis) / albumDuration.toFloat()).coerceIn(0.0f, 1.0f)
                    currentColor = argbEvaluator.evaluate(normalized, sourceColor, targetColor) as Int
                    alpha = Math.linearInterpolate(0, 255, normalized)
                    if (normalized >= 1.0f || skipAnim) {
                        albumState = AnimationState.DONE
                        currentColor = targetColor
                        artwork = nextArtwork ?: artwork
                        nextArtwork = null
                        alpha = 255
                    }
                }
            }
            else -> {}
        }
        when (resizeState) {
            AnimationState.STARTING -> {
                if (skipAnim) {
                    resizeState = AnimationState.DONE
                    currentSize = targetSize
                } else {
                    resizeStartTimeMillis = now
                    resizeState = AnimationState.RUNNING
                }
            }
            AnimationState.RUNNING -> {
                if (resizeStartTimeMillis >= 0) {
                    val normalized: Float = ((now - resizeStartTimeMillis) / resizeDuration.toFloat()).coerceIn(0.0f, 1.0f)
                    currentSize = Math.linearInterpolate(sourceSize, targetSize, normalized)
                    if (normalized >= 1.0f || skipAnim) {
                        resizeState = AnimationState.DONE
                        currentSize = targetSize
                    }
                }
            }
            else -> {}
        }
        background.color = currentColor
        background.setBounds(0, 0, bounds.width(), bounds.width())
        background.draw(p0)

        val drawBounds = Rect(0, -currentSize, bounds.width(), bounds.width() - currentSize)

        if (alpha == 0 || alpha == 255) {
            artwork.bounds = drawBounds
            artwork.draw(p0)
        } else {
            artwork.bounds = drawBounds
            artwork.alpha = 255 - alpha
            artwork.draw(p0)
            artwork.alpha = 255
            nextArtwork?.let {
                it.bounds = drawBounds
                it.alpha = alpha
                it.draw(p0)
                it.alpha = 255
            }
        }
        if (albumState != AnimationState.DONE || resizeState != AnimationState.DONE) {
            invalidateSelf()
        }
    }

    override fun updateAlbumCover(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig,
        skipAnim: Boolean
    ) {
        nextArtwork = artwork
        sourceColor = currentColor
        targetColor = colorConfig.bgStartColor
        albumState = AnimationState.STARTING
        this.colorConfig = colorConfig
        skipAnimOnce = skipAnim
        invalidateSelf()
    }
}