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

package dev.lackluster.mihelper.hook.rules.miuihome.widget

import android.appwidget.AppWidgetProviderInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object WidgetResizable : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.WIDGET_RESIZABLE) {
            "android.appwidget.AppWidgetHostView".toClass(null).method {
                name = "getAppWidgetInfo"
                returnType = "android.appwidget.AppWidgetProviderInfo"
            }.giveAll().hookAll {
                after {
                    val widgetInfo = (this.result ?:return@after) as AppWidgetProviderInfo
                    widgetInfo.resizeMode = AppWidgetProviderInfo.RESIZE_VERTICAL or AppWidgetProviderInfo.RESIZE_HORIZONTAL
                    widgetInfo.minHeight = 0
                    widgetInfo.minWidth = 0
                    widgetInfo.minResizeHeight = 0
                    widgetInfo.minResizeWidth = 0
                    this.result = widgetInfo
                }
            }
        }
    }
}