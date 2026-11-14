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
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object HideHomeElement : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    @Volatile
    private var hideRec = false
    @Volatile
    private var hideCommon = false
    @Volatile
    private var hidePopular = false

    private fun getBlacklist(): List<String> {
        return listOfNotNull(
            "com.miui.common.card.models.FuncListBannerCardModel".takeIf { hideRec },
            "com.miui.common.card.models.CommonlyUsedFunctionCardModel".takeIf { hideCommon },
            "com.miui.common.card.models.CommonlyUsedFunctionCardModelNew".takeIf { hideCommon },
            "com.miui.common.card.models.CommonlyUsedFunctionCardTitleModel".takeIf { hideCommon },
            "com.miui.common.card.models.PopularActionCardModel".takeIf { hidePopular },
        )
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.HIDE_HOME_REC.observe {
                hideRec = it
                updateSelfState(hideRec || hideCommon || hidePopular)
            }
        )
        registerDisposeCleanup(
            Preferences.SecurityCenter.HIDE_HOME_COMMON.observe {
                hideCommon = it
                updateSelfState(hideRec || hideCommon || hidePopular)
            }
        )
        registerDisposeCleanup(
            Preferences.SecurityCenter.HIDE_HOME_POPULAR.observe {
                hidePopular = it
                updateSelfState(hideRec || hideCommon || hidePopular)
            }
        )
    }

    override fun onStart() {
        "com.miui.common.card.CardViewRvAdapter".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "addAll"
                parameters("java.util.List")
            }?.hook {
                val newArgs = args.toTypedArray()
                val list = newArgs[0] as? List<*>
                val removeElements = getBlacklist()
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
                val removeElements = getBlacklist()
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