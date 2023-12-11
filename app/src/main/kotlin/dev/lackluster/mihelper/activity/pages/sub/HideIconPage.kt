package dev.lackluster.mihelper.activity.pages.sub

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("hide_icon")
class HideIconPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_status_bar_hide_icon)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_hide_icon_carrier)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_carrier_one),
            SwitchV(PrefKey.STATUSBAR_HIDE_CARRIER_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_carrier_two),
            SwitchV(PrefKey.STATUSBAR_HIDE_CARRIER_TWO)
        )
        Line()
        TitleText(textId = R.string.ui_title_hide_icon_mobile)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_sim_one),
            SwitchV(PrefKey.STATUSBAR_HIDE_SIM_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_sim_two),
            SwitchV(PrefKey.STATUSBAR_HIDE_SIM_TWO)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_no_sim),
            SwitchV(PrefKey.STATUSBAR_HIDE_NO_SIM)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_mobile_activity),
            SwitchV(PrefKey.STATUSBAR_HIDE_MOBILE_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_mobile_type),
            SwitchV(PrefKey.STATUSBAR_HIDE_MOBILE_TYPE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_small),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_SMALL)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_large),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_LARGE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_new),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_NEW)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_no_service),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_NO_SERVICE)
        )
        Line()
        TitleText(textId = R.string.ui_title_hide_icon_wifi)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi_activity),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi_type),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI_TYPE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi_slave),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI_SLAVE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hotspot),
            SwitchV(PrefKey.STATUSBAR_HIDE_HOTSPOT)
        )
        Line()
        TitleText(textId = R.string.ui_title_hide_icon_connectivity)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_flight_mode),
            SwitchV(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_gps),
            SwitchV(PrefKey.STATUSBAR_HIDE_GPS)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_bluetooth),
            SwitchV(PrefKey.STATUSBAR_HIDE_BLUETOOTH)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_bluetooth_battery),
            SwitchV(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_nfc),
            SwitchV(PrefKey.STATUSBAR_HIDE_NFC)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_vpn),
            SwitchV(PrefKey.STATUSBAR_HIDE_VPN)
        )
        Line()
        TitleText(textId = R.string.ui_title_hide_icon_other)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_battery),
            SwitchV(PrefKey.STATUSBAR_HIDE_BATTERY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_battery_mark),
            SwitchV(PrefKey.STATUSBAR_HIDE_BATTERY_PERCENT)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_charge),
            SwitchV(PrefKey.STATUSBAR_HIDE_CHARGE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_alarm),
            SwitchV(PrefKey.STATUSBAR_HIDE_ALARM)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_headset),
            SwitchV(PrefKey.STATUSBAR_HIDE_HEADSET)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_volume),
            SwitchV(PrefKey.STATUSBAR_HIDE_VOLUME)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_zen),
            SwitchV(PrefKey.STATUSBAR_HIDE_ZEN)
        )
    }
}