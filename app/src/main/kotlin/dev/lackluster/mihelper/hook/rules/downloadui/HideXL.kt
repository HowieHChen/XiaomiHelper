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
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideXL : YukiBaseHooker() {
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
        DexKit.dexKitBridge.getClassData("com.android.providers.downloads.ui.DownloadListDelegate")
    }
    private val actionBarInitMethod by lazy {
        DexKit.findMethodWithCache("action_bar_init") {
            matcher {
                returnType = "void"
                paramCount = 4
                paramTypes("android.app.Activity" ,"android.view.Window", null, "android.widget.ImageView")
                addUsingString("not found ACTION_BAR_MOVABLE_CONTAINER ", StringMatchType.Equals)
            }
            searchClasses = downloadListDelegateClass?.let { listOf(it) }
        }
    }

    override fun onHook() {
        hasEnable (Pref.Key.DownloadUI.HIDE_XL) {
            if (appClassLoader == null) return@hasEnable
            val fldXLTextView = "com.android.providers.downloads.ui.DownloadListDelegate".toClassOrNull()
                ?.resolve()?.firstFieldOrNull {
                    type(TextView::class)
                }
            actionBarInitMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    this.args(3).setNull()
                }
                after {
                    val fieldXLTextView = fldXLTextView?.copy()?.of(this.instance) ?: return@after
                    fieldXLTextView.get<TextView>()?.let { tv ->
                        tv.parent?.let { parent ->
                            if (parent is ViewGroup) {
                                parent.removeView(tv)
                            }
                        }
                    }
                    fieldXLTextView.set(null)
                }
            }
            clzHomeFragment?.getInstance(appClassLoader!!)?.apply {
                val fldIcon = resolve().firstFieldOrNull {
                    type(ImageView::class)
                }?.self?.apply {
                    makeAccessible()
                }
                resolve().firstMethodOrNull {
                    parameterCount = 1
                    parameters("miuix.appcompat.app.ActionBar")
                }?.hook {
                    after {
                        (fldIcon?.get(this.instance) as? ImageView)?.apply {
                            isClickable = false
                            visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}