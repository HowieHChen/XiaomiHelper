package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Device
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MiuiHomePage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var spValueQuickSwitch by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.MiuiHome.QUICK_SWITCH)) }
    var spValuePadShowMemory by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.MiuiHome.PAD_RECENT_SHOW_MEMORY)) }
    var spValueHideClearButton by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.MiuiHome.RECENT_HIDE_CLEAR_BUTTON)) }

    val tintColor = MiuixTheme.colorScheme.onSurfaceSecondary
    val dropdownEntriesQuickSwitchAction = listOf(
        DropDownEntry(
            title = stringResource(R.string.home_gesture_quick_switch_previous_app),
            iconRes = R.drawable.ic_header_android_green,
        ),
        DropDownEntry(
            title = stringResource(R.string.action_notifications),
            iconRes = R.drawable.ic_quick_switch_notifications,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_quick_settings),
            iconRes = R.drawable.ic_quick_switch_quick_settings,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_lock_screen),
            iconRes = R.drawable.ic_quick_switch_lock_screen,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_screenshot),
            iconRes = R.drawable.ic_quick_switch_screenshot,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_home),
            iconRes = R.drawable.ic_quick_switch_home,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_recents),
            iconRes = R.drawable.ic_quick_switch_recents,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_recognize_screen),
            iconRes = R.drawable.ic_quick_switch_recognize_screen,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_xiaoai),
            iconRes = R.drawable.ic_quick_switch_xiaoai,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_floating_window),
            iconRes = R.drawable.ic_quick_switch_floating_window,
            iconTint = tintColor
        ),
    )
    val dropdownEntriesLineGestureAction = listOf(
        DropDownEntry(
            title = stringResource(R.string.common_disabled),
            iconRes = R.drawable.ic_quick_switch_empty,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_notifications),
            iconRes = R.drawable.ic_quick_switch_notifications,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_quick_settings),
            iconRes = R.drawable.ic_quick_switch_quick_settings,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_lock_screen),
            iconRes = R.drawable.ic_quick_switch_lock_screen,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_screenshot),
            iconRes = R.drawable.ic_quick_switch_screenshot,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_home),
            iconRes = R.drawable.ic_quick_switch_home,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_recents),
            iconRes = R.drawable.ic_quick_switch_recents,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_recognize_screen),
            iconRes = R.drawable.ic_quick_switch_recognize_screen,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_xiaoai),
            iconRes = R.drawable.ic_quick_switch_xiaoai,
            iconTint = tintColor
        ),
        DropDownEntry(
            title = stringResource(R.string.action_floating_window),
            iconRes = R.drawable.ic_quick_switch_floating_window,
            iconTint = tintColor
        ),
    )
    val dropdownEntriesForceColorScheme = listOf(
        DropDownEntry(stringResource(R.string.weather_card_color_default)),
        DropDownEntry(stringResource(R.string.weather_card_color_light)),
        DropDownEntry(stringResource(R.string.weather_card_color_dark)),
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_miui_home),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        actions = {
            RebootMenuItem(
                appName = stringResource(R.string.scope_miui_home),
                appPkg = Scope.MIUI_HOME
            )
        }
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_anim),
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_anim_icon_zoom),
                    key = Pref.Key.MiuiHome.ANIM_ICON_ZOOM
                )
                SwitchPreference(
                    title = stringResource(R.string.home_anim_icon_darken),
                    key = Pref.Key.MiuiHome.ANIM_ICON_DARKEN
                )
                SwitchPreference(
                    title = stringResource(R.string.home_anim_folder_zoom),
                    key = Pref.Key.MiuiHome.ANIM_FOLDER_ZOOM
                )
                SwitchPreference(
                    title = stringResource(R.string.home_anim_folder_icon_darken),
                    key = Pref.Key.MiuiHome.ANIM_FOLDER_ICON_DARKEN
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_folder)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_folder_adapt_icon_size),
                    summary = stringResource(R.string.home_folder_adapt_icon_size_tips),
                    key = Pref.Key.MiuiHome.FOLDER_ADAPT_SIZE
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_gesture)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_gesture_double_tap),
                    key = Pref.Key.MiuiHome.DOUBLE_TAP_TO_SLEEP
                )
                SwitchPreference(
                    title = stringResource(R.string.home_gesture_back_haptic),
                    summary = stringResource(R.string.home_gesture_back_haptic_tips),
                    key = Pref.Key.MiuiHome.BACK_HAPTIC
                )
                SwitchPreference(
                    title = stringResource(R.string.home_gesture_quick_switch),
                    summary = stringResource(R.string.home_gesture_quick_switch_tips),
                    key = Pref.Key.MiuiHome.QUICK_SWITCH
                ) {
                    spValueQuickSwitch = it
                }
                AnimatedVisibility(
                    spValueQuickSwitch
                ) {
                    Column {
                        DropDownPreference(
                            title = stringResource(R.string.home_gesture_quick_switch_left),
                            entries = dropdownEntriesQuickSwitchAction,
                            key = Pref.Key.MiuiHome.QUICK_SWITCH_LEFT
                        )
                        DropDownPreference(
                            title = stringResource(R.string.home_gesture_quick_switch_right),
                            entries = dropdownEntriesQuickSwitchAction,
                            key = Pref.Key.MiuiHome.QUICK_SWITCH_RIGHT
                        )
                    }
                }
                DropDownPreference(
                    title = stringResource(R.string.home_gesture_line_long_press),
                    summary = stringResource(R.string.home_gesture_line_long_press_tips),
                    entries = dropdownEntriesLineGestureAction,
                    key = Pref.Key.MiuiHome.LINE_GESTURE_LONG_PRESS
                )
                DropDownPreference(
                    title = stringResource(R.string.home_gesture_line_double_tap),
                    summary = stringResource(R.string.home_gesture_line_double_tap_tips),
                    entries = dropdownEntriesLineGestureAction,
                    key = Pref.Key.MiuiHome.LINE_GESTURE_DOUBLE_TAP
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_recent)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_recent_dismiss_anim),
                    key = Pref.Key.MiuiHome.RECENT_CARD_ANIM
                )
                if (Device.isPad) {
                    SwitchPreference(
                        title = stringResource(R.string.home_recent_pad_show_memory),
                        key = Pref.Key.MiuiHome.PAD_RECENT_SHOW_MEMORY
                    ) {
                        spValuePadShowMemory = it
                    }
                    SwitchPreference(
                        title = stringResource(R.string.home_recent_pad_hide_world),
                        key = Pref.Key.MiuiHome.PAD_RECENT_HIDE_WORLD
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.home_recent_hide_clear_all),
                    key = Pref.Key.MiuiHome.RECENT_HIDE_CLEAR_BUTTON
                ) {
                    spValueHideClearButton = it
                }
                AnimatedVisibility(
                    spValueHideClearButton && (!Device.isPad || spValuePadShowMemory)
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.home_recent_mem_info_clear),
                        key = Pref.Key.MiuiHome.RECENT_MEM_INFO_CLEAR
                    )
                }
                AnimatedVisibility(
                    !Device.isPad || spValuePadShowMemory
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.home_recent_show_real_memory),
                        key = Pref.Key.MiuiHome.RECENT_SHOW_REAL_MEMORY
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_others),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_others_dock_remove_num_limit),
                    key = Pref.Key.MiuiHome.DOCK_REMOVE_NUM_LIMIT
                )
                SwitchPreference(
                    title = stringResource(R.string.home_others_show_minus_setting),
                    key = Pref.Key.MiuiHome.MINUS_RESTORE_SETTING
                )
                SwitchPreference(
                    title = stringResource(R.string.home_others_hide_fake_navbar),
                    summary = stringResource(R.string.home_others_hide_fake_navbar_tips),
                    key = Pref.Key.MiuiHome.RECENT_DISABLE_FAKE_NAVBAR
                )
                DropDownPreference(
                    title = stringResource(R.string.home_others_force_color_scheme_element),
                    summary = stringResource(R.string.home_others_force_color_scheme_element_tips),
                    entries = dropdownEntriesForceColorScheme,
                    key = Pref.Key.MiuiHome.FORCE_COLOR_TEXT_ICON
                )
                DropDownPreference(
                    title = stringResource(R.string.home_others_force_color_scheme_statusbar),
                    summary = stringResource(R.string.home_others_force_color_scheme_statusbar_tips),
                    entries = dropdownEntriesForceColorScheme,
                    key = Pref.Key.MiuiHome.FORCE_COLOR_STATUS_BAR
                )
                DropDownPreference(
                    title = stringResource(R.string.home_others_force_color_scheme_minus),
                    summary = stringResource(R.string.home_others_force_color_scheme_minus_tips),
                    entries = dropdownEntriesForceColorScheme,
                    key = Pref.Key.MiuiHome.FORCE_COLOR_MINUS
                )
            }
        }
    }
}