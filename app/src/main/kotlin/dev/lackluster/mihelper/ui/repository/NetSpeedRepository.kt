package dev.lackluster.mihelper.ui.repository

import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.model.FontState
import dev.lackluster.mihelper.ui.model.NetSpeedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NetSpeedRepository {
    private val _state = MutableStateFlow(loadInitialConfig())
    val state = _state.asStateFlow()

    private fun loadInitialConfig(): NetSpeedState {
        return NetSpeedState(
            style = SafeSP.getInt(IconTuner.NET_SPEED_MODE, 0),
            unitStyle = SafeSP.getInt(IconTuner.NET_SPEED_UNIT_MODE, 0),
            refreshPerSecond = SafeSP.getBoolean(IconTuner.NET_SPEED_REFRESH, false),
            numberFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.NET_SPEED_NUMBER, false),
                weight = SafeSP.getInt(FontWeight.NET_SPEED_NUMBER_VAL, 630),
            ),
            unitFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.NET_SPEED_UNIT, false),
                weight = SafeSP.getInt(FontWeight.NET_SPEED_UNIT_VAL, 630),
            ),
            separateStyleFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.NET_SPEED_SEPARATE, false),
                weight = SafeSP.getInt(FontWeight.NET_SPEED_SEPARATE_VAL, 630),
            ),
        )
    }

    fun updatePreference(key: String, value: Any) {
        SafeSP.putAny(key, value)

        _state.update { current ->
            when (key) {
                IconTuner.NET_SPEED_MODE -> current.copy(style = value as Int)
                IconTuner.NET_SPEED_UNIT_MODE -> current.copy(unitStyle = value as Int)
                IconTuner.NET_SPEED_REFRESH -> current.copy(refreshPerSecond = value as Boolean)

                FontWeight.NET_SPEED_NUMBER -> current.copy(numberFont = current.numberFont.copy(enabled = value as Boolean))
                FontWeight.NET_SPEED_NUMBER_VAL -> current.copy(numberFont = current.numberFont.copy(weight = value as Int))
                FontWeight.NET_SPEED_UNIT -> current.copy(numberFont = current.unitFont.copy(enabled = value as Boolean))
                FontWeight.NET_SPEED_UNIT_VAL -> current.copy(numberFont = current.unitFont.copy(weight = value as Int))
                FontWeight.NET_SPEED_SEPARATE -> current.copy(numberFont = current.separateStyleFont.copy(enabled = value as Boolean))
                FontWeight.NET_SPEED_SEPARATE_VAL -> current.copy(numberFont = current.separateStyleFont.copy(weight = value as Int))

                else -> current
            }
        }
    }
}