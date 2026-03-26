package com.caverock.androidsvg

import android.graphics.PointF

object SvgAnchorExtractor {
    fun extractCenterPercent(svg: SVG, id: String = "type_container"): PointF? {
        try {
            val viewBox = svg.documentViewBox
            val docWidth = viewBox?.width() ?: svg.documentWidth
            val docHeight = viewBox?.height() ?: svg.documentHeight

            if (docWidth <= 0f || docHeight <= 0f) {
                return null
            }
            val obj = svg.getElementById(id)
            if (obj is SVG.Rect) {
                val x = obj.x?.floatValue() ?: 0f
                val y = obj.y?.floatValue() ?: 0f
                val w = obj.width?.floatValue() ?: 0f
                val h = obj.height?.floatValue() ?: 0f
                if (w > 0f && h > 0f) {
                    val centerX = x + (w / 2f)
                    val centerY = y + (h / 2f)
                    return PointF(centerX / docWidth, centerY / docHeight)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}