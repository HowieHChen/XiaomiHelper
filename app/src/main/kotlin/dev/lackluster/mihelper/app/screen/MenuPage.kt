package dev.lackluster.mihelper.app.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.app.utils.showToastAsync
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.Scope
import kotlinx.coroutines.launch

private enum class RebootTarget {
    NONE, SYSTEM, SCOPE, SYSTEM_UI, LAUNCHER
}

@Composable
fun MenuPage() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val scopes = stringArrayResource(R.array.module_scope)
    val toastSuccess = stringResource(R.string.menu_reboot_done_toast)
    val toastError = stringResource(R.string.menu_reboot_error_toast)

    val onAction: (RebootTarget) -> Unit = { action ->
        coroutineScope.launch {
            var success = true
            when (action) {
                RebootTarget.NONE -> {}
                RebootTarget.SYSTEM -> {
                    success = SystemCommander.execAsync(
                        command = "/system/bin/sync;/system/bin/svc power reboot || reboot",
                        useRoot = true,
                        silent = true
                    ).isSuccess
                }
                RebootTarget.SCOPE -> {
                    scopes.forEach {  packageName ->
                        if (packageName != Scope.SYSTEM) {
                            val result = SystemCommander.execAsync(
                                command = "killall -q $packageName",
                                useRoot = true,
                                silent = true
                            )
                            success = success and (result.isSuccess || result.err.contains("No such process", ignoreCase = true))
                        }
                    }
                }
                RebootTarget.SYSTEM_UI -> {
                    success = SystemCommander.execAsync(
                        command = "killall ${Scope.SYSTEM_UI}",
                        useRoot = true,
                        silent = true
                    ).isSuccess
                }
                RebootTarget.LAUNCHER -> {
                    success = SystemCommander.execAsync(
                        command = "killall ${Scope.MIUI_HOME}",
                        useRoot = true,
                        silent = true
                    ).isSuccess
                }
            }
            if (success) {
                context.showToastAsync(toastSuccess, true)
            } else {
                context.showToastAsync(toastError, true)
            }
        }
    }

    MenuPageContent(
        onAction = onAction
    )
}

@Composable
private fun MenuPageContent(
    onAction: (RebootTarget) -> Unit
) {
    val showConfirmDialog = remember { mutableStateOf(false) }
    val rebootTarget = remember { mutableStateOf(RebootTarget.NONE) }

    val dialogTitle = when (rebootTarget.value) {
        RebootTarget.SYSTEM -> R.string.menu_reboot_system
        RebootTarget.SCOPE -> R.string.menu_reboot_scope
        RebootTarget.SYSTEM_UI -> R.string.menu_reboot_systemui
        RebootTarget.LAUNCHER -> R.string.menu_reboot_launcher
        else -> R.string.dialog_warning
    }.toUiText().asString()

    val dialogMessage = when (rebootTarget.value) {
        RebootTarget.SYSTEM -> R.string.menu_reboot_system_tips
        RebootTarget.SCOPE -> R.string.menu_reboot_scope_tips
        RebootTarget.SYSTEM_UI -> R.string.menu_reboot_systemui_tips
        RebootTarget.LAUNCHER -> R.string.menu_reboot_launcher_tips
        else -> R.string.dialog_warning
    }.toUiText().asString()

    HyperXPage(
        title = stringResource(R.string.page_menu)
    ) {
        itemPreferenceGroup(
            key = "menu_reboot_group",
            position = ItemPosition.Single
        ) {
            TextPreference(
                title = stringResource(R.string.menu_reboot_system),
                onClick = {
                    rebootTarget.value = RebootTarget.SYSTEM
                    showConfirmDialog.value = true
                }
            )
            TextPreference(
                title = stringResource(R.string.menu_reboot_scope),
                onClick = {
                    rebootTarget.value = RebootTarget.SCOPE
                    showConfirmDialog.value = true
                }
            )
            TextPreference(
                title = stringResource(R.string.menu_reboot_systemui),
                onClick = {
                    rebootTarget.value = RebootTarget.SYSTEM_UI
                    showConfirmDialog.value = true
                }
            )
            TextPreference(
                title = stringResource(R.string.menu_reboot_launcher),
                onClick = {
                    rebootTarget.value = RebootTarget.LAUNCHER
                    showConfirmDialog.value = true
                }
            )
        }
    }

    AlertDialog(
        visible = showConfirmDialog.value,
        onDismissRequest = { showConfirmDialog.value = false },
        title = dialogTitle,
        message = dialogMessage,
        cancelable = true,
        mode = AlertDialogMode.NegativeAndPositive,
        onPositiveButton = {
            showConfirmDialog.value = false
            onAction(rebootTarget.value)
        }
    )
}