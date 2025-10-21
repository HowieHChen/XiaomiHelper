package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_170
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_200
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_EXTRAL_SMALL
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_GODZILLA
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_HUGE
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_LARGE
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_MEDIUM
import dev.lackluster.mihelper.data.Constants.UI_MODE_TYPE_SCALE_SMALL
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object FontScale : YukiBaseHooker() {
    private val fontScaleSmall = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_SMALL, 0.9f)
    private val fontScaleMedium = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_MEDIUM, 1.0f)
    private val fontScaleLarge = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_LARGE, 1.1f)
    private val fontScaleHuge = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_HUGE, 1.25f)
    private val fontScaleGodzilla = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_GODZILLA, 1.45f)
    private val fontScale170 = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_170, 1.7f)
    private val fontScale200 = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_200, 2.0f)

    override fun onHook() {
        hasEnable(Pref.Key.Android.FONT_SCALE) {
            "android.content.res.MiuiConfiguration".toClass().apply {
                resolve()
                    .firstMethodOrNull {
                        name = "getFontScale"
                    }
                    ?.hook {
                        before {
                            this.result = when (this.args(0).int()) {
                                UI_MODE_TYPE_SCALE_EXTRAL_SMALL -> fontScaleSmall
                                UI_MODE_TYPE_SCALE_SMALL -> fontScaleSmall
                                UI_MODE_TYPE_SCALE_MEDIUM -> fontScaleMedium
                                UI_MODE_TYPE_SCALE_LARGE -> fontScaleLarge
                                UI_MODE_TYPE_SCALE_HUGE -> fontScaleHuge
                                UI_MODE_TYPE_SCALE_GODZILLA -> fontScaleGodzilla
                                UI_MODE_TYPE_SCALE_170 -> fontScale170
                                UI_MODE_TYPE_SCALE_200 -> fontScale200
                                else -> fontScaleMedium
                            }
                        }
                    }
            }
        }
    }
}