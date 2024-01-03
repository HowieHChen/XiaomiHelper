package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object BlurEnableAll : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_BLUR_ALL) {
            val blurUtilsClass = "com.miui.home.launcher.common.BlurUtils".toClass()
            blurUtilsClass.method {
                name = "getBlurType"
            }.hook {
                replaceTo(2)
            }

            hasEnable(PrefKey.HOME_BLUR_ENHANCE) {
                if (Device.isPad) {
                    val launcherClz = "com.miui.home.launcher.Launcher".toClass()
                    val openMethod =
                        launcherClz.methods.first { it.name == "lambda\$openLauncherFolder\$34\$Launcher" }
                    val closeMethod =
                        launcherClz.methods.first { it.name == "closeFolder" && it.parameterCount == 1 && it.parameterTypes[0] == BooleanType }
                    XposedBridge.hookMethod(openMethod, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            val launcher = param?.thisObject ?: return
                            val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                            val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                            if (!isLaptopMode && !isInEditing) {
                                val window = (launcher as Activity).window
                                XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 1.0f, window, true)
                            }
                        }
                    })
                    XposedBridge.hookMethod(closeMethod, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            val launcher = param?.thisObject ?: return
                            val isFolderOpen = param.args[0] as Boolean
                            val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                            val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                            if (!isLaptopMode && !isInEditing) {
                                val window = (launcher as Activity).window
                                XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlur", 0.0f, window, isFolderOpen)
                            }
                        }
                    })
                    "com.miui.home.recents.GestureModeApp".toClass()
                        .method {
                            name = "performAppToHome"
                        }
                        .hook {
                            after {
                                val mLauncher = this.instance.current().field { name = "mLauncher"; superClass() }.any() ?: return@after
                                val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
                                blurUtilsClass.method {
                                    name = "fastBlurWhenUseCompleteRecentsBlur"
                                    paramCount = 3
                                    modifiers { isStatic }
                                }.get().call(mLauncher, 1.0f, false)
                                if (!isFolderShowing) {
                                    blurUtilsClass.method {
                                        name = "fastBlurWhenUseCompleteRecentsBlur"
                                        paramCount = 3
                                        modifiers { isStatic }
                                    }.get().call(mLauncher, 0.0f, true)
                                }
                            }
                        }
                    "com.miui.home.launcher.Workspace".toClass()
                        .method {
                            name = "setEditMode"
                        }
                        .hook {
                            after {
                                val mLauncher = this.instance.current().field { name = "mLauncher"; superClass() }.any() ?: return@after
                                val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
                                val isInNormalEditing = XposedHelpers.callMethod(this.instance, "isInNormalEditingMode") as Boolean
                                if (!isInNormalEditing && isFolderShowing) {
                                    blurUtilsClass.method {
                                        name = "fastBlurWhenUseCompleteRecentsBlur"
                                        paramCount = 3
                                        modifiers { isStatic }
                                    }.get().call(mLauncher, 1.0f, false)
                                }
                            }
                        }
                }
                else {
                    "com.miui.home.recents.NavStubView".toClass()
                        .method {
                            name = "commonAnimStartAppToHome"
                        }
                        .hook {
                            after {
                                val mLauncher = this.args(0).any() ?: return@after
                                val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
                                if (!isFolderShowing) {
                                    blurUtilsClass.method {
                                        name = "fastBlurWhenUseCompleteRecentsBlur"
                                        paramCount = 3
                                        modifiers { isStatic }
                                    }.get().call(mLauncher, 1.0f, false)
                                    blurUtilsClass.method {
                                        name = "fastBlurWhenUseCompleteRecentsBlur"
                                        paramCount = 3
                                        modifiers { isStatic }
                                    }.get().call(mLauncher, 0.0f, true)
                                }
                            }
                        }
                }
            }
        }
    }
}