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

package dev.lackluster.mihelper.hook.rules.android

import android.content.pm.ApplicationInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device
import kotlin.getValue

object DarkModeForAll : StaticHooker() {
    private val fldI18n by lazy {
        "miui.os.Build".toClass().resolve().firstField {
            name = "IS_INTERNATIONAL_BUILD"
            modifiers(Modifiers.STATIC)
        }
    }
    private val clzForceDarkAppListManager by "com.android.server.ForceDarkAppListManager".lazyClass()
    private val metGetDarkModeAppList by lazy {
        clzForceDarkAppListManager.resolve().optional().firstMethodOrNull {
            name = "getDarkModeAppList"
        }
    }
    private val metShouldShowInSettings by lazy {
        clzForceDarkAppListManager.resolve().optional().firstMethodOrNull {
            name = "shouldShowInSettings"
        }
    }
    private val metIsSystemApp by lazy {
        ApplicationInfo::class.resolve().firstMethodOrNull {
            name = "isSystemApp"
            returnType = Boolean::class
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        updateSelfState(
            Preferences.System.DISABLE_FORCE_DARK_WHITELIST.get() && !Device.isInternationalBuild
        )
    }

    override fun onHook() {
        metGetDarkModeAppList?.hook {
            val realIsInternationalBuild = Device.isInternationalBuild
            fldI18n.set(true)
            val ori = proceed()
            fldI18n.set(realIsInternationalBuild)
            result(ori)
        }
        metShouldShowInSettings?.hook {
            val info = getArg(0) as? ApplicationInfo
            val isSystemApp = info?.let { info ->
                metIsSystemApp?.invoke(info)
            } ?: false
            result(
                !(info == null || isSystemApp || info.uid < 10000)
            )
        }
    }
}