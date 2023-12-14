package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotificationMaxNumber : YukiBaseHooker() {
    private val maxIcon by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_NOTIF_ICON_MAX, 1)
    }
    private val maxDot by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_NOTIF_DOT_MAX, 3)
    }
    private val maxLockscreen by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_NOTIF_LOCKSCREEN_MAX, 3)
    }
    override fun onHook() {
        hasEnable(PrefKey.STATUSBAR_NOTIF_MAX) {
            "com.android.systemui.statusbar.phone.NotificationIconContainer".toClass()
                .constructor()
                .hook {
                    after {
                        this.instance.current().field {
                            name = "mMaxDots"
                        }.set(maxDot)
                        this.instance.current().field {
                            name = "mMaxStaticIcons"
                        }.set(maxIcon)
                        this.instance.current().field {
                            name = "mMaxIconsOnLockscreen"
                        }.set(maxLockscreen)
//                        this.instance.current().field {
//                            name = "mMaxStaticIcons"
//                        }.set(maxLockscreen)
                    }
                }
        }
    }
}