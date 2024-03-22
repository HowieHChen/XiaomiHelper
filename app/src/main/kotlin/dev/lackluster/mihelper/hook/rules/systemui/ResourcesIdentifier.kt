package dev.lackluster.mihelper.hook.rules.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Scope

object ResourcesIdentifier : YukiBaseHooker() {
    private const val PKG_NAME = Scope.SYSTEM_UI
    private var isInitialized = false
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
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    clock = this.resources.getIdentifier("clock", "id", PKG_NAME)
                    pad_clock = this.resources.getIdentifier("pad_clock", "id", PKG_NAME)
                    big_time = this.resources.getIdentifier("big_time", "id", PKG_NAME)
                    horizontal_time = this.resources.getIdentifier("horizontal_time", "id", PKG_NAME)
                    date_time = this.resources.getIdentifier("date_time", "id", PKG_NAME)
                    miui_notification_menu_more_setting = this.resources.getIdentifier("miui_notification_menu_more_setting", "string", PKG_NAME)
                    fmt_time_12hour_minute_second_pm = this.resources.getIdentifier("fmt_time_12hour_minute_second_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute_second = this.resources.getIdentifier("fmt_time_12hour_minute_second", "string", PKG_NAME)
                    fmt_time_12hour_minute_pm = this.resources.getIdentifier("fmt_time_12hour_minute_pm", "string", PKG_NAME)
                    fmt_time_12hour_minute = this.resources.getIdentifier("fmt_time_12hour_minute", "string", PKG_NAME)
                    fmt_time_24hour_minute_second = this.resources.getIdentifier("fmt_time_24hour_minute_second", "string", PKG_NAME)
                    fmt_time_24hour_minute = this.resources.getIdentifier("fmt_time_24hour_minute", "string", PKG_NAME)
                    status_bar_clock_date_time_format = this.resources.getIdentifier("status_bar_clock_date_time_format", "string", PKG_NAME)
                    status_bar_clock_date_time_format_12 = this.resources.getIdentifier("status_bar_clock_date_time_format_12", "string", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}