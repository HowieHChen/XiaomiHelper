package dev.lackluster.mihelper.ui.repository

import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.model.WlanState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object WlanRepository {
    private val _state = MutableStateFlow(loadInitialConfig())
    val state = _state.asStateFlow()

    private fun loadInitialConfig(): WlanState {
        return WlanState(
            hideWifiStandard = SafeSP.getBoolean(IconTuner.HIDE_WIFI_STANDARD, false),
            hideWifiActivity = SafeSP.getBoolean(IconTuner.HIDE_WIFI_ACTIVITY, false),
            rightWifiActivity = SafeSP.getBoolean(IconTuner.WIFI_ACTIVITY_RIGHT, false),
            hideWifiUnavailable = SafeSP.getBoolean(IconTuner.HIDE_WIFI_UNAVAILABLE, false)
        )
    }

    fun updatePreference(key: String, value: Any) {
        SafeSP.putAny(key, value)

        _state.update { current ->
            when (key) {
                IconTuner.HIDE_WIFI_STANDARD -> current.copy(hideWifiStandard = value as Boolean)
                IconTuner.HIDE_WIFI_ACTIVITY -> current.copy(hideWifiActivity = value as Boolean)
                IconTuner.WIFI_ACTIVITY_RIGHT -> current.copy(rightWifiActivity = value as Boolean)
                IconTuner.HIDE_WIFI_UNAVAILABLE -> current.copy(hideWifiUnavailable = value as Boolean)

                else -> current
            }
        }
    }
}