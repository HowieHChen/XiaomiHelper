package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("icon_position")
class IconPositionPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_status_bar_icon_position)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_icon_position_swap)
        val swapBatteryBinding = GetDataBinding({
            !MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_swap_battery_percent),
            SwitchV(PrefKey.STATUSBAR_SWAP_BATTERY_PERCENT),
            dataBindingRecv = swapBatteryBinding.getRecv(1)
        )
        val swapMobileWIFIBinding = GetDataBinding({
            !(MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_ONE, false)
                    && MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_TWO, false))
                    && !MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_WIFI, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_swap_mobile_wifi),
            SwitchV(PrefKey.STATUSBAR_SWAP_MOBILE_WIFI),
            dataBindingRecv = swapMobileWIFIBinding.getRecv(1)
        )
        TitleText(textId = R.string.ui_title_icon_position_right)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_alarm),
            SwitchV(PrefKey.STATUSBAR_RIGHT_ALARM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_headset),
            SwitchV(PrefKey.STATUSBAR_RIGHT_HEADSET)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_net_speed),
            SwitchV(PrefKey.STATUSBAR_RIGHT_NETSPEED)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_nfc),
            SwitchV(PrefKey.STATUSBAR_RIGHT_NFC)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_volume),
            SwitchV(PrefKey.STATUSBAR_RIGHT_VOLUME)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_right_zen),
            SwitchV(PrefKey.STATUSBAR_RIGHT_ZEN)
        )
    }
}