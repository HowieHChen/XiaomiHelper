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

package dev.lackluster.mihelper.hook.rules.themes

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : StaticHooker() {
    private val tryAdSplashMethod by lazy {
        DexKit.findMethodWithCache("try_ad_splash") {
            matcher {
                addUsingString("trySplash: calling package = ", StringMatchType.Equals)
                addUsingString("tryAdSplash : start", StringMatchType.Equals)
                addUsingString("tryAdSplash : end", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.Themes.SKIP_SPLASH.get().also {
            updateSelfState(it)
        }.ifTrue {
            tryAdSplashMethod
        }
    }

    override fun onHook() {
        tryAdSplashMethod?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}