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

import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage("page_security_center")
class SecurityCenterPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(
            if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center
        )
    }

    override fun onCreate() {
        TitleText(
            textId = if (Device.isPad) R.string.ui_title_security_security_pad else R.string.ui_title_security_security
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_security_skip_warning,
                tipsId = R.string.security_security_skip_warning_tips
            ),
            SwitchV(Pref.Key.SecurityCenter.SKIP_WARNING)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_security_skip_open_app,
                tipsId = R.string.security_security_skip_open_app_tips
            ),
            SwitchV(Pref.Key.SecurityCenter.SKIP_OPEN_APP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_security_screen_battery,
                tipsId = R.string.security_security_screen_battery_tips
            ),
            SwitchV(Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY)
        )
        TextWithSwitch(
            TextV(textId = R.string.security_security_bubble_restriction),
            SwitchV(Pref.Key.SecurityCenter.DISABLE_BUBBLE_RESTRICT)
        )
        TextWithSwitch(
            TextV(textId = R.string.security_security_system_app_wifi),
            SwitchV(Pref.Key.SecurityCenter.CTRL_SYSTEM_APP_WIFI)
        )
        TextWithSwitch(
            TextV(textId = R.string.security_security_icon_open),
            SwitchV(Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN)
        )
        Line()
        TitleText(textId = R.string.ui_title_security_power)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_power_do_not_kill_app,
            ),
            SwitchV(Pref.Key.PowerKeeper.DO_NOT_KILL_APP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.security_power_block_battery_whitelist,
            ),
            SwitchV(Pref.Key.PowerKeeper.BLOCK_BATTERY_WHITELIST)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.security_power_battery_optimization,
                tipsId = R.string.security_power_battery_optimization_tips,
                onClickListener = {
                    try {
                        val intent = Intent()
                        val comp = ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$HighPowerApplicationsActivity"
                        )
                        intent.component = comp
                        activity.startActivity(intent)
                    } catch (t: Throwable) {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.security_power_battery_optimization_failed_toast) + t.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        )
    }
}