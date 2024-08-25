package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.res.Configuration
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object PadClockAnim : YukiBaseHooker() {
    private val setPolicyVisibilityMethod by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClass().method {
            name = "setPolicyVisibility"
            paramCount = 1
            param(IntType)
        }.give()
    }
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.NotifCenter.CLOCK_PAD_ANIM, extraCondition = { Device.isPad }) {
            "com.android.systemui.statusbar.policy.FakeStatusBarClockController".toClass().apply {
                method {
                    name = "needFakeClock"
                }.hook {
                    before {
                        val oldConfig = this.instance.current().field { name = "oldConfig" }.cast<Configuration>() ?: return@before
                        oldConfig.orientation = Configuration.ORIENTATION_PORTRAIT
                    }
                }
            }
            "com.android.systemui.controlcenter.phone.widget.NotificationShadeFakeStatusBarClock".toClass().apply {
                method {
                    name = "onConfigChanged"
                }.hook {
                    before {
                        val oldConfig = this.instance.current().field { name = "oldConfig" }.cast<Configuration>() ?: return@before
                        val newConfig = this.args(0).cast<Configuration>() ?: return@before
                        oldConfig.orientation = Configuration.ORIENTATION_PORTRAIT
                        newConfig.orientation = Configuration.ORIENTATION_PORTRAIT
                    }
                }
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        val horizontalTime = this.instance.current().field { name = "horizontalTime" }.any() ?: return@after
                        val bigTime = this.instance.current().field { name = "bigTime" }.any() ?: return@after
                        setPolicyVisibilityMethod?.invoke(horizontalTime, View.GONE)
                        setPolicyVisibilityMethod?.invoke(bigTime, View.VISIBLE)
                    }
                }
            }
        }
    }
}