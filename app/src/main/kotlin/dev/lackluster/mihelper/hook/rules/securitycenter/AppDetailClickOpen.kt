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

import android.content.pm.PackageInfo
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.hasEnable
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField

object AppDetailClickOpen : YukiBaseHooker() {
    private const val KEY_PKG_NAME = "KEY_PKG_NAME"
    private val clzAppDetailTitlePreference by lazy {
        "com.miui.appmanager.widget.AppDetailTitlePreference".toClassOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.CLICK_ICON_TO_OPEN) {
            "com.miui.appmanager.fragment.ApplicationsDetailsFragment".toClassOrNull()?.apply {
                val fldAppDetailTitlePreference = resolve().firstFieldOrNull {
                    type("com.miui.appmanager.widget.AppDetailTitlePreference")
                }
                val fldPackageInfo = resolve().firstFieldOrNull {
                    type(PackageInfo::class)
                }
                val metNotifyChanged = clzAppDetailTitlePreference?.resolve()?.firstMethodOrNull {
                    name = "notifyChanged"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "initView"
                }?.hook {
                    after {
                        val appDetailTitle = fldAppDetailTitlePreference?.copy()?.of(this.instance)?.get() ?: return@after
                        val packageInfo = fldPackageInfo?.copy()?.of(this.instance)?.get<PackageInfo>() ?: return@after
                        appDetailTitle.setAdditionalInstanceField(KEY_PKG_NAME, packageInfo.packageName)
                        metNotifyChanged?.copy()?.of(appDetailTitle)?.invoke()
                    }
                }
            }
            clzAppDetailTitlePreference?.apply {
                val fldImageView = resolve().firstFieldOrNull {
                    type(ImageView::class)
                }
                resolve().firstMethodOrNull {
                    name = "onBindViewHolder"
                }?.hook {
                    after {
                        val packageName = this.instance.getAdditionalInstanceField<String>(KEY_PKG_NAME) ?: return@after
                        fldImageView?.copy()?.of(this.instance)?.get<ImageView>()?.let { imageView ->
                            imageView.setOnClickListener {
                                val context = imageView.context
                                context.packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                                    context.startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}