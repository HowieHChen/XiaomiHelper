package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.preference.DropDownMode
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.NetSpeedState
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val networkSpeedStyleOptions = listOf(
    DropDownOption(0, R.string.icon_detail_net_speed_style_default, iconRes = R.drawable.ic_net_speed_style_default),
    DropDownOption(1, R.string.icon_detail_net_speed_style_separate, iconRes = R.drawable.ic_net_speed_style_separate),
    DropDownOption(2, R.string.icon_detail_net_speed_style_separate_arrow, iconRes = R.drawable.ic_net_speed_style_separate_arrow),
    DropDownOption(3, R.string.icon_detail_net_speed_style_separate_tri_filled, iconRes = R.drawable.ic_net_speed_style_separate_tri_filled),
    DropDownOption(4, R.string.icon_detail_net_speed_style_separate_tri_outline, iconRes = R.drawable.ic_net_speed_style_separate_tri_outline),
)

private val networkSpeedUnitStyleOptions = listOf(
    DropDownOption(0, R.string.icon_detail_net_speed_unit_style_k),
    DropDownOption(1, R.string.icon_detail_net_speed_unit_style_kb),
    DropDownOption(2, R.string.icon_detail_net_speed_unit_style_kbps),
)

fun LazyListScope.netSpeedTabContent(
    isVisible: Boolean,
    netSpeedState: NetSpeedState,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "NET_SPEED",
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_MODE,
            title = stringResource(R.string.icon_detail_net_speed_style),
            options = networkSpeedStyleOptions,
            mode = DropDownMode.Dialog,
        )
        AnimatedVisibility(netSpeedState.style != 0) {
            DropDownPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_UNIT_MODE,
                title = stringResource(R.string.icon_detail_net_speed_unit_style),
                options = networkSpeedUnitStyleOptions,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_REFRESH,
            title = stringResource(R.string.icon_detail_net_speed_refresh),
            summary = stringResource(R.string.icon_detail_net_speed_refresh_tips),
        )
    }
    itemPreferenceGroup(
        key = "NET_FONT_0",
        titleRes = R.string.ui_title_icon_detail_font_weight,
        position = ItemPosition.Last,
        visible = (netSpeedState.style == 0)
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_NUMBER,
            title = stringResource(R.string.icon_detail_net_speed_fw_num),
        )
        AnimatedVisibility(netSpeedState.numberFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.NET_SPEED_NUMBER_WEIGHT,
                title = stringResource(R.string.icon_detail_net_speed_fw_num_weight),
                min = 1,
                max = 1000,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_UNIT,
            title = stringResource(R.string.icon_detail_net_speed_fw_unit),
        )
        AnimatedVisibility(netSpeedState.unitFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.NET_SPEED_UNIT_WEIGHT,
                title = stringResource(R.string.icon_detail_net_speed_fw_unit_weight),
                min = 1,
                max = 1000,
            )
        }
    }
    itemPreferenceGroup(
        key = "NET_FONT_1",
        titleRes = R.string.ui_title_icon_detail_font_weight,
        position = ItemPosition.Last,
        visible = (netSpeedState.style != 0)
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_SEPARATE,
            title = stringResource(R.string.icon_detail_net_speed_fw_separate),
        )
        AnimatedVisibility(netSpeedState.separateStyleFont.enabled) {
            SeekBarPreference(
                key = Preferences.SystemUI.StatusBar.Font.NET_SPEED_SEPARATE_WEIGHT,
                title = stringResource(R.string.icon_detail_net_speed_fw_separate_weight),
                min = 1,
                max = 1000,
            )
        }
    }
}