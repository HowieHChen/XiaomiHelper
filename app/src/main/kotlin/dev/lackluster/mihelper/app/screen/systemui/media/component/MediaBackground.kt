package dev.lackluster.mihelper.app.screen.systemui.media.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.lackluster.mihelper.R
import kotlin.math.max
import kotlin.math.min

@Composable
fun MediaBackground(
    modifier: Modifier,
    style: Int,
    blurRadius: Int,
    backgroundColor: Color,
    ambientLight: Boolean,
    ambientLightOpt: Boolean
) {
    when (style) {
        0 -> {
            Layout(
                modifier = Modifier.background(backgroundColor),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.media_bg_def),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                    if (ambientLight) {
                        Image(
                            painter = painterResource(id = R.drawable.media_bg_circle),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(
                                if (ambientLightOpt) MediaDefaults.mainColorHCTPrimary_12_OPT
                                else MediaDefaults.mainColorHCTPrimary_12
                            ))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.media_bg_circle),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(
                                if (ambientLightOpt) MediaDefaults.mainColorHCTPrimary_10_OPT
                                else MediaDefaults.mainColorHCTPrimary_10
                            ))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.media_bg_circle),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(
                                if (ambientLightOpt) MediaDefaults.mainColorHCTTertiary_12_OPT
                                else MediaDefaults.mainColorHCTTertiary_12
                            ))
                        )
                    }
                }
            ) { measurables, constraints ->
                val width = constraints.maxWidth
                val height = constraints.maxHeight
                val albumSize = max(width, height)
                val albumConstraints = constraints.copy(
                    minWidth = albumSize, maxWidth = albumSize,
                    minHeight = albumSize, maxHeight = albumSize
                )
                val album = measurables[0].measure(albumConstraints)
                val circles = mutableListOf<Placeable>()
                val circleWidth = (width * 1.054f).toInt()
                val circleHeight = (height * 2.295f).toInt()
                if (ambientLight) {
                    val circleConstraints = constraints.copy(
                        minWidth = circleWidth, maxWidth = circleWidth,
                        minHeight = circleHeight, maxHeight = circleHeight
                    )
                    for (i in 1..measurables.lastIndex) {
                        circles.add(measurables[i].measure(circleConstraints))
                    }
                }
                layout(width, height) {
                    album.place(0, (constraints.maxHeight - albumSize) / 2)
                    circles.forEachIndexed { index, circle ->
                        when (index) {
                            0 -> circle.place((width * 0.12f).toInt() - circleWidth / 2, (height * 1.45f).toInt() - circleHeight / 2)
                            1 -> circle.place((width * 0.5f).toInt() - circleWidth / 2, (height * 1.34f).toInt() - circleHeight / 2)
                            else -> circle.place((width * 0.83f).toInt() - circleWidth / 2, (height * 1.45f).toInt() - circleHeight / 2)
                        }
                    }
                }
            }
        }
        4 -> {
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
        }
        else -> {
            val radiusBase = LocalDensity.current.density * 100f
            var viewSize by remember { mutableStateOf(IntSize(800, 800)) }
            val backgroundPainter = when (style) {
                1 -> painterResource(id = R.drawable.media_bg_art)
                2, 3 -> painterResource(id = R.drawable.media_bg_radial)
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
}