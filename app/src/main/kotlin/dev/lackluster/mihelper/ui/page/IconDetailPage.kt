package dev.lackluster.mihelper.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lackluster.hyperx.compose.navigation.Navigator
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.base.LoadingDialog
import dev.lackluster.hyperx.compose.base.TabRow
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.StackedMobile
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.BatteryIcon
import dev.lackluster.mihelper.ui.component.CustomSignalIcon
import dev.lackluster.mihelper.ui.component.MobileIcons
import dev.lackluster.mihelper.ui.component.NetworkSpeed
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.ui.component.StandaloneTypeIcon
import dev.lackluster.mihelper.ui.component.WifiIcon
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.component.scaleDp
import dev.lackluster.mihelper.ui.model.IconTab
import dev.lackluster.mihelper.ui.page.icondetail.batteryTabContent
import dev.lackluster.mihelper.ui.page.icondetail.mobileTabContent
import dev.lackluster.mihelper.ui.page.icondetail.netSpeedTabContent
import dev.lackluster.mihelper.ui.page.icondetail.stackedMobileTabContent
import dev.lackluster.mihelper.ui.page.icondetail.wlanTabContent
import dev.lackluster.mihelper.ui.provider.LocalStackedMobileViewModel
import dev.lackluster.mihelper.ui.viewmodel.IconDetailPageViewModel
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun IconDetailPage(navigator: Navigator, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val hapticFeedback = LocalHapticFeedback.current

    val stackedViewModel = LocalStackedMobileViewModel.current
    val stackedPictures by stackedViewModel.stackedPictures.collectAsState()
    val singlePictures by stackedViewModel.singlePictures.collectAsState()
    val stackedState by stackedViewModel.configState.collectAsState()
    val svgUiState by stackedViewModel.screenState.collectAsState()

    val pageViewModel: IconDetailPageViewModel = viewModel()
    val pageUiState by pageViewModel.pageUiState.collectAsState()
    val mobileState by pageViewModel.mobileState.collectAsState()
    val wlanState by pageViewModel.wlanState.collectAsState()
    val batteryState by pageViewModel.batteryState.collectAsState()
    val netSpeedState by pageViewModel.netSpeedState.collectAsState()

    val isAnyLoading = svgUiState.isLoading || pageUiState.isLoading
    val currentErrorMessage = svgUiState.errorDialogMessage ?: pageUiState.errorDialogMessage

    val tabs = IconTab.entries

    LaunchedEffect(stackedState.signal.effectiveStackedSVG, stackedState.signal.alphaFg, stackedState.signal.alphaBg, stackedState.signal.alphaError) {
        stackedViewModel.updateStackedSvg(stackedState.signal.effectiveStackedSVG, stackedState.signal.alphaFg, stackedState.signal.alphaBg, stackedState.signal.alphaError)
    }
    LaunchedEffect(stackedState.signal.effectiveSingleSVG, stackedState.signal.alphaFg, stackedState.signal.alphaBg, stackedState.signal.alphaError) {
        stackedViewModel.updateSingleSvg(stackedState.signal.effectiveSingleSVG, stackedState.signal.alphaFg, stackedState.signal.alphaBg, stackedState.signal.alphaError)
    }

    BasePage(
        navigator,
        adjustPadding,
        stringResource(R.string.page_status_bar_icon_detail),
        MainActivity.blurEnabled,
        mode,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        actions = {
            RebootMenuItem(
                stringResource(R.string.scope_systemui),
                Scope.SYSTEM_UI
            )
        },
        fixedContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp, top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(24.scaleDp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        NetworkSpeed(
                            style = netSpeedState.style,
                            unitStyle = netSpeedState.unitStyle,
                            netSpeedNumFW = netSpeedState.numberFont.enabled,
                            netSpeedNumFWVal = netSpeedState.numberFont.weight,
                            netSpeedUnitFW = netSpeedState.unitFont.enabled,
                            netSpeedUnitFWVal = netSpeedState.unitFont.weight,
                            netSpeedSeparateFW = netSpeedState.separateStyleFont.enabled,
                            netSpeedSeparateFWVal = netSpeedState.separateStyleFont.weight
                        )
                        if (stackedState.enabled) {
                            CustomSignalIcon(
                                picture = stackedPictures["4_4"],
                                anchor = stackedViewModel.getAnchor(stackedState.signal.effectiveStackedSVG),
                                netType = if (stackedState.small.showOnStacked) "5GA" else "",
                                fontSizeDp = stackedState.small.size,
                                fontWeight = stackedState.small.weight,
                                fontMode = stackedState.font.mode,
                                fontPath = stackedState.font.path,
                                condensedWidth = stackedState.font.condensedWidth,
                                typefaceProvider = stackedViewModel::getTypeface
                            )
                            StandaloneTypeIcon(
                                netType = "5GA",
                                isVisible = !stackedState.large.hideWhenDisconnect,
                                fontSizeDp = stackedState.large.size,
                                fontWeight = stackedState.large.weight,
                                fontMode = stackedState.font.mode,
                                fontPath = stackedState.font.path,
                                condensedWidth = stackedState.font.condensedWidth,
                                paddingStartDp = stackedState.large.paddingStart,
                                paddingEndDp = stackedState.large.paddingEnd,
                                verticalOffsetDp = stackedState.large.verticalOffset,
                                typefaceProvider = stackedViewModel::getTypeface
                            )
                        } else {
                            MobileIcons(
                                dataConnected = true,
                                hideCellularActivity = mobileState.hideActivity,
                                hideCellularType = mobileState.hideSmallType,
                                cellularTypeSingle = mobileState.separateType,
                                cellularTypeSingleSwap = mobileState.rightSeparateType,
                                cellularTypeSingleSize = mobileState.separateTypeSize.enabled,
                                cellularTypeSingleSizeVal = mobileState.separateTypeSize.size,
                                cellularTypeFW = mobileState.smallTypeFont.enabled,
                                cellularTypeFWVal = mobileState.smallTypeFont.weight,
                                cellularTypeSingleFW = mobileState.separateTypeFont.enabled,
                                cellularTypeSingleFWVal = mobileState.separateTypeFont.weight,
                                hideRoamGlobal = mobileState.hideRoamGlobal,
                                hideRoam = mobileState.hideLargeRoam,
                                hideSmallRoam = mobileState.hideSmallRoam,
                            )
                        }
                        BatteryIcon(
                            batteryStyle = batteryState.styleStatusBar,
                            fallbackStyle = STYLE_TEXT_IN,
                            batteryPercentMarkStyle = batteryState.percentMarkStyle,
                            batteryPadding = batteryState.customPadding,
                            batteryPaddingStartVal = batteryState.paddingStart,
                            batteryPaddingEndVal = batteryState.paddingEnd,
                            batteryHideChargeOut = batteryState.hideCharge,
                            batteryPercentInSize = batteryState.percentInSize.enabled,
                            batteryPercentInSizeVal = batteryState.percentInSize.size,
                            batteryPercentOutSize = batteryState.percentOutSize.enabled,
                            batteryPercentOutSizeVal = batteryState.percentOutSize.size,
                            batteryPercentInFW = batteryState.percentInFont.enabled,
                            batteryPercentInFWVal = batteryState.percentInFont.weight,
                            batteryPercentOutFW = batteryState.percentOutFont.enabled,
                            batteryPercentOutFWVal = batteryState.percentOutFont.weight,
                            batteryPercentMarkFW = batteryState.percentMarkFont.enabled,
                            batteryPercentMarkFWVal = batteryState.percentMarkFont.weight,
                        )
                    }
                    Row {
                        if (stackedState.enabled) {
                            CustomSignalIcon(
                                picture = singlePictures["4"],
                                anchor = stackedViewModel.getAnchor(stackedState.signal.effectiveSingleSVG),
                                netType = if (stackedState.small.showOnSingle) "5GA" else "",
                                fontSizeDp = stackedState.small.size,
                                fontWeight = stackedState.small.weight,
                                fontMode = stackedState.font.mode,
                                fontPath = stackedState.font.path,
                                condensedWidth = stackedState.font.condensedWidth,
                                typefaceProvider = stackedViewModel::getTypeface
                            )
                            CustomSignalIcon(
                                picture = singlePictures["4"],
                                anchor = stackedViewModel.getAnchor(stackedState.signal.effectiveSingleSVG),
                                netType = if (stackedState.small.showOnSingle) (if (stackedState.small.showRoaming) "R4G" else "4G") else "",
                                fontSizeDp = stackedState.small.size,
                                fontWeight = stackedState.small.weight,
                                fontMode = stackedState.font.mode,
                                fontPath = stackedState.font.path,
                                condensedWidth = stackedState.font.condensedWidth,
                                typefaceProvider = stackedViewModel::getTypeface
                            )
                            StandaloneTypeIcon(
                                netType = "5GA",
                                isVisible = !stackedState.large.hideWhenWifi,
                                fontSizeDp = stackedState.large.size,
                                fontWeight = stackedState.large.weight,
                                fontMode = stackedState.font.mode,
                                fontPath = stackedState.font.path,
                                condensedWidth = stackedState.font.condensedWidth,
                                paddingStartDp = stackedState.large.paddingStart,
                                paddingEndDp = stackedState.large.paddingEnd,
                                verticalOffsetDp = stackedState.large.verticalOffset,
                                typefaceProvider = stackedViewModel::getTypeface
                            )
                        } else {
                            MobileIcons(
                                dataConnected = false,
                                hideCellularActivity = mobileState.hideActivity,
                                hideCellularType = mobileState.hideSmallType,
                                cellularTypeSingle = mobileState.separateType,
                                cellularTypeSingleSwap = mobileState.rightSeparateType,
                                cellularTypeSingleSize = mobileState.separateTypeSize.enabled,
                                cellularTypeSingleSizeVal = mobileState.separateTypeSize.size,
                                cellularTypeFW = mobileState.smallTypeFont.enabled,
                                cellularTypeFWVal = mobileState.smallTypeFont.weight,
                                cellularTypeSingleFW = mobileState.separateTypeFont.enabled,
                                cellularTypeSingleFWVal = mobileState.separateTypeFont.weight,
                                hideRoamGlobal = mobileState.hideRoamGlobal,
                                hideRoam = mobileState.hideLargeRoam,
                                hideSmallRoam = mobileState.hideSmallRoam,
                            )
                        }
                        WifiIcon(
                            hideWifiActivity = wlanState.hideWifiActivity,
                            hideWifiStandard = wlanState.hideWifiStandard,
                            rightWifiActivity = wlanState.rightWifiActivity
                        )
                        BatteryIcon(
                            batteryStyle = batteryState.styleControlCenter,
                            fallbackStyle = STYLE_TEXT_OUT,
                            batteryPercentMarkStyle = batteryState.percentMarkStyle,
                            batteryPadding = batteryState.customPadding,
                            batteryPaddingStartVal = batteryState.paddingStart,
                            batteryPaddingEndVal = batteryState.paddingEnd,
                            batteryHideChargeOut = batteryState.hideCharge,
                            batteryPercentInSize = batteryState.percentInSize.enabled,
                            batteryPercentInSizeVal = batteryState.percentInSize.size,
                            batteryPercentOutSize = batteryState.percentOutSize.enabled,
                            batteryPercentOutSizeVal = batteryState.percentOutSize.size,
                            batteryPercentInFW = batteryState.percentInFont.enabled,
                            batteryPercentInFWVal = batteryState.percentInFont.weight,
                            batteryPercentOutFW = batteryState.percentOutFont.enabled,
                            batteryPercentOutFWVal = batteryState.percentOutFont.weight,
                            batteryPercentMarkFW = batteryState.percentMarkFont.enabled,
                            batteryPercentMarkFWVal = batteryState.percentMarkFont.weight,
                        )
                    }
                }
            }
        }
    ) {
        item {
            TabRow(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp),
                tabs = tabs.map { stringResource(it.titleResId) },
                selectedTabIndex = pageUiState.selectedTab.ordinal
            ) {
                pageViewModel.selectTab(tabs[it])
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            }
        }
        itemPreferenceGroup(
            key = "MOBILE",
            visible = (pageUiState.selectedTab == IconTab.MOBILE)
        ) {
            SwitchPreference(
                icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_stacked_icon),
                title = stringResource(R.string.icon_tuner_stacked_mobile),
                summary = stringResource(R.string.icon_tuner_stacked_mobile_tips),
                value = stackedState.enabled,
                onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.ENABLED, it) }
            )
        }
        mobileTabContent(
            isVisible = (pageUiState.selectedTab == IconTab.MOBILE && !stackedState.enabled),
            mobileState = mobileState,
            validateAndUpdateCustomTypeMap = pageViewModel::validateAndUpdateCustomTypeMap,
            updateMobilePreference = pageViewModel::updateMobilePreference
        )
        stackedMobileTabContent(
            isVisible = pageUiState.selectedTab == IconTab.MOBILE && stackedState.enabled,
            stackedState = stackedState,
            mobileState = mobileState,
            stackedViewModel = stackedViewModel,
            validateAndUpdateCustomTypeMap = pageViewModel::validateAndUpdateCustomTypeMap,
            updateMobilePreference = pageViewModel::updateMobilePreference
        )
        wlanTabContent(
            isVisible = pageUiState.selectedTab == IconTab.WLAN,
            wlanState = wlanState,
            updateWlanPreference = pageViewModel::updateWlanPreference
        )
        batteryTabContent(
            isVisible = pageUiState.selectedTab == IconTab.BATTERY,
            batteryState = batteryState,
            updateBatteryPreference = pageViewModel::updateBatteryPreference
        )
        netSpeedTabContent(
            isVisible = pageUiState.selectedTab == IconTab.NET_SPEED,
            netSpeedState = netSpeedState,
            updateNetSpeedPreference = pageViewModel::updateNetSpeedPreference
        )
    }

    if (isAnyLoading) {
        LoadingDialog()
    }

    currentErrorMessage?.let { errorMessage ->
        AlertDialog(
            title = stringResource(R.string.dialog_error),
            message = errorMessage,
            cancelable = false,
            onDismissRequest = {
                stackedViewModel.dismissErrorDialog()
                pageViewModel.dismissErrorDialog()
            },
        )
    }
}