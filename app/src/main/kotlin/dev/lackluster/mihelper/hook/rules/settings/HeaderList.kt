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
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.UserHandle
import android.view.View
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.getResID

object HeaderList : YukiBaseHooker() {
    private const val XIAOMI_HELPER_IDENTIFIER = 9641L
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
            2 -> -1
            else -> R.string.module_settings_name_helper
        }
    }
    private val entryNameCustom = Prefs.getString(Pref.Key.Module.SETTINGS_NAME_CUSTOM, "Hyper Helper")
    private val showGoogleSettings = Prefs.getBoolean(Pref.Key.Settings.SHOE_GOOGLE, false)

    private var idMyDevice: Int = 0

    private val clzHeaderViewHolder by lazy {
        $$"com.android.settings.MiuiSettings$HeaderViewHolder".toClassOrNull()
    }
    private val clzHeader by lazy {
        $$"com.android.settingslib.miuisettings.preference.PreferenceActivity$Header".toClassOrNull()
    }
    private val fldHeaderId by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "id"
        }
    }
    private val fldHeaderIntent by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "intent"
        }
    }
    private val fldHeaderTitle by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "title"
        }
    }
    private val fldHeaderIconRes by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "iconRes"
        }
    }
    private val fldHeaderExtras by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "extras"
        }
    }


    override fun onHook() {
        if (showSettingsEntry) {
            val fldHolderIcon = clzHeaderViewHolder?.resolve()?.firstFieldOrNull {
                name = "icon"
            }
            val metCreateBitmap = "com.android.settings.Utils".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "createBitmap"
                parameterCount = 3
                modifiers(Modifiers.STATIC)
            }
            $$"com.android.settings.MiuiSettings$HeaderAdapter".toClass().resolve().firstMethodOrNull {
                name = "setIcon"
            }?.hook {
                before {
                    val headerViewHolder = this.args(0).any() ?: return@before
                    val header = this.args(1).any() ?: return@before
                    val identifier = fldHeaderId?.copy()?.of(header)?.get<Long>()
                    if (identifier == XIAOMI_HELPER_IDENTIFIER) {
                        fldHolderIcon?.copy()?.of(headerViewHolder)?.get<ImageView>()?.let { icon ->
                            if (icon.visibility != View.GONE) {
                                try {
                                    icon.visibility = View.VISIBLE
                                    val moduleIcon = Icon.createWithResource(BuildConfig.APPLICATION_ID, iconDrawable).loadDrawable(icon.context)
                                    val headerIconPixelSize = icon.resources.getDimensionPixelSize(
                                        icon.context.getResID("header_icon_size", "dimen", "com.android.settings")
                                    )
                                    icon.setImageBitmap(
                                        metCreateBitmap?.copy()?.invoke<Bitmap>(moduleIcon, headerIconPixelSize, headerIconPixelSize)
                                    )
                                } catch (_: Throwable) {
                                    icon.visibility = View.INVISIBLE
                                }
                            }
                            this.result = null
                        }
                    }
                }
            }
        }
        if (showSettingsEntry || showGoogleSettings) {
            val ctorHeader = clzHeader?.resolve()?.firstConstructor {
                parameterCount = 0
            }
            val ctorUserHandle = UserHandle::class.java.resolve().firstConstructor {
                parameterCount = 1
                parameters(Int::class)
            }
            "com.android.settings.MiuiSettings".toClassOrNull()?.apply {
                val metAddGoogleSettingsHeaders = resolve().firstMethodOrNull {
                    name = "AddGoogleSettingsHeaders"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "updateHeaderList"
                    parameters("java.util.List")
                }?.hook {
                    after {
                        val headerList = this.args(0).list<Any?>() as MutableList<Any?>
                        if (showSettingsEntry) {
                            val activity = this.instance<Activity>()
                            val moduleRes = activity.packageManager.getResourcesForApplication(BuildConfig.APPLICATION_ID)
                            if (idMyDevice == 0) {
                                idMyDevice = activity.getResID("my_device", "id", "com.android.settings")
                            }
                            val header = ctorHeader?.copy()?.create()
                            fldHeaderId?.copy()?.of(header)?.set(XIAOMI_HELPER_IDENTIFIER)
                            fldHeaderIntent?.copy()?.of(header)?.set(
                                Intent().apply {
                                    putExtra("isDisplayHomeAsUpEnabled", true)
                                    setClassName(BuildConfig.APPLICATION_ID, MainActivity::class.java.canonicalName!!)
                                }
                            )
                            fldHeaderTitle?.copy()?.of(header)?.set(
                                if (entryName == -1) entryNameCustom else moduleRes.getString(entryName)
                            )
                            fldHeaderIconRes?.copy()?.of(header)?.set(iconDrawable)
                            fldHeaderExtras?.copy()?.of(header)?.set(
                                Bundle().apply {
                                    val users = arrayListOf(
                                        ctorUserHandle.copy().createAsType<UserHandle>(0)
                                    )
                                    putParcelableArrayList("header_user", users)
                                }
                            )

                            var added = false
                            for ((index, head) in headerList.withIndex()) {
                                val identifier = fldHeaderId?.copy()?.of(head)?.get<Long>()?.toInt()
                                if (identifier == idMyDevice) {
                                    headerList.add(index + 1, header)
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
                        if (showGoogleSettings && !Device.isGlobal) {
                            metAddGoogleSettingsHeaders?.copy()?.of(this.instance)?.invoke(headerList)
                        }
                    }
                }
            }
        }
    }
}