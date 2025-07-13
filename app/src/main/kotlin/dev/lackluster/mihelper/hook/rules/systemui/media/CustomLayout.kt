package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
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
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp

object CustomLayout : YukiBaseHooker() {
    private val album = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ALBUM, 0)
    private val actionsLeftAligned = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS, false)
    private val actionsOrder = Prefs.getInt(Pref.Key.SystemUI.MediaControl.LYT_ACTIONS_ORDER, 0)
    private val hideTime = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME, false)
    private val hideSeamless = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS, false)
    private val headerMargin = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.LYT_HEADER_MARGIN, 21.0f)
    private val headerPadding = Prefs.getFloat(Pref.Key.SystemUI.MediaControl.LYT_HEADER_PADDING, 2.0f)

    private val mediaButtonClass by lazy {
        "com.android.systemui.media.controls.shared.model.MediaButton".toClassOrNull()
            ?: "com.android.systemui.media.controls.models.player.MediaButton".toClassOrNull()
    }
    private val constraintSetClass by lazy {
        "androidx.constraintlayout.widget.ConstraintSet".toClass()
    }
    private val mediaViewControllerClass by lazy {
        "com.android.systemui.media.controls.ui.controller.MediaViewController".toClassOrNull()
            ?: "com.android.systemui.media.controls.ui.MediaViewController".toClassOrNull()
    }
    private val clear by lazy {
        constraintSetClass.method {
            name = "clear"
            paramCount = 2
            param(IntType, IntType)
        }.give()
    }
    private val setVisibility by lazy {
        constraintSetClass.method {
            name = "setVisibility"
            paramCount = 2
            param(IntType, IntType)
        }.give()
    }
    private val connect by lazy {
        constraintSetClass.method {
            name = "connect"
            paramCount = 4
            param(IntType, IntType, IntType, IntType)
        }.give()
    }
    private val setMargin by lazy {
        constraintSetClass.method {
            name = "setMargin"
            paramCount = 3
            param(IntType, IntType, IntType)
        }.give()
    }
    private val setGoneMargin by lazy {
        constraintSetClass.method {
            name = "setGoneMargin"
            paramCount = 3
            param(IntType, IntType, IntType)
        }.give()
    }
    override fun onHook() {
        if (actionsOrder != 0) {
            mediaButtonClass?.apply {
                method {
                    name = "getActionById"
                }.hook {
                    before {
                        val id = this.args(0).int()
                        when (id) {
                            action0 -> {
                                this.result =
                                    if (actionsOrder == 1) this.instance.current().field { name = "prevOrCustom" }.any()
                                    else this.instance.current().field { name = "playOrPause" }.any()
                            }
                            action1 -> {
                                this.result =
                                    if (actionsOrder == 1) this.instance.current().field { name = "playOrPause" }.any()
                                    else this.instance.current().field { name = "prevOrCustom" }.any()
                            }
                            action2 -> this.result = this.instance.current().field { name = "nextOrCustom" }.any()
                            action3 -> this.result = this.instance.current().field { name = "custom0" }.any()
                            action4 -> this.result = this.instance.current().field { name = "custom1" }.any()
                        }
                    }
                }
            }
        }
        if (album != 0 || actionsLeftAligned || hideTime || hideSeamless || headerMargin != 26.0f || headerPadding != 2.0f) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaNotificationControllerImpl".toClassOrNull()?.apply {
                constructor().hook {
                    after {
                        val context = this.instance.current().field {
                            name = "context"
                        }.cast<Context>() ?: return@after
                        val normalLayout = this.instance.current().field {
                            name = "normalLayout"
                        }.any() ?: return@after
                        updateConstraintSet(context, normalLayout)
                    }
                }
            }
            if (hideTime) {
                "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl".toClassOrNull()?.apply {
                    method {
                        name = "onFullAodStateChanged"
                    }.hook {
                        after {
                            val holder = this.instance.current().field {
                                name = "holder"
                            }.any() ?: return@after
                            holder.current().field { name = "elapsedTimeView" }.cast<TextView>()?.visibility = View.GONE
                            holder.current().field { name = "totalTimeView" }.cast<TextView>()?.visibility = View.GONE
                        }
                    }
                }
            }
            mediaViewControllerClass?.apply {
                var newMediaPanel = false
                method {
                    name = "loadLayoutForType"
                }.ignored().onNoSuchMethod {
                    newMediaPanel = true
                }.hook {
                    after {
                        val context = this.instance.current().field {
                            name = "context"
                        }.cast<Context>() ?: return@after
                        val expandedLayout = this.instance.current().field {
                            name = "expandedLayout"
                        }.any() ?: return@after
                        updateConstraintSet(context, expandedLayout)
                    }
                }
                if (hideTime && !newMediaPanel) {
                    method {
                        name = "refreshState"
                    }.hook {
                        before {
                            val expandedLayout = this.instance.current().field {
                                name = "expandedLayout"
                            }.any() ?: return@before
                            setVisibility?.invoke(expandedLayout, media_elapsed_time, View.GONE)
                            setVisibility?.invoke(expandedLayout, media_total_time, View.GONE)
                        }
                    }
                }
            }
        }
    }

    private fun updateConstraintSet(context: Context, constraintSet: Any) {
        val standardMargin = 26.dp(context)
        if (album != 0) {
            setVisibility?.invoke(constraintSet, icon, View.GONE)
        }
        if (album == 2) {
//                            connect?.invoke(expandedLayout,
//                                header_title, ConstraintSet.START,
//                                ConstraintSet.PARENT_ID, ConstraintSet.START
//                            )
//                            connect?.invoke(expandedLayout,
//                                header_artist, ConstraintSet.START,
//                                ConstraintSet.PARENT_ID, ConstraintSet.START
//                            )
//                            connect?.invoke(expandedLayout,
//                                actions, ConstraintSet.TOP,
//                                ConstraintSet.PARENT_ID, ConstraintSet.TOP
//                            )
//                            connect?.invoke(expandedLayout,
//                                action0, ConstraintSet.TOP,
//                                ConstraintSet.PARENT_ID, ConstraintSet.TOP
//                            )
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.START, standardMargin)
            setGoneMargin?.invoke(constraintSet, header_artist, ConstraintSet.START, standardMargin)
            setGoneMargin?.invoke(constraintSet, actions, ConstraintSet.TOP, 68.5.dp(context))
            setGoneMargin?.invoke(constraintSet, action0, ConstraintSet.TOP, 79.5.dp(context))
            setVisibility?.invoke(constraintSet, album_art, View.GONE)
        }
        if (headerMargin != 21.0f) {
            val headerMarginTop = headerMargin.dp(context)
            setMargin?.invoke(constraintSet, header_title, ConstraintSet.TOP, headerMarginTop)
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.TOP, headerMarginTop)
        }
        if (headerPadding != 2.0f) {
            setMargin?.invoke(constraintSet, header_artist, ConstraintSet.TOP, headerPadding.dp(context))
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
//                            connect?.invoke(expandedLayout,
//                                header_title, ConstraintSet.END,
//                                ConstraintSet.PARENT_ID, ConstraintSet.END
//                            )
//                            connect?.invoke(expandedLayout,
//                                header_artist, ConstraintSet.END,
//                                ConstraintSet.PARENT_ID, ConstraintSet.END
//                            )
            setGoneMargin?.invoke(constraintSet, header_title, ConstraintSet.END, standardMargin)
            setGoneMargin?.invoke(constraintSet, header_artist, ConstraintSet.END, standardMargin)
        }
        if (hideTime) {
            setVisibility?.invoke(constraintSet, media_elapsed_time, View.GONE)
            setVisibility?.invoke(constraintSet, media_total_time, View.GONE)
        }
    }
}