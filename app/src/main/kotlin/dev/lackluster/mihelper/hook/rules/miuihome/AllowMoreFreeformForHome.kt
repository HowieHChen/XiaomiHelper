/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MaxFreeForm <https://github.com/YifePlayte/MaxFreeForm/blob/main/app/src/main/java/com/yifeplayte/maxfreeform/hook/hooks/singlepackage/home/UnlockEnterSmallWindow.kt>
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

package dev.lackluster.mihelper.hook.rules.miuihome

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AllowMoreFreeformForHome : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Android.ALLOW_MORE_FREEFORM) {
            "com.miui.home.launcher.RecentsAndFSGestureUtils".toClassOrNull()?.apply {
                method {
                    name = "canTaskEnterMiniSmallWindow"
                }.ignored().giveAll().hookAll {
                    replaceToTrue()
                }
                method {
                    name = "canTaskEnterSmallWindow"
                }.ignored().giveAll().hookAll {
                    replaceToTrue()
                }
            }
            "com.miui.home.smallwindow.SmallWindowStateHelperUseManager".toClassOrNull()?.apply {
                method {
                    name = "canEnterMiniSmallWindow"
                }.ignored().hook {
                    replaceAny {
                        (this.instance.current().field {
                            name = "mMiniSmallWindowInfoSet"
                        }.any() as ArraySet<*>).isEmpty()
                    }
                }
            }
        }
    }
}