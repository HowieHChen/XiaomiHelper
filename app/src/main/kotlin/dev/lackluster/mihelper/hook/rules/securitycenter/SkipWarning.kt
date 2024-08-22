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

package dev.lackluster.mihelper.hook.rules.securitycenter

import android.os.Handler
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipWarning : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SKIP_WARNING) {
            "android.widget.TextView".toClass().method {
                name = "setEnabled"
                param(BooleanType)
            }.hook {
                before {
                    this.args(0).set(true)
                }
            }
            try {
                val mInnerClasses = "com.miui.permcenter.privacymanager.InterceptBaseFragment".toClass().declaredClasses
                var mHandlerClass: Class<*>? = null
                for (mInnerClass in mInnerClasses) {
                    if (Handler::class.java.isAssignableFrom(mInnerClass)) {
                        mHandlerClass = mInnerClass
                        break
                    }
                }
                if (mHandlerClass != null) {
                    mHandlerClass.constructor().hookAll {
                        before {
                            if (this.args.size == 2) {
                                this.args(1).set(0)
                            }
                        }
                    }
                    mHandlerClass.method {
                        returnType = Void.TYPE
                        param(IntType)
                    }.ignored().hook {
                        before {
                            this.args(0).set(0)
                        }
                    }
                }
            }
            catch (_: Throwable) {
                YLog.info("Failed to find class: com.miui.permcenter.privacymanager.InterceptBaseFragment")
            }
        }
    }
}