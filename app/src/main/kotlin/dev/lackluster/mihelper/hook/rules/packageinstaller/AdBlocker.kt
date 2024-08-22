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
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DialogClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object AdBlocker : YukiBaseHooker() {
    private val ads by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("ads_enable", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.singleOrNull()
    }
    private val adSettings by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("android.provider.MiuiSettings\$Ad", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.singleOrNull()
    }
    private val recommend by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("app_store_recommend", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.singleOrNull()
    }
    private val scan by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("virus_scan_install", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.singleOrNull()
    }
    private val initMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("findViewById(R.id.loading_icon_container)", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val showPopup by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val cancelClick by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("pure_mode_guide_dialog_day_finish", StringMatchType.Equals)
                addUsingString("is_remember", StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }.singleOrNull()
    }

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.REMOVE_ELEMENT) {
            if (appClassLoader == null) return@hasEnable
            ads?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            adSettings?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            recommend?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            scan?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            initMethod?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val activity = this.instance as Activity
                    val reportButton = activity.findViewById<ImageView>(
                        activity.resources.getIdentifier("feedback_icon","id", activity.packageName)
                    )
                    reportButton.visibility = View.GONE
                }
            }
            "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject".toClassOrNull()?.method {
                param("com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject\$ViewHolder".toClass())
                paramCount = 1
            }?.ignored()?.hookAll {
                after {
                    val viewHolder = this.args(0).any()
                    (viewHolder?.current()?.method {
                        name = "getClContentView"
                    }?.call() as? View)?.visibility = View.GONE
                }
            }
            val cancelClickInstance = cancelClick?.getMethodInstance(appClassLoader!!)
            showPopup?.getMethodInstance(appClassLoader!!)?.hook {
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