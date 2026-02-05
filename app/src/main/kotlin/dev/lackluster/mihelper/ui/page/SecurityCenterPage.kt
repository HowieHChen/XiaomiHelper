package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
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
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@Composable
fun SecurityCenterPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var visibilityShowSystem by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY)) }

    val securityCenter = if (Device.isPad) stringResource(R.string.page_security_center_pad) else stringResource(R.string.page_security_center)

    val dropdownEntriesChainStart = listOf(
        DropDownEntry(
            title = stringResource(R.string.security_security_skip_open_app_default)
        ),
        DropDownEntry(
            title = stringResource(R.string.security_security_skip_open_app_auto_approve),
            summary = stringResource(R.string.security_security_skip_open_app_auto_approve_tips)
        ),
        DropDownEntry(
            title = stringResource(R.string.security_security_skip_open_app_disable),
            summary = stringResource(R.string.security_security_skip_open_app_disable_tips)
        ),
    )

    BasePage(
        navController,
        adjustPadding,
        securityCenter,
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = securityCenter,
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.security_security_skip_warning),
                    summary = stringResource(R.string.security_security_skip_warning_tips),
                    key = Pref.Key.SecurityCenter.SKIP_WARNING
                )
                DropDownPreference(
                    title = stringResource(R.string.security_security_skip_open_app),
                    summary = stringResource(R.string.security_security_skip_open_app_tips),
                    entries = dropdownEntriesChainStart,
                    key = Pref.Key.SecurityCenter.LINK_START
                )
                SwitchPreference(
                    title = stringResource(R.string.security_security_screen_battery),
                    summary = stringResource(R.string.security_security_screen_battery_tips),
                    key = Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY
                ) {
                    visibilityShowSystem = it
                }
                AnimatedVisibility(
                    visible = visibilityShowSystem
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.security_security_hide_screen_system),
                        key = Pref.Key.SecurityCenter.SHOW_SYSTEM_BATTERY
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.security_security_icon_open),
                    key = Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN
                )
                SwitchPreference(
                    title = stringResource(R.string.security_security_system_app_wifi),
                    key = Pref.Key.SecurityCenter.CTRL_SYSTEM_APP_WIFI
                )
                SwitchPreference(
                    title = stringResource(R.string.security_security_bubble_restriction),
                    key = Pref.Key.SecurityCenter.DISABLE_BUBBLE_RESTRICT
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_security_guard)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.security_guard_env_check),
                    key = Pref.Key.GuardProvider.BLOCK_ENV_CHECK
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_lbe)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.security_lbe_block_remove_auto_start),
                    summary = stringResource(R.string.security_lbe_block_remove_auto_start_tips),
                    key = Pref.Key.LBE.BLOCK_REMOVE_AUTO_STARTUP
                )
                SwitchPreference(
                    title = stringResource(R.string.security_lbe_clipboard_toast),
                    summary = stringResource(R.string.security_lbe_clipboard_toast_tips),
                    key = Pref.Key.LBE.CLIPBOARD_TOAST
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_security_mi_trust_service)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.mi_trust_disable_risk_check),
                    key = Pref.Key.MiTrust.DISABLE_RISK_CHECK
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_security_power),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.security_power_unlock_custom_refresh),
                    key = Pref.Key.PowerKeeper.UNLOCK_CUSTOM_REFRESH
                )
                SwitchPreference(
                    title = stringResource(R.string.security_power_block_battery_whitelist),
                    key = Pref.Key.PowerKeeper.BLOCK_BATTERY_WHITELIST
                )
                SwitchPreference(
                    title = stringResource(R.string.security_power_gms_bg_running),
                    summary = stringResource(R.string.security_power_gms_bg_running_tips),
                    key = Pref.Key.PowerKeeper.GMS_BG_RUNNING
                )
            }
        }
    }
}