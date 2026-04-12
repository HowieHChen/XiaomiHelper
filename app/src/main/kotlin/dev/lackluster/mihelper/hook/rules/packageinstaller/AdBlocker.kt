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

package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.annotation.SuppressLint
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlocker : StaticHooker() {
    private val clzCloudParams by "com.miui.packageInstaller.model.CloudParams".lazyClassOrNull()
    private val ctorCloudParams by lazy {
        clzCloudParams?.resolve()?.firstConstructor()
    }
    private val cloudParamsMethod by lazy {
        DexKit.findMethodWithCache("cloud_params") {
            matcher {
                addUsingString("apkSignature3Sha256", StringMatchType.Equals)
            }
        }
    }
    private val ctorCloudResultSuccess by lazy {
        $$"com.miui.packageInstaller.model.CloudResult$Success".toClassOrNull()
            ?.resolve()?.firstConstructorOrNull {
                parameters("com.miui.packageInstaller.model.CloudParams")
            }?.toTyped()
    }
    private val adsEnableMethod by lazy {
        DexKit.findMethodWithCache("ads_enable") {
            matcher {
                addUsingString("ads_enable", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val appStoreRecommendMethod by lazy {
        DexKit.findMethodWithCache("app_store_recommend") {
            matcher {
                addUsingString("app_store_recommend", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val isPersonalizedAdEnabledMethod by lazy {
        DexKit.findMethodWithCache("personalized_ad_enable") {
            matcher {
                addUsingString($$"android.provider.MiuiSettings$Ad", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val virusScanInstallMethod by lazy {
        DexKit.findMethodWithCache("virus_scan_install") {
            matcher {
                addUsingString("virus_scan_install", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }
    private val metStartVirusScan by lazy {
        DexKit.findMethodWithCache("start_virus_scan") {
            matcher {
                addUsingString("startVirusScan", StringMatchType.Equals)
                addUsingString("get_guardprovider_time", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.PackageInstaller.REMOVE_ELEMENT.get().also { 
            updateSelfState(it)
        }.ifTrue {
            adsEnableMethod
            appStoreRecommendMethod
            isPersonalizedAdEnabledMethod
            virusScanInstallMethod
            metStartVirusScan
            cloudParamsMethod
        }
    }

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        adsEnableMethod?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        appStoreRecommendMethod?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        isPersonalizedAdEnabledMethod?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        virusScanInstallMethod?.getMethodInstance(classLoader)?.hook {
            result(false)
        }
        metStartVirusScan?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        cloudParamsMethod?.getMethodInstance(classLoader)?.hook {
            val cloudParams = ctorCloudParams?.copy()?.create()
            val cloudResult = cloudParams?.let { ctorCloudResultSuccess?.newInstance(it) }
            if (cloudResult != null) {
                result(cloudResult)
            } else {
                result(cloudParams)
            }
        }
        clzCloudParams?.apply {
            val installNotAllow = resolve().firstFieldOrNull {
                name = "installNotAllow"
            }?.toTyped<Boolean>()
            val showSafeModeTip = resolve().firstFieldOrNull {
                name = "showSafeModeTip"
            }?.toTyped<Boolean>()
            val showAdsBefore = resolve().firstFieldOrNull {
                name = "showAdsBefore"
            }?.toTyped<Boolean>()
            val showAdsAfter = resolve().firstFieldOrNull {
                name = "showAdsAfter"
            }?.toTyped<Boolean>()
            val useSystemAppRules = resolve().firstFieldOrNull {
                name = "useSystemAppRules"
            }?.toTyped<Boolean>()
            val registrationStatus = resolve().firstFieldOrNull {
                name = "registrationStatus"
            }?.toTyped<Int>()
            ctorCloudParams?.self?.hook {
                val ori = proceed()
                installNotAllow?.set(thisObject, false)
                showSafeModeTip?.set(thisObject, false)
                showAdsBefore?.set(thisObject, false)
                showAdsAfter?.set(thisObject, false)
                useSystemAppRules?.set(thisObject, true)
                registrationStatus?.set(thisObject, 2)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "getAppRegisterScene"
            }?.hook {
                result("registered")
            }
            resolve().firstMethodOrNull {
                name = "isMarketApp"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "isMarketApp64NotInstallAllow"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "isProhibitInstalling"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "isNewUnregistered"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "isUnrecorded"
            }?.hook {
                result(false)
            }
        }
    }
}