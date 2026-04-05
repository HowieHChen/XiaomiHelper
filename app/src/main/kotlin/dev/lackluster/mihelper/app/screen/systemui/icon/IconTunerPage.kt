package dev.lackluster.mihelper.app.screen.systemui.icon

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.component.Hint
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemAnimated
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.state.UiEvent
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences

private val visibilityOptions = listOf(
    DropDownOption(0, R.string.icon_tuner_hide_selection_default),
    DropDownOption(1, R.string.icon_tuner_hide_selection_show_all),
    DropDownOption(2, R.string.icon_tuner_hide_selection_show_statusbar),
    DropDownOption(3, R.string.icon_tuner_hide_selection_show_qs),
    DropDownOption(4, R.string.icon_tuner_hide_selection_hidden),
)

private val iconPositionOptions = listOf(
    DropDownOption(0, R.string.icon_tuner_general_order_default),
    DropDownOption(1, R.string.icon_tuner_general_order_swap),
    DropDownOption(2, R.string.icon_tuner_general_order_custom),
)

private val leftContainerOptions = listOf(
    DropDownOption(0, R.string.icon_tuner_left_icon_disabled),
    DropDownOption(1, R.string.icon_tuner_left_icon_unlocked),
    DropDownOption(2, R.string.icon_tuner_left_icon_enabled),
)

private val requiredCompoundIcons = setOf(
    Constants.IconSlots.LOCATION,
    Constants.IconSlots.ALARM_CLOCK,
    Constants.IconSlots.ZEN,
    Constants.IconSlots.VOLUME
)

@Composable
fun IconTunerPage() {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val onAction: (UiEvent) -> Unit = { action ->
        when(action) {
            is UiEvent.NavigateTo -> navigator.push(action.route)
            is UiEvent.ShowToast -> context.showToast(action.message.asString(context), action.long)
            else -> {}
        }
    }

    IconTunerPageContent(
        onAction = onAction
    )
}

