package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.media_bg_view
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiIslandMediaViewBinderImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getMediaViewHolderField
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.applyTo
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.clone
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.connect
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.ctorConstraintSet
import dev.lackluster.mihelper.hook.rules.systemui.compat.ConstraintSetCompat.setVisibility
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
import dev.lackluster.mihelper.utils.HostExecutor
import dev.lackluster.mihelper.utils.Prefs

object CustomBackground : YukiBaseHooker() {
    private const val KEY_VIEW_HOLDER_WRAPPER = "KEY_VIEW_HOLDER_WRAPPER"
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle;
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val diBackgroundStyle = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.BACKGROUND_STYLE, 0)
    private var ncProcessor: BgProcessor? = null
    private var diProcessor: BgProcessor? = null

    private val ncPlayerConfig = PlayerConfig()
    private val diPlayerConfig = PlayerConfig()
    private val diPlayerConfigDummy = PlayerConfig()

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
        loadHooker(AlwaysDark)
        loadHooker(AmbientLight)
        if (ncBackgroundStyle !in 1..4 && diBackgroundStyle !in 1..4) return
        loadHooker(MediaControlBgFactory)
        onHookNotificationCenter()
        onHookDynamicIsland()
    }

    private fun onHookNotificationCenter() {
        ncProcessor = when (ncBackgroundStyle) {
            1 -> CoverArtProcessor()
            2 -> BlurredCoverProcessor()
            3 -> RadialGradientProcessor()
            4 -> LinearGradientProcessor()
            else -> return
        }
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
            resolve().firstMethodOrNull {
                name = "updateForegroundColors"
            }?.hook {
                intercept()
            }
            resolve().firstMethodOrNull {
                name = "updateMediaBackground"
            }?.hook {
                intercept()
            }
            resolve().firstMethodOrNull {
                name = "detach"
            }?.hook {
                after {
                    finiMediaViewHolder(false)
                }
            }
            resolve().firstMethodOrNull {
                name = "attach"
            }?.hook {
                after {
                    val holder = fldHolder?.get(this.instance) ?: return@after
                    getMediaViewHolderWrapper(holder, false)
                }
            }
            resolve().firstMethodOrNull {
                name = "bindMediaData"
            }?.hook {
                after {
                    val mediaData = this.args(0).any() ?: return@after
                    val context = fldContext?.get(this.instance) as? Context ?: return@after
                    val holder = fldHolder?.get(this.instance) ?: return@after
                    val artwork = fldArtwork?.get(mediaData) as? Icon ?: return@after
                    val packageName = fldPackageName?.get(mediaData) as? String ?: return@after
                    val holderWrapper = getMediaViewHolderWrapper(holder, false)  ?: return@after
                    val isArtWorkUpdate = fldIsArtWorkUpdate?.get(this.instance) == true || ncPlayerConfig.mCurrentPkgName != packageName || !ncPlayerConfig.mIsArtworkBound
                    if (isArtWorkUpdate) {
                        updateBackground(context, artwork, packageName, holderWrapper, PlayerType.NOTIFICATION_CANTER)
                    }
                }
            }
        }
    }

    private fun onHookDynamicIsland() {
        diProcessor = when (diBackgroundStyle) {
            1 -> CoverArtProcessor()
            2 -> BlurredCoverProcessor()
            3 -> RadialGradientProcessor()
            4 -> LinearGradientProcessor()
            else -> return
        }
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
            resolve().firstMethodOrNull {
                name = "updateForegroundColors"
            }?.hook {
                intercept()
            }
            resolve().firstMethodOrNull {
                name = "detach"
            }?.hook {
                after {
                    finiMediaViewHolder(true)
                }
            }
            resolve().firstMethodOrNull {
                name = "attach"
            }?.hook {
                after {
                    val holder = fldHolder?.get(this.instance) ?: return@after
                    getMediaViewHolderWrapper(holder, true)
                    val dummyHolder = fldDummyHolder?.get(this.instance) ?: return@after
                    getMediaViewHolderWrapper(dummyHolder, true)
                }
            }
            resolve().firstMethodOrNull {
                name = "bindMediaData"
            }?.hook {
                after {
                    val mediaData = this.args(0).any() ?: return@after
                    val context = fldContext?.get(this.instance) as? Context ?: return@after
                    val holder = fldHolder?.get(this.instance) ?: return@after
                    val dummyHolder = fldDummyHolder?.get(this.instance) ?: return@after
                    val artwork = fldArtwork?.get(mediaData) as? Icon ?: return@after
                    val packageName = fldPackageName?.get(mediaData) as? String ?: return@after
                    val holderWrapper = getMediaViewHolderWrapper(holder, true) ?: return@after
                    val dummyHolderWrapper = getMediaViewHolderWrapper(dummyHolder, true) ?: return@after
                    val isArtWorkUpdate = fldIsArtWorkUpdate?.get(this.instance) == true || diPlayerConfig.mCurrentPkgName != packageName || !diPlayerConfig.mIsArtworkBound
                    if (isArtWorkUpdate) {
                        updateBackground(context, artwork, packageName, holderWrapper, PlayerType.DYNAMIC_ISLAND)
                        updateBackground(context, artwork, packageName, dummyHolderWrapper, PlayerType.DUMMY_DYNAMIC_ISLAND)
                    }
                }
            }
        }
    }

    private fun getMediaViewHolderWrapper(mMediaViewHolder: Any, isDynamicIsland: Boolean): MiuiMediaViewHolderWrapper? {
        (XposedHelpers.getAdditionalInstanceField(mMediaViewHolder, KEY_VIEW_HOLDER_WRAPPER) as? MiuiMediaViewHolderWrapper)?.let {
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
                val index = (mediaBgView?.let { it1 -> parent.indexOfChild(it1) + 1 } ?: 0).coerceIn(0, parent.childCount)
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
            seekBar,
        ).also {
            XposedHelpers.setAdditionalInstanceField(mMediaViewHolder, KEY_VIEW_HOLDER_WRAPPER, it)
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
        when (type) {
            PlayerType.NOTIFICATION_CANTER -> {
                processor = ncProcessor ?: return
                playerConfig = ncPlayerConfig
            }
            PlayerType.DYNAMIC_ISLAND -> {
                processor = diProcessor ?: return
                playerConfig = diPlayerConfig
            }
            PlayerType.DUMMY_DYNAMIC_ISLAND -> {
                processor = diProcessor ?: return
                playerConfig = diPlayerConfigDummy
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
                    } catch (_: Exception) {
                        YLog.warn("application not found!")
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
                playerConfig.mArtworkDrawable = processor.createBackground(processedArtwork, colorConfig)
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