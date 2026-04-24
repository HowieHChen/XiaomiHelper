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
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object MiuiXExpandButton : StaticHooker() {
    private var expandButtonSizePx: Int? = null
    private var expandDrawable: Drawable? = null
    private var shrinkDrawable: Drawable? = null

    private val pillDrawable by lazy {
        Color.TRANSPARENT.toDrawable()
    }

    override fun onInit() {
        updateSelfState(Preferences.SystemUI.NotifCenter.MIUIX_EXPAND_BUTTON.get())
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        "com.android.internal.widget.NotificationExpandButton".toClassOrNull()?.apply {
            val mIconView = resolve().firstFieldOrNull {
                name = "mIconView"
            }?.toTyped<ImageView>()
            val mPillDrawable = resolve().firstFieldOrNull {
                name = "mPillDrawable"
            }?.toTyped<Drawable>()
            val mExpanded = resolve().firstFieldOrNull {
                name = "mExpanded"
            }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "onFinishInflate"
            }?.hook {
                val ori = proceed()
                val iconView = mIconView?.get(thisObject)
                if (iconView != null) {
                    mPillDrawable?.set(thisObject, pillDrawable)
                    (iconView.parent as? ViewGroup)?.background = null
                    iconView.setPadding(0, 0, 0, 0)
                    if (expandButtonSizePx == null || expandDrawable == null || shrinkDrawable == null) {
                        val context = iconView.context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                        expandDrawable = context.getDrawable(R.drawable.miuix_notification_btn_expand)
                        shrinkDrawable = context.getDrawable(R.drawable.miuix_notification_btn_shrink)
                        expandButtonSizePx = context.resources.getDimensionPixelSize(R.dimen.miuix_btn_size)
                    }
                    expandButtonSizePx?.let {
                        iconView.layoutParams = iconView.layoutParams.apply {
                            width = it
                            height = it
                        }
                    }
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "updateExpandedState"
            }?.hook {
                val expanded = mExpanded?.get(thisObject)
                val iconView = mIconView?.get(thisObject)
                if (expanded != null && iconView != null && shrinkDrawable != null && expandDrawable != null) {
                    iconView.setImageDrawable(if (expanded) shrinkDrawable else expandDrawable)
                }
                result(null)
            }
        }
    }
}