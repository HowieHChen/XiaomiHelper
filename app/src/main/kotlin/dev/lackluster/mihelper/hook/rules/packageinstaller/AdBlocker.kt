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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AdBlocker : YukiBaseHooker() {
    private val cloudParamsClass by lazy {
        "com.miui.packageInstaller.model.CloudParams".toClassOrNull()
    }
    private val cloudParamsMethod by lazy {
        DexKit.findMethodWithCache("cloud_params") {
            matcher {
                addUsingString("apkSignature3Sha256", StringMatchType.Equals)
            }
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
                addUsingString("android.provider.MiuiSettings\$Ad", StringMatchType.Equals)
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
            cloudParamsMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    val cloudParams = cloudParamsClass?.constructor()?.get()?.call() ?: return@before
                    this.result = cloudParams
                }
            }
            cloudParamsClass?.apply {
                constructor().hook {
                    after {
                        this.instance.current().field {
                            name = "installNotAllow"
                        }.setFalse()
                        this.instance.current().field {
                            name = "showSafeModeTip"
                        }.setFalse()
                        this.instance.current().field {
                            name = "showAdsBefore"
                        }.setFalse()
                        this.instance.current().field {
                            name = "showAdsAfter"
                        }.setFalse()
                        this.instance.current().field {
                            name = "useSystemAppRules"
                        }.setTrue()
                        this.instance.current().field {
                            name = "registrationStatus"
                        }.set(2)
                    }
                }
                method {
                    name = "getAppRegisterScene"
                }.hook {
                    replaceTo("registered")
                }
                method {
                    name = "isMarketApp"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isMarketApp64NotInstallAllow"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isProhibitInstalling"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isNewUnregistered"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isUnrecorded"
                }.hook {
                    replaceToFalse()
                }
            }
        }
    }
}