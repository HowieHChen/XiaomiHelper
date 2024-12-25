package dev.lackluster.mihelper.activity.page

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.navigation.navigateWithPopup
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.ShellUtils
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ImmersionMore
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup

@Composable
fun MainPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val isTopPopupExpanded = remember { mutableStateOf(false) }
    val showTopPopup = remember { mutableStateOf(false) }

    val contextMenuItems = listOf(
        stringResource(R.string.ui_title_menu_reboot),
        stringResource(R.string.menu_shortcut_lsposed)
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_main),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        navigationIcon = {},
        actions = { padding ->
            val hapticFeedback = LocalHapticFeedback.current
            if (isTopPopupExpanded.value) {
                ListPopup(
                    show = showTopPopup,
                    popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                    alignment = PopupPositionProvider.Align.TopRight,
                    onDismissRequest = {
                        isTopPopupExpanded.value = false
                    }
                ) {
                    ListPopupColumn {
                        contextMenuItems.forEachIndexed { index, string ->
                            DropdownImpl(
                                text = string,
                                optionSize = contextMenuItems.size,
                                isSelected = false,
                                onSelectedIndexChange = {
                                    when(it) {
                                        0 -> {
                                            navController.navigateWithPopup(Pages.MENU)
                                        }
                                        1 -> {
                                            try {
                                                ShellUtils.tryExec(
                                                    Constants.CMD_LSPOSED,
                                                    useRoot = true,
                                                    checkSuccess = true
                                                )
                                            } catch (tout : Throwable) {
                                                makeText(
                                                    HyperXActivity.context,
                                                    tout.message,
                                                    LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                    dismissPopup(showTopPopup)
                                    isTopPopupExpanded.value = false
                                },
                                index = index
                            )
                        }
                    }
                }
                showTopPopup.value = true
            }
            IconButton(
                modifier = Modifier.padding(padding).padding(end = 21.dp).size(40.dp),
                onClick = {
                    isTopPopupExpanded.value = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {
                Icon(
                    imageVector = MiuixIcons.ImmersionMore,
                    contentDescription = "Menu"
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
                    navController.navigateWithPopup(Pages.MODULE_SETTINGS)
                }
            }
        }
        item {
            PreferenceGroup {
                AnimatedVisibility(
                    visible = MainActivity.liteMode.value
                ) {
                    Column {
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_systemui),
                            title = stringResource(R.string.page_virtual_media_control_style)
                        ) {
                            navController.navigateWithPopup(Pages.MEDIA_CONTROL)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_home),
                            title = stringResource(R.string.page_virtual_home_refactor)
                        ) {
                            navController.navigateWithPopup(Pages.HOME_REFACTOR)
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !MainActivity.liteMode.value
                ) {
                    Column {
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_systemui),
                            title = stringResource(R.string.page_systemui)
                        ) {
                            navController.navigateWithPopup(Pages.SYSTEM_UI)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_android_green),
                            title = stringResource(R.string.page_android)
                        ) {
                            navController.navigateWithPopup(Pages.SYSTEM_FRAMEWORK)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_home),
                            title = stringResource(R.string.page_miui_home)
                        ) {
                            navController.navigateWithPopup(Pages.MIUI_HOME)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_cleaner),
                            title = stringResource(R.string.page_cleaner)
                        ) {
                            navController.navigateWithPopup(Pages.CLEAN_MASTER)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_security_center),
                            title = stringResource(if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center)
                        ) {
                            navController.navigateWithPopup(Pages.SECURITY_CENTER)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_interconnection),
                            title = stringResource(R.string.page_interconnection)
                        ) {
                            navController.navigateWithPopup(Pages.INTERCONNECTION)
                        }
                        TextPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_header_others),
                            title = stringResource(R.string.page_others)
                        ) {
                             navController.navigateWithPopup(Pages.OTHERS)
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_header_about),
                    title = stringResource(R.string.page_about)
                ) {
                    navController.navigateWithPopup(Pages.ABOUT)
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
        dismissDialog(dialogDisabledVisibility)
        navController.navigateTo(Pages.MODULE_SETTINGS)
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
                checkSuccess = true
            )
        } catch (tout : Throwable) {
            makeText(
                HyperXActivity.context,
                tout.message,
                LENGTH_LONG
            ).show()
        }
        dismissDialog(dialogInactiveVisibility)
    }
    AlertDialog(
        visibility = dialogRootRequiredVisibility,
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.main_app_root_tips),
        cancelable = false
    )
}