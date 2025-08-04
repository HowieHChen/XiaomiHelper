@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.systemui.media

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.AsyncTask
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.MiuiMediaControlPanelClass
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.MiuiMediaViewControllerImplClass
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.PlayerTwoCircleViewClass
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.conColorScheme2
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.conColorScheme3
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.defaultColorConfig
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.enumStyleContent
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeNeutral1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeNeutral2
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent1
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldColorSchemeAccent2
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.fldTonalPaletteAllShades
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getScaledBackground
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.getWallpaperColor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.BgProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.BlurredCoverProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.CoverArtProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.RadialGradientProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.LinearGradientProcessor
import dev.lackluster.mihelper.hook.rules.systemui.media.bg.MediaViewColorConfig
import dev.lackluster.mihelper.utils.Prefs


object CustomBackground : YukiBaseHooker() {
    // background: 0 -> Default; 1 -> Art; 2 -> Blurred cover; 3 -> AndroidNewStyle; 4 -> AndroidOldStyle
    private val backgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private lateinit var processor: BgProcessor

    private var mArtworkBoundId = 0
    private var mArtworkNextBindRequestId = 0
    private var mArtworkDrawable: MediaControlBgDrawable? = null
    private var mIsArtworkBound = false
    private var mCurrentPkgName = ""

    private var mPrevColorConfig = defaultColorConfig
    private var mCurrColorConfig = defaultColorConfig

    private var lastWidth = 0
    private var lastHeight = 0

    override fun onHook() {
        processor = when (backgroundStyle) {
            1 -> CoverArtProcessor()
            2 -> BlurredCoverProcessor()
            3 -> RadialGradientProcessor()
            4 -> LinearGradientProcessor()
            else -> return
        }
        loadHooker(MediaControlBgFactory)
        "com.android.systemui.media.controls.ui.controller.MediaViewController".toClassOrNull()?.apply {
            method {
                name = "resetLayoutResource"
            }.ignored().hook {
                intercept()
            }
        }
        PlayerTwoCircleViewClass?.apply {
            constructor {
                paramCount = 4
            }.ignored().hook {
                after {
                    this.instance.current().field { name = "mPaint1" }.cast<Paint>()?.alpha = 0
                    this.instance.current().field { name = "mPaint2" }.cast<Paint>()?.alpha = 0
                    this.instance.current().field { name = "mRadius" }.set(0.0f)
                }
            }
            method {
                name = "setBackground"
            }.ignored().hook {
                before {
                    this.result = null
                }
            }
            method {
                name = "setPaintColor"
            }.ignored().hook {
                before {
                    this.result = null
                }
            }
        }
        MiuiMediaControlPanelClass?.apply {
            method {
                name = "onDestroy"
                superClass()
            }.hook {
                after {
                    finiMediaViewHolder()
                }
            }
            method {
                name = "setPlayerBg"
            }.ignored().hook {
                intercept()
            }
            method {
                name = "setForegroundColors"
            }.ignored().hook {
                intercept()
            }
            method {
                name = "bindPlayer"
            }.hook {
                after {
                    val context = this.instance.current().field {
                        name = "mContext"
                        superClass()
                    }.cast<Context>() ?: return@after
                    val mediaData = this.args(0).any() ?: return@after
                    val artwork = mediaData.current().field {
                        name = "artwork"
                    }.cast<Icon>()
                    val packageName = mediaData.current().field {
                        name = "packageName"
                    }.string()
                    val isArtWorkUpdate = this.instance.current().field {
                        name = "mIsArtworkUpdate"
                    }.boolean() || mCurrentPkgName != packageName
                    val mMediaViewHolder = this.instance.current().field {
                        name = "mMediaViewHolder"
                        superClass()
                    }.any() ?: return@after
                    val holder = initMediaViewHolder(mMediaViewHolder) ?: return@after
                    updateBackground(context, isArtWorkUpdate, artwork, packageName, holder)
                }
            }
        }
        MiuiMediaViewControllerImplClass?.apply {
            method {
                name = "updateMediaBackground"
            }.hook {
                intercept()
            }
            method {
                name = "detach"
            }.hook {
                after {
                    finiMediaViewHolder()
                }
            }
            method {
                name = "updateForegroundColors"
            }.hook {
                intercept()
            }
            method {
                name = "bindMediaData"
            }.hook {
                after {
                    val context = this.instance.current().field {
                        name = "context"
                    }.cast<Context>() ?: return@after
                    val mediaData = this.args(0).any() ?: return@after
                    val artwork = mediaData.current().field {
                        name = "artwork"
                    }.cast<Icon>()
                    val packageName = mediaData.current().field {
                        name = "packageName"
                    }.string()
                    val isArtWorkUpdate = this.instance.current().field {
                        name = "isArtWorkUpdate"
                    }.boolean() || mCurrentPkgName != packageName
                    val mMediaViewHolder = this.instance.current().field {
                        name = "holder"
                        superClass()
                    }.any() ?: return@after
                    val holder = initMediaViewHolder(mMediaViewHolder) ?: return@after
                    updateBackground(context, isArtWorkUpdate, artwork, packageName, holder)
                }
            }
        }
    }

