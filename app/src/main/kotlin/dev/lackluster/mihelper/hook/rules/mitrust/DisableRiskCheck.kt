/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/trustservice/DisableMrm.java>
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

package dev.lackluster.mihelper.hook.rules.mitrust

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableRiskCheck : YukiBaseHooker() {
    private val metInitMrmService by lazy {
        DexKit.findMethodWithCache("init_mrm") {
            matcher {
                returnType = "boolean"
                addUsingString("MiTrustService/statusEventHandle", StringMatchType.Equals)
                addUsingString("vendor.xiaomi.hardware.mrm.IMrm/default", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiTrust.DISABLE_RISK_CHECK) {
            if (appClassLoader == null) return@hasEnable
            metInitMrmService?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
        }
    }
}