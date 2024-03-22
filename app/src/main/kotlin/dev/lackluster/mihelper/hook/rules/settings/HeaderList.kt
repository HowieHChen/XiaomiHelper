/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/systemsettings/HyperCeilerSettings.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object HeaderList : YukiBaseHooker() {
    private val showSettingsEntry = Prefs.getBoolean(Pref.Key.Module.SHOW_IN_SETTINGS, false)
    private val iconStyle = Prefs.getInt(Pref.Key.Module.SETTINGS_ICON_STYLE, 0)
    private val iconColor = Prefs.getInt(Pref.Key.Module.SETTINGS_ICON_COLOR, 0)
    private val iconDrawable by lazy {
        when (iconStyle * 6 + iconColor) {
            0 -> R.drawable.ic_header_hyper_helper_gray
            1 -> R.drawable.ic_header_hyper_helper_red
            2 -> R.drawable.ic_header_hyper_helper_green
            3 -> R.drawable.ic_header_hyper_helper_blue
            4 -> R.drawable.ic_header_hyper_helper_purple
            5 -> R.drawable.ic_header_hyper_helper_yellow
            6 -> R.drawable.ic_header_android_gray
            7 -> R.drawable.ic_header_android_red
            8 -> R.drawable.ic_header_android_green
            9 -> R.drawable.ic_header_android_blue
            10 -> R.drawable.ic_header_android_purple
            11 -> R.drawable.ic_header_android_yellow
            else -> R.drawable.ic_header_hyper_helper_gray
        }
    }
    private val entryName by lazy {
        when (Prefs.getInt(Pref.Key.Module.SETTINGS_NAME, 0)) {
            0 -> R.string.module_settings_name_helper
            1 -> R.string.module_settings_name_advanced
            else -> R.string.module_settings_name_helper
        }
    }
    private val showGoogleSettings = Prefs.getBoolean(Pref.Key.Settings.SHOE_GOOGLE, false)
    private val isGlobal by lazy {
        "miui.os.Build".toClass().field {
            name = "IS_GLOBAL_BUILD"
        }.get().boolean()
    }
    override fun onHook() {
        if (showSettingsEntry || showGoogleSettings) {
            val preferenceHeaderClz = "com.android.settingslib.miuisettings.preference.PreferenceActivity\$Header".toClassOrNull() ?: return
            val miuiSettingsClz = "com.android.settings.MiuiSettings".toClassOrNull() ?: return
            miuiSettingsClz.method {
                name = "updateHeaderList"
                param(ListClass)
            }.hook {
                after {
                    val headerList = this.args(0).any() as MutableList<Any?>
                    if (showSettingsEntry) {
                        val activity = this.instance as Activity
                        activity.injectModuleAppResources()
                        val intent = Intent()
                        intent.putExtra("isDisplayHomeAsUpEnabled", true)
                        intent.setClassName(BuildConfig.APPLICATION_ID, MainActivity::class.java.canonicalName!!)
                        val header = preferenceHeaderClz.constructor().get().call()
                        header?.current()?.field { name = "id" }?.set(6666L)
                        header?.current()?.field { name = "intent" }?.set(intent)
                        header?.current()?.field { name = "title" }?.set(activity.getString(entryName))
                        header?.current()?.field { name = "iconRes" }?.set(iconDrawable)
                        val bundle = Bundle()
                        val users = arrayListOf<UserHandle>(
                            UserHandle::class.java.constructor().get().call(0) as UserHandle
                        )
                        bundle.putParcelableArrayList("header_user", users)
                        header?.current()?.field { name = "extras" }?.set(bundle)
                        var added = false
                        for ((position, head) in headerList.withIndex()) {
                            val index = head?.current()?.field { name = "id" }?.long()
                            if (index == -1L) {
                                headerList.add(position, header)
                                added = true
                                break
                            }
                        }
                        if (!added) {
                            if (headerList.size > 25) {
                                headerList.add(25, header)
                            } else {
                                headerList.add(header)
                            }
                        }
                    }
                    if (showGoogleSettings && !isGlobal) {
                        this.instance.current {
                            method {
                                name = "AddGoogleSettingsHeaders"
                            }.call(headerList)
                        }
                    }
                }
            }
        }
    }
}