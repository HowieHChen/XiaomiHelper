package dev.lackluster.mihelper.hook.rules.systemui

import android.graphics.Typeface
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object LockScreenFont : YukiBaseHooker() {
    private val timeTextFont = Prefs.getBoolean(PrefKey.SYSTEMUI_LOCKSCREEN_TIME_FONT, false)
    private val dateTextFont = Prefs.getBoolean(PrefKey.SYSTEMUI_LOCKSCREEN_DATE_FONT, false)
    override fun onHook() {
        if (!timeTextFont && !dateTextFont) {
            return
        }
        if (timeTextFont) {
            for (clazz in setOf(
                "com.miui.clock.MiuiCenterHorizontalClock",
                "com.miui.clock.MiuiLeftTopClock",
                "com.miui.clock.MiuiLeftTopLargeClock",
                "com.miui.clock.MiuiVerticalClock",
            )) {
                clazz.toClass().method {
                    name = "updateViewsTextSize"
                }.hook {
                    after {
                        val mCurrentTime = this.instance.current().field {
                            name = "mTimeText"
                        }.any() as? TextView
                        mCurrentTime?.typeface = Typeface.DEFAULT
                    }
                }
            }
            "com.miui.clock.MiuiDualClock".toClass()
                .method {
                    name = "updateViewsTextSize"
                }
                .hook {
                    after {
                        val mLocalTime = this.instance.current().field {
                            name = "mLocalTime"
                        }.any() as? TextView
                        mLocalTime?.typeface = Typeface.DEFAULT
                        val mResidentTime = this.instance.current().field {
                            name = "mResidentTime"
                        }.any() as? TextView
                        mResidentTime?.typeface = Typeface.DEFAULT
                    }
                }
        }
        if (dateTextFont) {
            "com.miui.clock.MiuiLeftTopLargeClock".toClass()
                .method {
                    name = "onLanguageChanged"
                    param(StringClass)
                }
                .hook {
                    after {
                        val mCurrentDateLarge = this.instance.current().field {
                            name = "mCurrentDateLarge"
                        }.any() as? TextView
                        mCurrentDateLarge?.typeface = Typeface.DEFAULT
                    }
                }
        }
    }
}