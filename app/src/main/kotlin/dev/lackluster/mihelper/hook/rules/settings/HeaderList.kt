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
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.MainActivity
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.getResId

object HeaderList : StaticHooker() {
    private const val XIAOMI_HELPER_IDENTIFIER = 9641L

    private val showSettingsEntry by Preferences.Module.SHOW_IN_SETTINGS.lazyGet()
    private val entryNameResId by lazy {
        when (Preferences.Module.SETTINGS_NAME.get()) {
            0 -> R.string.module_settings_name_helper
            1 -> R.string.module_settings_name_advanced
            2 -> -1
            else -> R.string.module_settings_name_helper
        }
    }
    private val entryNameCustom by Preferences.Module.CUSTOM_SETTINGS_NAME.lazyGet()
    private val showGoogleSettings by Preferences.Settings.SHOW_GOOGLE_ENTRY.lazyGet()
    private val iconDrawableResId by lazy {
        if (Preferences.Module.SETTINGS_ICON_STYLE.get() == 1) {
            when (Preferences.Module.SETTINGS_ICON_COLOR.get()) {
                0 -> R.drawable.ic_header_android_gray
                1 -> R.drawable.ic_header_android_red
                2 -> R.drawable.ic_header_android_green
                3 -> R.drawable.ic_header_android_blue
                4 -> R.drawable.ic_header_android_purple
                5 -> R.drawable.ic_header_android_yellow
                else -> R.drawable.ic_header_android_green
            }
        } else {
            when (Preferences.Module.SETTINGS_ICON_COLOR.get()) {
                0 -> R.drawable.ic_header_hyper_helper_gray
                1 -> R.drawable.ic_header_hyper_helper_red
                2 -> R.drawable.ic_header_hyper_helper_green
                3 -> R.drawable.ic_header_hyper_helper_blue
                4 -> R.drawable.ic_header_hyper_helper_purple
                5 -> R.drawable.ic_header_hyper_helper_yellow
                else -> R.drawable.ic_header_hyper_helper_gray
            }
        }
    }

    private val clzHeaderViewHolder by $$"com.android.settings.MiuiSettings$HeaderViewHolder".lazyClassOrNull()
    private val clzHeader by $$"com.android.settingslib.miuisettings.preference.PreferenceActivity$Header".lazyClassOrNull()
    private val fldHeaderId by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "id"
        }?.toTyped<Long>()
    }
    private val fldHeaderIntent by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "intent"
        }?.toTyped<Intent>()
    }
    private val fldHeaderTitle by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "title"
        }?.toTyped<String>()
    }
    private val fldHeaderIconRes by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "iconRes"
        }?.toTyped<Int>()
    }
    private val fldHeaderExtras by lazy {
        clzHeader?.resolve()?.firstFieldOrNull {
            name = "extras"
        }?.toTyped<Bundle>()
    }

    override fun onInit() {
        updateSelfState(showSettingsEntry || showGoogleSettings)
    }

    override fun onHook() {
        if (showSettingsEntry) {
            val fldHolderIcon = clzHeaderViewHolder?.resolve()?.firstFieldOrNull {
                name = "icon"
            }?.toTyped<ImageView>()
            val metCreateBitmap = "com.android.settings.Utils".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "createBitmap"
                parameterCount = 3
                modifiers(Modifiers.STATIC)
            }?.toTyped<Bitmap>()
            var headerIconSizeResId = 0
            $$"com.android.settings.MiuiSettings$HeaderAdapter".toClass().resolve().firstMethodOrNull {
                name = "setIcon"
            }?.hook {
                val headerViewHolder = getArg(0)
                val header = getArg(1)
                val identifier = fldHeaderId?.get(header)
                if (identifier == XIAOMI_HELPER_IDENTIFIER) {
                    val icon = fldHolderIcon?.get(headerViewHolder)
                    if (icon != null && icon.visibility != View.GONE) {
                        try {
                            icon.visibility = View.VISIBLE
                            val moduleIcon = Icon.createWithResource(BuildConfig.APPLICATION_ID, iconDrawableResId).loadDrawable(icon.context)
                            if (headerIconSizeResId == 0) {
                                headerIconSizeResId = icon.context.getResId("header_icon_size", "dimen", Scope.SETTINGS)
                            }
                            val headerIconPixelSize = icon.resources.getDimensionPixelSize(headerIconSizeResId)
                            icon.setImageBitmap(
                                metCreateBitmap?.invoke(null, moduleIcon, headerIconPixelSize, headerIconPixelSize)
                            )
                        } catch (t: Throwable) {
                            e(t) { "Failed to set icon" }
                            icon.visibility = View.INVISIBLE
                        }
                    }
                    return@hook result(null)
                }
                result(proceed())
            }
        }
        if (showSettingsEntry || showGoogleSettings) {
            val ctorHeader = clzHeader?.resolve()?.firstConstructor {
                parameterCount = 0
            }?.toTyped()
            val ctorUserHandle = UserHandle::class.java.resolve().firstConstructor {
                parameterCount = 1
                parameters(Int::class)
            }.toTyped()
            var idMyDevice = 0
            "com.android.settings.MiuiSettings".toClassOrNull()?.apply {
                val metAddGoogleSettingsHeaders = resolve().firstMethodOrNull {
                    name = "AddGoogleSettingsHeaders"
                    superclass()
                }?.toTyped<Unit>()
                resolve().firstMethodOrNull {
                    name = "updateHeaderList"
                    parameters("java.util.List")
                }?.hook {
                    val ori = proceed()
                    @Suppress("UNCHECKED_CAST")
                    val headerList = getArg(0) as? MutableList<Any?> ?: return@hook result(ori)
                    if (showSettingsEntry) {
                        val activity = thisObject as? Activity ?: return@hook result(ori)
                        val moduleRes = activity.packageManager.getResourcesForApplication(BuildConfig.APPLICATION_ID)
                        if (idMyDevice == 0) {
                            idMyDevice = activity.getResId("wifi_settings", "id", Scope.SETTINGS)
                        }
                        val header = ctorHeader?.newInstance()
                        fldHeaderId?.set(header, XIAOMI_HELPER_IDENTIFIER)
                        fldHeaderIntent?.set(header,
                            Intent().apply {
                                putExtra("isDisplayHomeAsUpEnabled", true)
                                setClassName(BuildConfig.APPLICATION_ID, MainActivity::class.java.canonicalName!!)
                            }
                        )
                        fldHeaderTitle?.set(header,
                            if (entryNameResId == -1) entryNameCustom else moduleRes.getString(entryNameResId)
                        )
                        fldHeaderIconRes?.set(header, iconDrawableResId)
                        fldHeaderExtras?.set(header,
                            Bundle().apply {
                                val users = arrayListOf(ctorUserHandle.newInstance(0))
                                putParcelableArrayList("header_user", users)
                            }
                        )

                        val targetIndex = headerList.indexOfFirst { head ->
                            fldHeaderId?.get(head)?.toInt() == idMyDevice
                        }

                        if (targetIndex != -1) {
                            headerList.add(targetIndex, header)
                        } else {
                            if (headerList.size > 25) {
                                headerList.add(25, header)
                            } else {
                                headerList.add(header)
                            }
                        }
                    }
                    if (showGoogleSettings && !Device.isGlobal) {
                        metAddGoogleSettingsHeaders?.invoke(thisObject, headerList)
                    }
                    result(ori)
                }
            }
        }
    }
}