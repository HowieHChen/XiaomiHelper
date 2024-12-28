package dev.lackluster.mihelper.activity.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref

@Composable
fun IconTurnerPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val dropdownEntriesAdvVisible = listOf(
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_default)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_all)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_statusbar)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_qs)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_hidden)),
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
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_wifi)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi),
                    title = stringResource(R.string.icon_tuner_wifi_wifi),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTurner.WIFI
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
    }
}