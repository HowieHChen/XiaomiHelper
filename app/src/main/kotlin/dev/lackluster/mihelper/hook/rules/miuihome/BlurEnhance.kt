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

package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.PrefDefValue
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.hook.view.MiBlurView
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.MiBlurUtils
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import java.lang.reflect.Method
import java.util.concurrent.Executor

object BlurEnhance : YukiBaseHooker() {
    private val printDebugInfo = BuildConfig.DEBUG // && false

    private val blurUtils by lazy {
        "com.miui.home.launcher.common.BlurUtils".toClass()
    }
    private val blurUtilities by lazy {
        "com.miui.home.launcher.common.BlurUtilities".toClass()
    }
    private val mainThreadExecutor by lazy {
        "com.miui.home.recents.TouchInteractionService".toClass().field {
            name = "MAIN_THREAD_EXECUTOR"
            modifiers { isStatic }
        }.get().any() as Executor
    }
    private val overviewState by lazy {
        "com.miui.home.launcher.LauncherState".toClass().field {
            name = "OVERVIEW"
            modifiers { isStatic }
        }.get().any()
    }
    private val overviewStateAlpha by lazy {
        "com.miui.home.recents.OverviewState".toClass().method {
            name = "getShortcutMenuLayerAlpha"
        }
    }
    private val overviewStateScale by lazy {
        "com.miui.home.recents.OverviewState".toClass().method {
            name = "getShortcutMenuLayerScale"
        }
    }
    private val isBackgroundBlurEnabled by lazy {
        "com.miui.home.launcher.common.BlurUtilities".toClass(). field {
            name = "IS_BACKGROUND_BLUR_ENABLED"
            modifiers { isStatic }
        }.get()
    }
//    private val overviewStateFromFsGesture by lazy {
//        "com.miui.home.launcher.LauncherState".toClass().field {
//            name = "mIsFromFsGesture"
//        }.get(overviewState)
//    }
//    private val isInMultiWindowMode by lazy {
//        "com.miui.home.launcher.DeviceConfig".toClass().method {
//            name = "isInMultiWindowModeCompatAndroidT"
//            modifiers { isStatic }
//        }.get().boolean()
//    }
    private val navStubView by lazy {
        "com.miui.home.recents.NavStubView".toClass()
    }
//    private val mHomeFadeOutAnim by lazy {
//        navStubView.field {
//            name = "mHomeFadeOutAnim"
//        }
//    }
//    private val checkUpdateShortcutMenuLayerType by lazy {
//        navStubView.method {
//            name = "checkUpdateShortcutMenuLayerType"
//        }
//    }

    private val appsUseBlur =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_APPS_BLUR, PrefDefValue.HOME_REFACTOR_APPS_BLUR)
    private val appsBlurRadius =
        Prefs.getInt(PrefKey.HOME_REFACTOR_APPS_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_APPS_BLUR_RADIUS)
    private val appsUseDim =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_APPS_DIM, PrefDefValue.HOME_REFACTOR_APPS_DIM)
    private val appsDimAlpha =
        Prefs.getInt(PrefKey.HOME_REFACTOR_APPS_DIM_MAX, PrefDefValue.HOME_REFACTOR_APPS_DIM_MAX)
    private val appsUseNonlinearType =
        Prefs.getInt(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_TYPE)
    private val appsNonlinearDeceFactor =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR)
    private val appsNonlinearPathX1 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1)
    private val appsNonlinearPathY1 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1)
    private val appsNonlinearPathX2 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2)
    private val appsNonlinearPathY2 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2)

    private val wallUseBlur =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALL_BLUR, PrefDefValue.HOME_REFACTOR_WALL_BLUR)
    private val wallBlurRadius =
        Prefs.getInt(PrefKey.HOME_REFACTOR_WALL_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_WALL_BLUR_RADIUS)
    private val wallUseDim =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALL_DIM, PrefDefValue.HOME_REFACTOR_WALL_DIM)
    private val wallDimAlpha =
        Prefs.getInt(PrefKey.HOME_REFACTOR_WALL_DIM_MAX, PrefDefValue.HOME_REFACTOR_WALL_DIM_MAX)
    private val wallUseNonlinearType =
        Prefs.getInt(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_TYPE)
    private val wallNonlinearDeceFactor =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR)
    private val wallNonlinearPathX1 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1)
    private val wallNonlinearPathY1 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1)
    private val wallNonlinearPathX2 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2)
    private val wallNonlinearPathY2 =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2)

    private val launchShow =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_LAUNCH_SHOW, PrefDefValue.HOME_REFACTOR_LAUNCH_SHOW)
    private val launchScale =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_SCALE, PrefDefValue.HOME_REFACTOR_LAUNCH_SCALE)
