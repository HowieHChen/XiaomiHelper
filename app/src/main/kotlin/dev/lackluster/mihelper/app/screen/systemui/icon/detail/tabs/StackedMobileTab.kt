package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.MobileState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.StackedMobileState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences

sealed interface StackedMobileAction {
    data class ImportSVGFile(val isStacked: Boolean) : StackedMobileAction
    data class ValidateAndUpdateSvg(val svgContent: String, val isStacked: Boolean) : StackedMobileAction
    object ImportLocalFont : StackedMobileAction
    data class ApplyManualPath(val path: String) : StackedMobileAction
    data class UpdateCustomTypeMap(val list: String) : StackedMobileAction
}

private val visibilityOptions = listOf(
    DropDownOption(0, R.string.icon_tuner_hide_selection_default),
    DropDownOption(1, R.string.icon_tuner_hide_selection_show_all),
    DropDownOption(2, R.string.icon_tuner_hide_selection_show_statusbar),
    DropDownOption(3, R.string.icon_tuner_hide_selection_show_qs),
    DropDownOption(4, R.string.icon_tuner_hide_selection_hidden),
)

private val signalIconStyle = listOf(
    DropDownOption(0, R.string.icon_detail_stacked_signal_style_miui),
    DropDownOption(1, R.string.icon_detail_stacked_signal_style_ios),
    DropDownOption(2, R.string.icon_detail_stacked_signal_style_custom),
)

private val dropdownEntriesTypeFont = listOf(
    DropDownOption(0, R.string.icon_detail_stacked_type_font_default),
    DropDownOption(1, R.string.icon_detail_stacked_type_font_custom),
    DropDownOption(2, R.string.icon_detail_stacked_type_font_misans),
    DropDownOption(3, R.string.icon_detail_stacked_type_font_sfpro),
)

