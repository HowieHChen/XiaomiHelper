package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.media.drawable.AmbientLightDrawable
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.tiny_media_session_view
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clone
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.ctorConstraintSet
import dev.lackluster.mihelper.hook.rules.systemui.media.AmbientLight.getMainColorHCT
import dev.lackluster.mihelper.hook.rules.systemui.media.AmbientLight.metAcquireApplicationIcon
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.clzMediaData
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.ctorColorScheme
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.enumStyleContent
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldTonalPaletteAllShades
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getCachedWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.releaseCachedWallpaperColor
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.HostExecutor
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode

object FlipTinyScreen : StaticHooker() {
    private var Any.mediaBgView by extraOf<ImageView>("KEY_MEDIA_BG_VIEW")
    private var Any.lightMediaBgColor by extraOf<Int>("KEY_MEDIA_BG_COLOR_LIGHT")
    private var Any.darkMediaBgColor by extraOf<Int>("KEY_MEDIA_BG_COLOR_DARK")
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle by Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(false).lazyGet()
    private val ncAmbientLight by Preferences.SystemUI.MediaControl.NotifCenter.BG_AMBIENT_LIGHT.lazyGet()
    private val ncAmbientColorOpt by Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.get(false).lazyGet()
    private val ncAlwaysDark by Preferences.SystemUI.MediaControl.NotifCenter.BG_ALWAYS_DARK.lazyGet()
    
    var ncCurrentPkgName = ""
    var ncIsArtworkBound = false

