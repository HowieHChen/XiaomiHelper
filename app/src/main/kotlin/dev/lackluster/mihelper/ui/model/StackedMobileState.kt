package dev.lackluster.mihelper.ui.model

import dev.lackluster.mihelper.data.Constants

data class SignalIconState(
    val singleStyle: Int = 0,
    val singleSVG: String = Constants.STACKED_MOBILE_ICON_SINGLE_MIUI,
    val stackedStyle: Int = 0,
    val stackedSVG: String = Constants.STACKED_MOBILE_ICON_STACKED_IOS,
    val alphaFg: Float = 1.0f,
    val alphaBg: Float = 0.4f,
    val alphaError: Float = 0.2f
) {
    val effectiveSingleSVG: String
        get() = when (singleStyle) {
            0 -> Constants.STACKED_MOBILE_ICON_SINGLE_MIUI
            1 -> Constants.STACKED_MOBILE_ICON_SINGLE_IOS
            else -> singleSVG
        }

    val effectiveStackedSVG: String
        get() = when (stackedStyle) {
            0 -> Constants.STACKED_MOBILE_ICON_STACKED_MIUI
            1 -> Constants.STACKED_MOBILE_ICON_STACKED_IOS
            else -> stackedSVG
        }
}

data class TypefaceState(
    val mode: Int = 0,
    val path: String = Constants.VARIABLE_FONT_DEFAULT_PATH,
    val condensedWidth: Int = 80
)

data class SmallTypeState(
    val size: Float = 7.159973f,
    val weight: Int = 630,
    val showOnStacked: Boolean = false,
    val showOnSingle: Boolean = false,
    val showRoaming: Boolean = false
)

data class LargeTypeState(
    val size: Float = 14f,
    val weight: Int = 400,
    val paddingStart: Float = 2f,
    val paddingEnd: Float = 2f,
    val verticalOffset: Float = 0f,
    val hideWhenWifi: Boolean = false,
    val hideWhenDisconnect: Boolean = false
)

data class StackedMobileState(
    val enabled: Boolean = false,
    val signal: SignalIconState = SignalIconState(),
    val font: TypefaceState = TypefaceState(),
    val small: SmallTypeState = SmallTypeState(),
    val large: LargeTypeState = LargeTypeState()
)
