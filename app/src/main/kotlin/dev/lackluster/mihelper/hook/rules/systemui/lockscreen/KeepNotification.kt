package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object KeepNotification : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.LockScreen.KEEP_NOTIFICATION) {
            "com.android.systemui.statusbar.notification.interruption.KeyguardNotificationVisibilityProviderImpl".toClassOrNull()?.apply {
                method {
                    name = "shouldHideNotification"
                }.hook {
                    before {
                        val notificationEntry = this.args(0).any() ?: return@before
                        val mSbn = notificationEntry.current().field {
                            name = "mSbn"
                        }.any() ?: return@before
                        mSbn.current().field {
                            name = "mHasShownAfterUnlock"
                        }.setFalse()
                    }
                }
            }
        }
    }
}