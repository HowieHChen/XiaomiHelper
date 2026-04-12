package dev.lackluster.mihelper.app.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.navigation.HyperXRoute
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.app.state.AppEnvViewModel
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.utils.Device
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme

private sealed interface MainPageAction {
    data class NavigateTo(val route: Route) : MainPageAction
    object OpenMenu : MainPageAction
    object OpenLSPosed : MainPageAction
    object RefreshEnvironment : MainPageAction
}

@Composable
fun MainPage(
    appEnvVm: AppEnvViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val scope = rememberCoroutineScope()

    val envState by appEnvVm.envState.collectAsState()

    val showDisabledDialog = remember { mutableStateOf(false) }
    val showInactiveDialog = remember { mutableStateOf(false) }
    val showRootDialog = remember { mutableStateOf(false) }

    LaunchedEffect(envState) {
        if (envState.canWork) {
            showDisabledDialog.value = false
            showInactiveDialog.value = false
            showRootDialog.value = false
            return@LaunchedEffect
        }
        if (envState.isModuleActivated) {
            showInactiveDialog.value = false
            if (!envState.isModuleEnabled) {
                showDisabledDialog.value = true
                showRootDialog.value = false
            } else if (!envState.isRootGranted && !envState.isRootIgnored) {
                showRootDialog.value = true
                showDisabledDialog.value = false
            }
        } else {
            delay(500)
            showInactiveDialog.value = true
            showDisabledDialog.value = false
            showRootDialog.value = false
        }
    }

    val onAction: (MainPageAction) -> Unit = { action ->
        when (action) {
            is MainPageAction.NavigateTo -> {
                navigator.popUntil { it is HyperXRoute.Main }
                navigator.push(action.route)
            }
            is MainPageAction.OpenMenu -> {
                navigator.popUntil { it is HyperXRoute.Main }
                navigator.push(Route.Menu)
            }
            is MainPageAction.OpenLSPosed -> {
                scope.launch {
                    val result = SystemCommander.execAsync(
                        command = Constants.CMD_LSPOSED,
                        useRoot = true,
                        silent = true
                    )
                    if (!result.isSuccess) {
                        context.showToast(result.err, true)
                    }
                }
            }
            is MainPageAction.RefreshEnvironment -> {
                appEnvVm.refreshEnvState()
            }
        }
    }

    MainPageContent(
        onAction = onAction
    )

    AlertDialog(
        visible = showDisabledDialog.value,
        onDismissRequest = { showDisabledDialog.value = false},
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.main_module_disabled_tips),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_ignore),
        positiveText = stringResource(R.string.button_enable)
    ) {
        showDisabledDialog.value = false
        onAction(MainPageAction.NavigateTo(Route.ModuleSettings))
    }
    AlertDialog(
        visible = showInactiveDialog.value,
        onDismissRequest = { showInactiveDialog.value = false },
        title = stringResource(R.string.dialog_error),
        message = stringResource(R.string.main_module_inactive_tips),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_ignore),
        positiveText = stringResource(R.string.main_module_inactive_dialog_lsposed)
    ) {
        onAction(MainPageAction.OpenLSPosed)
    }
    AlertDialog(
        visible = showRootDialog.value,
        onDismissRequest = { showRootDialog.value = false },
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.main_app_root_tips),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_ignore),
        positiveText = stringResource(R.string.button_enable)
    ) {
        onAction(MainPageAction.RefreshEnvironment)
    }
}

@Composable
private fun MainPageContent(
    onAction: (MainPageAction) -> Unit
) {
    val showTopPopup = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val contextMenuItems = listOf(
        stringResource(R.string.ui_title_menu_reboot),
        stringResource(R.string.menu_shortcut_lsposed)
    )

    HyperXPage(
        title = stringResource(R.string.page_main),
        navigationIcon = {},
        actions = {
            OverlayListPopup(
                show = showTopPopup.value,
                popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                alignment = PopupPositionProvider.Align.TopEnd,
                onDismissRequest = { showTopPopup.value = false }
            ) {
                ListPopupColumn {
                    contextMenuItems.forEachIndexed { index, string ->
                        DropdownImpl(
                            text = string,
                            optionSize = contextMenuItems.size,
                            isSelected = false,
                            onSelectedIndexChange = {
                                when (it) {
                                    0 -> onAction(MainPageAction.OpenMenu)
                                    1 -> onAction(MainPageAction.OpenLSPosed)
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
        itemPreferenceGroup(
            key = "MAIN_MODULE",
            position = ItemPosition.First
        ) {
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_hyper_helper_gray),
                title = stringResource(R.string.page_module),
            ) { onAction(MainPageAction.NavigateTo(Route.ModuleSettings)) }
        }
        itemPreferenceGroup(
            key = "MAIN_SCOPE",
        ) {
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_systemui),
                title = stringResource(R.string.page_systemui),
                onClick = { onAction(MainPageAction.NavigateTo(Route.SystemUI)) },
            )
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_android_green),
                title = stringResource(R.string.page_android),
                onClick = { onAction(MainPageAction.NavigateTo(Route.SystemFramework)) },
            )
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_home),
                title = stringResource(R.string.page_miui_home),
                onClick = { onAction(MainPageAction.NavigateTo(Route.MiuiHome)) },
            )
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_cleaner),
                title = stringResource(R.string.page_cleaner),
                onClick = { onAction(MainPageAction.NavigateTo(Route.CleanMaster)) },
            )
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_security_center),
                title = stringResource(if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center),
                onClick = { onAction(MainPageAction.NavigateTo(Route.SecurityCenter)) },
            )
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_others),
                title = stringResource(R.string.page_others),
                onClick = { onAction(MainPageAction.NavigateTo(Route.Others)) },
            )
        }
        itemPreferenceGroup(
            key = "MAIN_ABOUT",
            position = ItemPosition.Last
        ) {
            TextPreference(
                icon = ImageIcon(R.drawable.ic_header_about),
                title = stringResource(R.string.page_about),
                onClick = { onAction(MainPageAction.NavigateTo(Route.About)) },
            )
        }
    }
}
