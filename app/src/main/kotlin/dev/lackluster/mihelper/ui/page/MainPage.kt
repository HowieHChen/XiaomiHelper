package dev.lackluster.mihelper.ui.page

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.navigation.HyperXRoute
import dev.lackluster.hyperx.compose.navigation.Navigator
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.ShellUtils
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.SuperListPopup
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MainPage(navigator: Navigator, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    val showTopPopup = remember { mutableStateOf(false) }

    val contextMenuItems = listOf(
        stringResource(R.string.ui_title_menu_reboot),
        stringResource(R.string.menu_shortcut_lsposed)
    )

    BasePage(
        navigator,
        adjustPadding,
        stringResource(R.string.page_main),
        MainActivity.blurEnabled,
        mode,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        navigationIcon = {},
        actions = { padding ->
            val hapticFeedback = LocalHapticFeedback.current
            SuperListPopup(
                show = showTopPopup.value,
                popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                alignment = PopupPositionProvider.Align.TopEnd,
                onDismissRequest = {
                    showTopPopup.value = false
                }
            ) {
                ListPopupColumn {
                    contextMenuItems.forEachIndexed { index, string ->
                        DropdownImpl(
                            text = string,
                            optionSize = contextMenuItems.size,
                            isSelected = false,
                            onSelectedIndexChange = {
                                when (it) {
                                    0 -> {
                                        navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                                        navigator.push(Route.Menu)
                                    }

                                    1 -> {
                                        try {
                                            ShellUtils.tryExec(
                                                Constants.CMD_LSPOSED,
                                                useRoot = true,
                                                throwIfError = true
                                            )
                                        } catch (tout: Throwable) {
                                            makeText(
                                                context,
                                                tout.message,
                                                LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                                showTopPopup.value = false
                            },
                            index = index
                        )
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .padding(padding)
                    .padding(end = 21.dp)
                    .size(40.dp),
                onClick = {
                    showTopPopup.value = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                holdDownState = showTopPopup.value
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.More,
                    contentDescription = "Menu",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        }
    ) {
        item {
            PreferenceGroup(first = true) {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_hyper_helper_gray),
                    title = stringResource(R.string.page_module)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.ModuleSettings)
                }
            }
        }
        item {
            PreferenceGroup {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_systemui),
                    title = stringResource(R.string.page_systemui)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.SystemUI)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_android_green),
                    title = stringResource(R.string.page_android)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.SystemFramework)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_home),
                    title = stringResource(R.string.page_miui_home)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.MiuiHome)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_cleaner),
                    title = stringResource(R.string.page_cleaner)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.CleanMaster)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_security_center),
                    title = stringResource(if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.SecurityCenter)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_others),
                    title = stringResource(R.string.page_others)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.Others)
                }
            }
        }
        item {
            PreferenceGroup(
                last = true
            ) {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_about),
                    title = stringResource(R.string.page_about)
                ) {
                    navigator.popUntil { it is HyperXRoute.Main || it is HyperXRoute.Empty }
                    navigator.push(Route.About)
                }
            }
        }
    }

    val dialogInactiveVisibility = remember { mutableStateOf(false) }
    val dialogDisabledVisibility = remember { mutableStateOf(false) }
    val dialogRootRequiredVisibility = remember { mutableStateOf(false) }
    if (MainActivity.moduleActive.value) {
        if (!MainActivity.moduleEnabled.value) {
            dialogDisabledVisibility.value = true
        } else if (!MainActivity.rootGranted.value) {
            dialogRootRequiredVisibility.value = true
        }
    } else {
        dialogInactiveVisibility.value = true
    }
    AlertDialog(
        visibility = dialogDisabledVisibility,
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.main_module_disabled_tips),
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_ignore),
        positiveText = stringResource(R.string.button_enable)
    ) {
        dialogDisabledVisibility.value = false
        navigator.push(Route.ModuleSettings)
    }
    AlertDialog(
        visibility = dialogInactiveVisibility,
        title = stringResource(R.string.dialog_error),
        message = stringResource(R.string.main_module_inactive_tips),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_ignore),
        positiveText = stringResource(R.string.main_module_inactive_dialog_lsposed)
    ) {
        try {
            ShellUtils.tryExec(
                Constants.CMD_LSPOSED,
                useRoot = true,
                throwIfError = true
            )
        } catch (tout: Throwable) {
            makeText(
                context,
                tout.message,
                LENGTH_LONG
            ).show()
        }
        dialogInactiveVisibility.value = false
    }
    AlertDialog(
        visibility = dialogRootRequiredVisibility,
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.main_app_root_tips),
        cancelable = false
    )
}