fun LazyListScope.stackedMobileTabContent(
    isVisible: Boolean,
    stackedState: StackedMobileState,
    mobileState: MobileState,
    onAction: (StackedMobileAction) -> Unit,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "STACKED_GENERAL",
        titleRes = R.string.ui_title_icon_detail_stacked_visibility,
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.STACKED_MOBILE_ICON,
            icon = ImageIcon(R.drawable.ic_stat_sys_stacked_icon),
            title = stringResource(R.string.icon_tuner_stacked_mobile_icon),
            summary = stringResource(R.string.icon_tuner_stacked_mobile_icon_tips),
            options = visibilityOptions,
        )
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.STACKED_MOBILE_TYPE,
            icon = ImageIcon(R.drawable.ic_stat_sys_stacked_type),
            title = stringResource(R.string.ui_title_icon_detail_stacked_type),
            summary = stringResource(R.string.icon_tuner_stacked_mobile_type_tips),
            options = visibilityOptions,
        )
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SINGLE_MOBILE_SIM1,
            icon = ImageIcon(R.drawable.ic_stat_sys_single_sim1),
            title = stringResource(R.string.icon_tuner_single_mobile_sim1),
            summary = stringResource(R.string.icon_tuner_single_mobile_summary),
            options = visibilityOptions,
        )
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SINGLE_MOBILE_SIM2,
            icon = ImageIcon(R.drawable.ic_stat_sys_single_sim2),
            title = stringResource(R.string.icon_tuner_single_mobile_sim2),
            summary = stringResource(R.string.icon_tuner_single_mobile_summary),
            options = visibilityOptions,
        )
    }
    itemPreferenceGroup(
        key = "STACKED_SIGNAL",
        titleRes = R.string.ui_title_icon_detail_stacked_signal,
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE,
            title = stringResource(R.string.icon_detail_stacked_signal_style_single),
            options = signalIconStyle,
        )
        AnimatedColumn(stackedState.signal.singleStyle == 2) {
            TextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_single_val),
                summary = stackedState.signal.singleSVGName.ifBlank {
                    stringResource(R.string.icon_detail_stacked_signal_style_val_file)
                },
                onClick = { onAction(StackedMobileAction.ImportSVGFile(isStacked = false)) }
            )
            EditTextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_single_val),
                summary = stringResource(R.string.icon_detail_stacked_signal_style_val_clipboard),
                text = stackedState.signal.singleSVG,
                dialogMessage = stringResource(R.string.icon_detail_stacked_signal_style_single_msg),
                valuePosition = ValuePosition.Hidden,
                onTextChange = { if (it.isNotBlank()) onAction(StackedMobileAction.ValidateAndUpdateSvg(it, isStacked = false)) }
            )
        }
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED,
            title = stringResource(R.string.icon_detail_stacked_signal_style_stacked),
            options = signalIconStyle,
        )
        AnimatedColumn(stackedState.signal.stackedStyle == 2) {
            TextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_stacked_val),
                summary = stackedState.signal.stackedSVGName.ifBlank {
                    stringResource(R.string.icon_detail_stacked_signal_style_val_file)
                },
                onClick = { onAction(StackedMobileAction.ImportSVGFile(isStacked = true)) }
            )
            EditTextPreference(
                title = stringResource(R.string.icon_detail_stacked_signal_style_stacked_val),
                summary = stringResource(R.string.icon_detail_stacked_signal_style_val_clipboard),
                text = stackedState.signal.stackedSVG,
                dialogMessage = stringResource(R.string.icon_detail_stacked_signal_style_stacked_msg),
                valuePosition = ValuePosition.Hidden,
                onTextChange = { if (it.isNotBlank()) onAction(StackedMobileAction.ValidateAndUpdateSvg(it, isStacked = true)) }
            )
        }
        SeekBarPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_FG,
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_fg),
            min = 0.0f,
            max = 1.0f,
        )
        SeekBarPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_BG,
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_bg),
            min = 0.0f,
            max = 1.0f,
        )
        SeekBarPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_ERROR,
            title = stringResource(R.string.icon_detail_stacked_signal_alpha_error),
            min = 0.0f,
            max = 1.0f,
        )
    }
    itemPreferenceGroup(
        key = "STACKED_TYPE",
        titleRes = R.string.ui_title_icon_detail_stacked_type,
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE,
            title = stringResource(R.string.icon_detail_stacked_type_font),
            summary = stringResource(R.string.icon_detail_stacked_type_font_tips),
            options = dropdownEntriesTypeFont,
        )
        AnimatedColumn(stackedState.font.mode == 1) {
            val displayName = stackedState.font.displayName
            val isDefault = displayName == Constants.VARIABLE_FONT_DEFAULT_PATH
            val isManualPath = displayName.contains("/") && !isDefault
            val isImported = !displayName.contains("/") && !isDefault

            TextPreference(
                title = stringResource(R.string.font_general_path),
                summary = if (isImported) displayName else stringResource(R.string.font_general_path_file),
                onClick = { onAction(StackedMobileAction.ImportLocalFont) }
            )
            EditTextPreference(
                title = stringResource(R.string.font_general_path),
                summary = if (isManualPath) displayName else stringResource(R.string.font_general_path_path),
                text = if (isManualPath) displayName else "",
                dialogMessage = stringResource(R.string.font_general_path_tips),
                valuePosition = ValuePosition.Hidden,
                onTextChange = { onAction(StackedMobileAction.ApplyManualPath(it)) }
            )
        }
        AnimatedVisibility(stackedState.font.mode == 2 || stackedState.font.mode == 3) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.StackedMobile.TYPE_WIDTH_CONDENSED,
                title = stringResource(R.string.icon_detail_stacked_type_font_width_condensed),
                min = 10,
                max = 200,
                valueFormatter = { "${it}%" },
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_LIST,
            title = stringResource(R.string.icon_detail_cellular_type_map),
        )
        AnimatedVisibility(mobileState.customTypeMap) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_cellular_type_map_value),
                text = mobileState.typeMapString,
                dialogMessage = stringResource(R.string.icon_detail_cellular_type_map_msg),
                dialogHint = Constants.CELLULAR_TYPE_LIST,
                valuePosition = ValuePosition.Hidden,
                onTextChange = { if (it.isNotBlank()) onAction(StackedMobileAction.UpdateCustomTypeMap(it)) }
            )
        }
    }
    itemPreferenceGroup(
        key = "STACKED_SMALL_TYPE",
        titleRes = R.string.ui_title_icon_detail_stacked_small_type,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE,
            title = stringResource(R.string.icon_detail_stacked_small_type_show_single),
            summary = stringResource(R.string.icon_detail_stacked_small_type_show_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_STACKED,
            title = stringResource(R.string.icon_detail_stacked_small_type_show_stacked),
            summary = stringResource(R.string.icon_detail_stacked_small_type_show_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ROAMING,
            title = stringResource(R.string.icon_detail_stacked_small_type_roaming),
        )
        EditTextPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SIZE,
            title = stringResource(R.string.icon_detail_stacked_small_type_size),
            isValueValid = { it > 0.0f },
        )
        AnimatedVisibility(stackedState.font.mode != 0) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_FONT_WEIGHT,
                title = stringResource(R.string.icon_detail_stacked_small_type_weight),
                min = 1,
                max = 1000,
            )
        }
    }
    itemPreferenceGroup(
        key = "STACKED_LARGE_TYPE",
        titleRes = R.string.ui_title_icon_detail_stacked_large_type,
        position = ItemPosition.Last
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT,
            title = stringResource(R.string.icon_detail_stacked_large_type_hide_disconnect),
            summary = stringResource(R.string.icon_detail_stacked_large_type_hide_disconnect_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI,
            title = stringResource(R.string.icon_detail_stacked_large_type_hide_wifi),
        )
        EditTextPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_SIZE,
            title = stringResource(R.string.icon_detail_stacked_large_type_size),
            isValueValid = { it > 0.0f },
        )
        AnimatedVisibility(stackedState.font.mode != 0) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_FONT_WEIGHT,
                title = stringResource(R.string.icon_detail_stacked_large_type_weight),
                min = 1,
                max = 1000,
            )
        }
        EditTextPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_VERTICAL_OFFSET,
            title = stringResource(R.string.icon_detail_stacked_large_type_vertical_offset),
            summary = stringResource(R.string.icon_detail_stacked_large_type_vertical_offset_tips),
        )
        EditTextPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_START_VAL,
            title = stringResource(R.string.icon_detail_battery_padding_start),
        )
        EditTextPreference(
            key = Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_END_VAL,
            title = stringResource(R.string.icon_detail_battery_padding_end),
        )
    }
}