    private fun initMediaViewHolder(mMediaViewHolder: Any): MiuiMediaViewHolder? {
        val mediaBg = mMediaViewHolder.current(true).field { name = "mediaBg" }.cast<ImageView>() ?: return null
        val titleText = mMediaViewHolder.current(true).field { name = "titleText" }.cast<TextView>() ?: return null
        val artistText = mMediaViewHolder.current(true).field { name = "artistText" }.cast<TextView>() ?: return null
        val seamlessIcon = mMediaViewHolder.current(true).field { name = "seamlessIcon" }.cast<ImageView>() ?: return null
        val action0 = mMediaViewHolder.current(true).field { name = "action0" }.cast<ImageButton>() ?: return null
        val action1 = mMediaViewHolder.current(true).field { name = "action1" }.cast<ImageButton>() ?: return null
        val action2 = mMediaViewHolder.current(true).field { name = "action2" }.cast<ImageButton>() ?: return null
        val action3 = mMediaViewHolder.current(true).field { name = "action3" }.cast<ImageButton>() ?: return null
        val action4 = mMediaViewHolder.current(true).field { name = "action4" }.cast<ImageButton>() ?: return null
        val seekBar = mMediaViewHolder.current(true).field { name = "seekBar" }.cast<SeekBar>() ?: return null
        val elapsedTimeView = mMediaViewHolder.current(true).field { name = "elapsedTimeView" }.cast<TextView>() ?: return null
        val totalTimeView = mMediaViewHolder.current(true).field { name = "totalTimeView" }.cast<TextView>() ?: return null
        val albumView = mMediaViewHolder.current(true).field { name = "albumView" }.cast<ImageView>() ?: return null
        return MiuiMediaViewHolder(
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
        )
    }

    private fun finiMediaViewHolder() {
        mArtworkDrawable = null
        mIsArtworkBound = false
        mCurrentPkgName = ""
    }

    private fun updateForegroundColors(holder: MiuiMediaViewHolder, colorConfig: MediaViewColorConfig) {
        val primaryColorStateList = ColorStateList.valueOf(colorConfig.textPrimary)
        holder.titleText.setTextColor(colorConfig.textPrimary)
        holder.artistText.setTextColor(colorConfig.textSecondary)
        holder.seamlessIcon.imageTintList = primaryColorStateList
        holder.action0.imageTintList = primaryColorStateList
        holder.action1.imageTintList = primaryColorStateList
        holder.action2.imageTintList = primaryColorStateList
        holder.action3.imageTintList = primaryColorStateList
        holder.action4.imageTintList = primaryColorStateList
        holder.seekBar.thumb.setTintList(primaryColorStateList)
        holder.seekBar.progressTintList = primaryColorStateList
        holder.seekBar.progressBackgroundTintList = primaryColorStateList
        holder.elapsedTimeView.setTextColor(colorConfig.textPrimary)
        holder.totalTimeView.setTextColor(colorConfig.textPrimary)
    }

