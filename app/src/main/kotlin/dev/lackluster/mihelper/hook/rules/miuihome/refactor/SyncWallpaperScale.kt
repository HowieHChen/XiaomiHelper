/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2024 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import android.content.Context
import android.os.IBinder
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.wallpaperZoomManager
import java.lang.reflect.Method
import java.util.concurrent.Executor

object SyncWallpaperScale : YukiBaseHooker() {
    private val setWallpaperZoomOut by lazy {
        "com.miui.home.launcher.wallpaper.WallpaperZoomManagerKt".toClass().method{
            name = "findUpdateZoomMethod"
            modifiers { isStatic }
        }.get().call() as Method
    }

    override fun onHook() {
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "animateWallpaperZoom", BooleanType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    param?.result = null
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "abortWallpaperAnimations",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    param?.result = null
                }
            }
        )
        "com.miui.home.launcher.wallpaper.WallpaperZoomManager".toClass().apply {
            constructor().hook {
                after {
                    val context = this.instance.current().field { name = "context" }.any() as? Context ?: return@after
                    val windowToken = this.instance.current().field { name = "mWindowToken" }.any() as? IBinder ?: return@after
                    val mWallPaperExecutor = this.instance.current().field { name = "mWallPaperExecutor" }.any() as? Executor ?: return@after
                    wallpaperZoomManager = WallpaperZoomManager(context, windowToken, setWallpaperZoomOut, mWallPaperExecutor)
                }
            }
            method {
                name = "animateWallpaperZoom"
            }.hook {
                intercept()
            }
            method {
                name = "abortAnimations"
            }.hook {
                intercept()
            }
            method {
                name = "animateZoomOutTo"
            }.hook {
                intercept()
            }
            method {
                name = "setWallpaperZoomOut"
            }.hook {
                intercept()
            }
        }
    }
}