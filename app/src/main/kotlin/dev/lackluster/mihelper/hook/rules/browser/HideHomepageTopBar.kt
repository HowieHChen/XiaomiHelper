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

package dev.lackluster.mihelper.hook.rules.browser

import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object HideHomepageTopBar : YukiBaseHooker() {
    private val homePageClass by lazy {
        DexKit.findClassWithCache("simplified_homepage") {
            matcher {
                addUsingString("hot_card_tag_click", StringMatchType.Equals)
            }
        }
    }
    private val topViewField by lazy {
        if (homePageClass == null) null
        else DexKit.findFieldWithCache("homepage_top_view") {
            val clzData = DexKit.dexKitBridge.getClassData(homePageClass!!.serialize())
            matcher {
                declaredClass(clzData!!.name, StringMatchType.Equals)
                type = "android.widget.RelativeLayout"
                modifiers(Modifier.FINAL)
            }
            searchClasses = listOf(clzData!!)
        }
    }
    private val visibilityMethod by lazy {
        if (homePageClass == null) null
        else DexKit.findMethodWithCache("homepage_settings_visible") {
            val clzData = DexKit.dexKitBridge.getClassData(homePageClass!!.serialize())
            matcher {
                declaredClass(clzData!!.name, StringMatchType.Equals)
                paramCount = 0
                returnType = "boolean"
                modifiers(Modifier.FINAL)
                addCaller(clzData.methods.single { it.isConstructor }.descriptor)
            }
            searchClasses = listOf(clzData!!)
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.HIDE_HOMEPAGE_TOP_BAR) {
            if (appClassLoader == null) return@hasEnable
            val topView = topViewField?.getFieldInstance(appClassLoader!!)
            if (topView != null) {
                homePageClass?.getInstance(appClassLoader!!)?.apply {
                    resolve().firstConstructor().hook {
                        after {
                            (topView.get(this.instance) as? View)?.visibility = View.INVISIBLE
                            topView.set(this.instance, null)
                        }
                    }
                }
            } else {
                visibilityMethod?.getMethodInstance(appClassLoader!!)?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}