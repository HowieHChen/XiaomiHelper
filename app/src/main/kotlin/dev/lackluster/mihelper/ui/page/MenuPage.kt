package dev.lackluster.mihelper.ui.page

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.utils.ShellUtils

@Composable
fun MenuPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current
    
    val dialogRebootSystem = remember { mutableStateOf(false) }
    val dialogRebootScope = remember { mutableStateOf(false) }
    val dialogRebootSystemUI = remember { mutableStateOf(false) }
    val dialogRebootLauncher = remember { mutableStateOf(false) }
    
    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_menu),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                first = true,
                last = true
            ) {
                TextPreference(
                    title = stringResource(R.string.menu_reboot_system)
                ) {
                    dialogRebootSystem.value = true
                }
                TextPreference(
                    title = stringResource(R.string.menu_reboot_scope)
                ) {
                    dialogRebootScope.value = true
                }
                TextPreference(
                    title = stringResource(R.string.menu_reboot_systemui)
                ) {
                    dialogRebootSystemUI.value = true
                }
                TextPreference(
                    title = stringResource(R.string.menu_reboot_launcher)
                ) {
                    dialogRebootLauncher.value = true
                }
            }
        }
    }

    AlertDialog(
        visibility = dialogRebootSystem,
        title = stringResource(R.string.menu_reboot_system),
        message = stringResource(R.string.menu_reboot_system_tips),
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_cancel),
        positiveText = stringResource(R.string.button_ok)
    ) {
        dialogRebootSystem.value = false
        try {
            ShellUtils.tryExec("/system/bin/sync;/system/bin/svc power reboot || reboot", useRoot = true, checkSuccess = true)
        } catch (tout : Throwable) {
            makeText(
                context,
                tout.message,
                LENGTH_LONG
            ).show()
        }
    }
    AlertDialog(
        visibility = dialogRebootScope,
        title = stringResource(R.string.menu_reboot_scope),
        message = stringResource(R.string.menu_reboot_scope_tips),
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_cancel),
        positiveText = stringResource(R.string.button_ok)
    ) {
        dialogRebootScope.value = false
        context.let {
            try {
                it.resources.getStringArray(R.array.module_scope).forEach { pkg ->
                    try {
                        if (pkg != "android") ShellUtils.tryExec("killall -q $it", useRoot = true, checkSuccess = true)
                    } catch (t: Throwable) {
                        if (t.message?.contains("No such process") == false) {
                            throw t
                        }
                    }
                }
                makeText(
                    it,
                    it.getString(R.string.menu_reboot_done_toast),
                    LENGTH_LONG
                ).show()
            } catch (tout : Throwable) {
                makeText(
                    it,
                    tout.message,
                    LENGTH_LONG
                ).show()
            }
        }
    }
    AlertDialog(
        visibility = dialogRebootSystemUI,
        title = stringResource(R.string.menu_reboot_systemui),
        message = stringResource(R.string.menu_reboot_systemui_tips),
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_cancel),
        positiveText = stringResource(R.string.button_ok)
    ) {
        dialogRebootSystemUI.value = false
        try {
            ShellUtils.tryExec("killall com.android.systemui", useRoot = true, checkSuccess = true)
            context.let {
                makeText(
                    it,
                    it.getString(R.string.menu_reboot_done_toast),
                    LENGTH_LONG
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
    }
    AlertDialog(
        visibility = dialogRebootLauncher,
        title = stringResource(R.string.menu_reboot_launcher),
        message = stringResource(R.string.menu_reboot_launcher_tips),
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_cancel),
        positiveText = stringResource(R.string.button_ok)
    ) {
        dialogRebootLauncher.value = false
        try {
            ShellUtils.tryExec("killall com.miui.home", useRoot = true, checkSuccess = true)
            context.let {
                makeText(
                    it,
                    it.getString(R.string.menu_reboot_done_toast),
                    LENGTH_LONG
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
    }
}