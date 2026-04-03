package dev.lackluster.mihelper.app.screen.systemui.media.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.constraintlayout.compose.layoutId
import dev.lackluster.hyperx.ui.component.Card
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.media.MediaControlState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.miuixShape

@Composable
fun MediaControlCard(
    isIsland: Boolean = false,
    state: MediaControlState
) {
    val backgroundStyle = state.background.style
    val blurRadius = state.background.blurRadius
    val allowReverse = state.background.allowReverse
    val ambientLight = state.background.ambientLight
    val ambientLightOpt = state.background.effectiveLightOpt

    val lytAlbum = state.layout.album
    val lytLeftActions = state.layout.leftActions
    val lytActionsOrder = state.layout.actionsOrder
    val lytHideTime = state.layout.hideTime
    val lytHideSeamless = state.layout.hideSeamless
    val lytHeaderMargin = state.layout.headerTopMargin
    val lytHeaderPadding = state.layout.headerPadding

    val modifyTextSize = state.element.customTextSize
    val titleSize = state.element.titleSize
    val artistSize = state.element.artistSize
    val timeSize = state.element.timeSize
    val progressStyle = state.element.progressStyle
    val progressWidth = state.element.progressWidth
    val progressComet = state.element.progressComet
    val thumbStyle = state.element.thumbStyle
    val progressRound = state.element.progressRound
    val albumShadow = state.element.albumShadow

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.media_session_height_expanded))
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp, bottom = 6.dp),
        shape = if (isIsland) miuixShape(30.dp) else miuixShape(20.dp)
    ) {
        val backgroundColor: Color
        val textPrimaryColor: Color
        val textSecondaryColor: Color
        when(backgroundStyle) {
            1 -> {
                backgroundColor = Color(MediaDefaults.accent1_8)
                textPrimaryColor = Color(MediaDefaults.accent1_2)
                textSecondaryColor = Color(MediaDefaults.accent1_2)
            }
            2,3 -> {
                backgroundColor = Color(MediaDefaults.accent2_9)
                textPrimaryColor = Color(MediaDefaults.neutral1_1)
                textSecondaryColor = Color(MediaDefaults.neutral2_3)
            }
            4 -> {
                if (allowReverse) {
                    backgroundColor = Color(MediaDefaults.accent1_3)
                    textPrimaryColor = Color(MediaDefaults.accent1_8)
                    textSecondaryColor = Color(MediaDefaults.accent1_8)
                } else {
                    backgroundColor = Color(MediaDefaults.accent1_8)
                    textPrimaryColor = Color(MediaDefaults.accent1_2)
                    textSecondaryColor = Color(MediaDefaults.accent1_2)
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
                start.linkTo(parent.start, 15.dp)
                top.linkTo(parent.top, 15.dp)
                if (lytAlbum == 2) {
                    visibility = Visibility.Gone
                }
            }
            constrain(icon) {
                end.linkTo(albumArt.end, 4.dp)
                bottom.linkTo(albumArt.bottom, 4.dp)
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
                top.linkTo(parent.top, lytHeaderMargin.dp)
                horizontalBias = 0.0f
            }
            constrain(headerArtist) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(albumArt.end, 12.dp, 26.dp)
                end.linkTo(mediaSeamless.start, 6.dp, 26.dp)
                top.linkTo(headerTitle.bottom, lytHeaderPadding.dp)
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
                top.linkTo(albumArt.bottom, 11.dp, 78.5.dp)
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
                backgroundColor = backgroundColor,
                ambientLight = ambientLight,
                ambientLightOpt = ambientLightOpt
            )
            Image(
                modifier = Modifier
                    .layoutId("album_art")
                    .size(52.5.dp)
                    .graphicsLayer(
                        clip = true,
                        shape = miuixShape(10.dp),
                        shadowElevation = if (albumShadow) 32f else 0f
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
                painter = when (lytActionsOrder) {
                    1 ->  painterResource(R.drawable.ic_media_prev)
                    2 ->  painterResource(R.drawable.ic_media_pause)
                    else ->  painterResource(R.drawable.ic_media_lyric)
                },
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action1")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = when (lytActionsOrder) {
                    1 ->  painterResource(R.drawable.ic_media_pause)
                    else ->  painterResource(R.drawable.ic_media_prev)
                },
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action2")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = when (lytActionsOrder) {
                    0 ->  painterResource(R.drawable.ic_media_pause)
                    else ->  painterResource(R.drawable.ic_media_next)
                },
                colorFilter = ColorFilter.tint(textPrimaryColor),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .layoutId("action3")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(60.dp)
                    .height(50.dp),
                painter = when (lytActionsOrder) {
                    0 ->  painterResource(R.drawable.ic_media_next)
                    else ->  painterResource(R.drawable.ic_media_lyric)
                },
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
                progressWidth = progressWidth,
                progressRound = progressRound,
                progressComet = progressComet,
            )
        }
    }
}