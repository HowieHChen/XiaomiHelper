package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object FolderBlur : YukiBaseHooker() {
    private var isShouldBlur = false
    override fun onHook() {
        hasEnable(PrefKey.HOME_FOLDER_BLUR) {
            val folderInfo = "com.miui.home.launcher.FolderInfo".toClass()
            val launcherClass = "com.miui.home.launcher.Launcher".toClass()
            val blurUtilsClass = "com.miui.home.launcher.common.BlurUtils".toClass()
            val navStubViewClass = "com.miui.home.recents.NavStubView".toClass()
            val cancelShortcutMenuReasonClass = "com.miui.home.launcher.shortcuts.CancelShortcutMenuReason".toClass()
            val applicationClass = "com.miui.home.launcher.Application".toClass()
            runCatching {
                XposedHelpers.findAndHookMethod(launcherClass, "isShouldBlur", object :XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        param?.result = false
                    }
                })
                XposedHelpers.findAndHookMethod(blurUtilsClass, "fastBlurWhenOpenOrCloseFolder", launcherClass, Boolean::class.java, object :XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        param?.result = null
                    }
                })
            }
            XposedHelpers.findAndHookMethod(launcherClass, "openFolder", folderInfo, View::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass, "getLauncher") as Activity
                    val isInNormalEditing = XposedHelpers.callMethod(mLauncher, "isInNormalEditing") as Boolean
                    if (!isInNormalEditing)
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true)
                }
            })
            XposedHelpers.findAndHookMethod(launcherClass, "isFolderShowing", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    isShouldBlur = param?.result as Boolean
                }
            })
            XposedHelpers.findAndHookMethod(launcherClass, "closeFolder", Boolean::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    isShouldBlur = false
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    val isInNormalEditing = XposedHelpers.callMethod(mLauncher, "isInNormalEditing") as Boolean
                    if (isInNormalEditing)
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                    else
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 0.0f, mLauncher.window, true)
                }
            })
            XposedHelpers.findAndHookMethod(launcherClass, "cancelShortcutMenu", Int::class.java, cancelShortcutMenuReasonClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    if (isShouldBlur)
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                }
            })
            XposedHelpers.findAndHookMethod(launcherClass, "onGesturePerformAppToHome", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    if (isShouldBlur)
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                }
            })
            XposedBridge.hookAllMethods(blurUtilsClass, "fastBlurWhenStartOpenOrCloseApp", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    val isInEditing = XposedHelpers.callMethod(mLauncher, "isInEditing") as Boolean
                    if (isShouldBlur) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                    else if (isInEditing) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                }
            })
            XposedBridge.hookAllMethods(blurUtilsClass, "fastBlurWhenFinishOpenOrCloseApp", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    val isInEditing = XposedHelpers.callMethod(mLauncher, "isInEditing") as Boolean
                    if (isShouldBlur) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                    else if (isInEditing) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                }
            })
            XposedBridge.hookAllMethods(blurUtilsClass, "fastBlurWhenEnterRecents", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    val isInShortcutMenuState = XposedHelpers.callMethod(mLauncher, "isInShortcutMenuState") as Boolean
                    if (isInShortcutMenuState)
                        XposedHelpers.callMethod(param?.args?.get(0) ?: return, "hideShortcutMenuWithoutAnim")
                }
            })
            XposedBridge.hookAllMethods(blurUtilsClass, "fastBlurWhenExitRecents", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                    val isInEditing = XposedHelpers.callMethod(mLauncher, "isInEditing") as Boolean
                    if (isShouldBlur) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                    else if (isInEditing) param?.result =
                        XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, mLauncher.window, true, 0L)
                }
            })
            XposedBridge.hookAllMethods(blurUtilsClass, "fastBlurDirectly", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val blurRatio = param?.args?.get(0) as Float
                    if (isShouldBlur && blurRatio == 0.0f) {
                        param.result = null
                    }
                }
            })

            val blurEnhance = Prefs.getBoolean(PrefKey.HOME_BLUR_ALL, false) && Prefs.getBoolean(PrefKey.HOME_BLUR_ENHANCE, false)
            if (blurEnhance) {
                XposedHelpers.findAndHookMethod(navStubViewClass, "onPointerEvent", MotionEvent::class.java, object :XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                        val motionEvent = param?.args?.get(0) as MotionEvent
                        val action = motionEvent.action
                        if (action == 2) Thread.currentThread().priority = 10
                        if (action == 2 && isShouldBlur)
                            XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlurDirectly", 1.0f, mLauncher.window)
                    }
                })
            }
        }
    }
}