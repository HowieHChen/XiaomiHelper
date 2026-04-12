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

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_control_bg_radius
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.notification_item_bg_radius
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clone
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.ctorConstraintSet
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.setVisibility
import dev.lackluster.mihelper.hook.rules.systemui.compat.PairCompat
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomProgressBar.realSeekBar
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.clzMediaData
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.ctorColorScheme
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.defaultColorConfig
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.enumStyleContent
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeNeutral1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeNeutral2
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent2
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldTonalPaletteAllShades
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getScaledBackground
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.releaseCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.BgProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.BlurredCoverProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.CoverArtProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.RadialGradientProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.LinearGradientProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MediaViewColorConfig
import dev.lackluster.mihelper.hook.rules.systemui.media.data.MiuiMediaViewHolderWrapper
import dev.lackluster.mihelper.hook.rules.systemui.media.data.PlayerConfig
import dev.lackluster.mihelper.hook.rules.systemui.media.data.PlayerType
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.HostExecutor
import dev.lackluster.mihelper.utils.factory.getResId

object CustomBackground : StaticHooker() {
    private var Any.viewHolderWrapper by extraOf<MiuiMediaViewHolderWrapper>("KEY_VIEW_HOLDER_WRAPPER")
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(false).lazyGet()
    private val ncBackgroundAnim by Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.get(false).lazyGet()
    private val diBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(true).lazyGet()
    private val diBackgroundAnim by Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.get(true).lazyGet()

    private var ncProcessor: BgProcessor? = null
    private var diProcessor: BgProcessor? = null

