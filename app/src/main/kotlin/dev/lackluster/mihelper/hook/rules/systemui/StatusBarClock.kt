package dev.lackluster.mihelper.hook.rules.systemui

import android.graphics.Color
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import kotlin.math.roundToInt

object StatusBarClock : YukiBaseHooker() {
    private val clockPaddingLeft by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_LEFT, 0)
    }
    private val clockPaddingRight by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_RIGHT, 0)
    }
    override fun onHook() {
        hasEnable(PrefKey.STATUSBAR_CLOCK_CUSTOM) {
            "com.android.systemui.statusbar.views.MiuiClock".toClass()
                .constructor {
                    paramCount = 3
                }
                .hook {
                    after {
                        val miuiClock = this.instance as TextView
                        val scale = miuiClock.context.resources.displayMetrics.density
                        miuiClock.setPadding(
                            (clockPaddingLeft * scale).roundToInt(),
                            miuiClock.paddingTop,
                            (clockPaddingRight * scale).roundToInt(),
                            miuiClock.paddingBottom
                        )
                    }
                }
        }
        hasEnable(PrefKey.STATUSBAR_CLOCK_COLOR_FIX) {
            "com.android.systemui.controlcenter.phone.widget.NotificationShadeFakeStatusBarClock".toClass()
                .method {
                    name = "updateHeaderColor"
                }
                .hook {
                    replaceUnit {
                        val bigTimeColor = this.instance.current().field {
                            name = "bigTimeColor"
                        }.any() as Color
                        val tintColor = Color.valueOf(this.instance.current().field {
                            name = "mTint"
                        }.int())
                        val lightColor = Color.valueOf(this.instance.current().field {
                            name = "mLightColor"
                        }.int())
                        val darkColor = Color.valueOf(this.instance.current().field {
                            name = "mDarkColor"
                        }.int())
                        val whiteFraction = this.instance.current().field {
                            name = "mWhiteFraction"
                        }.float()
                        val areas = this.instance.current().field {
                            name = "mAreas"
                        }.any() as ArrayList<*>
                        val darkIntensity = this.instance.current().field {
                            name = "mDarkIntensity"
                        }.float()
                        val useTint = this.instance.current().field {
                            name = "mUseTint"
                        }.boolean()
                        val inTintColor = Color.argb(
                            lerp(tintColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                            lerp(tintColor.red(), bigTimeColor.red(), whiteFraction),
                            lerp(tintColor.green(), bigTimeColor.green(), whiteFraction),
                            lerp(tintColor.blue(), bigTimeColor.blue(), whiteFraction)
                        )
                        val inLightColor = Color.argb(
                            lerp(lightColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                            lerp(lightColor.red(), bigTimeColor.red(), whiteFraction),
                            lerp(lightColor.green(), bigTimeColor.green(), whiteFraction),
                            lerp(lightColor.blue(), bigTimeColor.blue(), whiteFraction)
                        )
                        val inDarkColor = Color.argb(
                            lerp(darkColor.alpha(), bigTimeColor.alpha(), whiteFraction),
                            lerp(darkColor.red(), bigTimeColor.red(), whiteFraction),
                            lerp(darkColor.green(), bigTimeColor.green(), whiteFraction),
                            lerp(darkColor.blue(), bigTimeColor.blue(), whiteFraction)
                        )
                        this.instance.current().method {
                            name = "getBigTime"
                        }.call()?.current()?.method {
                            name = "onDarkChanged"
                        }?.call(areas, darkIntensity, inTintColor, inLightColor, inDarkColor, useTint)
                    }
                }
        }
    }

    private fun lerp(start: Float, stop: Float, amount: Float): Float {
        return start + (stop - start) * amount
    }
}