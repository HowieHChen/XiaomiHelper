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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : YukiBaseHooker() {
    private val thirdPartyLaunchAdMethod by lazy {
        DexKit.findMethodWithCache("skip_splash_third") {
            matcher {
                returnType = "void"
                paramCount = 1
                paramTypes = listOf("android.content.Context")
                addUsingString("onTrackAppOpenThird appLaunchWay:", StringMatchType.Equals)
                addUsingString("第三方调起", StringMatchType.Equals)
            }
        }
    }
    private val iconLaunchAdMethod by lazy {
        DexKit.findMethodWithCache("skip_splash_icon") {
            matcher {
                returnType = "void"
                addUsingString("SplashActiveAdManager", StringMatchType.Equals)
                addUsingString("requestAd", StringMatchType.Equals)
                addUsingString("msa_request", StringMatchType.Equals)
            }
        }
    }
    private val supportPassive by lazy {
        DexKit.findMethodWithCache("skip_splash_support") {
            matcher {
                returnType = "boolean"
                addUsingString("SystemSplashAd", StringMatchType.Equals)
                addUsingString("support_passive", StringMatchType.Equals)
                addUsingString("content://com.miui.systemAdSolution.extContentProvider/supportPassive", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.SKIP_SPLASH) {
            if (appClassLoader == null) return@hasEnable
            thirdPartyLaunchAdMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            iconLaunchAdMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            supportPassive?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            "com.android.browser.splash.SplashAdManager".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "inWhiteList"
                }?.hook {
                    replaceToTrue()
                }
            }
//            "com.android.browser.xiangkan.AppDownloadHelper".toClass().apply {
//                method {
//                    name = "onLoadData"
//                }.hook {
//                    intercept()
//                }
//            }
        }
    }
}