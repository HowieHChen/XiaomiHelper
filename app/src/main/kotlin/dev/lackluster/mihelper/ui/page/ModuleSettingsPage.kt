package dev.lackluster.mihelper.ui.page

import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref.Key.App
import dev.lackluster.mihelper.data.Pref.Key.Module
import dev.lackluster.mihelper.utils.BackupUtils
import dev.lackluster.mihelper.utils.BackupUtils.BACKUP_FILE_PREFIX
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun ModuleSettingsPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val dropdownEntriesEntryIcon = listOf(
        DropDownEntry(iconRes = R.drawable.ic_header_hyper_helper_gray, title = stringResource(R.string.module_settings_icon_style_default)),
        DropDownEntry(iconRes = R.drawable.ic_header_android_green, title = stringResource(R.string.module_settings_icon_style_android)),
    )
    val dropdownEntriesEntryColor = listOf(
        DropDownEntry(iconRes = R.drawable.ic_color_gray, title = stringResource(R.string.module_settings_icon_color_gray)),
        DropDownEntry(iconRes = R.drawable.ic_color_red, title = stringResource(R.string.module_settings_icon_color_red)),
        DropDownEntry(iconRes = R.drawable.ic_color_green, title = stringResource(R.string.module_settings_icon_color_green)),
        DropDownEntry(iconRes = R.drawable.ic_color_blue, title = stringResource(R.string.module_settings_icon_color_blue)),
        DropDownEntry(iconRes = R.drawable.ic_color_purple, title = stringResource(R.string.module_settings_icon_color_purple)),
        DropDownEntry(iconRes = R.drawable.ic_color_yellow, title = stringResource(R.string.module_settings_icon_color_yellow)),
    )
    val dropdownEntriesEntryName = listOf(
        DropDownEntry(stringResource(R.string.module_settings_name_helper)),
        DropDownEntry(stringResource(R.string.module_settings_name_advanced)),
        DropDownEntry(stringResource(R.string.module_settings_name_custom)),
    )

    var visibilityShowInSettings by remember { mutableStateOf(SafeSP.getBoolean(Module.SHOW_IN_SETTINGS)) }
    var visibilityCustomEntryName by remember { mutableStateOf(SafeSP.getInt(Module.SETTINGS_NAME) == 2) }
    val dialogBackupAndRestoreVisibility = remember { mutableStateOf(false) }
    val dialogResetVisibility = remember { mutableStateOf(false) }
    val dialogResetResultVisibility = remember { mutableStateOf(false) }
    val backupUri = remember { mutableStateOf<Uri?>(null) }
    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) {
        backupUri.value = it
    }
    backupUri.value?.let {
        BackAndRestoreResultDialog(
            dialogBackupAndRestoreVisibility,
            BackupUtils.WRITE_DOCUMENT_CODE,
            it
        )
    }
    val restoreUri = remember { mutableStateOf<Uri?>(null) }
    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) {
        restoreUri.value = it
    }
    restoreUri.value?.let { uri ->
        BackAndRestoreResultDialog(
            dialogBackupAndRestoreVisibility,
            BackupUtils.READ_DOCUMENT_CODE,
            uri
        )
    }
    val resetResult = remember { mutableStateOf(false) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_module),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_module_general),
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.module_main_switch),
                    key = Module.ENABLED,
                    defValue = MainActivity.moduleEnabled.value
                ) {
                    MainActivity.moduleEnabled.value = it
                }
                SwitchPreference(
                    title = stringResource(R.string.module_dexkit_cache),
                    summary = stringResource(R.string.module_dexkit_cache_tips),
                    key = Module.DEX_KIT_CACHE,
                    defValue = true
                )
                SwitchPreference(
                    title = stringResource(R.string.module_skip_root_check),
                    summary = stringResource(R.string.module_skip_root_check_tips),
                    key = App.SKIP_ROOT_CHECK
                )
                SwitchPreference(
                    title = stringResource(R.string.module_hide_icon),
                    key = Module.HIDE_ICON
                ) {
                    HyperXActivity.context.let { context ->
                        context.packageManager.setComponentEnabledSetting(
                            ComponentName(context, "${BuildConfig.APPLICATION_ID}.launcher"),
                            if (it) {
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            } else {
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            },
                            PackageManager.DONT_KILL_APP
                        )
                    }
                }
                SwitchPreference(
                    title = stringResource(R.string.module_show_in_settings),
                    key = Module.SHOW_IN_SETTINGS
                ) {
                    visibilityShowInSettings = it
                }
                AnimatedVisibility (
                    visible = visibilityShowInSettings
                ) {
                    Column {
                        DropDownPreference(
                            title = stringResource(R.string.module_settings_icon_style),
                            entries = dropdownEntriesEntryIcon,
                            key = Module.SETTINGS_ICON_STYLE
                        )
                        DropDownPreference(
                            title = stringResource(R.string.module_settings_icon_color),
                            entries = dropdownEntriesEntryColor,
                            key = Module.SETTINGS_ICON_COLOR
                        )
                        DropDownPreference(
                            title = stringResource(R.string.module_settings_name),
                            entries = dropdownEntriesEntryName,
                            key = Module.SETTINGS_NAME
                        ) {
                            visibilityCustomEntryName = (it == 2)
                        }
                        AnimatedVisibility (
                            visible = visibilityCustomEntryName
                        ) {
                            EditTextPreference(
                                title = stringResource(R.string.module_settings_custom_name),
                                key = Module.SETTINGS_NAME_CUSTOM,
                                dataType = EditTextDataType.STRING
                            )
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_module_ui)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.module_ui_blur),
                    key = App.HAZE_BLUR,
                    defValue = MainActivity.blurEnabled.value
                ) {
                    MainActivity.blurEnabled.value = it
                }
                AnimatedVisibility(
                    visible = MainActivity.blurEnabled.value
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.module_ui_blur_tint_alpha_light),
                            key = App.HAZE_TINT_ALPHA_LIGHT,
                            defValue = (MainActivity.blurTintAlphaLight.floatValue * 100).roundToInt().coerceIn(0..100),
                            dataType = EditTextDataType.INT,
                            dialogMessage = stringResource(R.string.module_ui_blur_tint_alpha_tips),
                            isValueValid = {
                                (it as? Int) in (0..100)
                            }
                        ) { _, newValue ->
                            (newValue as? Int)?.let {
                                MainActivity.blurTintAlphaLight.floatValue = it / 100f
                            }
                        }
                        EditTextPreference(
                            title = stringResource(R.string.module_ui_blur_tint_alpha_dark),
                            key = App.HAZE_TINT_ALPHA_DARK,
                            defValue = (MainActivity.blurTintAlphaDark.floatValue * 100).roundToInt().coerceIn(0..100),
                            dataType = EditTextDataType.INT,
                            dialogMessage = stringResource(R.string.module_ui_blur_tint_alpha_tips),
                            isValueValid = {
                                (it as? Int) in (0..100)
                            }
                        ) { _, newValue ->
                            (newValue as? Int)?.let {
                                MainActivity.blurTintAlphaDark.floatValue = it / 100f
                            }
                        }
                    }
                }
                SwitchPreference(
                    title = stringResource(R.string.module_ui_split),
                    summary = stringResource(R.string.module_ui_split_tips),
                    key = App.SPLIT_VIEW,
                    defValue = MainActivity.splitEnabled.value
                ) {
                    MainActivity.splitEnabled.value = it
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_module_backup),
                last = true
            ) {
                TextPreference(
                    title = stringResource(R.string.module_backup)
                ) {
                    val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
                    val backupFileName = BACKUP_FILE_PREFIX + timeFormatter.format(LocalDateTime.now())
                    backupLauncher.launch(backupFileName)
                }
                TextPreference(
                    title = stringResource(R.string.module_restore)
                ) {
                    restoreLauncher.launch(arrayOf("application/json"))
                }
                TextPreference(
                    title = stringResource(R.string.module_reset)
                ) {
                    dialogResetVisibility.value = true
                }
            }
        }
    }

    AlertDialog(
        visibility = dialogResetVisibility,
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.module_reset_warning),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        onPositiveButton = {
            dialogResetVisibility.value = false
            resetResult.value = BackupUtils.handleReset()
            dialogResetResultVisibility.value = true
        }
    )
    ResetResultDialog(
        dialogResetResultVisibility,
        resetResult
    )
}

