package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.AmbientLightDrawable
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
import dev.lackluster.mihelper.utils.HostExecutor
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.isSystemInDarkMode
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField

object FlipTinyScreen : YukiBaseHooker() {
    private const val KEY_MEDIA_BG_VIEW = "KEY_MEDIA_BG_VIEW"
    private const val KEY_MEDIA_BG_COLOR_LIGHT = "KEY_MEDIA_BG_COLOR_LIGHT"
    private const val KEY_MEDIA_BG_COLOR_DARK = "KEY_MEDIA_BG_COLOR_DARK"
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val ncAmbientLight = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT, false)
    private val ncAmbientColorOpt = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT_OPT, false)
    private val ncAlwaysDark = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ALWAYS_DARK, false)

    var ncCurrentPkgName = ""
    var ncIsArtworkBound = false

    private val clzFlipRowAdapter by lazy {
        "com.android.notification.tinypanel.FlipRowAdapter".toClassOrNull()
    }
    private val clzFlipMediaRowHolder by lazy {
        "com.android.notification.tinypanel.FlipMediaRowHolder".toClassOrNull()
    }
    private val ctorFlipMediaRowHolder by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstConstructorOrNull {
            parameterCount = 1
        }
    }
    private val fldMediaBg by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstFieldOrNull {
            name = "mMediaBg"
        }?.self?.apply { makeAccessible() }
    }
    private val fldItemView by lazy {
        clzFlipMediaRowHolder?.resolve()?.firstFieldOrNull {
            name = "mItemView"
            superclass()
        }?.self?.apply { makeAccessible() }
    }
    private val fldIsPlaying by lazy {
        clzMediaData?.resolve()?.firstFieldOrNull {
            name = "isPlaying"
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
            clzFlipMediaRowHolder?.apply {
                val fldIsArtWorkUpdate = resolve().firstFieldOrNull {
                    name = "mIsArtWorkUpdate"
                }?.self?.apply { makeAccessible() }
                val fldMediaData = resolve().firstFieldOrNull {
                    name = "mMediaData"
                }?.self?.apply { makeAccessible() }
                resolve().firstConstructor().hook {
                    after {
                        val holder = this.instance
                        getNewMediaBgView(holder)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setButton"
                }?.hook {
                    after {
                        val mediaData = fldMediaData?.get(this.instance) ?: return@after
                        val packageName = fldPackageName?.get(mediaData) as? String ?: return@after
                        val isArtWorkUpdate = fldIsArtWorkUpdate?.get(this.instance) == true || ncCurrentPkgName != packageName || !ncIsArtworkBound

                        val holder = this.instance
                        val itemView = fldItemView?.get(holder) as? View
                        val context = itemView?.context ?: return@after

                        if (isArtWorkUpdate) {
                            updateColor(context, mediaData, packageName, holder, ncAlwaysDark || context.isSystemInDarkMode)
                        } else {
                            val mediaBgView = getNewMediaBgView(holder) ?: return@after
                            val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return@after
                            val isPlaying = fldIsPlaying?.get(mediaData) == true
                            if (isPlaying) {
                                ambientLightDrawable.resume()
                            } else {
                                ambientLightDrawable.pause()
                            }
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setForegroundColors"
                }?.hook {
                    after {
                        val holder = this.instance
                        val mediaBgView = getNewMediaBgView(holder)
                        val ambientLightDrawable = mediaBgView?.drawable as? AmbientLightDrawable ?: return@after
                        val itemView = fldItemView?.get(holder) as? View
                        val context = itemView?.context ?: return@after
                        val isDark = context.isSystemInDarkMode
                        if (ncAmbientColorOpt) {
                            val light = holder.getAdditionalInstanceField<Int>(KEY_MEDIA_BG_COLOR_LIGHT) ?: Color.TRANSPARENT
                            val dark = holder.getAdditionalInstanceField<Int>(KEY_MEDIA_BG_COLOR_DARK) ?: Color.TRANSPARENT
                            ambientLightDrawable.setGradientColor(if (isDark) dark else light, !mediaBgView.isShown)
                        }
                        ambientLightDrawable.setLightMode(!isDark)
                    }
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
                    after {
                        val holder = this.args(0).any()
                        if (holder != null && clzFlipMediaRowHolder?.isInstance(holder) == true) {
                            (getNewMediaBgView(holder)?.drawable as? AmbientLightDrawable)?.stop()
                            ncCurrentPkgName = ""
                            ncIsArtworkBound = false
                            releaseCachedWallpaperColor()
                        }
                    }
                }
            }
        }
        if (ncAlwaysDark) {
            clzFlipRowAdapter?.apply {
                resolve().firstMethodOrNull {
                    name = "onCreateViewHolder"
                }?.hook {
                    before {
                        if (this.args(1).int() == 4) {
                            val parent = this.args(0).cast<ViewGroup>()
                            val context = parent?.context ?: return@before
                            val oriConfiguration = context.resources.configuration
                            val configuration = Configuration(oriConfiguration).apply {
                                uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                            }
                            val wrappedContext = context.createConfigurationContext(configuration)
                            val view = LayoutInflater.from(wrappedContext).inflate(tiny_media_session_view, parent, false)
                            this.result = ctorFlipMediaRowHolder?.create(view)
                        }
                    }
                }
            }
            clzFlipMediaRowHolder?.apply {
                resolve().firstMethodOrNull {
                    name = "setForegroundColors"
                }?.hook {
                    before {
                        val holder = this.instance
                        val itemView = fldItemView?.get(holder) as? View
                        val context = itemView?.context ?: return@before
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        itemView.asResolver().firstFieldOrNull {
                            name = "mContext"
                        }?.set(wrappedContext)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "setPlayerBg"
                }?.hook {
                    before {
                        val holder = this.instance
                        val itemView = fldItemView?.get(holder) as? View
                        val context = itemView?.context ?: return@before
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        itemView.asResolver().firstFieldOrNull {
                            name = "mContext"
                        }?.set(wrappedContext)
                    }
                }
            }
        }
    }

    private fun getNewMediaBgView(mMediaViewHolder: Any): ImageView? {
        val newMediaBgView = mMediaViewHolder.getAdditionalInstanceField<ImageView>(KEY_MEDIA_BG_VIEW)
        if (newMediaBgView?.drawable is AmbientLightDrawable) {
            return newMediaBgView
        } else {
            val mediaBg = fldMediaBg?.get(mMediaViewHolder) as? View
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
            mMediaViewHolder.setAdditionalInstanceField(KEY_MEDIA_BG_VIEW, musicBgView)
            return musicBgView
        }
    }

    private fun updateColor(context: Context, mediaData: Any, pkgName: String, holder: Any, isDark: Boolean) {
        val mediaBgView = getNewMediaBgView(holder) ?: return
        val ambientLightDrawable = mediaBgView.drawable as? AmbientLightDrawable ?: return
        val artwork = fldArtwork?.get(mediaData) as? Icon
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
                        } catch (_: Exception) {
                            YLog.warn("application not found!")
                            return@execute null
                        }
                    }
                    val accent1 = (fldTonalPaletteAllShades?.get(fldColorSchemeAccent1!!.get(mutableColorScheme)) as? List<*>)?.filterIsInstance<Int>()
                    if (accent1?.size != 13) {
                        mainColorHCT = Color.TRANSPARENT
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