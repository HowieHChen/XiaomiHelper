package dev.lackluster.mihelper.ui.page.icondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownMode
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.model.BatteryState

fun LazyListScope.batteryTabContent(
    isVisible: Boolean,
    batteryState: BatteryState,
    updateBatteryPreference: (String, Any) -> Unit,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "BATTERY",
    ) {
        val dropdownEntriesBatteryStyle = listOf(
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_default),
                summary = stringResource(R.string.icon_detail_battery_style_default_tips),
                iconRes = R.drawable.ic_battery_style_hidden
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_icon_only),
                summary = stringResource(R.string.icon_detail_battery_style_icon_only_tips),
                iconRes = R.drawable.ic_battery_style_icon_only
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_text_in),
                summary = stringResource(R.string.icon_detail_battery_style_text_in_tips),
                iconRes = R.drawable.ic_battery_style_text_in
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_line),
                summary = stringResource(R.string.icon_detail_battery_style_line_tips),
                iconRes = R.drawable.ic_battery_style_line
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_text_out),
                summary = stringResource(R.string.icon_detail_battery_style_text_out_tips),
                iconRes = R.drawable.ic_battery_style_text_out
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_text_only),
                summary = stringResource(R.string.icon_detail_battery_style_text_only_tips),
                iconRes = R.drawable.ic_battery_style_text_only
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_style_hidden),
                summary = stringResource(R.string.icon_detail_battery_style_hidden_tips),
                iconRes = R.drawable.ic_battery_style_hidden
            ),
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_battery_bar_style),
            entries = dropdownEntriesBatteryStyle,
            value = batteryState.styleStatusBar,
            mode = DropDownMode.Dialog,
            onSelectedIndexChange = { updateBatteryPreference(IconTuner.BATTERY_STYLE, it) }
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_battery_cc_style),
            entries = dropdownEntriesBatteryStyle,
            value = batteryState.styleControlCenter,
            mode = DropDownMode.Dialog,
            onSelectedIndexChange = { updateBatteryPreference(IconTuner.BATTERY_STYLE_CC, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_layout_custom),
            value = batteryState.customPadding,
            onCheckedChange = { updateBatteryPreference(IconTuner.BATTERY_PADDING_HORIZON, it) }
        )
        AnimatedVisibility(batteryState.customPadding) {
            Column {
                EditTextPreference(
                    title = stringResource(R.string.icon_detail_battery_padding_start),
                    value = batteryState.paddingStart,
                    defValue = 0.0f,
                    dataType = EditTextDataType.FLOAT,
                    onValueChange = { _, value -> updateBatteryPreference(IconTuner.BATTERY_PADDING_START_VAL, value as Float) }
                )
                EditTextPreference(
                    title = stringResource(R.string.icon_detail_battery_padding_end),
                    value = batteryState.paddingEnd,
                    defValue = 0.0f,
                    dataType = EditTextDataType.FLOAT,
                    onValueChange = { _, value -> updateBatteryPreference(IconTuner.BATTERY_PADDING_END_VAL, value as Float) }
                )
            }
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_hide_charge),
            summary = stringResource(R.string.icon_detail_battery_hide_charge_tips),
            value = batteryState.hideCharge,
            onCheckedChange = { updateBatteryPreference(IconTuner.HIDE_BATTERY_CHARGE_OUT, it) }
        )
    }
    itemPreferenceGroup(
        key = "BATTERY_PERCENT",
        titleResId = R.string.ui_title_icon_detail_batter_percentage,
    ) {
        val dropdownEntriesBatteryPercentage = listOf(
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_percent_mark_style_default),
                iconRes = R.drawable.ic_battery_percent_style_default
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_percent_mark_style_uni),
                iconRes = R.drawable.ic_battery_percent_style_digit
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_battery_percent_mark_style_hidden),
                iconRes = R.drawable.ic_battery_percent_style_hidden
            ),
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_battery_percent_mark_style),
            entries = dropdownEntriesBatteryPercentage,
            value = batteryState.percentMarkStyle,
            mode = DropDownMode.Dialog,
            onSelectedIndexChange = { updateBatteryPreference(IconTuner.BATTERY_PERCENT_MARK_STYLE, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_percent_out_size),
            value = batteryState.percentOutSize.enabled,
            onCheckedChange = { updateBatteryPreference(IconTuner.BATTERY_PERCENT_OUT_SIZE, it) }
        )
        AnimatedVisibility(batteryState.percentOutSize.enabled) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_battery_percent_out_size_value),
                value = batteryState.percentOutSize.size,
                defValue = 12.5f,
                dataType = EditTextDataType.FLOAT,
                isValueValid = {
                    (it as? Float ?: -1.0f) > 0.0f
                },
                onValueChange = { _, value -> updateBatteryPreference(IconTuner.BATTERY_PERCENT_OUT_SIZE_VAL, value) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_percent_in_size),
            value = batteryState.percentInSize.enabled,
            onCheckedChange = { updateBatteryPreference(IconTuner.BATTERY_PERCENT_IN_SIZE, it) }
        )
        AnimatedVisibility(batteryState.percentInSize.enabled) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_battery_percent_in_size_value),
                value = batteryState.percentInSize.size,
                defValue = 9.599976f,
                dataType = EditTextDataType.FLOAT,
                isValueValid = {
                    (it as? Float ?: -1.0f) > 0.0f
                },
                onValueChange = { _, value -> updateBatteryPreference(IconTuner.BATTERY_PERCENT_IN_SIZE_VAL, value) }
            )
        }
    }
    itemPreferenceGroup(
        key = "BATTERY_FONT",
        titleResId = R.string.ui_title_icon_detail_font_weight,
        last = true,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_fw_percent_out),
            value = batteryState.percentOutFont.enabled,
            onCheckedChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_OUT, it) }
        )
        AnimatedVisibility(batteryState.percentOutFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_battery_fw_percent_out_weight),
                value = batteryState.percentOutFont.weight,
                defValue = 500,
                min = 1,
                max = 1000,
                onValueChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_OUT_VAL, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_fw_percent_mark),
            value = batteryState.percentMarkFont.enabled,
            onCheckedChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_MARK, it) }
        )
        AnimatedVisibility(batteryState.percentMarkFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_battery_fw_percent_mark_weight),
                value = batteryState.percentMarkFont.weight,
                defValue = 600,
                min = 1,
                max = 1000,
                onValueChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_MARK_VAL, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_battery_fw_percent_in),
            value = batteryState.percentInFont.enabled,
            onCheckedChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_IN, it) }
        )
        AnimatedVisibility(batteryState.percentInFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_battery_fw_percent_in_weight),
                value = batteryState.percentInFont.weight,
                defValue = 620,
                min = 1,
                max = 1000,
                onValueChange = { updateBatteryPreference(FontWeight.BATTERY_PERCENTAGE_IN_VAL, it) }
            )
        }
    }
}