@Composable
private fun BackAndRestoreResultDialog(
    visible: MutableState<Boolean>,
    requestCode: Int,
    uri: Uri
) {
    var titleId = R.string.dialog_error
    var msgId = R.string.module_unknown_failure
    var errMsg = ""
    try {
        when (requestCode) {
            BackupUtils.WRITE_DOCUMENT_CODE -> {
                BackupUtils.handleBackup(HyperXActivity.context, uri)
                titleId = R.string.dialog_done
                msgId = R.string.module_backup_success
            }
            BackupUtils.READ_DOCUMENT_CODE -> {
                BackupUtils.handleRestore(HyperXActivity.context, uri)
                titleId = R.string.dialog_done
                msgId = R.string.module_restore_success
            }
            else -> return
        }
    } catch (t: Throwable) {
        errMsg = "\n${t.stackTraceToString()}"
        when (requestCode) {
            BackupUtils.WRITE_DOCUMENT_CODE -> {
                msgId = R.string.module_backup_failure
            }
            BackupUtils.READ_DOCUMENT_CODE -> {
                msgId = R.string.module_restore_failure
            }
        }
    }
    AlertDialog(
        visible,
        title = stringResource(titleId),
        message = stringResource(msgId) + errMsg,
        cancelable = false,
        mode = AlertDialogMode.Positive,
        onPositiveButton = {
            visible.value = false
            if (requestCode == BackupUtils.READ_DOCUMENT_CODE) {
                Thread {
                    Thread.sleep(500)
                    BackupUtils.restartApp(HyperXActivity.context)
                }.start()
            }
        }
    )
    visible.value = true
}

@Composable
private fun ResetResultDialog(
    visible: MutableState<Boolean>,
    success: MutableState<Boolean>
) {
    val title: String
    val msg: String
    if (success.value) {
        title = stringResource(R.string.dialog_done)
        msg = stringResource(R.string.module_reset_success)
    } else {
        title = stringResource(R.string.dialog_error)
        msg = stringResource(R.string.module_reset_failure)
    }
    AlertDialog(
        visible,
        title = title,
        message = msg,
        cancelable = false,
        mode = AlertDialogMode.Positive,
        onPositiveButton = {
            visible.value = false
            Thread {
                Thread.sleep(500)
                BackupUtils.restartApp(HyperXActivity.context)
            }.start()
        }
    )
}