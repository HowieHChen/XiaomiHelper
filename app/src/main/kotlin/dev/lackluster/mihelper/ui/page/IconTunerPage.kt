package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.IconSlots
import dev.lackluster.mihelper.data.Constants.COMPOUND_ICON_PRIORITY_STR
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.ui.component.itemAnimated
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup

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

    var hintCloseAdvancedTextures by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.Hints.ICON_TUNER_GENERAL, false)
    ) }
    var visibilityIconOrder by remember { mutableStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.ICON_POSITION, 0) == 2
    ) }
    var visibilityCompoundIcon by remember { mutableStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.COMPOUND_ICON, 0) in 1..3
    ) }
    var visibilityLeftIcon by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.LEFT_CONTAINER, false)
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
        itemAnimated(
            key = "ICON_TUNER_GENERAL_HINT",
            visible = !hintCloseAdvancedTextures
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                text = stringResource(R.string.icon_tuner_hint_ignore_sys_hide),
                closeable = true
            ) {
                hintCloseAdvancedTextures = true
                SafeSP.putAny(Pref.Key.Hints.ICON_TUNER_GENERAL, true)
            }
        }
        itemPreferenceGroup(
            key = "ICON_TUNER_GENERAL",
            titleResId = R.string.ui_title_icon_tuner_general,
            first = hintCloseAdvancedTextures
        ) {
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
        itemPreferenceGroup(
            key = "ICON_TUNER_NETWORK",
            titleResId = R.string.ui_title_icon_tuner_network
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
        itemPreferenceGroup(
            key = "ICON_TUNER_CONNECTIVITY",
            titleResId = R.string.ui_title_icon_tuner_connectivity
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
                icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_headset),
                title = stringResource(R.string.icon_tuner_connect_headset),
                entries = dropdownEntriesAdvVisible,
                key = Pref.Key.SystemUI.IconTuner.HEADSET
            )
            DropDownPreference(
                icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_location),
                title = stringResource(R.string.icon_tuner_connect_location),
                entries = dropdownEntriesAdvVisible,
                key = Pref.Key.SystemUI.IconTuner.LOCATION
            )
        }
        itemPreferenceGroup(
            key = "ICON_TUNER_DEVICE",
            titleResId = R.string.ui_title_icon_tuner_device
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
        itemPreferenceGroup(
            key = "ICON_TUNER_OTHER",
            titleResId = R.string.ui_title_icon_tuner_other
        ) {
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
        itemPreferenceGroup(
            key = "ICON_TUNER_COMPOUND_ICON",
            titleResId = R.string.ui_title_icon_tuner_compound_icon
        ) {
            DropDownPreference(
                icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_compound),
                title = stringResource(R.string.icon_tuner_compound_icon),
                summary = stringResource(R.string.icon_tuner_compound_icon_tips),
                entries = dropdownEntriesAdvVisible,
                key = Pref.Key.SystemUI.IconTuner.COMPOUND_ICON
            ) {
                visibilityCompoundIcon = it in 1..3
            }
            AnimatedVisibility(
                visibilityCompoundIcon
            ) {
                Column {
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_location),
                        title = stringResource(R.string.icon_tuner_connect_location),
                        key = Pref.Key.SystemUI.IconTuner.COMPOUND_ICON_LOCATION
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_alarm_clock),
                        title = stringResource(R.string.icon_tuner_other_alarm),
                        key = Pref.Key.SystemUI.IconTuner.COMPOUND_ICON_ALARM
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_zen),
                        title = stringResource(R.string.icon_tuner_other_zen),
                        key = Pref.Key.SystemUI.IconTuner.COMPOUND_ICON_ZEN
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volume),
                        title = stringResource(R.string.icon_tuner_other_volume),
                        key = Pref.Key.SystemUI.IconTuner.COMPOUND_ICON_VOLUME
                    )
                    EditTextPreference(
                        title = stringResource(R.string.icon_tuner_compound_priority),
                        key = Pref.Key.SystemUI.IconTuner.COMPOUND_PRIORITY,
                        defValue = COMPOUND_ICON_PRIORITY_STR,
                        dataType = EditTextDataType.STRING,
                        dialogMessage = stringResource(R.string.icon_tuner_compound_priority_tips),
                        isValueValid = { newValue ->
                            newValue.toString().split(',', ' ', '，').let {
                                it.size == 4 &&
                                        it.contains(IconSlots.LOCATION) &&
                                        it.contains(IconSlots.ALARM_CLOCK) &&
                                        it.contains(IconSlots.ZEN) &&
                                        it.contains(IconSlots.VOLUME)
                            }
                        },
                        valuePosition = ValuePosition.SUMMARY_VIEW,
                    )
                }
            }
        }
        itemPreferenceGroup(
            key = "ICON_TUNER_LEFT_CONTAINER",
            titleResId = R.string.ui_title_icon_tuner_left_icon,
            last = !visibilityLeftIcon
        ) {
            SwitchPreference(
                title = stringResource(R.string.icon_tuner_left_icon),
                summary = stringResource(R.string.icon_tuner_left_icon_tips),
                key = Pref.Key.SystemUI.IconTuner.LEFT_CONTAINER
            ) {
                visibilityLeftIcon = it
            }
            AnimatedVisibility(visibilityLeftIcon) {
                Column {
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_compound),
                        title = stringResource(R.string.icon_tuner_compound_icon),
                        key = Pref.Key.SystemUI.IconTuner.LEFT_COMPOUND_ICON
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_location),
                        title = stringResource(R.string.icon_tuner_connect_location),
                        key = Pref.Key.SystemUI.IconTuner.LEFT_LOCATION
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_alarm_clock),
                        title = stringResource(R.string.icon_tuner_other_alarm),
                        key = Pref.Key.SystemUI.IconTuner.LEFT_ALARM_CLOCK
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_zen),
                        title = stringResource(R.string.icon_tuner_other_zen),
                        key = Pref.Key.SystemUI.IconTuner.LEFT_ZEN
                    )
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volume),
                        title = stringResource(R.string.icon_tuner_other_volume),
                        key = Pref.Key.SystemUI.IconTuner.LEFT_VOLUME
                    )
                }
            }
        }
        itemAnimated(
            key = "ICON_TUNER_LEFT_CONTAINER_HINT",
            visible = visibilityLeftIcon
        ) {
            Hint(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp, bottom = 12.dp),
                text = stringResource(R.string.icon_tuner_hint_left_icon_order)
            )
        }
    }
}