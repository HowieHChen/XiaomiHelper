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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object BlockUploadAppInfo : YukiBaseHooker() {
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
                addUsingString("\$apkInfo", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.BLOCK_UPLOAD_INFO) {
            if (appClassLoader == null) return@hasEnable
            layoutMethod.forEach {
                val instance = it.getMethodInstance(appClassLoader!!)
                instance.hook {
                    intercept()
                }
            }
            reportMethod.forEach {
                val instance = it.getMethodInstance(appClassLoader!!)
                instance.hook {
                    intercept()
                }
            }
        }
    }
}