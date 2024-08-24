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

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.MiBlurUtils
import dev.lackluster.mihelper.utils.MiBlurUtils.setBlurRoundRect
import kotlin.math.abs

class MiBlurView(context: Context): View(context) {
    companion object {
        const val DEFAULT_ANIM_DURATION = 250
        const val DEFAULT_BLUR_ENABLED = true
        const val DEFAULT_BLUR_MAX_RADIUS = 100
        const val DEFAULT_DIM_ENABLED = false
        const val DEFAULT_DIM_MAX_ALPHA = 64
        const val DEFAULT_SCALE_ENABLED = false
        const val DEFAULT_SCALE_MAX_RATIO = 0f
        const val DEFAULT_NONLINEAR_ENABLED = false
    }

    private var mainAnimator: ValueAnimator? = null
    private var animCurrentRatio = 0.0f
    private var animTargetRatio = 0.0f
    private var animCount = 0
    private var allowRestoreDirectly = false
    private var isBlurInitialized = false
    // Personalized Configurations
    private var blurEnabled = DEFAULT_BLUR_ENABLED
    private var blurMaxRadius = DEFAULT_BLUR_MAX_RADIUS
    private var dimEnabled = DEFAULT_DIM_ENABLED
    private var dimMaxAlpha = DEFAULT_DIM_MAX_ALPHA
    private var scaleEnabled = DEFAULT_SCALE_ENABLED
    private var scaleMaxRatio = DEFAULT_SCALE_MAX_RATIO
    private var nonlinearEnabled = DEFAULT_NONLINEAR_ENABLED
    private var nonlinearInterpolator: Interpolator = LinearInterpolator()
    private var passWindowBlurEnabled = false
    private val mAnimatorListener: AnimatorListener

    init {
        this.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.setBackgroundColor(Color.TRANSPARENT)
        this.visibility = GONE
        mAnimatorListener = object : AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animCount = 0
                initViewIfNeeded()
            }
            override fun onAnimationEnd(p0: Animator) {
                releaseViewIfNeeded()
            }
            override fun onAnimationCancel(p0: Animator) {
            }
            override fun onAnimationRepeat(p0: Animator) {
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.visibility = VISIBLE
        initViewIfNeeded()
        applyBlur(animCurrentRatio, false)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mainAnimator?.isRunning == true) {
            mainAnimator?.cancel()
        }
        releaseViewIfNeeded()
    }

    // Configure
    fun setPassWindowBlur(enabled: Boolean) {
        passWindowBlurEnabled = enabled
    }

    fun setBlur(useBlur: Boolean, maxRadius: Int) {
        blurEnabled = useBlur
        blurMaxRadius = maxRadius.coerceIn(0, 500)
        if (blurMaxRadius <= 0) {
            blurEnabled = false
        }
    }

    fun setDim(useDim: Boolean, maxAlpha: Int) {
        dimEnabled = useDim
        dimMaxAlpha = maxAlpha
        if (maxAlpha <= 0) {
            dimEnabled = false
        }
    }

    fun setScale(useScale: Boolean, maxRatio: Float) {
        scaleEnabled = useScale
        scaleMaxRatio = maxRatio.coerceIn(0.0f, 1.0f)
        if (scaleMaxRatio !in 0.0f..1.0f) {
            scaleEnabled = false
        }
    }

    fun setCornerRadius(radius: Int) {
        if (radius > 0) {
            setBlurRoundRect(radius)
        } else {
            clipToOutline = false
            outlineProvider = null
        }
    }

    fun setNonlinear(useNonlinear: Boolean, interpolator: Interpolator) {
        nonlinearEnabled = useNonlinear
        nonlinearInterpolator = interpolator
    }

    // Blur
    fun restore(directly: Boolean = false) {
        if (!directly) {
            applyBlur(animCurrentRatio, false)
        }
        else if (allowRestoreDirectly) {
            allowRestoreDirectly = false
            initViewIfNeeded()
            applyBlurDirectly(animCurrentRatio)
        }
    }

    fun setStatus(visible: Boolean, useAnim: Boolean = false) {
        applyBlur(
            if (visible) 1.0f else 0.0f,
            useAnim
        )
    }

    fun setStatus(targetRatio: Float, useAnim: Boolean = false) {
        applyBlur(targetRatio, useAnim)
    }

    fun setStatus(targetRatio: Float, useAnim: Boolean, duration: Int) {
        applyBlur(targetRatio, useAnim, duration)
    }

    private fun applyBlur(ratio: Float, useAnim: Boolean, duration: Int = DEFAULT_ANIM_DURATION) {
        val targetRatio = ratio.coerceIn(0.0f, 1.0f)
        if (mainAnimator?.isRunning == true) {
            mainAnimator?.cancel()
        }
        if (!useAnim || animCurrentRatio == targetRatio) {
            initViewIfNeeded()
            applyBlurDirectly(targetRatio)
        }
        else {
            val currentRatio = animCurrentRatio
            animTargetRatio = targetRatio
            if (mainAnimator == null) {
                mainAnimator = ValueAnimator().apply {
                    interpolator = LinearInterpolator()
                    addListener(mAnimatorListener)
                    addUpdateListener { animator ->
                        animCount++
                        val animaValue = animator.animatedValue as Float
                        if ((animCount % 2 != 1 || animaValue == animCurrentRatio) && animCurrentRatio != animTargetRatio) {
                            return@addUpdateListener
                        }
                        applyBlurDirectly(
                            if (nonlinearEnabled) { nonlinearInterpolator.getInterpolation(animaValue) }
                            else { animaValue }
                        )
                    }
                }
            }
            mainAnimator?.let {
                it.setFloatValues(currentRatio, targetRatio)
                it.duration = (abs(currentRatio - targetRatio) * duration).toLong()
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
        if (scaleEnabled) {
            MiBlurUtils.setBackgroundBlurScaleRatio(this, Math.linearInterpolate(0.0f, scaleMaxRatio, ratio))
        }
        animCurrentRatio = ratio
        allowRestoreDirectly = true
    }

    private fun initViewIfNeeded() {
        if (blurEnabled || scaleEnabled) {
            initBlur()
            this.visibility = VISIBLE
        } else if (dimEnabled) {
            this.visibility = VISIBLE
        }
    }

    private fun releaseViewIfNeeded() {
        if (blurEnabled || scaleEnabled) {
            if (animCurrentRatio == 0.0f) {
                releaseBlur()
                this.visibility = GONE
            }
        } else if (dimEnabled) {
            this.visibility = GONE
        }
    }

    private fun initBlur() {
        if (isBlurInitialized) return
        MiBlurUtils.clearAllBlur(this)
        MiBlurUtils.setPassWindowBlurEnable(this, passWindowBlurEnabled)
        MiBlurUtils.setViewBackgroundBlur(this, MiBlurUtils.USAGE_BACKGROUND)
        MiBlurUtils.setViewBlur(this, 1)
        isBlurInitialized = true
    }

    private fun releaseBlur() {
        if (!isBlurInitialized) return
        isBlurInitialized = false
        MiBlurUtils.clearAllBlur(this)
    }
}