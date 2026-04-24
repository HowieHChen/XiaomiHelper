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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object HideHomepageTopBar : StaticHooker() {
    private val homePageClass by lazy {
        DexKit.findClassWithCache("simplified_homepage") {
            matcher {
                addUsingString("hot_card_tag_click", StringMatchType.Equals)
            }
        }
    }
    private val topViewField by lazy {
        DexKit.findFieldWithCache("homepage_top_view") {
            val classData = homePageClass?.let { clz ->
                DexKit.withBridge {
                    getClassData(clz.serialize())
                }
            }
            matcher {
                classData?.let {
                    declaredClass(it.name, StringMatchType.Equals)
                }
                type = "android.widget.RelativeLayout"
                modifiers(Modifier.FINAL)
            }
            searchClasses = listOfNotNull(classData)
        }
    }
    private val visibilityMethod by lazy {
        DexKit.findMethodWithCache("homepage_settings_visible") {
            val classData = homePageClass?.let { clz ->
                DexKit.withBridge {
                    getClassData(clz.serialize())
                }
            }
            matcher {
                classData?.let {
                    declaredClass(it.name, StringMatchType.Equals)
                    addCaller(it.methods.single { it1 -> it1.isConstructor }.descriptor)
                }
                paramCount = 0
                returnType = "boolean"
                modifiers(Modifier.FINAL)
            }
            searchClasses = listOfNotNull(classData)
        }
    }

    override fun onInit() {
        Preferences.Browser.HIDE_HOMEPAGE_TOP_BAR.get().also {
            updateSelfState(it)
        }.ifTrue {
            homePageClass
            topViewField
            visibilityMethod
        }
    }

    override fun onHook() {
        val topView = topViewField?.getFieldInstance(classLoader)
        if (topView != null) {
            homePageClass?.getInstance(classLoader)?.apply {
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    (topView.get(thisObject) as? View)?.visibility = View.INVISIBLE
                    topView.set(thisObject, null)
                    result(ori)
                }
            }
        } else {
            visibilityMethod?.getMethodInstance(classLoader)?.hook {
                result(false)
            }
        }
    }
}