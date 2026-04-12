package dev.lackluster.mihelper.app.screen.systemui.statusbar

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.state.ViewState
import dev.lackluster.mihelper.app.utils.getFileNameFromUri
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.MLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StatusBarFontViewModel(
    private val fontRepository: FontRepository,
    private val repo: GlobalPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ViewState<UiText>>(ViewState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _fontDisplayName = MutableStateFlow(
        repo.get(Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL)
    )
    val fontDisplayName = _fontDisplayName.asStateFlow()

    fun resetToDefault(target: FontTarget) {
        viewModelScope.launch {
            fontRepository.resetToDefault(target)
            val defaultPath = Constants.VARIABLE_FONT_DEFAULT_PATH
            repo.update(Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL, defaultPath)
            _fontDisplayName.value = defaultPath
        }
    }

    fun importFontFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading
            val result = withContext(Dispatchers.IO) {
                var tempFile: File? = null
                try {
                    val appContext = context.applicationContext
                    val displayName = appContext.getFileNameFromUri(uri)

                    val validExtensions = listOf(".ttf", ".otf", ".ttc")
                    val isValid = validExtensions.any { ext ->
                        displayName.endsWith(ext, ignoreCase = true)
                    }

                    if (!isValid) {
                        throw IllegalArgumentException("Illegal file type $displayName")
                    }

                    tempFile = File(appContext.cacheDir, "temp_import_${FontTarget.STATUS_BAR.name}.ttf")
                    appContext.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { output -> input.copyTo(output) }
                    } ?: throw Exception("Unable to read the selected file stream!")

                    val success = fontRepository.applyCustomFont(tempFile.absolutePath, FontTarget.STATUS_BAR)
                    if (success) {
                        repo.update(Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL, displayName)
                        _fontDisplayName.value = displayName
                    }
                    success
                } catch (e: Exception) {
                    MLog.e(e, "importFontFromUri")
                    false
                } finally {
                    tempFile?.delete()
                }
            }
            handleResult(result)
        }
    }

    fun applyFontFromPath(path: String) {
        if (path == Constants.VARIABLE_FONT_DEFAULT_PATH) {
            resetToDefault(FontTarget.STATUS_BAR)
            return
        }

        viewModelScope.launch {
            _uiState.value = ViewState.Loading
            val result = withContext(Dispatchers.IO) {
                try {
                    val file = File(path)
                    if (!file.exists() || !file.isFile) {
                        throw IllegalArgumentException("The file does not exist!")
                    }

                    val displayName = file.absolutePath

                    val validExtensions = listOf(".ttf", ".otf", ".ttc")
                    val isValid = validExtensions.any { ext ->
                        displayName.endsWith(ext, ignoreCase = true)
                    }

                    if (!isValid) {
                        throw IllegalArgumentException("Illegal file type $displayName")
                    }

                    val success = fontRepository.applyCustomFont(file.absolutePath, FontTarget.STATUS_BAR)
                    if (success) {
                        repo.update(Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL, path)
                        _fontDisplayName.value = path
                    }
                    success
                } catch (e: Exception) {
                    MLog.e(e, "applyFontFromPath")
                    false
                }
            }
            handleResult(result)
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