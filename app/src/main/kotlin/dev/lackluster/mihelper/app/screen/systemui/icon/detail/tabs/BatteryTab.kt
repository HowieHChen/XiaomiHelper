package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.preference.DropDownMode
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.BatteryState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val batteryStyleOptions = listOf(
    DropDownOption(0, R.string.icon_detail_battery_style_default, R.string.icon_detail_battery_style_default_tips, R.drawable.ic_battery_style_hidden),
    DropDownOption(1, R.string.icon_detail_battery_style_icon_only, R.string.icon_detail_battery_style_icon_only_tips, R.drawable.ic_battery_style_icon_only),
    DropDownOption(2, R.string.icon_detail_battery_style_text_in, R.string.icon_detail_battery_style_text_in_tips, R.drawable.ic_battery_style_text_in),
    DropDownOption(3, R.string.icon_detail_battery_style_line, R.string.icon_detail_battery_style_line_tips, R.drawable.ic_battery_style_line),
    DropDownOption(4, R.string.icon_detail_battery_style_text_out, R.string.icon_detail_battery_style_text_out_tips, R.drawable.ic_battery_style_text_out),
    DropDownOption(5, R.string.icon_detail_battery_style_text_only, R.string.icon_detail_battery_style_text_only_tips, R.drawable.ic_battery_style_text_only),
    DropDownOption(6, R.string.icon_detail_battery_style_hidden, R.string.icon_detail_battery_style_hidden_tips, R.drawable.ic_battery_style_hidden),
)

private val batteryPercentageStyleOptions = listOf(
    DropDownOption(0, R.string.icon_detail_battery_percent_mark_style_default, iconRes = R.drawable.ic_battery_percent_style_default),
    DropDownOption(1, R.string.icon_detail_battery_percent_mark_style_uni, iconRes = R.drawable.ic_battery_percent_style_digit),
    DropDownOption(2, R.string.icon_detail_battery_percent_mark_style_hidden, iconRes = R.drawable.ic_battery_percent_style_hidden),
)

fun LazyListScope.batteryTabContent(
    isVisible: Boolean,
    batteryState: BatteryState,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "BATTERY",
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_BAR,
            title = stringResource(R.string.icon_detail_battery_bar_style),
            options = batteryStyleOptions,
            mode = DropDownMode.Dialog,
        )
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_STYLE_CC,
            title = stringResource(R.string.icon_detail_battery_cc_style),
            options = batteryStyleOptions,
            mode = DropDownMode.Dialog,
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PADDING_HORIZON,
            title = stringResource(R.string.icon_detail_battery_layout_custom),
        )
        AnimatedColumn(batteryState.customPadding) {
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_START_VAL,
                title = stringResource(R.string.icon_detail_battery_padding_start),
            )
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PADDING_END_VAL,
                title = stringResource(R.string.icon_detail_battery_padding_end),
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_BATTERY_CHARGE_OUT,
            title = stringResource(R.string.icon_detail_battery_hide_charge),
            summary = stringResource(R.string.icon_detail_battery_hide_charge_tips),
        )
    }
    itemPreferenceGroup(
        key = "BATTERY_PERCENTAGE",
        titleRes = R.string.ui_title_icon_detail_batter_percentage,
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_MARK_STYLE,
            title = stringResource(R.string.icon_detail_battery_percent_mark_style),
            options = batteryPercentageStyleOptions,
            mode = DropDownMode.Dialog,
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_OUT_SIZE,
            title = stringResource(R.string.icon_detail_battery_percent_out_size),
        )
        AnimatedVisibility(batteryState.percentOutSize.enabled) {
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_OUT_SIZE_VAL,
                title = stringResource(R.string.icon_detail_battery_percent_out_size_value),
                isValueValid = { it > 0.0f },
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_BATTERY_PERCENT_IN_SIZE,
            title = stringResource(R.string.icon_detail_battery_percent_in_size),
        )
        AnimatedVisibility(batteryState.percentInSize.enabled) {
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.BATTERY_PERCENT_IN_SIZE_VAL,
                title = stringResource(R.string.icon_detail_battery_percent_in_size_value),
                isValueValid = { it > 0.0f },
            )
        }
    }
    itemPreferenceGroup(
        key = "BATTERY_FONT",
        titleRes = R.string.ui_title_icon_detail_font_weight,
        position = ItemPosition.Last,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_OUT,
            title = stringResource(R.string.icon_detail_battery_fw_percent_out),
        )
        AnimatedVisibility(batteryState.percentOutFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_OUT_WEIGHT,
                title = stringResource(R.string.icon_detail_battery_fw_percent_out_weight),
                min = 1,
                max = 1000,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_MARK,
            title = stringResource(R.string.icon_detail_battery_fw_percent_mark),
        )
        AnimatedVisibility(batteryState.percentMarkFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_MARK_WEIGHT,
                title = stringResource(R.string.icon_detail_battery_fw_percent_mark_weight),
                min = 1,
                max = 1000,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_BATTERY_PERCENTAGE_IN,
            title = stringResource(R.string.icon_detail_battery_fw_percent_in),
        )
        AnimatedVisibility(batteryState.percentInFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.BATTERY_PERCENTAGE_IN_WEIGHT,
                title = stringResource(R.string.icon_detail_battery_fw_percent_in_weight),
                min = 1,
                max = 1000,
            )
        }
    }
}