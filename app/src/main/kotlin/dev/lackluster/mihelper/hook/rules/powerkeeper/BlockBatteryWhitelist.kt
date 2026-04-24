/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/powerkeeper/PreventBatteryWitelist.java>
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

package dev.lackluster.mihelper.hook.rules.powerkeeper

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object BlockBatteryWhitelist : StaticHooker() {
    private val whitelist by lazy {
        DexKit.findMethodWithCache("power_save_whitelist") {
            matcher {
                addUsingString("addPowerSaveWhitelistApps", StringMatchType.StartsWith)
            }
        }
    }

    override fun onInit() {
        Preferences.PowerKeeper.BLOCK_BATTERY_WHITELIST.get().also {
            updateSelfState(it)
        }.ifTrue {
            whitelist
        }
    }

    override fun onHook() {
        whitelist?.getMethodInstance(classLoader)?.hook {
            val strArr = getArg(0) as? Array<*>
            if (strArr != null && strArr.size > 1) {
                result(null)
            } else {
                result(proceed())
            }
        }
    }
}