/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.activity.pages.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.HeaderData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.mihelper.BuildConfig.BUILD_TYPE
import dev.lackluster.mihelper.BuildConfig.VERSION_NAME
import dev.lackluster.mihelper.BuildConfig.VERSION_CODE
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.References
import dev.lackluster.mihelper.utils.factory.dp

@BMPage(Pages.ABOUT)
class AboutPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_about)
    }
    override fun onCreate() {
        PreferenceCategory(
            null,
            CategoryData(hideTitle = true, hideLine = true)
        ) {
            HeaderPreference(
                DescData(
                    icon = getDrawable(R.mipmap.ic_launcher),
                    title = getString(R.string.app_name),
                    summary = "${VERSION_NAME}(${VERSION_CODE})-${BUILD_TYPE}"
                ),
                HeaderData(
                    largeIcon = true,
                    corneRadius = 30.dp(this.activity)
                ),
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
                            } catch (t: Throwable) {
                                makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            HeaderPreference(
                DescData(
                    icon = getDrawable(R.mipmap.ic_yukihookapi),
                    titleId = R.string.about_yuki,
                    summaryId = R.string.about_yuki_tips
                ),
                HeaderData(
                    largeIcon = true,
                    corneRadius = 30.dp(this.activity)
                ),
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
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_about_developer),
            CategoryData(),
        ) {
            TextPreference(
                DescData(
                    titleId = R.string.about_author,
                    summaryId = R.string.about_author_tips
                ),
                TextData(),
                onClickListener = {
                    try {
                        val uri = Uri.parse(getString(R.string.about_author_url))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                    }
                }
            )
            TextPreference(
                DescData(
                    titleId = R.string.about_donate,
                    summaryId = R.string.about_donate_tips
                ),
                TextData(),
                onClickListener = {
                    try {
                        val uri = Uri.parse(getString(R.string.about_donate_url))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                    }
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_about_open_source),
            CategoryData(),
        ) {
            TextPreference(
                DescData(
                    titleId = R.string.about_repository
                ),
                TextData(),
                onClickListener = {
                    try {
                        val uri = Uri.parse(getString(R.string.about_repository_url))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                    }
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_about_reference),
            CategoryData()
        ) {
            for (project in References.list) {
                TextPreference(
                    DescData(
                        title = project.first,
                        summary = project.second
                    ),
                    TextData(),
                    onClickListener = {
                        try {
                            val uri = Uri.parse(project.third)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            activity.startActivity(intent)
                        } catch (e: Exception) {
                            makeText(activity, R.string.about_jump_error_toast, LENGTH_SHORT).show()
                        }
                    }
                )
            }
//            FilterSortViewWidget(
//                FilterSortViewData(
//                   arrayOf(
//                       FilterSortViewData.TabViewData(
//                           text = "Tab1",
//                           onClickListener = {
//                               Toast.makeText(it.context, "Tab1 Clicked", Toast.LENGTH_SHORT).show()
//                           },
//                           indicator = AppCompatResources.getDrawable(activity, R.drawable.ic_header_systemui),
//                           indicatorVisibility = View.VISIBLE
//                       ),
//                       FilterSortViewData.TabViewData(
//                           text = "Tab2",
//                           onClickListener = {
//                               Toast.makeText(it.context, "Tab2 Clicked", Toast.LENGTH_SHORT).show()
//                           },
//                           indicator = AppCompatResources.getDrawable(activity, R.drawable.ic_header_android_green),
//                           indicatorVisibility = View.GONE
//                       ),
//                       FilterSortViewData.TabViewData(
//                           text = "Tab3",
//                           onClickListener = {
//                               showFragment("BezierCurveAppsPage")
//                           }
//                       ),
//                   )
//                )
//            )
        }
    }
}