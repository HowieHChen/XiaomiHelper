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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlocker : YukiBaseHooker() {
    private val clzCloudParams by lazy {
        "com.miui.packageInstaller.model.CloudParams".toClassOrNull()
    }
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
            }
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

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.REMOVE_ELEMENT) {
            if (appClassLoader == null) return@hasEnable
            adsEnableMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            appStoreRecommendMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            isPersonalizedAdEnabledMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            virusScanInstallMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            metStartVirusScan?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            cloudParamsMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    val cloudParams = ctorCloudParams?.copy()?.create() ?: return@before
                    val cloudResult = ctorCloudResultSuccess?.copy()?.create(cloudParams)
                    if (cloudResult != null) {
                        this.result = cloudResult
                    } else {
                        this.result = cloudParams
                    }
                }
            }
            clzCloudParams?.apply {
                val installNotAllow = resolve().firstFieldOrNull {
                    name = "installNotAllow"
                }
                val showSafeModeTip = resolve().firstFieldOrNull {
                    name = "showSafeModeTip"
                }
                val showAdsBefore = resolve().firstFieldOrNull {
                    name = "showAdsBefore"
                }
                val showAdsAfter = resolve().firstFieldOrNull {
                    name = "showAdsAfter"
                }
                val useSystemAppRules = resolve().firstFieldOrNull {
                    name = "useSystemAppRules"
                }
                val registrationStatus = resolve().firstFieldOrNull {
                    name = "registrationStatus"
                }
                ctorCloudParams?.copy()?.hook {
                    after {
                        installNotAllow?.copy()?.of(this.instance)?.set(false)
                        showSafeModeTip?.copy()?.of(this.instance)?.set(false)
                        showAdsBefore?.copy()?.of(this.instance)?.set(false)
                        showAdsAfter?.copy()?.of(this.instance)?.set(false)
                        useSystemAppRules?.copy()?.of(this.instance)?.set(true)
                        registrationStatus?.copy()?.of(this.instance)?.set(2)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getAppRegisterScene"
                }?.hook {
                    replaceTo("registered")
                }
                resolve().firstMethodOrNull {
                    name = "isMarketApp"
                }?.hook {
                    replaceToFalse()
                }
                resolve().firstMethodOrNull {
                    name = "isMarketApp64NotInstallAllow"
                }?.hook {
                    replaceToFalse()
                }
                resolve().firstMethodOrNull {
                    name = "isProhibitInstalling"
                }?.hook {
                    replaceToFalse()
                }
                resolve().firstMethodOrNull {
                    name = "isNewUnregistered"
                }?.hook {
                    replaceToFalse()
                }
                resolve().firstMethodOrNull {
                    name = "isUnrecorded"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}