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

import android.content.Context
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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.TextAppearance_StatusBar_NetWorkSpeedNumber
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.kilobyte_per_second
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.megabyte_per_second
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.dpFloat

object NetworkSpeed : StaticHooker() {
    private var Any.upBytes by extraOf("KEY_UP_BYTES", 0L)
    private var Any.downBytes by extraOf("KEY_DOWN_BYTES", 0L)

    private val mode by Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_MODE.lazyGet()
    private val refreshPerSecond by Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_REFRESH.lazyGet()
    private val unitMode by Preferences.SystemUI.StatusBar.IconDetail.NET_SPEED_UNIT_MODE.lazyGet()

    private val measureText by lazy {
        StringBuilder().apply {
            append("0.00")
            when (unitMode) {
                0 -> "K"
                1 -> "KB"
                2 -> "KB/s"
                else -> null
            }?.let {
                append(it)
            }
            when (mode) {
                2 -> append("↑")
                3 -> append("▲")
                4 -> append("△")
            }
            append(" ")
        }.toString()
    }

    private val valueNetSpeedFW by Preferences.SystemUI.StatusBar.Font.NET_SPEED_NUMBER_WEIGHT.lazyGet()
    private val valueNetUnitFW by Preferences.SystemUI.StatusBar.Font.NET_SPEED_UNIT_WEIGHT.lazyGet()
    private val valueNetSeparateFW by Preferences.SystemUI.StatusBar.Font.NET_SPEED_SEPARATE_WEIGHT.lazyGet()
    private val modifyNetSpeedNumberFW by lazy {
        mode == 0 && Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_NUMBER.get() && valueNetSpeedFW in 1..1000
    }
    private val modifyNetSpeedUnitFW by lazy {
        mode == 0 && Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_UNIT.get() && valueNetUnitFW in 1..1000
    }
    private val modifyNetSpeedSeparateFW by lazy {
        mode != 0 && Preferences.SystemUI.StatusBar.Font.CUSTOM_NET_SPEED_SEPARATE.get() && valueNetSeparateFW in 1..1000
    }

    private val typefaceNetSpeedNumberFW by lazy {
        getTypeface(valueNetSpeedFW)
    }
    private val typefaceNetSpeedUnitFW by lazy {
        getTypeface(valueNetUnitFW)
    }
    private val typefaceNetSpeedSeparateFW by lazy {
        getTypeface(valueNetSeparateFW)
    }

    override fun onInit() {
        updateSelfState(mode != 0 || refreshPerSecond || modifyNetSpeedNumberFW || modifyNetSpeedUnitFW)
    }

