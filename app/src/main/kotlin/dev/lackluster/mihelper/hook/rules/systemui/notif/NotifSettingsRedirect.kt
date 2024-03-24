/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.systemui.notif

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClass
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.miui_notification_menu_more_setting
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object NotifSettingsRedirect : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_SETTINGS) {
            "com.android.systemui.statusbar.notification.row.MiuiNotificationMenuRow\$\$ExternalSyntheticLambda1".toClass().method {
                name = "onClick"
            }.hook {
                before {
                    val typeId = this.instance.current().field {
                        name = "\$r8\$classId"
                    }.int()
                    if (typeId == 2) {
                        val miuiNotificationMenuRow3 = this.instance.current().field {
                            name = "f\$0"
                        }.any() ?: return@before
                        if (Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.NOTIF_CHANNEL_DIALOG, false)) {
                            val thisContext = this.instance.current().field {
                                type = ContextClass
                            }.any() as? Context ?: return@before
                            val miuiNotificationMenuItem = this.instance.current().field {
                                name = "f\$2"
                            }.any() ?: return@before
                            val modalDialog = "com.android.systemui.statusbar.notification.modal.ModalDialog".toClass()
                                .constructor().get().call(thisContext) ?: return@before
                            val iconView = modalDialog.current().field {
                                name = "mIconView"
                            }.any() as ImageView
                            iconView.setImageResource(
                                miuiNotificationMenuItem.current().field { name = "mIconResId" }.int()
                            )
                            iconView.visibility = View.VISIBLE
                            val titleTv = modalDialog.current().field {
                                name = "mTitleTv"
                            }.any() as TextView
                            titleTv.text = thisContext.getString(
                                miui_notification_menu_more_setting
                            )
                            titleTv.visibility = View.VISIBLE
                            thisContext.injectModuleAppResources()
                            modalDialog.current().method {
                                name = "setNegativeButton"
                            }.call(object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0?.dismiss()
                                    tryGoToChannelSettings(miuiNotificationMenuRow3, false)
                                }
                            })
                            modalDialog.current().method {
                                name = "setPositiveButton"
                            }.call(object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0?.dismiss()
                                    tryGoToChannelSettings(miuiNotificationMenuRow3, true)
                                }
                            })
                            (modalDialog.current().field {
                                name = "mNegativeButton"
                            }.any() as TextView).text = thisContext.getText(R.string.systemui_notif_redirect_dialog_negative_default)
                            (modalDialog.current().field {
                                name = "mPositiveButton"
                            }.any() as TextView).text = thisContext.getText(R.string.systemui_notif_redirect_dialog_positive_default)
                            modalDialog.current().method {
                                name = "show"
                            }.call()
                            this.result = null
                        } else {
                            if (tryGoToChannelSettings(miuiNotificationMenuRow3, true)) {
                                this.result = null
                            }
                        }
                    }
                }
            }
        }
    }

    fun tryGoToChannelSettings(miuiNotificationMenuRow: Any, channelSettings: Boolean): Boolean {
        val expandedNotification = miuiNotificationMenuRow.current().field {
            name = "mSbn"
        }.any() ?: return false
        val statusBarNotification =
            expandedNotification as StatusBarNotification
        val context = miuiNotificationMenuRow.current().field {
            name = "mContext"
        }.any() as? Context ?: return false
        val isHybrid =
            "com.android.systemui.statusbar.notification.NotificationUtil".toClass()
                .method {
                    name = "isHybrid"
                    modifiers { isStatic }
                }.get().boolean(expandedNotification)
        if (isHybrid) return false
        var intent = Intent(Intent.ACTION_MAIN)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (channelSettings) {
            intent
                .setClassName("com.android.settings", "com.android.settings.SubSettings")
                .putExtra(Settings.EXTRA_APP_PACKAGE, statusBarNotification.packageName)
                .putExtra("uid", statusBarNotification.uid)
                .putExtra(Settings.EXTRA_CHANNEL_ID, statusBarNotification.notification.channelId)
                .putExtra(Settings.EXTRA_CONVERSATION_ID, statusBarNotification.notification.shortcutId)
                .putExtra(
                    ":android:show_fragment",
                    "com.android.settings.notification.ChannelNotificationSettings"
                )
        } else {
            val bundle = Bundle()
            bundle.putString(Settings.EXTRA_APP_PACKAGE, statusBarNotification.packageName)
            bundle.putInt("uid", statusBarNotification.uid)
            intent
                .setClassName("com.android.settings", "com.android.settings.Settings\$AppNotificationSettingsActivity")
                .putExtra(Settings.EXTRA_APP_PACKAGE, statusBarNotification.packageName)
                .putExtra("uid", statusBarNotification.uid)
                .putExtra(
                    ":android:show_fragment",
                    bundle
                )
        }
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
        }.get().call("com.android.systemui.statusbar.notification.modal.ModalController".toClass()) ?: return false
        modalController.current().method {
            name = "animExitModal"
            paramCount = 4
        }.call(50L, true, "MORE", false)
        val commandQueue = "com.android.systemui.Dependency".toClass().method {
            name = "get"
            param(JavaClass)
            modifiers { isStatic }
        }.get().call("com.android.systemui.statusbar.CommandQueue".toClass()) ?: return false
        commandQueue.current().method {
            name = "animateCollapsePanels"
            param(IntType, BooleanType)
            paramCount = 2
        }.call(0, false)
        return true
    }
}