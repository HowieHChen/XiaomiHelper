package dev.lackluster.mihelper.app.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.Device

private val chainStartOptions = listOf(
    DropDownOption(0, R.string.security_security_skip_open_app_default),
    DropDownOption(1, R.string.security_security_skip_open_app_auto_approve, R.string.security_security_skip_open_app_auto_approve_tips),
    DropDownOption(2, R.string.security_security_skip_open_app_disable, R.string.security_security_skip_open_app_disable_tips),
)

@Composable
fun SecurityCenterPage() {
    val securityCenterTitle = if (Device.isPad) {
        stringResource(R.string.page_security_center_pad)
    } else {
        stringResource(R.string.page_security_center)
    }

    HyperXPage(
        title = securityCenterTitle
    ) {
        itemPreferenceGroup(
            key = R.string.page_security_center,
            title = securityCenterTitle,
            position = ItemPosition.First
        ) {
            SwitchPreference(
                key = Preferences.SecurityCenter.SKIP_WARNING_DIALOG,
                title = stringResource(R.string.security_security_skip_warning),
                summary = stringResource(R.string.security_security_skip_warning_tips),
            )
            DropDownPreference(
                key = Preferences.SecurityCenter.LINK_START,
                title = stringResource(R.string.security_security_skip_open_app),
                summary = stringResource(R.string.security_security_skip_open_app_tips),
                options = chainStartOptions,
            )
            val showScreenBatteryUsage = rememberPreferenceState(Preferences.SecurityCenter.BATTERY_SHOW_SCREEN)
            SwitchPreference(
                title = stringResource(R.string.security_security_screen_battery),
                summary = stringResource(R.string.security_security_screen_battery_tips),
                checked = showScreenBatteryUsage.value,
                onCheckedChange = { showScreenBatteryUsage.value = it }
            )
            AnimatedVisibility(
                visible = showScreenBatteryUsage.value
            ) {
                SwitchPreference(
                    key = Preferences.SecurityCenter.BATTERY_SHOW_SYSTEM,
                    title = stringResource(R.string.security_security_hide_screen_system),
                )
            }
            SwitchPreference(
                key = Preferences.SecurityCenter.CLICK_ICON_TO_OPEN,
                title = stringResource(R.string.security_security_icon_open),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.CTRL_SYSTEM_APP_WIFI,
                title = stringResource(R.string.security_security_system_app_wifi),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.DISABLE_BUBBLE_RESTRICT,
                title = stringResource(R.string.security_security_bubble_restriction),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.DISABLE_RISK_APP_NOTIF,
                title = stringResource(R.string.security_security_disable_risk_app_notif),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_security_cleaner
        ) {
            SwitchPreference(
                key = Preferences.SecurityCenter.SKIP_SPLASH,
                title = stringResource(R.string.security_cleaner_skip_splash),
                summary = stringResource(R.string.security_cleaner_skip_splash_tips),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.LOCK_SCORE,
                title = stringResource(R.string.security_cleaner_lock_score),
                summary = stringResource(R.string.security_cleaner_lock_score_tips),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.HIDE_HOME_RED_DOT,
                title = stringResource(R.string.security_cleaner_hide_red_dot),
                summary = stringResource(R.string.security_cleaner_hide_red_dot_tips),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.HIDE_HOME_REC,
                title = stringResource(R.string.security_cleaner_hide_home_rec),
                summary = stringResource(R.string.security_cleaner_hide_home_rec_tips),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.HIDE_HOME_COMMON,
                title = stringResource(R.string.security_cleaner_hide_home_common),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.HIDE_HOME_POPULAR,
                title = stringResource(R.string.security_cleaner_hide_home_popular),
                summary = stringResource(R.string.security_cleaner_hide_home_popular_tips),
            )
            SwitchPreference(
                key = Preferences.SecurityCenter.REMOVE_REPORT,
                title = stringResource(R.string.security_cleaner_remove_report),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_security_guard,
        ) {
            SwitchPreference(
                key = Preferences.GuardProvider.BLOCK_ENV_CHECK,
                title = stringResource(R.string.security_guard_block_env_check),
            )
            SwitchPreference(
                key = Preferences.GuardProvider.BLOCK_UPLOAD_APP,
                title = stringResource(R.string.security_guard_block_upload_app),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_lbe,
        ) {
            SwitchPreference(
                key = Preferences.LBE.BLOCK_REMOVE_AUTO_STARTUP,
                title = stringResource(R.string.security_lbe_block_remove_auto_start),
                summary = stringResource(R.string.security_lbe_block_remove_auto_start_tips),
            )
            SwitchPreference(
                key = Preferences.LBE.TOAST_CLIPBOARD_USAGE,
                title = stringResource(R.string.security_lbe_clipboard_toast),
                summary = stringResource(R.string.security_lbe_clipboard_toast_tips),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_security_mi_trust_service,
        ) {
            SwitchPreference(
                key = Preferences.MiTrust.BLOCK_RISK_CHECK,
                title = stringResource(R.string.mi_trust_disable_risk_check),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_security_power,
            position = ItemPosition.Last
        ) {
            SwitchPreference(
                key = Preferences.PowerKeeper.UNLOCK_CUSTOM_REFRESH,
                title = stringResource(R.string.security_power_unlock_custom_refresh),
            )
            SwitchPreference(
                key = Preferences.PowerKeeper.BLOCK_BATTERY_WHITELIST,
                title = stringResource(R.string.security_power_block_battery_whitelist),
            )
            SwitchPreference(
                key = Preferences.PowerKeeper.GMS_BG_RUNNING,
                title = stringResource(R.string.security_power_gms_bg_running),
                summary = stringResource(R.string.security_power_gms_bg_running_tips),
            )
        }
    }
}