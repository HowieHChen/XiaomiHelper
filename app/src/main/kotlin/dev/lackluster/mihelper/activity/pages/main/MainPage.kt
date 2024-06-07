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
import cn.fkj233.ui.activity.view.TextSummaryV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMMainPage()
class MainPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_main)
    }
    override fun onCreate() {
        val liteMode = MIUIActivity.safeSP.getBoolean(Pref.Key.Module.LITE_MODE, false)
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_hyper_helper_gray)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_module),
            onClickListener = {
                showFragment("page_module")
                this.itemList.clear()
            }
        )
        Line()
        if (liteMode) {
            initLiteMode()
        } else {
            initFullMode()
        }
        Line()
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_about)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_about),
            onClickListener = {
                showFragment("page_about")
            }
        )
    }

    private fun initFullMode() {
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_systemui)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_systemui),
            onClickListener = {
                showFragment("page_systemui")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_android_green)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_android),
            onClickListener = {
                showFragment("page_android")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_home)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_miui_home),
            onClickListener = {
                showFragment("page_miui_home")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_cleaner)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_cleaner),
            onClickListener = {
                showFragment("page_cleaner")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_security_center)!!,
            textSummaryV = TextSummaryV(textId = if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center),
            onClickListener = {
                showFragment("page_security_center")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_interconnection)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_interconnection),
            onClickListener = {
                showFragment("page_interconnection")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_others)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_others),
            onClickListener = {
                showFragment("page_others")
            }
        )
    }

    private fun initLiteMode() {
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_systemui)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_virtual_media_control_style),
            onClickListener = {
                showFragment("page_media_control")
            }
        )
        Page(
            pageHead = AppCompatResources.getDrawable(activity, R.drawable.ic_header_home)!!,
            textSummaryV = TextSummaryV(textId = R.string.page_virtual_honme_refactor),
            onClickListener = {
                showFragment("page_home_refactor")
            }
        )
    }
}