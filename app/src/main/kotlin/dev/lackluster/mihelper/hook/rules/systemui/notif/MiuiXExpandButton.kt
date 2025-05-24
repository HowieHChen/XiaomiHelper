package dev.lackluster.mihelper.hook.rules.systemui.notif

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object MiuiXExpandButton : YukiBaseHooker() {
    private var expandButtonSize: Int? = null
    private var expand: Drawable? = null
    private var shrink: Drawable? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.MIUIX_EXPAND_BUTTON) {
            "com.android.internal.widget.NotificationExpandButton".toClassOrNull()?.apply {
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        val mIconView = this.instance.current().field {
                            name = "mIconView"
                        }.cast<ImageView>() ?: return@after
                        this.instance.current().field {
                            name = "mPillDrawable"
                        }.set(Color.TRANSPARENT.toDrawable())
                        (mIconView.parent as? ViewGroup)?.background = null
                        mIconView.setPadding(0, 0, 0, 0)
                        if (expandButtonSize == null || expand == null || shrink == null) {
                            val context = mIconView.context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                            expand = context.getDrawable(R.drawable.miuix_btn_expand_pressed)
                            shrink = context.getDrawable(R.drawable.miuix_btn_shrink_pressed)
                            expandButtonSize = context.resources.getDimensionPixelSize(R.dimen.miuix_btn_size)
                        }
                        expandButtonSize?.let {
                            mIconView.layoutParams = mIconView.layoutParams.apply {
                                width = it
                                height = it
                            }
                        }
                    }
                }
                method {
                    name = "updateExpandedState"
                }.hook {
                    before {
                        val mExpanded = this.instance.current().field {
                            name = "mExpanded"
                        }.boolean()
                        val mIconView = this.instance.current().field {
                            name = "mIconView"
                        }.cast<ImageView>()
                        if (mIconView == null || shrink == null || expand == null) return@before
                        mIconView.setImageDrawable(if (mExpanded) shrink else expand)
                        this.result = null
                    }
                }
            }
        }
    }
}