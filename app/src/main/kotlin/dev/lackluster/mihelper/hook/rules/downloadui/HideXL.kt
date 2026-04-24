/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.downloadui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object HideXL : StaticHooker() {
    private val clzHomeFragment by lazy {
        DexKit.findClassWithCache("fragment") {
            matcher {
                addUsingString("tab_index", StringMatchType.Equals)
                addUsingString("Alt+Enter", StringMatchType.Equals)
                superClass("miuix.appcompat.app.Fragment")
            }
        }
    }
    private val downloadListDelegateClass by lazy {
        DexKit.withBridge { getClassData("com.android.providers.downloads.ui.DownloadListDelegate") }
    }
    private val actionBarInitMethod by lazy {
        DexKit.findMethodWithCache("action_bar_init") {
            matcher {
                returnType = "void"
                paramCount = 4
                paramTypes("android.app.Activity" ,"android.view.Window", null, "android.widget.ImageView")
                addUsingString("not found ACTION_BAR_MOVABLE_CONTAINER ", StringMatchType.Equals)
            }
            searchClasses = listOfNotNull(downloadListDelegateClass)
        }
    }

    override fun onInit() {
        Preferences.DownloadUI.HIDE_XL.get().also {
            updateSelfState(it)
        }.ifTrue {
            actionBarInitMethod
            clzHomeFragment
        }
    }

    override fun onHook() {
        val fldXLTextView = "com.android.providers.downloads.ui.DownloadListDelegate".toClassOrNull()
            ?.resolve()?.firstFieldOrNull {
                type(TextView::class)
            }?.toTyped<TextView>()
        actionBarInitMethod?.getMethodInstance(classLoader)?.hook {
            val newArgs = args.toTypedArray()
            newArgs[3] = null
            val ori = proceed(newArgs)
            fldXLTextView?.get(thisObject)?.let {
                val parent = it.parent
                if (parent is ViewGroup) {
                    parent.removeView(it)
                }
                fldXLTextView.set(thisObject, null)
            }
            result(ori)
        }
        clzHomeFragment?.getInstance(classLoader)?.apply {
            val fldIcon = resolve().firstFieldOrNull {
                type(ImageView::class)
            }?.toTyped<ImageView>()
            resolve().firstMethodOrNull {
                parameterCount = 1
                parameters("miuix.appcompat.app.ActionBar")
            }?.hook {
                val ori = proceed()
                fldIcon?.get(thisObject)?.apply {
                    isClickable = false
                    visibility = View.GONE
                }
                result(ori)
            }
        }
    }
}