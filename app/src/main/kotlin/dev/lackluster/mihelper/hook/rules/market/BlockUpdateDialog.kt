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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object BlockUpdateDialog : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.Market.BLOCK_UPDATE_DIALOG.get())
    }

    override fun onHook() {
        "com.xiaomi.market.ui.UpdateListFragment".toClassOrNull()?.apply {
            resolve().optional().firstMethodOrNull {
                name = "tryShowDialog"
            }?.hook {
                result(null)
            }
        }
        "com.xiaomi.market.ui.update.UpdatePushDialogManager".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "tryShowDialog"
            }?.hook {
                result(null)
            }
        }
        "com.xiaomi.market.ui.UpdateListRvAdapter".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "shouldAddAutoUpdateItem"
            }?.hook {
                result(false)
            }
        }
    }
}