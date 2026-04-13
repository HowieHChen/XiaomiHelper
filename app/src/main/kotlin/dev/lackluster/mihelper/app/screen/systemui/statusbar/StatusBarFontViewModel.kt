package dev.lackluster.mihelper.app.screen.systemui.statusbar

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatusBarFontViewModel(
    private val prefRepo: GlobalPreferencesRepository,
    private val fontRepo: FontRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ViewState<UiText>>(ViewState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _fontDisplayName = MutableStateFlow(
        prefRepo.get(FontTarget.STATUS_BAR.displayNameSpKey)
    )
    val fontDisplayName = _fontDisplayName.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.Default) {
            prefRepo.preferenceUpdates.collect { key ->
                if (key == FontTarget.STATUS_BAR.displayNameSpKey) {
                    _fontDisplayName.update {
                        prefRepo.get(FontTarget.STATUS_BAR.displayNameSpKey)
                    }
                }
            }
        }
    }

    fun resetToDefault(target: FontTarget) {
        viewModelScope.launch {
            fontRepo.resetToDefault(target)
        }
    }

    fun importFontFromUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading

            val success = fontRepo.importFontFromUri(uri, FontTarget.STATUS_BAR)

            handleResult(success)
        }
    }

    fun applyFontFromPath(path: String) {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading

            val success = fontRepo.applyFontFromPath(path, FontTarget.STATUS_BAR)

            handleResult(success)
        }
    }

    private fun handleResult(isSuccess: Boolean) {
        if (isSuccess) {
            _uiState.value = ViewState.Success(UiText.StringResource(R.string.font_general_path_success))
        } else {
            _uiState.value = ViewState.Error(UiText.StringResource(R.string.font_general_path_failure))
        }
    }

    fun resetState() {
        _uiState.value = ViewState.Idle
    }
}