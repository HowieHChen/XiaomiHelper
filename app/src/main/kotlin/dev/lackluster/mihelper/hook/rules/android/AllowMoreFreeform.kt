/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxFreeForm <https://github.com/YifePlayte/MaxFreeForm/blob/main/app/src/main/java/com/yifeplayte/maxfreeform/hook/hooks/singlepackage/android/UnlockFreeformQuantityLimit.kt>
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
import dev.lackluster.mihelper.utils.factory.hasEnable

object AllowMoreFreeform : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Android.ALLOW_MORE_FREEFORM) {
            "com.android.server.wm.MiuiFreeFormStackDisplayStrategy".toClass().apply {
                method {
                    name = "getMaxMiuiFreeFormStackCount"
                }.ignored().hook {
                    replaceTo(256)
                }
                method {
                    name = "getMaxMiuiFreeFormStackCountForFlashBack"
                }.ignored().hook {
                    replaceTo(256)
                }
                method {
                    name = "shouldStopStartFreeform"
                }.ignored().hook {
                    replaceToFalse()
                }
            }

            "miui.app.MiuiFreeFormManager".toClass().method {
                name = "getMaxMiuiFreeFormStackCountForFlashBack"
            }.hook {
                replaceTo(256)
            }
        }
    }
}