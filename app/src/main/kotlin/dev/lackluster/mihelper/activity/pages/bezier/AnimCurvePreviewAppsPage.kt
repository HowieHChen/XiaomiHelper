package dev.lackluster.mihelper.activity.pages.bezier

import cn.fkj233.ui.activity.annotation.BMPage
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages

@BMPage(Pages.BEZIER_CURVE_APPS, hideMenu = false)
class AnimCurvePreviewAppsPage : AnimCurvePreviewBasePage() {
    override fun getKeyPrefix(): String = "apps"
    override fun getPageTitleId(): Int = R.string.ui_title_home_refactor_apps
}