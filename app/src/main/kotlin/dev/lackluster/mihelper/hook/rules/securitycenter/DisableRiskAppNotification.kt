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

package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableRiskAppNotification : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = false

    private val metIsSupportRiskAppIntercept by lazy {
        DexKit.findMethodWithCache("is_support_risk_app_intercept") {
            matcher {
                addUsingString("addStartupBlockedPackage", StringMatchType.Equals)
                addUsingString("isSupportRiskAppIntercept", StringMatchType.Contains)
            }
        }
    }
    private val metIsSupportMalwareStartupIntercept by lazy {
        DexKit.findMethodWithCache("malware_startup_intercept") {
            matcher {
                addUsingString("malware_startup_intercept", StringMatchType.Equals)
                paramCount = 1
                returnType = "boolean"
            }
        }
    }
    private val metRegisterTrackAntiVursData by lazy {
        DexKit.findMethodWithCache("miui.intent.action.TRACK_ANTIVURS_DATA") {
            matcher {
                addUsingString("miui.intent.action.TRACK_ANTIVURS_DATA", StringMatchType.Equals)
                paramTypes("android.content.Context")
            }
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.DISABLE_RISK_APP_NOTIF.observe {
                updateSelfState(it)
            }
        )
        metIsSupportRiskAppIntercept
        metIsSupportMalwareStartupIntercept
        metRegisterTrackAntiVursData
    }
    
    override fun onStart() {
        "com.miui.antivirus.service.VirusScanJobService".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "onStartJob"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                parameters(List::class, Int::class, Int::class, Int::class, Int::class, Int::class)
            }?.hook {
                result(null)
            }
        }
        metIsSupportRiskAppIntercept?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        metIsSupportMalwareStartupIntercept?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        metRegisterTrackAntiVursData?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}