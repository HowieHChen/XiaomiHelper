package dev.lackluster.mihelper.hook.rules.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.getResID

object ResourcesUtils : YukiBaseHooker() {
    private const val PKG_NAME = Scope.SYSTEM_UI
    private var isInitialized = false
    // Status bar clock
    var clock = 0
    var pad_clock = 0
    var big_time = 0
    var horizontal_time = 0
    var date_time = 0
    var miui_notification_menu_more_setting = 0
    var fmt_time_12hour_minute_second_pm = 0
    var fmt_time_12hour_minute_second = 0
    var fmt_time_12hour_minute_pm = 0
    var fmt_time_12hour_minute = 0
    var fmt_time_24hour_minute_second = 0
    var fmt_time_24hour_minute = 0
    var status_bar_clock_date_time_format = 0
    var status_bar_clock_date_time_format_12 = 0
    // Media control panel
    var notification_element_blend_shade_colors = 0
    var notification_element_blend_colors = 0
    var notification_item_bg_radius = 0
    // Lockscreen
    var pad_clock_xml = 0
    var keyguard_carrier_text = 0
    var TextAppearance_StatusBar_Clock = 0
    var status_bar_padding_extra_start = 0
    var status_bar_clock_margin_end = 0

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    clock = this.getResID("clock", "id", PKG_NAME)
                    pad_clock = this.getResID("pad_clock", "id", PKG_NAME)
                    big_time = this.getResID("big_time", "id", PKG_NAME)
                    horizontal_time = this.getResID("horizontal_time", "id", PKG_NAME)
                    date_time = this.getResID("date_time", "id", PKG_NAME)
                    miui_notification_menu_more_setting = this.getResID("miui_notification_menu_more_setting", "string", PKG_NAME)
                    fmt_time_12hour_minute_second_pm = this.getResID("fmt_time_12hour_minute_second_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute_second = this.getResID("fmt_time_12hour_minute_second", "string", PKG_NAME)
                    fmt_time_12hour_minute_pm = this.getResID("fmt_time_12hour_minute_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute = this.getResID("fmt_time_12hour_minute", "string", PKG_NAME)
                    fmt_time_24hour_minute_second = this.getResID("fmt_time_24hour_minute_second", "string", PKG_NAME)
                    fmt_time_24hour_minute = this.getResID("fmt_time_24hour_minute", "string", PKG_NAME)
                    status_bar_clock_date_time_format = this.getResID("status_bar_clock_date_time_format", "string", PKG_NAME)
                    status_bar_clock_date_time_format_12 = this.getResID("status_bar_clock_date_time_format_12", "string", PKG_NAME)
                    notification_element_blend_shade_colors = this.getResID("notification_element_blend_shade_colors", "array", PKG_NAME)
                    notification_element_blend_colors = this.getResID("notification_element_blend_colors", "array", PKG_NAME)
                    notification_item_bg_radius = this.getResID("notification_item_bg_radius", "dimen", PKG_NAME)
                    pad_clock_xml = this.getResID("pad_clock", "layout", PKG_NAME)
                    keyguard_carrier_text = this.getResID("keyguard_carrier_text", "id", PKG_NAME)
                    TextAppearance_StatusBar_Clock = this.getResID("TextAppearance.StatusBar.Clock", "style", PKG_NAME)
                    status_bar_padding_extra_start = this.getResID("status_bar_padding_extra_start", "dimen", PKG_NAME)
                    status_bar_clock_margin_end = this.getResID("status_bar_clock_margin_end", "dimen", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}