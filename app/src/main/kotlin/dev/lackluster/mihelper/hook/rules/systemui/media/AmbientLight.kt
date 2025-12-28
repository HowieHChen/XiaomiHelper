package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
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
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.log.YLog

object AmbientLight : YukiBaseHooker() {
    private const val KEY_MEDIA_BG_VIEW = "KEY_MEDIA_BG_VIEW"
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val diBackgroundStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.BACKGROUND_STYLE, 0)
    private val ncAmbientLight = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT, false)
    private val diAmbientLight = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT, true)

    private val clzMusicBgView by lazy {
        "com.mi.widget.view.MusicBgView".toClassOrNull()
    }
    private val ctorMusicBgView by lazy {
        clzMusicBgView?.resolve()?.firstConstructorOrNull {
            parameterCount = 1
            parameters(Context::class)
        }?.self
    }
    private val metStart by lazy {
        clzMusicBgView?.resolve()?.firstMethodOrNull {
            name = "start"
        }?.self
    }
    private val metStop by lazy {
        clzMusicBgView?.resolve()?.firstMethodOrNull {
            name = "stop"
        }?.self
    }
    private val metResume by lazy {
        clzMusicBgView?.resolve()?.firstMethodOrNull {
            name = "resume"
        }?.self
    }
    private val metPause by lazy {
        clzMusicBgView?.resolve()?.firstMethodOrNull {
            name = "pause"
        }?.self
    }
    private val metSetGradientColor by lazy {
        clzMusicBgView?.resolve()?.firstMethodOrNull {
            name = "setGradientColor"
        }?.self
    }
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
    private val metGetPaletteColor by lazy {
        clzMiPalette?.resolve()?.firstMethodOrNull {
            name = "getPaletteColor"
            parameters(Int::class, String::class, Int::class)
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

    override fun onHook() {
        if (ncBackgroundStyle == 0 && ncAmbientLight) {
            var parentOnGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
            clzMiuiMediaViewControllerImpl?.apply {
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldMediaData = resolve().firstFieldOrNull {
                    name = "mediaData"
                }?.self
                resolve().firstMethodOrNull {
                    name = "detach"
                }?.hook {
                    after {
                        val holder = fldHolder?.get(this.instance) ?: return@after
                        XposedHelpers.getAdditionalInstanceField(holder, KEY_MEDIA_BG_VIEW)?.let { musicBgView ->
                            metStop?.invoke(musicBgView)
                            val parent = (musicBgView as? View)?.parent as? ViewGroup ?: return@after
                            parentOnGlobalLayoutListener?.let {
                                parent.viewTreeObserver.removeOnGlobalLayoutListener(it)
                            }
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val controllerImpl = this.instance
                        val holder = fldHolder?.get(controllerImpl) ?: return@after
                        val mediaBg = getMediaViewHolderField("mediaBg", false)?.get(holder) as? ImageView ?: return@after
                        val parent = mediaBg.parent as? ViewGroup ?: return@after
                        val index = (parent.indexOfChild(mediaBg) + 1).coerceIn(0, parent.childCount)
                        (ctorMusicBgView?.newInstance(mediaBg.context) as? View)?.let { musicBgView ->
                            musicBgView.apply {
                                id = media_bg_view
                                layoutParams = ViewGroup.LayoutParams(0, 0)
                                clipToOutline = true
                                outlineProvider = mediaBg.outlineProvider
                            }
                            parent.addView(musicBgView, index)
                            XposedHelpers.setAdditionalInstanceField(holder, KEY_MEDIA_BG_VIEW, musicBgView)
                            val constraintSet = ctorConstraintSet.newInstance()
                            clone?.invoke(constraintSet, parent)
                            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                            connect?.invoke(constraintSet, media_bg_view, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                            applyTo?.invoke(constraintSet, parent)
                        }
                        parentOnGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val holder = fldHolder.get(controllerImpl) ?: return
                                val musicBgView = XposedHelpers.getAdditionalInstanceField(holder, KEY_MEDIA_BG_VIEW) as? View ?: return
                                val mediaData = fldMediaData?.get(controllerImpl) ?: return
                                val isPlaying = fldIsPlaying?.get(mediaData) == true
                                if (parent.isVisible && isPlaying) {
                                    metStart?.invoke(musicBgView)
                                    metResume?.invoke(musicBgView)
                                } else {
                                    metPause?.invoke(musicBgView)
                                }
                            }
                        }
                        parentOnGlobalLayoutListener.let {
                            parent.viewTreeObserver.addOnGlobalLayoutListener(it)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "bindMediaData"
                }?.hook {
                    after {
                        val mediaData = this.args(0).any() ?: return@after
                        val holder = fldHolder?.get(this.instance) ?: return@after
                        val musicBgView = XposedHelpers.getAdditionalInstanceField(holder, KEY_MEDIA_BG_VIEW) as? View ?: return@after
                        val context = musicBgView.context
                        val artwork = fldArtwork?.get(mediaData) as? Icon

                        val isPlaying = fldIsPlaying?.get(mediaData) == true
                        val artWorkDrawable =
                            (artwork?.loadDrawable(context) ?: (metAcquireApplicationIcon?.invoke(null, context, mediaData) as? Drawable)) ?: return@after
                        val mainColorHCT = getMainColorHCT(artWorkDrawable)?.also { YLog.info(it.toHexString()) } ?: Color.TRANSPARENT
                        if (isPlaying) {
                            metSetGradientColor?.invoke(musicBgView, mainColorHCT, intArrayOf(
                                (metGetPaletteColor?.invoke(null, mainColorHCT, "primary", 12) as? Int)?.also { YLog.info(it.toHexString()) } ?: mainColorHCT,
                                (metGetPaletteColor?.invoke(null, mainColorHCT, "primary", 10) as? Int)?.also { YLog.info(it.toHexString()) } ?: mainColorHCT,
                                (metGetPaletteColor?.invoke(null, mainColorHCT, "tertiary", 12) as? Int)?.also { YLog.info(it.toHexString()) } ?: mainColorHCT,
                            ))
                            metStart?.invoke(musicBgView)
                            metResume?.invoke(musicBgView)
                        } else {
                            metPause?.invoke(musicBgView)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onFullAodStateChanged"
                }?.hook {
                    after {
                        val holder = fldHolder?.get(this.instance) ?: return@after
                        val musicBgView = XposedHelpers.getAdditionalInstanceField(holder, KEY_MEDIA_BG_VIEW) as? View ?: return@after
                        val mediaData = fldMediaData?.get(this.instance) ?: return@after
                        val toFullAod = this.args(0).boolean()
                        val isPlaying = fldIsPlaying?.get(mediaData) == true
                        if (!toFullAod && isPlaying) {
                            metStart?.invoke(musicBgView)
                            metResume?.invoke(musicBgView)
                        } else {
                            metPause?.invoke(musicBgView)
                        }
                    }
                }
            }
        }
        if (diBackgroundStyle == 0 && !diAmbientLight) {
            clzMiuiIslandMediaViewBinderImpl?.apply {
                val fldHolder = resolve().firstFieldOrNull {
                    name = "holder"
                }?.self
                val fldDummyHolder = resolve().firstFieldOrNull {
                    name = "dummyHolder"
                }?.self
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
            }
        }
    }

    private fun getMainColorHCT(drawable: Drawable): Int? {
        return metDrawable2Bitmap?.invoke(null, drawable)?.let { bitmap ->
            metGetMainColorHCT?.invoke(null, bitmap) as? Int
        }
    }
}