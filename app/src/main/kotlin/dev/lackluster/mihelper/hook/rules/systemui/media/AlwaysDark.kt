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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.utils.Prefs

object AlwaysDark : YukiBaseHooker() {
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val ncAlwaysDark = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ALWAYS_DARK, false)

    override fun onHook() {
        if (ncBackgroundStyle == 0 && ncAlwaysDark) {
            clzMiuiMediaViewControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.self?.apply {
                    isAccessible = true
                }
                val fldMediaFullAodListener = resolve().firstFieldOrNull {
                    name = "mediaFullAodListener"
                }?.self
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.self
                val fldListeners = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mListeners"
                    }?.self
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.self
                resolve().firstConstructor().hook {
                    after {
                        val context = fldContext?.get(this.instance) as? Context ?: return@after
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        fldContext.set(this.instance, wrappedContext)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val mediaFullAodListener = fldMediaFullAodListener?.get(this.instance) ?: return@after
                        val fullAodControllerLazy = fldFullAodController?.get(this.instance)
                        val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        fullAodController?.let { it1 -> fldListeners?.get(it1) as? MutableList<*> }?.remove(mediaFullAodListener)
                    }
                }
            }
        }
    }
}