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

import android.telephony.SubscriptionManager
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzCoroutineScope
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.cancelJob
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.combineFlows
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.utils.Prefs

object HideCellularIcon : YukiBaseHooker() {
    private const val KEY_DEF_DATA_CONFIG_FLOW = "KEY_DEF_DATA_CONFIG_FLOW"

    private val hideSimAuto = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_SIM_AUTO, false)
    private val hideSimOne = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_SIM_ONE, false)
    private val hideSimTwo = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.HIDE_SIM_TWO, false)

    private val hideSimJobMap = mutableMapOf<Int, Pair<Any?, Any?>>()

    override fun onHook() {
        if (hideSimAuto || hideSimOne || hideSimTwo) {
            val clzMobileIconInteractorImpl = "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconInteractorImpl".toClassOrNull()
            if (hideSimAuto) {
                "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconsInteractorImpl".toClassOrNull()?.apply {
                    val defaultDataSubId = resolve().firstFieldOrNull {
                        name = "defaultDataSubId"
                    }
                    resolve().firstMethodOrNull {
                        name = "getMobileConnectionInteractorForSubId"
                    }?.hook {
                        after {
                            val mobileIconInteractor = this.result
                            val defaultDataSubIdFlow = defaultDataSubId?.copy()?.of(this.instance)?.get()
                            XposedHelpers.setAdditionalInstanceField(
                                mobileIconInteractor,
                                KEY_DEF_DATA_CONFIG_FLOW,
                                defaultDataSubIdFlow
                            )
                        }
                    }
                }
            }
            "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClassOrNull()?.apply {
                val subscriptionId = resolve().firstFieldOrNull {
                    name = "subscriptionId"
                }
                val isVisible = resolve().firstFieldOrNull {
                    name = "isVisible"
                }
                resolve().firstConstructor().hook {
                    after {
                        val subId = subscriptionId?.copy()?.of(this.instance)?.get<Int>()
                        val slotIndex = subId?.let { SubscriptionManager.getSlotIndex(it) }
                        if (hideSimAuto && subId != null) {
                            hideSimJobMap[subId]?.let {
                                cancelJob(it.first)
                                cancelJob(it.second)
                            }
                            val coroutineScope =
                                this.args.firstOrNull { clzCoroutineScope?.isInstance(it) == true } ?: return@after
                            val mobileIconInteractor =
                                this.args.firstOrNull { clzMobileIconInteractorImpl?.isInstance(it) == true } ?: return@after
                            val defaultDataSubIdFlow = XposedHelpers.getAdditionalInstanceField(
                                mobileIconInteractor,
                                KEY_DEF_DATA_CONFIG_FLOW
                            )?.let { ReadonlyStateFlowCompat<Int?>().of(it) } ?: return@after
                            val oriVisibleFlow = isVisible?.copy()?.of(this.instance)?.get()?.let {
                                ReadonlyStateFlowCompat<Boolean>().of(it)
                            } ?: return@after
                            val proxyStateFlow = MutableStateFlowCompat(false)
                            val jobs = combineFlows(
                                coroutineScope,
                                oriVisibleFlow,
                                false,
                                defaultDataSubIdFlow,
                                -1,
                                proxyStateFlow
                            ) { a, b ->
                                return@combineFlows a && (b == subId)
                            }
                            hideSimJobMap.put(subId, jobs)
                            isVisible.copy().of(instance).set(proxyStateFlow.toReadonlyStateFlow())
                        } else if (
                            slotIndex == 0 && hideSimOne ||
                            slotIndex == 1 && hideSimTwo
                        ) {
                            isVisible?.copy()?.of(instance)?.set(
                                readonlyStateFlowFalse
                            )
                        }
                    }
                }
            }
        }
    }
}