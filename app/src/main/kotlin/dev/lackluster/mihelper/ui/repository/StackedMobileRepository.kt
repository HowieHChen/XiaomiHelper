package dev.lackluster.mihelper.ui.repository

import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.StackedMobile
import dev.lackluster.mihelper.ui.model.LargeTypeState
import dev.lackluster.mihelper.ui.model.SignalIconState
import dev.lackluster.mihelper.ui.model.SmallTypeState
import dev.lackluster.mihelper.ui.model.StackedMobileState
import dev.lackluster.mihelper.ui.model.TypefaceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object StackedMobileRepository {
    private val _state = MutableStateFlow(loadInitialConfig())
    val state = _state.asStateFlow()

    private fun loadInitialConfig(): StackedMobileState {
        return StackedMobileState(
            enabled = SafeSP.getBoolean(StackedMobile.ENABLED, false),
            signal = SignalIconState(
                singleStyle = SafeSP.getInt(StackedMobile.SIGNAL_SVG_SINGLE, 0),
                singleSVG = SafeSP.getString(StackedMobile.SIGNAL_SVG_SINGLE_VAL, Constants.STACKED_MOBILE_ICON_SINGLE_MIUI),
                stackedStyle = SafeSP.getInt(StackedMobile.SIGNAL_SVG_STACKED, 0),
                stackedSVG = SafeSP.getString(StackedMobile.SIGNAL_SVG_STACKED_VAL, Constants.STACKED_MOBILE_ICON_STACKED_MIUI),
                alphaFg = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_FG, 1.0f),
                alphaBg = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_BG, 0.4f),
                alphaError = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_ERROR, 0.2f),
            ),
            font = TypefaceState(
                mode = SafeSP.getInt(StackedMobile.TYPE_FONT_MODE, 0),
                path = SafeSP.getString(StackedMobile.TYPE_FONT_PATH_REAL, Constants.VARIABLE_FONT_DEFAULT_PATH),
                condensedWidth = SafeSP.getInt(StackedMobile.TYPE_WIDTH_CONDENSED, 80),
            ),
            small = SmallTypeState(
                size = SafeSP.getFloat(StackedMobile.SMALL_TYPE_SIZE, 7.159973f),
                weight = SafeSP.getInt(StackedMobile.SMALL_TYPE_FONT_WEIGHT, 630),
                showOnStacked = SafeSP.getBoolean(StackedMobile.SMALL_TYPE_SHOW_ON_STACKED, false),
                showOnSingle = SafeSP.getBoolean(StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE, false),
                showRoaming = SafeSP.getBoolean(StackedMobile.SMALL_TYPE_SHOW_ROAMING, false),
            ),
            large = LargeTypeState(
                size = SafeSP.getFloat(StackedMobile.LARGE_TYPE_SIZE, 14f),
                weight = SafeSP.getInt(StackedMobile.LARGE_TYPE_FONT_WEIGHT, 400),
                paddingStart = SafeSP.getFloat(StackedMobile.LARGE_TYPE_PADDING_START_VAL, 2.0f),
                paddingEnd = SafeSP.getFloat(StackedMobile.LARGE_TYPE_PADDING_END_VAL, 2.0f),
                verticalOffset = SafeSP.getFloat(StackedMobile.LARGE_TYPE_VERTICAL_OFFSET, 0.0f),
                hideWhenWifi = SafeSP.getBoolean(StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI, false),
                hideWhenDisconnect = SafeSP.getBoolean(StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT, false),
            )
        )
    }

    fun updatePreference(key: String, value: Any) {
        SafeSP.putAny(key, value)

        _state.update { current ->
            when (key) {
                StackedMobile.ENABLED -> current.copy(enabled = value as Boolean)

                StackedMobile.SIGNAL_SVG_SINGLE -> current.copy(signal = current.signal.copy(singleStyle = value as Int))
                StackedMobile.SIGNAL_SVG_SINGLE_VAL -> current.copy(signal = current.signal.copy(singleSVG = value as String))
                StackedMobile.SIGNAL_SVG_STACKED -> current.copy(signal = current.signal.copy(stackedStyle = value as Int))
                StackedMobile.SIGNAL_SVG_STACKED_VAL -> current.copy(signal = current.signal.copy(stackedSVG = value as String))
                StackedMobile.SIGNAL_ALPHA_FG -> current.copy(signal = current.signal.copy(alphaFg = value as Float))
                StackedMobile.SIGNAL_ALPHA_BG -> current.copy(signal = current.signal.copy(alphaBg = value as Float))
                StackedMobile.SIGNAL_ALPHA_ERROR -> current.copy(signal = current.signal.copy(alphaError = value as Float))

                StackedMobile.TYPE_FONT_MODE -> current.copy(font = current.font.copy(mode = value as Int))
                StackedMobile.TYPE_FONT_PATH_REAL -> current.copy(font = current.font.copy(path = value as String))
                StackedMobile.TYPE_WIDTH_CONDENSED -> current.copy(font = current.font.copy(condensedWidth = value as Int))

                StackedMobile.SMALL_TYPE_SIZE -> current.copy(small = current.small.copy(size = value as Float))
                StackedMobile.SMALL_TYPE_FONT_WEIGHT -> current.copy(small = current.small.copy(weight = value as Int))
                StackedMobile.SMALL_TYPE_SHOW_ON_STACKED -> current.copy(small = current.small.copy(showOnStacked = value as Boolean))
                StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE -> current.copy(small = current.small.copy(showOnSingle = value as Boolean))
                StackedMobile.SMALL_TYPE_SHOW_ROAMING -> current.copy(small = current.small.copy(showRoaming = value as Boolean))

                StackedMobile.LARGE_TYPE_SIZE -> current.copy(large = current.large.copy(size = value as Float))
                StackedMobile.LARGE_TYPE_FONT_WEIGHT -> current.copy(large = current.large.copy(weight = value as Int))
                StackedMobile.LARGE_TYPE_PADDING_START_VAL -> current.copy(large = current.large.copy(paddingStart = value as Float))
                StackedMobile.LARGE_TYPE_PADDING_END_VAL -> current.copy(large = current.large.copy(paddingEnd = value as Float))
                StackedMobile.LARGE_TYPE_VERTICAL_OFFSET -> current.copy(large = current.large.copy(verticalOffset = value as Float))
                StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI -> current.copy(large = current.large.copy(hideWhenWifi = value as Boolean))
                StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT -> current.copy(large = current.large.copy(hideWhenDisconnect = value as Boolean))

                else -> current
            }
        }
    }
}