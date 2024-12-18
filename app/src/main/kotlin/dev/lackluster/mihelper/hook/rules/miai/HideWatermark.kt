/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/voiceassist/DisableChatWatermark.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 * Add more hooks, modified by HowieHChen (howie.dev@outlook.com) on 03/20/2024

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

package dev.lackluster.mihelper.hook.rules.miai

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideWatermark : YukiBaseHooker(){
    private val addWatermark by lazy {
        DexKit.findMethodWithCache("add_watermark") {
            matcher {
                addUsingString("add watermark", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiAi.HIDE_WATERMARK) {
            if (appClassLoader == null) return@hasEnable
            addWatermark?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
        }
    }
}