/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/thememanager/DisableThemeAdNew.kt>
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

package dev.lackluster.mihelper.hook.rules.themes

import android.view.View
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Themes.AD_BLOCKER) {
            val drmClz = "miui.drm.DrmManager".toClassOrNull()
            runCatching {
                drmClz?.method {
                    name = "isSupportAd"
                }?.hookAll {
                    replaceToFalse()
                }
            }
            runCatching {
                drmClz?.method {
                    name = "setSupportAd"
                }?.hookAll {
                    intercept()
                }
            }
            "com.android.thememanager.basemodule.ad.model.AdInfoResponse".toClassOrNull()?.method {
                name = "isAdValid"
            }?.hook {
                replaceToFalse()
            }
            "com.android.thememanager.recommend.view.listview.viewholder.SelfFontItemAdViewHolder".toClassOrNull()?.constructor {
                paramCount = 2
            }?.hook {
                after {
                    val view = this.args(0).any() as? View ?: return@after
                    val params = FrameLayout.LayoutParams(0, 0)
                    view.layoutParams = params
                    view.visibility = View.GONE
                }
            }
            "com.android.thememanager.recommend.view.listview.viewholder.SelfRingtoneItemAdViewHolder".toClassOrNull()?.constructor {
                paramCount = 2
            }?.hook {
                after {
                    val view = this.args(0).any() as? View ?: return@after
                    val params = FrameLayout.LayoutParams(0, 0)
                    view.layoutParams = params
                    view.visibility = View.GONE
                }
            }
        }
    }
}