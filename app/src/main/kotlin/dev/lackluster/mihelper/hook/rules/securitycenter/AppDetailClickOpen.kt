/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references YukiVoyager
 * Copyright (C) 2023 hosizoraru

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
import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AppDetailClickOpen : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN) {
            "com.miui.appmanager.ApplicationsDetailsActivity".toClassOrNull()?.method {
                name = "initView"
            }?.hook {
                after {
                    val activity = this.instance as Activity
                    val appDetailTitle = activity.findViewById<View?>(
                        activity.resources.getIdentifier("am_detail_title", "id", activity.packageName)
                    )
                    val appIcon =appDetailTitle.findViewById<ImageView>(
                        activity.resources.getIdentifier("app_manager_details_appicon", "id", activity.packageName)
                    )
                    appIcon.setOnClickListener {
                        activity.startActivity(
                            activity.packageManager.getLaunchIntentForPackage(
                                activity.intent.getStringExtra("package_name") ?: return@setOnClickListener
                            )
                        )
                    }
                }
            }
        }
    }
}