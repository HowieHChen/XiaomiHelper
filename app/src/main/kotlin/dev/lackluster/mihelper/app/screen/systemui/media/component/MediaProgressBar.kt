package dev.lackluster.mihelper.app.screen.systemui.media.component

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

@Composable
fun MediaProgressBar(
    modifier: Modifier,
    color: Color,
    thumbStyle: Int,
    progressStyle: Int,
    progressWidth: Float,
    progressRound: Boolean,
    progressComet: Boolean
) {
    val progressHeight = if (progressStyle == 0) 4.dp else progressWidth.dp
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
    ) {
        val progressRatio = 0.728f
        val barWidth = size.width
        if (progressStyle == 2) {
            val heightFraction = 1.0f
            val phaseSpeed = 8.dp.toPx()
            val waveLength = 20.dp.toPx()
            val phaseOffset = (SystemClock.uptimeMillis() / 1000f * phaseSpeed) % waveLength
            val lineAmplitude = 1.5.dp.toPx()
            val path = Path()
            val strokeWidth = 2.dp.toPx()
            val wavePaint = Paint().apply {
                this.strokeWidth = strokeWidth
                this.strokeCap = StrokeCap.Round
                this.style = PaintingStyle.Stroke
                this.color = color
                this.colorFilter = ColorFilter.tint(color)
            }
            val linePaint = Paint().apply {
                this.strokeWidth = strokeWidth
                this.strokeCap = StrokeCap.Round
                this.style = PaintingStyle.Stroke
                this.color = color
                this.colorFilter = ColorFilter.tint(color.copy(0.3f))
            }
            val thumbPaint = Paint().apply {
                this.strokeWidth = strokeWidth
                this.strokeCap = StrokeCap.Round
                this.style = PaintingStyle.Fill
                this.color = color
                this.colorFilter = ColorFilter.tint(color)
            }
            drawIntoCanvas { canvas ->
                val totalProgressPx = barWidth * progressRatio
                val waveProgressPx = barWidth * progressRatio
                // Build Wiggly Path
                val waveStart = -phaseOffset - waveLength / 2f
                // helper function, computes amplitude for wave segment
                val computeAmplitude: (Float, Float) -> Float = { _, sign ->
                    sign * heightFraction * lineAmplitude
                }
                // Reset path object to the start
                path.rewind()
                path.moveTo(waveStart, 0f)
                // Build the wave, incrementing by half the wavelength each time
                var currentX = waveStart
                var waveSign = 1f
                var currentAmp = computeAmplitude(currentX, waveSign)
                val dist = waveLength / 2f
                while (currentX < waveProgressPx) {
                    waveSign = -waveSign
                    val nextX = currentX + dist
                    val midX = currentX + dist / 2
                    val nextAmp = computeAmplitude(nextX, waveSign)
                    path.cubicTo(midX, currentAmp, midX, nextAmp, nextX, nextAmp)
                    currentAmp = nextAmp
                    currentX = nextX
                }
                // translate to the start position of the progress bar for all draw commands
                val clipTop = lineAmplitude + strokeWidth
                canvas.save()
                canvas.translate(0.0f, center.y)
                // Draw path up to progress position
                canvas.save()
                canvas.clipRect(0f, -1f * clipTop, totalProgressPx, clipTop)
                canvas.drawPath(path, wavePaint)
                canvas.restore()
                canvas.drawLine(Offset(totalProgressPx, 0f), Offset(barWidth, 0f), linePaint)
                canvas.restore()
                when (thumbStyle) {
                    0 -> {
                        val radius = 5.dp.toPx()
                        canvas.drawCircle(Offset(totalProgressPx, center.y), radius, thumbPaint)
                    }
                    2 -> {
                        val width = 4.dp.toPx()
                        val height = 14.dp.toPx()
                        val left = totalProgressPx - width / 2
                        val top = center.y - height / 2
                        canvas.drawRoundRect(left, top, left + width, top + height, width / 2, width / 2, thumbPaint)
                    }
                }
            }
        } else {
            val currentTrackHeight = progressHeight.toPx()
            val centerY = size.height / 2f

            val trackColor = color.copy(alpha = if (progressStyle == 0) 0.1f else 0x33 / 255.0f)
            val progressColor = color.copy(alpha = if (progressStyle == 0) 0.5f else 0x99 / 255.0f)
            val cometColor = color.copy(alpha = if (progressStyle == 0) 1.0f else 0xFF / 255.0f)

            val trackTop = centerY - currentTrackHeight / 2f
            val trackBottom = centerY + currentTrackHeight / 2f
            val cornerRadius = currentTrackHeight / 2f

            val trackPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = 0f,
                        top = trackTop,
                        right = size.width,
                        bottom = trackBottom,
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                )
            }
            clipPath(trackPath) {
                drawRect(
                    color = trackColor,
                    topLeft = Offset(0f, trackTop),
                    size = Size(size.width, currentTrackHeight)
                )
            }
            val availableWidth = size.width
            val progressWidth: Float
            when (thumbStyle) {
                0 -> {
                    val thumbHeight = 10.dp.toPx().coerceAtLeast(currentTrackHeight)
                    val availableRunway = (availableWidth - thumbHeight).coerceAtLeast(0f)
                    progressWidth = (thumbHeight / 2) + (availableRunway * progressRatio)
                }
                1 -> {
                    if (progressRound) {
                        val availableRunway = (availableWidth - currentTrackHeight).coerceAtLeast(0f)
                        progressWidth = currentTrackHeight + (availableRunway * progressRatio)
                    } else {
                        progressWidth = availableWidth * progressRatio
                    }
                }
                else -> {
                    val thumbVBarWidth = 4.dp.toPx()
                    val availableRunway = (availableWidth - thumbVBarWidth).coerceAtLeast(0f)
                    progressWidth = (thumbVBarWidth / 2) + (availableRunway * progressRatio)
                }
            }

            val currentX = progressWidth
            clipPath(trackPath) {
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        progressColor,
                        if (progressStyle == 1 && progressComet) cometColor else progressColor
                    ),
                    start = Offset(currentX - 52.dp.toPx(), 0f),
                    end = Offset(currentX, 0f),
                    tileMode = TileMode.Clamp
                )
                if (progressStyle == 1 && thumbStyle == 1 && progressRound) {
                    drawRoundRect(
                        brush = gradientBrush,
                        size = Size(currentX, currentTrackHeight),
                        topLeft = Offset(0f, center.y - currentTrackHeight / 2),
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                } else {
                    drawRect(
                        brush = gradientBrush,
                        topLeft = Offset(0f, trackTop),
                        size = Size(currentX, currentTrackHeight)
                    )
                }
            }
            when (thumbStyle) {
                0 -> {
                    val radius = 10.dp.toPx().coerceAtLeast(currentTrackHeight) / 2
                    drawCircle(
                        color = cometColor,
                        radius = radius,
                        center = Offset(currentX, center.y)
                    )
                }
                2 -> {
                    val width = 4.dp.toPx()
                    val height = 14.dp.toPx()
                    val left = currentX - width / 2
                    val top = center.y - height / 2
                    drawRoundRect(
                        color = cometColor,
                        topLeft = Offset(left, top),
                        size = Size(width, height),
                        cornerRadius = CornerRadius(width / 2)
                    )
                }
            }
        }
    }
}