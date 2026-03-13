/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references WOMMO <https://github.com/YifePlayte/WOMMO/blob/9e3c50a36452ae2bd3e3aa47924579ad02c764bf/app/src/main/java/com/yifeplayte/wommo/hook/hooks/singlepackage/getapps/DisableMarketCustomizeIcon.kt>
 * Copyright (C) 2026 YifePlayte

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

package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisableCustomizeIcon : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.DISABLE_CUSTOMIZE_ICON) {
            "com.xiaomi.market.customize_icon.CustomizeIconDataEditor".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSystemSupportCustomizeIcon"
                }?.hook {
                    replaceToFalse()
                }
            }
            $$"com.xiaomi.market.customize_icon.CustomizeIconDataEditor$Companion".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isSystemSupportCustomizeIcon"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}