/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/powerkeeper/CustomRefreshRate.kt>
 * and <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/misettings/CustomRefreshRate.kt>
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

package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Settings.UNLOCK_CUSTOM_REFRESH) {
            if (appClassLoader == null) return@hasEnable
            when(packageName) {
                Scope.POWER_KEEPER -> {
                    DexKit.dexKitBridge.findMethod {
                        matcher {
                            addUsingString("custom_mode_switch", StringMatchType.Equals)
                            addUsingString("fucSwitch", StringMatchType.Equals)
                        }
                    }.filter { it.isMethod }.map { it.getMethodInstance(appClassLoader!!) }.hookAll {
                        before {
                            this.instance.current().field {
                                name = "mIsCustomFpsSwitch"
                            }.set(true.toString())
                        }
                    }
                }
                Scope.MI_SETTINGS -> {
                    DexKit.dexKitBridge.findMethod {
                        matcher {
                            addUsingString("btn_preferce_category", StringMatchType.Equals)
                        }
                    }.singleOrNull()?.getMethodInstance(appClassLoader!!)?.hook {
                        before {
                            this.args(0).setTrue()
                        }
                    }
                    "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity".toClass().field {
                        modifiers {
                            isStatic && isFinal
                        }
                    }.get().setTrue()
                }
            }
        }
    }
}