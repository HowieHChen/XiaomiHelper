/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.lbe

import android.content.Context
import android.widget.Toast
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.getResId

object ClipboardToast : StaticHooker() {
    private var overlay_read_clip_toast = 0

    override fun onInit() {
        updateSelfState(Preferences.LBE.TOAST_CLIPBOARD_USAGE.get())
    }

    override fun onHook() {
        "com.lbe.security.utility.ToastUtil".toClassOrNull()?.apply {
            val mContext = resolve().firstFieldOrNull {
                name = "mContext"
            }?.toTyped<Context>()
            resolve().firstMethodOrNull {
                name = "initToastView"
            }?.hook {
                val type = getArg(1) as? Int
                if (type == 1) {
                    val context = mContext?.get(thisObject)
                    val pkgName = getArg(0) as? String
                    if (context != null && pkgName != null) {
                        if (overlay_read_clip_toast == 0) {
                            overlay_read_clip_toast = context.getResId("overlay_read_clip_toast", "string", Scope.LBE)
                        }
                        context.packageManager.let {
                            it.getPackageInfo(pkgName, 0).applicationInfo?.loadLabel(it)?.toString()
                        }?.let {
                            Toast.makeText(
                                context,
                                context.getString(overlay_read_clip_toast, it),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@hook result(null)
                        }
                    }
                }
                result(proceed())
            }
        }
    }
}