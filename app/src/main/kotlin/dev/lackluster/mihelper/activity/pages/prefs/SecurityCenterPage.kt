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
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage(Pages.SECURITY_CENTER)
class SecurityCenterPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(
            if (Device.isPad) R.string.page_security_center_pad else R.string.page_security_center
        )
    }

    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = if (Device.isPad) R.string.ui_title_security_security_pad else R.string.ui_title_security_security),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.security_security_skip_warning,
                    summaryId = R.string.security_security_skip_warning_tips
                ),
                SwitchData(Pref.Key.SecurityCenter.SKIP_WARNING)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.security_security_skip_open_app,
                    summaryId = R.string.security_security_skip_open_app_tips
                ),
                SwitchData(Pref.Key.SecurityCenter.SKIP_OPEN_APP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.security_security_screen_battery,
                    summaryId = R.string.security_security_screen_battery_tips
                ),
                SwitchData(Pref.Key.SecurityCenter.SHOW_SCREEN_BATTERY)
            )
            SwitchPreference(
                DescData(titleId = R.string.security_security_bubble_restriction),
                SwitchData(Pref.Key.SecurityCenter.DISABLE_BUBBLE_RESTRICT)
            )
            SwitchPreference(
                DescData(titleId = R.string.security_security_system_app_wifi),
                SwitchData(Pref.Key.SecurityCenter.CTRL_SYSTEM_APP_WIFI)
            )
            SwitchPreference(
                DescData(titleId = R.string.security_security_icon_open),
                SwitchData(Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_security_power),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.security_power_do_not_kill_app,
                ),
                SwitchData(Pref.Key.PowerKeeper.DO_NOT_KILL_APP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.security_power_block_battery_whitelist,
                ),
                SwitchData(Pref.Key.PowerKeeper.BLOCK_BATTERY_WHITELIST)
            )
            TextPreference(
                DescData(
                    titleId = R.string.security_power_battery_optimization,
                    summaryId = R.string.security_power_battery_optimization_tips
                ),
                TextData(),
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
        }
    }
}