package dev.lackluster.mihelper.app.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.app.utils.showToastAsync
import dev.lackluster.mihelper.data.Scope
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun RebootActionItem(
    appName: String,
    vararg appPkg: String,
    horizontalPadding: PaddingValues = PaddingValues.Zero
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }

    IconButton(
        modifier = Modifier.padding(horizontalPadding).padding(end = 21.dp).size(40.dp),
        onClick = { showDialog.value = true },
        holdDownState = showDialog.value
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            imageVector = MiuixIcons.Refresh,
            contentDescription = "Reboot app",
            tint = MiuixTheme.colorScheme.onSurfaceSecondary,
        )
    }

    val toastSuccess = stringResource(R.string.menu_reboot_done_toast)
    val toastError = stringResource(R.string.menu_reboot_error_toast)

    AlertDialog(
        visible = showDialog.value,
        onDismissRequest = { showDialog.value = false },
        title = stringResource(R.string.menu_reboot_common_title, appName),
        message = stringResource(R.string.menu_reboot_common_message, appName),
        mode = AlertDialogMode.NegativeAndPositive,
        cancelable = true,
        onPositiveButton = {
            showDialog.value = false
            scope.launch {
                var success = true
                if (appPkg.contains(Scope.ANDROID)) {
                    success = SystemCommander.execAsync(
                        command = "/system/bin/sync;/system/bin/svc power reboot || reboot",
                        useRoot = true,
                        silent = true
                    ).isSuccess
                } else {
                    appPkg.forEach { packageName ->
                        success = success and SystemCommander.execAsync(
                            command = "killall $packageName",
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
    )
}