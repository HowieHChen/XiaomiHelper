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
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object WallpaperZoomSync : YukiBaseHooker() {
    private var allowZoom = false
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.ANIM_WALLPAPER_ZOOM_SYNC, extraCondition = {
            !Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
        }) {
            val launcherClz = "com.miui.home.launcher.Launcher".toClass()
            val animateWallpaperZoomMethod = launcherClz.methods.first { it.name == "animateWallpaperZoom" }
            animateWallpaperZoomMethod.hook {
                before {
                    if (this.args(0).boolean()) {
                        if (allowZoom) {
                            allowZoom = false
                        } else {
                            this.result = null
                        }
                    }
                }
            }
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