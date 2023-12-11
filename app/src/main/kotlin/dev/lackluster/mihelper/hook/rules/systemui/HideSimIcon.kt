package dev.lackluster.mihelper.hook.rules.systemui

import android.telephony.SubscriptionManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object HideSimIcon : YukiBaseHooker(){
    private val hideSimOne by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_ONE, false)
    }
    private val hideSimTwo by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_TWO, false)
    }
    override fun onHook() {
        if (hideSimOne || hideSimTwo) {
            "com.android.systemui.statusbar.StatusBarMobileView".toClass()
                .method {
                    name = "applyMobileState"
                }
                .hook {
                    before {
                        val mobileIconState = this.args(0).any() ?: return@before
                        val subId = mobileIconState.current().field {
                            name = "subId"
                        }.int()
                        val slotId = SubscriptionManager.getSlotIndex(subId)
                        if (hideSimOne && slotId == 0) {
                            mobileIconState.current().field {
                                name = "visible"
                                superClass()
                            }.setFalse()
                        }
                        if (hideSimTwo && slotId == 1) {
                            mobileIconState.current().field {
                                name = "visible"
                                superClass()
                            }.setFalse()
                        }
                    }
                }
        }
    }
}