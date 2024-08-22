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

import androidx.appcompat.content.res.AppCompatResources
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.HeaderData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMMainPage
class MainPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_main)
    }
    override fun onCreate() {
        val liteMode = MIUIActivity.safeSP.getBoolean(Pref.Key.Module.LITE_MODE, false)
        PreferenceCategory(
            null,
            CategoryData(hideTitle = true, hideLine = true)
        ) {
            HeaderPreference(
                DescData(
                    icon = AppCompatResources.getDrawable(activity, R.drawable.ic_header_hyper_helper_gray),
                    titleId = R.string.page_module
                ),
                HeaderData(),
                onClickListener = {
                    showFragment(Pages.MODULE_SETTINGS)
                    this.itemList.clear()
                }
            )
        }
        PreferenceCategory(
            null,
            CategoryData(hideTitle = true)
        ) {
            if (liteMode) {
                initLiteMode()
            } else {
                initFullMode()
            }
        }
        PreferenceCategory(
            null,
            CategoryData(hideTitle = true)
        ) {
            HeaderPreference(
                DescData(
                    AppCompatResources.getDrawable(activity, R.drawable.ic_header_about),
                    titleId = R.string.page_about
                ),
                HeaderData(),
                onClickListener = {
                    showFragment(Pages.ABOUT)
                }
            )
        }
    }

    private fun initFullMode() {
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_systemui),
                titleId = R.string.page_systemui
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.SYSTEM_UI)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_android_green),
                titleId = R.string.page_android
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.SYSTEM_FRAMEWORK)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_home),
                titleId = R.string.page_miui_home
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.MIUI_HOME)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_cleaner),
                titleId = R.string.page_cleaner
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.CLEAN_MASTER)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_security_center),
                titleId = if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.SECURITY_CENTER)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_interconnection),
                titleId = R.string.page_interconnection
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.INTERCONNECTION)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_others),
                titleId = R.string.page_others
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.OTHERS)
            }
        )
    }

    private fun initLiteMode() {
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_systemui),
                titleId = R.string.page_virtual_media_control_style
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.MEDIA_CONTROL)
            }
        )
        HeaderPreference(
            DescData(
                AppCompatResources.getDrawable(activity, R.drawable.ic_header_home),
                titleId = R.string.page_virtual_home_refactor
            ),
            HeaderData(),
            onClickListener = {
                showFragment(Pages.HOME_REFACTOR)
            }
        )
    }
}