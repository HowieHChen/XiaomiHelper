package dev.lackluster.mihelper.hook.rules.systemui

import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.provider.Settings
import android.service.notification.StatusBarNotification
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotifSettingsRedirect : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS) {
            var statusBarNotification: StatusBarNotification? = null
            var context: Context? = null
            "com.android.systemui.statusbar.notification.row.MiuiNotificationMenuRow".toClass()
                .method {
                    name = "onClickInfoItem"
                }
                .hook {
                    before {
                        statusBarNotification = this.instance.current().field {
                            name = "mSbn"
                        }.any() as? StatusBarNotification
                        context = this.instance.current().field {
                            name = "mContext"
                        }.any() as? Context
                    }
                    after {
                        context = null
                        statusBarNotification = null
                    }
                }
            "com.android.systemui.statusbar.notification.NotificationSettingsHelper".toClass()
                .method {
                    name = "startAppNotificationSettings"
                }
                .hook {
                    before {
                        val intent = Intent(Intent.ACTION_MAIN)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setClassName("com.android.settings", "com.android.settings.SubSettings")
                            .putExtra(
                                ":android:show_fragment",
                                "com.android.settings.notification.ChannelNotificationSettings"
                            )
                            .putExtra(Settings.EXTRA_APP_PACKAGE, statusBarNotification?.packageName)
                            .putExtra(Settings.EXTRA_CHANNEL_ID, statusBarNotification?.notification?.channelId)
                            .putExtra("app_uid", statusBarNotification?.uid)
                            .putExtra(Settings.EXTRA_CONVERSATION_ID, statusBarNotification?.notification?.shortcutId)
                        val userHandleCurrent = UserHandle::class.java.field {
                            name = "CURRENT"
                            modifiers { isStatic }
                        }.get().any() as? UserHandle
                        ContextClass.method {
                            name = "startActivityAsUser"
                            param(IntentClass, UserHandleClass)
                        }.get(context ?: return@before).call(intent, userHandleCurrent)
                        this.result = null
                    }
                }
        }
    }
}