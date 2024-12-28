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

package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.provider.Settings
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.hyperx.core.util.SystemProperties
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_giga
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.status_bar_recent_memory_mega
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.text.DecimalFormat
import kotlin.math.ceil

object PadShowMemory : YukiBaseHooker() {
    private var mIsOpenExternalRam: Int = 0
    private var mExternalRam: Float = 0.0f
    private var mTotalMemory: Long = 0
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.PAD_RECENT_SHOW_MEMORY) {
            "com.miui.home.recents.views.RecentsDecorations".toClass().apply {
                constructor().hook {
                    after {
                        val view = this.instance as View
                        mIsOpenExternalRam = Settings.Global.getInt(view.context.contentResolver, "isExternalRamOn", 0)
                        val bdSize = try {
                            SystemProperties.get("persist.miui.extm.bdsize").toFloatOrNull() ?: 0.0f
                        } catch (_: Throwable) {
                            0.0f
                        }
                        mExternalRam = bdSize / 1024.0f
                        mTotalMemory = this.instance.current().field { name = "mTotalMemory" }.long()
                    }
                }
                method {
                    name = "hideTxtMemoryInfoView"
                }.ignored().hook {
                    intercept()
                }
                method {
                    name = "isMemInfoShow"
                }.ignored().hook {
                    replaceToTrue()
                }
                method {
                    name = "getFormatedMemory"
                }.ignored().hook {
                    before {
                        val context = (this.instance as View).context
                        val j = this.args(0).long()
                        val z = this.args(1).boolean()
                        val z2 = z && j == mTotalMemory
                        val round: Long
                        val j2: Long = j / 1024
                        var str = ""
                        if (j2 < 1024) {
                            this.result = context.getString(status_bar_recent_memory_mega, "$j2")
                            return@before
                        }
                        if (z2 && mIsOpenExternalRam == 1 && mExternalRam > 0.0f) {
                            str = "+" + DecimalFormat("#0.0").format(mExternalRam)
                        }
                        round = if (z) {
                            (ceil(j2 / 1024.0) * 10.0).toLong()
                        } else {
                            Math.round(j2 * 10.0 / 1024.0)
                        }
                        this.result = context.getString(status_bar_recent_memory_giga, "${(round / 10)}.${(round % 10)}${str}")
                    }
                }
            }

        }
    }
}