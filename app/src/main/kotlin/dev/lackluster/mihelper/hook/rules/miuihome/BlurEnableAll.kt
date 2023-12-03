package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import android.os.Message
import android.view.MotionEvent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XC_MethodHook
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
                val navStubViewClass = "com.miui.home.recents.NavStubView".toClass()
                val applicationClass = "com.miui.home.launcher.Application".toClass()
                XposedHelpers.findAndHookMethod(navStubViewClass, "onPointerEvent", MotionEvent::class.java, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val mLauncher = XposedHelpers.callStaticMethod(applicationClass,"getLauncher") as Activity
                        val motionEvent = param?.args?.get(0) as MotionEvent
                        val action = motionEvent.action
                        if (action == 2) Thread.currentThread().priority = 10
                        if (action == 2 && param.thisObject.current().field {
                                name = "mWindowMode"
                            }.int() == 2)
                            XposedHelpers.callStaticMethod(blurUtilsClass, "fastBlurDirectly", 1.0f, mLauncher.window)
                    }
                })
            }
        }
    }
}