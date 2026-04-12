/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.graphics.Paint
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.mobile_signal_container
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.mobile_type_single
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.toTyped

object CellularTypeIcon : StaticHooker() {
    private val typeSingle by Preferences.SystemUI.StatusBar.IconDetail.USE_CELLULAR_TYPE_SINGLE.lazyGet()
    private val swapTypeIcon by Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SWAP_INDEX.lazyGet()
    private val typeCustom by Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_LIST.lazyGet()
    private val typeCustomVal by Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_LIST_VAL.lazyGet()
    // Text size
    private val valueTypeSingleSize by Preferences.SystemUI.StatusBar.IconDetail.CELLULAR_TYPE_SINGLE_SIZE_VAL.lazyGet()
    private val modifyTypeSingleSize by lazy {
        Preferences.SystemUI.StatusBar.IconDetail.CUSTOM_CELLULAR_TYPE_SINGLE_SIZE.get() && valueTypeSingleSize > 0.0f
    }
    // Font weight
    private val valueTypeFW by Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_WEIGHT.lazyGet()
    private val valueTypeSingleFW by Preferences.SystemUI.StatusBar.Font.CELLULAR_TYPE_SINGLE_WEIGHT.lazyGet()
    private val modifyTypeFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE.get() && valueTypeFW in 1..1000
    }
    private val modifyTypeSingleFW by lazy {
        Preferences.SystemUI.StatusBar.Font.CUSTOM_CELLULAR_TYPE_SINGLE.get() && valueTypeSingleFW in 1..1000
    }

    private val clzMiuiMobileIconBinder by "com.android.systemui.statusbar.pipeline.mobile.ui.binder.MiuiMobileIconBinder".lazyClassOrNull()
    private val typefaceTypeFW by lazy {
        getTypeface(valueTypeFW)
    }
    private val typefaceTypeSingleFW by lazy {
        getTypeface(valueTypeSingleFW)
    }

    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        if (typeSingle || typeCustom) {
            $$"com.miui.interfaces.IOperatorCustomizedPolicy$OperatorConfig".toClassOrNull()?.apply {
                val showMobileDataTypeSingle = resolve().firstFieldOrNull {
                    name = "showMobileDataTypeSingle"
                }?.toTyped<Boolean>()
                val mobileTypeName = resolve().firstFieldOrNull {
                    name = "mobileTypeName"
                }?.toTyped<List<*>>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    if (typeSingle) {
                        showMobileDataTypeSingle?.set(thisObject, true)
                    }
                    if (typeCustom && typeCustomVal.isNotBlank()) {
                        val typeList = typeCustomVal.split(',')
                        if (typeList.size == 1 && typeList[0].isNotBlank()) {
                            mobileTypeName?.set(
                                thisObject,
                                List(15) { typeList[0] }
                            )
                        } else if (typeList.size == 15) {
                            mobileTypeName?.set(
                                thisObject,
                                typeList
                            )
                        }
                    }
                    result(ori)
                }
            }
        }
        if (modifyTypeFW) {
            "com.miui.systemui.statusbar.views.MobileTypeDrawable".toClassOrNull()?.apply {
                val mMobileTypeTextPaint = resolve().firstFieldOrNull {
                    name = "mMobileTypeTextPaint"
                }?.toTyped<Paint>()
                val mMobileTypePlusPaint = resolve().firstFieldOrNull {
                    name = "mMobileTypePlusPaint"
                }?.toTyped<Paint>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    mMobileTypeTextPaint?.get(thisObject)?.typeface = typefaceTypeFW
                    mMobileTypePlusPaint?.get(thisObject)?.typeface = typefaceTypeFW
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "setMiuiStatusBarTypeface"
                }?.hook {
                    val paint = getArg(0) as? Array<*>
                    paint?.forEach {
                        if (it is Paint) {
                            it.typeface = typefaceTypeFW
                        }
                    }
                    result(null)
                }
            }
        }
        if (
            typeSingle &&
            (swapTypeIcon || modifyTypeSingleSize || modifyTypeSingleFW)
        ) {
            clzMiuiMobileIconBinder?.resolve()?.firstMethodOrNull {
                name = "bind"
            }?.hook {
                val viewGroup = getArg(0) as? ViewGroup
                val mobileTypeSingle = viewGroup?.findViewById<TextView>(mobile_type_single)
                val mobileSignalContainer = viewGroup?.findViewById<ViewGroup>(mobile_signal_container)
                val parent = mobileTypeSingle?.parent
                if (
                    swapTypeIcon && parent is ViewGroup &&
                    mobileTypeSingle.parent == mobileSignalContainer?.parent
                ) {
                    val singleTypeIndex = parent.indexOfChild(mobileTypeSingle)
                    val containerIndex = parent.indexOfChild(mobileSignalContainer)
                    parent.removeView(mobileTypeSingle)
                    parent.removeView(mobileSignalContainer)
                    parent.addView(mobileSignalContainer, singleTypeIndex)
                    parent.addView(mobileTypeSingle, containerIndex)
                }
                if (modifyTypeSingleSize) {
                    mobileTypeSingle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valueTypeSingleSize)
                }
                if (modifyTypeSingleFW) {
                    mobileTypeSingle?.typeface = typefaceTypeSingleFW
                }
                result(proceed())
            }
        }
        if (typeSingle && (modifyTypeSingleSize || modifyTypeSingleFW)) {
            clzMiuiMobileIconBinder?.resolve()?.firstMethodOrNull {
                name {
                    it.contains("updateMobileLayoutParams")
                }
            }?.hook {
                val ori = proceed()
                val mobileTypeSingle = getArg(0) as? TextView
                if (modifyTypeSingleSize) {
                    mobileTypeSingle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valueTypeSingleSize)
                }
                if (modifyTypeSingleFW) {
                    mobileTypeSingle?.typeface = typefaceTypeSingleFW
                }
                result(ori)
            } ?: e { "updateMobileLayoutParams method not found!" }
        }
    }
}