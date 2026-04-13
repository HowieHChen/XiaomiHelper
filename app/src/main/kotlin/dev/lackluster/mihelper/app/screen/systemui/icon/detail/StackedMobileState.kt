package dev.lackluster.mihelper.app.screen.systemui.icon.detail

import dev.lackluster.mihelper.app.repository.FontMode
import dev.lackluster.mihelper.data.Constants

data class SignalIconState(
    val singleStyle: Int = 0,
    val singleSVG: String = "",
    val singleSVGName: String = "",
    val stackedStyle: Int = 0,
    val stackedSVG: String = "",
    val stackedSVGName: String = "",
    val alphaFg: Float = 1.0f,
    val alphaBg: Float = 0.4f,
    val alphaError: Float = 0.2f
)

data class TypefaceState(
    val mode: FontMode = FontMode.DEFAULT,
    val displayName: String = Constants.VARIABLE_FONT_DEFAULT_PATH,
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