//    private val launchUseNonlinear =
//        Prefs.getBoolean(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR, PrefDefValue.HOME_REFACTOR_LAUNCH_NONLINEAR)
//    private val launchNonlinearFactor =
//        Prefs.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR, PrefDefValue.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR)

    private val minusUseBlur =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_MINUS_BLUR, PrefDefValue.HOME_REFACTOR_MINUS_BLUR)
    private val minusBlurRadius =
        Prefs.getInt(PrefKey.HOME_REFACTOR_MINUS_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_MINUS_BLUR_RADIUS)
    private val minusUseDim =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_MINUS_DIM, PrefDefValue.HOME_REFACTOR_MINUS_DIM)
    private val minusDimAlpha =
        Prefs.getInt(PrefKey.HOME_REFACTOR_MINUS_DIM_MAX, PrefDefValue.HOME_REFACTOR_MINUS_DIM_MAX)
    private val minusOverlapMode =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_MINUS_OVERLAP, PrefDefValue.HOME_REFACTOR_MINUS_OVERLAP)
    private val minusShowLaunch =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_MINUS_LAUNCH, PrefDefValue.HOME_REFACTOR_MINUS_LAUNCH)

    private val wallpaperScaleSync =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALLPAPER_SCALE_SYNC, PrefDefValue.HOME_REFACTOR_WALLPAPER_SCALE_SYNC)
    private val extraFix =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_EXTRA_FIX, PrefDefValue.HOME_REFACTOR_EXTRA_FIX)
    private val fixSmallWindowAnim =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_FIX_SMALL_WINDOW, PrefDefValue.HOME_REFACTOR_FIX_SMALL_WINDOW)
    private val extraCompatibility =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_EXTRA_COMPATIBILITY, PrefDefValue.HOME_REFACTOR_EXTRA_COMPATIBILITY)
    private var isStartingApp = false

    private val setWallpaperZoomOut by lazy {
        "com.miui.home.launcher.wallpaper.WallpaperZoomManagerKt".toClass().method{
            name = "findUpdateZoomMethod"
            modifiers { isStatic }
        }.get().call() as Method
    }
    private var wallpaperZoomManager : WallpaperZoomManager? = null

    override fun onHook() {
        if (!MiBlurUtils.supportBackgroundBlur()) {
            YLog.warn("The High-quality materials function is unsupported.")
            return
        }
        var transitionBlurView : MiBlurView? = null
        var wallpaperBlurView : MiBlurView? = null
        var minusBlurView : MiBlurView? = null
        hasEnable(PrefKey.HOME_BLUR_REFACTOR) {
            // Block original blur
            if (extraCompatibility) {
                blurUtils.method {
                    name = "fastBlurDirectly"
                }.hook {
                    replaceUnit {
                        wallpaperBlurView?.show(false, this.args(0).float())
                    }
                }
            }
            else {
                blurUtils.method {
                    name = "fastBlurDirectly"
                }.hook {
                    intercept()
                }
            }
            // Add blur view to Launcher
            blurUtilities.method {
                name = "setBackgroundBlurEnabled"
                modifiers { isStatic }
            }.hook {
                after {
                    if (!isBackgroundBlurEnabled.boolean()) {
                        if (!Prefs.getBoolean(PrefKey.HOME_BLUR_ENHANCE, false)) {
                            YLog.warn("The High-quality materials function is disabled.")
                        }
                        isBackgroundBlurEnabled.setTrue()
                    }
                    val launcher = this.args(0).any()
                    transitionBlurView = MiBlurView(launcher as Activity)
                    transitionBlurView?.let {
                        it.setBlur(appsUseBlur, appsBlurRadius)
                        it.setDim(appsUseDim, appsDimAlpha)
                        when (appsUseNonlinearType) {
                            1 -> it.setNonlinear(true, DecelerateInterpolator(appsNonlinearDeceFactor))
                            2 -> it.setNonlinear(true, PathInterpolator(appsNonlinearPathX1, appsNonlinearPathY1, appsNonlinearPathX2, appsNonlinearPathY2))
                            else -> it.setNonlinear(false, LinearInterpolator())
                        }
                        if (extraCompatibility) {
                            it.setPassWindowBlur(true)
                        }
                    }
                    wallpaperBlurView = MiBlurView(launcher)
                    wallpaperBlurView?.let {
                        it.setBlur(wallUseBlur, wallBlurRadius)
                        it.setDim(wallUseDim, wallDimAlpha)
                        when (wallUseNonlinearType) {
                            1 -> it.setNonlinear(true, DecelerateInterpolator(wallNonlinearDeceFactor))
                            2 -> it.setNonlinear(true, PathInterpolator(wallNonlinearPathX1, wallNonlinearPathY1, wallNonlinearPathX2, wallNonlinearPathY2))
                            else -> it.setNonlinear(false, LinearInterpolator())
                        }
                        if (extraCompatibility) {
                            it.setPassWindowBlur(true)
                        }
                    }
                    minusBlurView = MiBlurView(launcher)
                    minusBlurView?.let {
                        it.setBlur(minusUseBlur, minusBlurRadius)
                        it.setDim(minusUseDim, minusDimAlpha)
                        it.setNonlinear(false, LinearInterpolator())
                        if (extraCompatibility) {
                            it.setPassWindowBlur(true)
                        }
                    }
                    val viewGroup = XposedHelpers.getObjectField(launcher, "mLauncherView") as ViewGroup
                    viewGroup.addView(transitionBlurView, viewGroup.indexOfChild(
                        XposedHelpers.getObjectField(launcher, "mOverviewPanel") as View
                    ).coerceAtLeast(0))
                    viewGroup.addView(wallpaperBlurView, 0)
                    if (minusOverlapMode) {
                        viewGroup.addView(minusBlurView)
                    }
                    else {
                        viewGroup.addView(minusBlurView, 0)
                    }
                }
            }
            // Remove blur view from Launcher
            "com.miui.home.recents.views.FloatingIconView".toClass().method {
                name = "onLauncherDestroy"
            }.hook {
                after {
                    val viewGroup = XposedHelpers.getObjectField(this.args(0).any(), "mLauncherView") as ViewGroup
                    if (transitionBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(transitionBlurView)
                    }
                    if (wallpaperBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(wallpaperBlurView)
                    }
                    if (minusBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(minusBlurView)
                    }
                    transitionBlurView = null
                    wallpaperBlurView = null
                    minusBlurView = null
                    wallpaperZoomManager = null
                }
            }
            // Seems to be used only for blurring wallpaper
            blurUtils.method {
                name = "fastBlur"
                paramCount = 3
            }.hook {
                before {
                    if (printDebugInfo)
                        YLog.info("fastBlur target: ${this.args(1).float()} useAnim: ${this.args(2).float()}")
                    wallpaperBlurView?.show(this.args(2).boolean(), this.args(0).float())
                    this.result = null
                }
            }
            // Seems to be used only for blurring wallpaper
            blurUtils.method {
                name = "fastBlur"
                paramCount = 4
            }.hook {
                before {
                    if (printDebugInfo)
                        YLog.info("fastBlur target: ${this.args(1).float()} useAnim: ${this.args(2).float()}")
                    wallpaperBlurView?.showWithDuration(this.args(2).boolean(), this.args(0).float(), 350)
                    this.result = null
                }
            }
            // Blur when launching app
            blurUtils.method {
                name = "fastBlurWhenStartOpenOrCloseApp"
            }.hook {
                before {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenStartOpenOrCloseApp isOpen: ${this.args(0).boolean()}")
                    val isOpen = this.args(0).boolean()
                    if (isOpen) {
                        wallpaperZoomManager?.zoomOut(true)
                        transitionBlurView?.show(true)
                        isStartingApp = true
                    }
                    else {
                        // "isOpen" seems to always be true
                        if (shouldBlurWallpaper(this.args(1).any() ?: return@before)) {
                            wallpaperBlurView?.show(false)
                        }
                        wallpaperZoomManager?.zoomOut(false)
                        transitionBlurView?.show(false)
                        wallpaperZoomManager?.zoomIn(true)
                        transitionBlurView?.hide(true)
                    }
                    this.result = null
                }
            }
            // Reset blur after launching the app
            blurUtils.method {
                name = "fastBlurWhenFinishOpenOrCloseApp"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenFinishOpenOrCloseApp")
                    wallpaperZoomManager?.zoomIn(false)
                    transitionBlurView?.hide(false)
                    if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                        wallpaperBlurView?.show(false)
                    }
                    else {
                        wallpaperBlurView?.hide(false)
                    }
                    isStartingApp = false
                }
            }
            // Widely used
            blurUtils.method {
                name = "fastBlurWhenUseCompleteRecentsBlur"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenUseCompleteRecentsBlur useAnim: ${this.args(2).boolean()} target: ${this.args(1).float()}]")
                    val useAnim = this.args(2).boolean()
                    mainThreadExecutor.execute {
                        if (useAnim) {
                            transitionBlurView?.show(
                                true, this.args(1).float()
                            )
                            wallpaperZoomManager?.zoomOut(
                                true, this.args(1).float()
                            )
                            isStartingApp = false
                        }
                        else if (isStartingApp) {
                            transitionBlurView?.restore()
                            wallpaperZoomManager?.restore()
                        }
                        else {
                            transitionBlurView?.show(
                                false, this.args(1).float()
                            )
                            wallpaperZoomManager?.zoomOut(
                                false, this.args(1).float()
                            )
                        }
                    }
                }
            }
            // Use with "fastBlurWhenUseCompleteRecentsBlur"
            blurUtils.method {
                name = "resetBlurWhenUseCompleteRecentsBlur"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("resetBlurWhenUseCompleteRecentsBlur")
                    mainThreadExecutor.execute {
                        val useAnim = this.args(1).boolean()
                        if (shouldBlurWallpaper(this.args(0).any() ?: return@execute)) {
                            wallpaperBlurView?.show(false)
                        }
//                        if (usrAnim) {
//                            transitionBlurView.show(false)
//                        }
                        wallpaperZoomManager?.zoomIn(useAnim)
                        transitionBlurView?.hide(useAnim)
                    }
                }
            }
            // Blur when entering recent tasks
            // Skip when triggered by a gesture in the app
            blurUtils.method {
                name = "fastBlurWhenEnterRecents"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenEnterRecents useAnim: ${this.args(2).boolean()}")
                    if (XposedHelpers.getBooleanField(this.args(1).any(), "mIsFromFsGesture")) {
                        if (printDebugInfo)
                            YLog.info("fastBlurWhenEnterRecents skip (IsFromFsGesture)")
                        return@replaceUnit
                    }
                    wallpaperZoomManager?.zoomOut(this.args(2).boolean())
                    transitionBlurView?.show(this.args(2).boolean())
                }
            }
            // Reset blur when exiting recent tasks
            // Skip when triggered by a gesture in the app
            blurUtils.method {
                name = "fastBlurWhenExitRecents"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenExitRecents useAnim: ${this.args(2).boolean()}")
                    if (XposedHelpers.getBooleanField(this.args(1).any(), "mIsFromFsGesture")) {
                        if (printDebugInfo)
                            YLog.info("fastBlurWhenExitRecents skip (IsFromFsGesture)")
                        return@replaceUnit
                    }
                    val useAnim = this.args(2).boolean()
                    if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                        wallpaperBlurView?.show(false)
                    }
