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

package dev.lackluster.mihelper.hook.rules.systemui.notif

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object MiuiXExpandButton : YukiBaseHooker() {
    private var expandButtonSize: Int? = null
    private var expand: Drawable? = null
    private var shrink: Drawable? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.MIUIX_EXPAND_BUTTON) {
            "com.android.internal.widget.NotificationExpandButton".toClassOrNull()?.apply {
                val mIconView = resolve().firstFieldOrNull {
                    name = "mIconView"
                }
                val mPillDrawable = resolve().firstFieldOrNull {
                    name = "mPillDrawable"
                }
                val mExpanded = resolve().firstFieldOrNull {
                    name = "mExpanded"
                }
                resolve().firstMethodOrNull {
                    name = "onFinishInflate"
                }?.hook {
                    after {
                        val iconView = mIconView?.copy()?.of(this.instance)?.get<ImageView>() ?: return@after
                        mPillDrawable?.copy()?.of(this.instance)?.set(Color.TRANSPARENT.toDrawable())
                        (iconView.parent as? ViewGroup)?.background = null
                        iconView.setPadding(0, 0, 0, 0)
                        if (expandButtonSize == null || expand == null || shrink == null) {
                            val context = iconView.context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                            expand = context.getDrawable(R.drawable.miuix_notification_btn_expand)
                            shrink = context.getDrawable(R.drawable.miuix_notification_btn_shrink)
                            expandButtonSize = context.resources.getDimensionPixelSize(R.dimen.miuix_btn_size)
                        }
                        expandButtonSize?.let {
                            iconView.layoutParams = iconView.layoutParams.apply {
                                width = it
                                height = it
                            }
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "updateExpandedState"
                }?.hook {
                    before {
                        val expanded = mExpanded?.copy()?.of(this.instance)?.get<Boolean>()
                        val iconView = mIconView?.copy()?.of(this.instance)?.get<ImageView>()
                        if (expanded == null || iconView == null || shrink == null || expand == null) return@before
                        iconView.setImageDrawable(if (expanded) shrink else expand)
                        this.result = null
                    }
                }
            }
        }
    }
}