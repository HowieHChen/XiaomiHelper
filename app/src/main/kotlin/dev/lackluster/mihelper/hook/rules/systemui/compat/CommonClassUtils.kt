/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.compat

import android.graphics.Typeface
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.SystemProperties
import java.io.File
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object CommonClassUtils : YukiBaseHooker() {
    val clzMiuiClock by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClassOrNull()
    }
    val clzMiuiKeyguardStatusBarView by lazy {
        "com.android.systemui.statusbar.phone.MiuiKeyguardStatusBarView".toClassOrNull()
    }
    val clzMiuiBatteryMeterView by lazy {
        "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClassOrNull()
    }
    val clzJavaAdapterKt by lazy {
        "com.android.systemui.util.kotlin.JavaAdapterKt".toClassOrNull()
    }

    val clzReadonlyStateFlow by lazy {
        "kotlinx.coroutines.flow.ReadonlyStateFlow".toClassOrNull()
    }
    val clzStateFlowKt by lazy {
        "kotlinx.coroutines.flow.StateFlowKt".toClassOrNull()
    }
    val clzMutableStateFlow by lazy {
        "kotlinx.coroutines.flow.MutableStateFlow".toClassOrNull()
    }
    val clzStateFlowImpl by lazy {
        "kotlinx.coroutines.flow.StateFlowImpl".toClassOrNull()
    }
    val clzMainDispatcherLoader by lazy {
        "kotlinx.coroutines.internal.MainDispatcherLoader".toClassOrNull()
    }
    val clzCoroutineScope by lazy {
        "kotlinx.coroutines.CoroutineScope".toClassOrNull()
    }
    val clzEmptyCoroutineContext by lazy {
        "kotlin.coroutines.EmptyCoroutineContext".toClassOrNull()
    }
    val clzPair by lazy {
        "kotlin.Pair".toClass()
    }
    val clzJob by lazy {
        "kotlinx.coroutines.Job".toClassOrNull()
    }
    val clzMiuiMediaViewControllerImpl by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl".toClassOrNull()
    }
    val clzMiuiMediaViewHolder by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewHolder".toClassOrNull()
    }
    val clzMiuiMediaNotificationControllerImpl by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaNotificationControllerImpl".toClassOrNull()
    }
    val clzMiuiIslandMediaViewBinderImpl by lazy {
        "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewBinderImpl".toClassOrNull()
    }
    val clzMiuiIslandMediaViewHolder by lazy {
        "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewHolder".toClassOrNull()
    }

    val readonlyStateFlowFalse by lazy {
        MutableStateFlowCompat(false).toReadonlyStateFlow()
    }
    val readonlyStateFlowTrue by lazy {
        MutableStateFlowCompat(true).toReadonlyStateFlow()
    }
    val readonlyStateFlow0 by lazy {
        MutableStateFlowCompat(0).toReadonlyStateFlow()
    }

    val fontPath by lazy {
        val defaultPath = SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
        val prefPath = Prefs.getString(FontWeight.FONT_PATH_INTERNAL, defaultPath) ?: defaultPath
        val fontFile = File(prefPath)
        if (fontFile.exists() && fontFile.isFile && fontFile.canRead()) prefPath else defaultPath
    }

    val typefaceCache by lazy {
        ConcurrentHashMap<Int, Typeface>()
    }

    private val ncMediaVHFieldCache by lazy {
        mutableMapOf<String, Field?>()
    }
    private val diMediaVHFieldCache by lazy {
        mutableMapOf<String, Field?>()
    }

    override fun onHook() {
        loadHooker(ConstraintSetCompat)
        clzMiuiClock
        clzMiuiKeyguardStatusBarView
        clzMiuiBatteryMeterView
        clzReadonlyStateFlow
        clzStateFlowKt
        clzMutableStateFlow
        clzStateFlowImpl
        clzJavaAdapterKt
        clzCoroutineScope
        clzEmptyCoroutineContext
        clzMainDispatcherLoader
        clzPair
        clzJob
        clzMiuiMediaViewControllerImpl
        clzMiuiMediaViewHolder
        clzMiuiMediaNotificationControllerImpl
        clzMiuiIslandMediaViewBinderImpl
        clzMiuiIslandMediaViewHolder
    }

    fun getTypeface(weight: Int): Typeface {
        return typefaceCache.getOrPut(weight) {
            Typeface.Builder(fontPath).setFontVariationSettings("'wght' $weight").build()
        }
    }

    fun getMediaViewHolderField(fieldName: String, isDynamicIsland: Boolean): Field? {
        return if (isDynamicIsland) {
            diMediaVHFieldCache.getOrPut(fieldName) {
                clzMiuiIslandMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = fieldName
                }?.self?.apply {
                    isAccessible = true
                }
            }
        } else {
            ncMediaVHFieldCache.getOrPut(fieldName) {
                clzMiuiMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = fieldName
                }?.self?.apply {
                    isAccessible = true
                }
            }
        }
    }
}