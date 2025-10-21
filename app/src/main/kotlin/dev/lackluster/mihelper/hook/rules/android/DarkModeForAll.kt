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

package dev.lackluster.mihelper.hook.rules.android

import android.content.pm.ApplicationInfo
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object DarkModeForAll : YukiBaseHooker() {
    private val i18nFiled by lazy {
        "miui.os.Build".toClass().resolve()
            .firstField {
                name = "IS_INTERNATIONAL_BUILD"
                modifiers(Modifiers.STATIC)
            }
    }
    override fun onHook() {
        hasEnable(Pref.Key.Android.BLOCK_FORCE_DARK_WHITELIST, extraCondition = { !Device.isInternationalBuild }) {
            "com.android.server.ForceDarkAppListManager".toClass().apply {
                resolve()
                    .optional()
                    .firstMethodOrNull {
                        name = "getDarkModeAppList"
                    }
                    ?.hook {
                        before {
                            i18nFiled.set(true)
                        }
                        after {
                            i18nFiled.set(Device.isInternationalBuild)
                        }
                    }
                resolve()
                    .optional()
                    .firstMethodOrNull {
                        name = "shouldShowInSettings"
                    }
                    ?.hook {
                        before {
                            val info = this.args(0).cast<ApplicationInfo>()
                            val isSystemApp = info?.asResolver()
                                ?.firstMethodOrNull {
                                    name = "isSystemApp"
                                    returnType = Boolean::class
                                }
                                ?.invoke<Boolean>() ?: false
                            this.result = !(info == null || isSystemApp || info.uid < 10000)
                        }
                    }
            }
        }
    }
}