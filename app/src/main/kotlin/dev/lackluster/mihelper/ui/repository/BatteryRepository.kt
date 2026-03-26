package dev.lackluster.mihelper.ui.repository

import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.model.BatteryState
import dev.lackluster.mihelper.ui.model.FontState
import dev.lackluster.mihelper.ui.model.SizeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object BatteryRepository {
    private val _state = MutableStateFlow(loadInitialConfig())
    val state = _state.asStateFlow()
    
    private fun loadInitialConfig(): BatteryState {
        return BatteryState(
            styleStatusBar = SafeSP.getInt(IconTuner.BATTERY_STYLE, 0),
            styleControlCenter = SafeSP.getInt(IconTuner.BATTERY_STYLE_CC, 0),
            customPadding = SafeSP.getBoolean(IconTuner.BATTERY_PADDING_HORIZON, false),
            paddingStart = SafeSP.getFloat(IconTuner.BATTERY_PADDING_START_VAL, 0f),
            paddingEnd = SafeSP.getFloat(IconTuner.BATTERY_PADDING_END_VAL, 0f),
            hideCharge = SafeSP.getBoolean(IconTuner.HIDE_BATTERY_CHARGE_OUT, false),
            percentMarkStyle = SafeSP.getInt(IconTuner.BATTERY_PERCENT_MARK_STYLE, 0),
            percentInSize = SizeState(
                enabled = SafeSP.getBoolean(IconTuner.BATTERY_PERCENT_IN_SIZE, false),
                size = SafeSP.getFloat(IconTuner.BATTERY_PERCENT_IN_SIZE_VAL, 9.599976f),
            ),
            percentOutSize = SizeState(
                enabled = SafeSP.getBoolean(IconTuner.BATTERY_PERCENT_OUT_SIZE, false),
                size = SafeSP.getFloat(IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL, 12.5f),
            ),
            percentInFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.BATTERY_PERCENTAGE_IN, false),
                weight = SafeSP.getInt(FontWeight.BATTERY_PERCENTAGE_IN_VAL, 620),
            ),
            percentOutFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.BATTERY_PERCENTAGE_OUT, false),
                weight = SafeSP.getInt(FontWeight.BATTERY_PERCENTAGE_OUT_VAL, 500),
            ),
            percentMarkFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.BATTERY_PERCENTAGE_MARK, false),
                weight = SafeSP.getInt(FontWeight.BATTERY_PERCENTAGE_MARK_VAL, 600),
            )
        )
    }

    fun updatePreference(key: String, value: Any) {
        SafeSP.putAny(key, value)

        _state.update { current ->
            when (key) {
                IconTuner.BATTERY_STYLE -> current.copy(styleStatusBar = value as Int)
                IconTuner.BATTERY_STYLE_CC -> current.copy(styleControlCenter = value as Int)
                IconTuner.BATTERY_PADDING_HORIZON -> current.copy(customPadding = value as Boolean)
                IconTuner.BATTERY_PADDING_START_VAL -> current.copy(paddingStart = value as Float)
                IconTuner.BATTERY_PADDING_END_VAL -> current.copy(paddingEnd = value as Float)
                IconTuner.HIDE_BATTERY_CHARGE_OUT -> current.copy(hideCharge = value as Boolean)
                IconTuner.BATTERY_PERCENT_MARK_STYLE -> current.copy(percentMarkStyle = value as Int)

                IconTuner.BATTERY_PERCENT_IN_SIZE -> current.copy(percentInSize = current.percentInSize.copy(enabled = value as Boolean))
                IconTuner.BATTERY_PERCENT_IN_SIZE_VAL -> current.copy(percentInSize = current.percentInSize.copy(size = value as Float))
                IconTuner.BATTERY_PERCENT_OUT_SIZE -> current.copy(percentOutSize = current.percentOutSize.copy(enabled = value as Boolean))
                IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL -> current.copy(percentOutSize = current.percentOutSize.copy(size = value as Float))

                FontWeight.BATTERY_PERCENTAGE_IN -> current.copy(percentInFont = current.percentInFont.copy(enabled = value as Boolean))
                FontWeight.BATTERY_PERCENTAGE_IN_VAL -> current.copy(percentInFont = current.percentInFont.copy(weight = value as Int))
                FontWeight.BATTERY_PERCENTAGE_OUT -> current.copy(percentOutFont = current.percentOutFont.copy(enabled = value as Boolean))
                FontWeight.BATTERY_PERCENTAGE_OUT_VAL -> current.copy(percentOutFont = current.percentOutFont.copy(weight = value as Int))
                FontWeight.BATTERY_PERCENTAGE_MARK -> current.copy(percentMarkFont = current.percentMarkFont.copy(enabled = value as Boolean))
                FontWeight.BATTERY_PERCENTAGE_MARK_VAL -> current.copy(percentMarkFont = current.percentMarkFont.copy(weight = value as Int))

                else -> current
            }
        }
    }
}