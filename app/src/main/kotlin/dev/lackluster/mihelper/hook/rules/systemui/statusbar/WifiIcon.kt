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

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlow0
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

private fun parseWifiStandardMap(value: String): IntArray? {
    val values = value.trim().split(',', '，', ' ', '\n', '\t').filter(String::isNotBlank)
    if (values.size !in setOf(1, 5)) return null
    val mapping = values.map { it.toIntOrNull() ?: return null }
    return if (mapping.size == 1) IntArray(5) { mapping.first() } else mapping.toIntArray()
}

private fun mapWifiStandard(standard: Int, mapping: IntArray): Int {
    if (standard !in 4..8) return 0
    return mapping[standard - 4].takeUnless { it == -1 } ?: 0
}

object WifiIcon : StaticHooker() {
    private val hideActivity by Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_ACTIVITY.lazyGet()
    private val wifiStandardMode by Preferences.SystemUI.StatusBar.IconDetail.WIFI_STANDARD_MODE.lazyGet()
    private val wifiStandardMap by Preferences.SystemUI.StatusBar.IconDetail.WIFI_STANDARD_MAP.lazyGet()
    private val activityRight by Preferences.SystemUI.StatusBar.IconDetail.WIFI_ACTIVITY_RIGHT.lazyGet()
    private val hideUnavailable by Preferences.SystemUI.StatusBar.IconDetail.HIDE_WIFI_UNAVAILABLE.lazyGet()

    private val flowJobs = mutableListOf<Any?>()

    private val hideStandard: Boolean
        get() = wifiStandardMode == 1

    private val modifyStandard: Boolean
        get() = wifiStandardMode in 1..3

    override fun onInit() {
        updateSelfState(hideActivity || modifyStandard || activityRight || hideUnavailable)
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
        if (!hideActivity && !modifyStandard) return
        "com.android.systemui.statusbar.pipeline.wifi.ui.viewmodel.WifiViewModel".toClassOrNull()?.apply {
            val activityInOutRes = resolve().firstFieldOrNull {
                name = "activityInOutRes"
            }?.toTyped<Any>()
            val wifiStandard = resolve().firstFieldOrNull {
                name = "wifiStandard"
            }?.toTyped<Any>()
            val wifiIcon = resolve().firstFieldOrNull {
                name = "wifiIcon"
            }?.toTyped<Any>()
            val inoutLeft = resolve().firstFieldOrNull {
                name = "inoutLeft"
            }?.toTyped<Any>()
            val wifiInteractor = "com.android.systemui.statusbar.pipeline.wifi.domain.interactor.WifiInteractorImpl".toClassOrNull()
            val wifiNetwork = wifiInteractor?.resolve()?.firstFieldOrNull {
                name = "wifiNetwork"
            }?.toTyped<Any>()
            val activeWifiNetwork = $$"com.android.systemui.statusbar.pipeline.wifi.shared.model.WifiNetworkModel$Active".toClassOrNull()
            val carrierMergedWifiNetwork = $$"com.android.systemui.statusbar.pipeline.wifi.shared.model.WifiNetworkModel$CarrierMerged".toClassOrNull()
            val hiddenWifiIcon = $$"com.android.systemui.statusbar.pipeline.wifi.ui.model.WifiIcon$Hidden".toClassOrNull()
            val wifiExt = activeWifiNetwork?.resolve()?.firstFieldOrNull {
                name = "ext"
            }?.toTyped<Any>()
            val miuiWifiExt = "com.android.systemui.statusbar.pipeline.MiuiWifiExt".toClassOrNull()
            val meteredHint = miuiWifiExt?.resolve()?.firstFieldOrNull {
                name = "meteredHint"
            }?.toTyped<Boolean>()
            val actualWifiStandard = miuiWifiExt?.resolve()?.firstFieldOrNull {
                name = "wifiStandard"
            }?.toTyped<Int>()
            val slaveWifiUtils = "com.miui.utils.SlaveWifiUtils".toClassOrNull()
            val getSlaveWifiUtils = slaveWifiUtils?.resolve()?.firstMethodOrNull {
                name = "getInstance"
                parameterCount = 1
            }?.toTyped<Any>()
            val getSlaveConnectionInfo = slaveWifiUtils?.resolve()?.firstMethodOrNull {
                name = "getWifiSlaveConnectionInfo"
                parameterCount = 0
            }?.toTyped<Any>()
            val getWifiStandard = "android.net.wifi.WifiInfo".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "getWifiStandard"
                parameterCount = 0
            }?.toTyped<Int>()
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
                flowJobs.forEach { job ->
                    FlowCompat.cancelJob(job)
                }
                flowJobs.clear()
                if (wifiStandardMode in 2..3) {
                    val interactor = getArg(5)
                    val coroutineScope = args.firstOrNull { CommonClassUtils.clzCoroutineScope?.isInstance(it) == true }
                    val context = args.firstOrNull { it is Context }
                    val iconFlow = wifiIcon?.get(thisObject)
                    val networkFlow = wifiNetwork?.get(interactor)

                    if (iconFlow != null && networkFlow != null) {
                        val customStandard = MutableStateFlowCompat(0)
                        val mapping = parseWifiStandardMap(wifiStandardMap)
                            ?: intArrayOf(4, 5, 6, 7, 8)

                        FlowCompat.combineFlows(
                            scope = coroutineScope,
                            src1 = ReadonlyStateFlowCompat<Any>().of(iconFlow),
                            src2 = ReadonlyStateFlowCompat<Any>().of(networkFlow),
                            dst = customStandard,
                        ) { icon, network ->
                            if (
                                hiddenWifiIcon?.isInstance(icon) == true ||
                                carrierMergedWifiNetwork?.isInstance(network) == true
                            ) {
                                return@combineFlows 0
                            }

                            val standard = if (activeWifiNetwork?.isInstance(network) == true) {
                                val ext = wifiExt?.get(network) ?: return@combineFlows 0
                                if (meteredHint?.get(ext) == true) return@combineFlows 0
                                actualWifiStandard?.get(ext) ?: 0
                            } else {
                                val slaveUtils = context?.let { getSlaveWifiUtils?.invoke(null, it) }
                                val slaveInfo = getSlaveConnectionInfo?.invoke(slaveUtils)
                                getWifiStandard?.invoke(slaveInfo) ?: 0
                            }

                            when (wifiStandardMode) {
                                2 -> standard.takeIf { it in 4..8 } ?: 0
                                3 -> mapWifiStandard(standard, mapping)
                                else -> 0
                            }
                        }.let { flowJobs.addAll(it) }
                        wifiStandard?.set(thisObject, customStandard.toReadonlyStateFlow())
                    }
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