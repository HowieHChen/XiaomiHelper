/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */


package dev.lackluster.mihelper.hook.rules.personalassist

import android.content.res.Configuration
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import kotlin.math.max

object BackgroundBlur : YukiBaseHooker() {
    private val REFACTOR = Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
    private val BLUR_TYPE = Prefs.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, 0)
    private var newVersion: Boolean = false

    override fun onHook() {
        if (!REFACTOR && BLUR_TYPE == 1) {
            val clzDeviceBlurBlendAdapter = "com.miui.personalassistant.device.DeviceBlurBlendAdapter".toClassOrNull()
            newVersion = clzDeviceBlurBlendAdapter != null
            if (clzDeviceBlurBlendAdapter != null) {
                clzDeviceBlurBlendAdapter.apply {
                    method {
                        name = "onScrollProgressChanged"
                        superClass()
                    }.hook {
                        replaceUnit {
                            val f = this.args(0).float()
                            val windowScrollProgressOffset = this.instance.current().method {
                                name = "getWindowScrollProgressOffset"
                                superClass()
                            }.float()
                            val ratio = max(0.0f, f - windowScrollProgressOffset) / (1.0f - windowScrollProgressOffset)
                            val maxBlurRadius = this.instance.current().field {
                                name = "maxBlurRadius"
                                superClass()
                            }.int()
                            this.instance.current().method {
                                name = "onBlurRadiusChanged"
                                superClass()
                            }.call((maxBlurRadius * ratio).toInt())
                            this.instance.current().method {
                                name = "onBlendColorChanged"
                                superClass()
                            }.call(ratio)
                        }
                    }
                }
            } else {
                val foldableDeviceAdapterClz = "com.miui.personalassistant.device.FoldableDeviceAdapter".toClass()
                "com.miui.personalassistant.device.DeviceAdapter".toClass().method {
                    name = "create"
                }.giveAll().hookAll {
                    before {
                        this.result = foldableDeviceAdapterClz.constructor().get().newInstance(this.args(0).any())
                    }
                }
                foldableDeviceAdapterClz.apply {
                    runCatching {
                        method {
                            name = "onEnter"
                            param(BooleanType)
                        }.hook {
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
                        superClass()
                    }.hook {
                        replaceUnit {
                            val f = this.args(0).float()
                            val i = (f * 100.0f).toInt()
                            val mCurrentBlurRadius: Int = this.instance.current().field {
                                name = "mCurrentBlurRadius"
                                superClass()
                            }.int()
                            if (mCurrentBlurRadius != i) {
                                if (mCurrentBlurRadius <= 0 || i >= 0) {
                                    this.instance.current().field {
                                        name = "mCurrentBlurRadius"
                                        superClass()
                                    }.set(i)
                                } else {
                                    this.instance.current().field {
                                        name = "mCurrentBlurRadius"
                                        superClass()
                                    }.set(0)
                                }
                                this.instance.current().method {
                                    name = "blurOverlayWindow"
                                    superClass()
                                }.call(mCurrentBlurRadius)
                            }
                        }
                    }
                }
            }
        }
    }
}