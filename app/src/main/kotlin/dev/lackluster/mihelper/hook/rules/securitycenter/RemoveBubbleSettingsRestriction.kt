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

import android.content.Context
import android.util.ArrayMap
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.util.ArrayList

object RemoveBubbleSettingsRestriction : YukiBaseHooker() {
    private val clzBubbleApp by lazy {
        "com.miui.bubbles.settings.BubbleApp".toClassOrNull()
    }
    private val ctorBubbleApp by lazy {
        clzBubbleApp?.resolve()?.firstConstructor {
            parameters(String::class, Int::class)
            parameterCount = 2
        }
    }
    private val metSetChecked by lazy {
        clzBubbleApp?.resolve()?.firstMethodOrNull {
            name = "setChecked"
            parameters(Boolean::class)
        }
    }
    private val metGetFreeformSuggestionList by lazy {
        "android.util.MiuiMultiWindowUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getFreeformSuggestionList"
            parameters(Context::class)
            parameterCount = 1
            modifiers(Modifiers.STATIC)
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.DISABLE_BUBBLE_RESTRICT) {
            "com.miui.bubbles.settings.BubblesSettings".toClassOrNull()?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "mContext"
                }
                val fldCurrentUserId = resolve().firstFieldOrNull {
                    name = "mCurrentUserId"
                }
                resolve().firstMethodOrNull {
                    name = "getDefaultBubbles"
                }?.hook {
                    before {
                        val arrayMap = ArrayMap<String, Any>()
                        val context = fldContext?.copy()?.of(this.instance)?.get<Context>()
                        val currentUserId =fldCurrentUserId?.copy()?.of(this.instance)?.get<Int>()
                        val freeformSuggestionList = metGetFreeformSuggestionList?.invoke<ArrayList<String>>(context)
                        freeformSuggestionList?.forEach { pkg ->
                            val bubbleApp = ctorBubbleApp?.create(pkg, currentUserId)
                            metSetChecked?.copy()?.of(bubbleApp)?.invoke(true)
                            arrayMap[pkg] = bubbleApp
                        }
                        this.result = arrayMap
                    }
                }
            }
        }
    }
}