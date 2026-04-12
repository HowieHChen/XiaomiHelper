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
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg_view
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clone
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.ctorConstraintSet
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.clzMediaData
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.media.drawable.AmbientLightDrawable
import dev.lackluster.mihelper.hook.rules.systemui.compat.PairCompat
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.ctorColorScheme
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.enumStyleContent
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldTonalPaletteAllShades
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.releaseCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.data.PlayerType
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.HostExecutor
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode

internal object AmbientLight : StaticHooker() {
    private var Any.mediaBgView by extraOf<ImageView>("KEY_MEDIA_BG_VIEW")
    private var Any.lightMediaBgColor by extraOf<Int>("KEY_MEDIA_BG_COLOR_LIGHT")
    private var Any.darkMediaBgColor by extraOf<Int>("KEY_MEDIA_BG_COLOR_DARK")
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(false).lazyGet()
    private val diBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(true).lazyGet()
    private val ncAmbientLight by Preferences.SystemUI.MediaControl.NotifCenter.BG_AMBIENT_LIGHT.lazyGet()
    private val diAmbientLightType by Preferences.SystemUI.MediaControl.DynamicIsland.BG_AMBIENT_LIGHT_TYPE.lazyGet()
    private val ncAmbientColorOpt by Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.get(false).lazyGet()
    private val diAmbientColorOpt by Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.get(true).lazyGet()

    var ncCurrentPkgName = ""
    var ncIsArtworkBound = false
    var diCurrentPkgName = ""
    var diIsArtworkBound = false
    var inFullAod = false

