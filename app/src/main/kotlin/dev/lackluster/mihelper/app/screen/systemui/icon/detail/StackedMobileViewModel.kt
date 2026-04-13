package dev.lackluster.mihelper.app.screen.systemui.icon.detail

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.FontMode
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.repository.StackedMobileRepository
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.MLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    fun handleSvgFileUri(context: Context, uri: Uri, isStacked: Boolean) {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

            val (fileName, svgContent) = withContext(Dispatchers.IO) {
                val name = getFileNameFromUri(context, uri)
                val content = try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader().readText()
                    }
                } catch (e: Exception) {
                    MLog.e(e, "读取 SVG 文件异常")
                    null
                }
                Pair(name, content)
            }

            if (!fileName.lowercase().endsWith(".svg")) {
                _screenState.update {
                    it.copy(
                        isLoading = false,
                        errorDialogMessage = "Invalid file type: extension is not .svg".toUiText()
                    )
                }
                return@launch
            }
            if (svgContent.isNullOrBlank()) {
                _screenState.update {
                    it.copy(
                        isLoading = false,
                        errorDialogMessage = "Empty file".toUiText()
                    )
                }
                return@launch
            }

            val result = if (isStacked) {
                stackedRepo.validateAndUpdateSignalSVG(
                    svgContent = svgContent,
                    svgName = fileName,
                    requiredIds = listOf("signal_1_1", "signal_1_2", "signal_1_3", "signal_1_4", "signal_2_1", "signal_2_2", "signal_2_3", "signal_2_4"),
                    contentKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_VAL,
                    nameKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME,
                )
            } else {
                stackedRepo.validateAndUpdateSignalSVG(
                    svgContent = svgContent,
                    svgName = fileName,
                    requiredIds = listOf("signal_1", "signal_2", "signal_3", "signal_4"),
                    contentKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_VAL,
                    nameKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME,
                )
            }

            result.onSuccess {
                _screenState.update { it.copy(isLoading = false, errorDialogMessage = null) }
            }.onFailure { error ->
                _screenState.update { it.copy(isLoading = false, errorDialogMessage = error.message.toUiText()) }
            }
        }
    }

    fun validateAndUpdateSingleSvg(svgContent: String, svgName: String = "") {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            val result = stackedRepo.validateAndUpdateSignalSVG(
                svgContent,
                svgName,
                listOf("signal_1", "signal_2", "signal_3", "signal_4"),
                Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_VAL,
                Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME
            )
            _screenState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = result.exceptionOrNull()?.message?.toUiText()
                )
            }
        }
    }

    fun validateAndUpdateStackedSvg(svgContent: String, svgName: String = "") {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            val result = stackedRepo.validateAndUpdateSignalSVG(
                svgContent,
                svgName,
                listOf("signal_1_1", "signal_1_2", "signal_1_3", "signal_1_4", "signal_2_1", "signal_2_2", "signal_2_3", "signal_2_4"),
                Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_VAL,
                Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME
            )
            _screenState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = result.exceptionOrNull()?.message?.toUiText()
                )
            }
        }
    }

    fun getTypeface(mode: FontMode): Typeface = stackedRepo.getTypeface(mode)

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (t: Throwable) {
                    MLog.e(t)
                }
            }
        }
        if (result == null) {
            result = uri.path?.let { File(it).name }
        }
        return result ?: "未知文件.svg"
    }
}