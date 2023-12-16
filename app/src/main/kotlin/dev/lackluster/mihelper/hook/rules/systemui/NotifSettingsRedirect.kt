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
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotifSettingsRedirect : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS) {
            "com.android.systemui.statusbar.notification.row.MiuiNotificationMenuRow\$\$ExternalSyntheticLambda1".toClass()
                .method {
                    name = "onClick"
                }
                .hook {
                    before {
                        val typeId = this.instance.current().field {
                            name = "\$r8\$classId"
                        }.int()
                        if (typeId == 2) {
                            val miuiNotificationMenuRow3 = this.instance.current().field {
                                name = "f\$0"
                            }.any() ?: return@before
                            val expandedNotification = miuiNotificationMenuRow3.current().field {
                                name = "mSbn"
                            }.any() ?: return@before
                            val statusBarNotification =
                                expandedNotification as StatusBarNotification
                            val context = miuiNotificationMenuRow3.current().field {
                                name = "mContext"
                            }.any() as? Context ?: return@before
                            val isHybrid =
                                "com.android.systemui.statusbar.notification.NotificationUtil".toClass()
                                    .method {
                                        name = "isHybrid"
                                        modifiers { isStatic }
                                    }.get().boolean(expandedNotification)
                            if (!isHybrid) {
                                var intent = Intent(Intent.ACTION_MAIN)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setClassName("com.android.settings", "com.android.settings.SubSettings")
                                    .putExtra(
                                        ":android:show_fragment",
                                        "com.android.settings.notification.ChannelNotificationSettings"
                                    )
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, statusBarNotification.packageName)
                                    .putExtra(Settings.EXTRA_CHANNEL_ID, statusBarNotification.notification.channelId)
                                    .putExtra("app_uid", statusBarNotification.uid)
                                    .putExtra(Settings.EXTRA_CONVERSATION_ID, statusBarNotification.notification.shortcutId)
                                val needSplit = "com.miui.utils.configs.MiuiConfigs".toClass().method {
                                    name = "isPadOrFoldLargeScreen"
                                    modifiers { isStatic }
                                }.get().boolean(context)
                                if (needSplit) {
                                    intent = "com.miui.utils.IntentUtils".toClass()
                                        .method {
                                            name = "transformToSplitIntent"
                                            paramCount = 2
                                            modifiers { isStatic }
                                        }.get().call(context, intent) as Intent
                                }
                                val userHandleCurrent = UserHandle::class.java.field {
                                    name = "CURRENT"
                                    modifiers { isStatic }
                                }.get().any() as? UserHandle
                                ContextClass.method {
                                    name = "startActivityAsUser"
                                    param(IntentClass, UserHandleClass)
                                }.get(context).call(intent, userHandleCurrent)
                                val modalController = "com.android.systemui.Dependency".toClass().method {
                                    name = "get"
                                    param(JavaClass)
                                    modifiers { isStatic }
                                }.get().call("com.android.systemui.statusbar.notification.modal.ModalController".toClass()) ?: return@before
                                modalController.current().method {
                                    name = "animExitModal"
                                    paramCount = 4
                                }.call(50L, true, "MORE", false)
                                val commandQueue = "com.android.systemui.Dependency".toClass().method {
                                    name = "get"
                                    param(JavaClass)
                                    modifiers { isStatic }
                                }.get().call("com.android.systemui.statusbar.CommandQueue".toClass()) ?: return@before
                                commandQueue.current().method {
                                    name = "animateCollapsePanels"
                                    param(IntType, BooleanType)
                                    paramCount = 2
                                }.call(0, false)
                                this.result = null
                            }
                        }
                    }
                }
        }
    }
}