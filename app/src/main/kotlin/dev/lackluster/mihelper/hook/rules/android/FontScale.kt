package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object FontScale : YukiBaseHooker() {
    private val fontScale = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_VAL, 0.9f)

    override fun onHook() {
        hasEnable(Pref.Key.Android.FONT_SCALE) {
            "android.content.res.MiuiConfiguration".toClass().apply {
                method {
                    name = "getFontScale"
                }.hook {
                    before {
                        when (this.args(0).int()) {
                            10,12 -> this.result = fontScale
                        }
                    }
                }
            }
        }
    }
}