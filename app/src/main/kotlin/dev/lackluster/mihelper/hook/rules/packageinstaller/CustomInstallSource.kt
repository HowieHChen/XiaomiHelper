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
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import org.luckypray.dexkit.query.enums.StringMatchType


object CustomInstallSource : YukiBaseHooker() {
    private var hostUid: Int? = null
    private var sourceUid: Int? = null

    private val clzInstallStart by lazy {
        "com.miui.packageInstaller.InstallStart".toClassOrNull()
    }
    private val clzViewHolder by lazy {
        $$"com.miui.packageInstaller.ui.listcomponets.AppInfoViewObject$ViewHolder".toClass()
    }
    private val clzAppInfoViewHolder by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("context.ge…ta.versionName)", StringMatchType.Contains)
                addUsingString("context.ge…o?.versionName)", StringMatchType.Contains)
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
                declaredClass(clzAppInfoViewHolder.first().name, StringMatchType.Equals)
                type(clzViewHolder)
            }
            searchClasses = clzAppInfoViewHolder
        }
    }

    private val customInstallSource = Prefs.getInt(Pref.Key.PackageInstaller.INSTALL_SOURCE, 0)
    private val sourcePackageName = when (customInstallSource) {
        1 -> "com.android.fileexplorer"
        2 -> "com.xiaomi.market"
        3 -> Prefs.getString(Pref.Key.PackageInstaller.SOURCE_PKG_NAME, "").takeIf {
            it?.isNotBlank() == true
        } ?: "com.android.fileexplorer"
        else -> "com.android.fileexplorer"
    }

    override fun onHook() {
        var appLabel: String? = null
        if(customInstallSource != 0) {
            if (appClassLoader == null) return
            Activity::class.apply {
                resolve().firstMethodOrNull {
                    name = "getLaunchedFromPackage"
                }?.hook {
                    after {
                        val activity = this.instance<Activity>()
                        val oriPkg = this.result<String>() ?: return@after
                        if (fromInstallStart(activity) && oriPkg != Scope.PACKAGE_INSTALLER) {
                            val realAppLabel = activity.packageManager.let {
                                it.getApplicationInfo(oriPkg, 0).loadLabel(it)
                            }
                            val fakeAppLabel = activity.packageManager.let {
                                it.getApplicationInfo(sourcePackageName, 0).loadLabel(it)
                            }
                            appLabel = "$realAppLabel ($fakeAppLabel)"
                            this.result = sourcePackageName
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getLaunchedFromUid"
                }?.hook {
                    after {
                        val activity = this.instance<Activity>()
                        val oriUid = this.result<Int>() ?: return@after
                        if (fromInstallStart(activity) && isFromOutside(activity, oriUid)) {
                            this.result = getSourceUid(activity) ?: 0
                        }
                    }
                }

            }
            val fldVH = fldViewHolder?.getFieldInstance(appClassLoader!!) ?: return
            val metGetTvInstallSource = clzViewHolder.resolve().firstMethodOrNull {
                name = "getTvInstallSource"
                returnType = TextView::class
            } ?: return
            metAppInfo.map {
                it.getMethodInstance(appClassLoader!!)
            }.hookAll {
                after {
                    val viewHolder = fldVH.get(this.instance)
                    val tvInstallSource = metGetTvInstallSource.copy().of(viewHolder).invoke<TextView>() ?: return@after
                    tvInstallSource.let {
                        it.text = it.context.getString(ResourcesUtils.dialog_install_source, appLabel)
                    }
                }
            }
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