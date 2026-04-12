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

package dev.lackluster.mihelper.hook.rules.securitycenter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.asString
import dev.lackluster.mihelper.hook.utils.toTyped

object SkipWarning : StaticHooker() {
    private val adbInstallVerifyClass by "com.miui.permcenter.install.AdbInstallVerifyActivity".lazyClassOrNull()

    override fun onInit() {
        updateSelfState(Preferences.SecurityCenter.SKIP_WARNING_DIALOG.get())
    }

    override fun onHook() {
        // InterceptFragment
        "com.miui.permcenter.privacymanager.InterceptBaseFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                parameterCount = 0
                returnType = Int::class
                modifiers(Modifiers.PUBLIC)
            }?.hook {
                result(0)
            }
        }
        "com.miui.permcenter.privacymanager.InterceptPermissionFragment".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "onCreate"
            }?.hook {
                val newArgs = args.toTypedArray()
                val bundle = newArgs[0] as? Bundle ?: Bundle()
                bundle.putInt("KET_STEP_COUNT", 0)
                bundle.putBoolean("KEY_ALLOW_ENABLE", true)
                newArgs[0] = bundle
                result(proceed(newArgs))
            }
        }
        // ADB Input
        "com.miui.permcenter.install.AdbInputApplyActivity".toClassOrNull()?.apply {
            val fldHandler = resolve().firstFieldOrNull {
                type(Handler::class)
            }?.toTyped<Handler>()
            resolve().firstMethodOrNull {
                name = "onCreate"
            }?.hook {
                val ori = proceed()
                val activity = thisObject as? Activity
                activity?.findViewById<TextView>(ResourcesUtils.warning_info)?.apply {
                    val msg = ResourcesUtils.usb_adb_input_apply_step_1.asString(context) + "\n\n" +
                            ResourcesUtils.usb_adb_input_apply_step_2.asString(context) + "\n\n" +
                            ResourcesUtils.usb_adb_input_apply_step_3.asString(context)
                    text = msg
                }
                activity?.findViewById<Button>(ResourcesUtils.accept)?.apply {
                    text = ResourcesUtils.button_text_accept.asString(context)
                    isEnabled = true
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onClick"
            }?.hook {
                val view = getArg(0) as? View
                val activity = thisObject as? Activity
                if (
                    activity != null && view != null &&
                    view.id == ResourcesUtils.accept
                ) {
                    fldHandler?.get(thisObject)?.removeMessages(100)
                    val intent = Intent(activity, adbInstallVerifyClass)
                    intent.putExtra("is_input", true)
                    activity.startActivityForResult(intent, 3)
                    result(null)
                } else {
                    result(proceed())
                }
            }
        }
        $$"com.miui.permcenter.install.AdbInputApplyActivity$a".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "handleMessage"
            }?.hook {
                result(null)
            }
        }
    }
}