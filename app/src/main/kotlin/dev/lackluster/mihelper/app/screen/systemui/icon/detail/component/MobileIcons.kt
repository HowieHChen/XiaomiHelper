package dev.lackluster.mihelper.app.screen.systemui.icon.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility
import androidx.constraintlayout.compose.layoutId
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.MobileState
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun MobileIcons(
    mobileTypeText: String,
    dataConnected: Boolean,
    state: MobileState,
    fontFamilyProvider: (isCustom: Boolean, weight: Int) -> FontFamily
) {
    val density = LocalDensity.current

    val fontSizeMobileType = with(density) {
        7.159973f.dp.toSp()
    }
    val fontSizeMobileTypeSingle = with(density) {
        val size = if (state.separateTypeSize.enabled) state.separateTypeSize.size else 14.0f
        size.dp.toSp()
    }

    val fontFamilyMobileType = remember(state.smallTypeFont.enabled, state.smallTypeFont.weight) {
        fontFamilyProvider(
            state.smallTypeFont.enabled,
            if (state.smallTypeFont.enabled) state.smallTypeFont.weight.coerceIn(1..1000) else 660
        )
    }
    val fontFamilyMobileTypeSingle = remember(state.separateTypeFont.enabled, state.separateTypeFont.weight) {
        fontFamilyProvider(
            state.separateTypeFont.enabled,
            if (state.separateTypeFont.enabled) state.separateTypeFont.weight.coerceIn(1..1000) else 400
        )
    }

    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val mobileContainer1 = createRefFor("mobile_container_1")
        val mobile1 = createRefFor("mobile_1")
        val mobileInout = createRefFor("mobile_inout")
        val mobileType1 = createRefFor("mobile_type_1")
        val mobileTypeSingle = createRefFor("mobile_type_single")

        val mobileRoam = createRefFor("mobile_roam")
        val mobileContainer2 = createRefFor("mobile_container_2")
        val mobile2 = createRefFor("mobile_2")
        val mobileRoamSmall = createRefFor("mobile_roam_small")
        val mobileType2 = createRefFor("mobile_type_2")

        val chain =
            if (state.rightSeparateType) {
                createHorizontalChain(
                    mobileContainer1, mobileTypeSingle, mobileRoam, mobileContainer2,
                    chainStyle = ChainStyle.Packed(0.5f)
                )
            } else {
                createHorizontalChain(
                    mobileTypeSingle, mobileContainer1, mobileRoam, mobileContainer2,
                    chainStyle = ChainStyle.Packed(0.5f)
                )
            }
        constrain(chain) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(mobileTypeSingle) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            if (!dataConnected || !state.separateType) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileContainer1) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = 24.dp.asDimension()
            height = 20.dp.asDimension()
        }
        constrain(mobileRoam) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            if (!dataConnected || state.hideRoamGlobal || state.hideLargeRoam) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileContainer2) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = 24.dp.asDimension()
            height = 20.dp.asDimension()
        }
        constrain(mobile1) {
            top.linkTo(mobileContainer1.top)
            bottom.linkTo(mobileContainer1.bottom)
            end.linkTo(mobileContainer1.end)
        }
        constrain(mobileType1) {
            top.linkTo(mobileContainer1.top, 0.3f.dp)
            start.linkTo(mobileContainer1.start)
            if (state.hideSmallType || (dataConnected && state.separateType)) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileInout) {
            top.linkTo(mobileContainer1.top)
            bottom.linkTo(mobileContainer1.bottom)
            start.linkTo(mobileContainer1.start)
            if (!dataConnected || state.hideActivity) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobile2) {
            top.linkTo(mobileContainer2.top)
            bottom.linkTo(mobileContainer2.bottom)
            end.linkTo(mobileContainer2.end)
        }
        constrain(mobileType2) {
            top.linkTo(mobileContainer2.top, 0.3f.dp)
            start.linkTo(mobileContainer2.start)
            if (state.hideSmallType || (!dataConnected && !state.hideRoamGlobal)) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileRoamSmall) {
            top.linkTo(mobileContainer2.top)
            bottom.linkTo(mobileContainer2.bottom)
            end.linkTo(mobileContainer2.end)
            if (dataConnected || state.hideRoamGlobal || state.hideSmallRoam) {
                visibility = Visibility.Gone
            }
        }
    }
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.dp),
    ) {
        Image(
            modifier = Modifier
                .layoutId("mobile_roam")
                .size(19.dp, 20.dp),
            painter = painterResource(R.drawable.stat_sys_data_connected_roam),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_single")
                .heightIn(max = 24.dp),
            fontSize = fontSizeMobileTypeSingle,
            fontFamily = fontFamilyMobileTypeSingle,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = mobileTypeText
        )
        Image(
            modifier = Modifier
                .layoutId("mobile_1")
                .size(20.dp),
            painter = painterResource(R.drawable.stat_sys_signal),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_1")
                .size(11.dp, 9.dp),
            fontSize = fontSizeMobileType,
            fontFamily = fontFamilyMobileType,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = mobileTypeText
        )
        Image(
            modifier = Modifier
                .layoutId("mobile_inout")
                .size(6.dp, 20.dp),
            painter = painterResource(R.drawable.stat_sys_signal_inout_left),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("mobile_2")
                .size(20.dp),
            painter = painterResource(R.drawable.stat_sys_signal),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_2")
                .size(11.dp, 9.dp),
            fontSize = fontSizeMobileType,
            fontFamily = fontFamilyMobileType,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = mobileTypeText
        )
        Image(
            modifier = Modifier
                .layoutId("mobile_roam_small")
                .size(24.dp, 20.dp),
            painter = painterResource(R.drawable.stat_sys_data_connected_roam_small),
            contentDescription = null
        )
    }
}