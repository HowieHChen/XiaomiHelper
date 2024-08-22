package dev.lackluster.mihelper.activity.view

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.data.DataBinding
import dev.lackluster.mihelper.R
import cn.fkj233.ui.activity.view.BaseView

class AnimCurvePreviewView(
    private val curveData: CurveData,
    val dataBindingSend: DataBinding.Binding.Send? = null,
    val dataBindingRecv: DataBinding.Binding.Recv? = null
) : BaseView {
    override fun isHyperXView(): Boolean {
        return true
    }
    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return LinearLayout(context).apply {
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.bezier_curve_view_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.bezier_curve_view_padding_top),
                context.resources.getDimensionPixelSize(R.dimen.bezier_curve_view_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.bezier_curve_view_padding_bottom)
            )
            addView(CurvePreviewView(context).also {
                it.background = AppCompatResources.getDrawable(context, R.drawable.bezier_curve_view_bg)
                val curveType = MIUIActivity.safeSP.getInt(curveData.keyCurveType, curveData.defCurveType)
                it.setDecelerateFactor(
                    MIUIActivity.safeSP.getFloat(curveData.keyDecelerateFactor, curveData.defDecelerateFactor)
                )
                it.setControlPointData(
                    PointF(
                        MIUIActivity.safeSP.getFloat(curveData.keyBezierPoint1X, curveData.defBezierPoint1X),
                        MIUIActivity.safeSP.getFloat(curveData.keyBezierPoint1Y, curveData.defBezierPoint1Y)
                    ),
                    PointF(
                        MIUIActivity.safeSP.getFloat(curveData.keyBezierPoint2X, curveData.defBezierPoint2X),
                        MIUIActivity.safeSP.getFloat(curveData.keyBezierPoint2Y, curveData.defBezierPoint2Y)
                    )
                )
                it.setControlPointUpdateListener(object : CurvePreviewView.ControlPointUpdateListener {
                    override fun onPointUpdate(which: Int, newPointF: PointF) {
                        when (which) {
                            1 -> {
                                MIUIActivity.safeSP.putAny(curveData.keyBezierPoint1X, newPointF.x)
                                MIUIActivity.safeSP.putAny(curveData.keyBezierPoint1Y, newPointF.y)
                                dataBindingSend?.let { send ->
                                    send.send(Pair(1, newPointF))
                                }
                            }
                            2 -> {
                                MIUIActivity.safeSP.putAny(curveData.keyBezierPoint2X, newPointF.x)
                                MIUIActivity.safeSP.putAny(curveData.keyBezierPoint2Y, newPointF.y)
                                dataBindingSend?.let { send ->
                                    send.send(Pair(2, newPointF))
                                }
                            }
                        }
                    }
                })
                it.setCurveType(
                    if (curveType in 0..2) {
                        curveType
                    } else {
                        0
                    }
                )
                dataBindingRecv?.setView(it)
            })
        }
    }

    data class CurveData(
        val keyCurveType: String,
        val keyDecelerateFactor: String,
        val keyBezierPoint1X: String,
        val keyBezierPoint1Y: String,
        val keyBezierPoint2X: String,
        val keyBezierPoint2Y: String,
        val defCurveType: Int = 0,
        val defDecelerateFactor: Float = 1.0f,
        val defBezierPoint1X: Float = 0.0f,
        val defBezierPoint1Y: Float = 0.0f,
        val defBezierPoint2X: Float = 1.0f,
        val defBezierPoint2Y: Float = 1.0f,
    )
}