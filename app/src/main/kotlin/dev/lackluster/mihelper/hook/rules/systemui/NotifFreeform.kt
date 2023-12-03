package dev.lackluster.mihelper.hook.rules.systemui

import android.os.Build
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotifFreeform : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_FREEFORM) {
            if (Device.androidVersion == Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                "com.android.systemui.statusbar.notification.row.MiuiExpandableNotificationRow".toClassOrNull()
                    ?.method {
                        name = "updateMiniWindowBar"
                    }
                    ?.hook {
                        after {
                            this.instance.current().field {
                                name = "mCanSlide"
                            }.setTrue()
                        }
                    }
            }
            else {
                "com.android.systemui.statusbar.notification.NotificationSettingsManager".toClassOrNull()
                    ?.method {
                        name = "canSlide"
                        param(StringClass)
                    }
                    ?.hook {
                        replaceToTrue()
                    }
            }
        }
    }
}