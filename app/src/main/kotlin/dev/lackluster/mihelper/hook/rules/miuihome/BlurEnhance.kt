package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.PrefDefValue
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.hook.view.MiBlurView
import dev.lackluster.mihelper.utils.MiBlurUtils
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import java.util.concurrent.Executor

object BlurEnhance : YukiBaseHooker() {
    private val printDebugInfo = BuildConfig.DEBUG

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
    private val appsUseNonlinear =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_APPS_NONLINEAR, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR)
    private val appsNonlinearFactor =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_FACTOR, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_FACTOR)

    private val wallUseBlur =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALL_BLUR, PrefDefValue.HOME_REFACTOR_WALL_BLUR)
    private val wallBlurRadius =
        Prefs.getInt(PrefKey.HOME_REFACTOR_WALL_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_WALL_BLUR_RADIUS)
    private val wallUseDim =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALL_DIM, PrefDefValue.HOME_REFACTOR_WALL_DIM)
    private val wallDimAlpha =
        Prefs.getInt(PrefKey.HOME_REFACTOR_WALL_DIM_MAX, PrefDefValue.HOME_REFACTOR_WALL_DIM_MAX)
    private val wallUseNonlinear =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_WALL_NONLINEAR, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR)
    private val wallNonlinearFactor =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_FACTOR, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_FACTOR)

    private val launchShow =
        Prefs.getBoolean(PrefKey.HOME_REFACTOR_LAUNCH_SHOW, PrefDefValue.HOME_REFACTOR_LAUNCH_SHOW)
    private val launchScale =
        Prefs.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_SCALE, PrefDefValue.HOME_REFACTOR_LAUNCH_SCALE)
//    private val launchUseNonlinear =
//        Prefs.getBoolean(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR, PrefDefValue.HOME_REFACTOR_LAUNCH_NONLINEAR)
//    private val launchNonlinearFactor =
//        Prefs.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR, PrefDefValue.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR)

    private val extraFix = Prefs.getBoolean(PrefKey.HOME_REFACTOR_EXTRA_FIX, PrefDefValue.HOME_REFACTOR_EXTRA_FIX)
    private var isStartingApp = false

    override fun onHook() {
        if (!MiBlurUtils.supportBackgroundBlur()) {
            return
        }
        var transitionBlurView : MiBlurView? = null
        var wallpaperBlurView : MiBlurView? = null
        hasEnable(PrefKey.HOME_BLUR_REFACTOR) {
            // Block original blurring
            blurUtils.method {
                name = "fastBlurDirectly"
            }.hook {
                intercept()
            }
            // Add blur view to Launcher
            blurUtilities.method {
                name = "setBackgroundBlurEnabled"
                modifiers { isStatic }
            }.hook {
                after {
                    if (!isBackgroundBlurEnabled.boolean()) {
                        if (Prefs.getBoolean(PrefKey.HOME_BLUR_ENHANCE, false)) {
                            isBackgroundBlurEnabled.setTrue()
                        }
                        else {
                            YLog.warn("The High-quality materials function is disabled.")
                        }
                    }
                    val launcher = this.args(0).any()
                    transitionBlurView = MiBlurView(launcher as Activity)
                    transitionBlurView?.let {
                        it.setBlur(appsUseBlur, appsBlurRadius)
                        it.setDim(appsUseDim, appsDimAlpha)
                        it.setNonlinear(appsUseNonlinear, appsNonlinearFactor)
                    }
                    wallpaperBlurView = MiBlurView(launcher)
                    wallpaperBlurView?.let {
                        it.setBlur(wallUseBlur, wallBlurRadius)
                        it.setDim(wallUseDim, wallDimAlpha)
                        it.setNonlinear(wallUseNonlinear, wallNonlinearFactor)
                    }
                    val viewGroup = XposedHelpers.getObjectField(launcher, "mLauncherView") as ViewGroup
                    viewGroup.addView(transitionBlurView, viewGroup.indexOfChild(
                        XposedHelpers.getObjectField(launcher, "mOverviewPanel") as View
                    ).coerceAtLeast(0))
                    viewGroup.addView(wallpaperBlurView, 0)
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
                    transitionBlurView = null
                    wallpaperBlurView = null
                }
            }
            // Seems to be used only for blurring wallpaper
            blurUtils.method {
                name = "fastBlur"
                paramCount = 3
            }.hook {
                before {
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
                        transitionBlurView?.show(true, 1.0f)
                        isStartingApp = true
                    }
                    else {
                        // "isOpen" seems to always be true
                        if (shouldBlurWallpaper(this.args(1).any() ?: return@before)) {
                            wallpaperBlurView?.show(false)
                        }
                        transitionBlurView?.show(false)
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
                    val usrAnim = this.args(2).boolean()
                    mainThreadExecutor.execute {
                        if (usrAnim) {
                            transitionBlurView?.show(
                                true, this.args(1).float()
                            )
                            isStartingApp = false
                        }
                        else if (isStartingApp) {
                            transitionBlurView?.restore()
                        }
                        else {
                            transitionBlurView?.show(
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
                        val usrAnim = this.args(1).boolean()
                        if (shouldBlurWallpaper(this.args(0).any() ?: return@execute)) {
                            wallpaperBlurView?.show(false)
                        }
//                        if (usrAnim) {
//                            transitionBlurView.show(false)
//                        }
                        transitionBlurView?.hide(usrAnim)
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
                        return@replaceUnit
                    }
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
                        return@replaceUnit
                    }
                    val usrAnim = this.args(2).boolean()
                    if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                        wallpaperBlurView?.show(false)
                    }
//                    if (usrAnim) {
//                        transitionBlurView.show(false)
//                    }
                    transitionBlurView?.hide(usrAnim)
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
                        val usrAnim = this.args(1).boolean()
                        if (shouldBlurWallpaper(this.args(0).any() ?: return@execute)) {
                            wallpaperBlurView?.show(false)
                        }
//                        if (usrAnim) {
//                            transitionBlurView.show(false)
//                        }
                        if (isStartingApp && !usrAnim) {
                            transitionBlurView?.hide(true)
                        }
                        else {
                            transitionBlurView?.hide(usrAnim)
                        }
                        isStartingApp = false
                    }
                }
            }
            // Blur wallpaper when opening a folder
            // Reset blurring when closing folder
            // Only affects wallpaper blur
            blurUtils.method {
                name = "fastBlurWhenOpenOrCloseFolder"
            }.ignored().hook {
                replaceUnit {
                    if (printDebugInfo)
                        YLog.info("fastBlurWhenOpenOrCloseFolder")
                    val usrAnim = this.args(1).boolean()
                    if (shouldBlurWallpaper(this.args(0).any() ?: return@replaceUnit)) {
                        wallpaperBlurView?.show(usrAnim)
                    }
                    else {
                        wallpaperBlurView?.hide(usrAnim)
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
                    val usrAnim = this.args(2).boolean()
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
                        usrAnim, this.args(1).float(), this.args(3).int()
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
                    transitionBlurView?.restore(true)
                }
            }
            // End of uncertainty section
            overviewStateScale.hook {
                replaceTo(launchScale)
            }
            if (launchShow) {
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
    }
    private fun shouldBlurWallpaper(launcher: Any): Boolean {
//        val isInNormalEditing = XposedHelpers.callMethod(launcher, "isInNormalEditing") as Boolean
//        val isFoldShowing = XposedHelpers.callMethod(launcher, "isFolderShowing") as Boolean
        return (XposedHelpers.callMethod(launcher, "isShouldBlur") as Boolean)
    }
}