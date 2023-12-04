package dev.lackluster.mihelper.activity.pages.scope

import android.content.ComponentName
import android.content.Intent
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_security_center", hideMenu = false)
class SecurityCenterPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(
            if (Device.isPad)
                R.string.ui_page_security_center_pad
            else
                R.string.ui_page_security_center
        )
    }
    override fun onCreate() {
        TitleText(textId =
            if (Device.isPad) R.string.ui_page_security_center_pad
            else R.string.ui_page_security_center
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_skip_warning,
                tipsId = R.string.security_skip_warning_tips
            ),
            SwitchV(PrefKey.SECURITY_SKIP_WARNING)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_skip_open_app,
                tipsId = R.string.security_skip_open_app_tips
            ),
            SwitchV(PrefKey.SECURITY_SKIP_OPEN_APP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_lock_score,
                tipsId = R.string.security_lock_score_tips
            ),
            SwitchV(PrefKey.SECURITY_LOCK_SCORE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_screen_battery,
                tipsId = R.string.security_screen_battery_tips
            ),
            SwitchV(PrefKey.SECURITY_SCREEN_BATTERY)
        )

        TitleText(textId = R.string.ui_scope_power_keeper)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.power_custom_refresh,
            ),
            SwitchV(PrefKey.POWER_CUSTOM_REFRESH)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.power_donot_kill_app,
            ),
            SwitchV(PrefKey.POWER_DONOT_KILL_APP)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.power_battery_optimization,
                tipsId = R.string.power_battery_optimization_tips,
                onClickListener = {
                    try {
                        val intent = Intent()
                        val comp = ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$HighPowerApplicationsActivity"
                        )
                        intent.component = comp
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, activity.getString(R.string.power_battery_optimization_failed_toast), LENGTH_LONG).show()
                    }
                })
        )
    }
}