@Composable
private fun IconTunerPageContent(
    onAction: (UiEvent) -> Unit
) {
    val showHint = rememberPreferenceState(Preferences.HintState.ICON_TUNER_IGNORE_SYS)

    HyperXPage(
        title = stringResource(R.string.page_status_bar_icon_tuner),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI),
            )
        }
    ) {
        itemAnimated(
            key = "ICON_TUNER_HINT",
            visible = showHint.value
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                text = stringResource(R.string.icon_tuner_hint_ignore_sys_hide),
                closeable = true,
                onClose = { showHint.value = false }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_icon_tuner_general,
            position = if (showHint.value) ItemPosition.Middle else ItemPosition.First
        ) {
            val iconPosition = rememberPreferenceState(Preferences.SystemUI.StatusBar.IconTuner.ICON_POSITION)
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.IGNORE_SYS_SETTINGS,
                title = stringResource(R.string.icon_tuner_general_ignore_sys_hide),
                summary = stringResource(R.string.icon_tuner_general_ignore_sys_hide_tips)
            )
            DropDownPreference(
                title = stringResource(R.string.icon_tuner_general_order),
                summary = stringResource(R.string.icon_tuner_general_order_tips),
                options = iconPositionOptions,
                value = iconPosition.value,
                onValueChange = { iconPosition.value = it }
            )
            AnimatedVisibility(iconPosition.value == 2) {
                TextPreference(
                    title = stringResource(R.string.icon_tuner_general_order_custom_entry),
                    onClick = { onAction(UiEvent.NavigateTo(Route.StatusBarIconPosition)) }
                )
            }
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.ICON_POSITION_REORDER,
                title = stringResource(R.string.icon_tuner_general_auto_reorder),
                summary = stringResource(R.string.icon_tuner_general_auto_reorder_tips)
            )
            TextPreference(
                title = stringResource(R.string.icon_tuner_general_detail),
                onClick = { onAction(UiEvent.NavigateTo(Route.IconDetail)) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_icon_tuner_network,
        ) {
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.MOBILE,
                icon = ImageIcon(R.drawable.ic_stat_sys_mobile),
                title = stringResource(R.string.icon_tuner_network_mobile),
                summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                options = visibilityOptions,
                enabled = false,
                onValueChange = {
                    if (it > 1) onAction(UiEvent.ShowToast(R.string.icon_tuner_network_warning.toUiText()))
                }
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.NO_SIM,
                icon = ImageIcon(R.drawable.ic_stat_sys_no_sim),
                title = stringResource(R.string.icon_tuner_network_no_sim),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.AIRPLANE,
                icon = ImageIcon(R.drawable.ic_stat_sys_airplane),
                title = stringResource(R.string.icon_tuner_network_airplane),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.WIFI,
                icon = ImageIcon(R.drawable.ic_stat_sys_wifi),
                title = stringResource(R.string.icon_tuner_network_wifi),
                summary = stringResource(R.string.icon_tuner_hide_mobile_wifi_warning),
                options = visibilityOptions,
                onValueChange = {
                    if (it > 1) onAction(UiEvent.ShowToast(R.string.icon_tuner_network_warning.toUiText()))
                }
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.HOTSPOT,
                icon = ImageIcon(R.drawable.ic_stat_sys_hotspot),
                title = stringResource(R.string.icon_tuner_network_hotspot),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.VPN,
                icon = ImageIcon(R.drawable.ic_stat_sys_vpn),
                title = stringResource(R.string.icon_tuner_network_vpn),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.NET_SPEED,
                icon = ImageIcon(R.drawable.ic_stat_sys_net_speed),
                title = stringResource(R.string.icon_tuner_network_net_speed),
                options = visibilityOptions
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_icon_tuner_connectivity,
        ) {
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.BLUETOOTH,
                icon = ImageIcon(R.drawable.ic_stat_sys_bluetooth),
                title = stringResource(R.string.icon_tuner_connect_bluetooth),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.BLUETOOTH_BATTERY,
                icon = ImageIcon(R.drawable.ic_stat_sys_bluetooth_handsfree_battery),
                title = stringResource(R.string.icon_tuner_connect_bluetooth_battery),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.HANDLE_BATTERY,
                icon = ImageIcon(R.drawable.ic_stat_sys_handle_battery),
                title = stringResource(R.string.icon_tuner_connect_handle_battery),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.NFC,
                icon = ImageIcon(R.drawable.ic_stat_sys_nfc),
                title = stringResource(R.string.icon_tuner_connect_nfc),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.HEADSET,
                icon = ImageIcon(R.drawable.ic_stat_sys_headset),
                title = stringResource(R.string.icon_tuner_connect_headset),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.LOCATION,
                icon = ImageIcon(R.drawable.ic_stat_sys_location),
                title = stringResource(R.string.icon_tuner_connect_location),
                options = visibilityOptions
            )
        }
        itemPreferenceGroup(
            key = "ICON_TUNER_DEVICE",
            titleRes = R.string.ui_title_icon_tuner_device,
            position = ItemPosition.Middle
        ) {
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.WIRELESS_HEADSET,
                icon = ImageIcon(R.drawable.ic_stat_sys_wireless_headset),
                title = stringResource(R.string.icon_tuner_device_wireless_headset),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.PHONE,
                icon = ImageIcon(R.drawable.ic_stat_sys_phone),
                title = stringResource(R.string.icon_tuner_device_phone),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.PAD,
                icon = ImageIcon(R.drawable.ic_stat_sys_pad),
                title = stringResource(R.string.icon_tuner_device_pad),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.PC,
                icon = ImageIcon(R.drawable.ic_stat_sys_pc),
                title = stringResource(R.string.icon_tuner_device_pc),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.SOUND_BOX_GROUP,
                icon = ImageIcon(R.drawable.ic_stat_sys_sound_box_group),
                title = stringResource(R.string.icon_tuner_device_sound_box_group),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.STEREO,
                icon = ImageIcon(R.drawable.ic_stat_sys_stereo),
                title = stringResource(R.string.icon_tuner_device_stereo),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.SOUND_BOX_SCREEN,
                icon = ImageIcon(R.drawable.ic_stat_sys_sound_box_screen),
                title = stringResource(R.string.icon_tuner_device_sound_box_screen),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.SOUND_BOX,
                icon = ImageIcon(R.drawable.ic_stat_sys_sound_box),
                title = stringResource(R.string.icon_tuner_device_sound_box),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.TV,
                icon = ImageIcon(R.drawable.ic_stat_sys_tv),
                title = stringResource(R.string.icon_tuner_device_tv),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.GLASSES,
                icon = ImageIcon(R.drawable.ic_stat_sys_glasses),
                title = stringResource(R.string.icon_tuner_device_glasses),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.CAR,
                icon = ImageIcon(R.drawable.ic_stat_sys_car),
                title = stringResource(R.string.icon_tuner_device_car),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.CAMERA,
                icon = ImageIcon(R.drawable.ic_stat_sys_camera),
                title = stringResource(R.string.icon_tuner_device_camera),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.DIST_COMPUTE,
                icon = ImageIcon(R.drawable.ic_stat_sys_dist_compute),
                title = stringResource(R.string.icon_tuner_device_dist_compute),
                options = visibilityOptions
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_icon_tuner_other,
        ) {
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.ALARM_CLOCK,
                icon = ImageIcon(R.drawable.ic_stat_sys_alarm_clock),
                title = stringResource(R.string.icon_tuner_other_alarm),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.ZEN,
                icon = ImageIcon(R.drawable.ic_stat_sys_zen),
                title = stringResource(R.string.icon_tuner_other_zen),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.VOLUME,
                icon = ImageIcon(R.drawable.ic_stat_sys_volume),
                title = stringResource(R.string.icon_tuner_other_volume),
                options = visibilityOptions
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.SECOND_SPACE,
                icon = ImageIcon(R.drawable.ic_stat_sys_second_space),
                title = stringResource(R.string.icon_tuner_other_second_space),
                options = visibilityOptions
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconTuner.HIDE_PRIVACY,
                icon = ImageIcon(R.drawable.ic_stat_sys_privacy_notice),
                title = stringResource(R.string.icon_tuner_other_hide_privacy),
            )
        }
        itemPreferenceGroup(
            key = "ICON_TUNER_COMPOUND_ICON",
            titleRes = R.string.ui_title_icon_tuner_compound_icon,
            position = ItemPosition.Middle
        ) {
            val compoundIcon = rememberPreferenceState(Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON)
            DropDownPreference(
                icon = ImageIcon(R.drawable.ic_stat_sys_compound),
                title = stringResource(R.string.icon_tuner_compound_icon),
                summary = stringResource(R.string.icon_tuner_compound_icon_tips),
                options = visibilityOptions,
                value = compoundIcon.value,
                onValueChange = { compoundIcon.value = it }
            )
            AnimatedColumn(compoundIcon.value in 1..3) {
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_LOCATION,
                    icon = ImageIcon(R.drawable.ic_stat_sys_location),
                    title = stringResource(R.string.icon_tuner_connect_location),
                    summary = Constants.IconSlots.LOCATION,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_ALARM,
                    icon = ImageIcon(R.drawable.ic_stat_sys_alarm_clock),
                    title = stringResource(R.string.icon_tuner_other_alarm),
                    summary = Constants.IconSlots.ALARM_CLOCK,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_ZEN,
                    icon = ImageIcon(R.drawable.ic_stat_sys_zen),
                    title = stringResource(R.string.icon_tuner_other_zen),
                    summary = Constants.IconSlots.ZEN,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_VOLUME,
                    icon = ImageIcon(R.drawable.ic_stat_sys_volume),
                    title = stringResource(R.string.icon_tuner_other_volume),
                    summary = Constants.IconSlots.VOLUME,
                )
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_PRIORITY,
                    title = stringResource(R.string.icon_tuner_compound_priority),
                    dialogMessage = stringResource(R.string.icon_tuner_compound_priority_tips),
                    valuePosition = ValuePosition.Summary,
                    isValueValid = { newValue ->
                        val list = newValue.split(',', ' ', '，').filter { it.isNotBlank() }
                        list.size == 4 && list.containsAll(requiredCompoundIcons)
                    }
                )
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_icon_tuner_left_icon,
            position = ItemPosition.Last
        ) {
            val leftContainer = rememberPreferenceState(Preferences.SystemUI.StatusBar.IconTuner.LEFT_CONTAINER)
            DropDownPreference(
                title = stringResource(R.string.icon_tuner_left_icon),
                summary = stringResource(R.string.icon_tuner_left_icon_tips),
                options = leftContainerOptions,
                value = leftContainer.value,
                onValueChange = { leftContainer.value = it }
            )
            AnimatedColumn(leftContainer.value != 0) {
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_EXT_BLOCK_LIST,
                    title = stringResource(R.string.icon_tuner_left_extra_blocked_slots),
                    summary = stringResource(R.string.icon_tuner_left_extra_blocked_slots_tips),
                    dialogMessage = stringResource(R.string.icon_tuner_left_extra_blocked_slots_msg),
                    valuePosition = ValuePosition.Summary
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_COMPOUND_ICON,
                    icon = ImageIcon(R.drawable.ic_stat_sys_compound),
                    title = stringResource(R.string.icon_tuner_compound_icon),
                    summary = Constants.COMPOUND_ICON_REAL_SLOTS.joinToString(","),
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_LOCATION,
                    icon = ImageIcon(R.drawable.ic_stat_sys_location),
                    title = stringResource(R.string.icon_tuner_connect_location),
                    summary = Constants.IconSlots.LOCATION,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_ALARM_CLOCK,
                    icon = ImageIcon(R.drawable.ic_stat_sys_alarm_clock),
                    title = stringResource(R.string.icon_tuner_other_alarm),
                    summary = Constants.IconSlots.ALARM_CLOCK,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_ZEN,
                    icon = ImageIcon(R.drawable.ic_stat_sys_zen),
                    title = stringResource(R.string.icon_tuner_other_zen),
                    summary = Constants.IconSlots.ZEN,
                )
                SwitchPreference(
                    key = Preferences.SystemUI.StatusBar.IconTuner.LEFT_VOLUME,
                    icon = ImageIcon(R.drawable.ic_stat_sys_volume),
                    title = stringResource(R.string.icon_tuner_other_volume),
                    summary = Constants.IconSlots.VOLUME,
                )
            }
        }
    }
}