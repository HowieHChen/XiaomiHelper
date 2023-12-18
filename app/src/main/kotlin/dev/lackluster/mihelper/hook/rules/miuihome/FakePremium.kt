package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object FakePremium :YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_FAKE_PREMIUM) {
            val deviceLevelUtilsClass = "com.miui.home.launcher.common.DeviceLevelUtils".toClassOrNull()
            val deviceConfigClass = "com.miui.home.launcher.DeviceConfig".toClassOrNull()
            "com.miui.home.launcher.common.CpuLevelUtils".toClassOrNull()
                ?.method {
                    name = "getQualcommCpuLevel"
                    paramCount = 1
                }?.ignored()
                ?.hook {
                    replaceTo(2)
                }
            "miuix.animation.utils.DeviceUtils".toClassOrNull()
                ?.method {
                    name = "getQualcommCpuLevel"
                    paramCount = 1
                }?.ignored()
                ?.hook {
                    replaceTo(2)
                }
            deviceLevelUtilsClass?.apply {
                method {
                    name = "isUseSimpleAnim"
                }.ignored().hook {
                    replaceToFalse()
                }
                method {
                    name = "getDeviceLevel"
                }.ignored().hook {
                    replaceTo(2)
                }
                method {
                    name = "getDeviceLevelOfCpuAndGpu"
                }.ignored().hook {
                    replaceTo(2)
                }
                method {
                    name = "isLowLevelOrLiteDevice"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            deviceConfigClass?.apply {
                method {
                    name = "isSupportCompleteAnimation"
                }.ignored().hook {
                    replaceToTrue()
                }
                method {
                    name = "isMiuiLiteVersion"
                }.ignored().hook {
                    replaceToFalse()
                }

            }
            "com.miui.home.launcher.util.noword.NoWordSettingHelperKt".toClassOrNull()
                ?.method {
                    name = "isNoWordAvailable"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "android.os.SystemProperties".toClassOrNull()
                ?.method {
                    name = "getBoolean"
                    param(StringClass, BooleanType)
                }?.ignored()
                ?.hookAll {
                    before {
                        if (this.args(0).string() == "ro.config.low_ram.threshold_gb") {
                            this.result = false
                        }
                        if (this.args(0).string() == "ro.miui.backdrop_sampling_enabled") {
                            this.result = true
                        }
                    }
                }
            "com.miui.home.launcher.common.Utilities".toClassOrNull()
                ?.method {
                    name = "canLockTaskView"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "com.miui.home.launcher.MIUIWidgetUtil".toClassOrNull()
                ?.method {
                    name = "isMIUIWidgetSupport"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
            "com.miui.home.launcher.MiuiHomeLog".toClassOrNull()
                ?.method {
                    name = "log"
                    param(StringClass, StringClass)
                }?.ignored()
                ?.hook {
                    intercept()
                }
            "com.xiaomi.onetrack.OneTrack".toClassOrNull()
                ?.method {
                    name = "isDisable"
                }?.ignored()
                ?.hook {
                    replaceToTrue()
                }
        }
    }
}