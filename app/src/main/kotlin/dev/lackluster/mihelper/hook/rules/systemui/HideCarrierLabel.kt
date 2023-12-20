package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object HideCarrierLabel : YukiBaseHooker() {
    private val hideSimOne by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_CARRIER_ONE, false)
    }
    private val hideSimTwo by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_CARRIER_TWO, false)
    }
    private var cardDisableArray = BooleanArray(2)

    override fun onHook() {
        if (hideSimOne || hideSimTwo) {
            "com.android.systemui.statusbar.policy.MiuiCarrierTextControllerImpl".toClass()
                .method {
                    name = "updateCarrierText"
                }
                .hook {
                    before {
                        val mCardDisable = this.instance.current().field { name = "mCardDisable" }.any() as BooleanArray
                        val mPhoneCount = this.instance.current().field { name = "mPhoneCount" }.int()
                        if (hideSimOne && mPhoneCount >= 1) {
                            cardDisableArray[0] = mCardDisable[0]
                            mCardDisable[0] = true
                        }
                        if (hideSimTwo && mPhoneCount >= 2) {
                            cardDisableArray[1] = mCardDisable[1]
                            mCardDisable[1] = true
                        }
                    }
                    after {
                        val mCardDisable = this.instance.current().field { name = "mCardDisable" }.any() as BooleanArray
                        val mPhoneCount = this.instance.current().field { name = "mPhoneCount" }.int()
                        if (hideSimOne && mPhoneCount >= 1) {
                            mCardDisable[0] = cardDisableArray[0]
                        }
                        if (hideSimTwo && mPhoneCount >= 2) {
                            mCardDisable[1] = cardDisableArray[1]
                        }
                    }
                }
        }
    }
}