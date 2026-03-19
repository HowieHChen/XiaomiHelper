package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SuppressFold : YukiBaseHooker() {
    override fun onHook() {
        hasEnable (Pref.Key.SystemUI.NotifCenter.SUPPRESS_FOLD) {
            "com.miui.systemui.notification.MiuiBaseNotifUtil".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "shouldSuppressFold"
                }?.hook {
                    replaceToTrue()
                }
            }
        }
    }
}