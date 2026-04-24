/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlow0
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object WifiIcon : StaticHooker() {
    private val hideActivity by Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_ACTIVITY.lazyGet()
    private val hideStandard by Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_STANDARD.lazyGet()
    private val activityRight by Preferences.SystemUI.StatusBar.IconDetail.WIFI_ACTIVITY_RIGHT.lazyGet()
    private val hideUnavailable by Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_UNAVAILABLE.lazyGet()

    override fun onInit() {
        updateSelfState(hideActivity || hideStandard || activityRight || hideUnavailable)
    }

    override fun onHook() {
        if (hideUnavailable) {
            $$"com.android.systemui.statusbar.pipeline.wifi.ui.model.WifiIcon$Companion".toClassOrNull()?.apply {
                val clzWifiNetworkModeActive = $$"com.android.systemui.statusbar.pipeline.wifi.shared.model.WifiNetworkModel$Active".toClassOrNull()
                val clzWifiIconHidden = $$"com.android.systemui.statusbar.pipeline.wifi.ui.model.WifiIcon$Hidden".toClassOrNull()
                        ?.resolve()?.firstFieldOrNull {
                            name = "INSTANCE"
                            modifiers(Modifiers.STATIC)
                        }?.get()
                resolve().firstMethodOrNull {
                    name = "fromModel"
                }?.hook {
                    val networkModel = getArg(0)
                    val hasInternet = getArg(4) as? Boolean ?: false
                    if (
                        clzWifiNetworkModeActive?.isInstance(networkModel) == true &&
                        !hasInternet && clzWifiIconHidden != null
                    ) {
                        result(clzWifiIconHidden)
                    } else {
                        result(proceed())
                    }
                }
            }
        }
        if (!hideActivity && !hideStandard) return
        "com.android.systemui.statusbar.pipeline.wifi.ui.viewmodel.WifiViewModel".toClassOrNull()?.apply {
            val activityInOutRes = resolve().firstFieldOrNull {
                name = "activityInOutRes"
            }?.toTyped<Any>()
            val wifiStandard = resolve().firstFieldOrNull {
                name = "wifiStandard"
            }?.toTyped<Any>()
            val inoutLeft = resolve().firstFieldOrNull {
                name = "inoutLeft"
            }?.toTyped<Any>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                if (hideActivity) {
                    activityInOutRes?.set(
                        thisObject,
                        readonlyStateFlow0
                    )
                }
                if (hideStandard) {
                    wifiStandard?.set(
                        thisObject,
                        readonlyStateFlow0
                    )
                }
                if (activityRight && hideStandard && !hideActivity) {
                    inoutLeft?.set(
                        thisObject,
                        readonlyStateFlowFalse
                    )
                }
                result(ori)
            }
        }
    }
}