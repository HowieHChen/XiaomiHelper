package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable


object BlurEnableAll : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_BLUR_ALL, extraCondition = {
            !Prefs.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)
        }) {
            val launcherClz = "com.miui.home.launcher.Launcher".toClass()
            val blurUtilsClass = "com.miui.home.launcher.common.BlurUtils".toClass()
            val fastBlurWhenUseCompleteRecentsBlur = blurUtilsClass.method {
                name = "fastBlurWhenUseCompleteRecentsBlur"
                paramCount = 3
                modifiers { isStatic }
            }.get()
            blurUtilsClass.method {
                name = "getBlurType"
            }.hook {
                replaceTo(2)
            }
            hasEnable(PrefKey.HOME_BLUR_ENHANCE) {
                if (Device.isPad) {
                    val isShouldBlurMethod =
                        launcherClz.methods.first { it.name == "isShouldBlur" }
                    val openMethod =
                        launcherClz.methods.first { it.name == "lambda\$openLauncherFolder\$34\$Launcher" }
                    val closeMethod =
                        launcherClz.methods.first { it.name == "closeFolder" && it.parameterCount == 1 && it.parameterTypes[0] == BooleanType }
                    XposedBridge.hookMethod(isShouldBlurMethod, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            val launcher = param?.thisObject ?: return
                            val isInNormalEditing = XposedHelpers.callMethod(launcher, "isInNormalEditing") as Boolean
                            val isFolderShowing = (XposedHelpers.callMethod(launcher, "isFolderShowing") as Boolean?) ?: false
                            param.result = isInNormalEditing || isFolderShowing
                        }
                    })
                    XposedBridge.hookMethod(openMethod, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            val launcher = param?.thisObject ?: return
                            val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                            val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                            if (!isLaptopMode && !isInEditing) {
                                fastBlurWhenUseCompleteRecentsBlur.call(launcher, 1.0f, true)
                            }
                        }
                    })
                    XposedBridge.hookMethod(closeMethod, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            val launcher = param?.thisObject ?: return
                            val isFolderOpenAnim = param.args[0] as Boolean
                            val isLaptopMode = XposedHelpers.callMethod(launcher, "isLapTopMode") as Boolean
                            val isInEditing = XposedHelpers.callMethod(launcher, "isInEditing") as Boolean
                            if (!isLaptopMode && !isInEditing) {
                                fastBlurWhenUseCompleteRecentsBlur.call(launcher, 0.0f, isFolderOpenAnim)
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
                                fastBlurWhenUseCompleteRecentsBlur.call(mLauncher, 1.0f, false)
                                if (!isFolderShowing) {
                                    fastBlurWhenUseCompleteRecentsBlur.call(mLauncher, 0.0f, true)
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
                                if (isInNormalEditing || isFolderShowing) {
                                    fastBlurWhenUseCompleteRecentsBlur.call(mLauncher, 1.0f, false)
                                }
                            }
                        }
                }
//                else {
//                    "com.miui.home.recents.NavStubView".toClass()
//                        .method {
//                            name = "commonAnimStartAppToHome"
//                        }
//                        .hook {
//                            after {
//                                val mLauncher = this.args(0).any() ?: return@after
//                                val isFolderShowing = (XposedHelpers.callMethod(mLauncher, "isFolderShowing") as Boolean?) ?: false
//                                if (!isFolderShowing) {
//                                    fastBlurWhenUseCompleteRecentsBlur.call(mLauncher, 1.0f, false)
//                                    fastBlurWhenUseCompleteRecentsBlur.call(mLauncher, 0.0f, true)
//                                }
//                            }
//                        }
//                }
            }
        }
    }
}