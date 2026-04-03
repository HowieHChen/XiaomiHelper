package dev.lackluster.mihelper.app.screen.systemui.icon.detail

import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontMode
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val mobileKeys: Set<PreferenceKey<*>> = setOf(
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_AUTO,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_ONE,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_TWO,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ACTIVITY,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_TYPE,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ROAM_GLOBAL,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_LARGE_ROAM,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SMALL_ROAM,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VO_WIFI,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE_NO_SERVICE,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SPEECH_HD,
    Preferences.SystemUI.StatusBar.IconDetail.USE_CELLULAR_TYPE_SINGLE,
    Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SWAP_INDEX,
    Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_LIST,
    Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_LIST_VAL,
    Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_SINGLE_SIZE,
    Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SIZE_VAL,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE,
    Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_WEIGHT,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE_SINGLE,
    Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_SINGLE_WEIGHT
)

private val wlanKeys: Set<PreferenceKey<*>> = setOf(
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_STANDARD,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_ACTIVITY,
    Preferences.SystemUI.StatusBar.IconDetail.WIFI_ACTIVITY_RIGHT,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_UNAVAILABLE
)

private val batteryKeys: Set<PreferenceKey<*>> = setOf(
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_BAR,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_CC,
    Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PADDING_HORIZON,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_START_VAL,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_END_VAL,
    Preferences.SystemUI.StatusBar.IconDetail.HIDE_BATTERY_CHARGE_OUT,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_MARK_STYLE,
    Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_IN_SIZE,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_IN_SIZE_VAL,
    Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_OUT_SIZE,
    Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_OUT_SIZE_VAL,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_IN,
    Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_IN_WEIGHT,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_OUT,
    Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_OUT_WEIGHT,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_MARK,
    Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_MARK_WEIGHT
)

private val netSpeedKeys: Set<PreferenceKey<*>> = setOf(
    Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_MODE,
    Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_UNIT_MODE,
    Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_REFRESH,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_NUMBER,
    Preferences.SystemUI.StatusBar.Font.NET_SPEED_NUMBER_WEIGHT,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_UNIT,
    Preferences.SystemUI.StatusBar.Font.NET_SPEED_UNIT_WEIGHT,
    Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_SEPARATE,
    Preferences.SystemUI.StatusBar.Font.NET_SPEED_SEPARATE_WEIGHT
)

