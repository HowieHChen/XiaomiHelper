/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/packageinstaller/DisableAppInfoUpload.kt>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.packageinstaller

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object BlockUploadAppInfo : StaticHooker() {
    private val layoutMethod by lazy {
        DexKit.findMethodsWithCache("layout_method") {
            matcher {
                paramCount = 7
                paramTypes = listOf("java.lang.String", "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.String", "java.lang.String", null)
            }
        }
    }
    private val reportMethod by lazy {
        DexKit.findMethodsWithCache("report_method") {
            matcher {
                addUsingString("appSourcepackageName", StringMatchType.Equals)
                addUsingString($$"$apkInfo", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }
    }
    private val metCloudFetchTime by lazy {
        DexKit.findMethodWithCache("cloud_data_fetch_time") {
            matcher {
                addUsingString("cloud_data_fetch_time", StringMatchType.Equals)
                paramCount = 0
                returnType = "long"
            }
        }
    }

    override fun onInit() {
        Preferences.PackageInstaller.BLOCK_UPLOAD_INFO.get().also { 
            updateSelfState(it)
        }.ifTrue {
            layoutMethod
            reportMethod
            metCloudFetchTime
        }
    }

    override fun onHook() {
        layoutMethod.map {
            it.getMethodInstance(classLoader)
        }.hookAll {
            result(null)
        }
        reportMethod.map {
            it.getMethodInstance(classLoader)
        }.hookAll {
            result(null)
        }
        metCloudFetchTime?.getMethodInstance(classLoader)?.hook {
            result(Long.MAX_VALUE)
        }
    }
}