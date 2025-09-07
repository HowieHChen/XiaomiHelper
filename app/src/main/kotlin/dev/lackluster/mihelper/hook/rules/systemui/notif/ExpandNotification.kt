package dev.lackluster.mihelper.hook.rules.systemui.notif

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ExpandNotification : YukiBaseHooker() {
    private val expand by lazy {
        Prefs.getInt(Pref.Key.SystemUI.NotifCenter.EXPAND_NOTIFICATION, 0)
    }

    override fun onHook() {
        if (expand != 0) {
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator".toClassOrNull()?.apply {
                constructor().hook {
                    after {
                        when (expand) {
                            1 -> {
                                this.instance.current(true).field {
                                    name = "mAutoExpandFirstNotification"
                                }.setTrue()
                            }
                            2 -> {
                                this.instance.current(true).field {
                                    name = "mAlwaysExpandNonGroupedNotification"
                                }.setTrue()
                            }
                        }
                    }
                }
            }
        }
    }
}