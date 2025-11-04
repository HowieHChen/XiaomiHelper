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

import android.graphics.Typeface
import android.net.TrafficStats
import android.os.Handler
import android.os.Message
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.isSubclassOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.TextAppearance_StatusBar_NetWorkSpeedNumber
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.fontPath
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dpFloat

object NetworkSpeed : YukiBaseHooker() {
    private const val KEY_UP_BYTES = "KEY_UP_BYTES"
    private const val KEY_DOWN_BYTES = "KEY_DOWN_BYTES"
    private val mode = Prefs.getInt(Pref.Key.SystemUI.IconTuner.NET_SPEED_MODE, 0)
    private val measureText by lazy {
        when (mode) {
            1 -> "0.00KB "
            2 -> "0.00KB↑ "
            3 -> "0.00KB▲ "
            4 -> "0.00KB△ "
            else -> ""
        }
    }

    private val valueNetSpeedFW = Prefs.getInt(FontWeight.NET_SPEED_NUMBER_VAL, 630)
    private val valueNetUnitFW = Prefs.getInt(FontWeight.NET_SPEED_UNIT_VAL, 630)
    private val valueNetSeparateFW = Prefs.getInt(FontWeight.NET_SPEED_SEPARATE_VAL, 630)
    private val modifyNetSpeedNumberFW =
        mode == 0 && Prefs.getBoolean(FontWeight.NET_SPEED_NUMBER, false) && valueNetSpeedFW in 1..1000
    private val modifyNetSpeedUnitFW =
        mode == 0 && Prefs.getBoolean(FontWeight.NET_SPEED_UNIT, false) && valueNetUnitFW in 1..1000
    private val modifyNetSpeedSeparateFW =
        mode != 0 && Prefs.getBoolean(FontWeight.NET_SPEED_SEPARATE, false) && valueNetSeparateFW in 1..1000
    private val typefaceNetSpeedNumberFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valueNetSpeedFW").build()
    }
    private val typefaceNetSpeedUnitFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valueNetUnitFW").build()
    }
    private val typefaceNetSpeedSeparateFW by lazy {
        Typeface.Builder(fontPath).setFontVariationSettings("'wght' $valueNetSeparateFW").build()
    }

    override fun onHook() {
        if (mode == 0 && !modifyNetSpeedNumberFW && !modifyNetSpeedUnitFW) return
        "com.android.systemui.statusbar.views.NetworkSpeedView".toClassOrNull()?.apply {
            val mNetworkSpeedNumberText = resolve().firstFieldOrNull {
                name = "mNetworkSpeedNumberText"
            }
            val mNetworkSpeedUnitText = resolve().firstFieldOrNull {
                name = "mNetworkSpeedUnitText"
            }
            resolve().firstMethodOrNull {
                name {
                    it.contains("updateResources")
                }
            }?.hook {
                after {
                    val networkSpeedNumberText = mNetworkSpeedNumberText?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    val networkSpeedUnitText = mNetworkSpeedUnitText?.copy()?.of(this.instance)?.get<TextView>() ?: return@after
                    if (mode == 0) {
                        if (modifyNetSpeedNumberFW) {
                            networkSpeedNumberText.typeface = typefaceNetSpeedNumberFW
                        }
                        if (modifyNetSpeedUnitFW) {
                            networkSpeedUnitText.typeface = typefaceNetSpeedUnitFW
                        }
                    } else {
                        val translationY = 4.dpFloat(this.instance<View>().context)
                        val lp = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        }
                        networkSpeedNumberText.setTextAppearance(TextAppearance_StatusBar_NetWorkSpeedNumber)
                        networkSpeedNumberText.translationY = -translationY
                        networkSpeedNumberText.layoutParams = lp
                        networkSpeedUnitText.setTextAppearance(TextAppearance_StatusBar_NetWorkSpeedNumber)
                        networkSpeedUnitText.translationY = translationY
                        networkSpeedUnitText.layoutParams = lp
                        if (modifyNetSpeedSeparateFW) {
                            networkSpeedNumberText.typeface = typefaceNetSpeedSeparateFW
                            networkSpeedUnitText.typeface = typefaceNetSpeedSeparateFW
                        }
                    }
                }
            }
            if (mode != 0) {
                val mEmptyWidth = resolve().firstFieldOrNull {
                    name = "mEmptyWidth"
                }
                resolve().firstMethodOrNull {
                    name = "getNetworkSpeedWidth"
                }?.hook {
                    before {
                        val view = this.instance<View>()
                        if (view.width != view.paddingStart + view.paddingEnd) {
                            this.result = view.width
                            return@before
                        }
                        val emptyWidth = mEmptyWidth?.copy()?.of(this.instance)?.get<Int>() ?: -1
                        if (emptyWidth != -1) {
                            this.result = emptyWidth
                            return@before
                        }
                        val networkSpeedNumberText = mNetworkSpeedNumberText?.copy()?.of(this.instance)?.get<TextView>()
                        val networkSpeedUnitText = mNetworkSpeedUnitText?.copy()?.of(this.instance)?.get<TextView>()
                        var finalWidth = 0
                        networkSpeedNumberText?.paint?.let {
                            finalWidth = it.measureText(measureText).toInt()
                        }
                        networkSpeedUnitText?.paint?.let {
                            finalWidth = maxOf(finalWidth, it.measureText(measureText).toInt())
                        }
                        finalWidth = finalWidth + view.paddingStart + view.paddingEnd
                        mEmptyWidth?.copy()?.of(this.instance)?.set(finalWidth)
                        this.result = finalWidth
                    }
                }
            }
        }
        if (mode != 0) {
            "com.android.systemui.statusbar.policy.NetworkSpeedController".toClassOrNull()?.apply {
                val mBgHandler = resolve().firstFieldOrNull {
                    name = "mBgHandler"
                }
                val mHandler = resolve().firstFieldOrNull {
                    name = "mHandler"
                }
                val mLastTime = resolve().firstFieldOrNull {
                    name = "mLastTime"
                }
                val updateText = resolve().firstMethodOrNull {
                    name = "updateText"
                }
                var clzHandler: Class<Any>? = null
                for (i in 1..9) {
                    val handler = "com.android.systemui.statusbar.policy.NetworkSpeedController$${i}".toClassOrNull()
                    if (handler?.isSubclassOf(Handler::class) == true) {
                        clzHandler = handler
                        break
                    }
                }
                clzHandler?.apply {
                    val r8ClassId = resolve().firstFieldOrNull {
                        type(Int::class)
                    }
                    val instance = resolve().firstFieldOrNull {
                        type("com.android.systemui.statusbar.policy.NetworkSpeedController")
                    }
                    resolve().firstMethodOrNull {
                        name = "handleMessage"
                    }?.hook {
                        before {
                            val obtainMessage = this.args(0).cast<Message>()
                            val classId = r8ClassId?.copy()?.of(this.instance)?.get<Int>()
                            val controller = instance?.copy()?.of(this.instance)?.get() ?: return@before
                            if (classId == 1 && obtainMessage?.what == 200001) {
                                val handler = mHandler?.copy()?.of(controller)?.get<Handler>() ?: return@before
                                val bgHandler = mBgHandler?.copy()?.of(controller)?.get<Handler>() ?: return@before
                                val message = handler.obtainMessage(100004)
                                val currentTimeMillis = System.currentTimeMillis()
                                val upload = TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo")
                                val download = TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo")
                                val lastTime = mLastTime?.copy()?.of(controller)?.get<Long>() ?: 0L
                                var uploadSpeed = 0L
                                var downloadSpeed = 0L
                                if (lastTime != 0L && currentTimeMillis > lastTime) {
                                    val uploadBytes = (XposedHelpers.getAdditionalInstanceField(controller, KEY_UP_BYTES) as? Long) ?: 0L
                                    if (uploadBytes != 0L && upload != 0L && upload > uploadBytes) {
                                        uploadSpeed = ((upload - uploadBytes) * 1000) / (currentTimeMillis - lastTime)
                                    }
                                    val downloadBytes = (XposedHelpers.getAdditionalInstanceField(controller, KEY_DOWN_BYTES) as? Long) ?: 0L
                                    if (downloadBytes != 0L && download != 0L && download > downloadBytes) {
                                        downloadSpeed = ((download - downloadBytes) * 1000) / (currentTimeMillis - lastTime)
                                    }
                                }
                                mLastTime?.copy()?.of(controller)?.set(currentTimeMillis)
                                XposedHelpers.setAdditionalInstanceField(controller, KEY_UP_BYTES, upload)
                                XposedHelpers.setAdditionalInstanceField(controller, KEY_DOWN_BYTES, download)
                                message.obj = Pair(uploadSpeed, downloadSpeed)
                                message.sendToTarget()
                                bgHandler.removeMessages(200001)
                                bgHandler.sendEmptyMessageDelayed(200001, 4000L)
                                this.result = null
                            }
                            if (classId == 0 && obtainMessage?.what == 100004) {
                                if (
                                    obtainMessage.obj is Pair<*, *> &&
                                    (obtainMessage.obj as Pair<*, *>).first is Long &&
                                    (obtainMessage.obj as Pair<*, *>).second is Long
                                ) {
                                    val uploadSpeed = (obtainMessage.obj as Pair<*, *>).first as Long
                                    val downloadSpeed = (obtainMessage.obj as Pair<*, *>).second as Long
                                    updateText?.copy()?.of(controller)?.invoke(arrayOf(
                                        formatSpeed(uploadSpeed / 1024.0f, true),
                                        formatSpeed(downloadSpeed / 1024.0f, false),
                                    ))
                                }
                                this.result = null
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formatSpeed(speed: Float, upload: Boolean): String {
        val unitMega: Boolean
        var finalSpeed = speed
        val downloadSymbol = if (upload) {
            when (mode) {
                2 -> "↑"
                3 -> "▲"
                4 -> if (finalSpeed == 0.0f) "△" else "▲"
                else -> ""
            }
        } else {
            when (mode) {
                2 -> "↓"
                3 -> "▼"
                4 -> if (finalSpeed == 0.0f) "▽" else "▼"
                else -> ""
            }
        }
        if (finalSpeed > 999.0f) {
            finalSpeed /= 1024.0f
            unitMega = true
        } else {
            unitMega = false
        }
        return (if (finalSpeed < 10.0f) {
            "%.2f".format(finalSpeed)
        } else if (finalSpeed < 100.0f) {
            "%.1f".format(finalSpeed)
        } else {
            "%.0f".format(finalSpeed)
        }) + (if (unitMega) "MB" else "KB") + downloadSymbol
    }
}