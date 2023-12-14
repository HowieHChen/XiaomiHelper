package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideDisturbNotification : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_DISTURB) {
            "com.android.systemui.statusbar.notification.zen.ZenModeViewController".toClass()
                .method {
                    name = "updateVisibility"
                }
                .hook {
                    before {
                        this.instance.current().field {
                            name = "manuallyDismissed"
                        }.setTrue()
                    }
                }
        }
    }
}