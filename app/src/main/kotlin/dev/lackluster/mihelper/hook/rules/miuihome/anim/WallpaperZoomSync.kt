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

package dev.lackluster.mihelper.hook.rules.miuihome.anim

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object WallpaperZoomSync : YukiBaseHooker() {
    private var allowZoom = false
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ANIM_WALLPAPER_ZOOM_SYNC, extraCondition = {
            !(Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false) &&
                    Prefs.getBoolean(Pref.Key.MiuiHome.Refactor.SYNC_WALLPAPER_SCALE, false))
        }) {
            XposedHelpers.findAndHookMethod(
                "com.miui.home.launcher.Launcher", this.appClassLoader,
                "animateWallpaperZoom", BooleanType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        if (param?.args?.get(0) == true) {
                            if (allowZoom) {
                                allowZoom = false
                            } else {
                                param.result = null
                            }
                        }
                    }
                }
            )
            "com.miui.home.recents.QuickstepAppTransitionManagerImpl".toClass().method {
                name = "startLauncherContentAnimator"
            }.hook {
                before {
                    if (this.args(0).boolean()) {
                        allowZoom = true
                    }
                }
            }
            "com.miui.home.recents.OverviewState".toClass().method {
                name = "onStateEnabled"
            }.hook {
                before {
                    allowZoom = true
                }
            }
            "com.miui.home.recents.NavStubView".toClass().apply {
                method {
                    name = "enterHomeHoldState"
                }.hook {
                    before {
                        allowZoom = true
                    }
                }
                method {
                    name = "exitRecentsHoldState"
                }.hook {
                    before {
                        allowZoom = true
                    }
                }
            }
            if (!Device.isPad) {
                "com.miui.home.recents.LauncherAppTransitionManagerImpl".toClass().method {
                    name = "composeRecentsLaunchAnimator"
                }.hook {
                    allowZoom = true
                }
            }
        }
    }
}