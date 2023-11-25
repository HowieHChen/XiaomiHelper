package dev.lackluster.mihelper.activity.pages.main

import android.content.ComponentName
import android.content.pm.PackageManager
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.PrefKey

@BMMainPage()
class MainPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_main)
    }
    override fun onCreate() {
        activity.title
        TitleText(textId = R.string.ui_title_general)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.switch_main
            ), SwitchV(PrefKey.ENABLE_MODULE, true)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.switch_hide_icon),
            SwitchV(PrefKey.HIDE_ICON, onClickListener = {
                activity.packageManager.setComponentEnabledSetting(
                    ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"),
                    if (it) {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    },
                    PackageManager.DONT_KILL_APP
                )
            })
        )
        Line()
        TitleText(textId = R.string.ui_title_scope)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_scope_systemui,
                onClickListener = { showFragment("scope_systemui") })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_scope_android,
                onClickListener = { showFragment("scope_android") })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_scope_security_center,
                onClickListener = { showFragment("scope_security_center") })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_scope_miui_home,
                onClickListener = { showFragment("scope_miui_home") })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_scope_other,
                onClickListener = { showFragment("scope_others") })
        )
        Line()
        TitleText(textId = R.string.ui_title_about)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.ui_about_module,
                onClickListener = { showFragment("scope_about") })
        )
    }

}