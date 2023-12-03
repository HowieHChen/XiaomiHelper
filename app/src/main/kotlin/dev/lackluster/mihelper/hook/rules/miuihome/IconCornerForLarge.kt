package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object IconCornerForLarge : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_ICON_CORNER4LARGE, extraCondition = { !Device.isPad }) {
            "com.miui.home.launcher.bigicon.BigIconUtil".toClass()
                .method {
                    name = "getCroppedFromCorner"
                    paramCount = 4
                }
                .hook {
                    before {
                        this.args(0).set(2)
                        this.args(1).set(2)
                    }
                }
            "com.miui.home.launcher.maml.MaMlHostView".toClass()
                .method {
                    name = "getCornerRadius"
                }
                .hook {
                    before {
                        this.result = this.instance.current().field {
                            name = "mEnforcedCornerRadius"
                            superClass()
                        }.float()
                    }
                }
            setOf(
                "com.miui.home.launcher.maml.MaMlHostView",
                "com.miui.home.launcher.LauncherAppWidgetHostView"
            ).forEach {
                it.toClass()
                    .method {
                        name = "computeRoundedCornerRadius"
                        paramCount = 1
                    }
                    .hook {
                        before {
                            this.result = this.instance.current().field {
                                name = "mEnforcedCornerRadius"
                                superClass()
                            }.float()
                        }
                    }
            }
        }
    }
}