package dev.lackluster.mihelper.ui.page.icondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.ui.model.WlanState

fun LazyListScope.wlanTabContent(
    isVisible: Boolean,
    wlanState: WlanState,
    updateWlanPreference: (String, Any) -> Unit,
) {
    if (!isVisible) return
    itemPreferenceGroup(
        key = "WLAN",
    ) {
        SwitchPreference(
            title = stringResource(R.string.icon_detail_wifi_hide_standard),
            value = wlanState.hideWifiStandard,
            onCheckedChange = { updateWlanPreference(IconTuner.HIDE_WIFI_STANDARD, it) }
        )
        SwitchPreference(
            title = stringResource(R.string.icon_detail_wifi_hide_activity),
            value = wlanState.hideWifiActivity,
            onCheckedChange = { updateWlanPreference(IconTuner.HIDE_WIFI_ACTIVITY, it) }
        )
        AnimatedVisibility(wlanState.hideWifiStandard && !wlanState.hideWifiActivity) {
            SwitchPreference(
                title = stringResource(R.string.icon_detail_wifi_right_activity),
                value = wlanState.rightWifiActivity,
                onCheckedChange = { updateWlanPreference(IconTuner.WIFI_ACTIVITY_RIGHT, it) },
            )
        }
    }
    itemPreferenceGroup(
        key = "WLAN_OTHERS",
        titleResId = R.string.ui_title_icon_detail_other,
        last = true,
    ) {
        SwitchPreference(
            icon = ImageIcon(iconRes = R.drawable.ic_stat_sys_wifi_unavailable),
            title = stringResource(R.string.icon_detail_wifi_hide_unavailable),
            value = wlanState.hideWifiUnavailable,
            onCheckedChange = { updateWlanPreference(IconTuner.HIDE_WIFI_UNAVAILABLE, it) },
        )
    }
}