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

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import org.luckypray.dexkit.query.enums.StringMatchType

object Configuration : StaticHooker() {
    private val showSugSwitch by Preferences.Browser.SHOW_SUG_SWITCH_ENTRY.lazyGet()
    private val blockDialog by Preferences.Browser.BLOCK_DIALOG.lazyGet()
    private val adBlocker by Preferences.Browser.AD_BLOCKER.lazyGet()

    private val clzConfigCenter by lazy {
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("pref_ad_open_ad_more", StringMatchType.Equals)
                    addUsingString("ad_app_download_exit_switch", StringMatchType.Equals)
                }
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

    override fun onInit() {
        updateSelfState(showSugSwitch || blockDialog || adBlocker)
        if (showSugSwitch) {
            metPrefShowSugSwitchView
        }
        if (blockDialog) {
            metRecPopupCardSwitch
            metPrefPushPopDialog
        }
        if (adBlocker) {
            metDefaultPageRealRimeHotSpotSwitch
            metDefaultPageGuessYouWantSwitch
            metAdAppDownloadExitSwitch
            metAdAppDownloadPushSwitch
            metAdAppDownloadHomeSwitch
        }
    }

    override fun onHook() {
        if (showSugSwitch) {
            metPrefShowSugSwitchView?.getMethodInstance(classLoader)?.hook {
                result(true)
            }
        }
        if (blockDialog) {
            metRecPopupCardSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
            metPrefPushPopDialog?.getMethodInstance(classLoader)?.hook {
                result(null)
            }
        }
        if (adBlocker) {
            metDefaultPageRealRimeHotSpotSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
            metDefaultPageGuessYouWantSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
            metAdAppDownloadExitSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
            metAdAppDownloadPushSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
            metAdAppDownloadHomeSwitch?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
        }
    }
}