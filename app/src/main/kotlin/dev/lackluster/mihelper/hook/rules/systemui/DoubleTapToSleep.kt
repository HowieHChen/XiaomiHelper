package dev.lackluster.mihelper.hook.rules.systemui

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import kotlin.math.abs

object DoubleTapToSleep : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_DOUBLE_TAP_TO_SLEEP) {
            (
                if (Device.androidVersion == Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    "com.android.systemui.shade.NotificationsQuickSettingsContainer"
                else
                    "com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer"
            ).toClass()
                .method {
                    name = "onFinishInflate"
                }
                .hook {
                    before {
                        val view = this.instance as View
                        XposedHelpers.setAdditionalInstanceField(
                            view,
                            "currentTouchTime",
                            0L
                        )
                        XposedHelpers.setAdditionalInstanceField(
                            view,
                            "currentTouchX",
                            0f
                        )
                        XposedHelpers.setAdditionalInstanceField(
                            view,
                            "currentTouchY",
                            0f
                        )
                        view.setOnTouchListener(View.OnTouchListener { v, motionEvent ->
                            if (motionEvent.action != MotionEvent.ACTION_DOWN) return@OnTouchListener false

                            var currentTouchTime = XposedHelpers.getAdditionalInstanceField(view, "currentTouchTime") as Long
                            var currentTouchX = XposedHelpers.getAdditionalInstanceField(view, "currentTouchX") as Float
                            var currentTouchY = XposedHelpers.getAdditionalInstanceField(view, "currentTouchY") as Float
                            val lastTouchTime = currentTouchTime
                            val lastTouchX = currentTouchX
                            val lastTouchY = currentTouchY

                            currentTouchTime = System.currentTimeMillis()
                            currentTouchX = motionEvent.x
                            currentTouchY = motionEvent.y

                            if (currentTouchTime - lastTouchTime < 250L
                                && abs(currentTouchX - lastTouchX) < 100f
                                && abs(currentTouchY - lastTouchY) < 100f
                            ) {
                                val keyguardMgr =
                                    v.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                                if (keyguardMgr.isKeyguardLocked) {
                                    XposedHelpers.callMethod(
                                        v.context.getSystemService(Context.POWER_SERVICE),
                                        "goToSleep",
                                        SystemClock.uptimeMillis()
                                    )
                                }
                                currentTouchTime = 0L
                                currentTouchX = 0f
                                currentTouchY = 0f
                            }
                            XposedHelpers.setAdditionalInstanceField(
                                view,
                                "currentTouchTime",
                                currentTouchTime
                            )
                            XposedHelpers.setAdditionalInstanceField(
                                view,
                                "currentTouchX",
                                currentTouchX
                            )
                            XposedHelpers.setAdditionalInstanceField(
                                view,
                                "currentTouchY",
                                currentTouchY
                            )
                            v.performClick()
                            false
                        })
                    }
                }
        }
    }
}