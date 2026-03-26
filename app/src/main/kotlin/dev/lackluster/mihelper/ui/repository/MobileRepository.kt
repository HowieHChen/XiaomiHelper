package dev.lackluster.mihelper.ui.repository

import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.model.FontState
import dev.lackluster.mihelper.ui.model.MobileState
import dev.lackluster.mihelper.ui.model.SizeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MobileRepository {
    private val _state = MutableStateFlow(loadInitialConfig())
    val state = _state.asStateFlow()

    private fun loadInitialConfig(): MobileState {
        return MobileState(
            hideSimAuto = SafeSP.getBoolean(IconTuner.HIDE_SIM_AUTO, false),
            hideSimOne = SafeSP.getBoolean(IconTuner.HIDE_SIM_ONE, false),
            hideSimTwo = SafeSP.getBoolean(IconTuner.HIDE_SIM_TWO, false),
            hideActivity = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_ACTIVITY, false),
            hideSmallType = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_TYPE, false),
            hideRoamGlobal = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_ROAM_GLOBAL, false),
            hideLargeRoam =  SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_ROAM, false),
            hideSmallRoam = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_SMALL_ROAM, false),
            hideVoWifi = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_VO_WIFI, false),
            hideVoLte = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_VOLTE, false),
            hideVoLteNoService = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_VOLTE_NO_SERVICE, false),
            hideSpeechHd = SafeSP.getBoolean(IconTuner.HIDE_CELLULAR_SPEECH_HD, false),
            separateType = SafeSP.getBoolean(IconTuner.CELLULAR_TYPE_SINGLE, false),
            rightSeparateType = SafeSP.getBoolean(IconTuner.CELLULAR_TYPE_SINGLE_SWAP, false),
            customTypeMap = SafeSP.getBoolean(IconTuner.CELLULAR_TYPE_CUSTOM, false),
            typeMapString = SafeSP.getString(IconTuner.CELLULAR_TYPE_CUSTOM_VAL, Pref.DefValue.SystemUI.CELLULAR_TYPE_LIST),
            separateTypeSize = SizeState(
                enabled = SafeSP.getBoolean(IconTuner.CELLULAR_TYPE_SINGLE_SIZE, false),
                size = SafeSP.getFloat(IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL, 14.0f),
            ),
            smallTypeFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.CELLULAR_TYPE, false),
                weight = SafeSP.getInt(FontWeight.CELLULAR_TYPE_VAL, 660),
            ),
            separateTypeFont = FontState(
                enabled = SafeSP.getBoolean(FontWeight.CELLULAR_TYPE_SINGLE, false),
                weight = SafeSP.getInt(FontWeight.CELLULAR_TYPE_SINGLE_VAL, 400)
            )
        )
    }

    fun updatePreference(key: String, value: Any) {
        SafeSP.putAny(key, value)
        
        _state.update { current ->
            when (key) {
                IconTuner.HIDE_SIM_AUTO -> current.copy(hideSimAuto = value as Boolean)
                IconTuner.HIDE_SIM_ONE -> current.copy(hideSimOne = value as Boolean)
                IconTuner.HIDE_SIM_TWO -> current.copy(hideSimTwo = value as Boolean)
                IconTuner.HIDE_CELLULAR_ACTIVITY -> current.copy(hideActivity = value as Boolean)
                IconTuner.HIDE_CELLULAR_TYPE -> current.copy(hideSmallType = value as Boolean)
                IconTuner.HIDE_CELLULAR_ROAM_GLOBAL -> current.copy(hideRoamGlobal = value as Boolean)
                IconTuner.HIDE_CELLULAR_ROAM -> current.copy(hideLargeRoam = value as Boolean)
                IconTuner.HIDE_CELLULAR_SMALL_ROAM -> current.copy(hideSmallRoam = value as Boolean)
                IconTuner.HIDE_CELLULAR_VO_WIFI -> current.copy(hideVoWifi = value as Boolean)
                IconTuner.HIDE_CELLULAR_VOLTE -> current.copy(hideVoLte = value as Boolean)
                IconTuner.HIDE_CELLULAR_VOLTE_NO_SERVICE -> current.copy(hideVoLteNoService = value as Boolean)
                IconTuner.HIDE_CELLULAR_SPEECH_HD -> current.copy(hideSpeechHd = value as Boolean)
                IconTuner.CELLULAR_TYPE_SINGLE -> current.copy(separateType = value as Boolean)
                IconTuner.CELLULAR_TYPE_SINGLE_SWAP -> current.copy(rightSeparateType = value as Boolean)
                IconTuner.CELLULAR_TYPE_CUSTOM -> current.copy(customTypeMap = value as Boolean)
                IconTuner.CELLULAR_TYPE_CUSTOM_VAL -> current.copy(typeMapString = value as String)

                IconTuner.CELLULAR_TYPE_SINGLE_SIZE -> current.copy(separateTypeSize = current.separateTypeSize.copy(enabled = value as Boolean))
                IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL -> current.copy(separateTypeSize = current.separateTypeSize.copy(size = value as Float))

                FontWeight.CELLULAR_TYPE -> current.copy(smallTypeFont = current.smallTypeFont.copy(enabled = value as Boolean))
                FontWeight.CELLULAR_TYPE_VAL -> current.copy(smallTypeFont = current.smallTypeFont.copy(weight = value as Int))
                FontWeight.CELLULAR_TYPE_SINGLE -> current.copy(separateTypeFont = current.separateTypeFont.copy(enabled = value as Boolean))
                FontWeight.CELLULAR_TYPE_SINGLE_VAL -> current.copy(separateTypeFont = current.separateTypeFont.copy(weight = value as Int))

                else -> current
            }
        }
    }
}