package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.w

object SuppressFold : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.SystemUI.NotifCenter.SUPPRESS_FOLD_NOTIF.get())
    }

    override fun onHook() {
        "com.miui.systemui.notification.MiuiBaseNotifUtil".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "shouldSuppressFold"
            }?.hook {
                result(true)
            }
        }
        "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "shouldIgnoreEntry"
            }?.hook {
                result(true)
            }
            resolve().firstMethodOrNull {
                name = "setFold"
            }?.hook {
                if (getArg(1) as? Boolean != false) {
                    w { "setFold isFold ${getArg(1)} ${getArg(0)}" }
                }
                result(proceed())
            }
        }
    }
}