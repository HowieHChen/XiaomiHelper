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
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref

@BMPage("page_interconnection", hideMenu = true)
class InterconnectionPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_interconnection)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_interconnection_milink)
        TextWithSwitch(
            TextV(textId = R.string.interconnection_milink_fuck_hpplay),
            SwitchV(Pref.Key.MiLink.FUCK_HPPLAY)
        )
        Line()
        TitleText(textId = R.string.ui_title_interconnection_mimirror)
        TextWithSwitch(
            TextV(textId = R.string.interconnection_mimirror_all_app),
            SwitchV(Pref.Key.MiMirror.CONTINUE_ALL_TASKS)
        )
        Line()
        TitleText(textId = R.string.ui_title_interconnection_mishare)
        TextWithSwitch(
            TextV(textId = R.string.interconnection_mishare_no_auto_off),
            SwitchV(Pref.Key.MiShare.ALWAYS_ON)
        )
    }
}