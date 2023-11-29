package dev.lackluster.mihelper.hook.rules.personalassist

import android.content.res.Configuration
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object BackgroundBlur : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.PERSON_ASSIST_BLUR) {
            "com.miui.personalassistant.device.DeviceAdapter".toClass()
                .method {
                    name = "create"
                }
                .hookAll {
                    before {
                        this.result = "com.miui.personalassistant.device.FoldableDeviceAdapter".toClass()
                            .constructor().get().newInstance(this.args(0).any())
                    }
                }
            "com.miui.personalassistant.device.FoldableDeviceAdapter".toClass().apply {
                runCatching {
                    method {
                        name = "onEnter"
                        param(BooleanType)
                    }.ignored().hook {
                        before {
                            this.instance.current().field {
                                name = "mScreenSize"
                            }.set(3)
                        }
                    }
                }.onFailure {
                    method {
                        name = "onOpened"
                    }.ignored().hook {
                        before {
                            this.instance.current().field {
                                name = "mScreenSize"
                            }.set(3)
                        }
                    }
                }

                method {
                    name = "onConfigurationChanged"
                    param(Configuration::class.java)
                }.hook {
                    before {
                        this.instance.current().field {
                            name = "mScreenSize"
                        }.set(3)
                    }
                }

                method {
                    name = "onScroll"
                    param(FloatType)
                }.hook {
                    replaceUnit {
                        val f = this.args(0).float()
                        val i = (f * 100.0f).toInt()
                        val mCurrentBlurRadius: Int = this.instance.current().field {
                            name = "mCurrentBlurRadius"
                        }.int()
                        if (mCurrentBlurRadius != i) {
                            if (mCurrentBlurRadius <= 0 || i >= 0) {
                                this.instance.current().field {
                                    name = "mCurrentBlurRadius"
                                }.set(i)
                            } else {
                                this.instance.current().field {
                                    name = "mCurrentBlurRadius"
                                }.set(0)
                            }
                            this.instance.current().method {
                                name = "blurOverlayWindow"
                            }.call(mCurrentBlurRadius)
                        }
                    }
                }
            }

        }
    }
}