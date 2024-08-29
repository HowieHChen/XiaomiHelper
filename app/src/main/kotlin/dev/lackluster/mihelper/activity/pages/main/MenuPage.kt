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
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.hyperx.app.AlertDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.utils.ShellUtils

@BMMenuPage
class MenuPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.page_menu)
    }
    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_menu_reboot),
            CategoryData(hideLine = true)
        ) {
            TextPreference(
                DescData(titleId = R.string.menu_reboot_system),
                TextData(),
                onClickListener = {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_warning)
                        .setMessage(R.string.menu_reboot_system_tips)
                        .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.button_ok) { dialog, _ ->
                            try {
                                ShellUtils.tryExec("/system/bin/sync;/system/bin/svc power reboot || reboot", useRoot = true, checkSuccess = true)
                                dialog.dismiss()
                            } catch (tout : Throwable) {
                                makeText(
                                    activity,
                                    tout.message,
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        }
                        .show()
                }
            )
            TextPreference(
                DescData(titleId = R.string.menu_reboot_scope),
                TextData(),
                onClickListener = {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_warning)
                        .setMessage(R.string.menu_reboot_scope_tips)
                        .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.button_ok) { dialog, _ ->
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
                                dialog.dismiss()
                            } catch (tout : Throwable) {
                                makeText(
                                    activity,
                                    tout.message,
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        }
                        .show()
                }
            )
            TextPreference(
                DescData(titleId = R.string.menu_reboot_systemui),
                TextData(),
                onClickListener = {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_warning)
                        .setMessage(R.string.menu_reboot_systemui_tips)
                        .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.button_ok) { dialog, _ ->
                            try {
                                ShellUtils.tryExec("killall com.android.systemui", useRoot = true, checkSuccess = true)
                                makeText(
                                    activity,
                                    getString(R.string.menu_reboot_done_toast),
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            } catch (tout : Throwable) {
                                makeText(
                                    activity,
                                    tout.message,
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        }
                        .show()
                }
            )
            TextPreference(
                DescData(titleId = R.string.menu_reboot_launcher),
                TextData(),
                onClickListener = {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_warning)
                        .setMessage(R.string.menu_reboot_launcher_tips)
                        .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.button_ok) { dialog, _ ->
                            try {
                                ShellUtils.tryExec("killall com.miui.home", useRoot = true, checkSuccess = true)
                                makeText(
                                    activity,
                                    getString(R.string.menu_reboot_done_toast),
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            } catch (tout : Throwable) {
                                makeText(
                                    activity,
                                    tout.message,
                                    LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        }
                        .show()
                }
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_menu_others),
            CategoryData()
        ) {
            TextPreference(
                DescData(titleId = R.string.menu_shortcut_lsposed),
                TextData(),
                onClickListener = {
                    try {
                        ShellUtils.tryExec(
                            "am start -a android.intent.action.MAIN -n com.android.shell/.BugreportWarningActivity -c org.lsposed.manager.LAUNCH_MANAGER -f 0x10000000",
                            useRoot = true,
                            checkSuccess = true
                        )
                    }catch (tout : Throwable) {
                        makeText(
                            activity,
                            tout.message,
                            LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}