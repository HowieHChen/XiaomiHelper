package dev.lackluster.mihelper.hook.drawable

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import androidx.core.graphics.withClip

class CometProgressDrawable(
    private val cometEffect: Boolean = true,
    private val roundCorner: Boolean = true
) : Drawable() {
    private val tailLengthPx: Float = 143f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private val fullBounds = RectF()
    private val trackPath = Path()

    private var baseColor: Int = Color.TRANSPARENT
    private var mTintList: ColorStateList? = null

    override fun setTintList(tint: ColorStateList?) {
        mTintList = tint
        updateColorState(state)
    }

    override fun isStateful(): Boolean {
        return (mTintList != null && mTintList!!.isStateful) || super.isStateful()
    }

    override fun onStateChange(state: IntArray): Boolean {
        return updateColorState(state) || super.onStateChange(state)
    }

    private fun updateColorState(state: IntArray): Boolean {
        val tint = mTintList ?: return false
        val newColor = tint.getColorForState(state, baseColor)
        if (baseColor != newColor) {
            baseColor = newColor
            invalidateSelf()
            return true
        }
        return false
    }

    override fun onLevelChange(level: Int): Boolean {
        invalidateSelf()
        return true
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fullBounds.set(bounds)
        if (!roundCorner) {
            val cornerRadius = fullBounds.height() / 2
            trackPath.reset()
            trackPath.addRoundRect(fullBounds, cornerRadius, cornerRadius, Path.Direction.CW)
        }
    }

    override fun draw(p0: Canvas) {
        val height = fullBounds.height()
        val totalWidth = fullBounds.width()

        if (totalWidth <= 0) return

        val progressRatio = level / 10000f
        val currentWidth: Float

        if (roundCorner) {
            val availableRunway = (totalWidth - height).coerceAtLeast(0f)
            currentWidth = height + (availableRunway * progressRatio)
        } else {
            currentWidth = totalWidth * progressRatio
        }

        rectF.set(
            fullBounds.left,
            fullBounds.top,
            fullBounds.left + currentWidth,
            fullBounds.bottom
        )

        val semiTransparentColor = (baseColor and 0x00FFFFFF) or (0x99 shl 24)
        if (cometEffect) {
            val shaderStart = currentWidth - tailLengthPx

            val solidColor = (baseColor and 0x00FFFFFF) or (0xFF shl 24)

            val shader = LinearGradient(
                shaderStart, 0f, currentWidth, 0f,
                intArrayOf(semiTransparentColor, solidColor),
                null,
                Shader.TileMode.CLAMP
            )

            paint.shader = shader
        } else {
            paint.color = semiTransparentColor
        }

        val cornerRadius = height / 2
        if (roundCorner) {
            p0.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        } else {
            p0.withClip(trackPath) {
                drawRect(rectF, paint)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }
}