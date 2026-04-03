package dev.lackluster.mihelper.app.screen.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.state.ViewState
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ModuleSettingsAction {
    object RequestExportBackup : ModuleSettingsAction
    object RequestImportBackup : ModuleSettingsAction
    data class ExportBackup(val uri: Uri) : ModuleSettingsAction
    data class ImportBackup(val uri: Uri) : ModuleSettingsAction
    data class ToggleHideIcon(val isHidden: Boolean) : ModuleSettingsAction
    object ResetSettings : ModuleSettingsAction
}

sealed interface ModuleSettingsEvent {
    data class SetLauncherIcon(val isHidden: Boolean) : ModuleSettingsEvent
    object RestartApp : ModuleSettingsEvent
}

data class ActionSuccess(
    val message: UiText,
    val requireRestart: Boolean = false
)

data class ModuleSettingsState(
    val isIconHidden: Boolean = false
)

class ModuleSettingsViewModel(
    private val repo: GlobalPreferencesRepository
) : ViewModel() {
    private val _uiEvent = Channel<ModuleSettingsEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<ViewState<ActionSuccess>>(ViewState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _moduleSettingsState = MutableStateFlow(
        ModuleSettingsState(
            isIconHidden = repo.get(Preferences.App.HIDE_ICON),
        )
    )
    val moduleSettingsState = _moduleSettingsState.asStateFlow()

    fun handleAction(action: ModuleSettingsAction) {
        when (action) {
            is ModuleSettingsAction.ToggleHideIcon -> {
                repo.update(Preferences.App.HIDE_ICON, action.isHidden)
                _moduleSettingsState.update { current ->
                    current.copy(isIconHidden = action.isHidden)
                }
                viewModelScope.launch {
                    _uiEvent.send(ModuleSettingsEvent.SetLauncherIcon(action.isHidden))
                }
            }
            is ModuleSettingsAction.ExportBackup -> backup(action.uri)
            is ModuleSettingsAction.ImportBackup -> restore(action.uri)
            is ModuleSettingsAction.ResetSettings -> reset()
            else -> {}
        }
    }

    fun onDialogConfirmed() {
        val currentState = _uiState.value
        _uiState.value = ViewState.Idle
        if (currentState is ViewState.Success && currentState.data.requireRestart) {
            viewModelScope.launch {
                delay(500)
                _uiEvent.send(ModuleSettingsEvent.RestartApp)
            }
        }
    }

    fun backup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading
            repo.exportBackup(uri).fold(
                onSuccess = {
                    _uiState.value = ViewState.Success(
                        ActionSuccess(
                            message = R.string.module_backup_success.toUiText(),
                            requireRestart = false
                        )
                    )
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Unknown Error"
                    _uiState.value = ViewState.Error(
                        R.string.module_backup_failure.toUiText(errorMsg)
                    )
                }
            )
        }
    }

    fun restore(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading
            repo.importBackup(uri).fold(
                onSuccess = {
                    _uiState.value = ViewState.Success(
                        ActionSuccess(
                            message = R.string.module_restore_success.toUiText(),
                            requireRestart = true
                        )
                    )
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Unknown Error"
                    _uiState.value = ViewState.Error(
                        R.string.module_restore_failure.toUiText(errorMsg)
                    )
                }
            )
        }
    }

    fun reset() {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading

            repo.resetSettings().fold(
                onSuccess = {
                    _uiState.value = ViewState.Success(
                        ActionSuccess(
                            message = R.string.module_reset_success.toUiText(),
                            requireRestart = true
                        )
                    )
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Unknown Error"
                    _uiState.value = ViewState.Error(
                        R.string.module_reset_failure.toUiText(errorMsg)
                    )
                }
            )
        }
    }
}