    private val ncUseNotifCornerRadius by Preferences.SystemUI.MediaControl.NotifCenter.BG_NOTIF_CORNER.lazyGet()
    private val ncOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(p0: View?, p1: Outline?) {
            if (p0 == null) return
            getNotifCenterCornerRadius(p0.context)?.let {
                p1?.setRoundRect(0, 0, p0.width, p0.height, p0.context.resources.getDimension(it))
                p0.clipToOutline = true
            }
        }
    }

    private val ncPlayerConfig = PlayerConfig()
    private val diPlayerConfig = PlayerConfig()
    private val diPlayerConfigDummy = PlayerConfig()

    private val fldArtwork by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "artwork"
        }?.toTyped<Icon>()
    }
    private val fldPackageName by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "packageName"
        }?.toTyped<String>()
    }

    override fun onInit() {
        updateSelfState(true)
        attach(AlwaysDark)
        attach(AmbientLight)
        attach(FlipTinyScreen) // Not fully verified
        attach(MediaControlBgFactory)
    }

    override fun onHook() {
        onHookCornerRadius()
        if (ncBackgroundStyle !in 1..4 && diBackgroundStyle !in 1..4) return
        onHookNotificationCenter()
        onHookDynamicIsland()
    }

    private fun getNotifCenterCornerRadius(context: Context): Int? {
        if (notification_item_bg_radius == 0 || media_control_bg_radius == 0) {
            notification_item_bg_radius = context.getResId("notification_item_bg_radius", "dimen", Scope.SYSTEM_UI)
            media_control_bg_radius = context.getResId("media_control_bg_radius", "dimen", Scope.SYSTEM_UI)
        }
        return (if (ncUseNotifCornerRadius) notification_item_bg_radius else media_control_bg_radius).takeIf { it != 0 }
    }

    private fun onHookCornerRadius() {
        if (ncUseNotifCornerRadius) {
            val clzNotifiFullAodController = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
            val fldMediaRadius = clzNotifiFullAodController?.resolve()?.firstFieldOrNull {
                name = "mMediaRadius"
            }?.toTyped<Float>()
            val fldContext = clzNotifiFullAodController?.resolve()?.firstFieldOrNull {
                name = "mContext"
            }?.toTyped<Context>()
            "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController_Factory".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "newInstance"
                }?.hook {
                    val ori = proceed()
                    val context = getArg(0) as? Context
                    if (context != null) {
                        getNotifCenterCornerRadius(context)?.let {
                            fldMediaRadius?.set(ori, context.resources.getDimension(it))
                        }
                    }
                    result(ori)
                }
            }
            "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController$3".toClassOrNull()?.apply {
                val outer = resolve().firstFieldOrNull {
                    name = "this$0"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "onDensityOrFontScaleChanged"
                }?.hook {
                    val ori = proceed()
                    val controller = outer?.get(thisObject)
                    val context = controller?.let { fldContext?.get(it) }
                    if (context != null) {
                        getNotifCenterCornerRadius(context)?.let {
                            fldMediaRadius?.set(controller, context.resources.getDimension(it))
                        }
                    }
                    result(ori)
                }
            }
            $$"com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl$mediaFullAodListener$1".toClassOrNull()?.apply {
                val fldOuter = resolve().firstFieldOrNull {
                    name = $$"this$0"
                }?.toTyped<Any>()
                val fldHolder = clzMiuiMediaViewControllerImpl?.resolve()?.firstFieldOrNull {
                    name = "holder"
                }?.toTyped<Any>()
                val fldMediaBg = getMediaViewHolderField("mediaBg", false)?.toTyped<ImageView>()
                resolve().firstMethodOrNull {
                    name = "updateFullAodAnimState"
                }?.hook {
                    val ori = proceed()
                    val outer = fldOuter?.get(thisObject)
                    val holder = fldHolder?.get(outer) ?: return@hook result(ori)
                    val mediaBg = fldMediaBg?.get(holder)
                    mediaBg?.outlineProvider = ncOutlineProvider
                    mediaBg?.clipToOutline = true
                    result(ori)
                }
            }
        }
    }

    private fun onHookNotificationCenter() {
        ncProcessor = when (ncBackgroundStyle) {
            1 -> CoverArtProcessor(
                useAnim = ncBackgroundAnim
            )
            2 -> BlurredCoverProcessor(
                blurRadius = Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.get(false).get(),
                useAnim = ncBackgroundAnim
            )
            3 -> RadialGradientProcessor(
                useAnim = ncBackgroundAnim
            )
            4 -> LinearGradientProcessor(
                allowReverse = Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.get(false).get(),
                useAnim = ncBackgroundAnim
            )
            else -> return
        }
        clzMiuiMediaViewControllerImpl?.apply {
            val fldContext = resolve().firstFieldOrNull {
                name = "context"
            }?.toTyped<Context>()
            val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                name = "isArtWorkUpdate"
            }?.toTyped<Boolean>()
            val fldHolder = resolve().firstFieldOrNull {
                name = "holder"
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "updateForegroundColors"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "updateMediaBackground"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "detach"
            }?.hook {
                val ori = proceed()
                finiMediaViewHolder(false)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "attach"
            }?.hook {
                val ori = proceed()
                fldHolder?.get(thisObject)?.let { holder ->
                    getMediaViewHolderWrapper(holder, false)
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "bindMediaData"
            }?.hook {
                val ori = proceed()
                val mediaData = getArg(0) ?: return@hook result(ori)
                val context = fldContext?.get(thisObject)
                val holder = fldHolder?.get(thisObject) ?: return@hook result(ori)
                val artwork = fldArtwork?.get(mediaData)
                val packageName = fldPackageName?.get(mediaData)
                val holderWrapper = getMediaViewHolderWrapper(holder, false)

                if (context == null || packageName == null || holderWrapper == null) {
                    return@hook result(ori)
                }

                val isArtWorkUpdate =
                    fldIsArtWorkUpdate?.get(thisObject) == true || ncPlayerConfig.mCurrentPkgName != packageName || !ncPlayerConfig.mIsArtworkBound
                if (isArtWorkUpdate) {
                    updateBackground(context, artwork, packageName, holderWrapper, PlayerType.NOTIFICATION_CANTER)
                }
                result(ori)
            }
        }
    }

    private fun onHookDynamicIsland() {
        diProcessor = when (diBackgroundStyle) {
            1 -> CoverArtProcessor(
                useAnim = diBackgroundAnim
            )
            2 -> BlurredCoverProcessor(
                blurRadius = Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.get(true).get(),
                useAnim = diBackgroundAnim
            )
            3 -> RadialGradientProcessor(
                useAnim = diBackgroundAnim
            )
            4 -> LinearGradientProcessor(
                allowReverse = Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.get(true).get(),
                useAnim = diBackgroundAnim
            )
            else -> return
        }
        clzMiuiIslandMediaViewBinderImpl?.apply {
            val fldContext = resolve().firstFieldOrNull {
                name = "context"
            }?.toTyped<Context>()
            val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                name = "isArtWorkUpdate"
            }?.toTyped<Boolean>()
            val fldHolder = resolve().firstFieldOrNull {
                name = "holder"
            }?.toTyped<Any>()
            val fldDummyHolder = resolve().firstFieldOrNull {
                name = "dummyHolder"
            }?.toTyped<Any>()
            val fldMediaBgTransYOffset = resolve().optional(true).firstFieldOrNull {
                name = "mediaBgTransYOffset"
            }?.toTyped<Float>()
            resolve().firstMethodOrNull {
                name = "updateForegroundColors"
            }?.hook {
                result(null)
            }
            resolve().firstMethodOrNull {
                name = "detach"
            }?.hook {
                val ori = proceed()
                finiMediaViewHolder(true)
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "attach"
            }?.hook {
                val ori = proceed()
                fldHolder?.get(thisObject)?.let { holder ->
                    getMediaViewHolderWrapper(holder, true)
                }
                fldDummyHolder?.get(thisObject)?.let { dummyHolder ->
                    getMediaViewHolderWrapper(dummyHolder, true)
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "bindMediaData"
            }?.hook {
                val ori = proceed()
                val mediaData = getArg(0) ?: return@hook result(ori)
                val context = fldContext?.get(thisObject)
                val holder = fldHolder?.get(thisObject) ?: return@hook result(ori)
                val dummyHolder = fldDummyHolder?.get(thisObject) ?: return@hook result(ori)
                val artwork = fldArtwork?.get(mediaData)
                val packageName = fldPackageName?.get(mediaData)
                val holderWrapper = getMediaViewHolderWrapper(holder, true)
                val dummyHolderWrapper = getMediaViewHolderWrapper(dummyHolder, true)

                if (context == null || packageName == null || holderWrapper == null || dummyHolderWrapper == null) {
                    return@hook result(ori)
                }
                val isArtWorkUpdate =
                    fldIsArtWorkUpdate?.get(thisObject) == true || diPlayerConfig.mCurrentPkgName != packageName || !diPlayerConfig.mIsArtworkBound
                if (isArtWorkUpdate) {
                    updateBackground(context, artwork, packageName, holderWrapper, PlayerType.DYNAMIC_ISLAND)
                    updateBackground(context, artwork, packageName, dummyHolderWrapper, PlayerType.DUMMY_DYNAMIC_ISLAND)
                }
                val mediaBgTransYOffset = fldMediaBgTransYOffset?.get(thisObject)
                if (mediaBgTransYOffset != null && mediaBgTransYOffset != 0.0f) {
                    val height = dummyHolderWrapper.mediaBg.height
                    dummyHolderWrapper.mediaBg.scaleY = (height + mediaBgTransYOffset) / height
                }
                result(ori)
            }
        }
        $$"com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewBinderImpl$attach$4$1".toClassOrNull()?.apply {
//            val fldHolder = resolve().firstFieldOrNull {
//                name = $$"$holder"
//            }?.self
            val fldDummyHolder = resolve().firstFieldOrNull {
                name = $$"$dummyHolder"
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "emit"
            }?.hook {
                val ori = proceed()
                val pair = getArg(0)
                val action = PairCompat.getFirst(pair) as? String
                val data = PairCompat.getSecond(pair) as? Bundle
//                    val mediaBgView = fldHolder?.get(thisObject)?.let { it1 -> getMediaViewHolderWrapper(it1, true)?.mediaBg  } ?: return@after
                val dummyMediaBgView = fldDummyHolder?.get(thisObject)?.let { it1 -> getMediaViewHolderWrapper(it1, true)?.mediaBg } ?: return@hook result(ori)
                when (action) {
                    "pull_down_type_start" -> {
                        dummyMediaBgView.pivotY = 0.0f
                    }
                    "pull_down_type_update" -> {
                        val pullDownOffset = data?.getFloat("pull_down_action_offset_y", 0.0f) ?: 0.0f
                        val height = dummyMediaBgView.height
                        dummyMediaBgView.scaleY = (height + pullDownOffset) / height
                    }
                    "pull_down_type_finish" -> {

                    }
                }
                result(ori)
            }
        }
    }

    private fun getMediaViewHolderWrapper(mMediaViewHolder: Any, isDynamicIsland: Boolean): MiuiMediaViewHolderWrapper? {
        mMediaViewHolder.viewHolderWrapper?.let {
            return it
        }
        val titleText = getMediaViewHolderField("titleText", isDynamicIsland)?.get(mMediaViewHolder) as? TextView ?: return null
        val artistText = getMediaViewHolderField("artistText", isDynamicIsland)?.get(mMediaViewHolder) as? TextView ?: return null
        val seamlessIcon = getMediaViewHolderField("seamlessIcon", isDynamicIsland)?.get(mMediaViewHolder) as? ImageView ?: return null
        val action0 = getMediaViewHolderField("action0", isDynamicIsland)?.get(mMediaViewHolder) as? ImageButton ?: return null
        val action1 = getMediaViewHolderField("action1", isDynamicIsland)?.get(mMediaViewHolder) as? ImageButton ?: return null
        val action2 = getMediaViewHolderField("action2", isDynamicIsland)?.get(mMediaViewHolder) as? ImageButton ?: return null
        val action3 = getMediaViewHolderField("action3", isDynamicIsland)?.get(mMediaViewHolder) as? ImageButton ?: return null
        val action4 = getMediaViewHolderField("action4", isDynamicIsland)?.get(mMediaViewHolder) as? ImageButton ?: return null
        val seekBar = getMediaViewHolderField("seekBar", isDynamicIsland)?.get(mMediaViewHolder) as? SeekBar ?: return null
        val realSeekBar = mMediaViewHolder.realSeekBar
        val elapsedTimeView = getMediaViewHolderField("elapsedTimeView", isDynamicIsland)?.get(mMediaViewHolder) as? TextView ?: return null
        val totalTimeView = getMediaViewHolderField("totalTimeView", isDynamicIsland)?.get(mMediaViewHolder) as? TextView ?: return null
        val albumView = getMediaViewHolderField("albumImageView", isDynamicIsland)?.get(mMediaViewHolder) as? ImageView ?: return null
        val mediaBg: ImageView
        if (isDynamicIsland) {
            val mediaBgView = getMediaViewHolderField("mediaBgView", true)?.get(mMediaViewHolder) as? View
            mediaBg = ImageView(titleText.context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                id = media_bg
                layoutParams = ViewGroup.LayoutParams(0, 0)
            }.also {
                val parent = titleText.parent as? ViewGroup ?: return@also
                val index: Int
                if (mediaBgView != null) {
                    index = (parent.indexOfChild(mediaBgView) + 1).coerceIn(0, parent.childCount)
                    it.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(p0: View?, p1: Outline?) {
                            val cornerRadius = p0?.context?.resources?.getDimension(media_control_bg_radius) ?: return
                            p1?.setRoundRect(0, 0, p0.width, p0.height, cornerRadius)
                        }
                    }
                    it.clipToOutline = true
                } else {
                    index = 0
                }
                parent.addView(it, index)
                parent.removeView(mediaBgView)
                val constraintSet = ctorConstraintSet.newInstance()
                clone?.invoke(constraintSet, parent)
                connect?.invoke(constraintSet, media_bg, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                connect?.invoke(constraintSet, media_bg, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect?.invoke(constraintSet, media_bg, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                connect?.invoke(constraintSet, media_bg, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                setVisibility?.invoke(constraintSet, media_bg_view, View.GONE)
                applyTo?.invoke(constraintSet, parent)
            }
        } else {
            mediaBg = getMediaViewHolderField("mediaBg", false)?.get(mMediaViewHolder) as? ImageView ?: return null
            mediaBg.outlineProvider = ncOutlineProvider
            mediaBg.clipToOutline = true
            mediaBg.invalidateOutline()
            if (ncBackgroundAnim) {
                mediaBg.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        }

        return MiuiMediaViewHolderWrapper(
            mMediaViewHolder.hashCode(),
            titleText,
            artistText,
            albumView,
            mediaBg,
            seamlessIcon,
            action0,
            action1,
            action2,
            action3,
            action4,
            elapsedTimeView,
            totalTimeView,
            realSeekBar ?: seekBar,
        ).also {
            mMediaViewHolder.viewHolderWrapper = it
        }
    }

    private fun finiMediaViewHolder(isDynamicIsland: Boolean) {
        if (isDynamicIsland) {
            diPlayerConfig.mArtworkDrawable = null
            diPlayerConfig.mIsArtworkBound = false
            diPlayerConfig.mCurrentPkgName = ""
            diPlayerConfigDummy.mArtworkDrawable = null
            diPlayerConfigDummy.mIsArtworkBound = false
            diPlayerConfigDummy.mCurrentPkgName = ""
        } else {
            ncPlayerConfig.mArtworkDrawable = null
            ncPlayerConfig.mIsArtworkBound = false
            ncPlayerConfig.mCurrentPkgName = ""
        }
        releaseCachedWallpaperColor()
    }

    private fun updateForegroundColors(holder: MiuiMediaViewHolderWrapper, colorConfig: MediaViewColorConfig) {
        val primaryColorStateList = ColorStateList.valueOf(colorConfig.textPrimary)
        holder.titleText.setTextColor(colorConfig.textPrimary)
        holder.artistText.setTextColor(colorConfig.textSecondary)
        holder.seamlessIcon.imageTintList = primaryColorStateList
        holder.action0.imageTintList = primaryColorStateList
        holder.action1.imageTintList = primaryColorStateList
        holder.action2.imageTintList = primaryColorStateList
        holder.action3.imageTintList = primaryColorStateList
        holder.action4.imageTintList = primaryColorStateList
        holder.seekBar.thumbTintList = primaryColorStateList
        holder.seekBar.progressTintList = primaryColorStateList
        holder.seekBar.progressBackgroundTintList = primaryColorStateList
        holder.elapsedTimeView.setTextColor(colorConfig.textPrimary)
        holder.totalTimeView.setTextColor(colorConfig.textPrimary)
    }

    @Suppress("UNCHECKED_CAST")
    fun updateBackground(context: Context, artwork: Icon?, pkgName: String, holder: MiuiMediaViewHolderWrapper, type: PlayerType) {
        val artworkLayer = artwork?.loadDrawable(context) ?: return
        val processor: BgProcessor
        val playerConfig: PlayerConfig
        val allowResizeAnim: Boolean
        when (type) {
            PlayerType.NOTIFICATION_CANTER -> {
                processor = ncProcessor ?: return
                playerConfig = ncPlayerConfig
                allowResizeAnim = true
            }
            PlayerType.DYNAMIC_ISLAND -> {
                processor = diProcessor ?: return
                playerConfig = diPlayerConfig
                allowResizeAnim = false
            }
            PlayerType.DUMMY_DYNAMIC_ISLAND -> {
                processor = diProcessor ?: return
                playerConfig = diPlayerConfigDummy
                allowResizeAnim = false
            }
        }
        val reqId = playerConfig.mArtworkNextBindRequestId++
        // Update album cover image
        holder.albumView.setImageDrawable(artworkLayer)
        // Capture width & height from views in foreground for artwork scaling in background
        val width: Int
        val height: Int
        if (holder.mediaBg.measuredWidth == 0 || holder.mediaBg.measuredHeight == 0) {
            if (playerConfig.lastWidth == 0 || playerConfig.lastHeight == 0) {
                width = artworkLayer.intrinsicWidth
                height = artworkLayer.intrinsicHeight
            } else {
                width = playerConfig.lastWidth
                height = playerConfig.lastHeight
            }
        } else {
            width = holder.mediaBg.measuredWidth
            height = holder.mediaBg.measuredHeight
            playerConfig.lastWidth = width
            playerConfig.lastHeight = height
        }
        // Override colors set by the original method
        updateForegroundColors(holder, playerConfig.mCurrColorConfig)

        HostExecutor.execute(
            tag = type,
            backgroundTask = {
                // Album art
                val mutableColorScheme: Any?
                val artworkDrawable: Drawable
                val wallpaperColors = context.getCachedWallpaperColor(artwork)
                if (wallpaperColors != null) {
                    mutableColorScheme = ctorColorScheme?.newInstance(wallpaperColors, true, enumStyleContent)
                    artworkDrawable = context.getScaledBackground(artwork, height, height) ?: Color.TRANSPARENT.toDrawable()
                } else {
                    // If there's no artwork, use colors from the app icon
                    artworkDrawable = Color.TRANSPARENT.toDrawable()
                    try {
                        val icon = context.packageManager.getApplicationIcon(pkgName)
                        mutableColorScheme = ctorColorScheme?.newInstance(WallpaperColors.fromDrawable(icon), true, enumStyleContent)?: throw Exception()
                    } catch (e: Exception) {
                        e(e) { "application not found!" }
                        return@execute null
                    }
                }
                var colorConfig = defaultColorConfig.copy()
                if (mutableColorScheme != null) {
                    val neutral1 = fldTonalPaletteAllShades?.get(fldColorSchemeNeutral1!!.get(mutableColorScheme)) as? List<Int>
                    val neutral2 = fldTonalPaletteAllShades?.get(fldColorSchemeNeutral2!!.get(mutableColorScheme)) as? List<Int>
                    val accent1 = fldTonalPaletteAllShades?.get(fldColorSchemeAccent1!!.get(mutableColorScheme)) as? List<Int>
                    val accent2 = fldTonalPaletteAllShades?.get(fldColorSchemeAccent2!!.get(mutableColorScheme)) as? List<Int>
                    if (neutral1 != null && neutral2 != null && accent1 != null && accent2 != null) {
                        colorConfig = processor.convertToColorConfig(artworkDrawable, neutral1, neutral2, accent1, accent2)
                    }
                }
                val processedArtwork = processor.processAlbumCover(
                    artworkDrawable,
                    colorConfig,
                    context,
                    width,
                    height
                )

                return@execute Pair(colorConfig, processedArtwork)
            },
            runOnMain = true
        ) { pair ->
            if (reqId < playerConfig.mArtworkBoundId) {
                return@execute
            }

            val colorConfig = pair.first
            val processedArtwork = pair.second

            if (playerConfig.mArtworkDrawable == null) {
                playerConfig.mArtworkDrawable = processor.createBackground(processedArtwork, colorConfig).apply {
                    setResizeAnim(allowResizeAnim)
                }
            }
            playerConfig.mArtworkDrawable?.setBounds(0, 0, width, height)
            playerConfig.mCurrentPkgName = pkgName

            playerConfig.mArtworkBoundId = reqId

            if (colorConfig != playerConfig.mCurrColorConfig) {
                updateForegroundColors(holder, colorConfig)
                playerConfig.mCurrColorConfig = colorConfig
            }

            // Bind the album view to the artwork or a transition drawable
            holder.mediaBg.setPadding(0, 0, 0, 0)
            holder.mediaBg.setImageDrawable(playerConfig.mArtworkDrawable)

            if (holder.mediaBg.isShown) {
                playerConfig.mArtworkDrawable?.updateAlbumCover(processedArtwork, colorConfig, false)
            } else {
                playerConfig.mArtworkDrawable?.updateAlbumCover(processedArtwork, colorConfig, true)
            }
            playerConfig.mIsArtworkBound = true
        }
    }
}