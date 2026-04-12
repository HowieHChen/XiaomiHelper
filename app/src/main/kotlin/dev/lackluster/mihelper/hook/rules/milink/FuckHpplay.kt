/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/milink/FuckHpplay.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 * Convert the code to Kotlin, modified by HowieHChen (howie.dev@outlook.com) on 03/20/2024

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

package dev.lackluster.mihelper.hook.rules.milink

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import java.io.File
import androidx.core.content.edit

object FuckHpplay : StaticHooker() {
    override fun onInit() {
        updateSelfState(Preferences.MiLink.FUCK_HPPLAY.get())
    }

    override fun onHook() {
        "com.xiaomi.aivsbluetoothsdk.utils.FileUtil".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "splicingFilePath"
                parameters(String::class, String::class, String::class, String::class)
            }?.hook {
                val newArgs = args.toTypedArray()
                val rootDir = newArgs[0] as? String ?: ""
                if (rootDir.startsWith("com.milink.service")) {
                    newArgs[0] = "MIUI${File.separator}AIVS${File.separator}${rootDir}"
                }
                result(proceed(newArgs))
            }
        }
        "com.hpplay.common.utils.ContextPath".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "initDirs"
            }?.hook {
                val context = getArg(0) as? Context
                if (context != null) {
                    @Suppress("DEPRECATION")
                    android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                        .edit {
                            putBoolean("key_sdcard_dir_enable", false)
                        }
                }
                result(proceed())
            }
        }
        "com.hpplay.sdk.source.api.LelinkSourceSDK".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "enableSDCard"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = false
                result(proceed(newArgs))
            }
        }
    }
}