package dev.lackluster.mihelper.hook.rules.systemui.media

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color

class AnimatingColorTransition(
    private val defaultColor: Int = Color.WHITE,
    private val applyColor: (Int) -> Unit
) : ValueAnimator.AnimatorUpdateListener {
    private val argbEvaluator = ArgbEvaluator()
    private val valueAnimator = buildAnimator()
    private var sourceColor: Int = defaultColor
    private var currentColor: Int = defaultColor
    private var targetColor: Int = defaultColor

    override fun onAnimationUpdate(animation: ValueAnimator) {
        currentColor = argbEvaluator.evaluate(animation.animatedFraction, sourceColor, targetColor) as Int
        applyColor(currentColor)
    }

    fun animateToNewColor(color: Int): Boolean {
        if (color != targetColor) {
            sourceColor = currentColor
            targetColor = color
            valueAnimator.cancel()
            valueAnimator.start()
            return true
        }
        return false
    }

    init {
        applyColor(defaultColor)
    }

    private fun buildAnimator(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 333
        animator.addUpdateListener(this)
        return animator
    }
}