package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("icon_tuner")
class IconTunerPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_status_bar_icon_tuner)
    }
    override fun onCreate() {
        val hideIconMode: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.status_bar_hide_selection_default)
            it[1] = getString(R.string.status_bar_hide_selection_show_all)
            it[2] = getString(R.string.status_bar_hide_selection_show_statusbar)
            it[3] = getString(R.string.status_bar_hide_selection_show_qs)
            it[4] = getString(R.string.status_bar_hide_selection_hidden)
        }
        TitleText(textId = R.string.ui_title_icon_tuner_carrier)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_carrier_one),
            SwitchV(PrefKey.STATUSBAR_HIDE_CARRIER_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_carrier_two),
            SwitchV(PrefKey.STATUSBAR_HIDE_CARRIER_TWO)
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_mobile)
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_sim_one),
            SwitchV(PrefKey.STATUSBAR_HIDE_SIM_ONE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_sim_two),
            SwitchV(PrefKey.STATUSBAR_HIDE_SIM_TWO)
        )
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_no_sim),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_NO_SIM,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NO_SIM, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NO_SIM, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NO_SIM, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NO_SIM, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NO_SIM, 4)
                }
            })
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_mobile_activity),
            SwitchV(PrefKey.STATUSBAR_HIDE_MOBILE_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_mobile_type),
            SwitchV(PrefKey.STATUSBAR_HIDE_MOBILE_TYPE)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_small),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_SMALL)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_large),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_LARGE)
        )
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_hd_new),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_HD_NEW,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HD_NEW, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HD_NEW, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HD_NEW, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HD_NEW, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HD_NEW, 4)
                }
            })
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_hd_no_service),
            SwitchV(PrefKey.STATUSBAR_HIDE_HD_NO_SERVICE)
        )
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_wifi)
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_wifi),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_WIFI,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIFI, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIFI, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIFI, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIFI, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIFI, 4)
                }
            })
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi_activity),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI_ACTIVITY)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_wifi_type),
            SwitchV(PrefKey.STATUSBAR_HIDE_WIFI_TYPE)
        )
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_hotspot),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_HOTSPOT,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HOTSPOT, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HOTSPOT, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HOTSPOT, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HOTSPOT, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HOTSPOT, 4)
                }
            })
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_battery)
        val swapBatteryBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val batteryPercentMarkBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_HIDE_BATTERY_PERCENT, false)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_battery),
            SwitchV(PrefKey.STATUSBAR_HIDE_BATTERY, dataBindingSend = swapBatteryBinding.bindingSend)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_battery_mark),
            SwitchV(PrefKey.STATUSBAR_HIDE_BATTERY_PERCENT, dataBindingSend = batteryPercentMarkBinding.bindingSend)
        )
        TextWithSwitch(
            TextV(textId = R.string.status_bar_hide_charge),
            SwitchV(PrefKey.STATUSBAR_HIDE_CHARGE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_swap_battery_percent),
            SwitchV(PrefKey.STATUSBAR_SWAP_BATTERY_PERCENT),
            dataBindingRecv = swapBatteryBinding.getRecv(1)
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
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_connectivity)
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_flight_mode),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_FLIGHT_MODE,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_gps),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_GPS,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_GPS, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_GPS, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_GPS, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_GPS, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_GPS, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_bluetooth),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_BLUETOOTH,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_bluetooth_battery),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_nfc),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_NFC,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NFC, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NFC, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NFC, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NFC, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NFC, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_vpn),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_VPN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VPN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VPN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VPN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VPN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VPN, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_net_speed),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_NET_SPEED,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NET_SPEED, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NET_SPEED, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NET_SPEED, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NET_SPEED, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_NET_SPEED, 4)
                }
            })
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_device)
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_car),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_CAR,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_CAR, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_CAR, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_CAR, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_CAR, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_CAR, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_pad),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_PAD,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PAD, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PAD, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PAD, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PAD, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PAD, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_pc),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_PC,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PC, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PC, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PC, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PC, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PC, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_phone),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_PHONE,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PHONE, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PHONE, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PHONE, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PHONE, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_PHONE, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_sound_box),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_SOUND_BOX,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_sound_box_group),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_sound_box_screen),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_stereo),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_STEREO,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_STEREO, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_STEREO, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_STEREO, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_STEREO, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_STEREO, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_tv),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_TV,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_TV, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_TV, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_TV, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_TV, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_TV, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_wireless_headset),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 4)
                }
            })
        Line()
        TitleText(textId = R.string.ui_title_icon_tuner_other)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_swap_mobile_wifi),
            SwitchV(PrefKey.STATUSBAR_SWAP_MOBILE_WIFI)
        )
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_alarm),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_ALARM,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ALARM, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ALARM, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ALARM, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ALARM, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ALARM, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_headset),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_HEADSET,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HEADSET, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HEADSET, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HEADSET, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HEADSET, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_HEADSET, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_volume),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_VOLUME,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VOLUME, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VOLUME, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VOLUME, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VOLUME, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_VOLUME, 4)
                }
            })
        TextWithSpinner(
            TextV(textId = R.string.status_bar_hide_zen),
            SpinnerV(
                hideIconMode[MIUIActivity.safeSP.getInt(
                    PrefKey.STATUSBAR_HIDE_ZEN,
                    0
                )].toString()
            ) {
                add(hideIconMode[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ZEN, 0)
                }
                add(hideIconMode[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ZEN, 1)
                }
                add(hideIconMode[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ZEN, 2)
                }
                add(hideIconMode[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ZEN, 3)
                }
                add(hideIconMode[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.STATUSBAR_HIDE_ZEN, 4)
                }
            })
    }
}