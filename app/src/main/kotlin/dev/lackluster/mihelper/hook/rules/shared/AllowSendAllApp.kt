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

package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AllowSendAllApp : YukiBaseHooker() {
    private val subScreen by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("support_all_app_sub_screen", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.singleOrNull()
    }
    private val relayAppMessageClazz by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("RelayAppMessage{type=", StringMatchType.Equals)
            }
        }.singleOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiMirror.CONTINUE_ALL_TASKS) {
            when (packageName) {
                Scope.MI_LINK -> {
                    "com.xiaomi.mirror.synergy.MiuiSynergySdk".toClass().method {
                        name = "isSupportSendApp"
                    }.hook {
                        after {
                            this.result = true
                        }
                    }
                }
                Scope.MI_MIRROR -> {
                    "com.xiaomi.mirror.message.proto.RelayApp\$RelayApplication".toClass().apply {
                        method {
                            name = "getIsHideIcon"
                        }.hook {
                            replaceToFalse()
                        }
                        method {
                            name = "getSupportHandOff"
                        }.hook {
                            replaceToTrue()
                        }
                        method {
                            name = "getSupportSubScreen"
                        }.hook {
                            replaceToTrue()
                        }
                    }
                    subScreen?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                        replaceToTrue()
                    }
                    val relayAppMessageClz = relayAppMessageClazz?.getInstance(appClassLoader ?: return@hasEnable)
                    val booleans = relayAppMessageClz?.field {
                        type = BooleanType
                    }?.giveAll()
                    booleans?.sortBy {
                        it.name
                    }
                    val fieldName = booleans?.get(1)?.name ?: return@hasEnable
                    relayAppMessageClz.method {
                        returnType = relayAppMessageClz
                    }.hookAll {
                        after {
                            this.result?.current(true)?.field {
                                name = fieldName
                            }?.setFalse()
                        }
                    }
                }
            }
        }
    }
}