class IconDetailViewModel(
    private val fontRepo: FontRepository,
    private val prefRepo: GlobalPreferencesRepository
) : ViewModel() {
    private val _pageUiState = MutableStateFlow(IconDetailPageState())
    val pageUiState = _pageUiState.asStateFlow()

    private val _mobileState = MutableStateFlow(loadMobileConfig())
    val mobileState = _mobileState.asStateFlow()

    private val _wlanState = MutableStateFlow(loadWlanConfig())
    val wlanState = _wlanState.asStateFlow()

    private val _batteryState = MutableStateFlow(loadBatteryConfig())
    val batteryState = _batteryState.asStateFlow()

    private val _netSpeedState = MutableStateFlow(loadNetSpeedConfig())
    val netSpeedState = _netSpeedState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            prefRepo.preferenceUpdates.collect { updatedKey ->
                when (updatedKey) {
                    in mobileKeys -> _mobileState.update { loadMobileConfig() }
                    in wlanKeys -> _wlanState.update { loadWlanConfig() }
                    in batteryKeys -> _batteryState.update { loadBatteryConfig() }
                    in netSpeedKeys -> _netSpeedState.update { loadNetSpeedConfig() }
                }
            }
        }
    }

    fun selectTab(tab: IconTab) {
        _pageUiState.update { it.copy(selectedTab = tab) }
    }

    fun dismissErrorDialog() {
        _pageUiState.update { it.copy(errorDialogMessage = null) }
    }

    fun validateAndUpdateCustomTypeMap(typeList: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _pageUiState.update { it.copy(isLoading = true) }

            if (typeList.isBlank()) {
                _pageUiState.update {
                    it.copy(
                        isLoading = false,
                        errorDialogMessage = R.string.common_invalid_input.toUiText()
                    )
                }
                return@launch
            }

            val list = typeList.split(',', ' ', '，')
            val valid = (list.size == 15) || (list.size == 1 && list.first().isNotBlank())

            if (valid) {
                prefRepo.update(Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_LIST_VAL, typeList)
            }

            _pageUiState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = if (valid) null else R.string.common_invalid_input.toUiText()
                )
            }
        }
    }

    fun getFontFamily(isCustom: Boolean, weight: Int): FontFamily {
        val mode = if (isCustom) FontMode.FROM_FILE else FontMode.MI_SANS
        return fontRepo.getFontFamily(
            target = FontTarget.STATUS_BAR,
            weight = weight,
            mode = mode,
            condensedWidth = 100,
            isCondensed = false
        )
    }

    private fun loadMobileConfig(): MobileState {
        return MobileState(
            hideSimAuto = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_AUTO),
            hideSimOne = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_ONE),
            hideSimTwo = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_TWO),
            hideActivity = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ACTIVITY),
            hideSmallType = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_TYPE),
            hideRoamGlobal = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ROAM_GLOBAL),
            hideLargeRoam = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_LARGE_ROAM),
            hideSmallRoam = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SMALL_ROAM),
            hideVoWifi = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VO_WIFI),
            hideVoLte = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE),
            hideVoLteNoService = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE_NO_SERVICE),
            hideSpeechHd = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SPEECH_HD),
            separateType = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.USE_CELLULAR_TYPE_SINGLE),
            rightSeparateType = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SWAP_INDEX),
            customTypeMap = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_LIST),
            typeMapString = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_LIST_VAL),
            separateTypeSize = SizeState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_SINGLE_SIZE),
                size = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SIZE_VAL),
            ),
            smallTypeFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_WEIGHT),
            ),
            separateTypeFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE_SINGLE),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_SINGLE_WEIGHT)
            )
        )
    }

    private fun loadWlanConfig(): WlanState {
        return WlanState(
            hideWifiStandard = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_STANDARD),
            hideWifiActivity = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_ACTIVITY),
            rightWifiActivity = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.WIFI_ACTIVITY_RIGHT),
            hideWifiUnavailable = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_UNAVAILABLE),
        )
    }

    private fun loadBatteryConfig(): BatteryState {
        return BatteryState(
            styleStatusBar = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_BAR),
            styleControlCenter = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_CC),
            customPadding = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PADDING_HORIZON),
            paddingStart = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_START_VAL),
            paddingEnd = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_END_VAL),
            hideCharge = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.HIDE_BATTERY_CHARGE_OUT),
            percentMarkStyle = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_MARK_STYLE),
            percentInSize = SizeState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_IN_SIZE),
                size = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_IN_SIZE_VAL),
            ),
            percentOutSize = SizeState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_OUT_SIZE),
                size = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_OUT_SIZE_VAL),
            ),
            percentInFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_IN),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_IN_WEIGHT),
            ),
            percentOutFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_OUT),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_OUT_WEIGHT),
            ),
            percentMarkFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_MARK),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_MARK_WEIGHT),
            )
        )
    }

    private fun loadNetSpeedConfig(): NetSpeedState {
        return NetSpeedState(
            style = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_MODE),
            unitStyle = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_UNIT_MODE),
            refreshPerSecond = prefRepo.get(Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_REFRESH),
            numberFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_NUMBER),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.NET_SPEED_NUMBER_WEIGHT),
            ),
            unitFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_UNIT),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.NET_SPEED_UNIT_WEIGHT),
            ),
            separateStyleFont = FontState(
                enabled = prefRepo.get(Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_SEPARATE),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.Font.NET_SPEED_SEPARATE_WEIGHT),
            ),
        )
    }
}