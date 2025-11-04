package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideNextAlarm : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.HIDE_NEXT_ALARM) {
            clzMiuiKeyguardStatusBarView?.apply {
                val mNextTrigger = resolve().firstFieldOrNull {
                    name = "mNextTrigger"
                }
                resolve().firstMethodOrNull {
                    name = "showNextAlarm"
                }?.hook {
                    before {
                        this.args(0).set(-1L)
                        mNextTrigger?.copy()?.of(this.instance)?.set(-1L)
                    }
                }
            }
        }
    }
}