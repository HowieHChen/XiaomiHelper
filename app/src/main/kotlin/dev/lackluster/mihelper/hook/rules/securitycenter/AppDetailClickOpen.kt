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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped

object AppDetailClickOpen : StaticHooker() {
    private var Any.pkgName by extraOf("KEY_PKG_NAME", "")
    private val clzAppDetailTitlePreference by "com.miui.appmanager.widget.AppDetailTitlePreference".lazyClassOrNull()

    override fun onInit() {
        updateSelfState(Preferences.SecurityCenter.CLICK_ICON_TO_OPEN.get())
    }

    override fun onHook() {
        "com.miui.appmanager.fragment.ApplicationsDetailsFragment".toClassOrNull()?.apply {
            val fldAppDetailTitlePreference = resolve().firstFieldOrNull {
                type("com.miui.appmanager.widget.AppDetailTitlePreference")
            }?.toTyped<Any>()
            val fldPackageInfo = resolve().firstFieldOrNull {
                type(PackageInfo::class)
            }?.toTyped<PackageInfo>()
            val metNotifyChanged = clzAppDetailTitlePreference?.resolve()?.firstMethodOrNull {
                name = "notifyChanged"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "initView"
            }?.hook {
                val ori = proceed()
                val appDetailTitle = fldAppDetailTitlePreference?.get(thisObject)
                val packageInfo = fldPackageInfo?.get(thisObject)
                if (appDetailTitle != null && packageInfo != null) {
                    appDetailTitle.pkgName = packageInfo.packageName
                    metNotifyChanged?.invoke(appDetailTitle)
                }
                result(ori)
            }
        }
        clzAppDetailTitlePreference?.apply {
            val fldImageView = resolve().firstFieldOrNull {
                type(ImageView::class)
            }?.toTyped<ImageView>()
            resolve().firstMethodOrNull {
                name = "onBindViewHolder"
            }?.hook {
                val ori = proceed()
                val packageName = thisObject.pkgName
                val imageView = fldImageView?.get(thisObject)
                if (packageName != null && imageView != null) {
                    imageView.setOnClickListener {
                        val context = imageView.context
                        context.packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                            context.startActivity(intent)
                        }
                    }
                }
                result(ori)
            }
        }
    }
}