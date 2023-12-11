package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_systemui", hideMenu = false)
class SystemUIPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_systemui)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_systemui_status_bar)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.systemui_statusbar_hide_icon,
                onClickListener = { showFragment("hide_icon") })
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.systemui_statusbar_icon_position,
                tipsId = R.string.systemui_statusbar_icon_position_tips,
                onClickListener = { showFragment("icon_position") })
        )
        val clockPaddingBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_CLOCK_CUSTOM, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_clock_custom),
            SwitchV(PrefKey.STATUSBAR_CLOCK_CUSTOM, dataBindingSend = clockPaddingBinding.bindingSend)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.status_bar_clock_padding_left,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.status_bar_clock_padding_left)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 0"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_LEFT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.STATUSBAR_CLOCK_PADDING_LEFT,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.status_bar_clock_padding_right,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.status_bar_clock_padding_right)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 0"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.STATUSBAR_CLOCK_PADDING_RIGHT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.STATUSBAR_CLOCK_PADDING_RIGHT,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = clockPaddingBinding.binding.getRecv(1)
        )
        val batteryPaddingBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_BATTERY_CUSTOM, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_battery_custom),
            SwitchV(PrefKey.STATUSBAR_BATTERY_CUSTOM, dataBindingSend = batteryPaddingBinding.bindingSend)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.status_bar_battery_padding_left,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.status_bar_battery_padding_left)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 0"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.STATUSBAR_BATTERY_PADDING_LEFT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.STATUSBAR_BATTERY_PADDING_LEFT,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.status_bar_battery_padding_right,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.status_bar_battery_padding_right)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 0"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.STATUSBAR_BATTERY_PADDING_RIGHT, 0)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.STATUSBAR_BATTERY_PADDING_RIGHT,
                                        getEditText().toInt()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPaddingBinding.binding.getRecv(1)
        )
        val batteryPercentMarkBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY_PERCENT, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_uni_battery_mark),
            SwitchV(PrefKey.STATUSBAR_CHANGE_BATTERY_PERCENT_MARK),
            dataBindingRecv = batteryPercentMarkBinding.binding.getRecv(0)
        )
        val batteryPercentSizeBinding = GetDataBinding({
                MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_CHANGE_BATTERY_PERCENT_SIZE, false)
            }) { view, flags, data ->
                when (flags) {
                    1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                }
            }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_change_battery_percent),
            SwitchV(PrefKey.STATUSBAR_CHANGE_BATTERY_PERCENT_SIZE, dataBindingSend = batteryPercentSizeBinding.bindingSend)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.status_bar_battery_percent_size,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.status_bar_battery_percent_size)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 0\n${activity.getString(R.string.status_bar_battery_percent_size_default)}"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.STATUSBAR_BATTERY_PERCENT_SIZE, 0f)
                        }")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.STATUSBAR_BATTERY_PERCENT_SIZE,
                                        getEditText().toFloat()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = batteryPercentSizeBinding.binding.getRecv(1)
        )
        val notificationMaxBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_NOTIF_MAX, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_notification_max),
            SwitchV(PrefKey.STATUSBAR_NOTIF_MAX, dataBindingSend = notificationMaxBinding.bindingSend)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_icon),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_ICON_MAX, 0, 15, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_dot),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_DOT_MAX, 0, 5, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_lockscreen),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_LOCKSCREEN_MAX, 0, 15, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_notification_center)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_notif_disable_whitelist),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_NO_WHITELIST)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_freeform,
                tipsId = R.string.systemui_notif_freeform_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_FREEFORM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_settings,
                tipsId = R.string.systemui_notif_redirect_settings_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_optimize,
                tipsId = R.string.systemui_botif_media_control_optimize_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_OPTIMIZE)
        )
        val monetBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(
                PrefKey.SYSTEMUI_NOTIF_MC_MONET, false
            )
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_monet,
                tipsId = R.string.systemui_botif_media_control_monet_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_MONET, dataBindingSend = monetBinding.bindingSend)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_botif_media_control_monet_reverse,
                tipsId = R.string.systemui_botif_media_control_monet_reverse_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_MC_MONET_REVERSE),
            dataBindingRecv = monetBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_control_center)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_control_bluetooth_restrict,
                tipsId = R.string.systemui_control_bluetooth_restrict_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_CONTROL_BLUETOOTH)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_lock_screen)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_lock_screen_time_font,
                tipsId = R.string.systemui_lock_screen_time_font_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_TIME_FONT)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_lock_screen_date_font,
                tipsId = R.string.systemui_lock_screen_date_font_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_DATE_FONT)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_unlock_tip),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_UNLOCK_TIP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_disturb),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_DISTURB)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_double_tap),
            SwitchV(PrefKey.SYSTEMUI_DOUBLE_TAP_TO_SLEEP)
        )
    }
}