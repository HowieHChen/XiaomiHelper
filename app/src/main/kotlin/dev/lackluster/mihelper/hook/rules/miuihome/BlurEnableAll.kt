package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
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
                "com.miui.home.recents.NavStubView".toClass()
                    .method {
                        name = if (Device.isPad) "performAppToHome" else "commonAnimStartAppToHome"
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