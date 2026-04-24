/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.content.res.Configuration
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object AlwaysDark : StaticHooker() {
    private val ncBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(false).lazyGet()
    private val ncAlwaysDark by Preferences.SystemUI.MediaControl.NotifCenter.BG_ALWAYS_DARK.lazyGet()

    override fun onInit() {
        updateSelfState(ncBackgroundStyle == 0 && ncAlwaysDark)
    }

    override fun onHook() {
        clzMiuiMediaViewControllerImpl?.apply {
            val fldContext = resolve().firstFieldOrNull {
                name = "context"
            }?.toTyped<Context>()
            val fldMediaFullAodListener = resolve().firstFieldOrNull {
                name = "mediaFullAodListener"
            }?.toTyped<Any>()
            val fldFullAodController = resolve().firstFieldOrNull {
                name = "fullAodController"
            }?.toTyped<Any>()
            val fldListeners = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                ?.resolve()?.firstFieldOrNull {
                    name = "mListeners"
                }?.toTyped<ArrayList<*>>()
            val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "get"
            }?.toTyped<Any>()
            resolve().firstConstructor().hook {
                val ori = proceed()
                val context = fldContext?.get(thisObject)
                if (context != null) {
                    val oriConfiguration = context.resources.configuration
                    val configuration = Configuration(oriConfiguration).apply {
                        uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                    }
                    val wrappedContext = context.createConfigurationContext(configuration)
                    fldContext.set(thisObject, wrappedContext)
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "attach"
            }?.hook {
                val ori = proceed()
                val mediaFullAodListener = fldMediaFullAodListener?.get(thisObject)
                val fullAodControllerLazy = fldFullAodController?.get(thisObject)
                val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                if (mediaFullAodListener != null && fullAodController != null) {
                    fldListeners?.get(fullAodController)?.remove(mediaFullAodListener)
                }
                result(ori)
            }
        }
    }
}