package dev.lackluster.mihelper.activity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dev.lackluster.mihelper.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


class CurvePreviewView : View {
    private var mCurveType: Int
    private val mPadding: Int
    private var mSize: Int = 0
    // Common
    private val mStartPointF: PointF
    private val mEndPointF: PointF
    private val mPath: Path
    private val mPaintCurve: Paint
    private val mPaintPoint: Paint
    private val mCircleRadius: Int
    // Linear

    // Decelerate
    private var mFactor: Float
    private val mDeceleratePath: Path = Path()
    private var mPathPointFData: FloatArray
    private val mPaintCurveWidth: Float
    // Bezier Curve
    private val mControlPoint1F: PointF
    private val mControlPoint2F: PointF
    private val mControlPoint1Data: PointF
    private val mControlPoint2Data: PointF
    private val mPaintControlLine: Paint
    private val mPaintControlPoint: Paint
    private val mPaintPointStroke: Paint
    private val mControlCircleRadius: Int
    private val mStrokeWidth: Int

    private var mTouchFlag: Int
    private var mListener: ControlPointUpdateListener? = null


    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int): super(context, attributeSet, defStyleAttr) {
        mCurveType = 0
        mCircleRadius = context.resources.getDimensionPixelSize(R.dimen.bezier_point_radius)
        mControlCircleRadius = context.resources.getDimensionPixelSize(R.dimen.bezier_control_point_radius)
        mStrokeWidth = context.resources.getDimensionPixelSize(R.dimen.bezier_point_stroke_width)
        mPadding = context.resources.getDimensionPixelSize(R.dimen.bezier_padding)
        mFactor = 1.0f
        mPathPointFData = FloatArray(0)
        mTouchFlag = 0

        mPath = Path()
        mStartPointF = PointF()
        mEndPointF = PointF()
        mControlPoint1F = PointF()
        mControlPoint2F = PointF()
        mControlPoint1Data = PointF(0.2f, 0.1f)
        mControlPoint2Data = PointF(0.3f, 1.0f)

        mPaintControlLine = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintControlLine.strokeWidth = context.resources.getDimension(R.dimen.bezier_control_line_width)
        mPaintControlLine.style = Paint.Style.STROKE
        mPaintControlLine.strokeCap = Paint.Cap.ROUND
        mPaintControlLine.color = context.getColor(R.color.foreground_dual_tone_half)

        mPaintCurve = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintCurveWidth = context.resources.getDimension(R.dimen.bezier_path_line_width)
        mPaintCurve.strokeWidth = mPaintCurveWidth
        mPaintCurve.strokeJoin = Paint.Join.ROUND
        mPaintCurve.style = Paint.Style.STROKE
        mPaintControlLine.strokeCap = Paint.Cap.ROUND
        mPaintCurve.color = context.getColor(R.color.foreground_dual_tone_full)

        mPaintPoint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintPoint.style = Paint.Style.FILL
        mPaintPoint.color = context.getColor(cn.fkj233.ui.R.color.hyperx_tint_color)

        mPaintControlPoint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintControlPoint.style = Paint.Style.FILL
        mPaintControlPoint.color = context.getColor(cn.fkj233.ui.R.color.hyperx_color_blue_primary_default)

        mPaintPointStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintPointStroke.style = Paint.Style.STROKE
        mPaintPointStroke.strokeWidth = mStrokeWidth.toFloat()
        mPaintPointStroke.color = context.getColor(cn.fkj233.ui.R.color.hyperx_color_blue_primary_default)
        mPaintPointStroke.alpha = 127
    }
    fun setCurveType(type: Int) {
        if (type !in 0..2) {
            throw IllegalArgumentException("Unknown type")
        }
        if (type == mCurveType) {
            return
        }
        mCurveType = type
        when (mCurveType) {
            1 -> updateDeceleratePathPoints()
            2 -> updateControlPoint()
        }
        invalidate()
    }
    fun setDecelerateFactor(factor: Float) {
        mFactor = factor
        updateDeceleratePathPoints()
    }
    fun setControlPointData(point1: PointF, point2: PointF) {
        mControlPoint1Data.set(point1)
        mControlPoint2Data.set(point2)
        updateControlPoint()
    }
    fun setControlPointUpdateListener(listener: ControlPointUpdateListener?) {
        mListener = listener
    }

    private fun checkTouchContained(x: Float, y: Float): Boolean {
        return (x in mStartPointF.x..mEndPointF.x &&
                y in mEndPointF.y..mStartPointF.y)
    }

    private fun checkTouchFlag(x: Float, y: Float, set: Boolean): Boolean {
        val touchRange = mControlCircleRadius * 2
        if (
            x in mControlPoint1F.x - touchRange..mControlPoint1F.x + touchRange &&
            y in mControlPoint1F.y - touchRange..mControlPoint1F.y + touchRange
            ) {
            if (set) {
                mTouchFlag = 1
            }
            return true
        }
        if (
            x in mControlPoint2F.x - touchRange..mControlPoint2F.x + touchRange &&
            y in mControlPoint2F.y - touchRange..mControlPoint2F.y + touchRange
        ) {
            if (set) {
                mTouchFlag = 2
            }
            return true
        }
        return false
    }

    private fun saveControlPointData() {
        when (mTouchFlag) {
            1 -> {
                mControlPoint1Data.set(
                    (mControlPoint1F.x - mStartPointF.x) / mSize,
                    1 - (mControlPoint1F.y - mEndPointF.y) / mSize)
                mListener?.onPointUpdate(1, mControlPoint1Data)
            }
            2 -> {
                mControlPoint2Data.set(
                    (mControlPoint2F.x - mStartPointF.x) / mSize,
                    1 - (mControlPoint2F.y - mEndPointF.y) / mSize)
                mListener?.onPointUpdate(2, mControlPoint2Data)
            }
        }
    }
    private fun getDecelerateValue(input: Float): Float {
        val result: Float = if (mFactor == 1.0f) {
            1.0f - (1.0f - input) * (1.0f - input)
        } else {
            (1.0f - (1.0f - input).pow((2 * mFactor)))
        }
        return result
    }
    private fun updateDeceleratePathPoints() {
        mDeceleratePath.reset()
        mDeceleratePath.moveTo(mStartPointF.x, mStartPointF.y)
        val length = mEndPointF.x - mStartPointF.x
//        val spacing = (mPaintCurveWidth / 2).roundToInt().coerceAtLeast(1)
        val spacing = 2
        val numberOfPoints = (length / spacing).roundToInt() + 1
        for (index in 1 until numberOfPoints - 1) {
            val realX = spacing * index
            val realY = (1 - getDecelerateValue(realX / length )) * length
            mDeceleratePath.lineTo(
                mStartPointF.x + realX,
                mEndPointF.y + realY
            )
        }
        mDeceleratePath.lineTo(
            mEndPointF.x, mEndPointF.y
        )
        invalidate()
    }
    private fun updateControlPoint() {
        mControlPoint1F.set(
            mStartPointF.x + mSize * mControlPoint1Data.x,
            mEndPointF.y + mSize * (1 - mControlPoint1Data.y)
        )
        mControlPoint2F.set(
            mStartPointF.x + mSize * mControlPoint2Data.x,
            mEndPointF.y + mSize * (1 - mControlPoint2Data.y)
        )
        invalidate()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (mCurveType != 2) {
            return false
        }
        val inBounds = checkTouchContained(event.x, event.y)
        val hitPoint = checkTouchFlag(event.x, event.y, false)
        if (event.action == MotionEvent.ACTION_DOWN && !(inBounds || hitPoint)) {
            return false
        }
        parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (checkTouchFlag(event.x, event.y, true)) {
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x.coerceIn(mStartPointF.x, mEndPointF.x)
                val y = event.y.coerceIn(mEndPointF.y, mStartPointF.y)
                when (mTouchFlag) {
                    1 -> {
                        mControlPoint1F.set(x, y)
                        invalidate()
                    }
                    2 -> {
                        mControlPoint2F.set(x, y)
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                saveControlPointData()
                mTouchFlag = 0
                invalidate()
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = measuredWidth
        val mHeight = measuredHeight
        val defSize = context.resources.getDimensionPixelSize(R.dimen.bezier_curve_view_size) + mPadding * 2
        val size = min(defSize, max(mWidth, mHeight))
        setMeasuredDimension(mWidth, size)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSize = min(w - mPadding * 2, h - mPadding * 2)
        mStartPointF.set((w - mSize).toFloat() / 2, h - (h - mSize).toFloat() / 2)
        mEndPointF.set(w - (w - mSize).toFloat() / 2, (h - mSize).toFloat() / 2)
        when (mCurveType) {
            1 -> updateDeceleratePathPoints()
            2 -> updateControlPoint()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (mCurveType) {
            0 -> {
                drawLinearCurve(canvas)
            }
            1 -> {
                drawDecelerateCurve(canvas)
            }
            2 -> {
                drawBezierCurve(canvas)
            }
        }
    }
    private fun drawLinearCurve(canvas: Canvas) {
        mPath.reset()
        mPath.moveTo(mStartPointF.x, mStartPointF.y)
        mPath.lineTo(mEndPointF.x, mEndPointF.y)
        canvas.drawPath(mPath, mPaintCurve)
        canvas.drawCircle(
            mStartPointF.x,
            mStartPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )
        canvas.drawCircle(
            mEndPointF.x,
            mEndPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )
    }
    private fun drawDecelerateCurve(canvas: Canvas) {
//        val points = mPathPointFData
//        val count = points.size
//        if (count >= 4) {
//            if (count and 2 != 0) {
//                canvas.drawLines(points, 0, count - 2, mPaintCurve)
//                canvas.drawLines(points, 2, count - 2, mPaintCurve)
//            } else {
//                canvas.drawLines(points, 0, count, mPaintCurve)
//                canvas.drawLines(points, 4, count - 4, mPaintCurve)
//            }
//        }
        canvas.drawPath(mDeceleratePath, mPaintCurve)
        canvas.drawCircle(
            mStartPointF.x,
            mStartPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )
        canvas.drawCircle(
            mEndPointF.x,
            mEndPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )
    }
    private fun drawBezierCurve(canvas: Canvas) {
        mPath.reset()
        mPath.moveTo(mStartPointF.x, mStartPointF.y)
        mPath.cubicTo(
            mControlPoint1F.x, mControlPoint1F.y,
            mControlPoint2F.x, mControlPoint2F.y,
            mEndPointF.x, mEndPointF.y
        )
        canvas.drawPath(mPath, mPaintCurve)
        canvas.drawLine(
            mStartPointF.x, mStartPointF.y,
            mControlPoint1F.x, mControlPoint1F.y,
            mPaintControlLine
        )
        canvas.drawLine(
            mControlPoint2F.x, mControlPoint2F.y,
            mEndPointF.x, mEndPointF.y,
            mPaintControlLine
        )
        canvas.drawCircle(
            mStartPointF.x,
            mStartPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )
        canvas.drawCircle(
            mEndPointF.x,
            mEndPointF.y,
            mCircleRadius.toFloat(),
            mPaintPoint
        )

        canvas.drawCircle(
            mControlPoint1F.x,
            mControlPoint1F.y,
            mControlCircleRadius.toFloat(),
            mPaintControlPoint
        )
        canvas.drawCircle(
            mControlPoint2F.x,
            mControlPoint2F.y,
            mControlCircleRadius.toFloat(),
            mPaintControlPoint
        )
        when (mTouchFlag) {
            1 -> {
                canvas.drawCircle(
                    mControlPoint1F.x,
                    mControlPoint1F.y,
                    mControlCircleRadius.toFloat(),
                    mPaintPointStroke
                )
            }
            2 -> {
                canvas.drawCircle(
                    mControlPoint2F.x,
                    mControlPoint2F.y,
                    mControlCircleRadius.toFloat(),
                    mPaintPointStroke
                )
            }
        }
    }

    interface ControlPointUpdateListener {
        fun onPointUpdate(which: Int, newPointF: PointF)
    }
}