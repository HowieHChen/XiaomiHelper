package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import java.util.HashMap

object FontScale : YukiBaseHooker() {
    private val fontScale = Prefs.getFloat(Pref.Key.Android.FONT_SCALE_VAL, 0.9f)

    @Suppress ("UNCHECKED_CAST")
    override fun onHook() {
        hasEnable(Pref.Key.Android.FONT_SCALE) {
            "com.android.settings.display.LargeFontUtils".toClass().apply {
                val fieldFontScale = field {
                    name = "FONT_SCALE"
                }.get().any() as? FloatArray ?: return@hasEnable
                val fieldUiModeFontScaleMapping = field {
                    name = "UI_MODE_FONT_SCALE_MAPPING"
                }.get().any() as? HashMap<Int, Float> ?: return@hasEnable
                val fieldSUiModeMapping = field {
                    name = "sUI_MODE_MAPPING"
                }.get().any() as? HashMap<Float, Int> ?: return@hasEnable
                val oldFontScale = fieldFontScale[0]
                fieldFontScale[0] = fontScale
                fieldUiModeFontScaleMapping.put(12, fontScale)
                fieldSUiModeMapping.put(fontScale, 12)
                fieldSUiModeMapping.remove(oldFontScale)
            }
            "com.android.settings.display.PageLayoutFragment".toClass().apply {
                val fieldPageLayoutMapping = field {
                    name = "PAGE_LAYOUT_MAPPING"
                }.get().any() as? HashMap<Int, Float> ?: return@hasEnable
                fieldPageLayoutMapping.put(0, fontScale)
            }
        }
    }
}