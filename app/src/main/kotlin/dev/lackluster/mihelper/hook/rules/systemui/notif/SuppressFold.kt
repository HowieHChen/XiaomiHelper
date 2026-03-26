package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
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
            "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "shouldIgnoreEntry"
                }?.hook {
                    replaceToTrue()
                }
                resolve().firstMethodOrNull {
                    name = "setFold"
                }?.hook {
                    before {
                        if (this.args(1).boolean()) {
                            YLog.info("setFold isFold ${this.args(1).boolean()} ${this.args(0).any()}")
                        }
                    }
                }
            }
        }
    }
}