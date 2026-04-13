package dev.lackluster.mihelper.app.screen.systemui.icon.detail.component

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.NetSpeedState

@Composable
fun NetworkSpeedIcon(
    state: NetSpeedState,
    nativeTypefaceProvider: (isCustom: Boolean) -> Typeface,
) {
    val density = LocalDensity.current

    val fontSize7 = with(density) {
        7.dp.toSp()
    }
    val fontSize6p4 = with(density) {
        6.4f.dp.toSp()
    }

    val baseTypefaceNum = remember(state.numberFont.enabled) {
        nativeTypefaceProvider(state.numberFont.enabled)
    }
    val baseTypefaceUnit = remember(state.unitFont.enabled) {
        nativeTypefaceProvider(state.unitFont.enabled)
    }
    val baseTypefaceSeparate = remember(state.separateStyleFont.enabled) {
        nativeTypefaceProvider(state.separateStyleFont.enabled)
    }

    val weightNum = if (state.numberFont.enabled) state.numberFont.weight.coerceIn(1..1000) else 630
    val weightUnit = if (state.unitFont.enabled) state.unitFont.weight.coerceIn(1..1000) else 630
    val weightSeparate = if (state.separateStyleFont.enabled) state.separateStyleFont.weight.coerceIn(1..1000) else 630

    val textLine1: String
    val textLine2: String
    if (state.style in 1..4) {
        val textLineSb1 = StringBuilder()
        val textLineSb2 = StringBuilder()
        textLineSb1.append("0.00")
        textLineSb2.append("11.8")
        textLineSb1.append("M")
        textLineSb2.append("M")
        when (state.unitStyle) {
            1 -> {
                textLineSb1.append("B")
                textLineSb2.append("B")
            }
            2 -> {
                textLineSb1.append("B/s")
                textLineSb2.append("B/s")
            }
        }
        when (state.style) {
            2 -> {
                textLineSb1.append("↑")
                textLineSb2.append("↓")
            }
            3 -> {
                textLineSb1.append("▲")
                textLineSb2.append("▼")
            }
            4 -> {
                textLineSb1.append("△")
                textLineSb2.append("▼")
            }
        }
        textLineSb1.append(" ")
        textLineSb2.append(" ")
        textLine1 = textLineSb1.toString()
        textLine2 = textLineSb2.toString()
    } else {
        textLine1 = "6.13 "
        textLine2 = "MB/s "
    }
    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val text1 = createRefFor("text1")
        val text2 = createRefFor("text2")
        constrain(text1) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            if (state.style == 0) {
                bottom.linkTo(parent.bottom, 9.2f.dp)
                start.linkTo(parent.start)
            } else {
                bottom.linkTo(parent.bottom, 8.0f.dp)
            }
        }
        constrain(text2) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            if (state.style == 0) {
                top.linkTo(parent.top, 7.8f.dp)
                start.linkTo(parent.start)
            } else {
                top.linkTo(parent.top, 8.0f.dp)
            }
        }
    }
    val textColorArgb = colorResource(R.color.foreground_dual_tone_full).toArgb()
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.dp)
    ) {
        AndroidView(
            modifier = Modifier.layoutId("text1"),
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                    gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    setTextColor(textColorArgb)
                }
            },
            update = { textView ->
                textView.text = textLine1
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize7.value)

                val isStyle0 = state.style == 0
                textView.typeface = if (isStyle0) baseTypefaceNum else baseTypefaceSeparate
                val currentWeight = if (isStyle0) weightNum else weightSeparate

                textView.fontVariationSettings = ""
                textView.fontVariationSettings = "'wght' $currentWeight"
            }
        )
        AndroidView(
            modifier = Modifier.layoutId("text2"),
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                    gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    setTextColor(textColorArgb)
                }
            },
            update = { textView ->
                textView.text = textLine2

                val isStyle0 = state.style == 0
                val currentFontSize = if (isStyle0) fontSize6p4 else fontSize7
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize.value)
                textView.typeface = if (isStyle0) baseTypefaceUnit else baseTypefaceSeparate
                val currentWeight = if (isStyle0) weightUnit else weightSeparate

                textView.fontVariationSettings = ""
                textView.fontVariationSettings = "'wght' $currentWeight"
            }
        )
    }
}