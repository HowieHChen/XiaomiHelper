package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.graphics.Paint
import android.graphics.Typeface
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.utils.Prefs
import java.io.File
import kotlin.math.abs

object ElementsFontWeight : YukiBaseHooker() {
    val fontPath by lazy {
        val defaultPath = "/system/fonts/MiSansVF.ttf"
        val prefPath = Prefs.getString(FontWeight.FONT_PATH, defaultPath) ?: defaultPath
        val fontFile = File(prefPath)
        if (fontFile.exists() && fontFile.isFile) prefPath else defaultPath
    }
    private val clockFont = Prefs.getBoolean(FontWeight.CLOCK, false)
    private val clockFontWeight = Prefs.getInt(FontWeight.CLOCK_WEIGHT, 430)
    private val clockNotifFont = Prefs.getBoolean(FontWeight.CLOCK_NOTIFICATION, false)
    private val clockNotifFontWeight = Prefs.getInt(FontWeight.CLOCK_NOTIFICATION_WEIGHT, 305)
    private val focusNotifFont = Prefs.getBoolean(FontWeight.FOCUS_NOTIFICATION, false)
    private val focusNotifFontWeight = Prefs.getInt(FontWeight.FOCUS_NOTIFICATION_WEIGHT, 430)
    private val netSpeedNumFont = Prefs.getBoolean(FontWeight.NET_SPEED_NUMBER, false)
    private val netSpeedNumFontWeight = Prefs.getInt(FontWeight.NET_SPEED_NUMBER_WEIGHT, 700)
    private val netSpeedUnitFont = Prefs.getBoolean(FontWeight.NET_SPEED_UNIT, false)
    private val netSpeedUnitFontWeight = Prefs.getInt(FontWeight.NET_SPEED_UNIT_WEIGHT, 700)
    private val mobileTypeFont = Prefs.getBoolean(FontWeight.MOBILE_TYPE, false)
    private val mobileTypeFontWeight = Prefs.getInt(FontWeight.MOBILE_TYPE_WEIGHT, 620)

    private val miuiClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClassOrNull()
    }
    private val focusedTextClass by lazy {
        "com.android.systemui.statusbar.widget.FocusedTextView".toClassOrNull()
    }

    override fun onHook() {
        if (clockFont || focusNotifFont) {
            "com.miui.utils.configs.MiuiConfigs".toClassOrNull()?.apply {
                method {
                    name = "setMiuiStatusBarTypeface"
                }.hook {
                    before {
                        val textViews = this.args(0).array<TextView>()
                        if (textViews.size != 1) return@before
                        if (clockFont && miuiClockClass?.isInstance(textViews[0]) == true) {
                            val typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $clockFontWeight").build()
                            textViews[0].typeface = typeface
                            this.result = null
                        } else if (focusNotifFont && focusedTextClass?.isInstance(textViews[0]) == true) {
                            val typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $focusNotifFontWeight").build()
                            textViews[0].typeface = typeface
                            this.result = null
                        }
                    }
                }
            }
        }
        if (mobileTypeFont) {
            "com.android.systemui.statusbar.views.MobileTypeDrawable".toClassOrNull()?.apply {
                field {
                    name = "sMiproTypeface"
                    modifiers { isStatic }
                }.get().set(
                    Typeface.Builder(fontPath).setFontVariationSettings("'wght' $mobileTypeFontWeight").build()
                )
                method {
                    name = "setMiuiStatusBarTypeface"
                }.hook {
                    before {
                        val paints = this.args(0).array<Paint>()
                        val typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $mobileTypeFontWeight").build()
                        for (paint in paints) {
                            paint.typeface = typeface
                        }
                        this.result = null
                    }
                }
            }
        }
        if (netSpeedNumFont || netSpeedUnitFont) {
            "com.android.systemui.statusbar.views.NetworkSpeedView".toClassOrNull()?.apply {
                method {
                    name = "onDensityOrFontScaleChanged"
                }.hook {
                    after {
                        if (netSpeedNumFont) {
                            this.instance.current().field {
                                name = "mNetworkSpeedNumberText"
                            }.cast<TextView>()?.typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $netSpeedNumFontWeight").build()
                        }
                        if (netSpeedUnitFont) {
                            this.instance.current().field {
                                name = "mNetworkSpeedUnitText"
                            }.cast<TextView>()?.typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $netSpeedUnitFontWeight").build()
                        }
                    }
                }
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        if (netSpeedNumFont) {
                            this.instance.current().field {
                                name = "mNetworkSpeedNumberText"
                            }.cast<TextView>()?.typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $netSpeedNumFontWeight").build()
                        }
                        if (netSpeedUnitFont) {
                            this.instance.current().field {
                                name = "mNetworkSpeedUnitText"
                            }.cast<TextView>()?.typeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $netSpeedUnitFontWeight").build()
                        }
                    }
                }
            }
        }
        if (clockFont || clockNotifFont) {
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController".toClassOrNull()?.apply {
                val clockNotificationFontWeight = if (clockNotifFont) clockNotifFontWeight else 305
                val clockNotificationTypeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $clockNotificationFontWeight").build()
                field {
                    name = "MI_PRO_TYPEFACE"
                    modifiers { isStatic }
                }.get().set(clockNotificationTypeface)
                val clockStatusBarFontWeight = if (clockFont) clockFontWeight else 430
                val clockStatusBarTypeface = Typeface.Builder(fontPath).setFontVariationSettings("'wght' $clockStatusBarFontWeight").build()
                val sampleCount = abs(clockNotificationFontWeight - clockStatusBarFontWeight) / 10
                val sampleStep = (clockNotificationFontWeight - clockStatusBarFontWeight) / sampleCount
                val samples = ArrayList<Typeface>()
                samples.add(clockStatusBarTypeface)
                for (i in 1 until sampleCount) {
                    samples.add(
                        Typeface.Builder(fontPath).setFontVariationSettings("'wght' ${clockStatusBarFontWeight + sampleStep * i}").build()
                    )
                }
                samples.add(clockNotificationTypeface)
                field {
                    name = "typefaces"
                    modifiers { isStatic }
                }.get().set(samples)
            }
            "com.android.systemui.qs.MiuiNotificationHeaderView".toClassOrNull()?.apply {
                methods.firstOrNull {
                    it.name.startsWith("updateResources")
                }?.hook {
                    after {
                        this.instance.current().field {
                            name = "usingMiPro"
                        }.setTrue()
                    }
                }
            }
        }
    }
}