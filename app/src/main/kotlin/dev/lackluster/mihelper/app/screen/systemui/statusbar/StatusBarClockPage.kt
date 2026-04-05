package dev.lackluster.mihelper.app.screen.systemui.statusbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.Device

@Composable
fun StatusBarClockPage() {
    val isGeekModeOn = rememberPreferenceState(Preferences.SystemUI.StatusBar.Clock.ENABLE_GEEK_MODE)

    HyperXPage(
        title = stringResource(R.string.page_status_bar_clock),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI),
            )
        }
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_clock_general,
            position = ItemPosition.First
        ) {
            val customHorizontalPadding = rememberPreferenceState(Preferences.SystemUI.StatusBar.Clock.CUSTOM_HORIZON_PADDING)
            SwitchPreference(
                title = stringResource(R.string.clock_general_custom_layout),
                checked = customHorizontalPadding.value,
                onCheckedChange = { customHorizontalPadding.value = it }
            )
            AnimatedColumn(customHorizontalPadding.value) {
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.Clock.PADDING_START_VAL,
                    title = stringResource(R.string.clock_general_padding_left),
                    dialogMessage = stringResource(R.string.clock_general_custom_layout)
                )
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.Clock.PADDING_END_VAL,
                    title = stringResource(R.string.clock_general_padding_right),
                    dialogMessage = stringResource(R.string.clock_general_custom_layout)
                )
            }
            SwitchPreference(
                title = stringResource(R.string.clock_general_geek),
                checked = isGeekModeOn.value,
                onCheckedChange = { isGeekModeOn.value = it }
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.Clock.FIXED_WIDTH,
                title = stringResource(R.string.clock_easy_fixed_width),
                summary = stringResource(R.string.clock_easy_fixed_width_tips)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_clock_geek_mode,
            visible = isGeekModeOn.value
        ) {
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_CLOCK,
                title = stringResource(R.string.clock_geek_time_format_pattern_clock),
                isValueValid = { it.isNotBlank() }
            )
            AnimatedVisibility(Device.isPad) {
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_PAD_CLOCK,
                    title = stringResource(R.string.clock_geek_time_format_pattern_pad_clock),
                    isValueValid = { it.isNotBlank() }
                )
            }
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_BIG_TIME,
                title = stringResource(R.string.clock_geek_time_format_pattern_big_time),
                isValueValid = { it.isNotBlank() }
            )
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_DATE_TIME,
                title = stringResource(R.string.clock_geek_time_format_pattern_date_time),
                isValueValid = { it.isNotBlank() }
            )
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_CC_DATE,
                title = stringResource(R.string.clock_geek_time_format_pattern_cc_date),
                isValueValid = { it.isNotBlank() }
            )
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.Clock.GEEK_FORMAT_HORIZON_TIME,
                title = stringResource(R.string.clock_geek_time_format_pattern_horizon),
                isValueValid = { it.isNotBlank() }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_clock_easy,
            visible = !isGeekModeOn.value
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_AMPM,
                title = stringResource(R.string.clock_easy_show_ampm),
                summary = stringResource(R.string.clock_easy_show_ampm_tips)
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_LEADING_ZERO,
                title = stringResource(R.string.clock_easy_show_leading_zero),
                summary = stringResource(R.string.clock_easy_show_leading_zero_tips)
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.Clock.EASY_SHOW_SECONDS,
                title = stringResource(R.string.clock_easy_show_seconds),
                summary = stringResource(R.string.clock_easy_show_seconds_tips)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_clock_font_weight,
            position = ItemPosition.Last
        ) {
            val customClockFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_CLOCK)
            SwitchPreference(
                title = stringResource(R.string.clock_fw_clock),
                checked = customClockFont.value,
                onCheckedChange = { customClockFont.value = it }
            )
            AnimatedVisibility(customClockFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.CLOCK_WEIGHT,
                    title = stringResource(R.string.clock_fw_clock_weight),
                    min = 1,
                    max = 1000
                )
            }
            AnimatedColumn(Device.isPad) {
                val customPadClockFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_PAD_CLOCK)
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_pad_clock),
                    checked = customPadClockFont.value,
                    onCheckedChange = { customPadClockFont.value = it }
                )
                AnimatedVisibility(customPadClockFont.value) {
                    SeekBarPreference(
                        key = Preferences.SystemUI.StatusBar.Font.PAD_CLOCK_WEIGHT,
                        title = stringResource(R.string.clock_fw_pad_clock_weight),
                        min = 1,
                        max = 1000
                    )
                }
            }
            val customBigTimeFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_BIG_TIME)
            SwitchPreference(
                title = stringResource(R.string.clock_fw_big_time),
                checked = customBigTimeFont.value,
                onCheckedChange = { customBigTimeFont.value = it }
            )
            AnimatedVisibility(customBigTimeFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.BIG_TIME_WEIGHT,
                    title = stringResource(R.string.clock_fw_big_time_weight),
                    min = 1,
                    max = 1000
                )
            }
            val customDateTimeFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_DATE_TIME)
            SwitchPreference(
                title = stringResource(R.string.clock_fw_date_time),
                checked = customDateTimeFont.value,
                onCheckedChange = { customDateTimeFont.value = it }
            )
            AnimatedVisibility(customDateTimeFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.DATE_TIME_WEIGHT,
                    title = stringResource(R.string.clock_fw_date_time_weight),
                    min = 1,
                    max = 1000
                )
            }
            val customCCDateTimeFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_CC_DATE)
            SwitchPreference(
                title = stringResource(R.string.clock_fw_cc_date),
                checked = customCCDateTimeFont.value,
                onCheckedChange = { customCCDateTimeFont.value = it }
            )
            AnimatedVisibility(customCCDateTimeFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.CC_DATE_WEIGHT,
                    title = stringResource(R.string.clock_fw_cc_date_weight),
                    min = 1,
                    max = 1000
                )
            }
            val customHorizonTimeFont = rememberPreferenceState(Preferences.SystemUI.StatusBar.Font.CUSTOM_HORIZONTAL_TIME)
            SwitchPreference(
                title = stringResource(R.string.clock_fw_horizontal_time),
                checked = customHorizonTimeFont.value,
                onCheckedChange = { customHorizonTimeFont.value = it }
            )
            AnimatedVisibility(customHorizonTimeFont.value) {
                SeekBarPreference(
                    key = Preferences.SystemUI.StatusBar.Font.HORIZONTAL_TIME_WEIGHT,
                    title = stringResource(R.string.clock_fw_horizontal_time_weight),
                    min = 1,
                    max = 1000
                )
            }
        }
    }
}