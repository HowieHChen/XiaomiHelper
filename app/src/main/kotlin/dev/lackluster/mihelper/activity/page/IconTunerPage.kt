package dev.lackluster.mihelper.activity.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownMode
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref

@Composable
fun IconTurnerPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var spValueBatteryStyle by remember { mutableIntStateOf(SafeSP.getInt(Pref.Key.SystemUI.IconTurner.BATTERY_STYLE)) }
    var spValueModifyBatteryPercentageSize by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE)) }
    var spValueModifyBatteryPadding by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.IconTurner.BATTERY_MODIFY_PADDING)) }

    val dropdownEntriesAdvVisible = listOf(
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_default)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_all)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_statusbar)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_qs)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_hidden)),
    )
    val dropdownEntriesBatteryStyle = listOf(
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_style_default),
            iconRes = R.drawable.ic_battery_style_default
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_style_both),
            iconRes = R.drawable.ic_battery_style_both
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_style_icon),
            iconRes = R.drawable.ic_battery_style_icon
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_style_percentage),
            iconRes = R.drawable.ic_battery_style_digit
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_style_hidden),
            iconRes = R.drawable.ic_battery_style_hidden
        ),
    )
    val dropdownEntriesBatteryPercentage = listOf(
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_percentage_symbol_style_default),
            iconRes = R.drawable.ic_battery_percentage_style_default
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_percentage_symbol_style_uni),
            iconRes = R.drawable.ic_battery_percentage_style_digit
        ),
        DropDownEntry(
            title = stringResource(R.string.icon_tuner_battery_percentage_symbol_style_hidden),
            iconRes = R.drawable.ic_battery_percentage_style_hidden
        ),
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_icon_tuner),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_mobile),
                first = true
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile),
                    title = stringResource(R.string.icon_tuner_mobile_mobile),
                    summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.MOBILE
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile_1),
                    title = stringResource(R.string.icon_tuner_mobile_hide_sim_one),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_SIM_ONE
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile_2),
                    title = stringResource(R.string.icon_tuner_mobile_hide_sim_two),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_SIM_TWO
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile_activity),
                    title = stringResource(R.string.icon_tuner_mobile_hide_mobile_activity),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_MOBILE_ACTIVITY
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile_type),
                    title = stringResource(R.string.icon_tuner_mobile_hide_mobile_type),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_MOBILE_TYPE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_no_sim),
                    title = stringResource(R.string.icon_tuner_mobile_no_sim),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.NO_SIM
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_hd),
                    title = stringResource(R.string.icon_tuner_mobile_hd_new),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.HD_NEW
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_hd_small),
                    title = stringResource(R.string.icon_tuner_mobile_hide_hd_small),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_HD_SMALL
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_roam),
                    title = stringResource(R.string.icon_tuner_mobile_hide_roam),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_ROAM
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_roam_small),
                    title = stringResource(R.string.icon_tuner_mobile_hide_roam_small),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_ROAM_SMALL
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volte),
                    title = stringResource(R.string.icon_tuner_mobile_hide_volte),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_VOLTE
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_vowifi),
                    title = stringResource(R.string.icon_tuner_mobile_hide_vowifi),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_VOWIFI
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_wifi)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi),
                    title = stringResource(R.string.icon_tuner_wifi_wifi),
                    summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.WIFI
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi_activity),
                    title = stringResource(R.string.icon_tuner_wifi_hide_wifi_activity),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_WIFI_ACTIVITY
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi_standard),
                    title = stringResource(R.string.icon_tuner_wifi_hide_wifi_type),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_WIFI_STANDARD
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_hotspot),
                    title = stringResource(R.string.icon_tuner_wifi_hotspot),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.HOTSPOT
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_connectivity)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_airplane),
                    title = stringResource(R.string.icon_tuner_connect_flight_mode),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.FLIGHT_MODE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_location),
                    title = stringResource(R.string.icon_tuner_connect_gps),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.GPS
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_bluetooth),
                    title = stringResource(R.string.icon_tuner_connect_bluetooth),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.BLUETOOTH
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_bluetooth_handsfree_battery),
                    title = stringResource(R.string.icon_tuner_connect_bluetooth_battery),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.BLUETOOTH_BATTERY
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_nfc),
                    title = stringResource(R.string.icon_tuner_connect_nfc),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.NFC
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_vpn),
                    title = stringResource(R.string.icon_tuner_connect_vpn),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.VPN
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_network_speed),
                    title = stringResource(R.string.icon_tuner_connect_net_speed),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.NET_SPEED
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_device)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_car),
                    title = stringResource(R.string.icon_tuner_device_car),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.CAR
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_pad),
                    title = stringResource(R.string.icon_tuner_device_pad),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.PAD
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_pc),
                    title = stringResource(R.string.icon_tuner_device_pc),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.PC
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_phone),
                    title = stringResource(R.string.icon_tuner_device_phone),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.PHONE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box),
                    title = stringResource(R.string.icon_tuner_device_sound_box),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.SOUND_BOX
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box_group),
                    title = stringResource(R.string.icon_tuner_device_sound_box_group),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.SOUND_BOX_GROUP
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box_screen),
                    title = stringResource(R.string.icon_tuner_device_sound_box_screen),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.SOUND_BOX_SCREEN
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_stereo),
                    title = stringResource(R.string.icon_tuner_device_stereo),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.STEREO
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_tv),
                    title = stringResource(R.string.icon_tuner_device_tv),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.TV
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wireless_headset),
                    title = stringResource(R.string.icon_tuner_device_wireless_headset),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.WIRELESS_HEADSET
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_other)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_alarm_clock),
                    title = stringResource(R.string.icon_tuner_other_alarm),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.ALARM
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_headset),
                    title = stringResource(R.string.icon_tuner_other_headset),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.HEADSET
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volume),
                    title = stringResource(R.string.icon_tuner_other_volume),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.VOLUME
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_zen),
                    title = stringResource(R.string.icon_tuner_other_zen),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.ZEN
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_battery)
            ) {
                DropDownPreference(
                    title = stringResource(R.string.icon_tuner_battery_style),
                    entries = dropdownEntriesBatteryStyle,
                    key = Pref.Key.SystemUI.IconTurner.BATTERY_STYLE,
                    mode = DropDownMode.Dialog
                ) {
                    spValueBatteryStyle = it
                }
                AnimatedVisibility(
                    spValueBatteryStyle in listOf(0, 1, 3)
                ) {
                    Column {
                        DropDownPreference(
                            title = stringResource(R.string.icon_tuner_battery_percentage_symbol_style),
                            entries = dropdownEntriesBatteryPercentage,
                            key = Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE,
                            mode = DropDownMode.Dialog
                        )
                        SwitchPreference(
                            title = stringResource(R.string.icon_tuner_battery_battery_percent_size),
                            key = Pref.Key.SystemUI.IconTurner.BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE
                        ) {
                            spValueModifyBatteryPercentageSize = it
                        }
                        AnimatedVisibility(
                            spValueModifyBatteryPercentageSize
                        ) {
                            EditTextPreference(
                                title = stringResource(R.string.icon_tuner_battery_percent_size),
                                key = Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_TEXT_SIZE,
                                defValue = 13.454498f,
                                dataType = EditTextDataType.FLOAT,
                                isValueValid = {
                                    (it as? Float ?: -1.0f) >= 0.0f
                                }
                            )
                        }

                    }
                }
                AnimatedVisibility(
                    spValueBatteryStyle in listOf(0, 1)
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.icon_tuner_battery_swap_battery_percent),
                        key = Pref.Key.SystemUI.IconTurner.SWAP_BATTERY_PERCENT
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.icon_tuner_battery_hide_charge),
                    key = Pref.Key.SystemUI.IconTurner.HIDE_CHARGE
                )
                SwitchPreference(
                    title = stringResource(R.string.icon_tuner_battery_layout_custom),
                    key = Pref.Key.SystemUI.IconTurner.BATTERY_MODIFY_PADDING
                ) {
                    spValueModifyBatteryPadding = it
                }
                AnimatedVisibility(
                    spValueModifyBatteryPadding
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.icon_tuner_battery_padding_left),
                            key = Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_LEFT,
                            defValue = 0.0f,
                            dataType = EditTextDataType.FLOAT
                        )
                        EditTextPreference(
                            title = stringResource(R.string.icon_tuner_battery_padding_right),
                            key = Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_RIGHT,
                            defValue = 0.0f,
                            dataType = EditTextDataType.FLOAT
                        )
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_other),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.icon_tuner_other_swap_mobile_wifi),
                    key = Pref.Key.SystemUI.IconTurner.SWAP_MOBILE_WIFI
                )
            }
        }
    }
}