package dev.lackluster.mihelper.app.screen.systemui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences

private sealed interface SystemUIAction {
    data class NavigateTo(val route: Route) : SystemUIAction
    object OpenNotifLayoutOptSheet : SystemUIAction
}

private val autoExpandNotifOptions = listOf(
    DropDownOption(0, R.string.systemui_notif_expand_notif_def, summaryRes = R.string.systemui_notif_expand_notif_def_tips),
    DropDownOption(1, R.string.systemui_notif_expand_notif_first, summaryRes = R.string.systemui_notif_expand_notif_first_tips),
    DropDownOption(2, R.string.systemui_notif_expand_notif_ungrouped, summaryRes = R.string.systemui_notif_expand_notif_ungrouped_tips),
)

private val regionSamplingOptions = listOf(
    DropDownOption(0, R.string.systemui_statusbar_region_sampling_def, summaryRes = R.string.systemui_statusbar_region_sampling_def_tips),
    DropDownOption(1, R.string.systemui_statusbar_region_sampling_enable, summaryRes = R.string.systemui_statusbar_region_sampling_enable_tips),
    DropDownOption(2, R.string.systemui_statusbar_region_sampling_disable, summaryRes = R.string.systemui_statusbar_region_sampling_disable_tips),
)

private val forceColorSchemeOptions = listOf(
    DropDownOption(0, R.string.weather_card_color_default),
    DropDownOption(1, R.string.weather_card_color_light),
    DropDownOption(2, R.string.weather_card_color_dark),
)

@Composable
fun SystemUIPage() {
    val navigator = LocalNavigator.current
    val appSettingsActions = LocalPreferenceActions.current

    val notifLayoutOptSheetVisibility = remember { mutableStateOf(false) }

    val isNotifLayoutOptOn = remember {
        mutableStateOf(appSettingsActions.get(Preferences.SystemUI.NotifCenter.ENABLE_LAYOUT_RANK_OPT))
    }

    val onAction: (SystemUIAction) -> Unit = { action ->
        when (action) {
            is SystemUIAction.NavigateTo -> navigator.push(action.route)
            SystemUIAction.OpenNotifLayoutOptSheet -> notifLayoutOptSheetVisibility.value = true
        }
    }

    SystemUIPageContent(
        isNotifLayoutOptOn = isNotifLayoutOptOn.value,
        onAction = onAction
    )

    NotifLayoutOptSheet(
        show = notifLayoutOptSheetVisibility.value,
        onDismissRequest = {
            notifLayoutOptSheetVisibility.value = false
            isNotifLayoutOptOn.value = appSettingsActions.get(Preferences.SystemUI.NotifCenter.ENABLE_LAYOUT_RANK_OPT)
        }
    )
}

