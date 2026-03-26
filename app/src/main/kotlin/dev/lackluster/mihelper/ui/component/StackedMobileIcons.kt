package dev.lackluster.mihelper.ui.component

import android.content.Context
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.lackluster.mihelper.R
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Composable
fun CustomSignalIcon(
    picture: Picture?,
    anchor: PointF?,
    netType: String,
    fontSizeDp: Float,
    fontWeight: Int,
    fontMode: Int,
    fontPath: String?,
    condensedWidth: Int,
    typefaceProvider: (context: Context, mode: Int, path: String?, weight: Int, condensedWidth: Int, isCondensed: Boolean) -> Typeface
) {
    if (picture == null) {
        // 当 L1 矢量缓存还在后台解析时，或者解析失败，提供一个透明占位符避免布局塌陷
        Box(
            modifier = Modifier.size(24.scaleDp, 24.scaleDp)
        )
        return
    }
    val context = LocalContext.current
    val density = LocalDensity.current

    val tintColor = colorResource(R.color.foreground_dual_tone_full)
    val tintColorArgb = tintColor.toArgb()

    // 1. 模拟 renderStandalone 的自动压缩逻辑
    val isStandaloneTypeAutoSpecialOpt = (fontMode == 2 || fontMode == 3)
    val isCondensed = isStandaloneTypeAutoSpecialOpt && netType.length > 2

    // 2. 准备 TextPaint 并获取懒加载缓存的 Typeface
    val textPaint = remember(fontSizeDp, fontWeight, fontMode, fontPath, condensedWidth, isCondensed, tintColorArgb) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintColorArgb
            textSize = fontSizeDp * DP_SCALE * density.density

            // 从 ViewModel 的 LRU 缓存中秒取 Typeface，拒绝卡顿
            typeface = typefaceProvider(
                context,
                fontMode,
                fontPath,
                (fontWeight * DP_SCALE).toInt().coerceIn(1, 1000),
                condensedWidth,
                isCondensed
            )
            textAlign = Paint.Align.LEFT
        }
    }

    val picturePaint = remember(tintColorArgb) {
        Paint().apply {
            colorFilter = PorterDuffColorFilter(tintColorArgb, PorterDuff.Mode.SRC_IN)
        }
    }

    val textWidth = remember(textPaint, netType) { textPaint.measureText(netType) }
    val fontMetrics = remember(textPaint) { textPaint.fontMetrics }

    // 3. 使用 BoxWithConstraints 获取高度约束，以计算出完美的物理宽度
    BoxWithConstraints(modifier = Modifier.height(24.scaleDp).padding(vertical = 2.scaleDp)) {
        val targetHeightPx = constraints.maxHeight.toFloat()

        val scale = targetHeightPx / picture.height.toFloat()
        val scaledPicWidthPx = picture.width * scale

        var minX = 0f
        var maxX = scaledPicWidthPx
        var textLeft = 0f
        var anchorY = 0f

        if (netType.isNotEmpty() && anchor != null) {
            val anchorX = scaledPicWidthPx * anchor.x
            anchorY = targetHeightPx * anchor.y

            textLeft = anchorX - (textWidth / 2f)
            val textRight = anchorX + (textWidth / 2f)

            minX = min(0f, textLeft)
            maxX = max(scaledPicWidthPx, textRight)
        }

        val finalWidthPx = ceil(maxX - minX)
        val offsetX = -minX

        // 4. 将计算出的总宽度转回 Dp，赋给 Canvas 以撑开父布局的 Row
        Canvas(modifier = Modifier.width(with(density) { finalWidthPx.toDp() }).fillMaxHeight()) {
            drawIntoCanvas { canvas ->
                translate(left = offsetX) {
                    // A. 绘制缩放后的信号底图
                    canvas.save()
                    canvas.scale(scale, scale)
                    canvas.nativeCanvas.saveLayer(null, picturePaint)
                    canvas.nativeCanvas.drawPicture(picture)
                    canvas.nativeCanvas.restore()
                    canvas.restore()

                    // B. 绘制文字 (对齐锚点)
                    if (netType.isNotEmpty() && anchor != null) {
                        val baselineY = anchorY - (fontMetrics.descent + fontMetrics.ascent) / 2f
                        canvas.nativeCanvas.drawText(netType, textLeft, baselineY, textPaint)
                    }
                }
            }
        }
    }
}

