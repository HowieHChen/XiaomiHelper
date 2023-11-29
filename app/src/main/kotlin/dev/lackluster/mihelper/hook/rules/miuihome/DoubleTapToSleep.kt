package dev.lackluster.mihelper.hook.rules.miuihome

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DoubleTapToSleep : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_DOUBLE_TAP_TO_SLEEP) {
            "com.miui.home.launcher.Workspace".toClass()
                .constructor()
                .hookAll {
                    after {
                        var mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(this.instance, "mDoubleTapControllerEx")
                        if (mDoubleTapControllerEx != null) return@after
                        mDoubleTapControllerEx = DoubleTapController((this.args(0).any() as Context))
                        XposedHelpers.setAdditionalInstanceField(
                            this.instance,
                            "mDoubleTapControllerEx",
                            mDoubleTapControllerEx
                        )
                    }
                }

            "com.miui.home.launcher.Workspace".toClass()
                .method {
                    name = "dispatchTouchEvent"
                    param(MotionEvent::class.java)
                }
                .hook {
                    before {
                        val mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(this.instance, "mDoubleTapControllerEx") as DoubleTapController
                        if (!mDoubleTapControllerEx.isDoubleTapEvent(this.args(0).any() as MotionEvent)) return@before
                        val mCurrentScreenIndex = this.instance.current().field {
                            name = "mCurrentScreenIndex"
                            superClass()
                        }.int()
                        val cellLayout = this.instance.current().method {
                            name = "getCellLayout"
                        }.call(mCurrentScreenIndex)
                        if (cellLayout != null)
                            if (
                                cellLayout.current().method {
                                    name = "lastDownOnOccupiedCell"
                                }.boolean()
                            ) return@before
                        if (
                            this.instance.current().method {
                                name = "isInNormalEditingMode"
                            }.boolean()
                        ) return@before
                        val context = this.instance.current().method {
                            name = "getContext"
                            superClass()
                        }.call() as Context
                        context.sendBroadcast(
                            Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE")
                                .putExtra(
                                    "com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID",
                                    10
                                )
                        )
                    }
                }
        }
    }
}