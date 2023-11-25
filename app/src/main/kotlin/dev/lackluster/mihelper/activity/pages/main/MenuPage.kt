package dev.lackluster.mihelper.activity.pages.main

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.annotation.BMMenuPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.utils.Shell

@BMMenuPage()
class MenuPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_menu)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_reboot)
        TextSummaryWithArrow(TextSummaryV(textId = R.string.reboot_system, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.reboot_system_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        Shell.exec("/system/bin/sync;/system/bin/svc power reboot || reboot", true)
                        dismiss()
                    }
                    catch (_ : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.reboot_error_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.reboot_scope, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.reboot_scope_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        activity.resources.getStringArray(R.array.module_scope).forEach {
                            if (it != "android") Shell.exec("killall $it", true)
                        }
                        makeText(
                            activity,
                            getString(R.string.reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (_ : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.reboot_error_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.reboot_systemui, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.reboot_systemui_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        Shell.exec("killall com.android.systemui", true)
                        makeText(
                            activity,
                            getString(R.string.reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (_ : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.reboot_error_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.reboot_launcher, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.reboot_launcher_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        Shell.exec("killall com.miui.home", true)
                        makeText(
                            activity,
                            getString(R.string.reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (_ : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.reboot_error_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        // TitleText(textId = R.string.ui_title_backup)
    }
}