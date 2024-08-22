/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/packageinstaller/UpdateSystemApp.java>
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
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable


object UpdateSystemApps : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.UPDATE_SYSTEM_APP) {
            "android.os.SystemProperties".toClassOrNull()?.method {
                name = "getBoolean"
                paramCount = 2
                param(StringClass, BooleanType)
            }?.hook {
                before {
                    if ("persist.sys.allow_sys_app_update" == this.args(0).string()) {
                        this.result = true
                    }
                }
            }
            // Untested!
            var letterClz = 'a'
            for (i in 0..25) {
                val clz = "j2.$letterClz".toClassOrNull() ?: continue
                val length = clz.declaredMethods.size
                if (length in 15..25) {
                    for (dMethod in clz.declaredMethods) {
                        runCatching {
                            if (dMethod.parameterTypes[0] == ApplicationInfoClass) {
                                dMethod.hook {
                                    replaceToFalse()
                                }
                            }
                        }
                    }
                }
                letterClz = (letterClz + 1)
            }
        }
    }
}