    private val fldIsPlaying by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "isPlaying"
        }?.toTyped<Boolean>()
    }
    private val clzMiPalette by "miuix.mipalette.MiPalette".lazyClassOrNull()
    private val metGetMainColorHCT by lazy {
        clzMiPalette?.resolve()?.firstMethodOrNull {
            name = "getMainColorHCT"
            parameters(Bitmap::class)
            modifiers(Modifiers.STATIC)
        }?.toTyped<Int>()
    }
    private val metDrawable2Bitmap by lazy {
        "com.miui.utils.DrawableUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "drawable2Bitmap"
            parameters(Drawable::class)
            modifiers(Modifiers.STATIC)
        }?.toTyped<Bitmap>()
    }
    val metAcquireApplicationIcon by lazy {
        "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "acquireApplicationIcon"
            parameterCount = 2
            modifiers(Modifiers.STATIC)
        }?.toTyped<Drawable>()
    }
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
    }

    override fun onHook() {
        if (ncBackgroundStyle == 0 && ncAmbientLight) {
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
                val fldMediaData = resolve().firstFieldOrNull {
                    name = "mediaData"
                }?.toTyped<Any>()
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.toTyped<Any>()
                val fldEnableFullAod = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mEnableFullAod"
                    }?.toTyped<Boolean>()
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    val ori = proceed()
                    fldHolder?.get(thisObject)?.let { holder ->
                        (getNewMediaBgView(holder, false)?.drawable as? AmbientLightDrawable)?.stop()
                    }
                    ncCurrentPkgName = ""
                    ncIsArtworkBound = false
                    releaseCachedWallpaperColor()
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    val ori = proceed()
                    val controllerImpl = thisObject
                    fldHolder?.get(controllerImpl)?.let { holder ->
                        getNewMediaBgView(holder, false)
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "bindMediaData"
                }?.hook {
                    val ori = proceed()
                    val mediaData = getArg(0)
                    val packageName = fldPackageName?.get(mediaData)
                    val isArtWorkUpdate = fldIsArtWorkUpdate?.get(thisObject) == true || ncCurrentPkgName != packageName || !ncIsArtworkBound

                    val holder = fldHolder?.get(thisObject)
                    val context = fldContext?.get(thisObject)

                    if (mediaData == null || packageName == null || holder == null || context == null) {
                        return@hook result(ori)
                    }

                    if (isArtWorkUpdate) {
                        val fullAodControllerLazy = fldFullAodController?.get(thisObject)
                        val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        val enableFullAod = fldEnableFullAod?.get(fullAodController) ?: false

                        updateColor(context, mediaData, packageName, holder, PlayerType.NOTIFICATION_CANTER, context.isSystemInDarkMode || enableFullAod)
                    } else {
                        val mediaBgView = getNewMediaBgView(holder, false)
                        val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable
                        val isPlaying = fldIsPlaying?.get(mediaData) ?: false
                        if (!inFullAod && isPlaying) {
                            ambientLightDrawable?.resume()
                        } else {
                            ambientLightDrawable?.pause()
                        }
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "onFullAodStateChanged"
                }?.hook {
                    val ori = proceed()
                    val holder = fldHolder?.get(thisObject) ?: return@hook result(ori)
                    val ambientLightDrawable = getNewMediaBgView(holder, false)?.drawable as? AmbientLightDrawable ?: return@hook result(ori)
                    val mediaData = fldMediaData?.get(thisObject) ?: return@hook result(ori)
                    val toFullAod = getArg(0) as? Boolean ?: false
                    if (toFullAod != inFullAod) {
                        ambientLightDrawable.animateNextResize()
                    }
                    inFullAod = toFullAod
                    val isPlaying = fldIsPlaying?.get(mediaData) ?: false
                    if (!toFullAod && isPlaying) {
                        ambientLightDrawable.resume()
                    } else {
                        ambientLightDrawable.pause()
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "updateForegroundColors"
                }?.hook {
                    val holder = fldHolder?.get(thisObject) ?: return@hook result(proceed())
                    val mediaBgView = getNewMediaBgView(holder, false)
                    val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable ?: return@hook result(proceed())
                    val context = fldContext?.get(thisObject) ?: return@hook result(proceed())
                    val fullAodControllerLazy = fldFullAodController?.get(thisObject)
                    val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                    val enableFullAod = fullAodController?.let { fldEnableFullAod?.get(it) } ?: false
                    val isDark = enableFullAod || context.isSystemInDarkMode
                    if (ncAmbientColorOpt) {
                        val light = holder.lightMediaBgColor  ?: Color.TRANSPARENT
                        val dark = holder.darkMediaBgColor ?: Color.TRANSPARENT
                        ambientLightDrawable.setGradientColor(if (isDark) dark else light, !mediaBgView.isShown)
                    }
                    ambientLightDrawable.setLightMode(!isDark)
                    result(proceed())
                }
            }
        }
        if (diBackgroundStyle == 0 && diAmbientLightType != 0) {
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
                if (diAmbientLightType == 1) {
                    resolve().firstMethodOrNull {
                        name = "attach"
                    }?.hook {
                        val ori = proceed()
                        fldHolder?.get(thisObject)?.let { holder ->
                            val mediaBgView = getMediaViewHolderField("mediaBgView", true)?.get(holder) as? View ?: return@let
                            val parent = mediaBgView.parent as? ViewGroup ?: return@let
                            parent.removeView(mediaBgView)
                        }
                        fldDummyHolder?.get(thisObject)?.let { holder ->
                            val mediaBgView = getMediaViewHolderField("mediaBgView", true)?.get(holder) as? View ?: return@let
                            val parent = mediaBgView.parent as? ViewGroup ?: return@let
                            parent.removeView(mediaBgView)
                        }
                        result(ori)
                    }
                } else {
                    resolve().firstMethodOrNull {
                        name = "detach"
                    }?.hook {
                        val ori = proceed()
                        fldHolder?.get(thisObject)?.let { holder ->
                            (getNewMediaBgView(holder, true)?.drawable as? AmbientLightDrawable)?.stop()
                        }
                        fldDummyHolder?.get(thisObject)?.let { dummyHolder ->
                            (getNewMediaBgView(dummyHolder, true)?.drawable as? AmbientLightDrawable)?.stop()
                        }
                        diCurrentPkgName = ""
                        diIsArtworkBound = false
                        releaseCachedWallpaperColor()
                        result(ori)
                    }
                    resolve().firstMethodOrNull {
                        name = "attach"
                    }?.hook {
                        val ori = proceed()
                        fldHolder?.get(thisObject)?.let { holder ->
                            getNewMediaBgView(holder, true)
                        }
                        fldDummyHolder?.get(thisObject)?.let { holder ->
                            getNewMediaBgView(holder, true)
                        }
                        result(ori)
                    }
                    resolve().firstMethodOrNull {
                        name = "bindMediaData"
                    }?.hook {
                        val ori = proceed()
                        val mediaData = getArg(0)
                        val packageName = mediaData?.let { fldPackageName?.get(it) }
                        val isArtWorkUpdate = fldIsArtWorkUpdate?.get(thisObject) == true || diCurrentPkgName != packageName || !diIsArtworkBound

                        val holder = fldHolder?.get(thisObject)
                        val dummyHolder = fldDummyHolder?.get(thisObject)
                        val context = fldContext?.get(thisObject)

                        if (
                            mediaData == null || packageName == null ||
                            holder == null || dummyHolder == null || context == null
                        ) {
                            return@hook result(ori)
                        }
                        if (isArtWorkUpdate) {
                            updateColor(context, mediaData, packageName, holder, PlayerType.DYNAMIC_ISLAND, true)
                            updateColor(context, mediaData, packageName, dummyHolder, PlayerType.DUMMY_DYNAMIC_ISLAND, true)
                        } else {
                            val mediaBgView = getNewMediaBgView(holder, true)
                            val dummyMediaBgView = getNewMediaBgView(dummyHolder, true)
                            val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable
                            val dummyAmbientLightDrawable = dummyMediaBgView?.drawable as? AmbientLightDrawable
                            val isPlaying = fldIsPlaying?.get(mediaData) ?: false
                            if (isPlaying) {
                                ambientLightDrawable?.resume()
                                dummyAmbientLightDrawable?.resume()
                            } else {
                                ambientLightDrawable?.pause()
                                dummyAmbientLightDrawable?.pause()
                            }
                        }
                        val mediaBgTransYOffset = fldMediaBgTransYOffset?.get(thisObject)
                        if (mediaBgTransYOffset != null && mediaBgTransYOffset != 0.0f) {
                            getNewMediaBgView(dummyHolder, true)?.translationY = mediaBgTransYOffset
                        }
                        result(ori)
                    }
                    $$"com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewBinderImpl$attach$4$1".toClassOrNull()?.apply {
                        val fldHolder = resolve().firstFieldOrNull {
                            name = $$"$holder"
                        }?.toTyped<Any>()
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
                            val mediaBgView = fldHolder?.get(thisObject)?.let { it1 -> getNewMediaBgView(it1, true) }
                            val dummyMediaBgView = fldDummyHolder?.get(thisObject)?.let { it1 -> getNewMediaBgView(it1, true) }
                            when (action) {
                                "pull_down_type_start" -> {
                                    (mediaBgView?.drawable as? AmbientLightDrawable)?.pause()
                                }
                                "pull_down_type_update" -> {
                                    dummyMediaBgView?.translationY = data?.getFloat("pull_down_action_offset_y", 0.0f) ?: 0.0f
                                }
                                "pull_down_type_finish" -> {
                                    (mediaBgView?.drawable as? AmbientLightDrawable)?.resume()
                                }
                            }
                            result(ori)
                        }
                    }
                }
            }
        }
    }

    private fun getNewMediaBgView(mMediaViewHolder: Any, isDynamicIsland: Boolean): ImageView? {
        val newMediaBgView = mMediaViewHolder.mediaBgView
        if (newMediaBgView?.drawable is AmbientLightDrawable) {
            return newMediaBgView
        } else {
            val mediaBg = if (isDynamicIsland) {
                getMediaViewHolderField("mediaBgView", true)?.get(mMediaViewHolder) as? View
            } else {
                getMediaViewHolderField("mediaBg", false)?.get(mMediaViewHolder) as? View
            }
            val parent = mediaBg?.parent as? ViewGroup ?: return null
            val index = (parent.indexOfChild(mediaBg) + 1).coerceIn(0, parent.childCount)
            val ambientLightDrawable = AmbientLightDrawable().apply {
                start()
            }
            val musicBgView = ImageView(mediaBg.context).apply {
                id = media_bg_view
                layoutParams = ViewGroup.LayoutParams(0, 0)
                clipToOutline = true
                outlineProvider = mediaBg.outlineProvider
                setImageDrawable(ambientLightDrawable)
            }
            parent.addView(musicBgView, index)
            if (isDynamicIsland) {
                parent.removeView(mediaBg)
            }
            val constraintSet = ctorConstraintSet.newInstance()
            clone?.invoke(constraintSet, parent)
            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            applyTo?.invoke(constraintSet, parent)
            mMediaViewHolder.mediaBgView = musicBgView
            return musicBgView
        }
    }

    fun getMainColorHCT(drawable: Drawable): Int? {
        return metDrawable2Bitmap?.invoke(null, drawable)?.let { bitmap ->
            metGetMainColorHCT?.invoke(null, bitmap)
        }
    }

    fun updateColor(context: Context, mediaData: Any, pkgName: String, holder: Any, type: PlayerType, isDark: Boolean) {
        val isDynamicIsland = (type != PlayerType.NOTIFICATION_CANTER)
        val mediaBgView = getNewMediaBgView(holder, isDynamicIsland) ?: return
        val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return
        val artwork = fldArtwork?.get(mediaData)
        val colorOpt = if (isDynamicIsland) diAmbientColorOpt else ncAmbientColorOpt

        HostExecutor.execute(
            tag = type,
            backgroundTask = {
                val mainColorHCT: Int
                if (colorOpt) {
                    val wallpaperColors = context.getCachedWallpaperColor(artwork)
                    val mutableColorScheme: Any?
                    if (wallpaperColors != null) {
                        mutableColorScheme = ctorColorScheme?.newInstance(wallpaperColors, true, enumStyleContent)
                    } else {
                        try {
                            val icon = context.packageManager.getApplicationIcon(pkgName)
                            mutableColorScheme = ctorColorScheme?.newInstance(WallpaperColors.fromDrawable(icon), true, enumStyleContent)?: throw Exception()
                        } catch (e: Exception) {
                            e(e) { "application not found!" }
                            return@execute null
                        }
                    }
                    val accent1 = (fldTonalPaletteAllShades?.get(fldColorSchemeAccent1!!.get(mutableColorScheme)) as? List<*>)?.filterIsInstance<Int>()
                    if (accent1?.size != 13) {
                        mainColorHCT = Color.TRANSPARENT
                    } else if (isDynamicIsland) {
                        mainColorHCT = accent1[7]
                    } else {
                        val light = accent1[4]
                        val dark = accent1[7]
                        holder.lightMediaBgColor = light
                        holder.darkMediaBgColor = dark
                        mainColorHCT = if (isDark) dark else light
                    }
                } else {
                    val artWorkDrawable = (artwork?.loadDrawable(context) ?: metAcquireApplicationIcon?.invoke(null, context, mediaData)) ?: return@execute null
                    mainColorHCT = getMainColorHCT(artWorkDrawable) ?: Color.TRANSPARENT
                }
                return@execute mainColorHCT
            },
            runOnMain = true
        ) { mainColorHCT ->
            val isPlaying = fldIsPlaying?.get(mediaData) == true
            ambientLightDrawable.setGradientColor(mainColorHCT, !mediaBgView.isShown)
            if (isPlaying && !(type == PlayerType.NOTIFICATION_CANTER && inFullAod)) {
                ambientLightDrawable.resume()
            } else {
                ambientLightDrawable.pause()
            }
            if (isDynamicIsland) {
                diCurrentPkgName = pkgName
                diIsArtworkBound = true
            } else {
                ncCurrentPkgName = pkgName
                ncIsArtworkBound = true
            }
        }
    }
}