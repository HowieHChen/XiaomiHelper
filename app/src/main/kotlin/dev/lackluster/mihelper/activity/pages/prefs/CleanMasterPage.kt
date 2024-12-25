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

import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@BMPage(Pages.CLEAN_MASTER)
class CleanMasterPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_cleaner)
    }

    override fun onCreate() {
        val padBinding = GetDataBinding({
            Device.isPad
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_ad_blocker),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_ad_blocker_market),
                SwitchData(Pref.Key.Market.AD_BLOCKER)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_ad_blocker_mms),
                SwitchData(Pref.Key.MMS.AD_BLOCKER)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_ad_blocker_music),
                SwitchData(Pref.Key.Music.AD_BLOCKER)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_ad_blocker_theme),
                SwitchData(Pref.Key.Themes.AD_BLOCKER)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_privacy),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.cleaner_privacy_block_upload_app,
                    summaryId = R.string.cleaner_privacy_block_upload_app_tips
                ),
                SwitchData(Pref.Key.GuardProvider.BLOCK_UPLOAD_APP)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.cleaner_privacy_block_ul_app_info,
                    summaryId = R.string.cleaner_privacy_block_ul_app_info_tips
                ),
                SwitchData(Pref.Key.PackageInstaller.BLOCK_UPLOAD_INFO)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_security_mi_trust_service),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.mi_trust_disable_risk_check),
                SwitchData(Pref.Key.MiTrust.DISABLE_RISK_CHECK)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_package),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_package_remove_element),
                SwitchData(Pref.Key.PackageInstaller.REMOVE_ELEMENT)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_package_skip_risk_check),
                SwitchData(Pref.Key.PackageInstaller.DISABLE_RISK_CHECK)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_package_no_count_check),
                SwitchData(Pref.Key.PackageInstaller.DISABLE_COUNT_CHECK)
            )
        }
        PreferenceCategory(
            DescData(titleId = if (Device.isPad) R.string.ui_title_cleaner_security_pad else R.string.ui_title_cleaner_security),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.cleaner_security_lock_score,
                    summaryId = R.string.cleaner_security_lock_score_tips
                ),
                SwitchData(Pref.Key.SecurityCenter.LOCK_SCORE)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_security_disable_risk_app_notif),
                SwitchData(Pref.Key.SecurityCenter.DISABLE_RISK_APP_NOTIF)
            )
            SwitchPreference(
                DescData(titleId = R.string.cleaner_security_remove_report),
                SwitchData(Pref.Key.SecurityCenter.REMOVE_REPORT)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_incallui),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_incallui_hide_crbt),
                SwitchData(Pref.Key.InCallUI.HIDE_CRBT)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_home),
            CategoryData(),
            dataBindingRecv = padBinding.binding.getRecv(1)
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_home_remove_report),
                SwitchData(Pref.Key.MiuiHome.REMOVE_REPORT),
                dataBindingRecv = padBinding.binding.getRecv(1)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_cleaner_taplus),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(titleId = R.string.cleaner_taplus_hide_shop),
                SwitchData(Pref.Key.Taplus.HIDE_SHOP)
            )
        }
    }
}