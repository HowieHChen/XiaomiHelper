package dev.lackluster.mihelper.app.screen.systemui.icon.detail

import android.graphics.Typeface
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontMode
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.repository.StackedMobileRepository
import dev.lackluster.mihelper.app.utils.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StackedMobileViewModel(
    private val stackedRepo: StackedMobileRepository,
    private val fontRepo: FontRepository,
) : ViewModel() {
    private val _screenState = MutableStateFlow(IconDetailPageState())
    val screenState = _screenState.asStateFlow()

    private val _fontUpdateTrigger = MutableStateFlow(0)
    val fontUpdateTrigger = _fontUpdateTrigger.asStateFlow()

    val configState = stackedRepo.configState
    val stackedPictures = stackedRepo.stackedPictures
    val singlePictures = stackedRepo.singlePictures
    val stackedAnchor = stackedRepo.stackedAnchor
    val singleAnchor = stackedRepo.singleAnchor

     init {
         viewModelScope.launch {
             fontRepo.fontUpdateEvent.collect { target ->
                 if (target == FontTarget.STACKED_TYPE) {
                     _fontUpdateTrigger.update { it + 1 }
                 }
             }
         }
     }

    fun dismissErrorDialog() {
        _screenState.update { it.copy(errorDialogMessage = null) }
    }

    fun importFontFromUri(uri: Uri) {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

            val success = fontRepo.importFontFromUri(uri, FontTarget.STACKED_TYPE)

            _screenState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = if (success) null else R.string.font_general_path_failure.toUiText()
                )
            }
        }
    }

    fun applyFontFromPath(path: String) {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

            val success = fontRepo.applyFontFromPath(path, FontTarget.STACKED_TYPE)

            _screenState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = if (success) null else R.string.font_general_path_failure.toUiText()
                )
            }
        }
    }

    fun handleSvgFileUri(uri: Uri, isStacked: Boolean) {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

           val result = stackedRepo.importSvgFromUri(uri, isStacked)

            result.onSuccess {
                _screenState.update { it.copy(isLoading = false, errorDialogMessage = null) }
            }.onFailure { error ->
                _screenState.update { it.copy(isLoading = false, errorDialogMessage = error.message.toUiText()) }
            }
        }
    }

    fun getTypeface(mode: FontMode): Typeface = stackedRepo.getTypeface(mode)
}