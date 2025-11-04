package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.utils.factory.hasEnable

object StatusBarClockContainer : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.KEEP_CLOCK_CONTAINER) {
            clzMiuiKeyguardStatusBarView?.apply {
                resolve().firstMethodOrNull {
                    name = "animateClockContainer"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}