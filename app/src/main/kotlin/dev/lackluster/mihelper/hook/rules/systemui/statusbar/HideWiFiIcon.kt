package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.KotlinFlowHelper.ReadonlyStateFlow
import dev.lackluster.mihelper.utils.Prefs

object HideWiFiIcon : YukiBaseHooker() {
    private val hideWifiActivity = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_WIFI_ACTIVITY, false)
    private val hideWifiStandard = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_WIFI_STANDARD, false)
    private val connectivityConstantsClass by lazy {
        "com.android.systemui.statusbar.pipeline.shared.ConnectivityConstants".toClassOrNull()
    }

    override fun onHook() {
        if (hideWifiActivity || hideWifiStandard) {
            "com.android.systemui.statusbar.pipeline.wifi.ui.viewmodel.WifiViewModel".toClassOrNull()?.apply {
                constructor().hookAll {
                    if (hideWifiActivity ) {
                        before {
                            this.args.firstOrNull {
                                connectivityConstantsClass?.isInstance(it) == true
                            }?.current()?.field {
                                name = "shouldShowActivityConfig"
                            }?.setFalse()
                        }
                    }
                    if (hideWifiStandard) {
                        after {
                            this.instance.current().field {
                                name = "wifiStandard"
                            }.set(
                                ReadonlyStateFlow(0 as Int?)
                            )
                        }
                    }
                }
            } ?: loadHooker(HideWifiActivityAndType)
        }
    }
}