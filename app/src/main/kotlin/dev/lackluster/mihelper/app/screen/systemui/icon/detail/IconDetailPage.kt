package dev.lackluster.mihelper.app.screen.systemui.icon.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.component.Card
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.LoadingDialog
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.layout.TabRow
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.BatteryIcon
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.CustomSignalIcon
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.MobileIcons
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.NetworkSpeedIcon
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.StandaloneTypeIcon
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.component.WifiIcon
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.StackedMobileAction
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.batteryTabContent
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.mobileTabContent
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.netSpeedTabContent
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.stackedMobileTabContent
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs.wlanTabContent
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import org.koin.androidx.compose.koinViewModel

@Composable
fun IconDetailPage(
    viewModel: IconDetailViewModel = koinViewModel(),
    stackedVM: StackedMobileViewModel = koinViewModel()
) {
    val hapticFeedback = LocalHapticFeedback.current

    val pageUiState by viewModel.pageUiState.collectAsState()
    val selectedTab = pageUiState.selectedTab
    val tabs = IconTab.entries

    val svgUiState by stackedVM.screenState.collectAsState()
    val fontUpdateTrigger by stackedVM.fontUpdateTrigger.collectAsState()
    val stackedPictures by stackedVM.stackedPictures.collectAsState()
    val singlePictures by stackedVM.singlePictures.collectAsState()
    val stackedState by stackedVM.configState.collectAsState()
    val singleAnchor by stackedVM.singleAnchor.collectAsState()
    val stackedAnchor by stackedVM.stackedAnchor.collectAsState()

    val mobileState by viewModel.mobileState.collectAsState()
    val wlanState by viewModel.wlanState.collectAsState()
    val batteryState by viewModel.batteryState.collectAsState()
    val netSpeedState by viewModel.netSpeedState.collectAsState()

    val isAnyLoading = svgUiState.isLoading || pageUiState.isLoading
    val currentErrorMessage = svgUiState.errorDialogMessage ?: pageUiState.errorDialogMessage

    val singleSvgLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { stackedVM.handleSvgFileUri(it, isStacked = false) }
    }
    val stackedSvgLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { stackedVM.handleSvgFileUri(it, isStacked = true) }
    }
    val fontPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            stackedVM.importFontFromUri(uri)
        }
    }

    val onAction: (StackedMobileAction) -> Unit = { action ->
        when (action) {
            is StackedMobileAction.ApplyManualPath -> {
                if (action.path.isNotBlank()) stackedVM.applyFontFromPath(action.path)
            }
            StackedMobileAction.ImportLocalFont -> {
                fontPickerLauncher.launch(arrayOf("font/ttf", "font/otf", "application/octet-stream"))
            }
            is StackedMobileAction.UpdateCustomTypeMap -> {
                viewModel.validateAndUpdateCustomTypeMap(action.list)
            }
            is StackedMobileAction.ImportSVGFile -> {
                if (action.isStacked) {
                    stackedSvgLauncher.launch(arrayOf("*/*"))
                } else {
                    singleSvgLauncher.launch(arrayOf("*/*"))
                }
            }
        }
    }

    HyperXPage(
        title = stringResource(R.string.page_status_bar_icon_detail),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI),
            )
        },
        fixedHeader = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        NetworkSpeedIcon(
                            state = netSpeedState,
                            nativeTypefaceProvider = viewModel::getTypeface,
                        )
                        if (stackedState.enabled) {
                            CustomSignalIcon(
                                picture = stackedPictures["4_4"],
                                anchor = stackedAnchor,
                                netType = if (stackedState.small.showOnStacked) "5GA" else "",
                                state = stackedState,
                                fontUpdateTrigger = fontUpdateTrigger,
                                nativeTypefaceProvider = stackedVM::getTypeface
                            )
                            StandaloneTypeIcon(
                                isVisible = !stackedState.large.hideWhenDisconnect,
                                netType = "5GA",
                                state = stackedState,
                                fontUpdateTrigger = fontUpdateTrigger,
                                typefaceProvider = stackedVM::getTypeface
                            )
                        } else {
                            MobileIcons(
                                mobileTypeText = "4G",
                                dataConnected = true,
                                state = mobileState,
                                nativeTypefaceProvider = viewModel::getTypeface
                            )
                        }
                        BatteryIcon(
                            batteryStyle = batteryState.styleStatusBar,
                            fallbackStyle = STYLE_TEXT_IN,
                            state = batteryState,
                            nativeTypefaceProvider = viewModel::getTypeface,
                        )
                    }
                    Row {
                        if (stackedState.enabled) {
                            CustomSignalIcon(
                                picture = singlePictures["4"],
                                anchor = singleAnchor,
                                netType = if (stackedState.small.showOnSingle) "5GA" else "",
                                state = stackedState,
                                fontUpdateTrigger = fontUpdateTrigger,
                                nativeTypefaceProvider = stackedVM::getTypeface
                            )
                            CustomSignalIcon(
                                picture = singlePictures["4"],
                                anchor = singleAnchor,
                                netType = if (stackedState.small.showOnSingle) (if (stackedState.small.showRoaming) "R4G" else "4G") else "",
                                state = stackedState,
                                fontUpdateTrigger = fontUpdateTrigger,
                                nativeTypefaceProvider = stackedVM::getTypeface
                            )
                            StandaloneTypeIcon(
                                isVisible = !stackedState.large.hideWhenWifi,
                                netType = "5GA",
                                state = stackedState,
                                fontUpdateTrigger = fontUpdateTrigger,
                                typefaceProvider = stackedVM::getTypeface
                            )
                        } else {
                            MobileIcons(
                                mobileTypeText = "4G",
                                dataConnected = false,
                                state = mobileState,
                                nativeTypefaceProvider = viewModel::getTypeface
                            )
                        }
                        WifiIcon(
                            state = wlanState,
                        )
                        BatteryIcon(
                            batteryStyle = batteryState.styleControlCenter,
                            fallbackStyle = STYLE_TEXT_OUT,
                            state = batteryState,
                            nativeTypefaceProvider = viewModel::getTypeface,
                        )
                    }
                }
            }
        }
    ) {
        item(
            key = "TAB_ROW"
        ) {
            TabRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                tabs = tabs.map { stringResource(it.titleResId) },
                selectedTabIndex = selectedTab.ordinal,
                onTabSelected = { index ->
                    viewModel.selectTab(tabs[index])
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            )
        }
        itemPreferenceGroup(
            key = "MOBILE_MASTER_SWITCH",
            visible = (selectedTab == IconTab.MOBILE)
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.StackedMobile.ENABLED,
                icon = ImageIcon(R.drawable.ic_stat_sys_stacked_icon),
                title = stringResource(R.string.icon_tuner_stacked_mobile),
                summary = stringResource(R.string.icon_tuner_stacked_mobile_tips),
            )
        }
        mobileTabContent(
            isVisible = (selectedTab == IconTab.MOBILE && !stackedState.enabled),
            mobileState = mobileState,
            validateAndUpdateCustomTypeMap = viewModel::validateAndUpdateCustomTypeMap,
        )
        stackedMobileTabContent(
            isVisible = (selectedTab == IconTab.MOBILE && stackedState.enabled),
            stackedState = stackedState,
            mobileState = mobileState,
            onAction = onAction
        )
        wlanTabContent(
            isVisible = selectedTab == IconTab.WLAN,
            wlanState = wlanState,
        )
        batteryTabContent(
            isVisible = selectedTab == IconTab.BATTERY,
            batteryState = batteryState,
        )
        netSpeedTabContent(
            isVisible = selectedTab == IconTab.NET_SPEED,
            netSpeedState = netSpeedState,
        )
    }

    LoadingDialog(
        visible = isAnyLoading,
        title = stringResource(dev.lackluster.hyperx.R.string.loading_dialog_processing),
        cancelable = false,
    )

    AlertDialog(
        visible = currentErrorMessage != null,
        title = stringResource(R.string.dialog_error),
        message = currentErrorMessage?.asString(),
        cancelable = false,
        onDismissRequest = {
            stackedVM.dismissErrorDialog()
            viewModel.dismissErrorDialog()
        },
    )
}