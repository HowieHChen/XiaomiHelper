/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxMiPad <https://github.com/YifePlayte/MaxMiPad/blob/main/app/src/main/java/com/yifeplayte/maxmipadinput/hook/hooks/singlepackage/android/DisableFixedOrientation.kt>
 * Copyright (C) 2023 YifePlayte

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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisableFixedOrientation : YukiBaseHooker() {
    private val shouldDisableFixedOrientationList by lazy {
        Prefs.getStringSet(Pref.Key.Android.BLOCK_FIXED_ORIENTATION_LIST, mutableSetOf())
    }
    override fun onHook() {
        hasEnable(Pref.Key.Android.BLOCK_FIXED_ORIENTATION) {
            "com.android.server.wm.MiuiFixedOrientationController".toClass().method {
                name = "shouldDisableFixedOrientation"
            }.hook {
                before {
                    if (this.args(0).string() in shouldDisableFixedOrientationList) {
                        this.result = true
                    }
                }
            }
        }
    }
}