/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/home/folder/UnlockBlurSupported.java>
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

package dev.lackluster.mihelper.hook.rules.miuihome.folder

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.RectClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable


object AdvancedTexture : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.FOLDER_BLUR) {
            "com.miui.home.launcher.common.BlurUtilities".toClass().method {
                name = "isBlurSupported"
            }.hook {
                before {
                    val isDefaultIcon = "com.miui.home.launcher.DeviceConfig".toClass().method {
                        name = "isDefaultIcon"
                        modifiers { isStatic }
                    }.get().boolean()
                    if (!isDefaultIcon) this.result = true
                }
            }
            "com.miui.home.launcher.folder.LauncherFolder2x2IconContainer".toClass().method {
                name = "resolveTopPadding"
                param(RectClass)
            }.ignored().hook {
                before {
                    val view = this.instance as View
                    val paddingTop = this.instance.current().method {
                        name = "getMContainerPaddingTop"
                        superClass()
                    }.int()
                    view.setPadding(0, paddingTop, 0, 0)
                    this.result = null
                }
            }
        }
    }
}