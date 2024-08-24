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
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ConfigurationClass
import com.highcapable.yukihookapi.hook.type.android.WindowClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_BLUR
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_DIM
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_OVERLAP
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isAllAppsShowing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isFolderShowing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isInNormalEditing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.wallpaperZoomManager
import dev.lackluster.mihelper.hook.view.MiBlurView
import dev.lackluster.mihelper.utils.Prefs
import java.util.concurrent.Executor

object BlurController : YukiBaseHooker() {
    private val blurUtilsClz by lazy {
        "com.miui.home.launcher.common.BlurUtils".toClass()
    }
    private val blurUtilitiesClz by lazy {
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

    // Configuration
    private val PRINT_DEBUG_INFO = BuildConfig.DEBUG // && false
    private val FIX_SMALL_WINDOW_ANIM = Prefs.getBoolean(Refactor.FIX_SMALL_WINDOW_ANIM, HomeRefactor.FIX_SMALL_WINDOW_ANIM)
    private val ALL_APPS_BLUR_BG = Prefs.getBoolean(Refactor.ALL_APPS_BLUR_BG, HomeRefactor.ALL_APPS_BLUR_BG)
    private val SHOW_LAUNCH_IN_MINUS = Prefs.getBoolean(Refactor.SHOW_LAUNCH_IN_MINUS, HomeRefactor.SHOW_LAUNCH_IN_MINUS)
    private val SHOW_LAUNCH_IN_MINUS_SCALE = Prefs.getFloat(Refactor.SHOW_LAUNCH_IN_MINUS_SCALE, HomeRefactor.SHOW_LAUNCH_IN_MINUS_SCALE)
    private val EXTRA_FIX = Prefs.getBoolean(Refactor.EXTRA_FIX, HomeRefactor.EXTRA_FIX)

    private var isStartingApp = false
    private var screenCornerRadius: Int = 84

    override fun onHook() {
        var transitionBlurView: MiBlurView? = null
        var folderBlurView: MiBlurView? = null
        var wallpaperBlurView: MiBlurView? = null
        var minusBlurView: MiBlurView? = null
        // Get and update screen corner radius
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "onAttachedToWindow",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    screenCornerRadius = XposedHelpers.callStaticMethod(
                        "com.miui.home.recents.util.WindowCornerRadiusUtil".toClass(),
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
                        "com.miui.home.recents.util.WindowCornerRadiusUtil".toClass(),
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
                    if (!XposedHelpers.getStaticBooleanField(blurUtilitiesClz, "IS_BACKGROUND_BLUR_ENABLED")) {
                        if (!Prefs.getBoolean(Pref.Key.MiuiHome.FAKE_PREMIUM, false)) {
                            YLog.warn("The High-quality materials function is disabled.")
                        }
                        XposedHelpers.setStaticBooleanField(blurUtilitiesClz, "IS_BACKGROUND_BLUR_ENABLED", true)
                    }
                    val viewGroup = XposedHelpers.getObjectField(launcher, "mLauncherView") as ViewGroup
                    // Init views
                    transitionBlurView = MiBlurView(launcher as Context)
                    folderBlurView = MiBlurView(launcher)
                    wallpaperBlurView = MiBlurView(launcher)
                    minusBlurView = MiBlurView(launcher)
                    initBlurView(launcher, transitionBlurView, folderBlurView, wallpaperBlurView, minusBlurView)
                    // Transition
                    val mOverviewPanel = XposedHelpers.getObjectField(launcher, "mOverviewPanel") as View
                    viewGroup.addView(
                        transitionBlurView,
                        viewGroup.indexOfChild(mOverviewPanel).coerceAtLeast(0)
                    )
                    // Folder
                    val mSearchEdgeLayout = XposedHelpers.getObjectField(launcher, "mSearchEdgeLayout") as FrameLayout
                    val mFolderCling = XposedHelpers.getObjectField(launcher, "mFolderCling") as FrameLayout
                    mSearchEdgeLayout.addView(
                        folderBlurView,
                        mSearchEdgeLayout.indexOfChild(mFolderCling).coerceAtLeast(0)
                    )
                    val mDragLayer = XposedHelpers.getObjectField(launcher, "mDragLayer") as FrameLayout
                    mDragLayer.clipChildren = false
                    val mShortcutMenuLayer = XposedHelpers.getObjectField(launcher, "mShortcutMenuLayer") as FrameLayout
                    mShortcutMenuLayer.clipChildren = false
                    // Wallpaper
                    viewGroup.addView(wallpaperBlurView, 0)
                    // Minus
                    if (MINUS_BLUR || MINUS_DIM) {
                        if (MINUS_OVERLAP) {
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
                    if (wallpaperBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(wallpaperBlurView)
                    }
                    if (minusBlurView?.isAttachedToWindow == true) {
                        viewGroup.removeView(minusBlurView)
                    }
                    val mSearchEdgeLayout = XposedHelpers.getObjectField(launcher, "mSearchEdgeLayout") as FrameLayout
                    if (folderBlurView?.isAttachedToWindow == true) {
                        mSearchEdgeLayout.removeView(folderBlurView)
                    }
                    transitionBlurView = null
                    folderBlurView = null
                    wallpaperBlurView = null
                    minusBlurView = null
                    wallpaperZoomManager = null
                }
            }
        )
        // Block original blur
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurDirectly(FLandroid/view/Window;)V
        if (BlurRefactorEntry.EXTRA_COMPATIBILITY) {
            blurUtilsClz.method {
                name = "fastBlurDirectly"
                paramCount = 2
                param(FloatType, WindowClass)
            }.hook {
                replaceUnit {
                    val targetRatio = this.args(0).float()
                    wallpaperBlurView?.setStatus(targetRatio, false)
                }
            }
        } else {
            blurUtilsClz.method {
                name = "fastBlurDirectly"
                paramCount = 2
                param(FloatType, WindowClass)
            }.hook {
                intercept()
            }
        }
        // Seems to be used only for blurring wallpaper
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlur(FLandroid/view/Window;Z)Landroid/animation/ValueAnimator;
        blurUtilsClz.method {
            name = "fastBlur"
            paramCount = 3
            param(FloatType, WindowClass, BooleanType)
        }.hook {
            before {
                val targetRatio = this.args(0).float()
                val useAnim = this.args(2).boolean()
                printDebugInfo("fastBlur target: $targetRatio useAnim: $useAnim")
                wallpaperBlurView?.setStatus(targetRatio, useAnim)
                this.result = null
            }
        }
        // Blur when entering or exiting edit mode
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlur(FLandroid/view/Window;ZJ)Landroid/animation/ValueAnimator;
        blurUtilsClz.method {
            name = "fastBlur"
            paramCount = 4
            param(FloatType, WindowClass, BooleanType, LongType)
        }.hook {
            before {
                val targetRatio = this.args(0).float()
                val useAnim = this.args(2).boolean()
                val duration = this.args(3).long().toInt()
                printDebugInfo("fastBlur target: $targetRatio useAnim: $useAnim duration: $duration")
                wallpaperBlurView?.setStatus(targetRatio, useAnim, duration)
                this.result = null
            }
        }
        // Blur when launching app
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenStartOpenOrCloseApp(ZLcom/miui/home/launcher/Launcher;)Landroid/animation/ValueAnimator;
        blurUtilsClz.method {
            name = "fastBlurWhenStartOpenOrCloseApp"
            paramCount = 2
            param(BooleanType, VagueType)
        }.hook {
            before {
                val isOpen = this.args(0).boolean()
                val launcher = this.args(1).any() ?: return@before
                printDebugInfo("fastBlurWhenStartOpenOrCloseApp isOpen: $isOpen")
                if (isOpen) {
                    wallpaperZoomManager?.zoomOut(true)
                    transitionBlurView?.setStatus(visible = true, useAnim = true)
                    isStartingApp = true
                } else {
                    // "isOpen" seems to always be true
                    wallpaperBlurView?.setStatus(
                        isInNormalEditing(launcher) || isAllAppsShowing(
                            launcher
                        ), false
                    )
                    folderBlurView?.setStatus(isFolderShowing(launcher), false)
                    wallpaperZoomManager?.zoomOut(false)
                    transitionBlurView?.setStatus(visible = true, useAnim = false)
                    wallpaperZoomManager?.zoomIn(true)
                    transitionBlurView?.setStatus(visible = false, useAnim = true)
                }
                this.result = null
            }
        }
        // Reset blur after launching the app
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenFinishOpenOrCloseApp(Lcom/miui/home/launcher/Launcher;)V
        blurUtilsClz.method {
            name = "fastBlurWhenFinishOpenOrCloseApp"
            paramCount = 1
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                printDebugInfo("fastBlurWhenFinishOpenOrCloseApp")
                wallpaperZoomManager?.zoomIn(false)
                transitionBlurView?.setStatus(visible = false, useAnim = false)
                wallpaperBlurView?.setStatus(
                    isInNormalEditing(launcher) || isAllAppsShowing(
                        launcher
                    ), false
                )
                folderBlurView?.setStatus(isFolderShowing(launcher), false)
                isStartingApp = false
            }
        }
        // Widely used
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenUseCompleteRecentsBlur(Lcom/miui/home/launcher/Launcher;FZ)V
        blurUtilsClz.method {
            name = "fastBlurWhenUseCompleteRecentsBlur"
            paramCount = 3
            param(VagueType, FloatType, BooleanType)
        }.hook {
            replaceUnit {
                val targetRatio = this.args(1).float()
                val useAnim = this.args(2).boolean()
                printDebugInfo("fastBlurWhenUseCompleteRecentsBlur useAnim: $useAnim target: $targetRatio")
                mainThreadExecutor.execute {
                    if (useAnim) {
                        transitionBlurView?.setStatus(targetRatio, true)
                        wallpaperZoomManager?.zoomOut(true, targetRatio)
                        isStartingApp = false
                    } else if (isStartingApp) {
                        transitionBlurView?.restore()
                        wallpaperZoomManager?.restore()
                    } else {
                        transitionBlurView?.setStatus(targetRatio, false)
                        wallpaperZoomManager?.zoomOut(false, targetRatio)
                    }
                }
            }
        }
        // Use with "fastBlurWhenUseCompleteRecentsBlur"
        // Lcom/miui/home/launcher/common/BlurUtils;->resetBlurWhenUseCompleteRecentsBlur(Lcom/miui/home/launcher/Launcher;Z)V
        blurUtilsClz.method {
            name = "resetBlurWhenUseCompleteRecentsBlur"
            paramCount = 2
            param(VagueType, BooleanType)
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val useAnim = this.args(1).boolean()
                printDebugInfo("resetBlurWhenUseCompleteRecentsBlur useAnim: $useAnim")
                mainThreadExecutor.execute {
                    wallpaperBlurView?.setStatus(
                        isInNormalEditing(launcher) || isAllAppsShowing(launcher),
                        false
                    )
                    folderBlurView?.setStatus(isFolderShowing(launcher), false)
                    wallpaperZoomManager?.zoomIn(useAnim)
                    transitionBlurView?.setStatus(visible = false, useAnim = useAnim)
                }
            }
        }
        // Similar to “fastBlurWhenUseCompleteRecentsBlur”
        // Only on tablet devices, not used
        blurUtilsClz.method {
            name = "fastBlurWhenDontUseNoBlurTypeWhenRecents"
            paramCount = 3
            param(VagueType, FloatType, BooleanType)
        }.ignored().hook {
            replaceUnit {
                val targetRatio = this.args(1).float()
                val useAnim = this.args(2).boolean()
                printDebugInfo("fastBlurWhenDontUseNoBlurTypeWhenRecents useAnim: $useAnim target: $targetRatio")
                mainThreadExecutor.execute {
                    if (useAnim) {
                        transitionBlurView?.setStatus(targetRatio, true)
                        wallpaperZoomManager?.zoomOut(true, targetRatio)
                        isStartingApp = false
                    } else if (isStartingApp) {
                        transitionBlurView?.restore()
                        wallpaperZoomManager?.restore()
                    } else {
                        transitionBlurView?.setStatus(targetRatio, false)
                        wallpaperZoomManager?.zoomOut(false, targetRatio)
                    }
                }
            }
        }
        // Similar to “resetBlurWhenUseCompleteRecentsBlur”
        // Only on tablet devices, not used
        blurUtilsClz.method {
            name = "resetBlurWhenDontUseNoBlurTypeWhenRecents"
            paramCount = 2
            param(VagueType, BooleanType)
        }.ignored().hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val useAnim = this.args(1).boolean()
                printDebugInfo("resetBlurWhenDontUseNoBlurTypeWhenRecents useAnim: $useAnim")
                mainThreadExecutor.execute {
                    wallpaperBlurView?.setStatus(
                        isInNormalEditing(launcher) || isAllAppsShowing(launcher),
                        false
                    )
                    folderBlurView?.setStatus(isFolderShowing(launcher), false)
                    wallpaperZoomManager?.zoomIn(useAnim)
                    transitionBlurView?.setStatus(visible = false, useAnim = useAnim)
                }
            }
        }
        // Blur when entering recent tasks
        // Skip when triggered by a gesture in the app
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenEnterRecents(Lcom/miui/home/launcher/Launcher;Lcom/miui/home/launcher/LauncherState;Z)V
        blurUtilsClz.method {
            name = "fastBlurWhenEnterRecents"
            paramCount = 3
            param(VagueType, VagueType, BooleanType)
        }.hook {
            replaceUnit {
                val launcherState = this.args(1).any() ?: return@replaceUnit
                val useAnim = this.args(2).boolean()
                printDebugInfo("fastBlurWhenEnterRecents useAnim: $useAnim")
                if (XposedHelpers.getBooleanField(launcherState, "mIsFromFsGesture")) {
                    printDebugInfo("fastBlurWhenEnterRecents skip (IsFromFsGesture)")
                    return@replaceUnit
                }
                wallpaperZoomManager?.zoomOut(useAnim)
                transitionBlurView?.setStatus(visible = true, useAnim = useAnim)
            }
        }
        // Reset blur when exiting recent tasks
        // Skip when triggered by a gesture in the app
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenExitRecents(Lcom/miui/home/launcher/Launcher;Lcom/miui/home/launcher/LauncherState;Z)V
        blurUtilsClz.method {
            name = "fastBlurWhenExitRecents"
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val launcherState = this.args(1).any() ?: return@replaceUnit
                val useAnim = this.args(2).boolean()
                printDebugInfo("fastBlurWhenExitRecents useAnim: $useAnim")
                if (XposedHelpers.getBooleanField(launcherState, "mIsFromFsGesture")) {
                    printDebugInfo("fastBlurWhenExitRecents skip (IsFromFsGesture)")
                    return@replaceUnit
                }
                wallpaperBlurView?.setStatus(
                    isInNormalEditing(launcher) || isAllAppsShowing(
                        launcher
                    ), false
                )
                folderBlurView?.setStatus(isFolderShowing(launcher), false)
//                    if (usrAnim) {
//                        transitionBlurView.show(false)
//                    }
                // Forced animation to avoid flickering when opening a small window
                // not sure if it has a negative effect for now
                wallpaperZoomManager?.zoomIn(useAnim || FIX_SMALL_WINDOW_ANIM)
                transitionBlurView?.setStatus(
                    visible = false,
                    useAnim = useAnim || FIX_SMALL_WINDOW_ANIM
                )
            }
        }
        // Reset blur, widely used
        // Lcom/miui/home/launcher/common/BlurUtils;->resetBlur(Lcom/miui/home/launcher/Launcher;Z)V
        blurUtilsClz.method {
            name = "resetBlur"
            paramCount = 2
            param(VagueType, BooleanType)
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val useAnim = this.args(1).boolean()
                printDebugInfo("resetBlur useAnim: $useAnim")
                mainThreadExecutor.execute {
                    wallpaperBlurView?.setStatus(
                        isInNormalEditing(launcher) || isAllAppsShowing(
                            launcher
                        ), false
                    )
                    folderBlurView?.setStatus(isFolderShowing(launcher), false)
//                        if (usrAnim) {
//                            transitionBlurView.show(false)
//                        }
                    if (isStartingApp && !useAnim) {
                        wallpaperZoomManager?.zoomIn(true)
                        transitionBlurView?.setStatus(visible = false, useAnim = true)
                    } else {
                        wallpaperZoomManager?.zoomIn(useAnim)
                        transitionBlurView?.setStatus(visible = false, useAnim = useAnim)
                    }
                    isStartingApp = false
                }
            }
        }
        // Blur when entering the folder edit page
        // Only affects wallpaper blur
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenEnterFolderPicker(Lcom/miui/home/launcher/Launcher;FZI)V
        blurUtilsClz.method {
            name = "fastBlurWhenEnterFolderPicker"
            paramCount = 4
            param(VagueType, FloatType, BooleanType, IntType)
        }.hook {
            replaceUnit {
                val targetRatio = this.args(1).float()
                val useAnim = this.args(2).boolean()
                val duration = this.args(3).long().toInt()
                printDebugInfo("fastBlurWhenEnterFolderPicker targetRatio: $targetRatio useAnim: $useAnim duration: $duration")
                wallpaperBlurView?.setStatus(targetRatio, useAnim, duration)
            }
        }
        // Reset blurring when exiting the folder edit page
        // Only affects wallpaper blur
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenExitFolderPicker(Lcom/miui/home/launcher/Launcher;FZI)V
        blurUtilsClz.method {
            name = "fastBlurWhenExitFolderPicker"
            paramCount = 4
            param(VagueType, FloatType, BooleanType, IntType)
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val targetRatio = this.args(1).float()
                val useAnim = this.args(2).boolean()
                val duration = this.args(3).long().toInt()
                printDebugInfo("fastBlurWhenExitFolderPicker targetRatio: $targetRatio useAnim: $useAnim duration: $duration")
                if (isInNormalEditing(launcher)) {
                    return@replaceUnit
                }
                if (
                    XposedHelpers.getObjectField(
                        XposedHelpers.getObjectField(launcher, "mStateManager"),
                        "mState"
                    ) == overviewState
                ) {
                    return@replaceUnit
                }
                wallpaperBlurView?.setStatus(targetRatio, useAnim, duration)
            }
        }
        // Beginning of the uncertainty section
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenEnterMultiWindowMode(Lcom/miui/home/launcher/Launcher;Z)V
        blurUtilsClz.method {
            name = "fastBlurWhenEnterMultiWindowMode"
            paramCount = 2
            param(VagueType, BooleanType)
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val useAnim = this.args(1).boolean()
                printDebugInfo("fastBlurWhenEnterMultiWindowMode useAnim: $useAnim")
                if (
                    XposedHelpers.getObjectField(
                        XposedHelpers.getObjectField(launcher, "mStateManager"),
                        "mState"
                    ) == overviewState
                ) {
                    wallpaperZoomManager?.zoomOut(useAnim)
                    transitionBlurView?.setStatus(visible = true, useAnim = useAnim)
                }
            }
        }
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenGestureResetTaskView(Lcom/miui/home/launcher/Launcher;Z)V
        blurUtilsClz.method {
            name = "fastBlurWhenGestureResetTaskView"
            paramCount = 2
            param(VagueType, BooleanType)
        }.hook {
            replaceUnit {
                val launcher = this.args(0).any() ?: return@replaceUnit
                val useAnim = this.args(1).boolean()
                printDebugInfo("fastBlurWhenGestureResetTaskView  useAnim: $useAnim")
                if (
                    XposedHelpers.getObjectField(
                        XposedHelpers.getObjectField(launcher, "mStateManager"),
                        "mState"
                    ) == overviewState
                ) {
                    wallpaperZoomManager?.zoomOut(useAnim)
                    transitionBlurView?.setStatus(visible = true, useAnim = useAnim)
                }
            }
        }
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenGestureAppModeStart(Lcom/miui/home/launcher/Launcher;)V
        blurUtilsClz.method {
            name = "fastBlurWhenGestureAppModeStart"
            paramCount = 1
        }.hook {
            intercept()
        }
        // Lcom/miui/home/launcher/common/BlurUtils;->restoreBlurRatioAfterAndroidS(Landroid/view/Window;)V
        blurUtilsClz.method {
            name = "restoreBlurRatioAfterAndroidS"
            paramCount = 1
        }.hook {
            replaceUnit {
                printDebugInfo("restoreBlurRatioAfterAndroidS")
//                    mainThreadExecutor?.execute {
//                        wallpaperZoomManager?.restore(true)
//                    }
                transitionBlurView?.restore(true)
            }
        }
        // End of uncertainty section
        // Blur wallpaper when opening a folder
        // Reset blurring when closing folder
        // Only affects wallpaper blur
        // Lcom/miui/home/launcher/common/BlurUtils;->fastBlurWhenOpenOrCloseFolder(Lcom/miui/home/launcher/Launcher;Z)V
        blurUtilsClz.method {
            name = "fastBlurWhenOpenOrCloseFolder"
            paramCount = 2
            param(VagueType, BooleanType)
        }.ignored().hook {
            intercept()
        }
        // Folder blur
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "fadeInOrOutScreenContentWhenFolderAnimate", BooleanType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val isOpen = param?.args?.get(0) as Boolean
                    folderBlurView?.setStatus(visible = isOpen, useAnim = true)
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "resetScreenContent",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    folderBlurView?.setStatus(visible = false, useAnim = false)
                }
            }
        )
        // All apps background
        if (ALL_APPS_BLUR_BG) {
            "com.miui.home.launcher.allapps.AllAppsTransitionController".toClass().apply {
                // Lcom/miui/home/launcher/allapps/AllAppsTransitionController;->setProgress(F)V
                method {
                    name = "setProgress"
                    paramCount = 1
                }.hook {
                    after {
                        val targetRatio = 1.0f - this.args(0).float()
                        printDebugInfo("AllAppsTransitionController#setProgress targetRatio: $targetRatio")
                        wallpaperBlurView?.setStatus(targetRatio, false)
                    }
                }
                // Lcom/miui/home/launcher/allapps/AllAppsTransitionController;->onProgressAnimationEnd(Lcom/miui/home/launcher/LauncherState;Z)V
                method {
                    name = "onProgressAnimationEnd"
                    paramCount = 2
                }.hook {
                    after {
                        val mProgress =
                            1.0f - this.instance.current().field { name = "mProgress" }.float()
                        if (mProgress == 1.0f || mProgress == 0.0f) {
                            wallpaperBlurView?.setStatus(mProgress, false)
                        }
                    }
                }
            }
        }
        // MinusScreen
        if (MINUS_OVERLAP) {
            "com.miui.home.launcher.overlay.assistant.AssistantDeviceAdapter".toClass().apply {
                method {
                    name = "inOverlapMode"
                }.hook {
                    replaceToTrue()
                }
            }
        } else {
            "com.miui.home.launcher.overlay.OverlayTransitionController".toClass().apply {
                method {
                    name = "onScrollChanged"
                }.hook {
                    replaceUnit {
                        val mCurrentAnimation = this.instance.current()
                            .field { name = "mCurrentAnimation"; superClass() }.any()
                            ?: return@replaceUnit
                        mCurrentAnimation.current().method {
                            name = "setPlayFraction"
                        }.call(
                            if (this.instance.current()
                                    .field { name = "isTargetOpenOverlay"; superClass() }.boolean()
                            ) {
                                this.args(0).float()
                            } else {
                                1.0f - this.args(0).float()
                            }
                        )
                    }
                }
            }
        }
        if (!MINUS_OVERLAP && SHOW_LAUNCH_IN_MINUS) {
            val superGetSearchBarProperty =
                "com.miui.home.launcher.LauncherState".toClass().method {
                    name = "getSearchBarProperty"
                }.give() ?: return
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
                        val superResult = XposedBridge.invokeOriginalMethod(
                            superGetSearchBarProperty,
                            this.instance,
                            this.args
                        ) as FloatArray
                        superResult[4] =
                            this.instance.current().method { name = "getWorkspaceTranslationX" }
                                .float(this.args[0])
                        this.result = superResult
                    }
                }
            }
        }
        if (MINUS_BLUR || MINUS_DIM) {
            "com.miui.home.launcher.overlay.assistant.AssistantOverlayTransitionController".toClass()
                .apply {
                    // Lcom/miui/home/launcher/overlay/assistant/AssistantOverlayTransitionController;->onScrollChanged(F)V
                    method {
                        name = "onScrollChanged"
                        paramCount = 1
                    }.hook {
                        before {
                            val targetRatio = this.args(0).float()
                            minusBlurView?.setStatus(targetRatio, false)
                            if (MINUS_OVERLAP) {
                                val mLauncher = this.instance.current().field {
                                    name = "mLauncher"
                                    superClass()
                                }.any()
                                val mScreenContent = XposedHelpers.getObjectField(mLauncher, "mScreenContent") as? FrameLayout ?: return@before
                                val scale = SHOW_LAUNCH_IN_MINUS_SCALE + (1 - targetRatio) * (1 - SHOW_LAUNCH_IN_MINUS_SCALE)
                                mScreenContent.scaleX = scale
                                mScreenContent.scaleY = scale
//                                mScreenContent.scaleX = 1.0f
//                                mScreenContent.scaleY = 1.0f
                                this.result = null
                            }
                        }
                    }
                    // Lcom/miui/home/launcher/overlay/assistant/AssistantOverlayTransitionController;->onScrollEnd(F)V
                    method {
                        name = "onScrollEnd"
                        paramCount = 1
                    }.hook {
                        after {
                            val targetRatio = this.args(0).float()
                            minusBlurView?.setStatus(targetRatio, false)
                            if (MINUS_OVERLAP) {
                                val mLauncher = this.instance.current().field {
                                    name = "mLauncher"
                                    superClass()
                                }.any()
                                val mWorkspace = XposedHelpers.getObjectField(
                                    mLauncher,
                                    "mWorkspace"
                                ) as? ViewGroup ?: return@after
                                mWorkspace.scaleX = 1.0f
                                mWorkspace.scaleY = 1.0f
                                val mScreenContent = XposedHelpers.getObjectField(mLauncher, "mScreenContent") as? FrameLayout ?: return@after
                                val scale = SHOW_LAUNCH_IN_MINUS_SCALE + (if (targetRatio == 1.0f) 0 else 1) * (1 - SHOW_LAUNCH_IN_MINUS_SCALE)
                                mScreenContent.scaleX = scale
                                mScreenContent.scaleY = scale
//                                mScreenContent.scaleX = 1.0f
//                                mScreenContent.scaleY = 1.0f
                            }
                        }
                    }
                }
        }
        // Extra fix
        if (EXTRA_FIX) {
            "com.miui.home.recents.NavStubView".toClass().apply {
                method {
                    name = "commonAppTouchFromDown"
                }.ignored().hook {
                    before {
                        if (isStartingApp) {
                            transitionBlurView?.restore()
                            wallpaperZoomManager?.restore()
                        } else {
                            transitionBlurView?.setStatus(visible = true, useAnim = false)
                            wallpaperZoomManager?.zoomOut(false, 1.0f)
                        }
                    }
                }
            }
        }
    }

    private fun initBlurView(
        context: Context,
        transitionBlurView: MiBlurView?,
        folderBlurView: MiBlurView?,
        wallpaperBlurView: MiBlurView?,
        minusBlurView: MiBlurView?
    ) {
        // Transition
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
        // Folder
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
        // Wallpaper
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
        // Minus screen
        val minusUseBlur = Prefs.getBoolean(Refactor.MINUS_BLUR, HomeRefactor.MINUS_BLUR)
        val minusBlurRadius = Prefs.getPixelByStr(Refactor.MINUS_BLUR_RADIUS_STR, HomeRefactor.MINUS_BLUR_RADIUS_STR, context)
        val minusUseDim = Prefs.getBoolean(Refactor.MINUS_DIM, HomeRefactor.MINUS_DIM)
        val minusDimAlpha = Prefs.getInt(Refactor.MINUS_DIM_MAX, HomeRefactor.MINUS_DIM_MAX)
        // Scale
//        val minusUseScale = MINUS_OVERLAP
//        val minusScaleRatio = 1.0f - SHOW_LAUNCH_IN_MINUS_SCALE

        transitionBlurView?.let {
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
        folderBlurView?.let {
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
        wallpaperBlurView?.let {
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
        if (MINUS_BLUR || MINUS_DIM) {
            minusBlurView?.let {
                it.setPassWindowBlur(true)
                it.setBlur(minusUseBlur, minusBlurRadius)
                it.setDim(minusUseDim, minusDimAlpha)
                it.setScale(false, 0f)
                it.setNonlinear(false, LinearInterpolator())
                // it.setPassWindowBlur(BlurRefactorEntry.EXTRA_COMPATIBILITY)
            }
        }
    }

    private fun printDebugInfo(msg: String) {
        if (PRINT_DEBUG_INFO) {
            YLog.info(msg)
        }
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