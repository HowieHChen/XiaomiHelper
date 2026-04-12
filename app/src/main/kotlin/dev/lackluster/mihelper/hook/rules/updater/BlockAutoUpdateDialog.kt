/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/library/hook/src/main/java/com/sevtinge/hyperceiler/hook/module/rules/updater/AutoUpdateDialog.kt>
 * Copyright (C) 2023-2025 HyperCeiler Contributions

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

package dev.lackluster.mihelper.hook.rules.updater

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object BlockAutoUpdateDialog : StaticHooker() {
    private val metShowAutoSetDialog by lazy {
        DexKit.findMethodWithCache("show_auto_set") {
            matcher {
                paramCount = 2
                paramTypes("boolean", "boolean")
                addUsingString("XBaseDownloadActivity", StringMatchType.Equals)
                addUsingString("showAutoSetDialog", StringMatchType.StartsWith)
            }
        }
    }

    override fun onInit() {
        Preferences.Updater.BLOCK_AUTO_UPDATE_DIALOG.get().also {
            updateSelfState(it)
        }.ifTrue {
            metShowAutoSetDialog
        }
    }

    override fun onHook() {
        metShowAutoSetDialog?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}