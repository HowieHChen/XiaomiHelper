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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipWarning : YukiBaseHooker() {
    private val adbInstallVerifyClass by lazy {
        "com.miui.permcenter.install.AdbInstallVerifyActivity".toClassOrNull()
    }
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SKIP_WARNING) {
            // InterceptFragment
            "com.miui.permcenter.privacymanager.InterceptBaseFragment".toClassOrNull()?.apply {
                method {
                    paramCount = 0
                    returnType = IntType
                    modifiers { isPublic }
                }.hook {
                    replaceTo(0)
                }
            }
            "com.miui.permcenter.privacymanager.InterceptPermissionFragment".toClassOrNull()?.apply {
                method {
                    name = "onCreate"
                }.hook {
                    before {
                        val bundle = this.args(0).cast<Bundle>() ?: Bundle()
                        bundle.putInt("KET_STEP_COUNT", 0)
                        bundle.putBoolean("KEY_ALLOW_ENABLE", true)
                        this.args(0).set(bundle)
                    }
                }
            }
            // ADB Input
            "com.miui.permcenter.install.AdbInputApplyActivity".toClassOrNull()?.apply {
                method {
                    name = "onCreate"
                }.hook {
                    after {
                        val activity = this.instance as? Activity ?: return@after
                        activity.findViewById<TextView>(ResourcesUtils.warning_info)?.apply {
                            val msg = context.getString(ResourcesUtils.usb_adb_input_apply_step_1) + "\n\n" +
                                    context.getString(ResourcesUtils.usb_adb_input_apply_step_2) + "\n\n" +
                                    context.getString(ResourcesUtils.usb_adb_input_apply_step_3)
                            text = msg
                        }
                        activity.findViewById<Button>(ResourcesUtils.accept).apply {
                            text = context.getString(ResourcesUtils.button_text_accept)
                            isEnabled = true
                        }
                    }
                }
                method {
                    name = "onClick"
                }.hook {
                    before {
                        val view = this.args(0).cast<View>() ?: return@before
                        if (view.id == ResourcesUtils.accept) {
                            val activity = this.instance as? Activity ?: return@before
                            this.instance.current().field {
                                type = HandlerClass
                            }.cast<Handler>()?.removeMessages(100)
                            val intent = Intent(activity, adbInstallVerifyClass)
                            intent.putExtra("is_input", true)
                            activity.startActivityForResult(intent, 3)
                            this.result = null
                        }
                    }
                }
            }
            "com.miui.permcenter.install.AdbInputApplyActivity\$a".toClassOrNull()?.apply {
                method {
                    name = "handleMessage"
                }.hook {
                    intercept()
                }
            }
        }
    }
}