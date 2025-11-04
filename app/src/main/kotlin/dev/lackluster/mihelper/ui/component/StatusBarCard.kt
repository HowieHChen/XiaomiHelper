package dev.lackluster.mihelper.ui.component

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_DEFAULT
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_HIDDEN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_ICON_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_LINE
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_ONLY
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.SystemProperties
import dev.lackluster.mihelper.utils.factory.dp2sp
import top.yukonga.miuix.kmp.basic.Text
import java.io.File

const val DP_SCALE = 1.0f

@Composable
fun MobileIcons(
    dataConnected: Boolean,
    hideCellularActivity: Boolean,
    hideCellularType: Boolean,
    cellularTypeSingle: Boolean,
    cellularTypeSingleSwap: Boolean,
    cellularTypeSingleSize: Boolean,
    cellularTypeSingleSizeVal: Float,
    cellularTypeFW: Boolean,
    cellularTypeFWVal: Int,
    cellularTypeSingleFW: Boolean,
    cellularTypeSingleFWVal: Int,
    hideRoamGlobal: Boolean,
    hideRoam: Boolean,
    hideSmallRoam: Boolean,
) {
    val context = LocalContext.current
    val mobileTypeText = "4G"
    val fontSizeMobileType = TextUnit((7.159973f * DP_SCALE).dp2sp(context), TextUnitType.Sp)
    val fontSizeMobileTypeSingle = TextUnit(
        ((if (cellularTypeSingleSize) cellularTypeSingleSizeVal else 14.0f) * DP_SCALE).dp2sp(context),
        TextUnitType.Sp
    )
    val fontFamilyMobileType = FontFamilyCache.getFontFamilyForWeight(
        ((if (cellularTypeFW) cellularTypeFWVal else 660) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val fontFamilyMobileTypeSingle = FontFamilyCache.getFontFamilyForWeight(
        ((if (cellularTypeSingleFW) cellularTypeSingleFWVal else 400) * DP_SCALE).toInt().coerceIn(1..1000)
    )
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
            if (cellularTypeSingleSwap) {
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
            if (!dataConnected || !cellularTypeSingle) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileContainer1) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = 24.scaleDp.asDimension()
            height = 20.scaleDp.asDimension()
        }
        constrain(mobileRoam) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            if (!dataConnected || hideRoamGlobal || hideRoam) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileContainer2) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = 24.scaleDp.asDimension()
            height = 20.scaleDp.asDimension()
        }
        constrain(mobile1) {
            top.linkTo(mobileContainer1.top)
            bottom.linkTo(mobileContainer1.bottom)
            end.linkTo(mobileContainer1.end)
        }
        constrain(mobileType1) {
            top.linkTo(mobileContainer1.top, 0.3f.scaleDp)
            start.linkTo(mobileContainer1.start)
            if (hideCellularType || (dataConnected && cellularTypeSingle)) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileInout) {
            top.linkTo(mobileContainer1.top)
            bottom.linkTo(mobileContainer1.bottom)
            start.linkTo(mobileContainer1.start)
            if (!dataConnected || hideCellularActivity) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobile2) {
            top.linkTo(mobileContainer2.top)
            bottom.linkTo(mobileContainer2.bottom)
            end.linkTo(mobileContainer2.end)
        }
        constrain(mobileType2) {
            top.linkTo(mobileContainer2.top, 0.3f.scaleDp)
            start.linkTo(mobileContainer2.start)
            if (hideCellularType || (!dataConnected && !hideRoamGlobal)) {
                visibility = Visibility.Gone
            }
        }
        constrain(mobileRoamSmall) {
            top.linkTo(mobileContainer2.top)
            bottom.linkTo(mobileContainer2.bottom)
            end.linkTo(mobileContainer2.end)
            if (dataConnected || hideRoamGlobal || hideSmallRoam) {
                visibility = Visibility.Gone
            }
        }
    }
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.scaleDp),
    ) {
        Image(
            modifier = Modifier
                .layoutId("mobile_roam")
                .size(19.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_data_connected_roam),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_single")
                .heightIn(max = 24.scaleDp),
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
                .size(20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_signal),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_1")
                .size(11.scaleDp, 9.scaleDp),
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
                .size(6.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_signal_inout_left),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("mobile_2")
                .size(20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_signal),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("mobile_type_2")
                .size(11.scaleDp, 9.scaleDp),
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
                .size(24.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_data_connected_roam_small),
            contentDescription = null
        )
    }
}

