package dev.lackluster.mihelper.hook.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.withClip
import dev.lackluster.mihelper.hook.drawable.GhostThumb
import dev.lackluster.mihelper.utils.Math.linearInterpolate
import dev.lackluster.mihelper.utils.factory.dp

class CometSeekBar(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    companion object {
        private const val HEIGHT_DP = 16
        private const val ALPHA_PROGRESS = 0x99
        private const val ALPHA_COMET = 0xFF
        private const val ALPHA_TRACK = 0x33
        private const val PROGRESS_NORMAL_HEIGHT_DP = 6
        private const val PROGRESS_PRESSED_HEIGHT_DP = 14
        private const val COMET_TAIL_LENGTH_DP = 52
        private const val THUMB_NORMAL_HEIGHT_DP = 10
        private const val THUMB_PRESSED_HEIGHT_DP = 16
        private const val THUMB_V_BAR_WIDTH_DP = 4
        private const val THUMB_V_BAR_HEIGHT_DP = 14
    }

    enum class ThumbStyle {
        Circle,
        VerticalBar,
        RoundRect,
        Hidden
    }

    var cometEffect: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var thumbStyle: ThumbStyle = ThumbStyle.Hidden
        set(value) {
            field = value
            adjustThumb()
            invalidate()
        }

    var progressHeight: Int = PROGRESS_NORMAL_HEIGHT_DP.dp(context)
        set(value) {
            field = value
            invalidate()
        }

    private var progressAlpha: Int = ALPHA_PROGRESS
    private var cometAlpha: Int = ALPHA_COMET
    private var trackAlpha: Int = ALPHA_TRACK

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trackPath = Path()
    private val trackRect = RectF()
    private var baseColor: Int = Color.WHITE

    private var touchAnimProgress = 0f // 0f..1f
    private var touchAnimator: ValueAnimator? = null
    private val animInterpolator = SpringInterpolator(0.95f, 0.35f)

    private val tailLength: Int = COMET_TAIL_LENGTH_DP.dp(context)
    private val progressHeightPressed: Int = PROGRESS_PRESSED_HEIGHT_DP.dp(context)
    private val thumbHeight: Int = THUMB_NORMAL_HEIGHT_DP.dp(context)
    private val thumbHeightPressed: Int = THUMB_PRESSED_HEIGHT_DP.dp(context)
    private val thumbVBarWidth: Int = THUMB_V_BAR_WIDTH_DP.dp(context)
    private val thumbVBarHeight: Int = THUMB_V_BAR_HEIGHT_DP.dp(context)

    init {
        trackPaint.shader = null
        trackPaint.style = Paint.Style.FILL
        progressPaint.shader = null
        progressPaint.style = Paint.Style.FILL
        thumbPaint.shader = null
        thumbPaint.style = Paint.Style.FILL

        progressTintList?.defaultColor?.let {
            baseColor = it
        }
        progressDrawable = null
        adjustThumb()
        thumb = GhostThumb(thumbHeight, HEIGHT_DP.dp(context))
        thumbOffset = 0
        splitTrack = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredHeight = HEIGHT_DP.dp(context) + paddingTop + paddingBottom
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        progressTintList?.getColorForState(drawableState, baseColor)?.let {
            baseColor = it
        }

        val animT = touchAnimProgress
        val progressRatio = if (max > 0) progress.toFloat() / max else 0f
        val currentTrackHeight = if (thumbStyle == ThumbStyle.RoundRect || thumbStyle == ThumbStyle.Hidden) {
            linearInterpolate(progressHeight, progressHeightPressed, animT)
        } else {
            progressHeight
        }
        val centerY = height / 2f

        val trackColor = (baseColor and 0x00FFFFFF) or (trackAlpha shl 24)
        val progressColor = (baseColor and 0x00FFFFFF) or (progressAlpha shl 24)
        val cometColor = (baseColor and 0x00FFFFFF) or (cometAlpha shl 24)

        // Draw track
        val trackTop = centerY - currentTrackHeight / 2f
        val trackBottom = centerY + currentTrackHeight / 2f
        val cornerRadius = currentTrackHeight / 2f

        trackRect.set(paddingLeft.toFloat(), trackTop, (width - paddingRight).toFloat(), trackBottom)
        trackPath.reset()
        trackPath.addRoundRect(trackRect, cornerRadius, cornerRadius, Path.Direction.CW)
        trackPaint.color = trackColor
        canvas.drawPath(trackPath, trackPaint)

        val availableWidth = width - paddingLeft - paddingRight
        val progressWidth: Float
        when (thumbStyle) {
            ThumbStyle.Circle -> {
                val availableRunway = (availableWidth - thumbHeight).coerceAtLeast(0)
                progressWidth = (thumbHeight / 2) + (availableRunway * progressRatio)
            }
            ThumbStyle.VerticalBar -> {
                val availableRunway = (availableWidth - thumbVBarWidth).coerceAtLeast(0)
                progressWidth = (thumbVBarWidth / 2) + (availableRunway * progressRatio)
            }
            ThumbStyle.RoundRect -> {
                val availableRunway = (availableWidth - currentTrackHeight).coerceAtLeast(0)
                progressWidth = currentTrackHeight + (availableRunway * progressRatio)
            }
            ThumbStyle.Hidden -> {
                progressWidth = availableWidth * progressRatio
            }
        }

        val currentX = paddingLeft + progressWidth
        if (currentX > paddingLeft) {
            // Draw progress
            if (cometEffect) {
                val shader = LinearGradient(
                    currentX - tailLength, 0f, currentX, 0f,
                    intArrayOf(progressColor, cometColor),
                    null,
                    Shader.TileMode.CLAMP
                )
                progressPaint.shader = shader
            } else {
                progressPaint.color = progressColor
            }
            if (thumbStyle == ThumbStyle.RoundRect) {
                canvas.drawRoundRect(paddingLeft.toFloat(), trackTop, currentX, trackBottom, cornerRadius, cornerRadius, progressPaint)
            } else {
                canvas.withClip(trackPath) {
                    drawRect(paddingLeft.toFloat(), trackTop, currentX, trackBottom, progressPaint)
                }
            }

            // Draw thumb
            thumbPaint.color = cometColor
            when (thumbStyle) {
                ThumbStyle.Circle -> {
                    val currentThumbHeight = linearInterpolate(thumbHeight, thumbHeightPressed, animT).coerceAtLeast(currentTrackHeight)
                    canvas.drawCircle(currentX, centerY, currentThumbHeight / 2.0f, thumbPaint)
                }
                ThumbStyle.VerticalBar -> {
                    val halfWidth = thumbVBarWidth / 2.0f
                    val halfHeight = thumbVBarHeight / 2.0f
                    canvas.drawRoundRect(currentX - halfWidth, centerY - halfHeight, currentX + halfWidth, centerY + halfHeight, halfWidth, halfWidth, thumbPaint)
                }
                else -> {}
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val superResult = super.onTouchEvent(event)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startAnimation(1f) // 按下变大
                // 解决 ScrollView 冲突
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startAnimation(0f) // 松手恢复
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return superResult
    }

//    override fun setProgressTintList(tint: ColorStateList?) {
//        super.setProgressTintList(tint)
//    }
//
//    override fun setProgressBackgroundTintList(tint: ColorStateList?) {
//        super.setProgressBackgroundTintList(tint)
//    }
//
//    override fun setThumbTintList(tint: ColorStateList?) {
//        super.setThumbTintList(tint)
//    }

    private fun startAnimation(target: Float) {
        if (touchAnimProgress == target) return
        touchAnimator?.cancel()
        touchAnimator = ValueAnimator.ofFloat(touchAnimProgress, target).apply {
            duration = 200
            interpolator = animInterpolator
            addUpdateListener {
                touchAnimProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun adjustThumb() {
        val requiredPadding = when (thumbStyle) {
            ThumbStyle.Circle -> thumbHeightPressed / 2
            ThumbStyle.VerticalBar -> thumbVBarWidth / 2
            else -> 0
        }
        setPadding(requiredPadding, paddingTop, requiredPadding, paddingBottom)
    }
}