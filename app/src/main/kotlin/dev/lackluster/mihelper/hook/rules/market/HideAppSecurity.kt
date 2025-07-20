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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideAppSecurity : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.HIDE_APP_SECURITY) {
            "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView".toClassOrNull()?.apply {
                constructor().hookAll {
                    after {
                        (this.instance as? View)?.visibility = View.GONE
                    }
                }
                method {
                    name = "checkShown"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "checkSettingSwitch"
                }.hook {
                    replaceToFalse()
                }
            }
            "com.xiaomi.market.util.SettingsUtils".toClassOrNull()?.apply {
                method {
                    name = "isSupportAppSecurityCheck"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            "com.xiaomi.market.common.analytics.onetrack.ExperimentManager\$Companion".toClassOrNull()?.apply {
                method {
                    name = "isMineAppSecurityCheckOpen"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
        }
    }
}