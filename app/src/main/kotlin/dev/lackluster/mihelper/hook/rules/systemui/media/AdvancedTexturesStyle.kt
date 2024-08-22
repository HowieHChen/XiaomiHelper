/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references MediaControl-BlurBg <https://github.com/YuKongA/MediaControl-BlurBg/blob/main/app/src/main/kotlin/top/yukonga/mediaControlBlur/MainHook.kt>
 * Copyright (C) 2024 YuKongA

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

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import android.widget.SeekBar
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.utils.MiBlurUtils
import dev.lackluster.mihelper.utils.MiBlurUtils.setBlurRoundRect
import dev.lackluster.mihelper.utils.MiBlurUtils.setMiBackgroundBlendColors
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode

object AdvancedTexturesStyle : YukiBaseHooker() {

    private val mediaControlPanelClass by lazy {
        "com.android.systemui.media.controls.ui.MediaControlPanel".toClass()
    }
    private val miuiMediaControlPanelClass by lazy {
        "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClass()
    }
    private val notificationUtilClass by lazy {
        "com.android.systemui.statusbar.notification.NotificationUtil".toClass()
    }

    override fun onHook() {
        mediaControlPanelClass.method {
            name = "attachPlayer"
        }.hook {
            after {
                val context = this.instance.current().field {
                    name = "mContext"
                    superClass()
                }.any() as? Context ?: return@after
                if (!context.isBackgroundBlurOpened) return@after
                val mediaViewHolder = this.instance.current().field {
                    name = "mMediaViewHolder"
                    superClass()
                }.any() ?: return@after
                val mediaBg = mediaViewHolder.current(true).field { name = "mediaBg" }.any() as ImageView
                val intArray = if (ResourcesUtils.notification_element_blend_shade_colors != 0) {
                    context.resources.getIntArray(ResourcesUtils.notification_element_blend_shade_colors)
                } else if (ResourcesUtils.notification_element_blend_colors != 0) {
                    context.resources.getIntArray(ResourcesUtils.notification_element_blend_colors)
                } else {
                    YLog.error("notification element blend colors not found!")
                    return@after
                }
                val radius = context.resources.getDimensionPixelSize(ResourcesUtils.notification_item_bg_radius)
                MiBlurUtils.setViewBlur(mediaBg, 1)
                mediaBg.setBlurRoundRect(radius)
                mediaBg.setMiBackgroundBlendColors(intArray, 1.0f)
            }
        }

        miuiMediaControlPanelClass.method {
            name = "bindPlayer"
        }.hook {
            after {
                val artwork = this.args(0).any()?.current()?.field { name = "artwork" }?.any() as? Icon ?: return@after

                val context = this.instance.current().field {
                    name = "mContext"
                    superClass()
                }.any() as? Context ?: return@after
                val mediaViewHolder = this.instance.current().field {
                    name = "mMediaViewHolder"
                    superClass()
                }.any() ?: return@after
                val mediaBg = mediaViewHolder.current(true).field { name = "mediaBg" }.any() as ImageView
                val titleText = mediaViewHolder.current(true).field { name = "titleText" }.any() as TextView
                val artistText = mediaViewHolder.current(true).field { name = "artistText" }.any() as TextView
                val seamlessIcon = mediaViewHolder.current(true).field { name = "seamlessIcon" }.any() as ImageView
                val action0 = mediaViewHolder.current(true).field { name = "action0" }.any() as ImageButton
                val action1 = mediaViewHolder.current(true).field { name = "action1" }.any() as ImageButton
                val action2 = mediaViewHolder.current(true).field { name = "action2" }.any() as ImageButton
                val action3 = mediaViewHolder.current(true).field { name = "action3" }.any() as ImageButton
                val action4 = mediaViewHolder.current(true).field { name = "action4" }.any() as ImageButton
                val seekBar = mediaViewHolder.current(true).field { name = "seekBar" }.any() as SeekBar
                val elapsedTimeView = mediaViewHolder.current(true).field { name = "elapsedTimeView" }.any() as TextView
                val totalTimeView = mediaViewHolder.current(true).field { name = "totalTimeView" }.any() as TextView
                val albumView = mediaViewHolder.current(true).field { name = "albumView" }.any() as ImageView

                val grey = if (context.isSystemInDarkMode) Color.parseColor("#80ffffff") else Color.parseColor("#99000000")
                if (!context.isBackgroundBlurOpened) {
                    titleText.setTextColor(Color.WHITE)
                    seamlessIcon.setColorFilter(Color.WHITE)
                    action0.setColorFilter(Color.WHITE)
                    action1.setColorFilter(Color.WHITE)
                    action2.setColorFilter(Color.WHITE)
                    action3.setColorFilter(Color.WHITE)
                    action4.setColorFilter(Color.WHITE)
                    seekBar.progressDrawable?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
                    seekBar.thumb?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
                } else {
                    if (!context.isSystemInDarkMode) {
                        titleText.setTextColor(Color.BLACK)
                        seamlessIcon.setColorFilter(Color.BLACK)
                        action0.setColorFilter(Color.BLACK)
                        action1.setColorFilter(Color.BLACK)
                        action2.setColorFilter(Color.BLACK)
                        action3.setColorFilter(Color.BLACK)
                        action4.setColorFilter(Color.BLACK)
                        seekBar.progressDrawable?.colorFilter = BlendModeColorFilter(Color.BLACK, BlendMode.SRC_IN)
                        seekBar.thumb?.colorFilter = BlendModeColorFilter(Color.BLACK, BlendMode.SRC_IN)
                    } else {
                        titleText.setTextColor(Color.WHITE)
                        seamlessIcon.setColorFilter(Color.WHITE)
                        action0.setColorFilter(Color.WHITE)
                        action1.setColorFilter(Color.WHITE)
                        action2.setColorFilter(Color.WHITE)
                        action3.setColorFilter(Color.WHITE)
                        action4.setColorFilter(Color.WHITE)
                        seekBar.progressDrawable?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
                        seekBar.thumb?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
                    }
                    artistText.setTextColor(grey)
                    elapsedTimeView.setTextColor(grey)
                    totalTimeView.setTextColor(grey)
                }
                val intArray = if (ResourcesUtils.notification_element_blend_shade_colors != 0) {
                    context.resources.getIntArray(ResourcesUtils.notification_element_blend_shade_colors)
                } else if (ResourcesUtils.notification_element_blend_colors != 0) {
                    context.resources.getIntArray(ResourcesUtils.notification_element_blend_colors)
                } else {
                    YLog.error("notification element blend colors not found!")
                    return@after
                }
                mediaBg.setMiBackgroundBlendColors(intArray, 1.0f)

                val artworkLayer = artwork.loadDrawable(context) ?: return@after
                val artworkBitmap = Bitmap.createBitmap(artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(artworkBitmap)
                artworkLayer.setBounds(0, 0, artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight)
                artworkLayer.draw(canvas)
                val resizedBitmap = Bitmap.createScaledBitmap(artworkBitmap, 300, 300, true)

                val radius = 45f
                val newBitmap = Bitmap.createBitmap(resizedBitmap.width, resizedBitmap.height, Bitmap.Config.ARGB_8888)
                val canvas1 = Canvas(newBitmap)

                val paint = Paint()
                val rect = Rect(0, 0, resizedBitmap.width, resizedBitmap.height)
                val rectF = RectF(rect)

                paint.isAntiAlias = true
                canvas1.drawARGB(0, 0, 0, 0)
                paint.color = Color.BLACK
                canvas1.drawRoundRect(rectF, radius, radius, paint)

                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas1.drawBitmap(resizedBitmap, rect, rect, paint)

                albumView.setImageDrawable(BitmapDrawable(context.resources, newBitmap))
            }
        }
    }

    private val Context.isBackgroundBlurOpened get() = notificationUtilClass.method {
        name = "isBackgroundBlurOpened"
        modifiers { isStatic }
        param(ContextClass)
    }.get().boolean(this)
}