@Composable
fun StandaloneTypeIcon(
    netType: String,
    isVisible: Boolean,
    fontSizeDp: Float,
    fontWeight: Int,
    fontMode: Int,
    fontPath: String?,
    condensedWidth: Int,
    paddingStartDp: Float,
    paddingEndDp: Float,
    verticalOffsetDp: Float,
    typefaceProvider: (context: Context, mode: Int, path: String?, weight: Int, condensedWidth: Int, isCondensed: Boolean) -> Typeface
) {
    if (!isVisible || netType.isEmpty()) {
        // 如果因为 wifi/disconnect 隐藏，或者文本为空，渲染一个宽度为0的占位符
        Box(modifier = Modifier.width(0.dp).height(24.scaleDp))
        return
    }

    val context = LocalContext.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    // 统一着色
    val tintColor = colorResource(R.color.foreground_dual_tone_full)
    val tintColorArgb = tintColor.toArgb()

    // 1. 提取自引擎的特殊排版逻辑判断
    val isStandaloneTypeAutoSpecialOpt = (fontMode == 2 || fontMode == 3)
    val isCondensed = isStandaloneTypeAutoSpecialOpt && netType.length > 2
    val isSpecialOpt = isStandaloneTypeAutoSpecialOpt && netType in listOf("4G+", "5G+", "5GA")

    // 2. 尺寸与字距计算
    val baseTextSizePx = fontSizeDp * DP_SCALE * density.density
    val subTextSizePx = baseTextSizePx * 0.7f
    val targetLetterSpacing = if (isCondensed) 0.02f else 0f

    // 3. 构建画笔并从 ViewModel 获取缓存字体
    val textPaint = remember(fontSizeDp, fontWeight, fontMode, fontPath, condensedWidth, isCondensed, tintColorArgb) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintColorArgb
            letterSpacing = targetLetterSpacing
            typeface = typefaceProvider(
                context,
                fontMode,
                fontPath,
                (fontWeight * DP_SCALE).toInt().coerceIn(1, 1000),
                condensedWidth,
                isCondensed
            )
            textAlign = Paint.Align.LEFT
        }
    }

    // 4. 动态测量文字宽度
    val (mainText, subText) = remember(netType, isSpecialOpt) {
        if (isSpecialOpt) netType.take(2) to netType.substring(2) else netType to ""
    }
    val (mainWidth, exactVisualTextWidth) = remember(
        textPaint, netType, isSpecialOpt, baseTextSizePx, subTextSizePx, targetLetterSpacing
    ) {
        if (isSpecialOpt) {
            textPaint.textSize = baseTextSizePx
            val mw = textPaint.measureText(mainText)

            textPaint.textSize = subTextSizePx
            val sw = textPaint.measureText(subText)
            val tail = subTextSizePx * targetLetterSpacing

            textPaint.textSize = baseTextSizePx // 恢复基础字号
            Pair(mw, mw + sw - tail)
        } else {
            textPaint.textSize = baseTextSizePx
            val mw = textPaint.measureText(netType)
            val tail = baseTextSizePx * targetLetterSpacing
            Pair(mw, mw - tail)
        }
    }

    // 5. 计算带 Padding 的总容器宽度
    val paddingStartPx = paddingStartDp * DP_SCALE * density.density
    val paddingEndPx = paddingEndDp * DP_SCALE * density.density
    val actualPaddingLeftPx = if (isRtl) paddingEndPx else paddingStartPx
    val actualPaddingRightPx = if (isRtl) paddingStartPx else paddingEndPx

    val totalWidthPx = exactVisualTextWidth + actualPaddingLeftPx + actualPaddingRightPx

    // 6. 绘制画布
    Canvas(
        modifier = Modifier
            .width(with(density) { totalWidthPx.toDp() })
            .height(24.scaleDp) // 保持与其他系统图标高度一致
    ) {
        drawIntoCanvas { canvas ->
            val verticalOffsetPx = verticalOffsetDp * DP_SCALE * density.density
            val centerY = (size.height / 2f) + verticalOffsetPx

            // 统一使用基础字号的 fontMetrics 来算 Baseline，保证主字和角标底端严格平齐
            val fontMetrics = textPaint.fontMetrics
            val baselineY = centerY - (fontMetrics.descent + fontMetrics.ascent) / 2f

            val startX = actualPaddingLeftPx

            if (isSpecialOpt) {
                // 画主文本 (4G / 5G)
                textPaint.textSize = baseTextSizePx
                canvas.nativeCanvas.drawText(mainText, startX, baselineY, textPaint)

                // 画角标 (+ / A)
                textPaint.textSize = subTextSizePx
                canvas.nativeCanvas.drawText(subText, startX + mainWidth, baselineY, textPaint)

                // 恢复字号防副作用
                textPaint.textSize = baseTextSizePx
            } else {
                // 普通文本直接画
                textPaint.textSize = baseTextSizePx
                canvas.nativeCanvas.drawText(netType, startX, baselineY, textPaint)
            }
        }
    }
}