@Composable
fun WifiIcon(
    hideWifiActivity: Boolean,
    hideWifiStandard: Boolean,
    rightWifiActivity: Boolean,
) {
    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val wifi = createRefFor("wifi")
        val wifiInout = createRefFor("wifi_inout")
        val wifiStandard = createRefFor("wifi_standard")
        constrain(wifi) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
        constrain(wifiInout) {
            top.linkTo(wifi.top)
            bottom.linkTo(wifi.bottom)
            if (hideWifiActivity) {
                visibility = Visibility.Gone
            } else if (hideWifiStandard && rightWifiActivity) {
                end.linkTo(wifi.end)
            } else {
                start.linkTo(wifi.start)
            }
        }
        constrain(wifiStandard) {
            top.linkTo(wifi.top)
            bottom.linkTo(wifi.bottom)
            end.linkTo(wifi.end)
            if (hideWifiStandard) {
                visibility = Visibility.Gone
            }
        }
    }
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.size(20.scaleDp, 24.scaleDp)
    ) {
        Image(
            modifier = Modifier
                .layoutId("wifi")
                .size(20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_wifi_signal),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("wifi_inout")
                .size(6.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_wifi_inout),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("wifi_standard")
                .size(6.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_sys_wifi_standard),
            contentDescription = null
        )
    }
}

