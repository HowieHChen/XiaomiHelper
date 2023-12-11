package dev.lackluster.mihelper.hook.rules.systemui

import android.os.Build
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotificationMaxNumber : YukiBaseHooker() {
    private val maxIcon by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_NOTIF_ICON_MAX, 3)
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
                .method {
                    name = "miuiShowNotificationIcons"
                    paramCount = 1
                }
                .hook {
                    replaceUnit {
                        if (this.args(0).boolean()) {
                            this.instance.current().field {
                                name = "MAX_DOTS"
                            }.set(maxDot)
                            this.instance.current().field {
                                name = "MAX_STATIC_ICONS"
                            }.set(maxIcon)
                            if (Device.androidVersion == Build.VERSION_CODES.TIRAMISU) {
                                this.instance.current().field {
                                    name = "MAX_ICONS_ON_LOCKSCREEN"
                                }.set(maxLockscreen)
                            }
                            else {
                                this.instance.current().field {
                                    name = "MAX_VISIBLE_ICONS_ON_LOCK"
                                }.set(maxLockscreen)
                            }
                        }
                        else {
                            this.instance.current().field {
                                name = "MAX_DOTS"
                            }.set(0)
                            this.instance.current().field {
                                name = "MAX_STATIC_ICONS"
                            }.set(0)
                            if (Device.androidVersion == Build.VERSION_CODES.TIRAMISU) {
                                this.instance.current().field {
                                    name = "MAX_ICONS_ON_LOCKSCREEN"
                                }.set(0)
                            }
                            else {
                                this.instance.current().field {
                                    name = "MAX_VISIBLE_ICONS_ON_LOCK"
                                }.set(0)
                            }
                        }
                        this.instance.current().method {
                            name = "updateState"
                        }.call()
                    }
                }
        }
    }
}