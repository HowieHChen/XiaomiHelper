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
import android.content.Context
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomInstallSource : YukiBaseHooker() {
    private val installStartClass by lazy {
        "com.miui.packageInstaller.InstallStart".toClassOrNull()
    }
    private val viewHolderClass by lazy {
        "com.miui.packageInstaller.ui.listcomponets.AppInfoViewObject\$ViewHolder".toClass()
    }
    private val appInfoViewHolderClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("context.ge…ta.versionName)", StringMatchType.Contains)
                addUsingString("context.ge…o?.versionName)", StringMatchType.Contains)
            }
        }
    }
    private val appInfoMethod by lazy {
        DexKit.findMethodsWithCache("app_info_holder") {
            matcher {
                paramCount = 0
                returnType = "void"
            }
            searchClasses = appInfoViewHolderClass
        }
    }
    private val getCallingPackageMethod by lazy {
        DexKit.findMethodWithCache("calling_pkg") {
            matcher {
                paramCount = 1
                paramTypes("com.miui.packageInstaller.InstallStart")
                returnType = "java.lang.String"
                addCaller("Lcom/miui/packageInstaller/InstallStart;->onCreate(Landroid/os/Bundle;)V")
            }
            searchPackages("com.miui.packageInstaller")
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
            installStartClass?.apply {
                method {
                    name = "getCallingPackage"
                }.ignored().hook {
                    after {
                        val realPackageName = this.result<String>() ?: return@after
                        val activity = this.instance<Activity>()
                        val realAppLabel = activity.packageManager.let {
                            it.getApplicationInfo(realPackageName, 0).loadLabel(it)
                        }.toString()
                        val fakeAppLabel = activity.packageManager.let {
                            it.getApplicationInfo(sourcePackageName, 0).loadLabel(it)
                        }
                        appLabel = "$realAppLabel ($fakeAppLabel)"
                        this.result = sourcePackageName
                    }
                }
            }
            getCallingPackageMethod?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val realPackageName = this.result<String>() ?: return@after
                    val activity = this.args(0).cast<Activity>() ?: return@after
                    val realAppLabel = activity.packageManager.let {
                        it.getApplicationInfo(realPackageName, 0).loadLabel(it)
                    }.toString()
                    val fakeAppLabel = activity.packageManager.let {
                        it.getApplicationInfo(sourcePackageName, 0).loadLabel(it)
                    }
                    appLabel = "$realAppLabel ($fakeAppLabel)"
                    this.result = sourcePackageName
                }
            }
            appInfoMethod.map {
                it.getMethodInstance(appClassLoader!!)
            }.hookAll {
                after {
                    val viewHolder = this.instance.current().field {
                        type = viewHolderClass
                    }.any() ?: return@after
                    val context = this.instance.current().method {
                        returnType = ContextClass
                        superClass()
                    }.invoke<Context>() ?: return@after
                    viewHolder.current().method {
                        name = "getTvInstallSource"
                        returnType = TextViewClass
                    }.invoke<TextView>()?.text = context.getString(
                        ResourcesUtils.dialog_install_source, appLabel
                    )
                }
            }
        }
    }
}