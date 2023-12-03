package dev.lackluster.mihelper.activity.pages.scope

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("scope_systemui")
class SystemUIPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_systemui)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_systemui_status_bar)

        Line()
        TitleText(textId = R.string.ui_title_systemui_notification_center)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_notif_disable_whitelist),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_NO_WHITELIST)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_freeform,
                tipsId = R.string.systemui_notif_freeform_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_FREEFORM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_settings,
                tipsId = R.string.systemui_notif_redirect_settings_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_optimize,
                tipsId = R.string.systemui_botif_media_control_optimize_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_OPTIMIZE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_monet,
                tipsId = R.string.systemui_botif_media_control_monet_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_MONET)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_monet_reverse,
                tipsId = R.string.systemui_botif_media_control_monet_reverse_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_MONET_REVERSE)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_control_center)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_control_bluetooth_restrict,
                tipsId = R.string.systemui_control_bluetooth_restrict_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_CONTROL_BLUETOOTH)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_lock_screen)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_lock_screen_time_font,
                tipsId = R.string.systemui_lock_screen_time_font_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_TIME_FONT)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_lock_screen_date_font,
                tipsId = R.string.systemui_lock_screen_date_font_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_DATE_FONT)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_unlock_tip),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_UNLOCK_TIP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_disturb),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_DISTURB)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_double_tap),
            SwitchV(PrefKey.SYSTEMUI_DOUBLE_TAP_TO_SLEEP)
        )
    }
}