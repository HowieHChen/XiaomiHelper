package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideStatusBarIcon : YukiBaseHooker() {
    override fun onHook() {
        "com.android.systemui.statusbar.phone.StatusBarIconControllerImpl".toClass()
            .method {
                name = "setIconVisibility"
                paramCount = 2
            }
            .hook {
                before {
                    hideIcon(this)
                }
            }
        "com.android.systemui.statusbar.phone.MiuiDripLeftStatusBarIconControllerImpl".toClass()
            .method {
                name = "setIconVisibility"
                paramCount = 2
            }
            .hook {
                before {
                    hideIcon(this)
                    when (this.args(0).string()) {
                        "alarm_clock" -> hasEnable(PrefKey.STATUSBAR_RIGHT_ALARM) {
                            this.args(1).setFalse()
                        }
                        "nfc" -> hasEnable(PrefKey.STATUSBAR_RIGHT_NFC) {
                            this.args(1).setFalse()
                        }
                        "volume" -> hasEnable(PrefKey.STATUSBAR_RIGHT_VOLUME) {
                            this.args(1).setFalse()
                        }
                        "zen" -> hasEnable(PrefKey.STATUSBAR_RIGHT_ZEN) {
                            this.args(1).setFalse()
                        }
                        "headset" -> hasEnable(PrefKey.STATUSBAR_RIGHT_HEADSET) {
                            this.args(1).setFalse()
                        }
                    }
                }
            }
    }

    private fun hideIcon(param: HookParam) {
        when (param.args(0).string()) {
            "bluetooth" -> hasEnable(PrefKey.STATUSBAR_HIDE_BLUETOOTH) {
                param.args(1).setFalse()
            }
            "bluetooth_handsfree_battery" -> hasEnable(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY) {
                param.args(1).setFalse()
            }
            "zen" -> hasEnable(PrefKey.STATUSBAR_HIDE_ZEN) {
                param.args(1).setFalse()
            }
            "volume" -> hasEnable(PrefKey.STATUSBAR_HIDE_VOLUME) {
                param.args(1).setFalse()
            }
            "wifi" -> hasEnable(PrefKey.STATUSBAR_HIDE_WIFI) {
                param.args(1).setFalse()
            }
            "slave_wifi" -> hasEnable(PrefKey.STATUSBAR_HIDE_WIFI_SLAVE) {
                param.args(1).setFalse()
            }
            "airplane" -> hasEnable(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE) {
                param.args(1).setFalse()
            }
            "alarm_clock" -> hasEnable(PrefKey.STATUSBAR_HIDE_ALARM) {
                param.args(1).setFalse()
            }
            "location" -> hasEnable(PrefKey.STATUSBAR_HIDE_GPS) {
                param.args(1).setFalse()
            }
            "hotspot" -> hasEnable(PrefKey.STATUSBAR_HIDE_HOTSPOT) {
                param.args(1).setFalse()
            }
            "headset" -> hasEnable(PrefKey.STATUSBAR_HIDE_HEADSET) {
                param.args(1).setFalse()
            }
            "vpn" -> hasEnable(PrefKey.STATUSBAR_HIDE_VPN) {
                param.args(1).setFalse()
            }
            "no_sim" -> hasEnable(PrefKey.STATUSBAR_HIDE_NO_SIM) {
                param.args(1).setFalse()
            }
            "nfc" -> hasEnable(PrefKey.STATUSBAR_HIDE_NFC) {
                param.args(1).setFalse()
            }
        }
    }
}