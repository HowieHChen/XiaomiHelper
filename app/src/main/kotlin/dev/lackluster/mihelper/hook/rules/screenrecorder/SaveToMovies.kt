/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/screenrecorder/SaveToMovies.kt>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.screenrecorder

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SaveToMovies : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.ScreenRecorder.SAVE_TO_MOVIES) {
            "android.os.Environment".toClass().field {
                name = "DIRECTORY_DCIM"
                modifiers { isStatic }
            }.get().set("Movies")
            "android.content.ContentValues".toClass().method {
                name = "put"
                param(StringClass, StringClass)
            }.hook {
                before {
                    if (this.args(0).string() == "relative_path") {
                        this.args(1).set(this.args(1).string().replace("DCIM", "Movies"))
                    }
                }
            }
        }
    }
}