    private val clzFlipRowAdapter by "com.android.notification.tinypanel.FlipRowAdapter".lazyClassOrNull()
    private val clzFlipMediaRowHolder by "com.android.notification.tinypanel.FlipMediaRowHolder".lazyClassOrNull()
    private val ctorFlipMediaRowHolder by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstConstructorOrNull {
            parameterCount = 1
        }
    }
    private val fldMediaBg by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstFieldOrNull {
            name = "mMediaBg"
        }?.toTyped<View>()
    }
    private val fldItemView by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstFieldOrNull {
            name = "mItemView"
            superclass()
        }?.toTyped<View>()
    }
    private val fldIsPlaying by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "isPlaying"
        }?.toTyped<Boolean>()
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
        updateSelfState(clzFlipMediaRowHolder != null && clzFlipRowAdapter != null)
    }

    override fun onHook() {
        if (ncBackgroundStyle == 0 && ncAmbientLight) {
            clzFlipMediaRowHolder?.apply {
                val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                    name = "mIsArtWorkUpdate"
                }?.toTyped<Boolean>()
                val fldMediaData = resolve().firstFieldOrNull {
                    name = "mMediaData"
                }?.toTyped<Any>()
                resolve().firstConstructor().hook {
                    val ori = proceed()
                    getNewMediaBgView(thisObject)
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "setButton"
                }?.hook {
                    val ori = proceed()
                    val mediaData = fldMediaData?.get(thisObject)
                    val packageName = fldPackageName?.get(mediaData)
                    val isArtWorkUpdate = fldIsArtWorkUpdate?.get(thisObject) == true || ncCurrentPkgName != packageName || !ncIsArtworkBound

                    val holder = thisObject
                    val itemView = fldItemView?.get(holder)
                    val context = itemView?.context

                    if (context == null || mediaData == null || packageName == null) {
                        return@hook result(ori)
                    }

                    if (isArtWorkUpdate) {
                        updateColor(context, mediaData, packageName, holder, ncAlwaysDark || context.isSystemInDarkMode)
                    } else {
                        val mediaBgView = getNewMediaBgView(holder)
                        val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable
                        val isPlaying = fldIsPlaying?.get(mediaData) == true
                        if (isPlaying) {
                            ambientLightDrawable?.resume()
                        } else {
                            ambientLightDrawable?.pause()
                        }
                    }
                    result(ori)
                }
                resolve().firstMethodOrNull {
                    name = "setForegroundColors"
                }?.hook {
                    val ori = proceed()
                    val holder = thisObject
                    val mediaBgView = getNewMediaBgView(holder)
                    val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable
                    val itemView = fldItemView?.get(holder)
                    val context = itemView?.context
                    if (ambientLightDrawable == null || context == null) {
                        return@hook result(ori)
                    }
                    val isDark = context.isSystemInDarkMode
                    if (ncAmbientColorOpt) {
                        val light = holder.lightMediaBgColor ?: Color.TRANSPARENT
                        val dark = holder.darkMediaBgColor ?: Color.TRANSPARENT
                        ambientLightDrawable.setGradientColor(if (isDark) dark else light, !mediaBgView.isShown)
                    }
                    ambientLightDrawable.setLightMode(!isDark)
                    result(ori)
                }
            }
            clzFlipRowAdapter?.apply {
//                resolve().firstMethodOrNull {
//                    name = "onViewAttachedToWindow"
//                }?.hook {
//                    after {
//
//                    }
//                }
                resolve().firstMethodOrNull {
                    name = "onViewDetachedFromWindow"
                }?.hook {
                    val ori = proceed()
                    val holder = getArg(0)
                    if (holder != null && clzFlipMediaRowHolder?.isInstance(holder) == true) {
                        (getNewMediaBgView(holder)?.drawable as? AmbientLightDrawable)?.stop()
                        ncCurrentPkgName = ""
                        ncIsArtworkBound = false
                        releaseCachedWallpaperColor()
                    }
                    result(ori)
                }
            }
        }
        if (ncAlwaysDark) {
            clzFlipRowAdapter?.apply {
                resolve().firstMethodOrNull {
                    name = "onCreateViewHolder"
                }?.hook {
                    val parent = getArg(0) as? ViewGroup
                    if (parent != null && getArg(1) == 4) {
                        val context = parent.context
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        val view = LayoutInflater.from(wrappedContext).inflate(tiny_media_session_view, parent, false)
                        result(ctorFlipMediaRowHolder?.create(view))
                    } else {
                        result(proceed())
                    }
                }
            }
            clzFlipMediaRowHolder?.apply {
                resolve().firstMethodOrNull {
                    name = "setForegroundColors"
                }?.hook {
                    val holder = thisObject
                    val itemView = fldItemView?.get(holder)
                    val context = itemView?.context
                    if (context != null) {
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        itemView.asResolver().firstFieldOrNull {
                            name = "mContext"
                        }?.set(wrappedContext)
                    }
                    result(proceed())
                }
                resolve().firstMethodOrNull {
                    name = "setPlayerBg"
                }?.hook {
                    val holder = thisObject
                    val itemView = fldItemView?.get(holder)
                    val context = itemView?.context
                    if (context != null) {
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        itemView.asResolver().firstFieldOrNull {
                            name = "mContext"
                        }?.set(wrappedContext)
                    }
                    result(proceed())
                }
            }
        }
    }

    private fun getNewMediaBgView(mMediaViewHolder: Any): ImageView? {
        val newMediaBgView = mMediaViewHolder.mediaBgView
        if (newMediaBgView?.drawable is AmbientLightDrawable) {
            return newMediaBgView
        } else {
            val mediaBg = fldMediaBg?.get(mMediaViewHolder)
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

    private fun updateColor(context: Context, mediaData: Any, pkgName: String, holder: Any, isDark: Boolean) {
        val mediaBgView = getNewMediaBgView(holder) ?: return
        val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return
        val artwork = fldArtwork?.get(mediaData)
        val colorOpt = ncAmbientColorOpt

        HostExecutor.execute(
            tag = "flip_tiny_screen",
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
            if (isPlaying) {
                ambientLightDrawable.resume()
            } else {
                ambientLightDrawable.pause()
            }
            ncCurrentPkgName = pkgName
            ncIsArtworkBound = true
        }
    }
}