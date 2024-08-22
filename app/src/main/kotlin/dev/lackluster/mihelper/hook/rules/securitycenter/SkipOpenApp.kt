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

import android.annotation.SuppressLint
import android.text.SpannableString
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipOpenApp : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SKIP_OPEN_APP) {
            "android.widget.TextView".toClass().method {
                name = "setText"
                param(CharSequenceClass)
            }.hook {
                after {
                    val textView = this.instance as TextView
                    if (this.args.isNotEmpty() && (this.args(0).any() as? SpannableString ?: return@after).toString() == textView.context.resources.getString(
                            textView.context.resources.getIdentifier(
                                "button_text_accept",
                                "string",
                                textView.context.packageName
                            )
                        )
                    ) {
                        textView.performClick()
                    }
                }
            }
        }
    }
}