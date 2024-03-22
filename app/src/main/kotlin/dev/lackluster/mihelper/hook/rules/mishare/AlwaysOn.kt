/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/mishare/NoAutoTurnOff.kt>
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 * Add more hooks, modified by HowieHChen (howie.dev@outlook.com) on 03/20/2024

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

package dev.lackluster.mihelper.hook.rules.mishare

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit.dexKitBridge
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object AlwaysOn : YukiBaseHooker() {
    private val autoTurnNull1 by lazy {
        dexKitBridge.findMethod {
            matcher {
                addUsingString("MiShareService", StringMatchType.Equals)
                addUsingString("EnabledState", StringMatchType.Equals)
                addUsingNumber(600000L)
                paramCount = 0
                modifiers = Modifier.PUBLIC
            }
        }.singleOrNull()
    }
    private val wakeLock by lazy {
        dexKitBridge.findClass {
            matcher {
                addUsingString("mishare:advertise_lock", StringMatchType.Equals)
            }
        }.findMethod {
            matcher {
                paramCount = 2
                modifiers = Modifier.STATIC
            }
        }.singleOrNull()
    }
    private val autoTurnNull2 by lazy {
        dexKitBridge.findMethod {
            matcher {
                addUsingString("com.miui.mishare.action.GRANT_NFC_TOUCH_PERMISSION", StringMatchType.Equals)
                addUsingNumber(600000L)
                paramCount = 0
                modifiers = Modifier.PRIVATE
            }
        }.singleOrNull()
    }
    private val checkRunJobsClazz by lazy {
        dexKitBridge.findClass {
            matcher {
                addUsingString("no more running jobs, will release after", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val shareToast by lazy {
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("null context", StringMatchType.Equals)
                    addUsingString("cta_agree", StringMatchType.Equals)
                }
                returnType = "boolean"
                paramTypes = listOf("android.content.Context", "java.lang.String")
                paramCount = 2
            }
        }.singleOrNull()
    }
    private val shareToast2 by lazy {
        dexKitBridge.findMethod {
            matcher {
                returnType = "void"
                paramTypes = listOf("android.content.Context", "java.lang.CharSequence", "int")
                paramCount = 3
                modifiers = Modifier.STATIC
            }
        }.singleOrNull()
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.MiShare.ALWAYS_ON) {
            if (appClassLoader == null) return@hasEnable
            autoTurnNull1?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            wakeLock?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            runCatching {
                autoTurnNull2?.getMethodInstance(appClassLoader!!)?.hook {
                    before {
                        (this.current().field {
                            type = HandlerClass
                        }.any() as? Handler)?.removeCallbacksAndMessages(null)
                    }
                }
            }
            runCatching {
                checkRunJobsClazz?.javaClass?.field {
                    type = IntType
                    modifiers { isStatic }
                }?.get()?.set(999999999)
            }
            shareToast?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    if (this.args(1).string() == "security_agree") {
                        this.result = false
                    }
                }
            }
            shareToast2?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    val context = this.args(0).any() as Context
                    val stringSecurityAgree = String.format(context.getString(
                        context.resources.getIdentifier("toast_auto_close_in_minutes", "string", context.packageName)
                    ), 10)
                    if (this.args(1).any().toString() == stringSecurityAgree) {
                        this.result = null
                    }
                }
            }
        }
    }
}