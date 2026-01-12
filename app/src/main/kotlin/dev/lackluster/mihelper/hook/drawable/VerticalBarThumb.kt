package dev.lackluster.mihelper.hook.drawable

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable

class VerticalBarThumb(density: Float): Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE // 默认白色
    }

    private val baseWidth = 4f * density
    private val baseHeight = 14f * density
    private val radius = baseWidth / 2f
    private val rectF = RectF()
    private var mTintList: ColorStateList? = null
    private var currentSolidColor: Int = Color.WHITE

    override fun draw(canvas: Canvas) {
        val bounds = bounds

        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()

        rectF.set(
            cx - baseWidth / 2f,
            cy - baseHeight / 2f,
            cx + baseWidth / 2f,
            cy + baseHeight / 2f
        )

        paint.color = currentSolidColor
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }

    override fun getIntrinsicWidth(): Int = baseWidth.toInt()
    override fun getIntrinsicHeight(): Int = baseHeight.toInt()

    override fun setTintList(tint: ColorStateList?) {
        mTintList = tint
        updateColor(state)
    }

    override fun isStateful(): Boolean = true

    override fun onStateChange(state: IntArray): Boolean {
        return updateColor(state)
    }

    private fun updateColor(state: IntArray): Boolean {
        val tint = mTintList ?: return false

        val colorWithAlpha = tint.getColorForState(state, Color.WHITE)
        val solidColor = (colorWithAlpha and 0x00FFFFFF) or (0xFF shl 24)

        if (currentSolidColor != solidColor) {
            currentSolidColor = solidColor
            invalidateSelf()
            return true
        }
        return false
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}