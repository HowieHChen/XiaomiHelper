package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
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
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp

object CustomLayout : YukiBaseHooker() {
    private val ncAlbum = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ALBUM, 0)
    private val ncActionsLeftAligned = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS, false)
    private val ncActionsOrder = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ACTIONS_ORDER, 0)
    private val ncHideTime = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME, false)
    private val ncHideSeamless = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS, false)
    private val ncHeaderMargin = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.LYT_HEADER_MARGIN, 21.0f)
    private val ncHeaderPadding = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.LYT_HEADER_PADDING, 4.0f)

    private val diAlbum = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.LYT_ALBUM, 0)
    private val diActionsLeftAligned = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.LYT_LEFT_ACTIONS, false)
    private val diActionsOrder = Prefs.getInt(Pref.Key.DynamicIsland.MediaControl.LYT_ACTIONS_ORDER, 0)
    private val diHideTime = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.LYT_HIDE_TIME, false)
    private val diHideSeamless = Prefs.getBoolean(Pref.Key.DynamicIsland.MediaControl.LYT_HIDE_SEAMLESS, false)
    private val diHeaderMargin = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.LYT_HEADER_MARGIN, 21.0f)
    private val diHeaderPadding = Prefs.getFloat(Pref.Key.DynamicIsland.MediaControl.LYT_HEADER_PADDING, 4.0f)

    override fun onHook() {
        if (ncAlbum != 0 || ncActionsLeftAligned || ncHideTime || ncHideSeamless || ncHeaderMargin != 21.0f || ncHeaderPadding != 4.0f) {
            clzMiuiMediaNotificationControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }
                val fldNormalLayout = resolve().firstFieldOrNull {
                    name = "normalLayout"
                }
                val fldNormalAlbumLayout = resolve().firstFieldOrNull {
                    name = "normalAlbumLayout"
                }
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("loadLayout")
                    }
                }?.hook {
                    after {
                        val context = fldContext?.copy()?.of(this.instance)?.get<Context>() ?: return@after
                        val normalLayout = fldNormalLayout?.copy()?.of(this.instance)?.get() ?: return@after
                        val normalAlbumLayout = fldNormalAlbumLayout?.copy()?.of(this.instance)?.get() ?: return@after
                        updateLayoutConstraintSet(context, normalLayout, false)
                        if (ncAlbum != 0) {
                            setVisibility?.invoke(normalAlbumLayout, icon, View.GONE)
                        }
                    }
                }
            }
            if (ncHideTime) {
                clzMiuiMediaViewControllerImpl?.apply {
                    val holder = resolve().firstFieldOrNull {
                        name = "holder"
                    }?.self
                    val fldElapsedTimeView = clzMiuiMediaViewHolder?.resolve()?.firstFieldOrNull {
                        name = "elapsedTimeView"
                    }?.self
                    val fldTotalTimeView = clzMiuiMediaViewHolder?.resolve()?.firstFieldOrNull {
                        name = "totalTimeView"
                    }?.self
                    resolve().firstMethodOrNull {
                        name = "onFullAodStateChanged"
                    }?.hook {
                        after {
                            val vh = holder?.get(this.instance) ?: return@after
                            (fldElapsedTimeView?.get(vh) as? TextView)?.visibility = View.GONE
                            (fldTotalTimeView?.get(vh) as? TextView)?.visibility = View.GONE
                        }
                    }
                }
            }
        }
        if (ncHideSeamless) {
            clzMiuiMediaViewControllerImpl?.resolve()?.firstMethodOrNull {
                name = "setSeamless"
            }?.hook {
                intercept()
            }
        }
        if (diAlbum != 0 || diActionsLeftAligned || diHideTime || diHideSeamless || diHeaderMargin != 21.0f || diHeaderPadding != 4.0f) {
            "com.android.systemui.statusbar.notification.mediaisland.MiuiIslandMediaControllerImpl".toClassOrNull()?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }
                val fldNormalLayoutIsland = resolve().firstFieldOrNull {
                    name = "normalLayoutIsland"
                }
                val fldMiuiPlayerHolder = resolve().firstFieldOrNull {
                    name = "miuiPlayerHolder"
                }?.self
                val fldMiuiDummyPlayerHolder = resolve().firstFieldOrNull {
                    name = "miuiDummyPlayerHolder"
                }?.self
                val fldAppIcon = clzMiuiIslandMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = "appIcon"
                }?.self
                val fldPlayer = clzMiuiIslandMediaViewHolder?.resolve()?.firstFieldOrNull {
                    name = "player"
                }?.self
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("reInflateView")
                    }
                }?.hook {
                    after {
                        val context = fldContext?.copy()?.of(this.instance)?.get<Context>() ?: return@after
                        val normalLayoutIsland = fldNormalLayoutIsland?.copy()?.of(this.instance)?.get() ?: return@after
                        updateLayoutConstraintSet(context, normalLayoutIsland, true)
                        val miuiPlayerHolder = fldMiuiPlayerHolder?.get(this.instance)
                        val miuiDummyPlayerHolder = fldMiuiDummyPlayerHolder?.get(this.instance)
                        miuiPlayerHolder?.let { it1 -> applyTo?.invoke(normalLayoutIsland, fldPlayer?.get(it1)) }
                        miuiDummyPlayerHolder?.let { it1 -> applyTo?.invoke(normalLayoutIsland, fldPlayer?.get(it1)) }
                        if (diAlbum != 0) {
                            miuiPlayerHolder?.let { it1 -> (fldAppIcon?.get(it1) as? ImageView)?.visibility = View.GONE }
                            miuiDummyPlayerHolder?.let { it1 -> (fldAppIcon?.get(it1) as? ImageView)?.visibility = View.GONE }
                        }
                    }
                }
            }
            "com.android.systemui.statusbar.notification.mediaisland.PlayerIslandConstraintLayout".toClassOrNull()?.apply {
                val fldNormalLayoutIsland = resolve().firstFieldOrNull {
                    name = "normalLayoutIsland"
                }
                resolve().firstConstructor {
                    parameterCount = 3
                }.hook {
                    after {
                        val context = this.args(0).cast<Context>() ?: return@after
                        val normalLayoutIsland = fldNormalLayoutIsland?.copy()?.of(this.instance)?.get() ?: return@after
                        updateLayoutConstraintSet(context, normalLayoutIsland, true)
                    }
                }
            }
        }
        if (diHideSeamless) {
            clzMiuiIslandMediaViewBinderImpl?.resolve()?.firstMethodOrNull {
                name = "setSeamless"
            }?.hook {
                intercept()
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