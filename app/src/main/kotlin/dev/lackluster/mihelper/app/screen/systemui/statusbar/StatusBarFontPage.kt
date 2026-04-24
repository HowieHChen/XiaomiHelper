package dev.lackluster.mihelper.app.screen.systemui.statusbar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.component.Hint
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.dialog.LoadingDialog
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemAnimated
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.repository.FontTarget
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.state.ViewState
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import org.koin.androidx.compose.koinViewModel

sealed interface StatusBarFontAction {
    object ImportLocalFont : StatusBarFontAction
    data class ApplyManualPath(val path: String) : StatusBarFontAction
    object ResetToDefault : StatusBarFontAction
}

@Composable
fun StatusBarFontPage(
    viewModel: StatusBarFontViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val fontDisplayName by viewModel.fontDisplayName.collectAsState()

    val fontPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.importFontFromUri(uri)
        }
    }

    var resultTitleRes by remember { mutableIntStateOf(R.string.dialog_done) }
    var resultMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ViewState.Success -> {
                resultTitleRes = R.string.dialog_done
                resultMessage = state.data
            }
            is ViewState.Error -> {
                resultTitleRes = R.string.dialog_error
                resultMessage = state.message
            }
            else -> {}
        }
    }

    val onAction: (StatusBarFontAction) -> Unit = { action ->
        when (action) {
            is StatusBarFontAction.ImportLocalFont -> {
                fontPickerLauncher.launch(arrayOf("font/ttf", "font/otf", "application/octet-stream"))
            }
            is StatusBarFontAction.ApplyManualPath -> {
                if (action.path.isNotBlank()) viewModel.applyFontFromPath(action.path)
            }
            is StatusBarFontAction.ResetToDefault -> {
                viewModel.resetToDefault(FontTarget.STATUS_BAR)
            }
        }
    }

    StatusBarFontPageContent(
        fontDisplayName = fontDisplayName,
        onAction = onAction,
    )

    LoadingDialog(
        visible = uiState is ViewState.Loading,
        title = stringResource(dev.lackluster.hyperx.R.string.loading_dialog_processing),
        cancelable = false,
    )
    AlertDialog(
        visible = uiState is ViewState.Success || uiState is ViewState.Error,
        onDismissRequest = {},
        title = stringResource(resultTitleRes),
        message = resultMessage?.asString(),
        cancelable = false,
        mode = AlertDialogMode.Positive,
        onPositiveButton = { viewModel.resetState() }
    )
}

@Composable
private fun StatusBarFontPageContent(
    fontDisplayName: String,
    onAction: (StatusBarFontAction) -> Unit,
) {
    val isDefault = fontDisplayName == Constants.VARIABLE_FONT_DEFAULT_PATH
    val isManualPath = fontDisplayName.contains("/") && !isDefault
    val isImported = !fontDisplayName.contains("/") && !isDefault

    val showResetConfirmDialog = remember { mutableStateOf(false) }
    val showHint = rememberPreferenceState(Preferences.HintState.STATUS_BAR_FONT_ROOT)

    HyperXPage(
        title = stringResource(R.string.page_status_bar_font),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI),
            )
        }
    ) {
        itemAnimated(
            key = "FIXED_HINT",
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                text = stringResource(R.string.font_hint_path),
                closeable = false,
            )
        }
        itemAnimated(
            key = "ROOT_HINT",
            visible = showHint.value
        ) {
            Hint(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = stringResource(R.string.font_hint_general_root),
                closeable = true,
                onClose = { showHint.value = false }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_font_general,
        ) {
            TextPreference(
                title = stringResource(R.string.font_general_path),
                summary = if (isImported) fontDisplayName else stringResource(R.string.font_general_path_file),
                onClick = { onAction(StatusBarFontAction.ImportLocalFont) }
            )
            EditTextPreference(
                title = stringResource(R.string.font_general_path),
                summary = if (isManualPath) fontDisplayName else stringResource(R.string.font_general_path_path),
                text = if (isManualPath) fontDisplayName else "",
                dialogMessage = stringResource(R.string.font_general_path_tips),
                valuePosition = ValuePosition.Hidden,
                onTextChange = { onAction(StatusBarFontAction.ApplyManualPath(it)) }
            )
            TextPreference(
                title = stringResource(R.string.font_general_path_reset),
                summary = Constants.VARIABLE_FONT_DEFAULT_PATH,
                onClick = { showResetConfirmDialog.value = true }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_font_weight,
            position = ItemPosition.Last
        ) {
            val customCarrierLabelFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_LOCK_SCREEN_CARRIER)
            SwitchPreference(
                title = stringResource(R.string.font_weight_lockscreen_carrier),
                checked = customCarrierLabelFont.value,
                onCheckedChange = { customCarrierLabelFont.value = it}
            )
            AnimatedVisibility(customCarrierLabelFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.LOCK_SCREEN_CARRIER_WEIGHT,
                    title = stringResource(R.string.font_weight_lockscreen_carrier_weight),
                    min = 1,
                    max = 1000,
                )
            }
        }
    }

    AlertDialog(
        visible = showResetConfirmDialog.value,
        onDismissRequest = { showResetConfirmDialog.value = false },
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.font_general_path_reset_msg),
        cancelable = true,
        mode = AlertDialogMode.NegativeAndPositive,
        onPositiveButton = {
            showResetConfirmDialog.value = false
            onAction(StatusBarFontAction.ResetToDefault)
        }
    )
}
