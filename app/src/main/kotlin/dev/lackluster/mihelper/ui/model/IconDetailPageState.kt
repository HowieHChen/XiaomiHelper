package dev.lackluster.mihelper.ui.model

import androidx.annotation.StringRes
import dev.lackluster.mihelper.R

data class IconDetailPageState(
    val selectedTab: IconTab = IconTab.MOBILE,
    val isLoading: Boolean = false,
    val errorDialogMessage: String? = null,
)

enum class IconTab(@param:StringRes val titleResId: Int) {
    MOBILE(R.string.ui_title_icon_detail_cellular),
    WLAN(R.string.ui_title_icon_detail_wifi),
    BATTERY(R.string.ui_title_icon_detail_battery),
    NET_SPEED(R.string.ui_title_icon_detail_net_speed)
}

data class FontState(
    val enabled: Boolean = false,
    val weight: Int = 630,
)

data class SizeState(
    val enabled: Boolean = false,
    val size: Float = 0.0f,
)

data class MobileState(
    val hideSimAuto: Boolean = false,
    val hideSimOne: Boolean = false,
    val hideSimTwo: Boolean = false,
    val hideActivity: Boolean = false,
    val hideSmallType: Boolean = false,
    val hideRoamGlobal: Boolean = false,
    val hideLargeRoam: Boolean = false,
    val hideSmallRoam: Boolean = false,
    val hideVoWifi: Boolean = false,
    val hideVoLte: Boolean = false,
    val hideVoLteNoService: Boolean = false,
    val hideSpeechHd: Boolean = false,
    val separateType: Boolean = false,
    val rightSeparateType: Boolean = false,
    val customTypeMap: Boolean = false,
    val typeMapString: String = "",
    val separateTypeSize: SizeState = SizeState(size = 14.0f),
    val smallTypeFont: FontState = FontState(weight = 660),
    val separateTypeFont: FontState = FontState(weight = 400),
)

data class WlanState(
    val hideWifiStandard: Boolean = false,
    val hideWifiActivity: Boolean = false,
    val rightWifiActivity: Boolean = false,
    val hideWifiUnavailable: Boolean = false,
)

data class BatteryState(
    val styleStatusBar: Int = 0,
    val styleControlCenter: Int = 0,
    val customPadding: Boolean = false,
    val paddingStart: Float = 0.0f,
    val paddingEnd: Float = 0.0f,
    val hideCharge: Boolean = false,
    val percentMarkStyle: Int = 0,
    val percentInSize: SizeState = SizeState(size = 9.599976f),
    val percentOutSize: SizeState = SizeState(size = 12.5f),
    val percentInFont: FontState = FontState(weight = 620),
    val percentOutFont: FontState = FontState(weight = 500),
    val percentMarkFont: FontState = FontState(weight = 600),
)

data class NetSpeedState(
    val style: Int = 0,
    val unitStyle: Int = 0,
    val refreshPerSecond: Boolean = false,
    val numberFont: FontState = FontState(),
    val unitFont: FontState = FontState(),
    val separateStyleFont: FontState = FontState(),
)