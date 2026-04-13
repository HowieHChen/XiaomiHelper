package dev.lackluster.mihelper.app.screen.systemui.icon.detail.component

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontMode
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.StackedMobileState
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Composable
fun CustomSignalIcon(
    picture: Picture?,
    anchor: PointF?,
    netType: String,
    state: StackedMobileState,
    fontUpdateTrigger: Int,
    nativeTypefaceProvider: (mode: FontMode) -> Typeface
) {
    if (picture == null) {
        // 当 L1 矢量缓存还在后台解析时，或者解析失败，提供一个透明占位符避免布局塌陷
        Box(
            modifier = Modifier.size(24.dp, 24.dp)
        )
        return
    }
    val density = LocalDensity.current

    val tintColor = colorResource(R.color.foreground_dual_tone_full)
    val tintColorArgb = tintColor.toArgb()

    // 1. 模拟 renderStandalone 的自动压缩逻辑
    val isStandaloneTypeAutoSpecialOpt = (state.font.mode == FontMode.MI_SANS_CONDENSED || state.font.mode == FontMode.SF_PRO)
    val isCondensed = isStandaloneTypeAutoSpecialOpt && netType.length > 2

    // 2. 准备 TextPaint 并获取懒加载缓存的 Typeface
    val textPaint = remember(
        fontUpdateTrigger,
        state.small.size,
        state.small.weight,
        state.font.mode,
        state.font.condensedWidth,
        isCondensed,
        tintColorArgb,
        density.density
    ) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintColorArgb
            textSize = state.small.size * density.density

            typeface = nativeTypefaceProvider(state.font.mode)

            val weight = state.small.weight.coerceIn(1, 1000)
            when (state.font.mode) {
                FontMode.FROM_FILE -> {
                    fontVariationSettings = "'wght' $weight"
                }
                FontMode.SF_PRO, FontMode.MI_SANS_CONDENSED -> {
                    val appliedWidth = if (isCondensed) state.font.condensedWidth else 100
                    fontVariationSettings = "'wght' $weight, 'wdth' $appliedWidth"
                }
                else -> {}
            }

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
    BoxWithConstraints(modifier = Modifier.height(24.dp).padding(vertical = 2.dp)) {
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
    isVisible: Boolean,
    netType: String,
    state: StackedMobileState,
    fontUpdateTrigger: Int,
    typefaceProvider: (mode: FontMode) -> Typeface
) {
    if (!isVisible || netType.isEmpty()) {
        Box(modifier = Modifier.width(0.dp).height(24.dp))
        return
    }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    // 统一着色
    val tintColor = colorResource(R.color.foreground_dual_tone_full)
    val tintColorArgb = tintColor.toArgb()

    // 1. 提取自引擎的特殊排版逻辑判断
    val isStandaloneTypeAutoSpecialOpt = (state.font.mode == FontMode.MI_SANS_CONDENSED || state.font.mode == FontMode.SF_PRO)
    val isCondensed = isStandaloneTypeAutoSpecialOpt && netType.length > 2
    val isSpecialOpt = isStandaloneTypeAutoSpecialOpt && netType in listOf("4G+", "5G+", "5GA")

    // 2. 尺寸与字距计算
    val baseTextSizePx = state.large.size * density.density
    val subTextSizePx = baseTextSizePx * 0.7f
    val targetLetterSpacing = if (isCondensed) 0.02f else 0f

    // 3. 构建画笔并从 ViewModel 获取缓存字体
    val textPaint = remember(
        fontUpdateTrigger,
        state.large.size,
        state.large.weight,
        state.font.mode,
        state.font.condensedWidth,
        isCondensed,
        tintColorArgb,
        density.density
    ) {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintColorArgb
            letterSpacing = targetLetterSpacing

            typeface = typefaceProvider(state.font.mode)

            val weight = state.large.weight.coerceIn(1, 1000)
            when (state.font.mode) {
                FontMode.FROM_FILE -> {
                    fontVariationSettings = "'wght' $weight"
                }
                FontMode.SF_PRO, FontMode.MI_SANS_CONDENSED -> {
                    val appliedWidth = if (isCondensed) state.font.condensedWidth else 100
                    fontVariationSettings = "'wght' $weight, 'wdth' $appliedWidth"
                }
                else -> {}
            }

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
    val paddingStartPx = state.large.paddingStart * density.density
    val paddingEndPx = state.large.paddingEnd * density.density
    val actualPaddingLeftPx = if (isRtl) paddingEndPx else paddingStartPx
    val actualPaddingRightPx = if (isRtl) paddingStartPx else paddingEndPx

    val totalWidthPx = exactVisualTextWidth + actualPaddingLeftPx + actualPaddingRightPx

    // 6. 绘制画布
    Canvas(
        modifier = Modifier
            .width(with(density) { totalWidthPx.toDp() })
            .height(24.dp) // 保持与其他系统图标高度一致
    ) {
        drawIntoCanvas { canvas ->
            val verticalOffsetPx = state.large.verticalOffset * density.density
            val centerY = (size.height / 2f) + verticalOffsetPx

            // 统一使用基础字号的 fontMetrics 来算 Baseline，保证主字和角标底端严格平齐
            val fontMetrics = textPaint.fontMetrics
            val baselineY = centerY - (fontMetrics.descent + fontMetrics.ascent) / 2f

            if (isSpecialOpt) {
                // 画主文本 (4G / 5G)
                textPaint.textSize = baseTextSizePx
                canvas.nativeCanvas.drawText(mainText, actualPaddingLeftPx, baselineY, textPaint)

                // 画角标 (+ / A)
                textPaint.textSize = subTextSizePx
                canvas.nativeCanvas.drawText(subText,
                    actualPaddingLeftPx + mainWidth, baselineY, textPaint)

                // 恢复字号防副作用
                textPaint.textSize = baseTextSizePx
            } else {
                // 普通文本直接画
                textPaint.textSize = baseTextSizePx
                canvas.nativeCanvas.drawText(netType, actualPaddingLeftPx, baselineY, textPaint)
            }
        }
    }
}