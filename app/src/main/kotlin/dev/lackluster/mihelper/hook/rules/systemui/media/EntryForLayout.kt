package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.view.View
import androidx.compose.ui.unit.Constraints
import androidx.constraintlayout.widget.ConstraintSet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
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
import dev.lackluster.mihelper.utils.factory.dp

object EntryForLayout : YukiBaseHooker() {
    private val constraintSetClass by lazy {
        "androidx.constraintlayout.widget.ConstraintSet".toClass()
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
    private val getWidth by lazy {
        constraintSetClass.method {
            name = "getWidth"
            paramCount = 1
            param(IntType)
        }.give()
    }
    private val constrainWidth by lazy {
        constraintSetClass.method {
            name = "constrainWidth"
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
    override fun onHook() {
        "com.android.systemui.media.controls.ui.controller.MediaViewController".toClassOrNull()?.apply {
            method {
                name = "loadLayoutForType"
            }.hook {
                after {
                    val context = this.instance.current().field {
                        name = "context"
                    }.cast<Context>() ?: return@after
                    val expandedLayout = this.instance.current().field {
                        name = "expandedLayout"
                    }.any() ?: return@after
                    setVisibility?.invoke(expandedLayout, icon, View.GONE)
                    connect?.invoke(
                        expandedLayout,
                        header_title, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START
                    )
                    connect?.invoke(
                        expandedLayout,
                        header_artist, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START
                    )
                    connect?.invoke(
                        expandedLayout,
                        actions, ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP
                    )
                    connect?.invoke(
                        expandedLayout,
                        action0, ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP
                    )
                    setMargin?.invoke(expandedLayout, header_title, ConstraintSet.TOP, 26.dp(context))
                    setMargin?.invoke(expandedLayout, header_title, ConstraintSet.START, 26.dp(context))
                    setMargin?.invoke(expandedLayout, header_artist, ConstraintSet.START, 26.dp(context))
                    setMargin?.invoke(expandedLayout, actions, ConstraintSet.TOP, 68.5.dp(context))
                    setMargin?.invoke(expandedLayout, action0, ConstraintSet.TOP, 79.5.dp(context))
                    setVisibility?.invoke(expandedLayout, album_art, View.GONE)
                    clear?.invoke(expandedLayout, action4, ConstraintSet.RIGHT)
//                    val a: ConstraintSet
//                    a.setGoneMargin()
//                    a.createBarrier()
//                    a.clear()
                }
            }
        }
    }
}