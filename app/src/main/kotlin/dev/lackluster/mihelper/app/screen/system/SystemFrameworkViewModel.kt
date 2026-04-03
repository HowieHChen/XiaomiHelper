package dev.lackluster.mihelper.app.screen.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.app.utils.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SystemFrameworkState(
    val currentFontScale: Float = 1.0f
)

sealed interface SystemFrameworkAction {
    object RefreshFontScale : SystemFrameworkAction
    data class UpdateFontScale(val scale: Float) : SystemFrameworkAction
}

class SystemFrameworkViewModel : ViewModel() {
    private val _state = MutableStateFlow(SystemFrameworkState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiText>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        handleAction(SystemFrameworkAction.RefreshFontScale)
    }

    fun handleAction(action: SystemFrameworkAction) {
        when (action) {
            is SystemFrameworkAction.RefreshFontScale -> {
                viewModelScope.launch {
                    val result = SystemCommander.execAsync(
                        command = "settings get system font_scale",
                        useRoot = true,
                        silent = true
                    )
                    if (result.isSuccess) {
                        val scale = result.out.trim().toFloatOrNull() ?: 1.0f
                        _state.value = _state.value.copy(currentFontScale = scale)
                    } else {
                        _uiEvent.send(R.string.android_display_temp_font_scale_fail_msg.toUiText())
                    }
                }
            }
            is SystemFrameworkAction.UpdateFontScale -> {
                viewModelScope.launch {
                    val result = SystemCommander.execAsync(
                        command = "settings put system font_scale ${action.scale}",
                        useRoot = true,
                        silent = true
                    )
                    if (result.isSuccess) {
                        _state.value = _state.value.copy(currentFontScale = action.scale)
                    } else {
                        _uiEvent.send(R.string.android_display_temp_font_scale_fail_msg.toUiText())
                    }
                }
            }
        }
    }
}