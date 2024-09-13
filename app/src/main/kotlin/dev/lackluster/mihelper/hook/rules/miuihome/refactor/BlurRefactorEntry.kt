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
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ConfigurationClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.hook.view.MiBlurView
import dev.lackluster.mihelper.utils.MiBlurUtils

object BlurRefactorEntry : YukiBaseHooker() {
    var transitionBlurView: MiBlurView? = null
    var folderBlurView: MiBlurView? = null
    var wallpaperBlurView: MiBlurView? = null
    var minusBlurView: MiBlurView? = null
    var wallpaperZoomManager : WallpaperZoomManager? = null
    private var screenCornerRadius: Int = 84
    private val clzBlurUtilities by lazy {
        "com.miui.home.launcher.common.BlurUtilities".toClass()
    }
    private val clzWindowCornerRadiusUtil by lazy {
        "com.miui.home.recents.util.WindowCornerRadiusUtil".toClass()
    }
    private val fieldAllAppsState by lazy {
        "com.miui.home.launcher.LauncherState".toClass().field {
            name = "ALL_APPS"
            modifiers { isStatic }
        }.get().any()
    }
    // Configuration
    private val ENABLED = Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
    private val MINUS_BLUR_TYPE = Prefs.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, 0)
    private val MINUS_MODE = Prefs.getInt(Pref.Key.MiuiHome.Refactor.MINUS_MODE, 0)
    override fun onHook() {
        if (!ENABLED && MINUS_BLUR_TYPE != 2) {
            return
        }
        if (!MiBlurUtils.supportBackgroundBlur()) {
            YLog.warn("The High-quality materials function is unsupported.")
            return
        }
        initBlurLayer()
        if (ENABLED) {
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
        loadHooker(BlurMinus)
        loadHooker(ShowLaunchInMinus)
    }

    private fun initBlurLayer() {
        // Get and update screen corner radius
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "onAttachedToWindow",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    screenCornerRadius = XposedHelpers.callStaticMethod(
                        clzWindowCornerRadiusUtil,
                        "getCornerRadius"
                    ) as Int
                    folderBlurView?.setCornerRadius(screenCornerRadius)
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "onConfigurationChanged", ConfigurationClass,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    screenCornerRadius = XposedHelpers.callStaticMethod(
                        clzWindowCornerRadiusUtil,
                        "getCornerRadius"
                    ) as Int
                    folderBlurView?.setCornerRadius(screenCornerRadius)
                }
            }
        )
        // Inject blur views
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "onCreate", BundleClass,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val launcher = param?.thisObject ?: return
                    if (!XposedHelpers.getStaticBooleanField(clzBlurUtilities, "IS_BACKGROUND_BLUR_ENABLED")) {
                        if (!Prefs.getBoolean(Pref.Key.MiuiHome.FAKE_PREMIUM, false)) {
                            YLog.warn("The High-quality materials function is disabled.")
                        }
                        XposedHelpers.setStaticBooleanField(clzBlurUtilities, "IS_BACKGROUND_BLUR_ENABLED", true)
                    }
                    val context = launcher as Context
                    // Init views
                    if (ENABLED) {
                        transitionBlurView = MiBlurView(context)
                        folderBlurView = MiBlurView(context)
                        wallpaperBlurView = MiBlurView(context)
                        minusBlurView = MiBlurView(context)
                    } else if (MINUS_BLUR_TYPE == 2) {
                        transitionBlurView = null
                        folderBlurView = null
                        wallpaperBlurView = null
                        minusBlurView = MiBlurView(context)
                    }
                    initBlurView(context)
                    val viewGroup = XposedHelpers.getObjectField(launcher, "mLauncherView") as ViewGroup
                    // Transition
                    transitionBlurView?.let {
                        val mOverviewPanel = XposedHelpers.getObjectField(launcher, "mOverviewPanel") as View
                        viewGroup.addView(
                            it,
                            viewGroup.indexOfChild(mOverviewPanel).coerceAtLeast(0)
                        )
                    }
                    // Folder
                    folderBlurView?.let {
                        val mSearchEdgeLayout = XposedHelpers.getObjectField(launcher, "mSearchEdgeLayout") as FrameLayout
                        val mFolderCling = XposedHelpers.getObjectField(launcher, "mFolderCling") as FrameLayout
                        mSearchEdgeLayout.addView(
                            it,
                            mSearchEdgeLayout.indexOfChild(mFolderCling).coerceAtLeast(0)
                        )
                        val mDragLayer = XposedHelpers.getObjectField(launcher, "mDragLayer") as FrameLayout
                        mDragLayer.clipChildren = false
                        val mShortcutMenuLayer = XposedHelpers.getObjectField(launcher, "mShortcutMenuLayer") as FrameLayout
                        mShortcutMenuLayer.clipChildren = false
                    }
                    // Wallpaper
                    wallpaperBlurView?.let {
                        viewGroup.addView(it, 0)
                    }
                    // Minus
                    minusBlurView?.let {
                        if (MINUS_MODE == 2) {
                            viewGroup.addView(minusBlurView)
                        } else {
                            viewGroup.addView(minusBlurView, 0)
                        }
                    }
                }
            }
        )
        // Remove blur view from Launcher
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "onDestroy",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val launcher = param?.thisObject ?: return
                    val viewGroup = XposedHelpers.getObjectField(launcher, "mLauncherView") as ViewGroup
                    if (transitionBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(transitionBlurView)
                    }
                    if (folderBlurView?.isAttachedToWindow == true) {
                        val mSearchEdgeLayout = XposedHelpers.getObjectField(launcher, "mSearchEdgeLayout") as FrameLayout
                        mSearchEdgeLayout.removeView(folderBlurView)
                    }
                    if (wallpaperBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(wallpaperBlurView)
                    }
                    if (minusBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(minusBlurView)
                    }
                    transitionBlurView = null
                    folderBlurView = null
                    wallpaperBlurView = null
                    minusBlurView = null
                    wallpaperZoomManager = null
                }
            }
        )
    }

    private fun initBlurView(context: Context) {
        val transitionLayer = transitionBlurView
        val folderLayer = folderBlurView
        val wallpaperLayer = wallpaperBlurView
        val minusLayer = minusBlurView
        // Transition
        if (transitionLayer != null) {
            val appsUseBlur = Prefs.getBoolean(Refactor.APPS_BLUR, HomeRefactor.APPS_BLUR)
            val appsBlurRadius = Prefs.getPixelByStr(Refactor.APPS_BLUR_RADIUS_STR, HomeRefactor.APPS_BLUR_RADIUS_STR, context)
            val appsUseDim = Prefs.getBoolean(Refactor.APPS_DIM, HomeRefactor.APPS_DIM)
            val appsDimAlpha = Prefs.getInt(Refactor.APPS_DIM_MAX, HomeRefactor.APPS_DIM_MAX)
            val appsUseNonlinearType = Prefs.getInt(Refactor.APPS_NONLINEAR_TYPE, HomeRefactor.APPS_NONLINEAR_TYPE)
            val appsNonlinearDeceFactor = Prefs.getFloat(Refactor.APPS_NONLINEAR_DECE_FACTOR, HomeRefactor.APPS_NONLINEAR_DECE_FACTOR)
            val appsNonlinearPathX1 = Prefs.getFloat(Refactor.APPS_NONLINEAR_PATH_X1, HomeRefactor.APPS_NONLINEAR_PATH_X1)
            val appsNonlinearPathY1 = Prefs.getFloat(Refactor.APPS_NONLINEAR_PATH_Y1, HomeRefactor.APPS_NONLINEAR_PATH_Y1)
            val appsNonlinearPathX2 = Prefs.getFloat(Refactor.APPS_NONLINEAR_PATH_X2, HomeRefactor.APPS_NONLINEAR_PATH_X2)
            val appsNonlinearPathY2 = Prefs.getFloat(Refactor.APPS_NONLINEAR_PATH_Y2, HomeRefactor.APPS_NONLINEAR_PATH_Y2)
            transitionLayer.let {
                it.setPassWindowBlur(true)
                it.setBlur(appsUseBlur, appsBlurRadius)
                it.setDim(appsUseDim, appsDimAlpha)
                it.setScale(false, 0f)
                when (appsUseNonlinearType) {
                    1 -> it.setNonlinear(true, DecelerateInterpolator(appsNonlinearDeceFactor))
                    2 -> it.setNonlinear(
                        true,
                        PathInterpolator(appsNonlinearPathX1, appsNonlinearPathY1, appsNonlinearPathX2, appsNonlinearPathY2)
                    )
                    else -> it.setNonlinear(false, LinearInterpolator())
                }
                // it.setPassWindowBlur(BlurRefactorEntry.EXTRA_COMPATIBILITY)
            }
        }
        // Folder
        if (folderLayer != null) {
            val folderUseBlur = Prefs.getBoolean(Refactor.FOLDER_BLUR, HomeRefactor.FOLDER_BLUR)
            val folderBlurRadius = Prefs.getPixelByStr(Refactor.FOLDER_BLUR_RADIUS_STR, HomeRefactor.FOLDER_BLUR_RADIUS_STR, context)
            val folderUseDim = Prefs.getBoolean(Refactor.FOLDER_DIM, HomeRefactor.FOLDER_DIM)
            val folderDimAlpha = Prefs.getInt(Refactor.FOLDER_DIM_MAX, HomeRefactor.FOLDER_DIM_MAX)
            val folderUseNonlinearType = Prefs.getInt(Refactor.FOLDER_NONLINEAR_TYPE, HomeRefactor.FOLDER_NONLINEAR_TYPE)
            val folderNonlinearDeceFactor = Prefs.getFloat(Refactor.FOLDER_NONLINEAR_DECE_FACTOR, HomeRefactor.FOLDER_NONLINEAR_DECE_FACTOR)
            val folderNonlinearPathX1 = Prefs.getFloat(Refactor.FOLDER_NONLINEAR_PATH_X1, HomeRefactor.FOLDER_NONLINEAR_PATH_X1)
            val folderNonlinearPathY1 = Prefs.getFloat(Refactor.FOLDER_NONLINEAR_PATH_Y1, HomeRefactor.FOLDER_NONLINEAR_PATH_Y1)
            val folderNonlinearPathX2 = Prefs.getFloat(Refactor.FOLDER_NONLINEAR_PATH_X2, HomeRefactor.FOLDER_NONLINEAR_PATH_X2)
            val folderNonlinearPathY2 = Prefs.getFloat(Refactor.FOLDER_NONLINEAR_PATH_Y2, HomeRefactor.FOLDER_NONLINEAR_PATH_Y2)
            folderLayer.let {
                it.setPassWindowBlur(true)
                it.setBlur(folderUseBlur, folderBlurRadius)
                it.setDim(folderUseDim, folderDimAlpha)
                it.setScale(false, 0f)
                it.setCornerRadius(screenCornerRadius)
                when (folderUseNonlinearType) {
                    1 -> it.setNonlinear(true, DecelerateInterpolator(folderNonlinearDeceFactor))
                    2 -> it.setNonlinear(
                        true,
                        PathInterpolator(folderNonlinearPathX1, folderNonlinearPathY1, folderNonlinearPathX2, folderNonlinearPathY2)
                    )
                    else -> it.setNonlinear(false, LinearInterpolator())
                }
                // it.setPassWindowBlur(BlurRefactorEntry.EXTRA_COMPATIBILITY)
            }
        }
        // Wallpaper
        if (wallpaperLayer != null) {
            val wallpaperUseBlur = Prefs.getBoolean(Refactor.WALLPAPER_BLUR, HomeRefactor.WALLPAPER_BLUR)
            val wallpaperBlurRadius = Prefs.getPixelByStr(Refactor.WALLPAPER_BLUR_RADIUS_STR, HomeRefactor.WALLPAPER_BLUR_RADIUS_STR, context)
            val wallpaperUseDim = Prefs.getBoolean(Refactor.WALLPAPER_DIM, HomeRefactor.WALLPAPER_DIM)
            val wallpaperDimAlpha = Prefs.getInt(Refactor.WALLPAPER_DIM_MAX, HomeRefactor.WALLPAPER_DIM_MAX)
            val wallpaperUseNonlinearType = Prefs.getInt(Refactor.WALLPAPER_NONLINEAR_TYPE, HomeRefactor.WALLPAPER_NONLINEAR_TYPE)
            val wallpaperNonlinearDeceFactor = Prefs.getFloat(Refactor.WALLPAPER_NONLINEAR_DECE_FACTOR, HomeRefactor.WALLPAPER_NONLINEAR_DECE_FACTOR)
            val wallpaperNonlinearPathX1 = Prefs.getFloat(Refactor.WALLPAPER_NONLINEAR_PATH_X1, HomeRefactor.WALLPAPER_NONLINEAR_PATH_X1)
            val wallpaperNonlinearPathY1 = Prefs.getFloat(Refactor.WALLPAPER_NONLINEAR_PATH_Y1, HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y1)
            val wallpaperNonlinearPathX2 = Prefs.getFloat(Refactor.WALLPAPER_NONLINEAR_PATH_X2, HomeRefactor.WALLPAPER_NONLINEAR_PATH_X2)
            val wallpaperNonlinearPathY2 = Prefs.getFloat(Refactor.WALLPAPER_NONLINEAR_PATH_Y2, HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y2)
            wallpaperLayer.let {
                it.setPassWindowBlur(true)
                it.setBlur(wallpaperUseBlur, wallpaperBlurRadius)
                it.setDim(wallpaperUseDim, wallpaperDimAlpha)
                it.setScale(false, 0f)
                when (wallpaperUseNonlinearType) {
                    1 -> it.setNonlinear(true, DecelerateInterpolator(wallpaperNonlinearDeceFactor))
                    2 -> it.setNonlinear(
                        true,
                        PathInterpolator(wallpaperNonlinearPathX1, wallpaperNonlinearPathY1, wallpaperNonlinearPathX2, wallpaperNonlinearPathY2)
                    )
                    else -> it.setNonlinear(false, LinearInterpolator())
                }
                // it.setPassWindowBlur(BlurRefactorEntry.EXTRA_COMPATIBILITY)
            }
        }
        // Minus screen
        if (minusLayer != null) {
            val minusUseBlur = Prefs.getBoolean(Refactor.MINUS_BLUR, HomeRefactor.MINUS_BLUR)
            val minusBlurRadius = Prefs.getPixelByStr(Refactor.MINUS_BLUR_RADIUS_STR, HomeRefactor.MINUS_BLUR_RADIUS_STR, context)
            val minusUseDim = Prefs.getBoolean(Refactor.MINUS_DIM, HomeRefactor.MINUS_DIM)
            val minusDimAlpha = Prefs.getInt(Refactor.MINUS_DIM_MAX, HomeRefactor.MINUS_DIM_MAX)
            minusLayer.let {
                it.setPassWindowBlur(true)
                it.setBlur(minusUseBlur, minusBlurRadius)
                it.setDim(minusUseDim, minusDimAlpha)
                it.setScale(false, 0f)
                it.setNonlinear(false, LinearInterpolator())
                // it.setPassWindowBlur(BlurRefactorEntry.EXTRA_COMPATIBILITY)
            }
        }
        // Scale
//        val minusUseScale = MINUS_OVERLAP
//        val minusScaleRatio = 1.0f - SHOW_LAUNCH_IN_MINUS_SCALE
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
        ) == fieldAllAppsState)
    }
}