@Composable
private fun SystemUIPageContent(
    isNotifLayoutOptOn: Boolean,
    onAction: (SystemUIAction) -> Unit
) {
    HyperXPage(
        title = stringResource(R.string.page_systemui),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI, Scope.SYSTEM_UI_PLUGIN),
            )
        }
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_statusbar,
            position = ItemPosition.First
        ) {
            TextPreference(
                title = stringResource(R.string.systemui_statusbar_font),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.StatusBarFont)) },
            )
            TextPreference(
                title = stringResource(R.string.systemui_statusbar_clock),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.StatusBarClock)) }
            )
            TextPreference(
                title = stringResource(R.string.systemui_statusbar_icon_tuner),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.IconTuner)) }
            )
            TextPreference(
                title = stringResource(R.string.systemui_statusbar_icon_detail),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.IconDetail)) }
            )
            val enableNotifMaxCount = rememberPreferenceState(Preferences.SystemUI.StatusBar.ENABLE_NOTIF_MAX_COUNT)
            SwitchPreference(
                title = stringResource(R.string.systemui_statusbar_notif_count),
                checked = enableNotifMaxCount.value,
                onCheckedChange = { enableNotifMaxCount.value = it }
            )
            AnimatedVisibility(enableNotifMaxCount.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.NOTIF_MAX_COUNT,
                    title = stringResource(R.string.systemui_statusbar_notif_count_icon),
                    min = 0,
                    max = 15
                )
            }
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.DOUBLE_TAP_TO_SLEEP,
                title = stringResource(R.string.systemui_statusbar_tap_to_sleep),
            )
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.REGION_SAMPLING,
                title = stringResource(R.string.systemui_statusbar_region_sampling),
                summary = stringResource(R.string.systemui_statusbar_region_sampling_tips),
                options = regionSamplingOptions,
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_lock_screen,
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.HIDE_DISTURB_NOTIF,
                title = stringResource(R.string.systemui_lock_hide_disturb),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.KEEP_NOTIFICATION,
                title = stringResource(R.string.systemui_lock_keep_notif),
                summary = stringResource(R.string.systemui_lock_keep_notif_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP,
                title = stringResource(R.string.systemui_lock_double_tap),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.KEEP_START_CONTAINER,
                title = stringResource(R.string.systemui_lock_keep_clock_container),
                summary = stringResource(R.string.systemui_lock_keep_clock_container_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.HIDE_NEXT_ALARM,
                title = stringResource(R.string.systemui_lock_hide_next_alarm),
                summary = stringResource(R.string.systemui_lock_hide_next_alarm_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.HIDE_CARRIER_ONE,
                title = stringResource(R.string.systemui_lock_hide_carrier_one),
            )
            SwitchPreference(
                key = Preferences.SystemUI.LockScreen.HIDE_CARRIER_TWO,
                title = stringResource(R.string.systemui_lock_hide_carrier_two),
            )
            DropDownPreference(
                key = Preferences.SystemUI.LockScreen.FORCE_COLOR_STATUS_BAR,
                title = stringResource(R.string.systemui_lock_force_color_scheme_statusbar),
                options = forceColorSchemeOptions,
            )
            SwitchPreference(
                key = Preferences.SystemUI.Plugin.LOCKSCREEN_AUTO_FLASH_ON,
                title = stringResource(R.string.systemui_lock_flashlight_on),
                summary = stringResource(R.string.systemui_lock_flashlight_on_tips),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_notification_center,
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.ALWAYS_ALLOW_FREEFORM,
                title = stringResource(R.string.systemui_notif_freeform),
                summary = stringResource(R.string.systemui_notif_freeform_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.DISABLE_NOTIF_WHITELIST,
                title = stringResource(R.string.systemui_notif_disable_whitelist),
                summary = stringResource(R.string.systemui_notif_disable_whitelist_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.SUPPRESS_FOLD_NOTIF,
                title = stringResource(R.string.systemui_notif_suppress_fold),
                summary = stringResource(R.string.systemui_notif_suppress_fold_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.MIUIX_EXPAND_BUTTON,
                title = stringResource(R.string.systemui_notif_miuix_expand_btn),
                summary = stringResource(R.string.systemui_notif_miuix_expand_btn_tips),
            )
            val autoExpandNotif = rememberPreferenceState(Preferences.SystemUI.NotifCenter.AUTO_EXPAND_NOTIF)
            DropDownPreference(
                title = stringResource(R.string.systemui_notif_expand_notif),
                summary = stringResource(R.string.systemui_notif_expand_notif_tips),
                value = autoExpandNotif.value,
                options = autoExpandNotifOptions,
                onValueChange = { autoExpandNotif.value = it },
            )
            AnimatedVisibility(autoExpandNotif.value == 1) {
                SwitchPreference(
                    key = Preferences.SystemUI.NotifCenter.EXPAND_IGNORE_FOCUS,
                    title = stringResource(R.string.systemui_notif_expand_ignore_focus),
                    summary = stringResource(R.string.systemui_notif_expand_ignore_focus_tips),
                )
            }
            TextPreference(
                title = stringResource(R.string.systemui_notif_lr_opt),
                summary = stringResource(R.string.systemui_notif_lr_opt_tips),
                value = stringResource(if (isNotifLayoutOptOn) R.string.common_on else R.string.common_off),
                onClick = { onAction(SystemUIAction.OpenNotifLayoutOptSheet) },
            )
            TextPreference(
                title = stringResource(R.string.systemui_notif_media_control_style),
                summary = stringResource(R.string.systemui_notif_media_control_style_tips),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.NotifMediaControl)) },
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_dynamic_island,
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.Plugin.DISABLE_ISLAND_NOTIF_WHITELIST,
                title = stringResource(R.string.systemui_di_disable_whitelist),
                summary = stringResource(R.string.systemui_di_disable_whitelist_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.Plugin.DISABLE_ISLAND_MEDIA_WHITELIST,
                title = stringResource(R.string.systemui_di_disable_media_whitelist),
                summary = stringResource(R.string.systemui_di_disable_media_whitelist_tips),
            )
            TextPreference(
                title = stringResource(R.string.systemui_di_media_control_style),
                summary = stringResource(R.string.systemui_di_media_control_style_tips),
                onClick = { onAction(SystemUIAction.NavigateTo(Route.IslandMediaControl)) },
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_control_center,
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.ControlCenter.HIDE_CARRIER_ONE,
                title = stringResource(R.string.systemui_control_hide_carrier_one),
            )
            SwitchPreference(
                key = Preferences.SystemUI.ControlCenter.HIDE_CARRIER_TWO,
                title = stringResource(R.string.systemui_control_hide_carrier_two),
            )
            SwitchPreference(
                key = Preferences.SystemUI.ControlCenter.HIDE_CARRIER_HD,
                title = stringResource(R.string.systemui_control_hide_carrier_hd),
                summary = stringResource(R.string.systemui_control_hide_carrier_hd_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.Plugin.CONTROL_CENTER_HIDE_EDIT,
                title = stringResource(R.string.systemui_control_hide_edit),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_systemui_others,
            position = ItemPosition.Last
        ) {
            val enableMonetOverlay = rememberPreferenceState(Preferences.SystemUI.NotifCenter.ENABLE_MONET_OVERLAY)
            SwitchPreference(
                title = stringResource(R.string.systemui_others_monet_overlay),
                summary = stringResource(R.string.systemui_others_monet_overlay_tips),
                checked = enableMonetOverlay.value,
                onCheckedChange = { enableMonetOverlay.value = it },
            )
            AnimatedVisibility(enableMonetOverlay.value) {
                MonetColorPreferenceItem()
            }
        }
    }
}

@Composable
private fun MonetColorPreferenceItem() {
    val showDialog = remember { mutableStateOf(false) }
    val holdDown = remember { mutableStateOf(false) }

    val monetColorState = rememberPreferenceState(Preferences.SystemUI.NotifCenter.MONET_OVERLAY_COLOR)

    TextPreference(
        title = stringResource(R.string.systemui_others_monet_color),
        summary = stringResource(R.string.systemui_others_monet_color_tips),
        holdDownState = holdDown.value,
        value = monetColorState.value,
        onClick = {
            holdDown.value = true
            showDialog.value = true
        }
    )

    ColorPickerDialog(
        visible = showDialog.value,
        title = stringResource(R.string.systemui_others_monet_color),
        initialColor = Color(monetColorState.value.toColorInt()),
        onConfirm = { finalColor ->
            val finalHexString = String.format("#%08X", finalColor.toArgb())
            monetColorState.value = finalHexString
        },
        onDismissRequest = { showDialog.value = false },
        onDismissFinished = { holdDown.value = false },
        supportAlpha = false,
        message = stringResource(R.string.systemui_others_monet_color_tips),
    )
}