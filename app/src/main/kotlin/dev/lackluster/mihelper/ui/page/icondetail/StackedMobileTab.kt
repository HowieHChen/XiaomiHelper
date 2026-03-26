package dev.lackluster.mihelper.ui.page.icondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.StackedMobile
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.model.MobileState
import dev.lackluster.mihelper.ui.model.StackedMobileState
import dev.lackluster.mihelper.ui.viewmodel.StackedMobileIconViewModel

fun LazyListScope.stackedMobileTabContent(
    isVisible: Boolean,
    stackedState: StackedMobileState,
    mobileState: MobileState,
    stackedViewModel: StackedMobileIconViewModel,
    validateAndUpdateCustomTypeMap: (String, String) -> Unit,
    updateMobilePreference: (String, Any) -> Unit,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "STACKED_GENERAL",
        titleResId = R.string.ui_title_icon_detail_cellular_general,
    ) {
        val dropdownEntriesAdvVisible = listOf(
            DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_default)),
            DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_all)),
            DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_statusbar)),
            DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_show_qs)),
            DropDownEntry(stringResource(R.string.icon_tuner_hide_selection_hidden)),
        )
        DropDownPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_stacked_icon),
            title = stringResource(R.string.icon_tuner_stacked_mobile_icon),
            summary = stringResource(R.string.icon_tuner_stacked_mobile_icon_tips),
            entries = dropdownEntriesAdvVisible,
            key = IconTuner.STACKED_MOBILE_ICON
        )
        DropDownPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_stacked_type),
            title = stringResource(R.string.ui_title_icon_detail_stacked_type),
            summary = stringResource(R.string.icon_tuner_stacked_mobile_type_tips),
            entries = dropdownEntriesAdvVisible,
            key = IconTuner.STACKED_MOBILE_TYPE
        )
        DropDownPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_single_sim1),
            title = stringResource(R.string.icon_tuner_single_mobile_sim1),
            summary = stringResource(R.string.icon_tuner_single_mobile_summary),
            entries = dropdownEntriesAdvVisible,
            key = IconTuner.SINGLE_MOBILE_SIM1
        )
        DropDownPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_single_sim2),
            title = stringResource(R.string.icon_tuner_single_mobile_sim2),
            summary = stringResource(R.string.icon_tuner_single_mobile_summary),
            entries = dropdownEntriesAdvVisible,
            key = IconTuner.SINGLE_MOBILE_SIM2
        )
    }
    itemPreferenceGroup(
        key = "STACKED_SIGNAL",
        titleResId = R.string.ui_title_icon_detail_stacked_signal,
    ) {
        val dropdownEntriesIconStyle = listOf(
            DropDownEntry(stringResource(R.string.icon_detail_stacked_signal_style_miui)),
            DropDownEntry(stringResource(R.string.icon_detail_stacked_signal_style_ios)),
            DropDownEntry(stringResource(R.string.icon_detail_stacked_signal_style_custom)),
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_stacked_signal_style_single),
            entries = dropdownEntriesIconStyle,
            value = stackedState.signal.singleStyle,
            onSelectedIndexChange = { stackedViewModel.updatePreference(StackedMobile.SIGNAL_SVG_SINGLE, it) } // 👈 回调分发
        )
        AnimatedVisibility(stackedState.signal.singleStyle == 2) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_single_val),
                value = stackedState.signal.stackedSVG,
                dataType = EditTextDataType.STRING,
                dialogMessage = stringResource(R.string.icon_detail_stacked_signal_style_single_msg),
                valuePosition = ValuePosition.HIDDEN,
                isValueValid = { (it as? String)?.isNotBlank() == true },
                onValueChange = { str, _ -> stackedViewModel.validateAndUpdateSingleSvg(str) }
            )
        }
        DropDownPreference(
            title = stringResource(R.string.icon_detail_stacked_signal_style_stacked),
            entries = dropdownEntriesIconStyle,
            value = stackedState.signal.stackedStyle,
            onSelectedIndexChange = { stackedViewModel.updatePreference(StackedMobile.SIGNAL_SVG_STACKED, it) }
        )
        AnimatedVisibility(stackedState.signal.stackedStyle == 2) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_stacked_val),
                value = stackedState.signal.stackedSVG,
                dataType = EditTextDataType.STRING,
                dialogMessage = stringResource(R.string.icon_detail_stacked_signal_style_stacked_msg),
                valuePosition = ValuePosition.HIDDEN,
                isValueValid = { (it as? String)?.isNotBlank() == true },
                onValueChange = { str, _ -> stackedViewModel.validateAndUpdateStackedSvg(str) }
            )
        }
        SeekBarPreference(
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_fg),
            value = stackedState.signal.alphaFg,
            defValue = 1.0f,
            min = 0.0f,
            max = 1.0f,
            onValueChange = { stackedViewModel.updatePreference(StackedMobile.SIGNAL_ALPHA_FG, it) }
        )
        SeekBarPreference(
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_bg),
            value = stackedState.signal.alphaBg,
            defValue = 0.4f,
            min = 0.0f,
            max = 1.0f,
            onValueChange = { stackedViewModel.updatePreference(StackedMobile.SIGNAL_ALPHA_BG, it) }
        )
        SeekBarPreference(
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_error),
            value = stackedState.signal.alphaError,
            defValue = 0.2f,
            min = 0.0f,
            max = 1.0f,
            onValueChange = { stackedViewModel.updatePreference(StackedMobile.SIGNAL_ALPHA_ERROR, it) }
        )
    }
    itemPreferenceGroup(
        key = "STACKED_TYPE",
        titleResId = R.string.ui_title_icon_detail_stacked_type,
    ) {
        val dropdownEntriesTypeFont = listOf(
            DropDownEntry(stringResource(R.string.icon_detail_stacked_type_font_default)),
            DropDownEntry(stringResource(R.string.icon_detail_stacked_type_font_custom)),
            DropDownEntry(stringResource(R.string.icon_detail_stacked_type_font_misans)),
            DropDownEntry(stringResource(R.string.icon_detail_stacked_type_font_sfpro)),
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_stacked_type_font),
            summary = stringResource(R.string.icon_detail_stacked_type_font_tips),
            entries = dropdownEntriesTypeFont,
            value = stackedState.font.mode,
            onSelectedIndexChange = { stackedViewModel.updatePreference(StackedMobile.TYPE_FONT_MODE, it) }
        )
        AnimatedVisibility(stackedState.font.mode == 1) {
            val errorHintSU = stringResource(R.string.font_hint_general_root)
            EditTextPreference(
                title = stringResource(R.string.font_general_path),
                value = stackedState.font.path,
                dataType = EditTextDataType.STRING,
                dialogMessage = stringResource(R.string.font_general_path_tips),
                valuePosition = ValuePosition.SUMMARY_VIEW,
                isValueValid = { (it as? String)?.isNotBlank() == true },
                onValueChange = { path, _ ->
                    stackedViewModel.updateFontPath(
                        newPath = path,
                        defaultPath = VARIABLE_FONT_DEFAULT_PATH,
                        errorHint = errorHintSU
                    )
                }
            )
        }
        AnimatedVisibility(stackedState.font.mode == 2 || stackedState.font.mode == 3) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_stacked_type_font_width_condensed),
                value = stackedState.font.condensedWidth,
                defValue = 80,
                min = 10,
                max = 200,
                format = "%d%%",
                onValueChange = { stackedViewModel.updatePreference(StackedMobile.TYPE_WIDTH_CONDENSED, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_type_map),
            value = mobileState.customTypeMap,
            onCheckedChange = { updateMobilePreference(IconTuner.CELLULAR_TYPE_CUSTOM, it) }
        )
        AnimatedVisibility(mobileState.customTypeMap) {
            val invalidHint = stringResource(R.string.common_invalid_input)
            EditTextPreference(
                title = stringResource(R.string.icon_detail_cellular_type_map_value),
                value = mobileState.typeMapString,
                defValue = Pref.DefValue.SystemUI.CELLULAR_TYPE_LIST,
                dataType = EditTextDataType.STRING,
                dialogMessage = stringResource(R.string.icon_detail_cellular_type_map_msg),
                isValueValid = { it is String && it.isNotEmpty() },
                valuePosition = ValuePosition.HIDDEN,
                onValueChange = { string, _ -> validateAndUpdateCustomTypeMap(string, invalidHint) }
            )
        }
    }
    itemPreferenceGroup(
        key = "STACKED_SMALL_TYPE",
        titleResId = R.string.ui_title_icon_detail_stacked_small_type,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_stacked_small_type_show_single),
            summary = stringResource(R.string.icon_detail_stacked_small_type_show_tips),
            value = stackedState.small.showOnSingle,
            onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_stacked_small_type_show_stacked),
            summary = stringResource(R.string.icon_detail_stacked_small_type_show_tips),
            value = stackedState.small.showOnStacked,
            onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.SMALL_TYPE_SHOW_ON_STACKED, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_stacked_small_type_roaming),
            value = stackedState.small.showRoaming,
            onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.SMALL_TYPE_SHOW_ROAMING, it) }
        )
        EditTextPreference(
            title = stringResource(R.string.icon_detail_stacked_small_type_size),
            value = stackedState.small.size,
            defValue = 7.159973f,
            dataType = EditTextDataType.FLOAT,
            isValueValid = { (it as? Float ?: -1.0f) > 0.0f },
            onValueChange = {  _, value -> stackedViewModel.updatePreference(StackedMobile.SMALL_TYPE_SIZE, value as Float) }
        )

        AnimatedVisibility(stackedState.font.mode != 0) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_stacked_small_type_weight),
                value = stackedState.small.weight,
                defValue = 630,
                min = 1,
                max = 1000,
                onValueChange = { stackedViewModel.updatePreference(StackedMobile.SMALL_TYPE_FONT_WEIGHT, it) }
            )
        }
    }
    itemPreferenceGroup(
        key = "STACKED_LARGE_TYPE",
        titleResId = R.string.ui_title_icon_detail_stacked_large_type,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_stacked_large_type_hide_disconnect),
            summary = stringResource(R.string.icon_detail_stacked_large_type_hide_disconnect_tips),
            value = stackedState.large.hideWhenDisconnect,
            onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_stacked_large_type_hide_wifi),
            value = stackedState.large.hideWhenWifi,
            onCheckedChange = { stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI, it) }
        )
        EditTextPreference(
            title = stringResource(R.string.icon_detail_stacked_large_type_size),
            value = stackedState.large.size,
            defValue = 14.0f,
            dataType = EditTextDataType.FLOAT,
            isValueValid = { (it as? Float ?: -1.0f) > 0.0f },
            onValueChange = {  _, value -> stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_SIZE, value as Float) }
        )
        AnimatedVisibility(stackedState.font.mode != 0) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_stacked_large_type_weight),
                value = stackedState.large.weight,
                defValue = 400,
                min = 1,
                max = 1000,
                onValueChange = { stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_FONT_WEIGHT, it) }
            )
        }
        EditTextPreference(
            title = stringResource(R.string.icon_detail_stacked_large_type_vertical_offset),
            summary = stringResource(R.string.icon_detail_stacked_large_type_vertical_offset_tips),
            value = stackedState.large.verticalOffset,
            defValue = 0.0f,
            dataType = EditTextDataType.FLOAT,
            onValueChange = {  _, value -> stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_VERTICAL_OFFSET, value as Float) }
        )
        EditTextPreference(
            title = stringResource(R.string.icon_detail_battery_padding_start),
            value = stackedState.large.paddingStart,
            defValue = 2.0f,
            dataType = EditTextDataType.FLOAT,
            onValueChange = {  _, value -> stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_PADDING_START_VAL, value as Float) }
        )
        EditTextPreference(
            title = stringResource(R.string.icon_detail_battery_padding_end),
            value = stackedState.large.paddingEnd,
            defValue = 2.0f,
            dataType = EditTextDataType.FLOAT,
            onValueChange = {  _, value -> stackedViewModel.updatePreference(StackedMobile.LARGE_TYPE_PADDING_END_VAL, value as Float) }
        )
    }
}