package dev.lackluster.mihelper.app.screen.settings

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.core.utils.toDecimalString
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.dialog.LoadingDialog
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.layout.LocalHyperXLayoutConfig
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.MainActivity
import dev.lackluster.mihelper.app.state.AppEnvState
import dev.lackluster.mihelper.app.state.AppEnvViewModel
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.state.ViewState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Constants.BACKUP_FILE_PREFIX
import dev.lackluster.mihelper.data.preference.Preferences
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

private val settingsEntryIconOptions = listOf(
    DropDownOption(0, R.string.module_settings_icon_style_default, iconRes = R.drawable.ic_header_hyper_helper_gray),
    DropDownOption(1, R.string.module_settings_icon_style_android, iconRes = R.drawable.ic_header_android_green),
)

private val settingsEntryColorOptions = listOf(
    DropDownOption(0, R.string.module_settings_icon_color_gray, iconRes = R.drawable.ic_color_gray),
    DropDownOption(1, R.string.module_settings_icon_color_red, iconRes = R.drawable.ic_color_red),
    DropDownOption(2, R.string.module_settings_icon_color_green, iconRes = R.drawable.ic_color_green),
    DropDownOption(3, R.string.module_settings_icon_color_blue, iconRes = R.drawable.ic_color_blue),
    DropDownOption(4, R.string.module_settings_icon_color_purple, iconRes = R.drawable.ic_color_purple),
    DropDownOption(5, R.string.module_settings_icon_color_yellow, iconRes = R.drawable.ic_color_yellow),
)

private val settingsEntryNameOptions = listOf(
    DropDownOption(0, R.string.module_settings_name_helper),
    DropDownOption(1, R.string.module_settings_name_advanced),
    DropDownOption(2, R.string.module_settings_name_custom),
)

