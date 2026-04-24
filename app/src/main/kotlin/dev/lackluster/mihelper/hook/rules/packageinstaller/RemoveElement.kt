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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object RemoveElement : StaticHooker() {
    private val menuCreateClass1 by lazy {
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("FullSafeStrategyType", StringMatchType.Contains)
                }
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
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("R.id.main_content", StringMatchType.Contains)
                    addUsingString("dark_loading.json", StringMatchType.Equals)
                }
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
            searchClasses = listOfNotNull(clzPureModeGuide?.className?.let { DexKit.withBridge { getClassData(it) } })
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
    private val clzSafeModeTipViewObject by "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject".lazyClassOrNull()
    private val clzSafeModeTipViewObjectViewHolder by $$"com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject$ViewHolder".lazyClassOrNull()
    private val metShowEnhanceDialog by lazy {
        DexKit.findMethodWithCache("show_enhance_dialog") {
            matcher {
                addUsingString("enhance_dialog_already_pop_sum", StringMatchType.Equals)
                returnType = "void"
            }
        }
    }

    override fun onInit() {
        Preferences.PackageInstaller.REMOVE_ELEMENT.get().also { 
            updateSelfState(it)
        }.ifTrue {
            onCreateOptionsMenuMethod1
            onCreateOptionsMenuMethod2
            clzPureModeGuide
            showPopupMethod
            cancelClickMethod
            miuixDialogClass
            metShowEnhanceDialog
        }
    }

    override fun onHook() {
        onCreateOptionsMenuMethod1?.getMethodInstance(classLoader)?.hook {
            val ori = proceed()
            val menu = getArg(0) as? Menu
            if (menu != null) {
                menu.findItem(ResourcesUtils.feedback)?.isVisible = false
            }
            result(ori)
        }
        onCreateOptionsMenuMethod2?.getMethodInstance(classLoader)?.hook {
            val ori = proceed()
            val menu = getArg(0) as? Menu
            if (menu != null) {
                menu.findItem(ResourcesUtils.feedback)?.isVisible = false
            }
            result(ori)
        }
        val metGetClContentView = clzSafeModeTipViewObjectViewHolder?.resolve()?.firstMethodOrNull {
            name = "getClContentView"
        }?.toTyped<View>()
        clzSafeModeTipViewObject?.resolve()?.firstMethodOrNull {
            clzSafeModeTipViewObjectViewHolder?.let {
                parameters(it)
            }
            parameterCount = 1
            modifiers(Modifiers.PUBLIC)
        }?.hook {
            val ori = proceed()
            val viewHolder = getArg(0)
            if (viewHolder != null) {
                metGetClContentView?.invoke(viewHolder)?.visibility = View.GONE
            }
            result(ori)
        }
        val cancelClickInstance = cancelClickMethod?.getMethodInstance(classLoader)
        val miuixDialogClz = miuixDialogClass?.getInstance(classLoader)
        clzPureModeGuide?.getInstance(classLoader)?.apply {
            val fldDialog = resolve().firstFieldOrNull {
                type {
                    it == miuixDialogClz || it == Dialog::class.java
                }
            }?.toTyped<Dialog>()
            val fldContext = resolve().firstFieldOrNull {
                type(Context::class)
            }?.toTyped<Context>()
            showPopupMethod?.getMethodInstance(this@RemoveElement.classLoader)?.hook {
                val dialog = fldDialog?.get(thisObject)
                val context = fldContext?.get(thisObject)
                if (dialog != null && context != null) {
                    if (cancelClickInstance == null) {
                        dialog.dismiss()
                        e { "Can't click the negative button of the dialog" }
                    } else {
                        val checkBox = CheckBox(context)
                        checkBox.isChecked = true
                        cancelClickInstance.invoke(null, thisObject, checkBox, dialog, -2)
                    }
                    result(null)
                } else {
                    result(proceed())
                }
            }
        }
        metShowEnhanceDialog?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}