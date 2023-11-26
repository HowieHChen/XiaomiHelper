package dev.lackluster.mihelper.activity.pages.scope

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_security_center")
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
        TitleText(textId = R.string.ui_title_security_function)
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

        TitleText(textId = R.string.ui_title_security_interface)
    }
}