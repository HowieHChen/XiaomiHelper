package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.ListClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object IconPosition : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.STATUSBAR_SWAP_MOBILE_WIFI) {
            val signalIcons: Array<String> = arrayOf(
                "hotspot",
                "slave_wifi",
                "wifi",
                "demo_wifi",
                "no_sim",
                "mobile",
                "demo_mobile",
                "airplane"
            )
            val signalRelatedIcons = ArrayList(listOf<String>(*signalIcons))
            "com.android.systemui.statusbar.phone.StatusBarIconList".toClass()
                .constructor {
                    paramCount = 1
                }
                .hook {
                    before {
                        val isRightController = "StatusBarIconControllerImpl" == this.instance.javaClass.simpleName
                        if (isRightController) {
                            val allStatusIcons = ArrayList<String>(mutableListOf<String>(*this.args(0).array<String>()))
                            var startIndex = allStatusIcons.indexOf("no_sim")
                            val endIndex = allStatusIcons.indexOf("demo_wifi") + 1
                            val removedIcons = allStatusIcons.subList(startIndex, endIndex)
                            removedIcons.clear()
                            startIndex = allStatusIcons.indexOf("ethernet") + 1
                            allStatusIcons.addAll(startIndex, signalRelatedIcons)
                            this.args(0).set(allStatusIcons.toTypedArray<String>())
                        }
                    }
                }
        }
        hasEnable(PrefKey.STATUSBAR_RIGHT_NETSPEED) {
            "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView".toClass()
                .method {
                    name = "updateCutoutLocation"
                }
                .hook {
                    after {
                        (this.instance.current().field {
                            name = "mDripNetworkSpeedView"
                        }.any())?.current()?.method {
                            name = "setBlocked"
                        }?.call(true)
                    }
                }
            "com.android.systemui.statusbar.policy.NetworkSpeedController".toClass()
                .method {
                    name = "setDripNetworkSpeedView"
                }
                .hook {
                    before {
                        this.args(0).setNull()
                    }
                }
        }
        "com.android.systemui.statusbar.phone.StatusBarIconController\$IconManager".toClass()
            .method {
                name = "setBlockList"
                param(ListClass)
            }
            .hook {
                before {
                    val rightBlockList = this.args(0).list<String>().toMutableList()
                    hasEnable(PrefKey.STATUSBAR_RIGHT_ALARM) {
                        rightBlockList.remove("alarm_clock")
                    }
                    // 显示右侧NFC
                    hasEnable(PrefKey.STATUSBAR_RIGHT_NFC) {
                        rightBlockList.remove("nfc")
                    }
                    // 显示右侧声音
                    hasEnable(PrefKey.STATUSBAR_RIGHT_VOLUME) {
                        rightBlockList.remove("volume")
                    }
                    // 显示右侧勿扰
                    hasEnable(PrefKey.STATUSBAR_RIGHT_ZEN) {
                        rightBlockList.remove("zen")
                    }
                    // 显示右侧耳机
                    hasEnable(PrefKey.STATUSBAR_RIGHT_HEADSET) {
                        rightBlockList.remove("headset")
                    }
                    // 显示右侧网速
                    hasEnable(PrefKey.STATUSBAR_RIGHT_NETSPEED) {
                        rightBlockList.remove("network_speed")
                    }
                    this.args(0).set(rightBlockList.toList())
                }
            }
    }
}