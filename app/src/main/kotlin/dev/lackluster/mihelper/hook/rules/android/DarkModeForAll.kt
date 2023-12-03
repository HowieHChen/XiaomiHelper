package dev.lackluster.mihelper.hook.rules.android

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DarkModeForAll : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.ANDROID_DARK_MODE_FOR_ALL, extraCondition = { !Device.isInternationalBuild }) {
            "com.android.server.ForceDarkAppListManager".toClass().apply {
                method {
                    name = "getDarkModeAppList"
                }.hookAll {
                    before {
                        "miui.os.Build".toClass().field {
                            name = "IS_INTERNATIONAL_BUILD"
                            modifiers { isStatic }
                        }.get().setTrue()
                    }
                    after {
                        "miui.os.Build".toClass().field {
                            name = "IS_INTERNATIONAL_BUILD"
                            modifiers { isStatic }
                        }.get().set(Device.isInternationalBuild)
                    }
                }
                method {
                    name = "shouldShowInSettings"
                }.hookAll {
                    before {
                        val info = this.args(0).any() as ApplicationInfo?
                        val isSystemApp = info?.current()?.method {
                            name = "isSystemApp"
                            returnType = BooleanType
                        }?.boolean() ?: false
                        this.result = !(info == null || isSystemApp || info.uid < 10000)
                    }
                }
            }
        }
    }
}