package dev.lackluster.mihelper.ui.page.icondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownMode
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.model.NetSpeedState

fun LazyListScope.netSpeedTabContent(
    isVisible: Boolean,
    netSpeedState: NetSpeedState,
    updateNetSpeedPreference: (String, Any) -> Unit,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "NET_SPEED",
    ) {
        val dropdownEntriesNetworkSpeed = listOf(
            DropDownEntry(
                title = stringResource(R.string.icon_detail_net_speed_style_default),
                iconRes = R.drawable.ic_net_speed_style_default
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_net_speed_style_separate),
                iconRes = R.drawable.ic_net_speed_style_separate
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_net_speed_style_separate_arrow),
                iconRes = R.drawable.ic_net_speed_style_separate_arrow
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_net_speed_style_separate_tri_filled),
                iconRes = R.drawable.ic_net_speed_style_separate_tri_filled
            ),
            DropDownEntry(
                title = stringResource(R.string.icon_detail_net_speed_style_separate_tri_outline),
                iconRes = R.drawable.ic_net_speed_style_separate_tri_outline
            ),
        )
        val dropdownEntriesNetworkSpeedUnit = listOf(
            DropDownEntry(title = stringResource(R.string.icon_detail_net_speed_unit_style_k)),
            DropDownEntry(title = stringResource(R.string.icon_detail_net_speed_unit_style_kb)),
            DropDownEntry(title = stringResource(R.string.icon_detail_net_speed_unit_style_kbps)),
        )
        DropDownPreference(
            title = stringResource(R.string.icon_detail_net_speed_style),
            entries = dropdownEntriesNetworkSpeed,
            value = netSpeedState.style,
            mode = DropDownMode.Dialog,
            onSelectedIndexChange = { updateNetSpeedPreference(IconTuner.NET_SPEED_MODE, it) }
        )
        AnimatedVisibility(netSpeedState.style != 0) {
            DropDownPreference(
                title = stringResource(R.string.icon_detail_net_speed_unit_style),
                entries = dropdownEntriesNetworkSpeedUnit,
                value = netSpeedState.unitStyle,
                onSelectedIndexChange = { updateNetSpeedPreference(IconTuner.NET_SPEED_UNIT_MODE, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_net_speed_refresh),
            summary = stringResource(R.string.icon_detail_net_speed_refresh_tips),
            value = netSpeedState.refreshPerSecond,
            onCheckedChange = { updateNetSpeedPreference(IconTuner.NET_SPEED_REFRESH, it) }
        )
    }
    itemPreferenceGroup(
        key = "NET_SPEED_FONT_0",
        titleResId = R.string.ui_title_icon_detail_font_weight,
        last = true,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_net_speed_fw_num),
            value = netSpeedState.numberFont.enabled,
            onCheckedChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_NUMBER, it) }
        )
        AnimatedVisibility(netSpeedState.numberFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_net_speed_fw_num_weight),
                value = netSpeedState.numberFont.weight,
                defValue = 630,
                min = 1,
                max = 1000,
                onValueChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_NUMBER_VAL, it) }
            )
        }
        SwitchPreference(
            title = stringResource(R.string.icon_detail_net_speed_fw_unit),
            value = netSpeedState.unitFont.enabled,
            onCheckedChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_UNIT, it) }
        )
        AnimatedVisibility(netSpeedState.unitFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_net_speed_fw_unit_weight),
                value = netSpeedState.unitFont.weight,
                defValue = 630,
                min = 1,
                max = 1000,
                onValueChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_UNIT_VAL, it) }
            )
        }
    }
    itemPreferenceGroup(
        key = "NET_SPEED_FONT_OTHERS",
        titleResId = R.string.ui_title_icon_detail_font_weight,
        last = true,
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_net_speed_fw_separate),
            value = netSpeedState.separateStyleFont.enabled,
            onCheckedChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_SEPARATE, it) }
        )
        AnimatedVisibility(netSpeedState.separateStyleFont.enabled) {
            SeekBarPreference(
                title = stringResource(R.string.icon_detail_net_speed_fw_separate_weight),
                value = netSpeedState.separateStyleFont.weight,
                defValue = 630,
                min = 1,
                max = 1000,
                onValueChange = { updateNetSpeedPreference(FontWeight.NET_SPEED_SEPARATE_VAL, it) }
            )
        }
    }
}