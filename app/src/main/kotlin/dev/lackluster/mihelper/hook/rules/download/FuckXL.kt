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

package dev.lackluster.mihelper.hook.rules.download

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object FuckXL : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Download.FUCK_XL) {
            val sdcardPath = "com.android.providers.downloads.config.XLDownloadCfg".toClassOrNull()
                ?.resolve()?.firstFieldOrNull {
                    name = "sdcardPath"
                }?.get<String>() ?: "/storage/emulated/0"
            $$"com.android.providers.downloads.config.DownloadSettings$XLSecureConfigSettings".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "getDpDebugLogPath"
                }?.hook {
                    before {
                        this.result = this.args(0).string()
                    }
                }
            }
            "com.android.providers.downloads.config.XLConfig".toClassOrNull()?.apply {
                resolve().firstFieldOrNull {
                    name = "logDir"
                    modifiers(Modifiers.STATIC)
                }?.set("${sdcardPath}/MIUI/.xlDownload/dp.log")
                resolve().firstFieldOrNull {
                    name = "logSoDir"
                    modifiers(Modifiers.STATIC)
                }?.set("${sdcardPath}/MIUI/.xlDownload/dp_so.log")
                resolve().firstMethodOrNull {
                    name = "isDebug"
                }?.hook {
                    replaceToFalse()
                }
                resolve().firstMethodOrNull {
                    name = "setDebug"
                }?.hook {
                    intercept()
                }
                resolve().firstMethodOrNull {
                    name = "setSoDebug"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}