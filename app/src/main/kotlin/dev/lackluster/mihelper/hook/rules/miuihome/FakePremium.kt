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
            val deviceLevelUtilsClass = "com.miui.home.launcher.common.DeviceLevelUtils".toClass()
            val deviceConfigClass = "com.miui.home.launcher.DeviceConfig".toClass()
            try {
                if ("com.miui.home.launcher.common.CpuLevelUtils".hasClass()) {
                    "com.miui.home.launcher.common.CpuLevelUtils".toClass()
                        .method {
                            name = "getQualcommCpuLevel"
                            paramCount = 1
                        }.ignored().onNoSuchMethod {
                            throw it
                        }
                        .hook {
                            replaceTo(2)
                        }
                }
            } catch (e: Exception) {
                if ("miuix.animation.utils.DeviceUtils".hasClass()) {
                    "miuix.animation.utils.DeviceUtils".toClass()
                        .method {
                            name = "getQualcommCpuLevel"
                            paramCount = 1
                        }
                        .hook {
                            replaceTo(2)
                        }
                }
            }
            runCatching {
                deviceConfigClass.method {
                    name = "isUseSimpleAnim"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            runCatching {
                deviceLevelUtilsClass.method {
                    name = "getDeviceLevel"
                }.ignored().hook {
                    replaceTo(2)
                }
            }
            runCatching {
                deviceLevelUtilsClass.method {
                    name = "getDeviceLevelOfCpuAndGpu"
                }.ignored().hook {
                    replaceTo(2)
                }
            }
            runCatching {
                deviceConfigClass.method {
                    name = "isSupportCompleteAnimation"
                }.ignored().hook {
                    replaceToTrue()
                }
            }

            runCatching {
                deviceLevelUtilsClass.method {
                    name = "isLowLevelOrLiteDevice"
                }.ignored().hook {
                    replaceToFalse()
                }
            }

            runCatching {
                deviceConfigClass.method {
                    name = "isMiuiLiteVersion"
                }.ignored().hook {
                    replaceToFalse()
                }
            }

            runCatching {
                "com.miui.home.launcher.util.noword.NoWordSettingHelperKt".toClass()
                    .method {
                        name = "isNoWordAvailable"
                    }.ignored()
                    .hook {
                        replaceToTrue()
                    }
            }

            runCatching {
                "android.os.SystemProperties".toClass()
                    .method {
                        name = "getBoolean"
                        param(StringClass, BooleanType)
                    }.ignored().hookAll {
                        before {
                            if (this.args(0).string() == "ro.config.low_ram.threshold_gb") {
                                this.result = false
                            }
                            if (this.args(0).string() == "ro.miui.backdrop_sampling_enabled") {
                                this.result = true
                            }
                        }
                    }
            }

            runCatching {
                "com.miui.home.launcher.common.Utilities".toClass()
                    .method {
                        name = "canLockTaskView"
                    }.ignored()
                    .hook {
                        replaceToTrue()
                    }
            }

            runCatching {
                "com.miui.home.launcher.MIUIWidgetUtil".toClass()
                    .method {
                        name = "isMIUIWidgetSupport"
                    }.ignored()
                    .hook {
                        replaceToTrue()
                    }
            }

            runCatching {
                "com.miui.home.launcher.MiuiHomeLog".toClass()
                    .method {
                        name = "log"
                        param(StringClass, StringClass)
                    }.ignored()
                    .hook {
                        before {
                            this.result = null
                        }
                    }
            }

            runCatching {
                "com.xiaomi.onetrack.OneTrack".toClass()
                    .method {
                        name = "isDisable"
                    }.ignored()
                    .hook {
                        replaceToTrue()
                    }
            }
        }
    }
}