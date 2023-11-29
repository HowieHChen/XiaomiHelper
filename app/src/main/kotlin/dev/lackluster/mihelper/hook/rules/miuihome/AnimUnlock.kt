package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AnimUnlock : YukiBaseHooker(){
    override fun onHook() {
        hasEnable(PrefKey.HOME_ANIM_UNLOCK) {
            (
                if (Device.isPad) "com.miui.home.launcher.compat.UserPresentAnimationCompatV12Spring"
                else "com.miui.home.launcher.compat.UserPresentAnimationCompatV12Phone"
            ).toClass()
                .method {
                    name = "getSpringAnimator"
                    paramCount = 6
                }
                .hook {
                    before {
                        this.args(4).set(0.5f)
                        this.args(5).set(0.5f)
                    }
                }


        }
    }
}