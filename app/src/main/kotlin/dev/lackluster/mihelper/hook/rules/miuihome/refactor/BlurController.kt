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

import android.view.ViewGroup
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
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
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_BLUR
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_DIM
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.MINUS_OVERLAP
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.folderBlurView
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isAllAppsShowing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isFolderShowing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.isInNormalEditing
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.minusBlurView
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.transitionBlurView
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.wallpaperBlurView
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.wallpaperZoomManager
import dev.lackluster.mihelper.utils.Prefs
import java.util.concurrent.Executor

object BlurController : YukiBaseHooker() {
    private val blurUtilsClz by lazy {
        "com.miui.home.launcher.common.BlurUtils".toClass()
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

    override fun onHook() {
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
                        isInNormalEditing(launcher) || isAllAppsShowing(
                            launcher
                        ), false
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