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
import dev.lackluster.mihelper.utils.factory.hasEnable

object BackgroundBlur : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.MINUS_BLUR, extraCondition = {
            !Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
        }) {
            "com.miui.personalassistant.device.DeviceAdapter".toClass().method {
                name = "create"
            }.giveAll().hookAll {
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