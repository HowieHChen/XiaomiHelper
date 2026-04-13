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
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_REAL_FILE_NAME
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.utils.SystemProperties
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object CommonClassUtils : StaticHooker() {
    val clzMiuiClock by "com.android.systemui.statusbar.views.MiuiClock".lazyClassOrNull()
    val clzMiuiKeyguardStatusBarView by "com.android.systemui.statusbar.phone.MiuiKeyguardStatusBarView".lazyClassOrNull()
    val clzMiuiBatteryMeterView by "com.android.systemui.statusbar.views.MiuiBatteryMeterView".lazyClassOrNull()
    val clzStatusBarIconControllerImpl by "com.android.systemui.statusbar.phone.ui.StatusBarIconControllerImpl".lazyClassOrNull()
    val clzJavaAdapterKt by "com.android.systemui.util.kotlin.JavaAdapterKt".lazyClassOrNull()

    val clzReadonlyStateFlow by "kotlinx.coroutines.flow.ReadonlyStateFlow".lazyClassOrNull()
    val clzStateFlowKt by "kotlinx.coroutines.flow.StateFlowKt".lazyClassOrNull()
    val clzMutableStateFlow by "kotlinx.coroutines.flow.MutableStateFlow".lazyClassOrNull()
    val clzStateFlowImpl by "kotlinx.coroutines.flow.StateFlowImpl".lazyClassOrNull()
    val clzMainDispatcherLoader by "kotlinx.coroutines.internal.MainDispatcherLoader".lazyClassOrNull()
    val clzCoroutineScope by "kotlinx.coroutines.CoroutineScope".lazyClassOrNull()
    val clzEmptyCoroutineContext by "kotlin.coroutines.EmptyCoroutineContext".lazyClassOrNull()
    val clzPair by "kotlin.Pair".lazyClass()
    val clzTriple by "kotlin.Triple".lazyClass()
    val clzJob by "kotlinx.coroutines.Job".lazyClassOrNull()

    val clzMiuiMediaViewControllerImpl by "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl".lazyClassOrNull()
    val clzMiuiMediaViewHolder by "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewHolder".lazyClassOrNull()
    val clzMiuiMediaNotificationControllerImpl by "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaNotificationControllerImpl".lazyClassOrNull()
    val clzMiuiIslandMediaViewBinderImpl by "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewBinderImpl".lazyClassOrNull()
    val clzMiuiIslandMediaViewHolder by "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewHolder".lazyClassOrNull()

    val readonlyStateFlowFalse by lazy {
        MutableStateFlowCompat(false).toReadonlyStateFlow()
    }
    val readonlyStateFlowTrue by lazy {
        MutableStateFlowCompat(true).toReadonlyStateFlow()
    }
    val readonlyStateFlow0 by lazy {
        MutableStateFlowCompat(0).toReadonlyStateFlow()
    }

    private val prefFontPath by Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL.lazyGet()
    private val defFontPath by lazy {
        SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
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

    override fun onInit() {
        attach(ConstraintSetCompat, null)
        clzMiuiClock
        clzMiuiKeyguardStatusBarView
        clzMiuiBatteryMeterView
        clzStatusBarIconControllerImpl
        clzJavaAdapterKt

        clzReadonlyStateFlow
        clzStateFlowKt
        clzMutableStateFlow
        clzStateFlowImpl
        clzCoroutineScope
        clzEmptyCoroutineContext
        clzMainDispatcherLoader
        clzPair
        clzTriple
        clzJob

        clzMiuiMediaViewControllerImpl
        clzMiuiMediaViewHolder
        clzMiuiMediaNotificationControllerImpl
        clzMiuiIslandMediaViewBinderImpl
        clzMiuiIslandMediaViewHolder
    }

    fun getTypeface(weight: Int): Typeface {
        return typefaceCache.getOrPut(weight) {
            var typeface: Typeface? = null

            if (prefFontPath.isNotBlank() && prefFontPath != defFontPath) {
                runCatching {
                    module.openRemoteFile(VARIABLE_FONT_REAL_FILE_NAME).use { pfd ->
                        typeface = Typeface.Builder(pfd.fileDescriptor)
                            .setFontVariationSettings("'wght' $weight")
                            .build()
                    }
                }.onFailure {
                    e(it) { "Failed to load remote font for weight $weight" }
                }
            }

            typeface ?: runCatching {
                Typeface.Builder(defFontPath)
                    .setFontVariationSettings("'wght' $weight")
                    .build()
            }.getOrNull() ?: Typeface.DEFAULT_BOLD
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