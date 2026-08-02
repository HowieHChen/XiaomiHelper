package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.WlanState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val wifiStandardModeOptions = listOf(
    DropDownOption(0, R.string.icon_detail_wifi_standard_default),
    DropDownOption(1, R.string.icon_detail_wifi_standard_hide),
    DropDownOption(2, R.string.icon_detail_wifi_standard_show),
    DropDownOption(3, R.string.icon_detail_wifi_standard_custom),
)

fun LazyListScope.wlanTabContent(
    isVisible: Boolean,
    wlanState: WlanState,
    validateAndUpdateWifiStandardMap: (String) -> Unit,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "WLAN",
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.WIFI_STANDARD_MODE,
            title = stringResource(R.string.icon_detail_wifi_standard),
            options = wifiStandardModeOptions,
        )
        AnimatedVisibility(wlanState.wifiStandardMode == 3) {
            EditTextPreference(
                title = stringResource(R.string.icon_detail_wifi_standard_map),
                text = wlanState.wifiStandardMap,
                dialogMessage = stringResource(R.string.icon_detail_wifi_standard_map_msg),
                dialogHint = "4,5,6,7,8",
                valuePosition = ValuePosition.Hidden,
                onTextChange = validateAndUpdateWifiStandardMap,
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_WIFI_PADDING_HORIZON,
            title = stringResource(R.string.icon_detail_wifi_padding_custom),
        )
        AnimatedColumn(wlanState.customPadding) {
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.WIFI_PADDING_START_VAL,
                title = stringResource(R.string.icon_detail_wifi_padding_start),
            )
            EditTextPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.WIFI_PADDING_END_VAL,
                title = stringResource(R.string.icon_detail_wifi_padding_end),
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_ACTIVITY,
            title = stringResource(R.string.icon_detail_wifi_hide_activity),
        )
        AnimatedVisibility(wlanState.hideWifiStandard && !wlanState.hideWifiActivity) {
            SwitchPreference(
                key = Preferences.SystemUI.StatusBar.IconDetail.WIFI_ACTIVITY_RIGHT,
                title = stringResource(R.string.icon_detail_wifi_right_activity),
            )
        }
    }
    itemPreferenceGroup(
        key = "WLAN_OTHERS",
        titleRes = R.string.ui_title_icon_detail_other,
        position = ItemPosition.Last
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_UNAVAILABLE,
            icon = ImageIcon(R.drawable.ic_stat_sys_wifi_unavailable),
            title = stringResource(R.string.icon_detail_wifi_hide_unavailable),
        )
    }
}
