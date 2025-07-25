package dev.lackluster.mihelper.ui.component

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.ShellUtils
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Reboot
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun RebootMenuItem(
    appName: String,
    appPkg: String
) {
    val context = LocalContext.current
    val dialogVisibility = remember { mutableStateOf(false) }
    IconButton(
        modifier = Modifier.padding(end = 21.dp).size(40.dp),
        onClick = {
            dialogVisibility.value = true
        },
        holdDownState = dialogVisibility.value
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            imageVector = MiuixIcons.Useful.Reboot,
            contentDescription = "Reboot app",
            tint = MiuixTheme.colorScheme.onSurfaceSecondary,
        )
    }
    AlertDialog(
        dialogVisibility,
        stringResource(R.string.menu_reboot_common_title, appName),
        stringResource(R.string.menu_reboot_common_message, appName),
        mode = AlertDialogMode.NegativeAndPositive,
        onPositiveButton = {
            try {
                if (appPkg == Scope.ANDROID) {
                    ShellUtils.tryExec("/system/bin/sync;/system/bin/svc power reboot || reboot", useRoot = true, checkSuccess = true)
                } else {
                    ShellUtils.tryExec("killall $appPkg", useRoot = true, checkSuccess = true)
                }
                context.let {
                    makeText(
                        it,
                        it.getString(R.string.menu_reboot_done_toast),
                        LENGTH_SHORT
                    ).show()
                }
            } catch (tout : Throwable) {
                context.let {
                    makeText(
                        it,
                        tout.message,
                        LENGTH_LONG
                    ).show()
                }
            }
            dialogVisibility.value = false
        }
    )
}