    @Suppress("UNCHECKED_CAST")
    fun updateBackground(context: Context, isArtWorkUpdate: Boolean, artwork: Icon?, pkgName: String, holder: MiuiMediaViewHolder) {
        val artworkLayer = artwork?.loadDrawable(context) ?: return
        val reqId = mArtworkNextBindRequestId++
        if (isArtWorkUpdate) {
            mIsArtworkBound = false
        }
        // Clip album cover image
//        val finalSize = min(artworkLayer.intrinsicWidth, artworkLayer.intrinsicHeight)
//        val bitmap = createBitmap(finalSize, finalSize)
//        val canvas = Canvas(bitmap)
//        val deltaW = (artworkLayer.intrinsicWidth - finalSize) / 2
//        val deltaH = (artworkLayer.intrinsicHeight - finalSize) / 2
//        artworkLayer.setBounds(-deltaW, -deltaH, finalSize + deltaW, finalSize + deltaH)
//        artworkLayer.draw(canvas)
//        val radius = 9.0f * context.resources.displayMetrics.density
//        val newBitmap = createBitmap(finalSize, finalSize)
//        val canvas1 = Canvas(newBitmap)
//        val paint = Paint()
//        val rect = Rect(0, 0, finalSize, finalSize)
//        val rectF = RectF(rect)
//        paint.isAntiAlias = true
//        canvas1.drawARGB(0, 0, 0, 0)
//        paint.color = Color.BLACK
//        canvas1.drawRoundRect(rectF, radius, radius, paint)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas1.drawBitmap(bitmap, rect, rect, paint)
//        if (!bitmap.isRecycled) {
//            bitmap.recycle()
//        }
        // Update album cover image
        holder.albumView.setImageDrawable(artworkLayer)
        // Capture width & height from views in foreground for artwork scaling in background
        val width: Int
        val height: Int
        if (holder.mediaBg.measuredWidth == 0 || holder.mediaBg.measuredHeight == 0) {
            if (lastWidth == 0 || lastHeight == 0) {
                width = artworkLayer.intrinsicWidth
                height = artworkLayer.intrinsicHeight
            } else {
                width = lastWidth
                height = lastHeight
            }
        } else {
            width = holder.mediaBg.measuredWidth
            height = holder.mediaBg.measuredHeight
            lastWidth = width
            lastHeight = height
        }
        // Override colors set by the original method
        updateForegroundColors(holder, mCurrColorConfig)

        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            // Album art
            val mutableColorScheme: Any?
            val artworkDrawable: Drawable
            val isArtworkBound: Boolean
            val wallpaperColors = context.getWallpaperColor(artwork)
            if (wallpaperColors != null) {
                mutableColorScheme =
                    conColorScheme3?.newInstance(wallpaperColors, true, enumStyleContent)
                        ?: conColorScheme2?.newInstance(wallpaperColors, enumStyleContent)
                artworkDrawable = context.getScaledBackground(artwork, height, height) ?: Color.TRANSPARENT.toDrawable()
                isArtworkBound = true
            } else {
                // If there's no artwork, use colors from the app icon
                artworkDrawable = Color.TRANSPARENT.toDrawable()
                isArtworkBound = false
                try {
                    val icon = context.packageManager.getApplicationIcon(pkgName)
                    mutableColorScheme =
                        conColorScheme3?.newInstance(WallpaperColors.fromDrawable(icon), true, enumStyleContent)
                            ?: conColorScheme2?.newInstance(wallpaperColors, enumStyleContent)
                                    ?: throw Exception()
                } catch (_: Exception) {
                    YLog.warn("application not found!")
                    return@execute
                }
            }
            var colorConfig = defaultColorConfig
            var colorSchemeChanged = false
            if (mutableColorScheme != null) {
                val neutral1 = fldTonalPaletteAllShades?.get(fldColorSchemeNeutral1!!.get(mutableColorScheme)) as? List<Int>
                val neutral2 = fldTonalPaletteAllShades?.get(fldColorSchemeNeutral2!!.get(mutableColorScheme)) as? List<Int>
                val accent1 = fldTonalPaletteAllShades?.get(fldColorSchemeAccent1!!.get(mutableColorScheme)) as? List<Int>
                val accent2 = fldTonalPaletteAllShades?.get(fldColorSchemeAccent2!!.get(mutableColorScheme)) as? List<Int>
                if (neutral1 != null && neutral2 != null && accent1 != null && accent2 != null) {
                    colorConfig = processor.convertToColorConfig(artworkDrawable, neutral1, neutral2, accent1, accent2)
                    colorSchemeChanged = colorConfig != mPrevColorConfig
                    mPrevColorConfig = colorConfig
                }
            }
            val processedArtwork =
                processor.processAlbumCover(
                    artworkDrawable,
                    colorConfig,
                    context,
                    width,
                    height
                )
            if (mArtworkDrawable == null) {
                mArtworkDrawable = processor.createBackground(processedArtwork, colorConfig)
            }
            mArtworkDrawable?.setBounds(0, 0, width, height)
            mCurrentPkgName = pkgName

            holder.mediaBg.post(Runnable {
                if (reqId < mArtworkBoundId) {
                    return@Runnable
                }
                mArtworkBoundId = reqId
                if (colorSchemeChanged) {
                    updateForegroundColors(holder, colorConfig)
                    mCurrColorConfig = colorConfig
                }

                // Bind the album view to the artwork or a transition drawable
                holder.mediaBg.setPadding(0, 0, 0, 0)
                if (isArtWorkUpdate || (!mIsArtworkBound && isArtworkBound)) {
                    holder.mediaBg.setImageDrawable(mArtworkDrawable)
                    mArtworkDrawable?.updateAlbumCover(processedArtwork, colorConfig)
                    mIsArtworkBound = isArtworkBound
                }
            })
        }
    }

    data class MiuiMediaViewHolder(
        var innerHashCode: Int,
        var titleText: TextView,
        var artistText: TextView,
        var albumView: ImageView,
        var mediaBg: ImageView,
        var seamlessIcon: ImageView,
        var action0: ImageButton,
        var action1: ImageButton,
        var action2: ImageButton,
        var action3: ImageButton,
        var action4: ImageButton,
        var elapsedTimeView: TextView,
        var totalTimeView: TextView,
        var seekBar: SeekBar
    )
}