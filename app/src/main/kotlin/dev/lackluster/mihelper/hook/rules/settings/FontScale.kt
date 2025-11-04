package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
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

    @Suppress ("UNCHECKED_CAST")
    override fun onHook() {
        hasEnable(Pref.Key.Android.FONT_SCALE) {
            "com.android.settings.display.LargeFontUtils".toClass().apply {
                resolve().firstFieldOrNull {
                    name = "FONT_SCALE"
                    modifiers(Modifiers.STATIC)
                }?.set(
                    floatArrayOf(
                        fontScaleSmall,
                        fontScaleMedium,
                        fontScaleLarge,
                        fontScaleHuge,
                        fontScaleGodzilla,
                        fontScale170,
                        fontScale200
                    )
                )
                resolve().firstFieldOrNull {
                    name = "UI_MODE_FONT_SCALE_MAPPING"
                    modifiers(Modifiers.STATIC)
                }?.set(
                    mutableMapOf(
                        UI_MODE_TYPE_SCALE_EXTRAL_SMALL to fontScaleSmall,
                        UI_MODE_TYPE_SCALE_SMALL to fontScaleSmall,
                        UI_MODE_TYPE_SCALE_MEDIUM to fontScaleMedium,
                        UI_MODE_TYPE_SCALE_LARGE to fontScaleLarge,
                        UI_MODE_TYPE_SCALE_HUGE to fontScaleHuge,
                        UI_MODE_TYPE_SCALE_GODZILLA to fontScaleGodzilla,
                        UI_MODE_TYPE_SCALE_170 to fontScale170,
                        UI_MODE_TYPE_SCALE_200 to fontScale200,
                    )
                )
                resolve().firstFieldOrNull {
                    name = "sUI_MODE_MAPPING"
                    modifiers(Modifiers.STATIC)
                }?.set(
                    mutableMapOf(
                        fontScaleSmall to UI_MODE_TYPE_SCALE_SMALL,
                        fontScaleMedium to UI_MODE_TYPE_SCALE_MEDIUM,
                        fontScaleLarge to UI_MODE_TYPE_SCALE_LARGE,
                        fontScaleHuge to UI_MODE_TYPE_SCALE_HUGE,
                        fontScaleGodzilla to UI_MODE_TYPE_SCALE_GODZILLA,
                        fontScale170 to UI_MODE_TYPE_SCALE_170,
                        fontScale200 to UI_MODE_TYPE_SCALE_200,
                    )
                )
            }
            "com.android.settings.display.PageLayoutFragment".toClass().apply {
                resolve().firstFieldOrNull {
                    name = "PAGE_LAYOUT_MAPPING"
                    modifiers(Modifiers.STATIC)
                }?.set(
                    mutableMapOf(
                        0 to fontScaleSmall,
                        1 to fontScaleMedium,
                        2 to fontScaleLarge,
                        3 to fontScaleHuge,
                        4 to fontScaleGodzilla,
                        5 to fontScale170,
                        6 to fontScale200,
                    )
                )
                resolve().firstMethodOrNull {
                    name = "getProgress"
                }?.hook {
                    before {
                        val mCurrentFontScale = this.instance.asResolver().firstFieldOrNull {
                            name = "mCurrentFontScale"
                        }?.get<Float>() ?: return@before
                        val index = when (mCurrentFontScale) {
                            fontScaleSmall -> 0
                            fontScaleMedium -> 1
                            fontScaleLarge -> 2
                            fontScaleHuge -> 3
                            fontScaleGodzilla -> 4
                            fontScale170 -> 5
                            fontScale200 -> 6
                            else -> -1
                        }
                        if (index != -1) {
                            this.result = index
                        }
                    }
                }
            }
        }
    }
}