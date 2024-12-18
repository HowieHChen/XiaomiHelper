package dev.lackluster.mihelper.hook.rules.lbe

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object BlockRemoveAutoStart : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.LBE.BLOCK_REMOVE_AUTO_STARTUP) {
            "com.miui.privacy.autostart.AutoRevokePermissionManager".toClassOrNull()?.apply {
                method {
                    name = "startScheduleASCheck"
                    param(ContextClass, BooleanType)
                }.hook {
                    intercept()
                }
            }
        }
    }
}