package dev.lackluster.mihelper.hook.rules.systemui

import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
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
    }
}