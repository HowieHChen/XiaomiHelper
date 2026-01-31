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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg_view
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clone
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.ctorConstraintSet
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.clzMediaData
import dev.lackluster.mihelper.utils.Prefs
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.hook.drawable.AmbientLightDrawable
import dev.lackluster.mihelper.hook.rules.systemui.compat.PairCompat
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.ctorColorScheme
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.enumStyleContent
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldTonalPaletteAllShades
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.releaseCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.data.PlayerType
import dev.lackluster.mihelper.utils.HostExecutor
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField

object AmbientLight : YukiBaseHooker() {
    private const val KEY_MEDIA_BG_VIEW = "KEY_MEDIA_BG_VIEW"
    private const val KEY_MEDIA_BG_COLOR_LIGHT = "KEY_MEDIA_BG_COLOR_LIGHT"
    private const val KEY_MEDIA_BG_COLOR_DARK = "KEY_MEDIA_BG_COLOR_DARK"
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val diBackgroundStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.BACKGROUND_STYLE, 0)
    private val ncAmbientLight = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT, false)
    private val diAmbientLightType = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_TYPE, 0)
    private val ncAmbientColorOpt = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT_OPT, false)
    private val diAmbientColorOpt = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_OPT, false)

    var ncCurrentPkgName = ""
    var ncIsArtworkBound = false
    var diCurrentPkgName = ""
    var diIsArtworkBound = false
    var inFullAod = false

    private val fldIsPlaying by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "isPlaying"
        }?.self
    }
    private val clzMiPalette by lazy {
        "miuix.mipalette.MiPalette".toClassOrNull()
    }
    private val metGetMainColorHCT by lazy {
        clzMiPalette?.resolve()?.firstMethodOrNull {
            name = "getMainColorHCT"
            parameters(Bitmap::class)
            modifiers(Modifiers.STATIC)
        }?.self
    }
    private val metDrawable2Bitmap by lazy {
        "com.miui.utils.DrawableUtils".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "drawable2Bitmap"
            parameters(Drawable::class)
            modifiers(Modifiers.STATIC)
        }?.self
    }
    private val metAcquireApplicationIcon by lazy {
        "com.android.systemui.statusbar.notification.utils.NotificationUtil".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "acquireApplicationIcon"
            parameterCount = 2
            modifiers(Modifiers.STATIC)
        }?.self
    }
    private val fldArtwork by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "artwork"
        }?.self
    }
    private val fldPackageName by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "packageName"
        }?.self
    }

    override fun onHook() {
        if (ncBackgroundStyle == 0 && ncAmbientLight) {
            clzMiuiMediaViewControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.self
                val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                    name = "isArtWorkUpdate"
                }?.self
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldMediaData = resolve().firstFieldOrNull {
                    name = "mediaData"
                }?.self
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.self
                val fldEnableFullAod = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mEnableFullAod"
                    }?.self
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.self
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    after {
                        val holder = fldHolder?.get(this.instance) ?: return@after
                        (getNewMediaBgView(holder, false)?.drawable as? AmbientLightDrawable)?.stop()
                        ncCurrentPkgName = ""
                        ncIsArtworkBound = false
                        releaseCachedWallpaperColor()
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val controllerImpl = this.instance
                        val holder = fldHolder?.get(controllerImpl) ?: return@after
                        getNewMediaBgView(holder, false)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "bindMediaData"
                }?.hook {
                    after {
                        val mediaData = this.args(0).any() ?: return@after
                        val packageName = fldPackageName?.get(mediaData) as? String ?: return@after
                        val isArtWorkUpdate = fldIsArtWorkUpdate?.get(this.instance) == true || ncCurrentPkgName != packageName || !ncIsArtworkBound

                        val holder = fldHolder?.get(this.instance) ?: return@after
                        val context = fldContext?.get(this.instance) as? Context ?: return@after

                        if (isArtWorkUpdate) {
                            val fullAodControllerLazy = fldFullAodController?.get(this.instance)
                            val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                            val enableFullAod = fldEnableFullAod?.get(fullAodController) == true

                            updateColor(context, mediaData, packageName, holder, PlayerType.NOTIFICATION_CANTER, context.isSystemInDarkMode || enableFullAod)
                        } else {
                            val mediaBgView = getNewMediaBgView(holder, false) ?: return@after
                            val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return@after
                            val isPlaying = fldIsPlaying?.get(mediaData) == true
                            if (!inFullAod && isPlaying) {
                                ambientLightDrawable.resume()
                            } else {
                                ambientLightDrawable.pause()
                            }
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onFullAodStateChanged"
                }?.hook {
                    after {
                        val holder = fldHolder?.get(this.instance) ?: return@after
                        val ambientLightDrawable = getNewMediaBgView(holder, false)?.drawable as? AmbientLightDrawable ?: return@after
                        val mediaData = fldMediaData?.get(this.instance) ?: return@after
                        val toFullAod = this.args(0).boolean()
                        if (toFullAod != inFullAod) {
                            ambientLightDrawable.animateNextResize()
                        }
                        inFullAod = toFullAod
                        val isPlaying = fldIsPlaying?.get(mediaData) == true
                        if (!toFullAod && isPlaying) {
                            ambientLightDrawable.resume()
                        } else {
                            ambientLightDrawable.pause()
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "updateForegroundColors"
                }?.hook {
                    before {
                        val holder = fldHolder?.get(this.instance) ?: return@before
                        val mediaBgView = getNewMediaBgView(holder, false)
                        val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable ?: return@before
                        val context = fldContext?.get(this.instance) as? Context ?: return@before
                        val fullAodControllerLazy = fldFullAodController?.get(this.instance)
                        val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        val enableFullAod = fldEnableFullAod?.get(fullAodController) == true
                        val isDark = enableFullAod || context.isSystemInDarkMode
                        if (ncAmbientColorOpt) {
                            val light = holder.getAdditionalInstanceField<Int>(KEY_MEDIA_BG_COLOR_LIGHT) ?: Color.TRANSPARENT
                            val dark = holder.getAdditionalInstanceField<Int>(KEY_MEDIA_BG_COLOR_DARK) ?: Color.TRANSPARENT
                            ambientLightDrawable.setGradientColor(if (isDark) dark else light, !mediaBgView.isShown)
                        }
                        ambientLightDrawable.setLightMode(!isDark)
                    }
                }
            }
        }
        if (diBackgroundStyle == 0 && diAmbientLightType != 0) {
            clzMiuiIslandMediaViewBinderImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.self
                val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                    name = "isArtWorkUpdate"
                }?.self
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldDummyHolder = resolve().firstFieldOrNull {
                    name = "dummyHolder"
                }?.self
                val fldMediaBgTransYOffset = resolve().optional(true).firstFieldOrNull {
                    name = "mediaBgTransYOffset"
                }?.self
                if (diAmbientLightType == 1) {
                    resolve().firstMethodOrNull {
                        name = "attach"
                    }?.hook {
                        after {
                            fldHolder?.get(this.instance)?.let { holder ->
                                val mediaBgView = getMediaViewHolderField("mediaBgView", true)?.get(holder) as? View ?: return@let
                                val parent = mediaBgView.parent as? ViewGroup ?: return@let
                                parent.removeView(mediaBgView)
                            }
                            fldDummyHolder?.get(this.instance)?.let { holder ->
                                val mediaBgView = getMediaViewHolderField("mediaBgView", true)?.get(holder) as? View ?: return@let
                                val parent = mediaBgView.parent as? ViewGroup ?: return@let
                                parent.removeView(mediaBgView)
                            }
                        }
                    }
                } else {
                    resolve().firstMethodOrNull {
                        name = "detach"
                    }?.hook {
                        after {
                            val holder = fldHolder?.get(this.instance) ?: return@after
                            val dummyHolder = fldDummyHolder?.get(this.instance) ?: return@after
                            (getNewMediaBgView(holder, true)?.drawable as? AmbientLightDrawable)?.stop()
                            (getNewMediaBgView(dummyHolder, true)?.drawable as? AmbientLightDrawable)?.stop()
                            diCurrentPkgName = ""
                            diIsArtworkBound = false
                            releaseCachedWallpaperColor()
                        }
                    }
                    resolve().firstMethodOrNull {
                        name = "attach"
                    }?.hook {
                        after {
                            fldHolder?.get(this.instance)?.let { holder ->
                                getNewMediaBgView(holder, true)
                            }
                            fldDummyHolder?.get(this.instance)?.let { holder ->
                                getNewMediaBgView(holder, true)
                            }
                        }
                    }
                    resolve().firstMethodOrNull {
                        name = "bindMediaData"
                    }?.hook {
                        after {
                            val mediaData = this.args(0).any() ?: return@after
                            val packageName = fldPackageName?.get(mediaData) as? String ?: return@after
                            val isArtWorkUpdate = fldIsArtWorkUpdate?.get(this.instance) == true || diCurrentPkgName != packageName || !diIsArtworkBound

                            val holder = fldHolder?.get(this.instance) ?: return@after
                            val dummyHolder = fldDummyHolder?.get(this.instance) ?: return@after
                            val context = fldContext?.get(this.instance) as? Context ?: return@after

                            if (isArtWorkUpdate) {
                                updateColor(context, mediaData, packageName, holder, PlayerType.DYNAMIC_ISLAND, true)
                                updateColor(context, mediaData, packageName, dummyHolder, PlayerType.DUMMY_DYNAMIC_ISLAND, true)
                            } else {
                                val mediaBgView = getNewMediaBgView(holder, true) ?: return@after
                                val dummyMediaBgView = getNewMediaBgView(dummyHolder, true) ?: return@after
                                val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return@after
                                val dummyAmbientLightDrawable = dummyMediaBgView.drawable as? AmbientLightDrawable ?: return@after
                                val isPlaying = fldIsPlaying?.get(mediaData) == true
                                if (isPlaying) {
                                    ambientLightDrawable.resume()
                                    dummyAmbientLightDrawable.resume()
                                } else {
                                    ambientLightDrawable.pause()
                                    dummyAmbientLightDrawable.pause()
                                }
                            }
                            val mediaBgTransYOffset = fldMediaBgTransYOffset?.get(this.instance) as? Float
                            if (mediaBgTransYOffset != null && mediaBgTransYOffset != 0.0f) {
                                getNewMediaBgView(dummyHolder, true)?.translationY = mediaBgTransYOffset
                            }
                        }
                    }
                    $$"com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaViewBinderImpl$attach$4$1".toClassOrNull()?.apply {
                        val fldHolder = resolve().firstFieldOrNull {
                            name = $$"$holder"
                        }?.self
                        val fldDummyHolder = resolve().firstFieldOrNull {
                            name = $$"$dummyHolder"
                        }?.self
                        resolve().firstMethodOrNull {
                            name = "emit"
                        }?.hook {
                            after {
                                val pair = this.args(0).any() ?: return@after
                                val action = PairCompat.getFirst(pair) as? String ?: return@after
                                val data = PairCompat.getSecond(pair) as? Bundle
                                val mediaBgView = fldHolder?.get(this.instance)?.let { it1 -> getNewMediaBgView(it1, true) } ?: return@after
                                val dummyMediaBgView = fldDummyHolder?.get(this.instance)?.let { it1 -> getNewMediaBgView(it1, true) } ?: return@after
                                when (action) {
                                    "pull_down_type_start" -> {
                                        (mediaBgView.drawable as? AmbientLightDrawable)?.pause()
                                    }
                                    "pull_down_type_update" -> {
                                        dummyMediaBgView.translationY = data?.getFloat("pull_down_action_offset_y", 0.0f) ?: 0.0f
                                    }
                                    "pull_down_type_finish" -> {
                                        (mediaBgView.drawable as? AmbientLightDrawable)?.resume()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getNewMediaBgView(mMediaViewHolder: Any, isDynamicIsland: Boolean): ImageView? {
        val newMediaBgView = mMediaViewHolder.getAdditionalInstanceField<ImageView>(KEY_MEDIA_BG_VIEW)
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
            mMediaViewHolder.setAdditionalInstanceField(KEY_MEDIA_BG_VIEW, musicBgView)
            return musicBgView
        }
    }

    private fun getMainColorHCT(drawable: Drawable): Int? {
        return metDrawable2Bitmap?.invoke(null, drawable)?.let { bitmap ->
            metGetMainColorHCT?.invoke(null, bitmap) as? Int
        }
    }

    fun updateColor(context: Context, mediaData: Any, pkgName: String, holder: Any, type: PlayerType, isDark: Boolean) {
        val isDynamicIsland = (type != PlayerType.NOTIFICATION_CANTER)
        val mediaBgView = getNewMediaBgView(holder, isDynamicIsland) ?: return
        val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return
        val artwork = fldArtwork?.get(mediaData) as? Icon
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
                        } catch (_: Exception) {
                            YLog.warn("application not found!")
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
                        holder.setAdditionalInstanceField(KEY_MEDIA_BG_COLOR_LIGHT, light)
                        holder.setAdditionalInstanceField(KEY_MEDIA_BG_COLOR_DARK, dark)
                        mainColorHCT = if (isDark) dark else light
                    }
                } else {
                    val artWorkDrawable = (artwork?.loadDrawable(context) ?: (metAcquireApplicationIcon?.invoke(null, context, mediaData) as? Drawable)) ?: return@execute null
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