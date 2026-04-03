package dev.lackluster.mihelper.app.screen.systemui.icon.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.WlanState
import dev.lackluster.mihelper.data.preference.Preferences

fun LazyListScope.wlanTabContent(
    isVisible: Boolean,
    wlanState: WlanState,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "WLAN",
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_STANDARD,
            title = stringResource(R.string.icon_detail_wifi_hide_standard),
        )
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