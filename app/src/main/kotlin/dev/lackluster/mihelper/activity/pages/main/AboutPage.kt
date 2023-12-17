package dev.lackluster.mihelper.activity.pages.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.BuildConfig.BUILD_TYPE
import dev.lackluster.mihelper.BuildConfig.VERSION_NAME
import dev.lackluster.mihelper.BuildConfig.VERSION_CODE
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.References

@BMPage("about")
class AboutPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_about)
    }
    override fun onCreate() {
        ImageWithText(
            authorHead = getDrawable(R.mipmap.ic_launcher),
            authorName = getString(R.string.app_name),
            authorTips = "${VERSION_NAME}(${VERSION_CODE})-${BUILD_TYPE}",
            onClickListener = {
                when ((0..2).random()) {
                    0 -> {
                        makeText(activity, R.string.about_easter_egg_toast, LENGTH_SHORT).show()
                    }
                    1 -> {
                        makeText(activity, R.string.xposed_desc, LENGTH_SHORT).show()
                    }
                    2 -> {
                        try {
                            val uri = Uri.parse(getString(R.string.about_easter_egg_url))
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            activity.startActivity(intent)
                        } catch (e: Exception) {
                            makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
        ImageWithText(
            authorHead = getDrawable(R.mipmap.ic_yukihookapi),
            authorName = getString(R.string.about_yuki),
            authorTips = getString(R.string.about_yuki_tips),
            onClickListener = {
                try {
                    val uri = Uri.parse(getString(R.string.about_yuki_url))
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                }
            }
        )
        Line()
        TitleText(textId = R.string.ui_title_about_developer)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.about_author,
                tipsId = R.string.about_author_tips,
                onClickListener = {
                    try {
                        val uri = Uri.parse(getString(R.string.about_author_url))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                    }
                })
        )
        Line()
        TitleText(textId = R.string.ui_title_about_open_source)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.about_repository,
                onClickListener = {
                    try {
                        val uri = Uri.parse(activity.getString(R.string.about_repository_url))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                    }
                })
        )
        Line()
        TitleText(textId = R.string.ui_title_about_reference)
        for (project in References.list) {
            TextSummaryWithArrow(
                TextSummaryV(
                    text = project.first,
                    tips = project.second,
                    onClickListener = {
                        try {
                            val uri = Uri.parse(project.third)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            activity.startActivity(intent)
                        } catch (e: Exception) {
                            makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                        }
                    })
            )
        }

    }
}