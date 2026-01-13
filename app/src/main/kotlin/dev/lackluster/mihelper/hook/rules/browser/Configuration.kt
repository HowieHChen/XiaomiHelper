/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object Configuration : YukiBaseHooker() {
    private val clzConfigCenter by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("pref_ad_open_ad_more", StringMatchType.Equals)
                addUsingString("ad_app_download_exit_switch", StringMatchType.Equals)
            }
        }
    }

    private val metPrefShowSugSwitchView by lazy {
        DexKit.findMethodWithCache("pref_show_sug_switch_view") {
            matcher {
                addUsingString("pref_show_sug_switch_view", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metRecPopupCardSwitch by lazy {
        DexKit.findMethodWithCache("rec_popup_card_switch") {
            matcher {
                addUsingString("rec_popup_card_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metPrefPushPopDialog by lazy {
        DexKit.findMethodWithCache("pref_push_pop_dialog") {
            matcher {
                addUsingString("pref_push_pop_dialog", StringMatchType.Equals)
                addUsingString("activity", StringMatchType.Equals)
            }
        }
    }
    private val metDefaultPageRealRimeHotSpotSwitch by lazy {
        DexKit.findMethodWithCache("default_page_real_time_hot_spot_switch") {
            matcher {
                addUsingString("default_page_real_time_hot_spot_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metDefaultPageGuessYouWantSwitch by lazy {
        DexKit.findMethodWithCache("default_page_guess_you_want_switch") {
            matcher {
                addUsingString("default_page_guess_you_want_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metAdAppDownloadExitSwitch by lazy {
        DexKit.findMethodWithCache("ad_app_download_exit_switch") {
            matcher {
                addUsingString("ad_app_download_exit_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metAdAppDownloadPushSwitch by lazy {
        DexKit.findMethodWithCache("ad_app_download_push_switch") {
            matcher {
                addUsingString("ad_app_download_push_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }
    private val metAdAppDownloadHomeSwitch by lazy {
        DexKit.findMethodWithCache("ad_app_download_home_switch") {
            matcher {
                addUsingString("ad_app_download_home_switch", StringMatchType.Equals)
                paramCount = 0
            }
            searchInClass(clzConfigCenter)
        }
    }

    override fun onHook() {
        if (appClassLoader == null) return
        hasEnable(Pref.Key.Browser.SHOW_SUG_SWITCH_VIEW) {
            metPrefShowSugSwitchView?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToTrue()
            }
        }
        hasEnable(Pref.Key.Browser.BLOCK_DIALOG) {
            metRecPopupCardSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metPrefPushPopDialog?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
        }
        hasEnable(Pref.Key.Browser.AD_BLOCKER) {
            metDefaultPageRealRimeHotSpotSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metDefaultPageGuessYouWantSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metAdAppDownloadExitSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metAdAppDownloadPushSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metAdAppDownloadHomeSwitch?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
        }
    }
}