@Composable
fun BatteryIcon(
    batteryStyle: Int,
    fallbackStyle: Int,
    batteryPercentMarkStyle: Int,
    batteryPadding: Boolean,
    batteryPaddingStartVal: Float,
    batteryPaddingEndVal: Float,
    batteryHideChargeOut: Boolean,
    batteryPercentInSize: Boolean,
    batteryPercentInSizeVal: Float,
    batteryPercentOutSize: Boolean,
    batteryPercentOutSizeVal: Float,
    batteryPercentInFW: Boolean,
    batteryPercentInFWVal: Int,
    batteryPercentOutFW: Boolean,
    batteryPercentOutFWVal: Int,
    batteryPercentMarkFW: Boolean,
    batteryPercentMarkFWVal: Int,

) {
    val context = LocalContext.current
    val style =
        if (batteryStyle in listOf(STYLE_DEFAULT, STYLE_LINE, STYLE_HIDDEN)) fallbackStyle
        else batteryStyle
    val showNormalIcon = style in listOf(STYLE_ICON_ONLY, STYLE_TEXT_OUT)
    val showHollowIcon = style == STYLE_TEXT_IN
    val showPercentOut = style in listOf(STYLE_TEXT_OUT, STYLE_TEXT_ONLY)
    val showPercentMark = showPercentOut && batteryPercentMarkStyle != 2
    val percentMarkTopMargin = if (showPercentMark && batteryPercentMarkStyle == 0) 1.scaleDp else 0.scaleDp

    val fontSizePercentIn = TextUnit(
        ((if (batteryPercentInSize) batteryPercentInSizeVal else 9.599976f) * DP_SCALE).dp2sp(context),
        TextUnitType.Sp
    )
    val fontSizePercentOut = TextUnit(
        ((if (batteryPercentOutSize) batteryPercentOutSizeVal else 12.5f) * DP_SCALE).dp2sp(context),
        TextUnitType.Sp
    )
    val fontSizePercentMark =
        if (batteryPercentMarkStyle == 1) fontSizePercentOut
        else TextUnit((10.0f * DP_SCALE).dp2sp(context), TextUnitType.Sp)
    val fontFamilyPercentIn = FontFamilyCache.getFontFamilyForWeight(
        ((if (batteryPercentInFW) batteryPercentInFWVal else 620) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val fontFamilyPercentOut = FontFamilyCache.getFontFamilyForWeight(
        ((if (batteryPercentOutFW) batteryPercentOutFWVal else 500) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val fontFamilyPercentMark = FontFamilyCache.getFontFamilyForWeight(
        ((if (batteryPercentMarkFW) batteryPercentMarkFWVal else 600) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val paddingStart = if (batteryPadding) batteryPaddingStartVal else 0
    val paddingEnd = if (batteryPadding) batteryPaddingEndVal else 0

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
            start.linkTo(parent.start, paddingStart.scaleDp)
            end.linkTo(percentOut.start, 0.scaleDp, paddingEnd.scaleDp)
            if (!showNormalIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentOut) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(percentMark.start, 0.scaleDp, paddingEnd.scaleDp)
            if (!showPercentOut) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentMark) {
            top.linkTo(parent.top, percentMarkTopMargin)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, paddingEnd.scaleDp)
            if (!showPercentMark) {
                visibility = Visibility.Gone
            }
        }
        constrain(hollowBattery) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, paddingStart.scaleDp)
            end.linkTo(charging.start, 0.scaleDp, paddingEnd.scaleDp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(percentIn) {
            top.linkTo(hollowBattery.top)
            bottom.linkTo(hollowBattery.bottom)
            start.linkTo(hollowBattery.start)
            end.linkTo(hollowBattery.end, 1.34f.scaleDp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
        }
        constrain(charging) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(hollowBattery.end)
            end.linkTo(parent.end, paddingEnd.scaleDp)
            if (!showHollowIcon) {
                visibility = Visibility.Gone
            }
            if (batteryHideChargeOut) {
                visibility = Visibility.Gone
            }
        }
    }
    FontFamily(
        Typeface.Builder("").build()
    )
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.scaleDp)
    ) {
        Image(
            modifier = Modifier
                .layoutId("battery")
                .size(28.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_battery),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("percent_out"),
            fontSize = fontSizePercentOut,
            fontFamily = fontFamilyPercentOut,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = "100"
        )
        Text(
            modifier = Modifier
                .layoutId("percent_mark"),
            fontSize = fontSizePercentMark,
            fontFamily = fontFamilyPercentMark,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = "%"
        )
        Image(
            modifier = Modifier
                .layoutId("hollow_battery")
                .size(28.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_hollow_battery),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .layoutId("percent_in"),
            fontSize = fontSizePercentIn,
            fontFamily = fontFamilyPercentIn,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = "100"
        )
        Image(
            modifier = Modifier
                .layoutId("charging")
                .size(8.scaleDp, 20.scaleDp),
            painter = painterResource(R.drawable.stat_charging),
            contentDescription = null
        )
    }
}

@Composable
fun NetworkSpeed(
    style: Int,
    netSpeedNumFW: Boolean,
    netSpeedNumFWVal: Int,
    netSpeedUnitFW: Boolean,
    netSpeedUnitFWVal: Int,
    netSpeedSeparateFW: Boolean,
    netSpeedSeparateFWVal: Int,
) {
    val context = LocalContext.current
    val fontSize7 = TextUnit(
        (7f * DP_SCALE).dp2sp(context),
        TextUnitType.Sp
    )
    val fontSize6p4 = TextUnit(
        (6.4f * DP_SCALE).dp2sp(context),
        TextUnitType.Sp
    )
    val fontFamilyNum = FontFamilyCache.getFontFamilyForWeight(
        ((if (netSpeedNumFW) netSpeedNumFWVal else 630) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val fontFamilyUnit = FontFamilyCache.getFontFamilyForWeight(
        ((if (netSpeedUnitFW) netSpeedUnitFWVal else 630) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val fontFamilySeparate = FontFamilyCache.getFontFamilyForWeight(
        ((if (netSpeedSeparateFW) netSpeedSeparateFWVal else 630) * DP_SCALE).toInt().coerceIn(1..1000)
    )
    val textLine1: String
    val textLine2: String
    when (style) {
        1 -> {
            textLine1 = "0.00MB "
            textLine2 = "11.8MB "
        }
        2 -> {
            textLine1 = "0.00MB↑ "
            textLine2 = "11.8MB↓ "
        }
        3 -> {
            textLine1 = "0.00MB▲ "
            textLine2 = "11.8MB▼ "
        }
        4 -> {
            textLine1 = "0.00MB△ "
            textLine2 = "11.8MB▼ "
        }
        else -> {
            textLine1 = "6.13 "
            textLine2 = "MB/s "
        }
    }
    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val text1 = createRefFor("text1")
        val text2 = createRefFor("text2")
        constrain(text1) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            if (style == 0) {
                bottom.linkTo(parent.bottom, 9.2f.scaleDp)
                start.linkTo(parent.start)
            } else {
                bottom.linkTo(parent.bottom, 8.0f.scaleDp)
            }
        }
        constrain(text2) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            if (style == 0) {
                top.linkTo(parent.top, 7.8f.scaleDp)
                start.linkTo(parent.start)
            } else {
                top.linkTo(parent.top, 8.0f.scaleDp)
            }
        }
    }
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.height(24.scaleDp)
    ) {
        Text(
            modifier = Modifier.layoutId("text1"),
            fontSize = fontSize7,
            fontFamily = if (style == 0) fontFamilyNum else fontFamilySeparate,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.End,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = textLine1
        )
        Text(
            modifier = Modifier.layoutId("text2"),
            fontSize = if (style == 0) fontSize6p4 else fontSize7,
            fontFamily = if (style == 0) fontFamilyUnit else fontFamilySeparate,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.End,
            color = colorResource(R.color.foreground_dual_tone_full),
            text = textLine2
        )
    }
}

@Stable
inline val Number.scaleDp: Dp
    get() = Dp(this.toFloat() * DP_SCALE)

private val vfPath by lazy {
    val defaultPath = SystemProperties.get("ro.miui.ui.font.mi_font_path", "/system/fonts/MiSansVF.ttf")
    val prefPath = SafeSP.getString(Pref.Key.SystemUI.FontWeight.FONT_PATH, defaultPath)
    val fontFile = File(prefPath)
    if (fontFile.exists() && fontFile.isFile) prefPath else defaultPath
}

@Stable
object FontFamilyCache {
    private val weightCache = mutableMapOf<Int, FontFamily>()

    fun getFontFamilyForWeight(weight: Int): FontFamily {
        return weightCache.getOrPut(weight) {
            FontFamily(
                Typeface.Builder(vfPath).setFontVariationSettings("'wght' $weight").build()
            )
        }
    }
}