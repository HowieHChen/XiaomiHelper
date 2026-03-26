package dev.lackluster.mihelper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.ui.model.IconDetailPageState
import dev.lackluster.mihelper.ui.model.IconTab
import dev.lackluster.mihelper.ui.repository.BatteryRepository
import dev.lackluster.mihelper.ui.repository.MobileRepository
import dev.lackluster.mihelper.ui.repository.NetSpeedRepository
import dev.lackluster.mihelper.ui.repository.WlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IconDetailPageViewModel : ViewModel() {
    private val _pageUiState = MutableStateFlow(IconDetailPageState())
    val pageUiState = _pageUiState.asStateFlow()

    val mobileState = MobileRepository.state
    val wlanState = WlanRepository.state
    val batteryState = BatteryRepository.state
    val netSpeedState = NetSpeedRepository.state

    fun selectTab(tab: IconTab) {
        _pageUiState.update { it.copy(selectedTab = tab) }
    }

    fun dismissErrorDialog() {
        _pageUiState.update { it.copy(errorDialogMessage = null) }
    }

    fun validateAndUpdateCustomTypeMap(typeList: String, invalidHint: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _pageUiState.update { it.copy(isLoading = true) }
            if (typeList.isBlank()) {
                _pageUiState.update {
                    it.copy(isLoading = false, errorDialogMessage = "SVG 不能为空")
                }
                return@launch
            }
            val list = typeList.split(',', ' ', '，')
            val valid = (list.size == 15) || (list.size == 1 && list.first().isNotBlank())
            if (valid) {
                MobileRepository.updatePreference(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM_VAL, typeList)
            }
            _pageUiState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = if (valid) null else invalidHint
                )
            }
        }
    }

    fun updateMobilePreference(key: String, value: Any) {
        MobileRepository.updatePreference(key, value)
    }

    fun updateWlanPreference(key: String, value: Any) {
        WlanRepository.updatePreference(key, value)
    }

    fun updateBatteryPreference(key: String, value: Any) {
        BatteryRepository.updatePreference(key, value)
    }

    fun updateNetSpeedPreference(key: String, value: Any) {
        NetSpeedRepository.updatePreference(key, value)
    }
}