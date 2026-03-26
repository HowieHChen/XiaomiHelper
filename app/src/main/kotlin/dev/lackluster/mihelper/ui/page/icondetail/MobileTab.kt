package dev.lackluster.mihelper.ui.page.icondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.model.MobileState

fun LazyListScope.mobileTabContent(
    isVisible: Boolean,
    mobileState: MobileState,
    validateAndUpdateCustomTypeMap: (String, String) -> Unit,
    updateMobilePreference: (String, Any) -> Unit,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "MOBILE_GENERAL",
        titleResId = R.string.ui_title_icon_detail_cellular_general,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_hide_sim_auto),
            summary = stringResource(R.string.icon_detail_cellular_hide_sim_auto_tips),
            value = mobileState.hideSimAuto,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_SIM_AUTO, it) }
        )
        AnimatedVisibility(!mobileState.hideSimAuto) {
            Column {
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_hide_sim_one),
                    value = mobileState.hideSimOne,
                    onCheckedChange = { updateMobilePreference(IconTuner.HIDE_SIM_ONE, it) }
                )
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_hide_sim_two),
                    value = mobileState.hideSimTwo,
                    onCheckedChange = { updateMobilePreference(IconTuner.HIDE_SIM_TWO, it) }
                )
            }
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_hide_activity),
            value = mobileState.hideActivity,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_ACTIVITY, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_hide_type),
            value = mobileState.hideSmallType,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_TYPE, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_hide_roam_global),
            summary = stringResource(R.string.icon_detail_cellular_hide_roam_global_tips),
            value = mobileState.hideRoamGlobal,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_ROAM_GLOBAL, it) }
        )
        AnimatedVisibility(!mobileState.hideRoamGlobal) {
            Column {
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_hide_roam),
                    value = mobileState.hideLargeRoam,
                    onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_ROAM, it) }
                )
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_hide_roam_small),
                    value = mobileState.hideSmallRoam,
                    onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_SMALL_ROAM, it) }
                )
            }
        }
    }
    itemPreferenceGroup(
        key = "MOBILE_TYPE",
        titleResId = R.string.ui_title_icon_detail_cellular_type,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_single),
            value = mobileState.separateType,
            onCheckedChange = { updateMobilePreference(IconTuner.CELLULAR_TYPE_SINGLE, it) }
        )
        AnimatedVisibility(mobileState.separateType) {
            Column {
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_single_swap),
                    summary = stringResource(R.string.icon_detail_cellular_single_swap_tips),
                    value = mobileState.rightSeparateType,
                    onCheckedChange = { updateMobilePreference(IconTuner.CELLULAR_TYPE_SINGLE_SWAP, it) }
                )
                SwitchPreference(
                    title = stringResource(R.string.icon_detail_cellular_single_size),
                    value = mobileState.separateTypeSize.enabled,
                    onCheckedChange = { updateMobilePreference(IconTuner.CELLULAR_TYPE_SINGLE_SIZE, it) }
                )
                AnimatedVisibility(mobileState.separateTypeSize.enabled) {
                    EditTextPreference(
                        title = stringResource(R.string.icon_detail_cellular_single_size_value),
                        value = mobileState.separateTypeSize.size,
                        defValue = 14f,
                        dataType = EditTextDataType.FLOAT,
                        isValueValid = { (it as? Float ?: -1.0f) > 0.0f },
                        onValueChange = { _, value -> updateMobilePreference(IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL, value as Float)}
                    )
                }
            }
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
        key = "MOBILE_FONT",
        titleResId = R.string.ui_title_icon_detail_font_weight,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_fw_type),
            value = mobileState.smallTypeFont.enabled,
            onCheckedChange = { updateMobilePreference(FontWeight.CELLULAR_TYPE, it) }
        )
        AnimatedVisibility(mobileState.smallTypeFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_cellular_fw_type_weight),
                value = mobileState.smallTypeFont.weight,
                defValue = 660,
                min = 1,
                max = 1000,
                onValueChange = { updateMobilePreference(FontWeight.CELLULAR_TYPE_VAL, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_cellular_fw_type_single),
            value = mobileState.separateTypeFont.enabled,
            onCheckedChange = { updateMobilePreference(FontWeight.CELLULAR_TYPE_SINGLE, it) }
        )
        AnimatedVisibility(mobileState.separateTypeFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_cellular_fw_type_single_weight),
                value = mobileState.separateTypeFont.weight,
                defValue = 400,
                min = 1,
                max = 1000,
                onValueChange = { updateMobilePreference(FontWeight.CELLULAR_TYPE_SINGLE_VAL, it) }
            )
        }
    }
    itemPreferenceGroup(
        key = "MOBILE_OTHERS",
        titleResId = R.string.ui_title_icon_detail_other,
        last = true,
    ) {
        SwitchPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_vowifi),
            title = stringResource(R.string.icon_detail_cellular_hide_vowifi),
            value = mobileState.hideVoWifi,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_VO_WIFI, it) }
        )
        SwitchPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_signal_volte),
            title = stringResource(R.string.icon_detail_cellular_hide_volte),
            value = mobileState.hideVoLte,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_VOLTE, it) }
        )
        SwitchPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_volte_no_service),
            title = stringResource(R.string.icon_detail_cellular_hide_volte_no_service),
            value = mobileState.hideVoLteNoService,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_VOLTE_NO_SERVICE, it) }
        )
        SwitchPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_speech_hd),
            title = stringResource(R.string.icon_detail_cellular_hide_speech_hd),
            value = mobileState.hideSpeechHd,
            onCheckedChange = { updateMobilePreference(IconTuner.HIDE_CELLULAR_SPEECH_HD, it) }
        )
    }
}