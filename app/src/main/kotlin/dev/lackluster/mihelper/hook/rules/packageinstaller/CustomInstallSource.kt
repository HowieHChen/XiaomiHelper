/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/packageinstaller/UpdateSystemApp.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.app.Activity
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.asString
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomInstallSource : StaticHooker() {
    private var hostUid: Int? = null
    private var sourceUid: Int? = null

    private val clzInstallStart by "com.miui.packageInstaller.InstallStart".lazyClassOrNull()

    private val clzViewHolder by $$"com.miui.packageInstaller.ui.listcomponets.AppInfoViewObject$ViewHolder".lazyClass()

    private val clzAppInfoViewHolder by lazy {
        DexKit.withBridge {
            findClass {
                matcher {
                    addUsingString("context.ge…ta.versionName)", StringMatchType.Contains)
                    addUsingString("context.ge…o?.versionName)", StringMatchType.Contains)
                }
            }
        }
    }
    private val metAppInfo by lazy {
        DexKit.findMethodsWithCache("app_info_holder") {
            matcher {
                paramCount = 0
                returnType = "void"
            }
            searchClasses = clzAppInfoViewHolder
        }
    }
    private val fldViewHolder by lazy {
        DexKit.findFieldWithCache("app_info_view_holder") {
            matcher {
                declaredClass(clzAppInfoViewHolder.single().name, StringMatchType.Equals)
                type(clzViewHolder)
            }
            searchClasses = clzAppInfoViewHolder
        }
    }

    private val customInstallSource by Preferences.PackageInstaller.CUSTOM_INSTALL_SOURCE.lazyGet()
    private val sourcePackageName by lazy {
        when (customInstallSource) {
            1 -> "com.android.fileexplorer"
            2 -> "com.xiaomi.market"
            3 -> Preferences.PackageInstaller.INSTALL_SOURCE_PKG.get().takeIf {
                it.isNotBlank()
            } ?: "com.android.fileexplorer"
            else -> "com.android.fileexplorer"
        }
    }

    override fun onInit() {
        updateSelfState(customInstallSource != 0)
        if (customInstallSource != 0) {
            metAppInfo
            fldViewHolder
        }
    }

    override fun onHook() {
        var appLabel: String? = null
        Activity::class.apply {
            resolve().firstMethodOrNull {
                name = "getLaunchedFromPackage"
            }?.hook {
                val ori = proceed()
                val activity = thisObject as? Activity
                val oriPkg = ori as? String
                if (
                    activity != null && fromInstallStart(activity) &&
                    oriPkg != null && oriPkg != Scope.PACKAGE_INSTALLER
                ) {
                    val realAppLabel = activity.packageManager.let {
                        it.getApplicationInfo(oriPkg, 0).loadLabel(it)
                    }
                    val fakeAppLabel = activity.packageManager.let {
                        it.getApplicationInfo(sourcePackageName, 0).loadLabel(it)
                    }
                    appLabel = "$realAppLabel ($fakeAppLabel)"
                    result(sourcePackageName)
                } else {
                    result(ori)
                }
            }
            resolve().firstMethodOrNull {
                name = "getLaunchedFromUid"
            }?.hook {
                val ori = proceed()
                val activity = thisObject as? Activity
                val oriUid = ori as? Int
                if (
                    activity != null && fromInstallStart(activity) &&
                    oriUid != null && isFromOutside(activity, oriUid)
                ) {
                    result(getSourceUid(activity) ?: 0)
                } else {
                    result(ori)
                }
            }
        }
        val fldVH = fldViewHolder?.getFieldInstance(classLoader) ?: return
        val metGetTvInstallSource = clzViewHolder.resolve().firstMethodOrNull {
            name = "getTvInstallSource"
            returnType = TextView::class
        }?.toTyped<TextView>() ?: return
        metAppInfo.map {
            it.getMethodInstance(classLoader)
        }.hookAll {
            val ori = proceed()
            val viewHolder = fldVH.get(thisObject)
            metGetTvInstallSource.invoke(viewHolder)?.let {
                it.text = ResourcesUtils.dialog_install_source.asString(it.context, appLabel)
            }
            result(ori)
        }
    }

    private fun fromInstallStart(activity: Activity): Boolean {
        if (clzInstallStart?.isInstance(activity) == true) {
            return true
        }
        try {
            Thread.currentThread().stackTrace.forEach {
                if ("com.miui.packageInstaller.InstallStart" == it.className && "onCreate" == it.methodName) {
                    return true
                }
            }
        } catch (_: Throwable) {}
        return false
    }

    private fun isFromOutside(activity: Activity, uid: Int): Boolean {
        if (hostUid == null) {
            hostUid = activity.packageManager.getApplicationInfo(Scope.PACKAGE_INSTALLER, 0).uid
        }
        return uid != hostUid
    }

    private fun getSourceUid(activity: Activity): Int? {
        if (sourceUid == null) {
            sourceUid = activity.packageManager.getApplicationInfo(sourcePackageName, 0).uid
        }
        return sourceUid
    }
}