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

package dev.lackluster.mihelper.activity.pages.main

import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.annotation.BMMenuPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.utils.ShellUtils

@BMMenuPage()
class MenuPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_menu)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_menu_reboot)
        TextSummaryWithArrow(TextSummaryV(textId = R.string.menu_reboot_system, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.menu_reboot_system_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        ShellUtils.tryExec("/system/bin/sync;/system/bin/svc power reboot || reboot", useRoot = true, checkSuccess = true)
                        dismiss()
                    }
                    catch (tout : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_error_toast) + "(${tout.message})",
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.menu_reboot_scope, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.menu_reboot_scope_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        activity.resources.getStringArray(R.array.module_scope).forEach {
                            try {
                                if (it != "android") ShellUtils.tryExec("killall -q $it", useRoot = true, checkSuccess = true)
                            } catch (t: Throwable) {
                                if (t.message?.contains("No such process") == false) {
                                    throw t
                                }
                            }
                        }
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (tout : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_error_toast) + "(${tout.message})",
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.menu_reboot_systemui, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.menu_reboot_systemui_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        ShellUtils.tryExec("killall com.android.systemui", useRoot = true, checkSuccess = true)
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (tout : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_error_toast) + "(${tout.message})",
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.menu_reboot_launcher, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.dialog_warning)
                setMessage(R.string.menu_reboot_launcher_tips)
                setLButton(R.string.button_cancel) {
                    dismiss()
                }
                setRButton(R.string.button_ok) {
                    try {
                        ShellUtils.tryExec("killall com.miui.home", useRoot = true, checkSuccess = true)
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_done_toast),
                            LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    catch (tout : Throwable) {
                        makeText(
                            activity,
                            getString(R.string.menu_reboot_error_toast) + "(${tout.message})",
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