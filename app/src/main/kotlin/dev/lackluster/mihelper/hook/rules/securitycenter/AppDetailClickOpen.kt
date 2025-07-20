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
import android.content.pm.PackageInfo
import android.view.View
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.android.PackageInfoClass
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AppDetailClickOpen : YukiBaseHooker() {
    private val applicationsDetailsFragment by lazy {
        "com.miui.appmanager.fragment.ApplicationsDetailsFragment".toClassOrNull()
    }
    private val appDetailTitlePreference by lazy {
        "com.miui.appmanager.widget.AppDetailTitlePreference".toClassOrNull()
    }

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN) {
            if (applicationsDetailsFragment != null) {
                applicationsDetailsFragment?.apply {
                    method {
                        name = "initView"
                    }.hook {
                        after {
                            val appDetailTitle = this.instance.current().field {
                                type = appDetailTitlePreference
                            }.any() ?: return@after
                            val packageInfo = this.instance.current().field {
                                type = PackageInfoClass
                            }.cast<PackageInfo>() ?: return@after
                            XposedHelpers.setAdditionalInstanceField(
                                appDetailTitle,
                                "packageName",
                                packageInfo.packageName
                            )
                            appDetailTitle.current().method {
                                name = "notifyChanged"
                                superClass()
                            }.call()
                        }
                    }
                }
                appDetailTitlePreference?.apply {
                    method {
                        name = "onBindViewHolder"
                    }.hook {
                        after {
                            val packageName = XposedHelpers.getAdditionalInstanceField(
                                this.instance,
                                "packageName"
                            ) as? String ?: return@after
                            this.instance.current().field {
                                type = ImageViewClass
                            }.cast<ImageView>()?.setOnClickListener {
                                it.context.let { context ->
                                    context.packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                "com.miui.appmanager.ApplicationsDetailsActivity".toClassOrNull()?.apply {
                    method {
                        name = "initView"
                    }.hook {
                        after {
                            val activity = this.instance<Activity>()
                            val appDetailTitle = activity.findViewById<View?>(
                                activity.resources.getIdentifier("am_detail_title", "id", activity.packageName)
                            ) ?: return@after
                            val appIcon = appDetailTitle.findViewById<ImageView>(
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
    }
}