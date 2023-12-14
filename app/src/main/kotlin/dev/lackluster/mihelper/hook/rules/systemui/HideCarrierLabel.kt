package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object HideCarrierLabel : YukiBaseHooker() {
    private val hideSimOne by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_CARRIER_ONE, false)
    }
    private val hideSimTwo by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_CARRIER_TWO, false)
    }
    override fun onHook() {
        if (hideSimOne || hideSimTwo) {
            "com.android.systemui.statusbar.policy.MiuiCarrierTextControllerImpl".toClass()
                .method {
                    name = "setSubs"
                    param(ListClass)
                }
                .hook {
                    after {
                        val cardDisableList = this.instance.current().field {
                            name = "mCardDisable"
                        }.any() as BooleanArray
                        val size = cardDisableList.size
                        if (hideSimOne && size >= 1) {
                            cardDisableList[0] = true
                        }
                        if (hideSimTwo && size >= 2) {
                            cardDisableList[1] = true
                        }
                    }
                }
        }
    }
}