package dev.lackluster.mihelper.app.screen.systemui.icon.detail.component

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility
import androidx.constraintlayout.compose.layoutId
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.BatteryState
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_DEFAULT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_HIDDEN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_ICON_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_LINE
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT

@SuppressLint("SetTextI18n")
@Composable
fun BatteryIcon(
    batteryStyle: Int,
    fallbackStyle: Int,
    state: BatteryState,
    nativeTypefaceProvider: (isCustom: Boolean) -> Typeface,
) {
    val density = LocalDensity.current

    val style =
        if (batteryStyle in listOf(STYLE_DEFAULT, STYLE_LINE, STYLE_HIDDEN)) fallbackStyle
        else batteryStyle
    val showNormalIcon = style in listOf(STYLE_ICON_ONLY, STYLE_TEXT_OUT)
    val showHollowIcon = style == STYLE_TEXT_IN
    val showPercentOut = style in listOf(STYLE_TEXT_OUT, STYLE_TEXT_ONLY)
    val showPercentMark = showPercentOut && state.percentMarkStyle != 2
    val percentMarkTopMargin = if (showPercentMark && state.percentMarkStyle == 0) 1.dp else 0.dp

    val fontSizePercentIn = with(density) {
        val size = if (state.percentInSize.enabled) state.percentInSize.size else 9.599976f
        size.dp.toSp()
    }
    val fontSizePercentOut = with(density) {
        val size = if (state.percentOutSize.enabled) state.percentOutSize.size else 12.5f
        size.dp.toSp()
    }
    val fontSizePercentMark = with(density) {
        if (state.percentMarkStyle == 1) fontSizePercentOut else 10.0f.dp.toSp()
    }

    val baseTypefacePercentIn = remember(state.percentInFont.enabled) {
        nativeTypefaceProvider(state.percentInFont.enabled)
    }
    val baseTypefacePercentOut = remember(state.percentOutFont.enabled) {
        nativeTypefaceProvider(state.percentOutFont.enabled)
    }
    val baseTypefacePercentMark = remember(state.percentMarkFont.enabled) {
        nativeTypefaceProvider(state.percentMarkFont.enabled)
    }

    val weightPercentIn = if (state.percentInFont.enabled) state.percentInFont.weight.coerceIn(1..1000) else 620
    val weightPercentOut = if (state.percentOutFont.enabled) state.percentOutFont.weight.coerceIn(1..1000) else 500
    val weightPercentMark = if (state.percentMarkFont.enabled) state.percentMarkFont.weight.coerceIn(1..1000) else 600

    val paddingStart = if (state.customPadding) state.paddingStart else 0f
    val paddingEnd = if (state.customPadding) state.paddingEnd else 0f

    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val battery = createRefFor("battery")
        val percentOut = createRefFor("percent_out")
        val percentMark = createRefFor("percent_mark")
        val hollowBattery = createRefFor("hollow_battery")
        val percentIn = createRefFor("percent_in")
        val charging = createRefFor("charging")
        constrain(battery) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, paddingStart.dp)
            end.linkTo(percentOut.start, 0.dp, paddingEnd.dp)
            if (!showNormalIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentOut) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(percentMark.start, 0.dp, paddingEnd.dp)
            if (!showPercentOut) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentMark) {
            top.linkTo(parent.top, percentMarkTopMargin)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, paddingEnd.dp)
            if (!showPercentMark) {
                visibility = Visibility.Gone
            }
        }
        constrain(hollowBattery) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, paddingStart.dp)
            end.linkTo(charging.start, 0.dp, paddingEnd.dp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentIn) {
            top.linkTo(hollowBattery.top)
            bottom.linkTo(hollowBattery.bottom)
            start.linkTo(hollowBattery.start)
            end.linkTo(hollowBattery.end, 1.34f.dp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(charging) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(hollowBattery.end)
            end.linkTo(parent.end, paddingEnd.dp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
            if (state.hideCharge) {
                visibility = Visibility.Gone
            }
        }
    }

    val textColorArgb = colorResource(R.color.foreground_dual_tone_full).toArgb()

    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.dp)
    ) {
        Image(
            modifier = Modifier
                .layoutId("battery")
                .size(28.dp, 20.dp),
            painter = painterResource(R.drawable.stat_battery),
            contentDescription = null
        )
        AndroidView(
            modifier = Modifier.layoutId("percent_out"),
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                    gravity = Gravity.CENTER
                    setTextColor(textColorArgb)
                }
            },
            update = { textView ->
                textView.text = "100"
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizePercentOut.value)
                textView.typeface = baseTypefacePercentOut
                textView.fontVariationSettings = ""
                textView.fontVariationSettings = "'wght' $weightPercentOut"
            }
        )
        AndroidView(
            modifier = Modifier.layoutId("percent_mark"),
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                    gravity = Gravity.CENTER
                    setTextColor(textColorArgb)
                }
            },
            update = { textView ->
                textView.text = "%"
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizePercentMark.value)
                textView.typeface = baseTypefacePercentMark
                textView.fontVariationSettings = ""
                textView.fontVariationSettings = "'wght' $weightPercentMark"
            }
        )
        Image(
            modifier = Modifier
                .layoutId("hollow_battery")
                .size(28.dp, 20.dp),
            painter = painterResource(R.drawable.stat_hollow_battery),
            contentDescription = null
        )
        AndroidView(
            modifier = Modifier.layoutId("percent_in"),
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                    gravity = Gravity.CENTER
                    setTextColor(textColorArgb)
                }
            },
            update = { textView ->
                textView.text = "100"
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizePercentIn.value)
                textView.typeface = baseTypefacePercentIn
                textView.fontVariationSettings = ""
                textView.fontVariationSettings = "'wght' $weightPercentIn"
            }
        )
        Image(
            modifier = Modifier
                .layoutId("charging")
                .size(8.dp, 20.dp),
            painter = painterResource(R.drawable.stat_charging),
            contentDescription = null
        )
    }
}