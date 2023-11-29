package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DoNotKillApp : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.POWER_DONOT_KILL_APP) {
            "miui.process.ProcessManager".toClass()
                .method {
                    name = "kill"
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}