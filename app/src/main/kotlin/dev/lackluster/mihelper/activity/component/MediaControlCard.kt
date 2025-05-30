package dev.lackluster.mihelper.activity.component

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.core.graphics.toColorInt
import dev.lackluster.mihelper.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import kotlin.math.floor
import kotlin.math.min

private val neutral1_1 = "#FEFBFC".toColorInt()
private val neutral2_3 = "#E0E3E6".toColorInt()
private val accent1_2 = "#E7F2FB".toColorInt()
private val accent1_3 = "#D9E4ED".toColorInt()
private val accent1_8 = "#556067".toColorInt()
//private val accent1_9 = "#3E484F".toColorInt()
private val accent2_9 = "#43474A".toColorInt()

@Composable
fun MediaControlCard(
    backgroundStyle: Int,
    allowReverse: Boolean,
    blurRadius: Int,
    lytAlbum: Int,
    lytLeftActions: Boolean,
    lytHideTime: Boolean,
    lytHideSeamless: Boolean,
    modifyTextSize: Boolean,
    titleSize: Float,
    artistSize: Float,
    timeSize: Float,
    thumbStyle: Int,
    progressStyle: Int,
    progressWidth: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.media_session_height_expanded))
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp, top = 12.dp)
    ) {
        val backgroundColor: Color
        val textPrimaryColor: Color
        val textSecondaryColor: Color
        when(backgroundStyle) {
            1 -> {
                backgroundColor = Color(accent1_8)
                textPrimaryColor = Color(accent1_2)
                textSecondaryColor = Color(accent1_2)
            }
            2,3 -> {
                backgroundColor = Color(accent2_9)
                textPrimaryColor = Color(neutral1_1)
                textSecondaryColor = Color(neutral2_3)
            }
            4 -> {
                if (allowReverse) {
                    backgroundColor = Color(accent1_3)
                    textPrimaryColor = Color(accent1_8)
                    textSecondaryColor = Color(accent1_8)
                } else {
                    backgroundColor = Color(accent1_8)
                    textPrimaryColor = Color(accent1_2)
                    textSecondaryColor = Color(accent1_2)
                }
            }
            else -> {
                backgroundColor = Color.Black
                textPrimaryColor = Color.White
                textSecondaryColor = Color.White
            }
        }
        val constraints = ConstraintSet {
            val mediaBg = createRefFor("media_bg")
            val albumArt = createRefFor("album_art")
            val icon = createRefFor("icon")
            val mediaSeamless = createRefFor("media_seamless")
            val headerTitle = createRefFor("header_title")
            val headerArtist = createRefFor("header_artist")
            val action0 = createRefFor("action0")
            val action1 = createRefFor("action1")
            val action2 = createRefFor("action2")
            val action3 = createRefFor("action3")
            val action4 = createRefFor("action4")
            val mediaElapsedTime = createRefFor("media_elapsed_time")
            val mediaTotalTime = createRefFor("media_total_time")
            val mediaProgressBar = createRefFor("media_progress_bar")
            val parent = createRefFor("parent")
            constrain(mediaBg) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            constrain(albumArt) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 16.dp)
                if (lytAlbum == 2) {
                    visibility = Visibility.Gone
                }
            }
            constrain(icon) {
                end.linkTo(albumArt.end, 2.dp)
                bottom.linkTo(albumArt.bottom, 2.dp)
                if (lytAlbum != 0) {
                    visibility = Visibility.Gone
                }
            }
            constrain(mediaSeamless) {
                width = 34.dp.asDimension()
                height = 34.dp.asDimension()
                end.linkTo(parent.end, 25.dp)
                top.linkTo(parent.top, 23.dp)
                if (lytHideSeamless) {
                    visibility = Visibility.Gone
                }
            }
            constrain(headerTitle) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(albumArt.end, 12.dp, 26.dp)
                end.linkTo(mediaSeamless.start, 6.dp, 26.dp)
                top.linkTo(parent.top, 21.dp)
                horizontalBias = 0.0f
            }
            constrain(headerArtist) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(albumArt.end, 12.dp, 26.dp)
                end.linkTo(mediaSeamless.start, 6.dp, 26.dp)
                top.linkTo(headerTitle.bottom, 2.dp)
                horizontalBias = 0.0f
                alpha = 0.8f
            }
            val chain = createHorizontalChain(
                action0, action1, action2, action3, action4,
                chainStyle = if (lytLeftActions) ChainStyle.Packed(0.0f) else ChainStyle.SpreadInside
            )
            constrain(chain) {
                start.linkTo(parent.start, 6.dp, 6.dp)
                end.linkTo(parent.end, 6.dp)
            }
            constrain(action0) {
                width = 60.dp.asDimension()
                height = 50.dp.asDimension()
                top.linkTo(albumArt.bottom, 11.dp, 79.5.dp)
            }
            constrain(action1) {
                width = 60.dp.asDimension()
                height = 50.dp.asDimension()
                top.linkTo(action0.top)
                bottom.linkTo(action0.bottom)
            }
            constrain(action2) {
                width = 60.dp.asDimension()
                height = 50.dp.asDimension()
                top.linkTo(action0.top)
                bottom.linkTo(action0.bottom)
            }
            constrain(action3) {
                width = 60.dp.asDimension()
                height = 50.dp.asDimension()
                top.linkTo(action0.top)
                bottom.linkTo(action0.bottom)
            }
            constrain(action4) {
                width = 60.dp.asDimension()
                height = 50.dp.asDimension()
                top.linkTo(action0.top)
                bottom.linkTo(action0.bottom)
            }
            constrain(mediaElapsedTime) {
                width = 50.dp.asDimension()
                height = Dimension.wrapContent
                absoluteLeft.linkTo(parent.absoluteLeft, 16.dp)
                top.linkTo(mediaProgressBar.top)
                bottom.linkTo(mediaProgressBar.bottom)
                if (lytHideTime) {
                    visibility = Visibility.Gone
                }
            }
            constrain(mediaProgressBar) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                absoluteLeft.linkTo(mediaElapsedTime.absoluteRight, 3.dp, 26.dp)
                absoluteRight.linkTo(mediaTotalTime.absoluteLeft, 3.dp, 26.dp)
                bottom.linkTo(parent.bottom, 16.dp)
            }
            constrain(mediaTotalTime) {
                width = 50.dp.asDimension()
                height = Dimension.wrapContent
                absoluteRight.linkTo(parent.absoluteRight, 16.dp)
                top.linkTo(mediaProgressBar.top)
                bottom.linkTo(mediaProgressBar.bottom)
                if (lytHideTime) {
                    visibility = Visibility.Gone
                }
            }
        }
        ConstraintLayout(constraints) {
            MediaBackground(
                modifier = Modifier
                    .layoutId("media_bg")
                    .fillMaxSize(),
                style = backgroundStyle,
                blurRadius = blurRadius,
                backgroundColor = backgroundColor
            )
            Image(
                modifier = Modifier
                    .layoutId("album_art")
                    .size(52.5.dp)
                    .graphicsLayer(
                        clip = true,
                        shape = SmoothRoundedCornerShape(8.dp),
                        shadowElevation = 32f
                    ),
                painter = painterResource(R.drawable.media_bg_ori),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("icon")
                    .size(16.dp)
                    .clip(RoundedCornerShape(5.dp)),
                painter = painterResource(R.drawable.media_app_icon),
                contentDescription = null
            )
            Image(
                modifier = Modifier.layoutId("media_seamless"),
                painter = painterResource(R.drawable.ic_media_seamless),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action0")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = painterResource(R.drawable.ic_media_lyric),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action1")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = painterResource(R.drawable.ic_media_prev),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action2")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = painterResource(R.drawable.ic_media_play),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action3")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = painterResource(R.drawable.ic_media_next),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action4")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = painterResource(R.drawable.ic_media_fav),
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Text(
                modifier = Modifier.layoutId("header_title"),
                fontSize = TextUnit(if (modifyTextSize) titleSize else 18f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = textPrimaryColor,
                text = "Cause you know"
            )
            Text(
                modifier = Modifier.layoutId("header_artist"),
                fontSize = TextUnit(if (modifyTextSize) artistSize else 12f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = textPrimaryColor,
                text = "11-G.E.M. 邓紫棋"
            )
            Text(
                modifier = Modifier
                    .layoutId("media_elapsed_time")
                    .width(50.dp),
                fontSize = TextUnit(if (modifyTextSize) timeSize else 12f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = textSecondaryColor,
                text = "02:46"
            )
            Text(
                modifier = Modifier
                    .layoutId("media_total_time")
                    .width(50.dp),
                fontSize = TextUnit(if (modifyTextSize) timeSize else 12f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = textPrimaryColor,
                text = "03:48"
            )
            MediaProgressBar(
                modifier = Modifier
                    .layoutId("media_progress_bar"),
                color = textPrimaryColor,
                thumbStyle = thumbStyle,
                progressStyle = progressStyle,
                progressWidth = progressWidth
            )
        }
    }
}

@Composable
private fun MediaBackground(
    modifier: Modifier,
    style: Int,
    blurRadius: Int,
    backgroundColor: Color
) {
    if (style == 4) {
        Layout(
            modifier = Modifier.background(backgroundColor),
            content = {
                Image(
                    painter = painterResource(id = R.drawable.media_bg_ori),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Box(
                    modifier = Modifier.background(
                        brush = Brush.horizontalGradient(
                            0.0f to backgroundColor,
                            1.0f to backgroundColor.copy(0.2f)
                        )
                    )
                )
            }
        ) { measurables, constraints ->
            val albumSize = min(constraints.maxHeight, constraints.maxWidth)
            val albumConstraints = constraints.copy(
                minWidth = albumSize, maxWidth = albumSize,
                minHeight = albumSize, maxHeight = albumSize
            )
            val album = measurables[0].measure(albumConstraints)
            val gradient = measurables[1].measure(albumConstraints)
            layout(constraints.maxWidth, constraints.maxHeight) {
                album.place(constraints.maxWidth - albumSize, 0)
                gradient.place(constraints.maxWidth - albumSize, 0)
            }
        }
    } else {
        val radiusBase = LocalDensity.current.density * 100f
        var viewSize by remember { mutableStateOf(IntSize(800, 800)) }
        val backgroundPainter = when(style) {
            1 -> painterResource(id = R.drawable.media_bg_art)
            2,3 -> painterResource(id = R.drawable.media_bg_radial)
            else -> painterResource(id = R.drawable.media_bg_def)
        }
        Image(
            modifier = modifier.then(
                if (style == 2) Modifier
                    .onGloballyPositioned { coordinates ->
                        viewSize = coordinates.size
                    }
                    .blur(
                        (viewSize.width * blurRadius / radiusBase).dp,
                        BlurredEdgeTreatment.Rectangle
                    )
                else Modifier
            ),
            painter = backgroundPainter,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

@Composable
private fun MediaProgressBar(
    modifier: Modifier,
    color: Color,
    thumbStyle: Int,
    progressStyle: Int,
    progressWidth: Float
) {
    val progressHeight = if (progressStyle == 0) 4.dp else progressWidth.dp
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
    ) {
        val barHeight = progressHeight.toPx()
        val barWidth = size.width
        val progWidth = (barWidth - barHeight) * 0.728f
        val thumbOffset = if (progressStyle == 2) {
            barWidth * 0.728f
        } else {
            (barWidth - barHeight) * 0.728f + floor(barHeight / 2)
        }
        val progressAlpha = if (progressStyle == 0) 0.5f else 0.75f
        val backgroundAlpha = if (progressStyle == 0) 0.1f else 0.2f
        if (progressStyle == 2) {
            var heightFraction = 1.0f
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
            drawIntoCanvas { canvas ->
                val progress = 0.728f
                val totalWidth = barWidth
                val totalProgressPx = totalWidth * progress
                val waveProgressPx = totalWidth * progress
                // Build Wiggly Path
                val waveStart = -phaseOffset - waveLength / 2f
                val waveEnd = waveProgressPx
                // helper function, computes amplitude for wave segment
                val computeAmplitude: (Float, Float) -> Float = { x, sign ->
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
                while (currentX < waveEnd) {
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
                canvas.drawLine(Offset(totalProgressPx, 0f), Offset(totalWidth, 0f), linePaint)
                canvas.restore()
            }
        } else {
            drawRoundRect(
                color = color.copy(alpha = backgroundAlpha),
                size = Size(barWidth, barHeight),
                topLeft = Offset(0f, center.y - barHeight / 2),
                cornerRadius = CornerRadius(barHeight / 2)
            )
            drawArc(
                color = color.copy(alpha = progressAlpha),
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                alpha = 1f,
                topLeft = Offset(0f, center.y - barHeight / 2),
                size = Size(floor(barHeight), barHeight)
            )
            drawRoundRect(
                color = color.copy(alpha = progressAlpha),
                size = Size(progWidth, barHeight),
                topLeft = Offset(floor(barHeight / 2), center.y - barHeight / 2),
                cornerRadius = CornerRadius.Zero
            )
        }
        when (thumbStyle) {
            0 -> {
                drawCircle(
                    color = color,
                    radius = 10.dp.toPx() / 2,
                    center = Offset(thumbOffset, center.y)
                )
            }
            2 -> {
                drawRoundRect(
                    color = color,
                    size = Size(4.dp.toPx(), 14.dp.toPx()),
                    topLeft = Offset(thumbOffset - 2.dp.toPx(), center.y - 7.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    }
}