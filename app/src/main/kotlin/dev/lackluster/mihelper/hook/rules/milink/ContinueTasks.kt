package dev.lackluster.mihelper.hook.rules.milink

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object ContinueTasks : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiMirror.CONTINUE_ALL_TASKS) {
            "com.xiaomi.mirror.synergy.MiuiSynergySdk".toClassOrNull()?.apply {
                method {
                    name = "isSupportSendApp"
                    paramCount = 2
                }.hook {
                    after {
                        this.result = true
                    }
                }
                method {
                    name = "isSupportSendApp"
                    paramCount = 3
                }.hook {
                    after {
                        this.result = true
                    }
                }
                method {
                    name = "isSupportSendAppToPhone"
                    paramCount = 2
                }.hook {
                    after {
                        this.result = true
                    }
                }
            }
        }
    }
}