package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ControlCenterBattery : YukiBaseHooker() {
    private const val KEY_EXTRA_TRANSLATION_X = "ccFakeIconsExtraX"
    private val batteryPercentage = Prefs.getBoolean(Pref.Key.SystemUI.ControlCenter.BATTERY_PERCENTAGE, false)
    private val batteryPercentageAnim = Prefs.getBoolean(Pref.Key.SystemUI.ControlCenter.BATTERY_PERCENTAGE_ANIM, false)

    override fun onHook() {
        if (batteryPercentage) {
            "com.android.systemui.controlcenter.phone.widget.ControlCenterStatusBarIcon".toClass().apply {
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        val battery = this.instance.current().field {
                            name = "battery"
                        }.cast<View>() ?: return@after
                        battery.tag = true
                    }
                }
            }
            "com.android.systemui.statusbar.views.MiuiBatteryMeterView".toClassOrNull()?.apply {
                method {
                    name = "onBatteryStyleChanged"
                }.hook {
                    before {
                        if (this.instance<View>().tag == true) {
                            this.args(0).set(3)
                        }
                    }
                }
            }
            if (batteryPercentageAnim) {
                "com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController".toClass().apply {
                    method {
                        name = "updateLocation"
                    }.hook {
                        after {
                            val combinedHeaderController =
                                this.instance.current().field {
                                    name = "headerController"
                                }.any()?.current()?.method {
                                    name = "get"
                                }?.call() ?: return@after
                            val realBattery =
                                combinedHeaderController.current().field {
                                    name = "controlCenterStatusIcons"
                                }.any()?.current()?.field {
                                    name = "mBattery"
                                }?.cast<View>() ?: return@after
                            val fakeBattery =
                                combinedHeaderController.current().field {
                                    name = "controlCenterFakeStatusIcons"
                                }.any()?.current()?.field {
                                    name = "battery"
                                }?.cast<View>() ?: return@after
                            XposedHelpers.setAdditionalInstanceField(
                                this.instance,
                                KEY_EXTRA_TRANSLATION_X,
                                realBattery.width - fakeBattery.width
                            )
                        }
                    }
                }
                "com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController\$controlCenterCallback$1".toClassOrNull()?.apply {
                    method {
                        name = "onExpansionChanged"
                    }.hook {
                        after {
                            val expandController = this.instance.current().field {
                                name = "this$0"
                            }.any() ?: return@after
                            val progress = this.args(0).float()
                            val extraX = XposedHelpers.getAdditionalInstanceField(
                                expandController,
                                KEY_EXTRA_TRANSLATION_X
                            ) as? Int ?: 0
                            if (progress in 0.0f..1.0f && extraX > 0) {
                                val combinedHeaderController =
                                    expandController.current().field {
                                        name = "headerController"
                                    }.any()?.current()?.method {
                                        name = "get"
                                    }?.call() ?: return@after
                                val switching =
                                    combinedHeaderController.current().field {
                                        name = "switching"
                                    }.boolean()
                                if (!switching || progress != 1.0f) {
                                    val controlCenterFakeStatusIcons =
                                        combinedHeaderController.current().field {
                                            name = "controlCenterFakeStatusIcons"
                                        }.cast<View>() ?: return@after
                                    controlCenterFakeStatusIcons.apply {
                                        translationX = translationX - extraX * progress
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}