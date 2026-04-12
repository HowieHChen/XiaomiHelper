package dev.lackluster.mihelper.app.screen.systemui.icon.position

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.model.StatusBarIconSlotWrap
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IconPositionViewModel(
    private val repo: GlobalPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<StatusBarIconSlotWrap>>(emptyList())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialSlots()
        viewModelScope.launch(Dispatchers.Default) {
            repo.preferenceUpdates.collect { updatedKey ->
                if (updatedKey == Preferences.SystemUI.StatusBar.IconTuner.ICON_POSITION_VAL) {
                    loadInitialSlots()
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.globalReloadEvent.collect {
                loadInitialSlots()
            }
        }
    }

    private fun loadInitialSlots() {
        val slotsOrderStr = repo.get(Preferences.SystemUI.StatusBar.IconTuner.ICON_POSITION_VAL)
            .mapNotNull { str -> str.split(":").takeIf { it.size == 2 } }
            .sortedBy { it[0].toInt() }
            .map { it[1] }
            .ifEmpty { Constants.STATUS_BAR_ICONS_DEFAULT }

        val finalSlots = processExtraIcons(slotsOrderStr)
        _uiState.value = finalSlots.mapNotNull { Constants.STATUS_BAR_ICON_SLOT_MAP[it] }
    }

    fun getDefaultSlots(): List<StatusBarIconSlotWrap> {
        val defaultSlots = processExtraIcons(Constants.STATUS_BAR_ICONS_DEFAULT)
        return defaultSlots.mapNotNull { Constants.STATUS_BAR_ICON_SLOT_MAP[it] }
    }

    fun savePositions(slots: List<StatusBarIconSlotWrap>) {
        val toSave = slots.mapIndexed { index, wrap -> "${index}:${wrap.slot}" }.toSet()
        repo.update(Preferences.SystemUI.StatusBar.IconTuner.ICON_POSITION_VAL, toSave)
    }

    private fun processExtraIcons(list: List<String>): List<String> {
        val mutableList = list.toMutableList()

        if (!mutableList.contains(Constants.IconSlots.COMPOUND_ICON_STUB)) {
            mutableList.add(
                mutableList.indexOf(Constants.IconSlots.ZEN).coerceAtLeast(0),
                Constants.IconSlots.COMPOUND_ICON_STUB
            )
        }

        val stackedIcons = listOf(
            Constants.IconSlots.SINGLE_MOBILE_SIM1,
            Constants.IconSlots.SINGLE_MOBILE_SIM2,
            Constants.IconSlots.STACKED_MOBILE_ICON,
            Constants.IconSlots.STACKED_MOBILE_TYPE
        )

        var insertIndex = mutableList.indexOf(Constants.IconSlots.DEMO_MOBILE).coerceAtLeast(0) + 1

        stackedIcons.forEach { icon ->
            if (!mutableList.contains(icon)) {
                mutableList.add(insertIndex, icon)
            }
            insertIndex = mutableList.indexOf(icon) + 1
        }

        return mutableList
    }
}