//                    if (usrAnim) {
//                        transitionBlurView.show(false)
//                    }
                    // Forced animation to avoid flickering when opening a small window
                    // not sure if it has a negative effect for now
                    wallpaperZoomManager?.zoomIn(useAnim || fixSmallWindowAnim)
                    transitionBlurView?.hide(useAnim || fixSmallWindowAnim)
                }
            }
            // Reset blur, widely used
            blurUtils.method {
                name = "resetBlur"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("resetBlur useAnim:${this.args(1).boolean()}")
                    mainThreadExecutor.execute {
                        val useAnim = this.args(1).boolean()
                        if (shouldBlurWallpaper(this.args(0).any() ?: return@execute)) {
                            wallpaperBlurView?.show(false)
                        }
//                        if (usrAnim) {
//                            transitionBlurView.show(false)
//                        }
                        if (isStartingApp && !useAnim) {
                            wallpaperZoomManager?.zoomIn(true)
                            transitionBlurView?.hide(true)
                        }
                        else {
                            wallpaperZoomManager?.zoomIn(useAnim)
                            transitionBlurView?.hide(useAnim)
                        }
                        isStartingApp = false
                    }
                }
            }
            // Blur when entering the folder edit page
            // Only affects wallpaper blur
            blurUtils.method {
                name = "fastBlurWhenEnterFolderPicker"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenEnterFolderPicker")
                    wallpaperBlurView?.showWithDuration(
                        this.args(2).boolean(), this.args(1).float(), this.args(3).int()
                    )
                }
            }
            // Reset blurring when exiting the folder edit page
            // Only affects wallpaper blur
            blurUtils.method {
                name = "fastBlurWhenExitFolderPicker"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenExitFolderPicker")
                    val useAnim = this.args(2).boolean()
                    if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                        return@replaceUnit
                    }
                    if (
                        XposedHelpers.getObjectField(
                            XposedHelpers.getObjectField(
                                this.args(0).any() ?: return@replaceUnit,"mStateManager"
                            ),
                            "mState"
                        ) == overviewState
                    ) {
                        return@replaceUnit
                    }
                    wallpaperBlurView?.showWithDuration(
                        useAnim, this.args(1).float(), this.args(3).int()
                    )
                }
            }
            // Beginning of the uncertainty section
            blurUtils.method {
                name = "fastBlurWhenEnterMultiWindowMode"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenEnterMultiWindowMode")
                    if (
                        XposedHelpers.getObjectField(
                            XposedHelpers.getObjectField(
                                this.args(0).any() ?: return@replaceUnit,"mStateManager"
                            ),
                            "mState"
                        ) == overviewState
                    ) {
                        wallpaperZoomManager?.zoomOut(this.args(1).boolean())
                        transitionBlurView?.show(this.args(1).boolean())
                    }
                }
            }
            blurUtils.method {
                name = "fastBlurWhenGestureResetTaskView"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenGestureResetTaskView  useAnim: ${this.args(1).boolean()}")
                    if (
                        XposedHelpers.getObjectField(
                            XposedHelpers.getObjectField(
                                this.args(0).any() ?: return@replaceUnit,"mStateManager"
                            ),
                            "mState"
                        ) == overviewState
                    ) {
                        wallpaperZoomManager?.zoomOut(this.args(1).boolean())
                        transitionBlurView?.show(this.args(1).boolean())
                    }
                }
            }
            blurUtils.method {
                name = "fastBlurWhenGestureAppModeStart"
            }.hook {
                intercept()
            }
            blurUtils.method {
                name = "restoreBlurRatioAfterAndroidS"
            }.hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("restoreBlurRatioAfterAndroidS")