@Composable
fun ModuleSettingsPage(
    envViewModel: AppEnvViewModel = koinViewModel(),
    viewModel: ModuleSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val envState by envViewModel.envState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val moduleSettingsState by viewModel.moduleSettingsState.collectAsState()

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> if (uri != null) viewModel.handleAction(ModuleSettingsAction.ExportBackup(uri)) }
    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) viewModel.handleAction(ModuleSettingsAction.ImportBackup(uri)) }

    var resultTitleRes by remember { mutableIntStateOf(R.string.dialog_done) }
    var resultMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event: ModuleSettingsEvent ->
            when (event) {
                ModuleSettingsEvent.RestartApp -> {
                    val intent =
                        Intent(context, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                    exitProcess(0)
                }
                is ModuleSettingsEvent.SetLauncherIcon -> {
                    context.packageManager.setComponentEnabledSetting(
                        ComponentName(context, "${BuildConfig.APPLICATION_ID}.launcher"),
                        if (event.isHidden) {
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        } else {
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        },
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ViewState.Success -> {
                resultTitleRes = R.string.dialog_done
                resultMessage = state.data.message
            }
            is ViewState.Error -> {
                resultTitleRes = R.string.dialog_error
                resultMessage = state.message
            }
            else -> {}
        }
    }

    val onAction: (ModuleSettingsAction) -> Unit = { action ->
        when (action) {
            is ModuleSettingsAction.RequestExportBackup -> {
                val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
                val backupFileName = BACKUP_FILE_PREFIX + timeFormatter.format(LocalDateTime.now())
                backupLauncher.launch(backupFileName)
            }
            is ModuleSettingsAction.RequestImportBackup -> {
                restoreLauncher.launch(arrayOf("application/json"))
            }
            else -> viewModel.handleAction(action)
        }
    }

    ModuleSettingsPageContent(
        envState = envState,
        state = moduleSettingsState,
        onAction = onAction
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
        onPositiveButton = { viewModel.onDialogConfirmed() }
    )
}

@Composable
private fun ModuleSettingsPageContent(
    envState: AppEnvState,
    state: ModuleSettingsState,
    onAction: (ModuleSettingsAction) -> Unit
) {
    val layoutConfig = LocalHyperXLayoutConfig.current
    val settingsActions = LocalPreferenceActions.current

    val showResetConfirmDialog = remember { mutableStateOf(false) }

    HyperXPage(
        title = stringResource(R.string.page_module)
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_module_general,
            position = ItemPosition.First
        ) {
            SwitchPreference(
                title = stringResource(R.string.module_main_switch),
                checked = envState.isModuleEnabled,
                onCheckedChange = { settingsActions.update(Preferences.Module.MODULE_ENABLED, it) }
            )
            SwitchPreference(
                key = Preferences.Module.DEX_KIT_CACHE,
                title = stringResource(R.string.module_dexkit_cache),
                summary = stringResource(R.string.module_dexkit_cache_tips),
            )
            SwitchPreference(
                title = stringResource(R.string.module_skip_root_check),
                summary = stringResource(R.string.module_skip_root_check_tips),
                checked = envState.isRootIgnored,
                onCheckedChange = { settingsActions.update(Preferences.App.SKIP_ROOT_CHECK, it) }
            )
            SwitchPreference(
                key = Preferences.Module.DEBUG,
                title = stringResource(R.string.module_debug),
                summary = stringResource(R.string.module_debug_tips)
            )
            SwitchPreference(
                title = stringResource(R.string.module_hide_icon),
                checked = state.isIconHidden,
                onCheckedChange = {
                    onAction(ModuleSettingsAction.ToggleHideIcon(it))
                }
            )
            val showInSystemSettings = rememberPreferenceState(Preferences.Module.SHOW_IN_SETTINGS)
            val customEntryName = rememberPreferenceState(Preferences.Module.SETTINGS_NAME)
            SwitchPreference(
                title = stringResource(R.string.module_show_in_settings),
                checked = showInSystemSettings.value,
                onCheckedChange = { showInSystemSettings.value = it }
            )
            AnimatedColumn(showInSystemSettings.value) {
                DropDownPreference(
                    key = Preferences.Module.SETTINGS_ICON_STYLE,
                    title = stringResource(R.string.module_settings_icon_style),
                    options = settingsEntryIconOptions,
                )
                DropDownPreference(
                    key = Preferences.Module.SETTINGS_ICON_COLOR,
                    title = stringResource(R.string.module_settings_icon_color),
                    options = settingsEntryColorOptions,
                )
                DropDownPreference(
                    value = customEntryName.value,
                    title = stringResource(R.string.module_settings_name),
                    options = settingsEntryNameOptions,
                    onValueChange = { customEntryName.value = it }
                )
                AnimatedVisibility(customEntryName.value == 2) {
                    val customEntryNameString = rememberPreferenceState(Preferences.Module.CUSTOM_SETTINGS_NAME)
                    EditTextPreference(
                        title = stringResource(R.string.module_settings_custom_name),
                        text = customEntryNameString.value,
                        onTextChange = {
                            customEntryNameString.value = it
                        },
                        dialogHint = Preferences.Module.CUSTOM_SETTINGS_NAME.default,
                    )
                }
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_module_ui,
            position = ItemPosition.Middle
        ) {
            SwitchPreference(
                title = stringResource(R.string.module_ui_blur),
                checked = layoutConfig.isBlurEnabled,
                onCheckedChange = { settingsActions.update(Preferences.App.HAZE_BLUR, it) }
            )
            AnimatedColumn(layoutConfig.isBlurEnabled) {
                val draftLightBlurAlpha = remember(layoutConfig.lightBlurAlpha) {
                    mutableFloatStateOf(layoutConfig.lightBlurAlpha)
                }
                SeekBarPreference(
                    title = stringResource(R.string.module_ui_blur_tint_alpha_light),
                    value = draftLightBlurAlpha.floatValue,
                    onValueChange = { draftLightBlurAlpha.floatValue = it },
                    onValueChangeFinished = { settingsActions.update(Preferences.App.HAZE_LIGHT_BLUR_ALPHA, draftLightBlurAlpha.floatValue) },
                    defaultValue = Preferences.App.HAZE_LIGHT_BLUR_ALPHA.default,
                    min = 0.0f,
                    max = 1.0f,
                    valueFormatter = { it.toDecimalString() }
                )
                val draftDarkBlurAlpha = remember(layoutConfig.darkBlurAlpha) {
                    mutableFloatStateOf(layoutConfig.darkBlurAlpha)
                }
                SeekBarPreference(
                    title = stringResource(R.string.module_ui_blur_tint_alpha_dark),
                    value = draftDarkBlurAlpha.floatValue,
                    onValueChange = { draftDarkBlurAlpha.floatValue = it },
                    onValueChangeFinished = { settingsActions.update(Preferences.App.HAZE_DARK_BLUR_ALPHA, draftDarkBlurAlpha.floatValue) },
                    defaultValue = Preferences.App.HAZE_DARK_BLUR_ALPHA.default,
                    min = 0.0f,
                    max = 1.0f,
                    valueFormatter = { it.toDecimalString() }
                )
            }
            SwitchPreference(
                title = stringResource(R.string.module_ui_split),
                summary = stringResource(R.string.module_ui_split_tips),
                checked = layoutConfig.isSplitScreenEnabled,
                onCheckedChange = { settingsActions.update(Preferences.App.ENABLE_SPLIT_SCREEN, it) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_module_backup,
            position = ItemPosition.Last
        ) {
            TextPreference(stringResource(R.string.module_backup)) {
                onAction(ModuleSettingsAction.RequestExportBackup)
            }
            TextPreference(title = stringResource(R.string.module_restore)) {
                onAction(ModuleSettingsAction.RequestImportBackup)
            }
            TextPreference(title = stringResource(R.string.module_reset)) {
                showResetConfirmDialog.value = true
            }
        }
    }

    AlertDialog(
        visible = showResetConfirmDialog.value,
        onDismissRequest = { showResetConfirmDialog.value = false },
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.module_reset_warning),
        cancelable = true,
        mode = AlertDialogMode.NegativeAndPositive,
        onPositiveButton = {
            showResetConfirmDialog.value = false
            onAction(ModuleSettingsAction.ResetSettings)
        }
    )
}