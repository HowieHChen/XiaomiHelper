package dev.lackluster.mihelper.hook.rules.systemui

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import kotlin.math.roundToInt

object HideBatteryIcon : YukiBaseHooker() {
    private val hideBattery by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY, false)
    }
    private val hideBatteryPercent by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY_PERCENT, false)
    }
    private val batteryPercentMarkSize by lazy {
        Prefs.getFloat(PrefKey.STATUSBAR_BATTERY_PERCENT_SIZE, 0f)
    }
    private val batteryPaddingLeft by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_BATTERY_PADDING_LEFT, 0)
    }
    private val batteryPaddingRight by lazy {
        Prefs.getInt(PrefKey.STATUSBAR_BATTERY_PADDING_RIGHT, 0)
    }
    override fun onHook() {
        "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClass()
            .method {
                name = "updateResources"
            }
            .hook {
                after {
                    val batteryPercentView = this.instance.current().field {
                        name = "mBatteryPercentView"
                    }.any() as? TextView ?: return@after
                    val batteryPercentMarkView = this.instance.current().field {
                        name = "mBatteryPercentMarkView"
                    }.any() as? TextView ?: return@after
                    // Hide Battery Icon
                    if (hideBattery) {
                        (this.instance.current().field {
                            name = "mBatteryIconView"
                        }.any() as? ImageView)?.visibility = View.GONE
                        if (
                            this.instance.current().field {
                                name = "mBatteryStyle"
                            }.int() == 1
                        ) {
                            (this.instance.current().field {
                                name = "mBatteryDigitalView"
                            }.any() as? FrameLayout)?.visibility = View.GONE
                        }
                    }
                    // Modify the font size of the battery percentage numbers
                    hasEnable(PrefKey.STATUSBAR_CHANGE_BATTERY_PERCENT_SIZE, extraCondition = { batteryPercentMarkSize > 0}) {
                        batteryPercentView.setTextSize(0, batteryPercentMarkSize)
                    }
                    if (hideBatteryPercent) {
                        // Hide Percentage Symbol
                        (this.instance.current().field {
                            name = "mBatteryPercentMarkView"
                        }.any() as? TextView)?.textSize = 0f
                    }
                    else if (Prefs.getBoolean(PrefKey.STATUSBAR_CHANGE_BATTERY_PERCENT_MARK, false)) {
                        // Align the text size of the percentage sign with the number
                        batteryPercentMarkView.layoutParams = batteryPercentView.layoutParams
                        batteryPercentMarkView.typeface = batteryPercentView.typeface
                        batteryPercentMarkView.setTextSize(0, batteryPercentView.textSize)
                        batteryPercentMarkView.setPadding(0,0,0,0)
                    }
                    if (
                        Prefs.getBoolean(PrefKey.STATUSBAR_SWAP_BATTERY_PERCENT, false) &&
                        !hideBattery
                    ) {
                        val batteryView = this.instance as LinearLayout
                        batteryView.removeView(batteryPercentView)
                        batteryView.removeView(batteryPercentMarkView)
                        batteryView.addView(batteryPercentMarkView, 0)
                        batteryView.addView(batteryPercentView, 0)
                    }
                    hasEnable(PrefKey.STATUSBAR_BATTERY_CUSTOM) {
                        val batteryView = this.instance as LinearLayout
                        val scale = batteryView.context.resources.displayMetrics.density
                        batteryView.setPadding(
                            (batteryPaddingLeft * scale).roundToInt(),//batteryView.paddingLeft,
                            batteryView.paddingTop,
                            (batteryPaddingRight * scale).roundToInt(),//batteryView.paddingRight,
                            batteryView.paddingBottom
                        )
                    }
                }
            }
        hasEnable(PrefKey.STATUSBAR_HIDE_CHARGE) {
            "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClass()
                .method {
                    name = "updateChargeAndText"
                }
                .hook {
                    after {
                        (this.instance.current().field {
                            name = "mBatteryChargingInView"
                        }.any() as? ImageView)?.visibility = View.GONE
                        (this.instance.current().field {
                            name = "mBatteryChargingView"
                        }.any() as? ImageView)?.visibility = View.GONE
                    }
                }
        }
    }
}