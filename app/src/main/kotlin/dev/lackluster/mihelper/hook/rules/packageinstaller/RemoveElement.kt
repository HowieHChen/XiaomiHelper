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

package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.app.Dialog
import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object RemoveElement : YukiBaseHooker() {
    private val menuCreateClass1 by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("FullSafeStrategyType", StringMatchType.Contains)
            }
        }
    }
    private val onCreateOptionsMenuMethod1 by lazy {
        DexKit.findMethodWithCache("create_options_menu1") {
            matcher {
                name = "onCreateOptionsMenu"
                returnType = "boolean"
            }
            searchClasses = menuCreateClass1
        }
    }
    private val menuCreateClass2 by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("R.id.main_content", StringMatchType.Contains)
                addUsingString("dark_loading.json", StringMatchType.Equals)
            }
        }
    }
    private val onCreateOptionsMenuMethod2 by lazy {
        DexKit.findMethodWithCache("create_options_menu2") {
            matcher {
                name = "onCreateOptionsMenu"
                returnType = "boolean"
            }
            searchClasses = menuCreateClass2
        }
    }
    private val clzPureModeGuide by lazy {
        DexKit.findClassWithCache("safe_mode_guidance") {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
        }
    }
    private val showPopupMethod by lazy {
        DexKit.findMethodWithCache("safe_mode_show_popup") {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
            searchClasses = listOfNotNull(clzPureModeGuide?.className?.let { DexKit.dexKitBridge.getClassData(it) })
        }
    }
    private val cancelClickMethod by lazy {
        DexKit.findMethodWithCache("safe_mode_popup_cancel") {
            matcher {
                addUsingString("pure_mode_guide_dialog_day_finish", StringMatchType.Equals)
                addUsingString("is_remember", StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }
    }
    private val miuixDialogClass by lazy {
        DexKit.findClassWithCache("miuix_dialog") {
            matcher {
                className("miuix.appcompat.app", StringMatchType.StartsWith)
                addField {
                    type = "miuix.appcompat.app.AlertController"
                    modifiers = Modifier.FINAL
                }
                addUsingString("android.ui", StringMatchType.Equals)
                addUsingString("android.imms", StringMatchType.Equals)
                addUsingString("system_server", StringMatchType.Equals)
            }
        }
    }
    private val clzSafeModeTipViewObject by lazy {
        "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject".toClassOrNull()
    }
    private val clzSafeModeTipViewObjectViewHolder by lazy {
        $$"com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject$ViewHolder".toClassOrNull()
    }
    private val metShowEnhanceDialog by lazy {
        DexKit.findMethodWithCache("show_enhance_dialog") {
            matcher {
                addUsingString("enhance_dialog_already_pop_sum", StringMatchType.Equals)
                returnType = "void"
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.REMOVE_ELEMENT) {
            if (appClassLoader == null) return@hasEnable
            onCreateOptionsMenuMethod1?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val menu = this.args(0).cast<Menu>() ?: return@after
                    menu.findItem(ResourcesUtils.feedback)?.isVisible = false
                }
            }
            onCreateOptionsMenuMethod2?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val menu = this.args(0).cast<Menu>() ?: return@after
                    menu.findItem(ResourcesUtils.feedback)?.isVisible = false
                }
            }
            val metGetClContentView = clzSafeModeTipViewObjectViewHolder?.resolve()?.firstMethodOrNull {
                name = "getClContentView"
            }
            clzSafeModeTipViewObject?.resolve()?.firstMethodOrNull {
                clzSafeModeTipViewObjectViewHolder?.let {
                    parameters(it)
                }
                parameterCount = 1
                modifiers(Modifiers.PUBLIC)
            }?.hook {
                after {
                    val viewHolder = this.args(0).any()
                    metGetClContentView?.copy()?.of(viewHolder)?.invoke<View>()?.visibility = View.GONE
                }
            }
            val cancelClickInstance = cancelClickMethod?.getMethodInstance(appClassLoader!!)
            val miuixDialogClz = miuixDialogClass?.getInstance(appClassLoader!!)
            clzPureModeGuide?.getInstance(appClassLoader!!)?.apply {
                val fldDialog = resolve().firstFieldOrNull {
                    type {
                        it == miuixDialogClz || it == Dialog::class.java
                    }
                }?.self?.apply {
                    makeAccessible()
                }
                val fldContext = resolve().firstFieldOrNull {
                    type(Context::class)
                }?.self?.apply {
                    makeAccessible()
                }
                showPopupMethod?.getMethodInstance(appClassLoader!!)?.hook {
                    before {
                        val dialog = fldDialog?.get(this.instance) as? Dialog
                        val context = fldContext?.get(this.instance) as? Context
                        if (dialog == null || context == null) return@before
                        if (cancelClickInstance == null) {
                            dialog.dismiss()
                            YLog.error("[PackageInstaller] Can't click the negative button of the dialog")
                        } else {
                            val checkBox = CheckBox(context).apply {
                                isChecked = true
                            }
                            checkBox.isChecked = true
                            cancelClickInstance.invoke(null, this.instance, checkBox, dialog, -2)
                        }
                        this.result = null
                    }
                }
            }
            metShowEnhanceDialog?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
        }
    }
}