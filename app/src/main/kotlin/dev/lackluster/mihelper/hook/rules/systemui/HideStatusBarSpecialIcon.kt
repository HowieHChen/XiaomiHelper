package dev.lackluster.mihelper.hook.rules.systemui

import android.telephony.SubscriptionManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideStatusBarSpecialIcon : YukiBaseHooker() {
    private val isBigMobileType by lazy {
        false
    }
    private val hideSimOne by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_ONE, false)
    }
    private val hideSimTwo by lazy {
        Prefs.getBoolean(PrefKey.STATUSBAR_HIDE_SIM_TWO, false)
    }
    override fun onHook() {
        "com.android.systemui.statusbar.StatusBarMobileView".toClass().apply {
            method {
                name = "applyMobileState"
                paramCount = 1
            }.hook {
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
                after {
                    hideHD(this)
                    hasEnable(PrefKey.STATUSBAR_HIDE_MOBILE_ACTIVITY) {
                        hideMobileActivity(this)
                    }
                    hasEnable(PrefKey.STATUSBAR_HIDE_MOBILE_TYPE) {
                        hideMobileType(this)
                    }
                }
            }
            method {
                name = "updateState"
                paramCount = 1
            }.hook {
                after {
                    hideHD(this)
                    hasEnable(PrefKey.STATUSBAR_HIDE_MOBILE_ACTIVITY) {
                        hideMobileActivity(this)
                    }
                    hasEnable(PrefKey.STATUSBAR_HIDE_MOBILE_TYPE) {
                        hideMobileType(this)
                    }
                }
            }
        }
//        hasEnable(PrefKey.STATUSBAR_HIDE_HD_NEW) {
//            "com.android.systemui.statusbar.policy.HDController".toClassOrNull()
//                ?.method {
//                    name = "update"
//                }?.ignored()
//                ?.hook {
//                    before {
//                        this.result = null
//                    }
//                }
//        }
    }

    private fun hideHD(param: HookParam) {
        hasEnable(PrefKey.STATUSBAR_HIDE_HD_LARGE) {
            (param.instance.current().field {
                name = "mVolte"
            }.any() as? ImageView)?.visibility = View.GONE
        }
        hasEnable(PrefKey.STATUSBAR_HIDE_HD_SMALL) {
            (param.instance.current().field {
                name = "mSmallHd"
            }.any() as? ImageView)?.visibility = View.GONE
        }
        hasEnable(PrefKey.STATUSBAR_HIDE_HD_NO_SERVICE) {
            (param.instance.current().field {
                name = "mVolteNoService"
            }.any() as? ImageView)?.visibility = View.GONE
        }
    }

    private fun hideMobileActivity(param: HookParam) {
        (param.instance.current().field {
            name = "mLeftInOut"
        }.any() as? ImageView)?.visibility = View.GONE
        (param.instance.current().field {
            name = "mRightInOut"
        }.any() as? ImageView)?.visibility = View.GONE
    }

    private fun hideMobileType(param: HookParam) {
        if (isBigMobileType) {
            (param.instance.current().field {
                name = "mMobileType"
            }.any() as? ImageView)?.visibility = View.GONE
            (param.instance.current().field {
                name = "mMobileTypeImage"
            }.any() as? ImageView)?.visibility = View.GONE
            (param.instance.current().field {
                name = "mMobileTypeSingle"
            }.any() as? TextView)?.visibility = View.GONE
        } else {
            (param.instance.current().field {
                name = "mMobileType"
            }.any() as? ImageView)?.visibility = View.INVISIBLE
            (param.instance.current().field {
                name = "mMobileTypeImage"
            }.any() as? ImageView)?.visibility = View.INVISIBLE
            (param.instance.current().field {
                name = "mMobileTypeSingle"
            }.any() as? TextView)?.visibility = View.INVISIBLE
        }
    }
}