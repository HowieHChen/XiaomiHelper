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

package dev.lackluster.mihelper.hook.rules.securitycenter

import android.annotation.SuppressLint
import android.content.Context
import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RemoveBubbleSettingsRestriction : YukiBaseHooker() {
    private val bubbleAppClass by lazy {
        "com.miui.bubbles.settings.BubbleApp".toClass()
    }
    private val getFreeformSuggestionListMethod by lazy {
        "android.util.MiuiMultiWindowUtils".toClass().method {
            name = "getFreeformSuggestionList"
            param(ContextClass)
            paramCount = 1
            modifiers { isStatic }
        }
    }

    @SuppressLint("PrivateApi")
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.DISABLE_BUBBLE_RESTRICT) {
            "com.miui.bubbles.settings.BubblesSettings".toClass().method {
                name = "getDefaultBubbles"
            }.hook {
                before {
                    val arrayMap = ArrayMap<String, Any>()
                    val context = this.instance.current().field {
                        name = "mContext"
                    }.any() as Context
                    val currentUserId = this.instance.current().field {
                        name = "mCurrentUserId"
                    }.int()
                    val freeformSuggestionList = getFreeformSuggestionListMethod.get().list<String>(context)
                    if (freeformSuggestionList.isNotEmpty()) {
                        for (str in freeformSuggestionList) {
                            val bubbleApp = bubbleAppClass.constructor().get().call(str, currentUserId)
                            bubbleApp?.current()?.method {
                                name = "setChecked"
                                param(BooleanType)
                            }?.call(true)
                            arrayMap[str] = bubbleApp
                        }
                    }
                    this.result = arrayMap
                }
            }
        }
    }
}