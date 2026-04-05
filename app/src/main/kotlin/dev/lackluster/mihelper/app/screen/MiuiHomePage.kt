package dev.lackluster.mihelper.app.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.Device
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val backHapticOptions = listOf(
    DropDownOption(0, R.string.home_gesture_back_haptic_default, R.string.home_gesture_back_haptic_default_tips),
    DropDownOption(1, R.string.home_gesture_back_haptic_enhanced, R.string.home_gesture_back_haptic_enhanced_tips),
    DropDownOption(2,R.string.home_gesture_back_haptic_disabled),
)

private val forceColorSchemeOptions = listOf(
    DropDownOption(0, R.string.weather_card_color_default),
    DropDownOption(1, R.string.weather_card_color_light),
    DropDownOption(2, R.string.weather_card_color_dark),
)

@Composable
fun MiuiHomePage() {
    val tintColor = MiuixTheme.colorScheme.onSurfaceSecondary

    val lineGestureActionOptions = remember(tintColor) {
        listOf(
            DropDownOption(0, R.string.common_disabled, iconRes = R.drawable.ic_quick_switch_empty,iconTint = tintColor),
            DropDownOption(1, R.string.action_notifications, iconRes = R.drawable.ic_quick_switch_notifications, iconTint = tintColor),
            DropDownOption(2, R.string.action_quick_settings, iconRes = R.drawable.ic_quick_switch_quick_settings, iconTint = tintColor),
            DropDownOption(3, R.string.action_lock_screen, iconRes = R.drawable.ic_quick_switch_lock_screen, iconTint = tintColor),
            DropDownOption(4, R.string.action_screenshot, iconRes = R.drawable.ic_quick_switch_screenshot, iconTint = tintColor),
            DropDownOption(5, R.string.action_home, iconRes = R.drawable.ic_quick_switch_home, iconTint = tintColor),
            DropDownOption(6, R.string.action_recents, iconRes = R.drawable.ic_quick_switch_recents, iconTint = tintColor),
            DropDownOption(7, R.string.action_recognize_screen, iconRes = R.drawable.ic_quick_switch_recognize_screen, iconTint = tintColor),
            DropDownOption(8, R.string.action_xiaoai, iconRes = R.drawable.ic_quick_switch_xiaoai, iconTint = tintColor),
            DropDownOption(9, R.string.action_floating_window, iconRes = R.drawable.ic_quick_switch_floating_window, iconTint = tintColor),
        )
    }

    HyperXPage(
        title = stringResource(R.string.page_miui_home),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_miui_home),
                appPkg = arrayOf(Scope.MIUI_HOME, Scope.PERSONAL_ASSIST),
            )
        }
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_home_anim,
            position = ItemPosition.First
        ) {
            SwitchPreference(
                key = Preferences.MiuiHome.DISABLE_ICON_ZOOM_ANIM,
                title = stringResource(R.string.home_anim_icon_zoom),
            )
            SwitchPreference(
                key = Preferences.MiuiHome.DISABLE_ICON_DARKEN_ANIM,
                title = stringResource(R.string.home_anim_icon_darken),
            )
            SwitchPreference(
                key = Preferences.MiuiHome.DISABLE_FOLDER_ZOOM_ANIM,
                title = stringResource(R.string.home_anim_folder_zoom),
            )
            SwitchPreference(
                key = Preferences.MiuiHome.DISABLE_FOLDER_DARKEN_ANIM,
                title = stringResource(R.string.home_anim_folder_icon_darken),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_home_folder,
        ) {
            SwitchPreference(
                key = Preferences.MiuiHome.FOLDER_ADAPT_SIZE,
                title = stringResource(R.string.home_folder_adapt_icon_size),
                summary = stringResource(R.string.home_folder_adapt_icon_size_tips),
            )
        }
        itemPreferenceGroup(
            key = R.string.ui_title_home_gesture,
            titleRes = R.string.ui_title_home_gesture,
        ) {
            DropDownPreference(
                key = Preferences.MiuiHome.BACK_GESTURE_HAPTIC,
                title = stringResource(R.string.home_gesture_back_haptic),
                options = backHapticOptions,
            )
            SwitchPreference(
                key = Preferences.MiuiHome.FIX_PREDICTIVE_BACK_PROG,
                title = stringResource(R.string.home_gesture_fix_predictive_back),
                summary = stringResource(R.string.home_gesture_fix_predictive_back_tips),
            )
            // (被注释的代码也用新架构重写并保留注释，随时可以解开使用)
            /*
            val quickSwitch = rememberSettingState(Pref.Key.MiuiHome.QUICK_SWITCH)
            SwitchPreference(
                title = stringResource(R.string.home_gesture_quick_switch),
                summary = stringResource(R.string.home_gesture_quick_switch_tips),
                checked = quickSwitch.value,
                onCheckedChange = { quickSwitch.value = it }
            )
            AnimatedVisibility(visible = quickSwitch.value) {
                Column {
                    val quickSwitchLeft = rememberSettingState(Pref.Key.MiuiHome.QUICK_SWITCH_LEFT)
                    DropDownPreference(
                        title = stringResource(R.string.home_gesture_quick_switch_left),
                        entries = dropdownEntriesQuickSwitchAction,
                        value = quickSwitchLeft.value,
                        onValueChange = { quickSwitchLeft.value = it as Int }
                    )

                    val quickSwitchRight = rememberSettingState(Pref.Key.MiuiHome.QUICK_SWITCH_RIGHT)
                    DropDownPreference(
                        title = stringResource(R.string.home_gesture_quick_switch_right),
                        entries = dropdownEntriesQuickSwitchAction,
                        value = quickSwitchRight.value,
                        onValueChange = { quickSwitchRight.value = it as Int }
                    )
                }
            }
            */
            DropDownPreference(
                key = Preferences.MiuiHome.LINE_GESTURE_LONG_PRESS,
                title = stringResource(R.string.home_gesture_line_long_press),
                summary = stringResource(R.string.home_gesture_line_long_press_tips),
                options = lineGestureActionOptions,
            )
            DropDownPreference(
                key = Preferences.MiuiHome.LINE_GESTURE_DOUBLE_TAP,
                title = stringResource(R.string.home_gesture_line_double_tap),
                summary = stringResource(R.string.home_gesture_line_double_tap_tips),
                options = lineGestureActionOptions,
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_home_recent,
        ) {
            SwitchPreference(
                key = Preferences.MiuiHome.OPT_RECENT_CARD_ANIM,
                title = stringResource(R.string.home_recent_dismiss_anim),
            )
            val hideClearButton = rememberPreferenceState(Preferences.MiuiHome.HIDE_RECENT_CLEAR_BUTTON)
            SwitchPreference(
                title = stringResource(R.string.home_recent_hide_clear_all),
                checked = hideClearButton.value,
                onCheckedChange = { hideClearButton.value = it }
            )
            AnimatedVisibility(visible = hideClearButton.value && !Device.isPad) {
                SwitchPreference(
                    key = Preferences.MiuiHome.RECENT_MEM_INFO_CLEAR,
                    title = stringResource(R.string.home_recent_mem_info_clear),
                )
            }
            AnimatedVisibility(visible = !Device.isPad) {
                SwitchPreference(
                    key = Preferences.MiuiHome.SHOW_RECENT_REAL_MEMORY,
                    title = stringResource(R.string.home_recent_show_real_memory),
                )
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_home_cleaner
        ) {
            SwitchPreference(
                key = Preferences.MiuiHome.REMOVE_REPORT,
                title = stringResource(R.string.home_cleaner_remove_report)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_home_others,
            position = ItemPosition.Last
        ) {
            SwitchPreference(
                key = Preferences.MiuiHome.REMOVE_DOCK_NUM_LIMIT,
                title = stringResource(R.string.home_others_dock_remove_num_limit),
            )
            SwitchPreference(
                key = Preferences.MiuiHome.RESTORE_MINUS_SETTING,
                title = stringResource(R.string.home_others_show_minus_setting),
            )
            SwitchPreference(
                key = Preferences.MiuiHome.DISABLE_RECENT_FAKE_NAVBAR,
                title = stringResource(R.string.home_others_hide_fake_navbar),
                summary = stringResource(R.string.home_others_hide_fake_navbar_tips),
            )
            DropDownPreference(
                key = Preferences.MiuiHome.FORCE_COLOR_TEXT_ICON,
                title = stringResource(R.string.home_others_force_color_scheme_element),
                summary = stringResource(R.string.home_others_force_color_scheme_element_tips),
                options = forceColorSchemeOptions,
            )
            DropDownPreference(
                key = Preferences.MiuiHome.FORCE_COLOR_STATUS_BAR,
                title = stringResource(R.string.home_others_force_color_scheme_statusbar),
                summary = stringResource(R.string.home_others_force_color_scheme_statusbar_tips),
                options = forceColorSchemeOptions,
            )
            DropDownPreference(
                key = Preferences.MiuiHome.FORCE_COLOR_MINUS,
                title = stringResource(R.string.home_others_force_color_scheme_minus),
                summary = stringResource(R.string.home_others_force_color_scheme_minus_tips),
                options = forceColorSchemeOptions,
            )
        }
    }
}