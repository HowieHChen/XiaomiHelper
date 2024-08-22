package dev.lackluster.mihelper.activity.pages.bezier

import cn.fkj233.ui.activity.annotation.BMPage
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages

@BMPage(Pages.BEZIER_CURVE_FOLDER, hideMenu = false)
class AnimCurvePreviewFolderPage : AnimCurvePreviewBasePage() {
    override fun getKeyPrefix(): String = "folder"

    override fun getPageTitleId(): Int = R.string.ui_title_home_refactor_folder
}