//                    mainThreadExecutor?.execute {
//                        wallpaperZoomManager?.restore(true)
//                    }
                    transitionBlurView?.restore(true)
                }
            }
            // End of uncertainty section
            if (Device.isPad) {
                // Tablet needs its own implementation
                val launcherClz = "com.miui.home.launcher.Launcher".toClass()
                val isShouldBlurMethod =
                    launcherClz.methods.first { it.name == "isShouldBlur" }
                val openMethod =
                    launcherClz.methods.first { it.name == "openFolder" }
                val closeMethod =
                    launcherClz.methods.first { it.name == "closeFolder" && it.parameterCount == 1 && it.parameterTypes[0] == BooleanType }
                // Folder blur
                XposedBridge.hookMethod(isShouldBlurMethod, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val launcher = param?.thisObject ?: return
                        val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                        val isInNormalEditing = XposedHelpers.callMethod(launcher, "isInNormalEditing") as Boolean
                        val isFolderShowing = (XposedHelpers.callMethod(launcher, "isFolderShowing") as Boolean?) ?: false
                        param.result = !isLaptopMode && (isInNormalEditing || isFolderShowing)
                    }
                })
                // Blur wallpaper when opening a folder (for tablet)
                XposedBridge.hookMethod(openMethod, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val launcher = param?.thisObject ?: return
                        val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                        val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                        if (!isLaptopMode && !isInEditing) {
                            wallpaperBlurView?.show(true)
                        }
                    }
                })
                // Reset blurring when closing folder (for tablet)
                XposedBridge.hookMethod(closeMethod, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val launcher = param?.thisObject ?: return
                        val isFolderOpenAnim = param.args[0] as Boolean
                        val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                        val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                        if (!isLaptopMode && !isInEditing) {
                            wallpaperBlurView?.hide(isFolderOpenAnim)
                        }
                    }
                })
                // Blur wallpaper directly when in a folder or in editing mode (for tablet)
                "com.miui.home.launcher.Workspace".toClass()
                    .method {
                        name = "setEditMode"
                    }
                    .hook {
                        after {
                            val mLauncher = this.instance.current().field { name = "mLauncher"; superClass() }.any() ?: return@after
                            val isLaptopMode = XposedHelpers.callMethod(mLauncher, "isLapTopMode") as Boolean
                            val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
                            val isInNormalEditing = XposedHelpers.callMethod(this.instance, "isInNormalEditingMode") as Boolean
                            if (!isLaptopMode && (isInNormalEditing || isFolderShowing)) {
                                wallpaperBlurView?.show(false)
                            }
                        }
                    }
            }
            else {
                // Blur wallpaper when opening a folder
                // Reset blurring when closing folder
                // Only affects wallpaper blur
                blurUtils.method {
                    name = "fastBlurWhenOpenOrCloseFolder"
                }.ignored().hook {
                    replaceUnit {
                        if (printDebugInfo)
                            YLog.info("fastBlurWhenOpenOrCloseFolder")
                        val useAnim = this.args(1).boolean()
                        if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                            wallpaperBlurView?.show(useAnim)
                        }
                        else {
                            wallpaperBlurView?.hide(useAnim)
                        }
                    }
                }
            }
            overviewStateScale.hook {
                replaceTo(launchScale)
            }
            if (launchShow && !Device.isPad) {
                navStubView.method {
                    name = "changeAlphaScaleForFsGesture"
                    paramCount = 2
                }.hook {
                    before {
                        this.args(0).set(1.0f)
                    }
                }
                overviewStateAlpha.hook {
                    replaceTo(1.0f)
                }
            }
