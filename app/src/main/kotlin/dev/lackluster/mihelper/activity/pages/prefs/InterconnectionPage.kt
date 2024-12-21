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

package dev.lackluster.mihelper.activity.pages.prefs

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref

@BMPage(Pages.INTERCONNECTION)
class InterconnectionPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_interconnection)
    }

    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_milink),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_milink_fuck_hpplay),
                SwitchData(Pref.Key.MiLink.FUCK_HPPLAY)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_others_mimirror),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.others_mimirror_all_app),
                SwitchData(Pref.Key.MiMirror.CONTINUE_ALL_TASKS)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_interconnection_mishare),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.interconnection_mishare_no_auto_off),
                SwitchData(Pref.Key.MiShare.ALWAYS_ON)
            )
        }
    }
}