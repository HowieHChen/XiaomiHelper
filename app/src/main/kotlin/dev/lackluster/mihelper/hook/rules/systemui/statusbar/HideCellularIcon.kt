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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzCoroutineScope
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.readonlyStateFlowFalse
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat.cancelJob
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat.combineFlows
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import java.util.concurrent.ConcurrentHashMap

object HideCellularIcon : StaticHooker() {
    private var Any.defDataSubIdFlow by extraOf<Any>("KEY_DEF_DATA_CONFIG_FLOW")

    private val enableStackedMobile by Preferences.SystemUI.StatusBar.StackedMobile.ENABLED.lazyGet()
    private val hideSimAuto by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_AUTO.get() && !enableStackedMobile
    }
    private val hideSimOne by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_ONE.get() || enableStackedMobile
    }
    private val hideSimTwo by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.HIDE_SIM_TWO.get() || enableStackedMobile
    }

    private val hideSimJobMap = ConcurrentHashMap<Int, List<Any?>>()

    override fun onInit() {
        updateSelfState(hideSimAuto || hideSimOne || hideSimTwo)
    }

    override fun onHook() {
        val clzMobileIconInteractor = "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconInteractor".toClassOrNull()
        if (hideSimAuto) {
            "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconsInteractorImpl".toClassOrNull()?.apply {
                val defaultDataSubId = resolve().firstFieldOrNull {
                    name = "defaultDataSubId"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "getMobileConnectionInteractorForSubId"
                }?.hook {
                    val mobileIconInteractor = proceed()
                    val defaultDataSubIdFlow = defaultDataSubId?.get(thisObject)
                    mobileIconInteractor.defDataSubIdFlow = defaultDataSubIdFlow
                    result(mobileIconInteractor)
                }
            }
        }
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClassOrNull()?.apply {
            val subscriptionId = resolve().firstFieldOrNull {
                name = "subscriptionId"
            }?.toTyped<Int>()
            val isVisible = resolve().firstFieldOrNull {
                name = "isVisible"
            }?.toTyped<Any>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                val subId = subscriptionId?.get(thisObject)
                val slotIndex = subId?.let { SubscriptionManager.getSlotIndex(it) }
                if (hideSimAuto && subId != null) {
                    hideSimJobMap[subId]?.forEach {
                        cancelJob(it)
                    }
                    val coroutineScope = args.firstOrNull { clzCoroutineScope?.isInstance(it) == true } ?: return@hook result(ori)
                    val mobileIconInteractor = args.firstOrNull { clzMobileIconInteractor?.isInstance(it) == true } ?: return@hook result(ori)
                    val defaultDataSubIdFlow = mobileIconInteractor.defDataSubIdFlow?.let {
                        ReadonlyStateFlowCompat<Int?>().of(it)
                    } ?: return@hook result(ori)
                    val oriVisibleFlow = isVisible?.get(thisObject)?.let {
                        ReadonlyStateFlowCompat<Boolean>().of(it)
                    } ?: return@hook result(ori)
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
                    hideSimJobMap[subId] = jobs
                    isVisible.set(thisObject, proxyStateFlow.toReadonlyStateFlow())
                } else if (
                    slotIndex == 0 && hideSimOne ||
                    slotIndex == 1 && hideSimTwo
                ) {
                    isVisible?.set(thisObject, readonlyStateFlowFalse)
                }
                result(ori)
            }
        }
    }
}