//            if (launchUseNonlinear) {
//                navStubView.method {
//                    name = "startHomeFadeOutAnim"
//                }.hook {
//                    before {
//                        (mHomeFadeOutAnim.get(this.instance).any() as? ValueAnimator?) ?: let {
//                            val fadeOutAnim = ValueAnimator()
//                            fadeOutAnim.interpolator = DecelerateInterpolator(launchNonlinearFactor)
//                            fadeOutAnim.duration = 250
//                            fadeOutAnim.addListener {
//                                it.doOnStart {
//                                    checkUpdateShortcutMenuLayerType.get(this.instance).call(2)
//                                }
//                                it.doOnEnd {
//                                    checkUpdateShortcutMenuLayerType.get(this.instance).call(0)
//                                }
//                            }
//                            mHomeFadeOutAnim.get(this.instance).set(fadeOutAnim)
//                        }
//                    }
//                }
//            }
            if (extraFix) {
                if (Device.isPad) {
                    "com.miui.home.recents.GestureModeApp".toClass()
                        .method {
                            name = "performAppToHome"
                        }
                        .hook {
                            before {
                                val mLauncher = this.instance.current().field { name = "mLauncher"; superClass() }.any() ?: return@before
                                val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
                                transitionBlurView?.show(false)
//                                wallpaperZoomManager?.zoomOut(false)
                                if (!isFolderShowing) {
                                    transitionBlurView?.hide(true)
//                                    wallpaperZoomManager?.zoomIn(true)
                                }
                            }
                        }
                }
                else {
                    navStubView.method {
                        name = "commonAppTouchFromMove"
                    }.hook {
                        after {
                            blurUtils.method {
                                name = "fastBlurWhenUseCompleteRecentsBlur"
                            }.get().call(null, 1.0f, false)
                        }
                    }
                }
            }
            // MinusScreen
            if (minusOverlapMode) {
                "com.miui.home.launcher.overlay.assistant.AssistantDeviceAdapter".toClass()
                    .method {
                        name = "inOverlapMode"
                    }
                    .hook {
                        replaceToTrue()
                    }
            }
            else {
                "com.miui.home.launcher.overlay.OverlayTransitionController".toClass()
                    .method {
                        name = "onScrollChanged"
                    }
                    .hook {
                        replaceUnit {
                            val mCurrentAnimation = this.instance.current().field { name = "mCurrentAnimation"; superClass() }.any() ?: return@replaceUnit
                            mCurrentAnimation.current().method {
                                name = "setPlayFraction"
                            }.call(
                                if (this.instance.current().field { name = "isTargetOpenOverlay"; superClass() }.boolean()) {
                                    this.args(0).float()
                                } else {
                                    1.0f - this.args(0).float()
                                }
                            )
                        }
                    }
            }
            if (!minusOverlapMode && minusShowLaunch) {
                val superGetSearchBarProperty = "com.miui.home.launcher.LauncherState".toClass().method {
                    name = "getSearchBarProperty"
                }.give() ?: return@hasEnable
                superGetSearchBarProperty.hook {
                    before {
                        // Make the original method accessible to avoid infinite loops
                    }
                }
                "com.miui.home.launcher.overlay.assistant.AssistantOverlayState".toClass().apply {
                    method {
                        name = "getVisibleElements"
                    }.hook {
                        replaceTo(1)
                    }
                    method {
                        name = "getSearchBarProperty"
                    }.hook {
                        before {
                            val superResult = XposedBridge.invokeOriginalMethod(superGetSearchBarProperty, this.instance, this.args) as FloatArray
                            superResult[4] = this.instance.current().method { name = "getWorkspaceTranslationX" }.float(this.args[0])
                            this.result = superResult
                        }
                    }
                }
            }
            "com.miui.home.launcher.overlay.assistant.AssistantOverlayTransitionController".toClass()
                .method {
                    name = "onScrollChanged"
                }
                .hook {
                    before {
                        minusBlurView?.show(false, this.args(0).float())
                    }
                }
            // Wallpaper scale
            if (wallpaperScaleSync) {
                "com.miui.home.launcher.Launcher".toClass().methods.first { it.name == "animateWallpaperZoom" }.hook {
                    intercept()
                }
                "com.miui.home.launcher.wallpaper.WallpaperZoomManager".toClass().apply {
                    constructor().hook {
                        after {
                            val context = (this.instance.current().field { name = "context" }.any() ?: return@after) as Context
                            val windowToken = (this.instance.current().field { name = "mWindowToken" }.any() ?: return@after) as IBinder
                            // (this.instance.current().field { name = "mWallPaperExecutor" }.any() ?: return@after) as Executor
                            wallpaperZoomManager = WallpaperZoomManager(context, windowToken, setWallpaperZoomOut)
                        }
                    }
                }
            }
        }
    }
    private fun shouldBlurWallpaper(launcher: Any): Boolean {
//        val isInNormalEditing = XposedHelpers.callMethod(launcher, "isInNormalEditing") as Boolean
//        val isFoldShowing = XposedHelpers.callMethod(launcher, "isFolderShowing") as Boolean
        return (XposedHelpers.callMethod(launcher, "isShouldBlur") as Boolean)
    }

    private fun WallpaperZoomManager.zoomOut(useAnim: Boolean, targetRatio: Float = 1.0f) {
        mainThreadExecutor.execute {
            zoom(useAnim, targetRatio)
        }
    }

    private fun WallpaperZoomManager.zoomIn(useAnim: Boolean, targetRatio: Float = 0.0f) {
        mainThreadExecutor.execute {
            zoom(useAnim, targetRatio)
        }
    }
}