package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.component.Hint
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun IconTunerPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val dropdownEntriesAdvVisible = listOf(
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_default)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_all)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_statusbar)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_qs)),
        DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_hidden)),
    )
    val dropDownEntriesIconPosition = listOf(
        DropDownEntry(stringResource(R.string.icon_tuner_general_order_default)),
        DropDownEntry(stringResource(R.string.icon_tuner_general_order_swap)),
        DropDownEntry(stringResource(R.string.icon_tuner_general_order_custom)),
    )

    var visibilityIconOrder by remember { mutableStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.ICON_POSITION, 0) == 2
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_icon_tuner),
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
            SmallTitle(
                text = stringResource(R.string.ui_title_icon_tuner_general),
                modifier = Modifier.padding(top = 6.dp),
            )
            Hint(
                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 6.dp),
                text = stringResource(R.string.icon_tuner_hint_ignore_sys_hide)
            )
            PreferenceGroup {
                SwitchPreference(
                    title = stringResource(R.string.icon_tuner_general_ignore_sys_hide),
                    summary = stringResource(R.string.icon_tuner_general_ignore_sys_hide_tips),
                    key = Pref.Key.SystemUI.IconTuner.IGNORE_SYS_SETTINGS
                )
                TextPreference(
                    title = stringResource(R.string.icon_tuner_general_detail)
                ) {
                    navController.navigateTo(Pages.ICON_DETAIL)
                }
                DropDownPreference(
                    title = stringResource(R.string.icon_tuner_general_order),
                    summary = stringResource(R.string.icon_tuner_general_order_tips),
                    entries = dropDownEntriesIconPosition,
                    key = Pref.Key.SystemUI.IconTuner.ICON_POSITION
                ) {
                    visibilityIconOrder = it == 2
                }
                AnimatedVisibility(
                    visibilityIconOrder
                ) {
                    TextPreference(
                        title = stringResource(R.string.icon_tuner_general_order_custom_entry)
                    ) {
                        navController.navigateTo(Pages.DIALOG_STATUS_BAR_ICON_POSITION)
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_network)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_mobile),
                    title = stringResource(R.string.icon_tuner_network_mobile),
                    summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.MOBILE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_no_sim),
                    title = stringResource(R.string.icon_tuner_network_no_sim),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.NO_SIM
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_airplane),
                    title = stringResource(R.string.icon_tuner_network_airplane),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.AIRPLANE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi),
                    title = stringResource(R.string.icon_tuner_network_wifi),
                    summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.WIFI
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_hotspot),
                    title = stringResource(R.string.icon_tuner_network_hotspot),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.HOTSPOT
                )
                DropDownPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_vpn),
                title = stringResource(R.string.icon_tuner_network_vpn),
                entries = dropdownEntriesAdvVisible,
                key = Pref.Key.SystemUI.IconTuner.VPN
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_net_speed),
                    title = stringResource(R.string.icon_tuner_network_net_speed),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.NET_SPEED
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_connectivity)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_bluetooth),
                    title = stringResource(R.string.icon_tuner_connect_bluetooth),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.BLUETOOTH
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_bluetooth_handsfree_battery),
                    title = stringResource(R.string.icon_tuner_connect_bluetooth_battery),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.BLUETOOTH_BATTERY
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_handle_battery),
                    title = stringResource(R.string.icon_tuner_connect_handle_battery),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.HANDLE_BATTERY
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_nfc),
                    title = stringResource(R.string.icon_tuner_connect_nfc),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.NFC
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_location),
                    title = stringResource(R.string.icon_tuner_connect_location),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.LOCATION
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_device)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wireless_headset),
                    title = stringResource(R.string.icon_tuner_device_wireless_headset),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.WIRELESS_HEADSET
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_phone),
                    title = stringResource(R.string.icon_tuner_device_phone),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.PHONE
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_pad),
                    title = stringResource(R.string.icon_tuner_device_pad),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.PAD
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_pc),
                    title = stringResource(R.string.icon_tuner_device_pc),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.PC
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box_group),
                    title = stringResource(R.string.icon_tuner_device_sound_box_group),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.SOUND_BOX_GROUP
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_stereo),
                    title = stringResource(R.string.icon_tuner_device_stereo),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.STEREO
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box_screen),
                    title = stringResource(R.string.icon_tuner_device_sound_box_screen),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.SOUND_BOX_SCREEN
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_sound_box),
                    title = stringResource(R.string.icon_tuner_device_sound_box),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.SOUND_BOX
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_tv),
                    title = stringResource(R.string.icon_tuner_device_tv),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.TV
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_glasses),
                    title = stringResource(R.string.icon_tuner_device_glasses),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.GLASSES
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_car),
                    title = stringResource(R.string.icon_tuner_device_car),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.CAR
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_camera),
                    title = stringResource(R.string.icon_tuner_device_camera),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.CAMERA
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_dist_compute),
                    title = stringResource(R.string.icon_tuner_device_dist_compute),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.DIST_COMPUTE
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_icon_tuner_other)
            ) {
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_headset),
                    title = stringResource(R.string.icon_tuner_other_headset),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.HEADSET
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_alarm_clock),
                    title = stringResource(R.string.icon_tuner_other_alarm),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.ALARM_CLOCK
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_zen),
                    title = stringResource(R.string.icon_tuner_other_zen),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.ZEN
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volume),
                    title = stringResource(R.string.icon_tuner_other_volume),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.VOLUME
                )
                DropDownPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_second_space),
                    title = stringResource(R.string.icon_tuner_other_second_space),
                    entries = dropdownEntriesAdvVisible,
                    key = Pref.Key.SystemUI.IconTuner.SECOND_SPACE
                )
                SwitchPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_privacy_notice),
                    title = stringResource(R.string.icon_tuner_other_hide_privacy),
                    key = Pref.Key.SystemUI.IconTuner.HIDE_PRIVACY
                )
            }
        }
    }
}