/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.MiBlurUtils
import kotlin.math.abs
import kotlin.math.pow

class MiBlurView(context: Context): View(context) {
    companion object {
        const val DEFAULT_ANIM_DURATION = 250
//        const val DEFAULT_BLUR_THRESHOLD = 10
        const val DEFAULT_BLUR_ENABLED = true
        const val DEFAULT_BLUR_MAX_RADIUS = 100
        const val DEFAULT_DIM_ENABLED = false
        const val DEFAULT_DIM_MAX_ALPHA = 64
        const val DEFAULT_NONLINEAR_ENABLED = false
        const val DEFAULT_NONLINEAR_FACTOR = 1.0f
    }

    private var mainAnimator: ValueAnimator? = null
    private var animCurrentRatio = 0.0f
    private var animCount = 0
    private var allowRestoreDirectly = false
    private var isBlurInitialized = false
//    private var dimThresholdAlpha = (DEFAULT_DIM_MAX_ALPHA * DEFAULT_BLUR_THRESHOLD.toFloat() / DEFAULT_BLUR_MAX_RADIUS).toInt()
    // Personalized Configurations
    private var blurEnabled = DEFAULT_BLUR_ENABLED
    private var blurMaxRadius = DEFAULT_BLUR_MAX_RADIUS
    private var dimEnabled = DEFAULT_DIM_ENABLED
    private var dimMaxAlpha = DEFAULT_DIM_MAX_ALPHA
    private var nonlinearEnabled = DEFAULT_NONLINEAR_ENABLED
    private var nonlinearFactor = DEFAULT_NONLINEAR_FACTOR

    init {
        this.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.setBackgroundColor(Color.TRANSPARENT)
        this.visibility = GONE
    }

    fun setBlur(useBlur: Boolean, maxRadius: Int) {
        blurEnabled = useBlur
        blurMaxRadius = maxRadius
        if (blurMaxRadius <= 0) {
            blurEnabled = false
        }
//        if (blurEnabled && dimEnabled) {
//            dimThresholdAlpha = (dimMaxAlpha * DEFAULT_BLUR_THRESHOLD.toFloat() / blurMaxRadius).toInt()
//        }
    }

    fun setDim(useDim: Boolean, maxAlpha: Int) {
        dimEnabled = useDim
        dimMaxAlpha = maxAlpha
        if (maxAlpha <= 0) {
            dimEnabled = false
        }
//        if (blurEnabled && dimEnabled) {
//            dimThresholdAlpha = (dimMaxAlpha * DEFAULT_BLUR_THRESHOLD.toFloat() / blurMaxRadius).toInt()
//        }
    }

    fun setNonlinear(useNonlinear: Boolean, factor: Float) {
        nonlinearEnabled = useNonlinear
        nonlinearFactor = factor
    }

    fun show(useAnim: Boolean, targetRatio: Float = 1.0f) {
        this.visibility = VISIBLE
        applyBlur(targetRatio, useAnim)
    }

    fun hide(useAnim: Boolean, targetRatio: Float = 0.0f) {
        this.visibility = VISIBLE
        applyBlur(targetRatio, useAnim)
    }

    fun restore(directly: Boolean = false) {
        this.visibility = VISIBLE
        if (!directly) {
            applyBlur(animCurrentRatio, false)
        }
        else if (allowRestoreDirectly) {
            allowRestoreDirectly = false
            if (blurEnabled && !isBlurInitialized) {
                initBlur()
            }
            applyBlurDirectly(animCurrentRatio)
        }
    }

    fun showWithDuration(useAnim: Boolean, targetRatio: Float, duration: Int) {
        this.visibility = VISIBLE
        applyBlur(targetRatio, useAnim, duration)
    }

    private fun applyBlur(ratio: Float, useAnim: Boolean, duration: Int = DEFAULT_ANIM_DURATION) {
        val targetRatio = ratio.coerceIn(0.0f, 1.0f)
        if (mainAnimator?.isRunning == true) {
            mainAnimator?.cancel()
        }
        if (blurEnabled && !isBlurInitialized) {
            initBlur()
        }
        if (!useAnim || animCurrentRatio == targetRatio) {
            applyBlurDirectly(targetRatio)
        }
        else {
            val currentRatio = animCurrentRatio
            if (mainAnimator == null) {
                mainAnimator = ValueAnimator()
            }
            mainAnimator?.let {
                it.setFloatValues(currentRatio, targetRatio)
                it.duration = (abs(currentRatio - targetRatio) * duration).toLong()
                it.interpolator = LinearInterpolator()
                it.removeAllUpdateListeners()
                it.addUpdateListener { animator ->
                    animCount++
                    val animaValue = animator.animatedValue as Float
                    if ((animCount % 2 != 1 || animaValue == currentRatio) && animaValue != targetRatio) {
                        return@addUpdateListener
                    }
                    applyBlurDirectly(
                        if (nonlinearEnabled) { fakeInterpolator(animaValue) }
                        else { animaValue }
                    )
                }
//                it.addListener { animator ->
//                    animator.doOnEnd {
//                        mainAnimator = null
//                    }
//                }
                animCount = 0
                it.start()
            }
        }
    }

    private fun applyBlurDirectly(ratio: Float) {
        val blurRadius = Math.linearInterpolate(0, blurMaxRadius, ratio)
        if (blurEnabled) {
            MiBlurUtils.setBlurRadius(this, blurRadius)
        }
        if (dimEnabled) {
            this.setBackgroundColor(
                Math.linearInterpolate(0, dimMaxAlpha, ratio).shl(24)
            )
        }
        animCurrentRatio = ratio
        allowRestoreDirectly = true
        if (ratio == 0.0f || blurRadius == 0) {
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
        MiBlurUtils.clearAllBlur(this)
        MiBlurUtils.setPassWindowBlurEnable(this, false)
        MiBlurUtils.setViewBackgroundBlur(this, MiBlurUtils.USAGE_BACKGROUND)
        MiBlurUtils.setViewBlur(this, 1)
        isBlurInitialized = true
    }

    private fun releaseBlur() {
        if (!isBlurInitialized) return
        isBlurInitialized = false
        this.visibility = GONE
        MiBlurUtils.clearAllBlur(this)
    }
}