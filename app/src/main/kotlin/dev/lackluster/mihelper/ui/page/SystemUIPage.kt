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
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope

@Composable
fun SystemUIPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val expandNotificationsEntries = listOf(
        DropDownEntry(
            title = stringResource(R.string.systemui_notif_expand_notif_def),
            summary = stringResource(R.string.systemui_notif_expand_notif_def_tips)
        ),
        DropDownEntry(
            title = stringResource(R.string.systemui_notif_expand_notif_first),
            summary = stringResource(R.string.systemui_notif_expand_notif_first_tips)
        ),
        DropDownEntry(
            title = stringResource(R.string.systemui_notif_expand_notif_ungrouped),
            summary = stringResource(R.string.systemui_notif_expand_notif_ungrouped_tips)
        ),
    )
    val regionSamplingEntries = listOf(
        DropDownEntry(
            title = stringResource(R.string.systemui_statusbar_region_sampling_def),
            summary = stringResource(R.string.systemui_statusbar_region_sampling_def_tips),
        ),
        DropDownEntry(
            title = stringResource(R.string.systemui_statusbar_region_sampling_enable),
            summary = stringResource(R.string.systemui_statusbar_region_sampling_enable_tips),
        ),
        DropDownEntry(
            title = stringResource(R.string.systemui_statusbar_region_sampling_disable),
            summary = stringResource(R.string.systemui_statusbar_region_sampling_disable_tips),
        ),
    )

    var visibilityCustomNotifCount by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT)
    ) }
    var visibilityExpandIgnoreFocus by remember { mutableStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.NotifCenter.EXPAND_NOTIFICATION, 0) == 1
    ) }
    var visibilityNotifOpt by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.NotifCenter.LAYOUT_RANK_OPT)
    ) }
    var visibilityMonetColor by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.NotifCenter.MONET_OVERLAY)
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_systemui),
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
            PreferenceGroup(
                stringResource(R.string.ui_title_systemui_status_bar),
                first = true
            ) {
                TextPreference(
                    title = stringResource(R.string.systemui_statusbar_font)
                ) {
                    navController.navigateTo(Pages.STATUS_BAR_FONT)
                }
                TextPreference(
                    title = stringResource(R.string.systemui_statusbar_clock)
                ) {
                    navController.navigateTo(Pages.STATUS_BAR_CLOCK)
                }
                SwitchPreference(
                    title = stringResource(R.string.systemui_statusbar_notif_count),
                    key = Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT,
                    defValue = visibilityCustomNotifCount
                ) {
                    visibilityCustomNotifCount = it
                }
                AnimatedVisibility(
                    visibilityCustomNotifCount
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.systemui_statusbar_notif_count_icon),
                        key = Pref.Key.SystemUI.StatusBar.NOTIFICATION_COUNT_ICON,
                        defValue = 3,
                        min = 0,
                        max = 15
                    )
                }
                TextPreference(
                    title = stringResource(R.string.systemui_statusbar_icon)
                ) {
                    navController.navigateTo(Pages.ICON_TUNER)
                }
                SwitchPreference(
                    title = stringResource(R.string.systemui_statusbar_tap_to_sleep),
                    key = Pref.Key.SystemUI.StatusBar.DOUBLE_TAP_TO_SLEEP
                )
                DropDownPreference(
                    title = stringResource(R.string.systemui_statusbar_region_sampling),
                    summary = stringResource(R.string.systemui_statusbar_region_sampling_tips),
                    entries = regionSamplingEntries,
                    key = Pref.Key.SystemUI.StatusBar.REGION_SAMPLING,
                )
            }
        }
        item {
            PreferenceGroup(
                stringResource(R.string.ui_title_systemui_lock_screen)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_hide_disturb),
                    key = Pref.Key.SystemUI.LockScreen.HIDE_DISTURB
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_keep_notif),
                    summary = stringResource(R.string.systemui_lock_keep_notif_tips),
                    key = Pref.Key.SystemUI.LockScreen.KEEP_NOTIFICATION
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_double_tap),
                    key = Pref.Key.SystemUI.LockScreen.DOUBLE_TAP_TO_SLEEP
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_flashlight_on),
                    summary = stringResource(R.string.systemui_lock_flashlight_on_tips),
                    key = Pref.Key.SystemUI.Plugin.AUTO_FLASH_ON
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_keep_clock_container),
                    summary = stringResource(R.string.systemui_lock_keep_clock_container_tips),
                    key = Pref.Key.SystemUI.LockScreen.KEEP_CLOCK_CONTAINER
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_hide_next_alarm),
                    summary = stringResource(R.string.systemui_lock_hide_next_alarm_tips),
                    key = Pref.Key.SystemUI.LockScreen.HIDE_NEXT_ALARM
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_hide_carrier_one),
                    key = Pref.Key.SystemUI.LockScreen.HIDE_CARRIER_ONE
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_lock_hide_carrier_two),
                    key = Pref.Key.SystemUI.LockScreen.HIDE_CARRIER_TWO
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_systemui_notification_center)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.systemui_notif_freeform),
                    summary = stringResource(R.string.systemui_notif_freeform_tips),
                    key = Pref.Key.SystemUI.NotifCenter.NOTIF_FREEFORM
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_notif_disable_whitelist),
                    summary = stringResource(R.string.systemui_notif_disable_whitelist_tips),
                    key = Pref.Key.SystemUI.NotifCenter.NOTIF_NO_WHITELIST
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_notif_miuix_expand_btn),
                    summary = stringResource(R.string.systemui_notif_miuix_expand_btn_tips),
                    key = Pref.Key.SystemUI.NotifCenter.MIUIX_EXPAND_BUTTON
                )
                DropDownPreference(
                    title = stringResource(R.string.systemui_notif_expand_notif),
                    summary = stringResource(R.string.systemui_notif_expand_notif_tips),
                    entries = expandNotificationsEntries,
                    key = Pref.Key.SystemUI.NotifCenter.EXPAND_NOTIFICATION
                ) {
                    visibilityExpandIgnoreFocus = (it == 1)
                }
                AnimatedVisibility(
                    visibilityExpandIgnoreFocus
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.systemui_notif_expand_ignore_focus),
                        summary = stringResource(R.string.systemui_notif_expand_ignore_focus_tips),
                        key = Pref.Key.SystemUI.NotifCenter.EXPAND_IGNORE_FOCUS
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.systemui_notif_lr_opt),
                    summary = stringResource(R.string.systemui_notif_lr_opt_tips),
                    key = Pref.Key.SystemUI.NotifCenter.LAYOUT_RANK_OPT
                ) {
                    visibilityNotifOpt = it
                }
                AnimatedVisibility(
                    visibilityNotifOpt
                ) {
                    Column {
                        SwitchPreference(
                            title = stringResource(R.string.systemui_notif_lr_hide_section_header),
                            summary = stringResource(R.string.systemui_notif_lr_hide_section_header_tips),
                            key = Pref.Key.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_HEADER,
                            defValue = true
                        )
                        SwitchPreference(
                            title = stringResource(R.string.systemui_notif_lr_hide_section_gap),
                            summary = stringResource(R.string.systemui_notif_lr_hide_section_gap_tips),
                            key = Pref.Key.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_GAP,
                            defValue = true
                        )
                        SwitchPreference(
                            title = stringResource(R.string.systemui_notif_lr_rerank),
                            summary = stringResource(R.string.systemui_notif_lr_rerank_tips),
                            key = Pref.Key.SystemUI.NotifCenter.LR_OPT_RERANK,
                            defValue = true
                        )
                    }
                }
                TextPreference(
                    title = stringResource(R.string.systemui_notif_media_control_style),
                    summary = stringResource(R.string.systemui_notif_media_control_style_tips)
                ) {
                    navController.navigateTo(Pages.MEDIA_CONTROL)
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_systemui_dynamic_island)
            ) {
                TextPreference(
                    title = stringResource(R.string.systemui_di_media_control_style),
                    summary = stringResource(R.string.systemui_di_media_control_style_tips)
                ) {
                    navController.navigateTo(Pages.ISLAND_MEDIA_CONTROL)
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_systemui_control_center)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.systemui_control_hide_carrier_one),
                    key = Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_ONE
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_control_hide_carrier_two),
                    key = Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_TWO
                )
                SwitchPreference(
                    title = stringResource(R.string.systemui_control_hide_carrier_hd),
                    summary = stringResource(R.string.systemui_control_hide_carrier_hd_tips),
                    key = Pref.Key.SystemUI.ControlCenter.HIDE_CARRIER_HD
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_systemui_others),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.systemui_others_monet_overlay),
                    summary = stringResource(R.string.systemui_others_monet_overlay_tips),
                    key = Pref.Key.SystemUI.NotifCenter.MONET_OVERLAY
                ) {
                    visibilityMonetColor = it
                }
                AnimatedVisibility(
                    visibilityMonetColor
                ) {
                    EditTextPreference(
                        title = stringResource(R.string.systemui_others_monet_color),
                        summary = stringResource(R.string.systemui_others_monet_color_tips),
                        key = Pref.Key.SystemUI.NotifCenter.MONET_OVERLAY_COLOR,
                        defValue = "#FF3482FF",
                        dataType = EditTextDataType.STRING,
                        dialogMessage = stringResource(R.string.systemui_others_monet_color_msg),
                        isValueValid = { color ->
                            (color as? String)?.let {
                                color.matches("#[0-9a-fA-f]{8}".toRegex()) || color.matches("#[0-9a-fA-f]{6}".toRegex())
                            } == true
                        }
                    )
                }
            }
        }
    }
}