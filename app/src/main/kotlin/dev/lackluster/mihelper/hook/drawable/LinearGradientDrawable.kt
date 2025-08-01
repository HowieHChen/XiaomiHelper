package dev.lackluster.mihelper.hook.drawable

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.MediaViewColorConfig
import dev.lackluster.mihelper.utils.Math
import kotlin.math.min


class LinearGradientDrawable(
    artwork: Drawable,
    colorConfig: MediaViewColorConfig,
    useAnim: Boolean = true
) : MediaControlBgDrawable(artwork, colorConfig, useAnim) {
    private var gradient: GradientDrawable = GradientDrawable()

    private var sourceColor: Int = colorConfig.bgStartColor
    private var currentColor: Int = colorConfig.bgStartColor
    private var targetColor: Int = colorConfig.bgStartColor

    init {
        gradient.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        gradient.shape = GradientDrawable.RECTANGLE
        background.color = currentColor
        gradient.colors = intArrayOf(
            currentColor and 0x00ffffff or (51 shl 24),
            currentColor and 0x00ffffff or (51 shl 24)
        )
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val newSize = min(bounds.width(), bounds.height())
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
        var alpha = 255
        when (albumState) {
            AnimationState.STARTING -> {
                albumStartTimeMillis = System.currentTimeMillis()
                albumState = AnimationState.RUNNING
            }
            AnimationState.RUNNING -> {
                if (albumStartTimeMillis >= 0) {
                    val normalized: Float = ((System.currentTimeMillis() - albumStartTimeMillis) / albumDuration.toFloat()).coerceIn(0.0f, 1.0f)
                    currentColor = argbEvaluator.evaluate(normalized, sourceColor, targetColor) as Int
                    alpha = Math.linearInterpolate(0, 255, normalized)
                    if (normalized >= 1.0f || !useAnim) {
                        albumState = AnimationState.DONE
                        currentColor = targetColor
                        artwork = nextArtwork ?: artwork
                        alpha = 255
                    }
                }
            }
            else -> {}
        }
        when (resizeState) {
            AnimationState.STARTING -> {
                resizeStartTimeMillis = System.currentTimeMillis()
                resizeState = AnimationState.RUNNING
            }
            AnimationState.RUNNING -> {
                if (resizeStartTimeMillis >= 0) {
                    val normalized: Float = ((System.currentTimeMillis() - resizeStartTimeMillis) / resizeDuration.toFloat()).coerceIn(0.0f, 1.0f)
                    currentSize = Math.linearInterpolate(sourceSize, targetSize, normalized)
                    if (normalized >= 1.0f || !useAnim) {
                        resizeState = AnimationState.DONE
                        currentSize = targetSize
                    }
                }
            }
            else -> {}
        }
        background.color = currentColor
        background.setBounds(0, 0, bounds.width(), bounds.height())
        background.draw(p0)
        if (alpha == 0 || alpha == 255) {
            artwork.setBounds(bounds.width() - currentSize, 0, bounds.width(), currentSize)
            artwork.draw(p0)
        } else {
            artwork.setBounds(bounds.width() - currentSize, 0, bounds.width(), currentSize)
            artwork.alpha = 255 - alpha
            artwork.draw(p0)
            artwork.alpha = 255
            nextArtwork?.let {
                it.setBounds(bounds.width() - currentSize, 0, bounds.width(), currentSize)
                it.alpha = alpha
                it.draw(p0)
                it.alpha = 255
            }
        }
        gradient.colors = intArrayOf(
            currentColor,
            currentColor and 0x00ffffff or (51 shl 24),
        )
        gradient.setBounds(bounds.width() - currentSize, 0, bounds.width(), bounds.height())
        gradient.draw(p0)
        if (albumState != AnimationState.DONE || resizeState != AnimationState.DONE) {
            invalidateSelf()
        }
    }

    override fun updateAlbumCover(
        artwork: Drawable,
        colorConfig: MediaViewColorConfig
    ) {
        nextArtwork = artwork
        sourceColor = currentColor
        targetColor = colorConfig.bgStartColor
        albumState = AnimationState.STARTING
        this.colorConfig = colorConfig
        invalidateSelf()
    }
}