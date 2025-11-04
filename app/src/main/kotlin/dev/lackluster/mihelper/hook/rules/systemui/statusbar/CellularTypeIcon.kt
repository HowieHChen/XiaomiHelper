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
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.mobile_signal_container
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.mobile_type_single
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.fontPath
import dev.lackluster.mihelper.utils.Prefs

object CellularTypeIcon : YukiBaseHooker() {
    private val typeSingle = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE, false)
    private val swapTypeIcon = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SWAP, false)
    private val typeCustom = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM, false)
    private val typeCustomVal = Prefs.getString(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_CUSTOM_VAL, "")
    private val valueTypeSingleSize = Prefs.getFloat(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE_VAL, 14.0f)
    private val modifyTypeSingleSize =
        Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.CELLULAR_TYPE_SINGLE_SIZE, false) && valueTypeSingleSize > 0.0f
    // Font Weight
    private val valueTypeFW = Prefs.getInt(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_VAL, 660)
    private val valueTypeSingleFW = Prefs.getInt(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE_VAL, 400)
    private val modifyTypeFW =
        Prefs.getBoolean(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE, false) && valueTypeFW in 1..1000
    private val modifyTypeSingleFW =
        Prefs.getBoolean(Pref.Key.SystemUI.FontWeight.CELLULAR_TYPE_SINGLE, false) && valueTypeSingleFW in 1..1000

    private val clzMiuiMobileIconBinder by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.ui.binder.MiuiMobileIconBinder".toClassOrNull()
    }
    private val typefaceTypeFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valueTypeFW").build()
    }
    private val typefaceTypeSingleFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valueTypeSingleFW").build()
    }

    override fun onHook() {
        if (typeSingle || typeCustom) {
            "com.miui.interfaces.IOperatorCustomizedPolicy\$OperatorConfig".toClassOrNull()?.apply {
                val showMobileDataTypeSingle = resolve().firstFieldOrNull {
                    name = "showMobileDataTypeSingle"
                }
                val mobileTypeName = resolve().firstFieldOrNull {
                    name = "mobileTypeName"
                }
                resolve().firstConstructor().hook {
                    after {
                        val config = this.instance
                        if (typeSingle) {
                            showMobileDataTypeSingle?.copy()?.of(config)?.set(true)
                        }
                        if (typeCustom && !typeCustomVal.isNullOrBlank()) {
                            val typeList = typeCustomVal.split(',')
                            if (typeList.size == 1 && typeList[0].isNotBlank()) {
                                mobileTypeName?.copy()?.of(config)?.set(
                                    List(15) { typeList[0] }
                                )
                            } else if (typeList.size == 15) {
                                mobileTypeName?.copy()?.of(config)?.set(typeList)
                            }
                        }
                    }
                }
            }
        }
        if (modifyTypeFW) {
            "com.miui.systemui.statusbar.views.MobileTypeDrawable".toClassOrNull()?.apply {
                val mMobileTypeTextPaint = resolve().firstFieldOrNull {
                    name = "mMobileTypeTextPaint"
                }
                val mMobileTypePlusPaint = resolve().firstFieldOrNull {
                    name = "mMobileTypePlusPaint"
                }
                resolve().firstConstructor().hook {
                    after {
                        YLog.info("MobileTypeDrawable Constructor")
                        mMobileTypeTextPaint?.copy()?.of(this.instance)?.get<Paint>()?.typeface = typefaceTypeFW
                        mMobileTypePlusPaint?.copy()?.of(this.instance)?.get<Paint>()?.typeface = typefaceTypeFW
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setMiuiStatusBarTypeface"
                }?.hook {
                    before {
                        YLog.info("MobileTypeDrawable setMiuiStatusBarTypeface")
                        val paints = this.args(0).array<Paint>()
                        for (paint in paints) {
                            paint.typeface = typefaceTypeFW
                        }
                        this.result = null
                    }
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
                before {
                    val viewGroup = this.args(0).cast<ViewGroup>() ?: return@before
                    val mobileTypeSingle = viewGroup.findViewById<TextView>(mobile_type_single) ?: return@before
                    val mobileSignalContainer = viewGroup.findViewById<ViewGroup>(mobile_signal_container) ?: return@before
                    val parent = mobileTypeSingle.parent
                    if (
                        swapTypeIcon && parent is ViewGroup &&
                        mobileTypeSingle.parent == mobileSignalContainer.parent
                    ) {
                        val singleTypeIndex = parent.indexOfChild(mobileTypeSingle)
                        val containerIndex = parent.indexOfChild(mobileSignalContainer)
                        parent.removeView(mobileTypeSingle)
                        parent.removeView(mobileSignalContainer)
                        parent.addView(mobileSignalContainer, singleTypeIndex)
                        parent.addView(mobileTypeSingle, containerIndex)
                    }
                    if (modifyTypeSingleSize) {
                        mobileTypeSingle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valueTypeSingleSize)
                    }
                    if (modifyTypeSingleFW) {
                        mobileTypeSingle.typeface = typefaceTypeSingleFW
                    }
                }
            }
        }
        if (typeSingle && (modifyTypeSingleSize || modifyTypeSingleFW)) {
            clzMiuiMobileIconBinder?.resolve()?.firstMethodOrNull {
                name {
                    it.contains("updateMobileLayoutParams")
                }
            }?.hook {
                after {
                    YLog.info("MiuiMobileIconBinder#updateMobileLayoutParams")
                    val mobileTypeSingle = this.args(0).cast<TextView>() ?: return@after
                    if (modifyTypeSingleSize) {
                        mobileTypeSingle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, valueTypeSingleSize)
                    }
                    if (modifyTypeSingleFW) {
                        mobileTypeSingle.typeface = typefaceTypeSingleFW
                    }
                }
            } ?: YLog.info("dont find updateMobileLayoutParams")
        }
    }
}