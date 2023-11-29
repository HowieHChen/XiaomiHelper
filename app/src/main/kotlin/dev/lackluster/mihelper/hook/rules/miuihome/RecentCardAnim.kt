package dev.lackluster.mihelper.hook.rules.miuihome

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.MotionEvent
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object RecentCardAnim : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_RECENT_ANIM) {
            val swipeHelperForRecentsCls =
                "com.miui.home.recents.views.SwipeHelperForRecents".toClass()
            val taskStackViewLayoutStyleHorizontalCls =
                "com.miui.home.recents.TaskStackViewLayoutStyleHorizontal".toClass()
            val deviceConfigCls =
                "com.miui.home.launcher.DeviceConfig".toClass()
            val physicBasedInterpolatorCls =
                "com.miui.home.launcher.anim.PhysicBasedInterpolator".toClass()
            val verticalSwipeCls =
                "com.miui.home.recents.views.VerticalSwipe".toClass()

            swipeHelperForRecentsCls.method {
                name = "onTouchEvent"
                param(MotionEvent::class.java)
            }.hook {
                after {
                    val mCurrView = this.instance.current().field {
                        name = "mCurrView"
                        superClass()
                    }.cast<View>()
                    mCurrView?.let {
                        mCurrView.alpha *= 0.9f + 0.1f
                        mCurrView.scaleX = 1f
                        mCurrView.scaleY = 1f
                    }
                }
            }

            taskStackViewLayoutStyleHorizontalCls.method {
                name = "createScaleDismissAnimation"
                param(View::class.java, Float::class.java)
            }.hook {
                before {
                    val view = this.args(0).any() as View
                    val getScreenHeight = deviceConfigCls.method {
                        name = "getScreenHeight"
                        modifiers {
                            isStatic
                        }
                    }.get().int()
                    val ofFloat = ObjectAnimator.ofFloat(
                        view,
                        View.TRANSLATION_Y,
                        view.translationY,
                        -getScreenHeight * 1.1484375f
                    )
                    val physicBasedInterpolator = physicBasedInterpolatorCls.constructor{
                        paramCount = 2
                        param(Float::class.java, Float::class.java)
                    }.get().newInstance<TimeInterpolator>(0.72f, 0.72f)
                    ofFloat.interpolator = physicBasedInterpolator
                    ofFloat.duration = 450L
                    this.result = ofFloat
                }
            }

            verticalSwipeCls.method {
                name = "calculate"
                param(Float::class.java)
            }.hook {
                after {
                    val f = this.args(0).float()
                    val asScreenHeightWhenDismiss = verticalSwipeCls.method {
                        name = "getAsScreenHeightWhenDismiss"
                        modifiers {
                            isStatic
                        }
                    }.get().int()
                    val f2 = f / asScreenHeightWhenDismiss
                    val mTaskViewHeight = this.instance.current().field {
                        name = "mTaskViewHeight"
                    }.float()
                    val mCurScale = this.instance.current().field {
                        name = "mCurScale"
                    }.float()
                    val f3: Float = mTaskViewHeight * mCurScale
                    val i =
                        if (f2 > 0.0f) 1
                        else if (f2 == 0.0f) 0
                        else -1
                    val afterFrictionValue: Float = this.instance.current().method {
                        name = "afterFrictionValue"
                    }.float(f, asScreenHeightWhenDismiss)
                    if (i < 0)
                        this.instance.current().field {
                            name = "mCurTransY"
                        }.set((mTaskViewHeight / 2f + afterFrictionValue * 2f) - (f3 / 2f))
                    val mCurAlpha = this.instance.current().field {
                        name = "mCurAlpha"
                    }.float()
                    this.instance.current().field {
                        name = "mCurAlpha"
                    }.set(mCurAlpha * 0.9f + 0.1f)
                }
            }
        }
    }
}