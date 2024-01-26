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

package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DialogClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object HideSafeModePopup : YukiBaseHooker() {
    private val showPopup by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
        }.firstOrNull()
    }
//    private val okClick by lazy {
//        DexKit.dexKitBridge.findMethod {
//            matcher {
//                addUsingString("pure_mode_guide_dialog_day_finish", StringMatchType.Equals)
//                addUsingString("is_remember", StringMatchType.Equals)
//                addUsingString("safe_mode_guidance_popup_open_btn", StringMatchType.Equals)
//                modifiers = Modifier.STATIC
//            }
//        }.firstOrNull()
//    }
    private val cancelClick by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("pure_mode_guide_dialog_day_finish", StringMatchType.Equals)
                addUsingString("is_remember", StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }.firstOrNull()
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_HIDE_SAFE_MODE_POPUP) {
            val cancelClickInstance = cancelClick?.getMethodInstance(appClassLoader?:return@hasEnable)
            showPopup?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                before {
                    val dialog = this.instance.current().field { type = DialogClass }.any() as Dialog
                    if (cancelClickInstance == null) {
                        dialog.dismiss()
                    }
                    else {
                        val context = this.instance.current().field { type = ContextClass }.any() as Context
                        val checkBox = LayoutInflater.from(context).inflate(
                            context.resources.getIdentifier("pure_mode_dialog_layout", "layout", Scope.PACKAGE_INSTALLER),
                            null,
                            false
                        ).findViewById<CheckBox>(
                            context.resources.getIdentifier("cb_do_show_again", "id", Scope.PACKAGE_INSTALLER)
                        )
                        cancelClickInstance.invoke(null, this.instance, checkBox, dialog, -2)
                    }
                    this.result = null
                }
            }
        }
    }
}