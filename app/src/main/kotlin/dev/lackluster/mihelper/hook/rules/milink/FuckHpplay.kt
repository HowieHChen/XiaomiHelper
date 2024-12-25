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

@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.milink

import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.io.File

object FuckHpplay : YukiBaseHooker() {
    private val preferenceClass by lazy {
        "com.hpplay.sdk.source.common.store.Preference".toClassOrNull()
    }
    private val hpplayClass by lazy {
        "com.hpplay.common.utils.ContextPath".toClassOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiLink.FUCK_HPPLAY) {
            "com.xiaomi.aivsbluetoothsdk.utils.FileUtil".toClassOrNull()?.apply {
                method {
                    name = "splicingFilePath"
                    param(StringClass, StringClass, StringClass, StringClass)
                }.hook {
                    before {
                        val rootDir = this.args(0).string()
                        if (rootDir.startsWith("com.milink.service")) {
                            this.args(0).set("MIUI${File.separator}AIVS${File.separator}${rootDir}")
                        }
                    }
                }
            }
            val keyExist = preferenceClass?.field {
                name = "KEY_SDCARD_DIR_ENABLE"
                type = StringClass
                modifiers { isStatic && isFinal }
            }?.ignored()?.give() != null
            if (keyExist) {
                hpplayClass?.apply {
                    method {
                        name = "initDirs"
                    }.hook {
                        before {
                            val context = this.args(0).cast<Context>() ?: return@before
                            PreferenceManager.getDefaultSharedPreferences(context)
                                .edit()
                                .putBoolean("key_sdcard_dir_enable", false)
                                .apply()
                        }
                    }
                }
                "com.hpplay.sdk.source.api.LelinkSourceSDK".toClassOrNull()?.apply {
                    method {
                        name = "enableSDCard"
                    }.hook {
                        before {
                            this.args(0).setFalse()
                        }
                    }
                }
            } else {
                val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
                hpplayClass?.apply {
                    method {
                        name = "jointPath"
                    }.hook {
                        before {
                            val args = this.args(0).cast<Array<Any>>() ?: return@before
                            val stringArgs = args.map { it.toString() }.toMutableList()
                            if (stringArgs.size == 3 && stringArgs[0] == sdcardPath && stringArgs[1] == "com.milink.service") {
                                stringArgs.add(1, "MIUI")
                                this.args(0).set(stringArgs.toTypedArray())
                            }
                        }
                    }
                }
            }
        }
    }
}