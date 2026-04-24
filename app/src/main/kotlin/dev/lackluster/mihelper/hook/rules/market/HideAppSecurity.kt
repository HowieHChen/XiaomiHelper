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

package dev.lackluster.mihelper.hook.rules.market

import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object HideAppSecurity : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.Market.HIDE_APP_SECURITY.get())
    }

    override fun onHook() {
        "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView".toClassOrNull()?.apply {
            resolve().firstConstructor().hook {
                val ori = proceed()
                (thisObject as? View)?.visibility = View.GONE
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "checkShown"
            }?.hook {
                result(false)
            }
            resolve().firstMethodOrNull {
                name = "checkSettingSwitch"
            }?.hook {
                result(false)
            }
        }
        "com.xiaomi.market.util.SettingsUtils".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isSupportAppSecurityCheck"
            }?.hook {
                result(false)
            }
        }
        $$"com.xiaomi.market.common.analytics.onetrack.ExperimentManager$Companion".toClassOrNull()?.apply {
            resolve().optional().firstMethodOrNull {
                name = "isMineAppSecurityCheckOpen"
            }?.hook {
                result(false)
            }
        }
    }
}