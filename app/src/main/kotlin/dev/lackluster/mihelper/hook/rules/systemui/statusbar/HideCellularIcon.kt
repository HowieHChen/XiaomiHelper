package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.telephony.SubscriptionManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.KotlinFlowHelper.READONLY_STATE_FLOW
import dev.lackluster.mihelper.utils.KotlinFlowHelper.ReadonlyStateFlow
import dev.lackluster.mihelper.utils.Prefs

object HideCellularIcon : YukiBaseHooker() {
    private val hideSimOne = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_SIM_ONE, false)
    private val hideSimTwo = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_SIM_TWO, false)
    private val hideMobileActivity = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_MOBILE_ACTIVITY, false)
    private val hideMobileType = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_MOBILE_TYPE, false)
    private val hideHDSmall = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_HD_SMALL, false)
    private val hideRoam = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_ROAM, false)
    private val hideRoamSmall = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_ROAM_SMALL, false)
    private val hideVoLte = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_VOLTE, false)
    private val hideVoWifi = Prefs.getBoolean(Pref.Key.SystemUI.IconTurner.HIDE_VOWIFI, false)
    private val miuiMobileIconInteractorImplClass by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MiuiMobileIconInteractorImpl".toClassOrNull()
    }
    private val readonlyStateFlowClass by lazy {
        READONLY_STATE_FLOW.toClassOrNull()
    }

    override fun onHook() {
        if (
            hideSimOne || hideSimTwo ||
            hideMobileActivity || hideMobileType || hideRoam || hideRoamSmall ||
            hideHDSmall || hideVoLte || hideVoWifi
        ) {
            if (hideMobileType) {
                "com.android.systemui.statusbar.pipeline.mobile.ui.binder.MiuiMobileIconBinder".toClassOrNull()?.apply {
                    method {
                        name = "updateMobileTypeLayoutParams"
                    }.hook {
                        before {
                            this.args(1).set(null)
                        }
                    }
                }
            }
            "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MiuiCellularIconVM".toClassOrNull()?.apply {
                constructor().hookAll {
                    after {
                        if (hideSimOne || hideSimTwo) {
                            val miuiMobileIconInteractor = this.args.firstOrNull {
                                miuiMobileIconInteractorImplClass?.isInstance(it) == true
                            }
                            miuiMobileIconInteractor?.current()?.field {
                                name = "subId"
                                type = IntType
                            }?.int()?.let {
                                val slotIndex = SubscriptionManager.getSlotIndex(it)
                                if ((slotIndex == 0 && hideSimOne) || (slotIndex == 1 && hideSimTwo)) {
                                    this.instance.current().field {
                                        name = "isVisible"
                                    }.set(
                                        ReadonlyStateFlow(false as Boolean?)
                                    )
                                }
                            }
                        }
                        if (hideMobileActivity) {
                            this.instance.current().field {
                                name = "inOutVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                        if (hideMobileType) {
                            this.instance.current().field {
                                name = "mobileTypeVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                            this.instance.current().field {
                                name = "mobileTypeImageVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                            this.instance.current().field {
                                name = "mobileTypeSingleVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                        if (hideHDSmall) {
                            val smallHd = this.instance.current().field {
                                name = "smallHdVisible"
                            }
                            if (readonlyStateFlowClass?.isInstance(smallHd.any()) == true) {
                                smallHd.set(ReadonlyStateFlow(false as Boolean?))
                            }
                        }
                        if (hideRoam) {
                            this.instance.current().field {
                                name = "mobileRoamVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                        if (hideRoamSmall) {
                            this.instance.current().field {
                                name = "smallRoamVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                        if (hideVoLte) {
                            val volteCN = this.instance.current().field {
                                name = "volteVisibleCn"
                            }
                            if (readonlyStateFlowClass?.isInstance(volteCN) == true) {
                                volteCN.set(ReadonlyStateFlow(false as Boolean?))
                            }
                            this.instance.current().field {
                                name = "volteVisibleGlobal"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                        if (hideVoWifi) {
                            this.instance.current().field {
                                name = "vowifiVisible"
                            }.set(
                                ReadonlyStateFlow(false as Boolean?)
                            )
                        }
                    }
                }
            } ?: loadHooker(HideStatusBarSpecialIcon)
        }
    }
}