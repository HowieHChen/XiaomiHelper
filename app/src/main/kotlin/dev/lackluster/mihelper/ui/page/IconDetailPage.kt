package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.base.TabRow
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownMode
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_IN
import dev.lackluster.mihelper.data.Constants.BatteryIndicator.STYLE_TEXT_OUT
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.BatteryIcon
import dev.lackluster.mihelper.ui.component.MobileIcons
import dev.lackluster.mihelper.ui.component.NetworkSpeed
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.ui.component.WifiIcon
import dev.lackluster.mihelper.ui.component.scaleDp
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun IconDetailPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val hapticFeedback = LocalHapticFeedback.current

    val tabRowItems = listOf(
        stringResource(R.string.ui_title_icon_detail_cellular),
        stringResource(R.string.ui_title_icon_detail_wifi),
        stringResource(R.string.ui_title_icon_detail_battery),
        stringResource(R.string.ui_title_icon_detail_net_speed),
    )
    val dropdownEntriesBatteryStyle = listOf(
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_default),
            summary = stringResource(R.string.icon_detail_battery_style_default_tips),
            iconRes = R.drawable.ic_battery_style_hidden
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_icon_only),
            summary = stringResource(R.string.icon_detail_battery_style_icon_only_tips),
            iconRes = R.drawable.ic_battery_style_icon_only
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_text_in),
            summary = stringResource(R.string.icon_detail_battery_style_text_in_tips),
            iconRes = R.drawable.ic_battery_style_text_in
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_line),
            summary = stringResource(R.string.icon_detail_battery_style_line_tips),
            iconRes = R.drawable.ic_battery_style_line
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_text_out),
            summary = stringResource(R.string.icon_detail_battery_style_text_out_tips),
            iconRes = R.drawable.ic_battery_style_text_out
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_text_only),
            summary = stringResource(R.string.icon_detail_battery_style_text_only_tips),
            iconRes = R.drawable.ic_battery_style_text_only
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_style_hidden),
            summary = stringResource(R.string.icon_detail_battery_style_hidden_tips),
            iconRes = R.drawable.ic_battery_style_hidden
        ),
    )
    val dropdownEntriesBatteryPercentage = listOf(
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_percent_mark_style_default),
            iconRes = R.drawable.ic_battery_percent_style_default
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_percent_mark_style_uni),
            iconRes = R.drawable.ic_battery_percent_style_digit
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_battery_percent_mark_style_hidden),
            iconRes = R.drawable.ic_battery_percent_style_hidden
        ),
    )
    val dropdownEntriesNetworkSpeed = listOf(
        DropDownEntry(
            title = stringResource(R.string.icon_detail_net_speed_style_default),
            iconRes = R.drawable.ic_net_speed_style_default
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_net_speed_style_separate),
            iconRes = R.drawable.ic_net_speed_style_separate
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_net_speed_style_separate_arrow),
            iconRes = R.drawable.ic_net_speed_style_separate_arrow
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_net_speed_style_separate_tri_filled),
            iconRes = R.drawable.ic_net_speed_style_separate_tri_filled
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_detail_net_speed_style_separate_tri_outline),
            iconRes = R.drawable.ic_net_speed_style_separate_tri_outline
        ),
    )

    var tabRowSelected by remember { mutableIntStateOf(0) }

    var hideSimAuto by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_SIM_AUTO, false)
    ) }
    var hideCellularActivity by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ACTIVITY, false)
    ) }
    var hideCellularType by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_TYPE, false)
    ) }
    var hideRoamGlobal by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM_GLOBAL, false)
    ) }
    var hideRoam by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM, false)
    ) }
    var hideSmallRoam by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_SMALL_ROAM, false)
    ) }
    var cellularTypeSingle by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE, false)
    ) }
    var cellularTypeSingleSwap by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SWAP, false)
    ) }
    var cellularTypeSingleSize by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE, false)
    ) }
    var cellularTypeSingleSizeVal by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL, 14.0f)
    ) }
    var cellularTypeCustom by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM, false)
    ) }
    var cellularTypeFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE, false)
    ) }
    var cellularTypeFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_VAL, 660)
    ) }
    var cellularTypeSingleFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE, false)
    ) }
    var cellularTypeSingleFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE_VAL, 400)
    ) }

    var hideWifiStandard by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_WIFI_STANDARD, false)
    ) }
    var hideWifiActivity by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_WIFI_ACTIVITY, false)
    ) }
    var rightWifiActivity by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.WIFI_ACTIVITY_RIGHT, false)
    ) }

    var batteryStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.BATTERY_STYLE, 0)
    ) }
    var batteryStyleCC by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.BATTERY_STYLE_CC, 0)
    ) }
    var batteryPadding by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_HORIZON, false)
    ) }
    var batteryPaddingStartVal by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_START_VAL, 0f)
    ) }
    var batteryPaddingEndVal by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_END_VAL, 0f)
    ) }
    var batteryHideChargeOut by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_BATTERY_CHARGE_OUT, false)
    ) }
    var batteryPercentMarkStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_MARK_STYLE, 0)
    ) }
    var batteryPercentInSize by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_IN_SIZE, false)
    ) }
    var batteryPercentInSizeVal by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_IN_SIZE_VAL, 9.599976f)
    ) }
    var batteryPercentOutSize by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_OUT_SIZE, false)
    ) }
    var batteryPercentOutSizeVal by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL, 12.5f)
    ) }

    var batteryPercentInFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN, false)
    ) }
    var batteryPercentInFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN_VAL, 620)
    ) }
    var batteryPercentOutFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT, false)
    ) }
    var batteryPercentOutFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT_VAL, 500)
    ) }
    var batteryPercentMarkFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK, false)
    ) }
    var batteryPercentMarkFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK_VAL, 600)
    ) }

    var netSpeedStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.NET_SPEED_MODE, 0)
    ) }
    var netSpeedNumFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER, false)
    ) }
    var netSpeedNumFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER_VAL, 630)
    ) }
    var netSpeedUnitFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT, false)
    ) }
    var netSpeedUnitFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT_VAL, 630)
    ) }
    var netSpeedSeparateFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.NET_SPEED_SEPARATE, false)
    ) }
    var netSpeedSeparateFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.NET_SPEED_SEPARATE_VAL, 630)
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_icon_detail),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        actions = {
            RebootMenuItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = Scope.SYSTEM_UI
            )
        }
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 12.dp)
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
                            style = netSpeedStyle,
                            netSpeedNumFW = netSpeedNumFW,
                            netSpeedNumFWVal = netSpeedNumFWVal,
                            netSpeedUnitFW = netSpeedUnitFW,
                            netSpeedUnitFWVal = netSpeedUnitFWVal,
                            netSpeedSeparateFW = netSpeedSeparateFW,
                            netSpeedSeparateFWVal = netSpeedSeparateFWVal
                        )
                        MobileIcons(
                            dataConnected = true,
                            hideCellularActivity = hideCellularActivity,
                            hideCellularType = hideCellularType,
                            cellularTypeSingle = cellularTypeSingle,
                            cellularTypeSingleSwap = cellularTypeSingleSwap,
                            cellularTypeSingleSize = cellularTypeSingleSize,
                            cellularTypeSingleSizeVal = cellularTypeSingleSizeVal,
                            cellularTypeFW = cellularTypeFW,
                            cellularTypeFWVal = cellularTypeFWVal,
                            cellularTypeSingleFW = cellularTypeSingleFW,
                            cellularTypeSingleFWVal = cellularTypeSingleFWVal,
                            hideRoamGlobal = hideRoamGlobal,
                            hideRoam = hideRoam,
                            hideSmallRoam = hideSmallRoam,
                        )
                        BatteryIcon(
                            batteryStyle = batteryStyle,
                            fallbackStyle = STYLE_TEXT_IN,
                            batteryPercentMarkStyle = batteryPercentMarkStyle,
                            batteryPadding = batteryPadding,
                            batteryPaddingStartVal = batteryPaddingStartVal,
                            batteryPaddingEndVal = batteryPaddingEndVal,
                            batteryHideChargeOut = batteryHideChargeOut,
                            batteryPercentInSize = batteryPercentInSize,
                            batteryPercentInSizeVal = batteryPercentInSizeVal,
                            batteryPercentOutSize = batteryPercentOutSize,
                            batteryPercentOutSizeVal = batteryPercentOutSizeVal,
                            batteryPercentInFW = batteryPercentInFW,
                            batteryPercentInFWVal = batteryPercentInFWVal,
                            batteryPercentOutFW = batteryPercentOutFW,
                            batteryPercentOutFWVal = batteryPercentOutFWVal,
                            batteryPercentMarkFW = batteryPercentMarkFW,
                            batteryPercentMarkFWVal = batteryPercentMarkFWVal
                        )
                    }
                    Row {
                        MobileIcons(
                            dataConnected = false,
                            hideCellularActivity = hideCellularActivity,
                            hideCellularType = hideCellularType,
                            cellularTypeSingle = cellularTypeSingle,
                            cellularTypeSingleSwap = cellularTypeSingleSwap,
                            cellularTypeSingleSize = cellularTypeSingleSize,
                            cellularTypeSingleSizeVal = cellularTypeSingleSizeVal,
                            cellularTypeFW = cellularTypeFW,
                            cellularTypeFWVal = cellularTypeFWVal,
                            cellularTypeSingleFW = cellularTypeSingleFW,
                            cellularTypeSingleFWVal = cellularTypeSingleFWVal,
                            hideRoamGlobal = hideRoamGlobal,
                            hideRoam = hideRoam,
                            hideSmallRoam = hideSmallRoam,
                        )
                        WifiIcon(
                            hideWifiActivity = hideWifiActivity,
                            hideWifiStandard = hideWifiStandard,
                            rightWifiActivity = rightWifiActivity
                        )
                        BatteryIcon(
                            batteryStyle = batteryStyleCC,
                            fallbackStyle = STYLE_TEXT_OUT,
                            batteryPercentMarkStyle = batteryPercentMarkStyle,
                            batteryPadding = batteryPadding,
                            batteryPaddingStartVal = batteryPaddingStartVal,
                            batteryPaddingEndVal = batteryPaddingEndVal,
                            batteryHideChargeOut = batteryHideChargeOut,
                            batteryPercentInSize = batteryPercentInSize,
                            batteryPercentInSizeVal = batteryPercentInSizeVal,
                            batteryPercentOutSize = batteryPercentOutSize,
                            batteryPercentOutSizeVal = batteryPercentOutSizeVal,
                            batteryPercentInFW = batteryPercentInFW,
                            batteryPercentInFWVal = batteryPercentInFWVal,
                            batteryPercentOutFW = batteryPercentOutFW,
                            batteryPercentOutFWVal = batteryPercentOutFWVal,
                            batteryPercentMarkFW = batteryPercentMarkFW,
                            batteryPercentMarkFWVal = batteryPercentMarkFWVal
                        )
                    }
                }
            }
        }
        item {
            TabRow(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp),
                tabs = tabRowItems,
                selectedTabIndex = tabRowSelected
            ) {
                tabRowSelected = it
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            }
            when (tabRowSelected) {
                0 -> {
                    PreferenceGroup {
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_hide_sim_auto),
                            summary = stringResource(R.string.icon_detail_cellular_hide_sim_auto_tips),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_SIM_AUTO
                        ) { hideSimAuto = it }
                        AnimatedVisibility(!hideSimAuto) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_hide_sim_one),
                                    key = Pref.Key.SystemUI.IconTuner.HIDE_SIM_ONE
                                )
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_hide_sim_two),
                                    key = Pref.Key.SystemUI.IconTuner.HIDE_SIM_TWO
                                )
                            }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_hide_activity),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ACTIVITY
                        ) { hideCellularActivity = it }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_hide_type),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_TYPE
                        ) { hideCellularType = it }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_hide_roam_global),
                            summary = stringResource(R.string.icon_detail_cellular_hide_roam_global_tips),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM_GLOBAL
                        ) { hideRoamGlobal = it }
                        AnimatedVisibility(!hideRoamGlobal) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_hide_roam),
                                    key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_ROAM
                                ) { hideRoam = it }
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_hide_roam_small),
                                    key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_SMALL_ROAM
                                ) { hideSmallRoam = it }
                            }
                        }
                    }
                    PreferenceGroup(
                        stringResource(R.string.ui_title_icon_detail_cellular_type)
                    ) {
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_single),
                            key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE
                        ) { cellularTypeSingle = it }
                        AnimatedVisibility(cellularTypeSingle) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_single_swap),
                                    summary = stringResource(R.string.icon_detail_cellular_single_swap_tips),
                                    key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SWAP
                                ) { cellularTypeSingleSwap = it }
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_cellular_single_size),
                                    key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE
                                ) { cellularTypeSingleSize = it }
                                AnimatedVisibility(cellularTypeSingleSize) {
                                    EditTextPreference(
                                        title = stringResource(R.string.icon_detail_cellular_single_size_value),
                                        key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL,
                                        defValue = 14f,
                                        dataType = EditTextDataType.FLOAT,
                                        isValueValid = {
                                            (it as? Float ?: -1.0f) > 0.0f
                                        }
                                    ) { _, value -> cellularTypeSingleSizeVal = value as? Float ?: 0.0f}
                                }
                            }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_type_map),
                            key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM
                        ) { cellularTypeCustom = it }
                        AnimatedVisibility(cellularTypeCustom) {
                            EditTextPreference(
                                title = stringResource(R.string.icon_detail_cellular_type_map_value),
                                key = Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM_VAL,
                                defValue = ",G,E,3G,H,H+,4G,4G+,,LTE,5G,5G,5G+,5GA,5G",
                                dataType = EditTextDataType.STRING,
                                dialogMessage = stringResource(R.string.icon_detail_cellular_type_map_msg),
                                isValueValid = {
                                    it is String && it.isNotEmpty() && it.split(',').let { list ->
                                        list.size == 15 || (list.size == 1 && list[0].isNotBlank())
                                    }
                                },
                                valuePosition = ValuePosition.HIDDEN,
                            )
                        }
                    }
                    PreferenceGroup(
                        stringResource(R.string.ui_title_icon_detail_font_weight)
                    ) {
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_fw_type),
                            key = Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE
                        ) { cellularTypeFW = it }
                        AnimatedVisibility(cellularTypeFW) {
                            SeekBarPreference(
                                title = stringResource(R.string.icon_detail_cellular_fw_type_weight),
                                key = Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_VAL,
                                defValue = 660,
                                min = 1,
                                max = 1000
                            ) { cellularTypeFWVal = it }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_cellular_fw_type_single),
                            key = Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE
                        ) { cellularTypeSingleFW = it }
                        AnimatedVisibility(cellularTypeSingleFW) {
                            SeekBarPreference(
                                title = stringResource(R.string.icon_detail_cellular_fw_type_single_weight),
                                key = Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE_VAL,
                                defValue = 400,
                                min = 1,
                                max = 1000
                            ) { cellularTypeSingleFWVal = it }
                        }
                    }
                    PreferenceGroup(
                        title = stringResource(R.string.ui_title_icon_detail_other),
                        last = true
                    ) {
                        SwitchPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_vowifi),
                            title = stringResource(R.string.icon_detail_cellular_hide_vowifi),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VO_WIFI
                        )
                        SwitchPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_signal_volte),
                            title = stringResource(R.string.icon_detail_cellular_hide_volte),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VOLTE
                        )
                        SwitchPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volte_no_service),
                            title = stringResource(R.string.icon_detail_cellular_hide_volte_no_service),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_VOLTE_NO_SERVICE
                        )
                        SwitchPreference(
                            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_speech_hd),
                            title = stringResource(R.string.icon_detail_cellular_hide_speech_hd),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_CELLULAR_SPEECH_HD
                        )
                    }
                }
                1 -> {
                    PreferenceGroup(last = true) {
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_wifi_hide_standard),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_WIFI_STANDARD
                        ) { hideWifiStandard = it }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_wifi_hide_activity),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_WIFI_ACTIVITY
                        ) { hideWifiActivity = it }
                        AnimatedVisibility(hideWifiStandard && !hideWifiActivity) {
                            SwitchPreference(
                                title = stringResource(R.string.icon_detail_wifi_right_activity),
                                key = Pref.Key.SystemUI.IconTuner.WIFI_ACTIVITY_RIGHT
                            ) { rightWifiActivity = it }
                        }
                    }
                }
                2 -> {
                    PreferenceGroup {
                        DropDownPreference(
                            title = stringResource(R.string.icon_detail_battery_bar_style),
                            entries = dropdownEntriesBatteryStyle,
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_STYLE,
                            mode = DropDownMode.Dialog
                        ) { batteryStyle = it }
                        DropDownPreference(
                            title = stringResource(R.string.icon_detail_battery_cc_style),
                            entries = dropdownEntriesBatteryStyle,
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_STYLE_CC,
                            mode = DropDownMode.Dialog
                        ) { batteryStyleCC = it }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_layout_custom),
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_HORIZON
                        ) { batteryPadding = it }
                        AnimatedVisibility(batteryPadding) {
                            Column {
                                EditTextPreference(
                                    title = stringResource(R.string.icon_detail_battery_padding_start),
                                    key = Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_START_VAL,
                                    defValue = 0.0f,
                                    dataType = EditTextDataType.FLOAT
                                ) { str, value -> batteryPaddingStartVal = value as Float }
                                EditTextPreference(
                                    title = stringResource(R.string.icon_detail_battery_padding_end),
                                    key = Pref.Key.SystemUI.IconTuner.BATTERY_PADDING_END_VAL,
                                    defValue = 0.0f,
                                    dataType = EditTextDataType.FLOAT
                                ) { str, value -> batteryPaddingEndVal = value as Float }
                            }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_hide_charge),
                            summary = stringResource(R.string.icon_detail_battery_hide_charge_tips),
                            key = Pref.Key.SystemUI.IconTuner.HIDE_BATTERY_CHARGE_OUT
                        ) { batteryHideChargeOut = it }
                    }
                    PreferenceGroup(
                        stringResource(R.string.ui_title_icon_detail_batter_percentage)
                    ) {
                        DropDownPreference(
                            title = stringResource(R.string.icon_detail_battery_percent_mark_style),
                            entries = dropdownEntriesBatteryPercentage,
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_MARK_STYLE,
                            mode = DropDownMode.Dialog
                        ) { batteryPercentMarkStyle = it }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_percent_out_size),
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_OUT_SIZE
                        ) { batteryPercentOutSize = it }
                        AnimatedVisibility(batteryPercentOutSize) {
                            EditTextPreference(
                                title = stringResource(R.string.icon_detail_battery_percent_out_size_value),
                                key = Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL,
                                defValue = 12.5f,
                                dataType = EditTextDataType.FLOAT,
                                isValueValid = {
                                    (it as? Float ?: -1.0f) > 0.0f
                                }
                            ) { _, value -> batteryPercentOutSizeVal = value as? Float ?: 0.0f}
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_percent_in_size),
                            key = Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_IN_SIZE
                        ) { batteryPercentInSize = it }
                        AnimatedVisibility(batteryPercentInSize) {
                            EditTextPreference(
                                title = stringResource(R.string.icon_detail_battery_percent_in_size_value),
                                key = Pref.Key.SystemUI.IconTuner.BATTERY_PERCENT_IN_SIZE_VAL,
                                defValue = 9.599976f,
                                dataType = EditTextDataType.FLOAT,
                                isValueValid = {
                                    (it as? Float ?: -1.0f) > 0.0f
                                }
                            ) { _, value -> batteryPercentInSizeVal = value as? Float ?: 0.0f}
                        }
                    }
                    PreferenceGroup(
                        title = stringResource(R.string.ui_title_icon_detail_font_weight),
                        last = true
                    ) {
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_fw_percent_out),
                            key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT
                        ) { batteryPercentOutFW = it }
                        AnimatedVisibility(batteryPercentOutFW) {
                            SeekBarPreference(
                                title = stringResource(R.string.icon_detail_battery_fw_percent_out_weight),
                                key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT_VAL,
                                defValue = 500,
                                min = 1,
                                max = 1000
                            ) { batteryPercentOutFWVal = it }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_fw_percent_mark),
                            key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK
                        ) { batteryPercentMarkFW = it }
                        AnimatedVisibility(batteryPercentMarkFW) {
                            SeekBarPreference(
                                title = stringResource(R.string.icon_detail_battery_fw_percent_mark_weight),
                                key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK_VAL,
                                defValue = 600,
                                min = 1,
                                max = 1000
                            ) { batteryPercentMarkFWVal = it }
                        }
                        SwitchPreference(
                            title = stringResource(R.string.icon_detail_battery_fw_percent_in),
                            key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN
                        ) { batteryPercentInFW = it }
                        AnimatedVisibility(batteryPercentInFW) {
                            SeekBarPreference(
                                title = stringResource(R.string.icon_detail_battery_fw_percent_in_weight),
                                key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN_VAL,
                                defValue = 620,
                                min = 1,
                                max = 1000
                            ) { batteryPercentInFWVal = it }
                        }
                    }
                }
                3 -> {
                    PreferenceGroup {
                        DropDownPreference(
                            title = stringResource(R.string.icon_detail_net_speed_style),
                            entries = dropdownEntriesNetworkSpeed,
                            key = Pref.Key.SystemUI.IconTuner.NET_SPEED_MODE,
                            mode = DropDownMode.Dialog
                        ) { netSpeedStyle = it }
                    }
                    PreferenceGroup(
                        title = stringResource(R.string.ui_title_icon_detail_font_weight),
                        last = true
                    ) {
                        AnimatedVisibility(netSpeedStyle == 0) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_net_speed_fw_num),
                                    key = Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER
                                ) { netSpeedNumFW = it }
                                AnimatedVisibility(netSpeedNumFW) {
                                    SeekBarPreference(
                                        title = stringResource(R.string.icon_detail_net_speed_fw_num_weight),
                                        key = Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER_VAL,
                                        defValue = 630,
                                        min = 1,
                                        max = 1000
                                    ) { netSpeedNumFWVal = it }
                                }
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_net_speed_fw_unit),
                                    key = Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT
                                ) { netSpeedUnitFW = it }
                                AnimatedVisibility(netSpeedUnitFW) {
                                    SeekBarPreference(
                                        title = stringResource(R.string.icon_detail_net_speed_fw_unit_weight),
                                        key = Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT_VAL,
                                        defValue = 630,
                                        min = 1,
                                        max = 1000
                                    ) { netSpeedUnitFWVal = it }
                                }
                            }
                        }
                        AnimatedVisibility(netSpeedStyle != 0) {
                            Column {
                                SwitchPreference(
                                    title = stringResource(R.string.icon_detail_net_speed_fw_separate),
                                    key = Pref.Key.SystemUI.FontWeight.NET_SPEED_SEPARATE
                                ) { netSpeedSeparateFW = it }
                                AnimatedVisibility(netSpeedSeparateFW) {
                                    SeekBarPreference(
                                        title = stringResource(R.string.icon_detail_net_speed_fw_separate_weight),
                                        key = Pref.Key.SystemUI.FontWeight.NET_SPEED_SEPARATE_VAL,
                                        defValue = 630,
                                        min = 1,
                                        max = 1000
                                    ) { netSpeedSeparateFWVal = it }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}