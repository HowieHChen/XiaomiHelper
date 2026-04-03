package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.MobileState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences

fun LazyListScope.mobileTabContent(
    isVisible: Boolean,
    mobileState: MobileState,
    validateAndUpdateCustomTypeMap: (String) -> Unit,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "MOBILE_GENERAL",
        titleRes = R.string.ui_title_icon_detail_cellular_general,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_AUTO,
            title = stringResource(R.string.icon_detail_cellular_hide_sim_auto),
            summary = stringResource(R.string.icon_detail_cellular_hide_sim_auto_tips),
        )
        AnimatedColumn(!mobileState.hideSimAuto) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_ONE,
                title = stringResource(R.string.icon_detail_cellular_hide_sim_one),
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_TWO,
                title = stringResource(R.string.icon_detail_cellular_hide_sim_two),
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ACTIVITY,
            title = stringResource(R.string.icon_detail_cellular_hide_activity),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_TYPE,
            title = stringResource(R.string.icon_detail_cellular_hide_type),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_ROAM_GLOBAL,
            title = stringResource(R.string.icon_detail_cellular_hide_roam_global),
            summary = stringResource(R.string.icon_detail_cellular_hide_roam_global_tips),
        )
        AnimatedColumn(!mobileState.hideRoamGlobal) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_LARGE_ROAM,
                title = stringResource(R.string.icon_detail_cellular_hide_roam),
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SMALL_ROAM,
                title = stringResource(R.string.icon_detail_cellular_hide_roam_small),
            )
        }
    }
    itemPreferenceGroup(
        key = "MOBILE_TYPE",
        titleRes = R.string.ui_title_icon_detail_cellular_type,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.USE_CELLULAR_TYPE_SINGLE,
            title = stringResource(R.string.icon_detail_cellular_single),
        )
        AnimatedColumn(mobileState.separateType) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SWAP_INDEX,
                title = stringResource(R.string.icon_detail_cellular_single_swap),
                summary = stringResource(R.string.icon_detail_cellular_single_swap_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_SINGLE_SIZE,
                title = stringResource(R.string.icon_detail_cellular_single_size),
            )
            AnimatedVisibility(mobileState.separateTypeSize.enabled) {
                EditTextPreference(
                    key = Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SIZE_VAL,
                    title = stringResource(R.string.icon_detail_cellular_single_size_value),
                    isValueValid = { it > 0.0f },
                )
            }
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
                onTextChange = { if (it.isNotBlank()) validateAndUpdateCustomTypeMap(it) }
            )
        }
    }
    itemPreferenceGroup(
        key = "MOBILE_FONT",
        titleRes = R.string.ui_title_icon_detail_font_weight,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE,
            title = stringResource(R.string.icon_detail_cellular_fw_type),
        )
        AnimatedVisibility(mobileState.smallTypeFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_WEIGHT,
                title = stringResource(R.string.icon_detail_cellular_fw_type_weight),
                min = 1,
                max = 1000,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE_SINGLE,
            title = stringResource(R.string.icon_detail_cellular_fw_type_single),
        )
        AnimatedVisibility(mobileState.separateTypeFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_SINGLE_WEIGHT,
                title = stringResource(R.string.icon_detail_cellular_fw_type_single_weight),
                min = 1,
                max = 1000,
            )
        }
    }
    itemPreferenceGroup(
        key = "MOBILE_OTHERS",
        titleRes = R.string.ui_title_icon_detail_other,
        position = ItemPosition.Last,
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VO_WIFI,
            icon = ImageIcon(R.drawable.ic_stat_sys_vowifi),
            title = stringResource(R.string.icon_detail_cellular_hide_vowifi),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE,
            icon = ImageIcon(R.drawable.ic_stat_sys_signal_volte),
            title = stringResource(R.string.icon_detail_cellular_hide_volte),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_VOLTE_NO_SERVICE,
            icon = ImageIcon(R.drawable.ic_stat_sys_volte_no_service),
            title = stringResource(R.string.icon_detail_cellular_hide_volte_no_service),
        )
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_CELLULAR_SPEECH_HD,
            icon = ImageIcon(R.drawable.ic_stat_sys_speech_hd),
            title = stringResource(R.string.icon_detail_cellular_hide_speech_hd),
        )
    }
}