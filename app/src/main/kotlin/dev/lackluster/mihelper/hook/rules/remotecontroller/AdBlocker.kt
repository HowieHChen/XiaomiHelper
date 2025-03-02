package dev.lackluster.mihelper.hook.rules.remotecontroller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.RemoteController.AD_BLOCKER) {
            "com.xiaomi.mitv.phone.remotecontroller.common.activity.BaseActivity".toClassOrNull()?.apply {
                method {
                    name = "setActionMark"
                    paramCount = 2
                }.hook {
                    intercept()
                }
            }
        }
    }
}