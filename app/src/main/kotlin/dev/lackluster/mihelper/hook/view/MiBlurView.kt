package dev.lackluster.mihelper.hook.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.MiBlurUtils
import kotlin.math.abs
import kotlin.math.pow

class MiBlurView(context: Context) : FrameLayout(context) {
    companion object {
        const val DEFAULT_BLUR_ANIM_DURATION = 250
        const val DEFAULT_BLUR_MAX_RADIUS = 100
        const val DEFAULT_BLUR_THRESHOLD = 10
        const val DEFAULT_DIM_ALPHA = 0.2f
        const val DEFAULT_NONLINEAR_FACTOR = 1.0f
    }
    private var blurAnimator: ValueAnimator? = null
    private var blurCurrentRatio = 0.0f
    private var blurCount = 0
    private var allowRestoreDirectly = false
    private var isBlurInitialized = false
    // Two layer
    private var blurLayer : FrameLayout
    private var maskLayer : FrameLayout? = null
    // Personalized Configurations
    private var maxBlurRadius = DEFAULT_BLUR_MAX_RADIUS
    private var useDimLayer = false
    private var dimAlpha = DEFAULT_DIM_ALPHA
    private var useNonlinear = false
    private var nonlinearFactor = DEFAULT_NONLINEAR_FACTOR

    init {
        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        blurLayer = FrameLayout(context)
        blurLayer.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        blurLayer.background = ColorDrawable(Color.BLACK)
        this.addView(blurLayer)
        this.visibility = View.GONE
    }

    fun setBlurLayer(maxBlurRadius: Int) {
        this.maxBlurRadius = maxBlurRadius
    }

    fun setDimLayer(useDimLayer: Boolean, dimAlpha: Float) {
        this.useDimLayer = useDimLayer
        this.dimAlpha = dimAlpha
        if (useDimLayer && maskLayer == null) {
            maskLayer = FrameLayout(context)
            maskLayer?.let {
                it.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                it.background = ColorDrawable(Color.BLACK)
                this.addView(it)
            }
        }
    }

    fun setNonlinear(useNonlinear: Boolean, nonlinearFactor: Float) {
        this.useNonlinear = useNonlinear
        this.nonlinearFactor = nonlinearFactor
    }

    fun show(useAnim: Boolean, targetRatio: Float = 1.0f) {
        this.visibility = View.VISIBLE
        blurWithMiBlur(targetRatio, useAnim)
    }

    fun hide(useAnim: Boolean, targetRatio: Float = 0.0f) {
        this.visibility = View.VISIBLE
        blurWithMiBlur(targetRatio, useAnim)
    }

    fun restore(directly: Boolean = false) {
        this.visibility = View.VISIBLE
        if (!directly) {
            blurWithMiBlur(blurCurrentRatio, false)
        }
        else if (allowRestoreDirectly) {
            allowRestoreDirectly = false
            blurWithMiBlurDirectly(blurCurrentRatio)
        }
    }

    fun showWithDuration(useAnim: Boolean, targetRatio: Float, duration: Int) {
        this.visibility = View.VISIBLE
        blurWithMiBlur(targetRatio, useAnim, duration)
    }

    private fun blurWithMiBlur(ratio: Float, useAnim: Boolean, duration: Int = DEFAULT_BLUR_ANIM_DURATION) {
        val targetRatio = ratio.coerceIn(0.0f, 1.0f)
        if (blurAnimator?.isRunning == true) {
            blurAnimator?.cancel()
        }
        if (!isBlurInitialized) {
            initBlur()
        }
        if (!useAnim || blurCurrentRatio == targetRatio) {
            blurWithMiBlurDirectly(targetRatio)
        }
        else {
            val currentRatio = blurCurrentRatio
            if (blurAnimator == null) {
                blurAnimator = ValueAnimator()
            }
            blurAnimator?.let {
                it.setFloatValues(currentRatio, targetRatio)
                it.duration = (abs(currentRatio - targetRatio) * duration).toLong()
                it.interpolator = LinearInterpolator()
                it.removeAllUpdateListeners()
                it.addUpdateListener { animator ->
                    blurCount++
                    val animaValue = animator.animatedValue as Float
                    if ((blurCount % 2 != 1 || animaValue == currentRatio) && animaValue != targetRatio) {
                        return@addUpdateListener
                    }
                    blurWithMiBlurDirectly(
                        if (useNonlinear) { fakeInterpolator(animaValue) }
                        else { animaValue }
                    )
                }
                it.addListener {
                    blurAnimator = null
                }
                blurCount = 0
                it.start()
            }
        }
    }

    private fun blurWithMiBlurDirectly(ratio: Float) {
        val blurRadius = Math.linearInterpolate(0, maxBlurRadius, ratio)
        if (blurRadius < DEFAULT_BLUR_THRESHOLD) {
            MiBlurUtils.setBlurRadius(blurLayer, DEFAULT_BLUR_THRESHOLD)
            maskLayer?.alpha = dimAlpha * DEFAULT_BLUR_THRESHOLD / maxBlurRadius
            this.alpha = Math.linearInterpolate(0.0f, 1.0f, blurRadius.toFloat() / DEFAULT_BLUR_THRESHOLD)
        }
        else {
            MiBlurUtils.setBlurRadius(blurLayer, blurRadius)
            maskLayer?.alpha = Math.linearInterpolate(0.0f, dimAlpha, ratio)
            this.alpha = 1.0f
        }
        blurCurrentRatio = ratio
        allowRestoreDirectly = true
        if (ratio == 0.0f) {
            releaseBlur()
        }
    }

    // Use a fake interpolator to make the animation consistent when reversed
    private fun fakeInterpolator(input: Float): Float {
        return if (nonlinearFactor == 1.0f) {
            1.0f - (1.0f - input) * (1.0f - input)
        } else {
            1.0f - (1.0f - input).pow(2 * nonlinearFactor)
        }
    }

    private fun initBlur() {
        if (isBlurInitialized) return
        MiBlurUtils.clearAllBlur(blurLayer)
        MiBlurUtils.setPassWindowBlurEnable(blurLayer, true)
        MiBlurUtils.setViewBackgroundBlur(blurLayer, 1)
        MiBlurUtils.setViewBlur(blurLayer, 3)
        isBlurInitialized = true
    }

    private fun releaseBlur() {
        if (!isBlurInitialized) return
        this.visibility = View.GONE
        MiBlurUtils.clearAllBlur(blurLayer)
        isBlurInitialized = false
    }
}