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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object RemoveBubbleSettingsRestriction : StaticHooker() {
    private val clzBubbleApp by "com.miui.bubbles.settings.BubbleApp".lazyClassOrNull()
    private val ctorBubbleApp by lazy {
        clzBubbleApp?.resolve()?.firstConstructor {
            parameters(String::class, Int::class)
            parameterCount = 2
        }?.toTyped()
    }
    private val metSetChecked by lazy {
        clzBubbleApp?.resolve()?.firstMethodOrNull {
            name = "setChecked"
            parameters(Boolean::class)
        }?.toTyped<Unit>()
    }
    private val metGetFreeformSuggestionList by lazy {
        "android.util.MiuiMultiWindowUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getFreeformSuggestionList"
            parameters(Context::class)
            parameterCount = 1
            modifiers(Modifiers.STATIC)
        }?.toTyped<List<String>>()
    }

    override fun onInit() {
        updateSelfState(Preferences.SecurityCenter.DISABLE_BUBBLE_RESTRICT.get())
    }

    override fun onHook() {
        "com.miui.bubbles.settings.BubblesSettings".toClassOrNull()?.apply {
            val fldContext = resolve().firstFieldOrNull {
                name = "mContext"
            }?.toTyped<Context>()
            val fldCurrentUserId = resolve().firstFieldOrNull {
                name = "mCurrentUserId"
            }?.toTyped<Int>()
            resolve().firstMethodOrNull {
                name = "getDefaultBubbles"
            }?.hook {
                val arrayMap = ArrayMap<String, Any>()
                val context = fldContext?.get(thisObject)
                val currentUserId =fldCurrentUserId?.get(thisObject)
                val freeformSuggestionList = metGetFreeformSuggestionList?.invoke(context)
                freeformSuggestionList?.forEach { pkg ->
                    val bubbleApp = ctorBubbleApp?.newInstance(pkg, currentUserId)
                    metSetChecked?.invoke(bubbleApp, true)
                    arrayMap[pkg] = bubbleApp
                }
                result(arrayMap)
            }
        }
    }
}