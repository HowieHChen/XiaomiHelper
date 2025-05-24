package dev.lackluster.mihelper.hook.drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import kotlin.math.max

class RadialGradientDrawable(
    private var artwork: Drawable,
    private var startColor: Int = Color.BLACK,
    private var endColor: Int = Color.BLACK
) : Drawable() {
    private var gradient: GradientDrawable = GradientDrawable()
    private var background: ColorDrawable = ColorDrawable()

    init {
        gradient.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradient.shape = GradientDrawable.RECTANGLE
        background.color = startColor
        gradient.colors = intArrayOf(
            startColor and 0x00ffffff or (64 shl 24),
            endColor and 0x00ffffff or (255 shl 24)
        )
    }

    override fun draw(p0: Canvas) {
        val bounds = bounds
        if (bounds.isEmpty) return
        background.color = startColor
        background.setBounds(0, 0, bounds.width(), bounds.height())
        background.draw(p0)
        artwork.setBounds(0, 0, bounds.width(), bounds.height())
        artwork.draw(p0)
        gradient.colors = intArrayOf(
            startColor and 0x00ffffff or (64 shl 24),
            endColor and 0x00ffffff or (255 shl 24)
        )
        gradient.setBounds(0, 0, bounds.width(), bounds.height())
        gradient.gradientRadius = max(bounds.width(), bounds.height()).toFloat()
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
}