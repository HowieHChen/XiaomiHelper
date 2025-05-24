package dev.lackluster.mihelper.hook.drawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

class LinearGradientDrawable(
    private var artwork: Drawable,
    color: Int = Color.BLACK
) : Drawable() {
    private var gradient: GradientDrawable = GradientDrawable()
    private var background: ColorDrawable = ColorDrawable()
    private val argbEvaluator = ArgbEvaluator()
    private var backgroundAnimation: ValueAnimator
    private var sourceColor: Int = color
    private var currentColor: Int = color
    private var targetColor: Int = color
    private var updateFlag = 0
    private var nextArtwork: Drawable? = null
    private var changeArtworkFlag = false
    init {
        gradient.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        gradient.shape = GradientDrawable.RECTANGLE
        background.color = currentColor
        gradient.colors = intArrayOf(
            currentColor and 0x00ffffff or (51 shl 24),
            currentColor and 0x00ffffff or (51 shl 24)
        )
        backgroundAnimation =
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 333
                addUpdateListener {
                    if (updateFlag.mod(2) == 0) {
                        currentColor = argbEvaluator.evaluate(it.animatedFraction, sourceColor, targetColor) as Int
                        if (changeArtworkFlag && it.animatedFraction > 0.5f) {
                            artwork = nextArtwork ?: artwork
                            changeArtworkFlag = false
                        }
                        invalidateSelf()
                    }
                    updateFlag++
                }
                addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            currentColor = targetColor
                            updateFlag = 0
                            artwork = nextArtwork?: artwork
                            invalidateSelf()
                        }
                    }
                )
            }
    }
    override fun draw(p0: Canvas) {
        val bounds = bounds
        if (bounds.isEmpty) return
        artwork.setBounds(bounds.width() - bounds.height(), 0, bounds.width(), bounds.height())
        artwork.draw(p0)
        background.color = currentColor
        background.setBounds(0, 0, bounds.width()-bounds.height(), bounds.height())
        background.draw(p0)
        gradient.colors = intArrayOf(
            currentColor,
            currentColor and 0x00ffffff or (51 shl 24),
        )
        gradient.setBounds(bounds.width() - bounds.height(), 0, bounds.width(), bounds.height())
        gradient.draw(p0)
    }

    override fun setAlpha(p0: Int) {
        artwork.alpha = p0
        background.alpha = p0
        gradient.alpha = p0
        invalidateSelf()
    }

    override fun setColorFilter(p0: ColorFilter?) {
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return background.opacity
    }

    fun setNewAlbum(artwork: Drawable, color: Int) {
        nextArtwork = artwork
        changeArtworkFlag = true
        if (color != targetColor) {
            sourceColor = currentColor
            targetColor = color
        }
        backgroundAnimation.cancel()
        backgroundAnimation.start()
    }
}