    override fun onHook() {
        if (mode != 0 || modifyNetSpeedNumberFW || modifyNetSpeedUnitFW) {
            "com.android.systemui.statusbar.views.NetworkSpeedView".toClassOrNull()?.apply {
                val mNetworkSpeedNumberText = resolve().firstFieldOrNull {
                    name = "mNetworkSpeedNumberText"
                }?.toTyped<TextView>()
                val mNetworkSpeedUnitText = resolve().firstFieldOrNull {
                    name = "mNetworkSpeedUnitText"
                }?.toTyped<TextView>()
                resolve().firstMethodOrNull {
                    name {
                        it.contains("updateResources")
                    }
                }?.hook {
                    val ori = proceed()
                    val context = (thisObject as? View)?.context
                    val networkSpeedNumberText = mNetworkSpeedNumberText?.get(thisObject)
                    val networkSpeedUnitText = mNetworkSpeedUnitText?.get(thisObject)
                    if (context == null || networkSpeedNumberText == null || networkSpeedUnitText == null) {
                        return@hook result(ori)
                    }
                    if (mode == 0) {
                        if (modifyNetSpeedNumberFW) {
                            networkSpeedNumberText.typeface = typefaceNetSpeedNumberFW
                        }
                        if (modifyNetSpeedUnitFW) {
                            networkSpeedUnitText.typeface = typefaceNetSpeedUnitFW
                        }
                    } else {
                        val translationY = 4.dpFloat(context)
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
                    result(ori)
                }
                if (mode != 0) {
                    val mEmptyWidth = resolve().firstFieldOrNull {
                        name = "mEmptyWidth"
                    }?.toTyped<Int>()
                    resolve().firstMethodOrNull {
                        name = "getNetworkSpeedWidth"
                    }?.hook {
                        val view = thisObject as? View ?: return@hook result(proceed())
                        if (view.width != view.paddingStart + view.paddingEnd) {
                            return@hook result(view.width)
                        }
                        val emptyWidth = mEmptyWidth?.get(thisObject) ?: -1
                        if (emptyWidth != -1) {
                            return@hook result(emptyWidth)
                        }
                        val networkSpeedNumberText = mNetworkSpeedNumberText?.get(thisObject)
                        val networkSpeedUnitText = mNetworkSpeedUnitText?.get(thisObject)
                        var finalWidth = 0
                        networkSpeedNumberText?.paint?.let {
                            finalWidth = it.measureText(measureText).toInt()
                        }
                        networkSpeedUnitText?.paint?.let {
                            finalWidth = maxOf(finalWidth, it.measureText(measureText).toInt())
                        }
                        finalWidth += view.paddingStart + view.paddingEnd
                        mEmptyWidth?.set(thisObject, finalWidth)
                        result(finalWidth)
                    }
                }
            }
        }
        if (mode != 0 || refreshPerSecond) {
            "com.android.systemui.statusbar.policy.NetworkSpeedController".toClassOrNull()?.apply {
                val mBgHandler = resolve().firstFieldOrNull {
                    name = "mBgHandler"
                }?.toTyped<Handler>()
                val mHandler = resolve().firstFieldOrNull {
                    name = "mHandler"
                }?.toTyped<Handler>()
                val mLastTime = resolve().firstFieldOrNull {
                    name = "mLastTime"
                }?.toTyped<Long>()
                val mContext = resolve().firstFieldOrNull {
                    name = "mContext"
                }?.toTyped<Context>()
                val updateText = resolve().firstMethodOrNull {
                    name = "updateText"
                }?.toTyped<Unit>()
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
                    }?.toTyped<Int>()
                    val instance = resolve().firstFieldOrNull {
                        type("com.android.systemui.statusbar.policy.NetworkSpeedController")
                    }?.toTyped<Any>()
                    val postDelayed = if (refreshPerSecond) 1000L else 4000L
                    resolve().firstMethodOrNull {
                        name = "handleMessage"
                    }?.hook {
                        val obtainMessage = getArg(0) as? Message
                        val classId = r8ClassId?.get(thisObject)
                        val controller = instance?.get(thisObject) ?: return@hook result(proceed())
                        if (classId == 1 && obtainMessage?.what == 200001) {
                            val handler = mHandler?.get(controller)
                            val bgHandler = mBgHandler?.get(controller)
                            if (handler == null || bgHandler == null) {
                                return@hook result(proceed())
                            }
                            val message = handler.obtainMessage(100004)
                            val currentTimeMillis = System.currentTimeMillis()
                            val upload = TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo")
                            val download = TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo")
                            val lastTime = mLastTime?.get(controller) ?: 0L
                            var uploadSpeed = 0L
                            var downloadSpeed = 0L
                            if (lastTime != 0L && currentTimeMillis > lastTime) {
                                val uploadBytes = controller.upBytes ?: 0L
                                if (uploadBytes != 0L && upload != 0L && upload > uploadBytes) {
                                    uploadSpeed = ((upload - uploadBytes) * 1000) / (currentTimeMillis - lastTime)
                                }
                                val downloadBytes = controller.downBytes ?: 0L
                                if (downloadBytes != 0L && download != 0L && download > downloadBytes) {
                                    downloadSpeed = ((download - downloadBytes) * 1000) / (currentTimeMillis - lastTime)
                                }
                            }
                            mLastTime?.set(controller, currentTimeMillis)
                            controller.upBytes = upload
                            controller.downBytes = download
                            message.obj = Pair(uploadSpeed, downloadSpeed)
                            message.sendToTarget()
                            bgHandler.removeMessages(200001)
                            bgHandler.sendEmptyMessageDelayed(200001, postDelayed)
                            return@hook result(null)
                        }
                        if (classId == 0 && obtainMessage?.what == 100004) {
                            if (
                                obtainMessage.obj is Pair<*, *> &&
                                (obtainMessage.obj as Pair<*, *>).first is Long &&
                                (obtainMessage.obj as Pair<*, *>).second is Long
                            ) {
                                val uploadSpeed = (obtainMessage.obj as Pair<*, *>).first as Long
                                val downloadSpeed = (obtainMessage.obj as Pair<*, *>).second as Long
                                if (mode != 0) {
                                    updateText?.invoke(
                                        controller,
                                        arrayOf(
                                            formatSpeed(uploadSpeed / 1024.0f, true),
                                            formatSpeed(downloadSpeed / 1024.0f, false)
                                        )
                                    )
                                } else  {
                                    var number = (uploadSpeed + downloadSpeed) / 1024.0f
                                    val stringResId: Int
                                    if (number > 999.0f) {
                                        number /= 1024.0f
                                        stringResId = megabyte_per_second
                                    } else {
                                        stringResId = kilobyte_per_second
                                    }
                                    val unitStr = mContext?.get(controller)?.getString(stringResId)
                                    val speedStr: String = if (number < 10.0f) {
                                        "%.2f".format(number)
                                    } else if (number < 100.0f) {
                                        "%.1f".format(number)
                                    } else {
                                        "%.0f".format(number)
                                    }
                                    updateText?.invoke(
                                        controller,
                                        arrayOf(speedStr, unitStr)
                                    )
                                }
                            }
                            return@hook result(null)
                        }
                        return@hook result(proceed())
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
                else -> null
            }
        } else {
            when (mode) {
                2 -> "↓"
                3 -> "▼"
                4 -> if (finalSpeed == 0.0f) "▽" else "▼"
                else -> null
            }
        }
        if (finalSpeed > 999.0f) {
            finalSpeed /= 1024.0f
            unitMega = true
        } else {
            unitMega = false
        }
        val sb = StringBuilder()
        if (finalSpeed < 10.0f) {
            sb.append("%.2f".format(finalSpeed))
        } else if (finalSpeed < 100.0f) {
            sb.append("%.1f".format(finalSpeed))
        } else {
            sb.append("%.0f".format(finalSpeed))
        }
        if (unitMega) {
            sb.append("M")
        } else {
            sb.append("K")
        }
        when (unitMode) {
            1 -> "B"
            2 -> "B/s"
            else -> null
        }?.let {
            sb.append(it)
        }
        downloadSymbol?.let {
            sb.append(it)
        }
        return sb.toString()
    }
}