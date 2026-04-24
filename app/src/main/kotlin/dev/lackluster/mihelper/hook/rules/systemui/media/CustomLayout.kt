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

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.action0
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.action1
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.action2
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.action3
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.action4
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.actions
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.header_artist
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.header_title
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.icon
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.album_art
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_elapsed_time
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_progress_bar
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_seamless
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_total_time
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaNotificationControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewHolder
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clear
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.setGoneMargin
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.setMargin
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.setVisibility
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.dp

object CustomLayout : StaticHooker() {
    private val ncAlbum by Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.get(false).lazyGet()
    private val ncActionsLeftAligned by Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.get(false).lazyGet()
    private val ncActionsOrder by Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.get(false).lazyGet()
    private val ncHideTime by Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.get(false).lazyGet()
    private val ncHideSeamless by Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.get(false).lazyGet()
    private val ncHeaderMargin by Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.get(false).lazyGet()
    private val ncHeaderPadding by Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.get(false).lazyGet()

    private val diAlbum by Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.get(true).lazyGet()
    private val diActionsLeftAligned by Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.get(true).lazyGet()
    private val diActionsOrder by Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.get(true).lazyGet()
    private val diHideTime by Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.get(true).lazyGet()
    private val diHideSeamless by Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.get(true).lazyGet()
    private val diHeaderMargin by Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.get(true).lazyGet()
    private val diHeaderPadding by Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.get(true).lazyGet()

    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        if (ncAlbum != 0 || ncActionsLeftAligned || ncHideTime || ncHideSeamless || ncHeaderMargin != 21.0f || ncHeaderPadding != 4.0f) {
            clzMiuiMediaNotificationControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.toTyped<Context>()
                val fldNormalLayout = resolve().firstFieldOrNull {
                    name = "normalLayout"
                }?.toTyped<Any>()
                val fldNormalAlbumLayout = resolve().firstFieldOrNull {
                    name = "normalAlbumLayout"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("loadLayout")
                    }
                }?.hook {
                    val ori = proceed()
                    val context = fldContext?.get(thisObject)
                    val normalLayout = fldNormalLayout?.get(thisObject)
                    val normalAlbumLayout = fldNormalAlbumLayout?.get(thisObject)
                    if (context != null && normalLayout != null && normalAlbumLayout != null) {
                        updateLayoutConstraintSet(context, normalLayout, false)
                        if (ncAlbum != 0) {
                            setVisibility?.invoke(normalAlbumLayout, icon, View.GONE)
                        }
                    }
                    result(ori)
                }
            }
            if (ncHideTime) {
                clzMiuiMediaViewControllerImpl?.apply {
                    val holder = resolve().firstFieldOrNull {
                        name = "holder"
                    }?.toTyped<Any>()
                    val fldElapsedTimeView = clzMiuiMediaViewHolder?.resolve()?.firstFieldOrNull {
                        name = "elapsedTimeView"
                    }?.toTyped<TextView>()
                    val fldTotalTimeView = clzMiuiMediaViewHolder?.resolve()?.firstFieldOrNull {
                        name = "totalTimeView"
                    }?.toTyped<TextView>()
                    resolve().firstMethodOrNull {
                        name = "onFullAodStateChanged"
                    }?.hook {
                        val ori = proceed()
                        val vh = holder?.get(thisObject)
                        if (vh != null) {
                            fldElapsedTimeView?.get(vh)?.visibility = View.GONE
                            fldTotalTimeView?.get(vh)?.visibility = View.GONE
                        }
                        result(ori)
                    }
                }
            }
        }
        if (ncHideSeamless) {
            clzMiuiMediaViewControllerImpl?.resolve()?.firstMethodOrNull {
                name = "setSeamless"
            }?.hook {
                result(null)
            }
        }
        if (diAlbum != 0 || diActionsLeftAligned || diHideTime || diHideSeamless || diHeaderMargin != 21.0f || diHeaderPadding != 4.0f) {
            "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaControllerImpl".toClassOrNull()?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.toTyped<Context>()
                val fldNormalLayoutIsland = resolve().firstFieldOrNull {
                    name = "normalLayoutIsland"
                }?.toTyped<Any>()
                val fldMiuiPlayerHolder = resolve().firstFieldOrNull {
                    name = "miuiPlayerHolder"
                }?.toTyped<Any>()
                val fldMiuiDummyPlayerHolder = resolve().firstFieldOrNull {
                    name = "miuiDummyPlayerHolder"
                }?.toTyped<Any>()
                val fldAppIcon = clzMiuiIslandMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = "appIcon"
                }?.toTyped<ImageView>()
                val fldPlayer = clzMiuiIslandMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = "player"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("reInflateView")
                    }
                }?.hook {
                    val ori = proceed()
                    val context = fldContext?.get(thisObject)
                    val normalLayoutIsland = fldNormalLayoutIsland?.get(thisObject)
                    if (context == null || normalLayoutIsland == null) {
                        return@hook result(ori)
                    }
                    updateLayoutConstraintSet(context, normalLayoutIsland, true)
                    val miuiPlayerHolder = fldMiuiPlayerHolder?.get(thisObject)
                    val miuiDummyPlayerHolder = fldMiuiDummyPlayerHolder?.get(thisObject)
                    miuiPlayerHolder?.let { it1 -> applyTo?.invoke(normalLayoutIsland, fldPlayer?.get(it1)) }
                    miuiDummyPlayerHolder?.let { it1 -> applyTo?.invoke(normalLayoutIsland, fldPlayer?.get(it1)) }
                    if (diAlbum != 0) {
                        miuiPlayerHolder?.let { it1 -> fldAppIcon?.get(it1)?.visibility = View.GONE }
                        miuiDummyPlayerHolder?.let { it1 -> fldAppIcon?.get(it1)?.visibility = View.GONE }
                    }
                    result(ori)
                }
            }
            "com.android.systemui.statusbar.notification.mediaisland.PlayerIslandConstraintLayout".toClassOrNull()?.apply {
                val fldNormalLayoutIsland = resolve().firstFieldOrNull {
                    name = "normalLayoutIsland"
                }?.toTyped<Any>()
                resolve().firstConstructor {
                    parameterCount = 3
                }.hook {
                    val ori =proceed()
                    val context = getArg(0) as? Context
                    val normalLayoutIsland = fldNormalLayoutIsland?.get(thisObject)
                    if (context != null && normalLayoutIsland != null) {
                        updateLayoutConstraintSet(context, normalLayoutIsland, true)
                    }
                    result(ori)
                }
            }
        }
        if (diHideSeamless) {
            clzMiuiIslandMediaViewBinderImpl?.resolve()?.firstMethodOrNull {
                name = "setSeamless"
            }?.hook {
                result(null)
            }
        }
    }

    private fun updateLayoutConstraintSet(context: Context, constraintSet: Any, dynamicIsland: Boolean) {
        val standardMargin = 26.dp(context)
        val album = if (dynamicIsland) diAlbum else ncAlbum
        val actionsLeftAligned = if (dynamicIsland) diActionsLeftAligned else ncActionsLeftAligned
        val actionsOrder = if (dynamicIsland) diActionsOrder else ncActionsOrder
        val hideTime = if (dynamicIsland) diHideTime else ncHideTime
        val hideSeamless = if (dynamicIsland) diHideSeamless else ncHideSeamless
        val headerMargin = if (dynamicIsland) diHeaderMargin else ncHeaderMargin
        val headerPadding = if (dynamicIsland) diHeaderPadding else ncHeaderPadding
        if (album == 2) {
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.START, standardMargin)
            setGoneMargin?.invoke(constraintSet, header_artist, ConstraintSet.START, standardMargin)
            setGoneMargin?.invoke(constraintSet, actions, ConstraintSet.TOP, 67.5.dp(context))
            setGoneMargin?.invoke(constraintSet, action0, ConstraintSet.TOP, 78.5.dp(context))
            setVisibility?.invoke(constraintSet, album_art, View.GONE)
        }
        if (headerMargin != 21.0f) {
            val headerMarginTop = headerMargin.dp(context)
            setMargin?.invoke(constraintSet, header_title, ConstraintSet.TOP, headerMarginTop)
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.TOP, headerMarginTop)
        }
        if (headerPadding != 4.0f) {
            setMargin?.invoke(constraintSet, header_artist, ConstraintSet.TOP, headerPadding.dp(context))
        }
        when (actionsOrder) {
            1 -> {
                connect?.invoke(constraintSet, action1, ConstraintSet.LEFT,  actions, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action1, ConstraintSet.RIGHT,  action2, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action2, ConstraintSet.LEFT,  action1, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action2, ConstraintSet.RIGHT,  action3, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action3, ConstraintSet.LEFT,  action2, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action3, ConstraintSet.RIGHT,  action0, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action0, ConstraintSet.LEFT,  action3, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action0, ConstraintSet.RIGHT,  action4, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action4, ConstraintSet.LEFT,  action0, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action4, ConstraintSet.RIGHT,  actions, ConstraintSet.RIGHT)
                setMargin?.invoke(constraintSet, action0, ConstraintSet.START, 0)
                setMargin?.invoke(constraintSet, action1, ConstraintSet.START, 6.dp(context))
            }
            2 -> {
                connect?.invoke(constraintSet, action2, ConstraintSet.LEFT,  actions, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action2, ConstraintSet.RIGHT,  action1, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action1, ConstraintSet.LEFT,  action2, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action1, ConstraintSet.RIGHT,  action3, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action3, ConstraintSet.LEFT,  action1, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action3, ConstraintSet.RIGHT,  action0, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action0, ConstraintSet.LEFT,  action3, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action0, ConstraintSet.RIGHT,  action4, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, action4, ConstraintSet.LEFT,  action0, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, action4, ConstraintSet.RIGHT,  actions, ConstraintSet.RIGHT)
                setMargin?.invoke(constraintSet, action0, ConstraintSet.START, 0)
                setMargin?.invoke(constraintSet, action2, ConstraintSet.START, 6.dp(context))
            }
        }
        if (actionsLeftAligned) {
            clear?.invoke(constraintSet, action4, ConstraintSet.RIGHT)
        }
        if (hideTime) {
            connect?.invoke(constraintSet,
                media_progress_bar, ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.LEFT
            )
            connect?.invoke(constraintSet,
                media_progress_bar, ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT
            )
            setMargin?.invoke(constraintSet, media_progress_bar, ConstraintSet.LEFT, standardMargin)
            setMargin?.invoke(constraintSet, media_progress_bar, ConstraintSet.RIGHT, standardMargin)
        }
        if (hideSeamless) {
            setVisibility?.invoke(constraintSet, media_seamless, View.GONE)
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.END, standardMargin)
            setGoneMargin?.invoke(constraintSet, header_artist, ConstraintSet.END, standardMargin)
        }
        if (hideTime) {
            setVisibility?.invoke(constraintSet, media_elapsed_time, View.GONE)
            setVisibility?.invoke(constraintSet, media_total_time, View.GONE)
        }
    }
}