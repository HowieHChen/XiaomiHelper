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
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage("page_cleaner")
class CleanMasterPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_cleaner)
    }

    override fun onCreate() {
        TitleText(textId = R.string.ui_title_cleaner_ad_blocker)
        TextWithSwitch(
            TextV(textId = R.string.cleaner_ad_blocker_market),
            SwitchV(Pref.Key.Market.AD_BLOCKER)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_ad_blocker_mms),
            SwitchV(Pref.Key.MMS.AD_BLOCKER)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_ad_blocker_music),
            SwitchV(Pref.Key.Music.AD_BLOCKER)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_ad_blocker_theme),
            SwitchV(Pref.Key.Themes.AD_BLOCKER)
        )
        Line()
        TitleText(textId = R.string.ui_title_cleaner_privacy)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.cleaner_privacy_block_upload_app,
                tipsId = R.string.cleaner_privacy_block_upload_app_tips
            ),
            SwitchV(Pref.Key.GuardProvider.BLOCK_UPLOAD_APP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.cleaner_privacy_block_ul_app_info,
                tipsId = R.string.cleaner_privacy_block_ul_app_info_tips
            ),
            SwitchV(Pref.Key.PackageInstaller.BLOCK_UPLOAD_INFO)
        )
        Line()
        TitleText(textId = R.string.ui_title_cleaner_mi_trust_service)
        TextWithSwitch(
            TextV(textId = R.string.mi_trust_disable_risk_check),
            SwitchV(Pref.Key.MiTrust.DISABLE_RISK_CHECK)
        )
        Line()
        TitleText(textId = R.string.ui_title_cleaner_package)
        TextWithSwitch(
            TextV(textId = R.string.cleaner_package_remove_element),
            SwitchV(Pref.Key.PackageInstaller.REMOVE_ELEMENT)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_package_skip_risk_check),
            SwitchV(Pref.Key.PackageInstaller.DISABLE_RISK_CHECK)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_package_no_count_check),
            SwitchV(Pref.Key.PackageInstaller.DISABLE_COUNT_CHECK)
        )
        Line()
        TitleText(
            textId = if (Device.isPad) R.string.ui_title_cleaner_security_pad else R.string.ui_title_cleaner_security
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.cleaner_security_lock_score,
                tipsId = R.string.cleaner_security_lock_score_tips
            ),
            SwitchV(Pref.Key.SecurityCenter.LOCK_SCORE)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_security_disable_risk_app_notif),
            SwitchV(Pref.Key.SecurityCenter.DISABLE_RISK_APP_NOTIF)
        )
        TextWithSwitch(
            TextV(textId = R.string.cleaner_security_remove_report),
            SwitchV(Pref.Key.SecurityCenter.REMOVE_REPORT)
        )
        Line()
        TitleText(textId = R.string.ui_title_in_call_ui)
        TextWithSwitch(
            TextV(textId = R.string.cleaner_incallui_hide_crbt),
            SwitchV(Pref.Key.InCallUI.HIDE_CRBT)
        )
        if (!Device.isPad) {
            Line()
            TitleText(textId = R.string.ui_title_home)
            TextWithSwitch(
                TextV(textId = R.string.cleaner_home_remove_report),
                SwitchV(Pref.Key.MiuiHome.REMOVE_REPORT)
            )
        }
        Line()
        TitleText(textId = R.string.ui_title_taplus)
        TextWithSwitch(
            TextV(textId = R.string.cleaner_taplus_hide_shop),
            SwitchV(Pref.Key.Taplus.HIDE_SHOP)
        )
    }
}