/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * This file is part of XiaomiHelper project

 * This file references Hyper5GSwitch <https://github.com/buffcow/Hyper5GSwitch/blob/2244de0a2675bdaa40b1a5c6c42da16537718ef4/app/src/main/kotlin/cn/buffcow/hyper5g/MainModule.kt>
 * @author qingyu
 * <p>Create on 2025/10/09 15:15</p>
 *
 * Copyright 2025 qingyu

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.lackluster.mihelper.hook.rules.systemui.plugin

import android.content.ComponentName
import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.toTyped

object PluginFactory : StaticHooker() {
    private const val PACKAGE_NAME_PLUGIN = Scope.SYSTEM_UI_PLUGIN
    private const val CLASS_NAME_FOCUS_NOTIFICATION = "miui.systemui.notification.FocusNotificationPluginImpl"
    private const val CLASS_NAME_CONTROL_CENTER = "miui.systemui.controlcenter.MiuiControlCenter"

    override fun onInit() {
        $$"com.android.systemui.shared.plugins.PluginInstance$PluginFactory".toClassOrNull()?.apply {
            val fldComponentName = resolve().firstFieldOrNull {
                name = "mComponentName"
            }?.toTyped<ComponentName>()
            resolve().firstMethodOrNull {
                name = "createPluginContext"
            }?.hook {
                val ori = proceed()
                val context = ori as? Context
                val classLoader = context?.classLoader ?: return@hook result(ori)
                val componentName = fldComponentName?.get(thisObject)
                if (componentName?.packageName == PACKAGE_NAME_PLUGIN) {
                    d { "createPluginContext ${componentName.className}" }
                    when (componentName.className) {
                        CLASS_NAME_FOCUS_NOTIFICATION -> {
                            listOf(
                                IslandWhitelist,
                            ).forEach { hooker ->
                                attach(hooker, classLoader)
                            }
                        }
                        CLASS_NAME_CONTROL_CENTER -> {
                            listOf(
                                HideEditButton,
                            ).forEach { hooker ->
                                attach(hooker, classLoader)
                            }
                        }
                    }
                }
                result(ori)
            }
        }
    }
}