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

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.utils.MiBlurUtils

object BlurRefactorEntry : YukiBaseHooker() {
    var wallpaperZoomManager : WallpaperZoomManager? = null

    private val allAppsState by lazy {
        "com.miui.home.launcher.LauncherState".toClass().field {
            name = "ALL_APPS"
            modifiers { isStatic }
        }.get().any()
    }
    // Configuration
    private val ENABLED = Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
    val EXTRA_COMPATIBILITY = Prefs.getBoolean(Refactor.EXTRA_COMPATIBILITY, HomeRefactor.EXTRA_COMPATIBILITY)
    val MINUS_OVERLAP = Prefs.getBoolean(Refactor.MINUS_OVERLAP, HomeRefactor.MINUS_OVERLAP)
    val MINUS_BLUR = Prefs.getBoolean(Refactor.MINUS_BLUR, HomeRefactor.MINUS_BLUR)
    val MINUS_DIM = Prefs.getBoolean(Refactor.MINUS_DIM, HomeRefactor.MINUS_DIM)
    override fun onHook() {
        if (!ENABLED) {
            return
        }
        if (!MiBlurUtils.supportBackgroundBlur()) {
            YLog.warn("The High-quality materials function is unsupported.")
            return
        }
        loadHooker(BlurController)
        hasEnable(Refactor.SYNC_WALLPAPER_SCALE, HomeRefactor.SYNC_WALLPAPER_SCALE) {
            loadHooker(SyncWallpaperScale)
        }
        hasEnable(Refactor.SHOW_LAUNCH_IN_FOLDER, HomeRefactor.SHOW_LAUNCH_IN_FOLDER) {
            loadHooker(ShowLaunchInFolder)
        }
        hasEnable(Refactor.SHOW_LAUNCH_IN_RECENTS, HomeRefactor.SHOW_LAUNCH_IN_RECENTS) {
            loadHooker(ShowLaunchInRecents)
        }
    }

    fun isFolderShowing(launcher: Any): Boolean {
        return (XposedHelpers.callMethod(launcher, "isFolderShowing") as Boolean)
    }

    fun isInNormalEditing(launcher: Any): Boolean {
        return (XposedHelpers.callMethod(launcher, "isInNormalEditing") as Boolean)
    }

    fun isAllAppsShowing(launcher: Any): Boolean {
        return (XposedHelpers.getObjectField(
            XposedHelpers.getObjectField(launcher, "mStateManager"),
            "mState"
        ) == allAppsState)
    }
}