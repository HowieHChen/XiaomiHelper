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

package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet

object HideHomeElement : StaticHooker() {
    private val hideRec by Preferences.SecurityCenter.HIDE_HOME_REC.lazyGet()
    private val hideCommon by Preferences.SecurityCenter.HIDE_HOME_COMMON.lazyGet()
    private val hidePopular by Preferences.SecurityCenter.HIDE_HOME_POPULAR.lazyGet()

    private val removeElements by lazy {
        mutableListOf<String>().apply {
            if (hideRec) {
                add("com.miui.common.card.models.FuncListBannerCardModel")
            }
            if (hideCommon) {
                add("com.miui.common.card.models.CommonlyUsedFunctionCardModel")
                add("com.miui.common.card.models.CommonlyUsedFunctionCardModelNew")
                add("com.miui.common.card.models.CommonlyUsedFunctionCardTitleModel")
            }
            if (hidePopular) {
                add("com.miui.common.card.models.PopularActionCardModel")
            }
        }
    }

    override fun onInit() {
        updateSelfState(hideRec || hideCommon || hidePopular)
    }

    override fun onHook() {
        "com.miui.common.card.CardViewRvAdapter".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "addAll"
                parameters("java.util.List")
            }?.hook {
                val newArgs = args.toTypedArray()
                val list = newArgs[0] as? List<*>
                list?.filter {
                    it != null && !removeElements.contains(it.javaClass.name)
                }?.toList()?.let {
                    newArgs[0] = it
                }
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "setModelList"
                parameters("java.util.ArrayList")
            }?.hook {
                val newArgs = args.toTypedArray()
                val list = newArgs[0] as? List<*>
                list?.filter {
                    it != null && !removeElements.contains(it.javaClass.name)
                }?.toMutableList()?.let {
                    newArgs[0] = it
                }
                result(proceed(newArgs))
            }
        }
    }
}