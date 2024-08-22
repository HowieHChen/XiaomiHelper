/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/module/hook/home/other/FreeformShortcutMenu.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 * Add more hooks, modified by HowieHChen (howie.dev@outlook.com) on 03/20/2024

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

package dev.lackluster.mihelper.hook.rules.miuihome

import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.ic_shortcut_menu_small_window_icon
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.ic_start_new_window
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.ic_task_add_pair
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.ic_task_small_window
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.ic_task_small_window_pad
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.small_window
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils.start_new_window
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs


object ShortcutMenu : YukiBaseHooker() {
    private val addFreeform = Prefs.getBoolean(Pref.Key.MiuiHome.SHORTCUT_FREEFORM, false)
    private val addInstance = Prefs.getBoolean(Pref.Key.MiuiHome.SHORTCUT_INSTANCE, false)
    private val mViewDarkModeHelper by lazy {
        "com.miui.home.launcher.util.ViewDarkModeHelper".toClassOrNull()
    }
    private val mSystemShortcutMenu by lazy {
        "com.miui.home.launcher.shortcuts.SystemShortcutMenu".toClassOrNull()
    }
    private val mSystemShortcutMenuItem by lazy {
        "com.miui.home.launcher.shortcuts.SystemShortcutMenuItem".toClassOrNull()
    }
    private val mAppShortcutMenu by lazy {
        "com.miui.home.launcher.shortcuts.AppShortcutMenu".toClassOrNull()
    }
//    private val mShortcutMenuItem by lazy {
//        "com.miui.home.launcher.shortcuts.ShortcutMenuItem".toClassOrNull()
//    }
    private val mAppDetailsShortcutMenuItem by lazy {
        "com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$AppDetailsShortcutMenuItem".toClassOrNull()
    }
    private val mActivityUtilsCompat by lazy {
        "com.miui.launcher.utils.ActivityUtilsCompat".toClassOrNull()
    }
//    private val mRecentsAndFSGestureUtils by lazy {
//        "com.miui.home.launcher.RecentsAndFSGestureUtils".toClassOrNull()
//    }
    private var injectResources = false
    override fun onHook() {
        if (!addFreeform && !addInstance) return
        var mContext: Context? = null
        if (Device.isPad) {
            "com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$MultipleSmallWindowShortcutMenuItem".toClassOrNull()?.method {
                name = "isValid"
            }?.giveAll()?.hookAll {
                replaceToTrue()
            }
        } else {
            "com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$SmallWindowShortcutMenuItem".toClassOrNull()?.method {
                name = "isValid"
            }?.giveAll()?.hookAll {
                replaceToTrue()
            }
        }
        val mActivity = Activity::class.java

        try {
            mViewDarkModeHelper?.method {
                name = "onConfigurationChanged"
            }?.giveAll()?.hookAll {
                after {
                    XposedHelpers.callStaticMethod(mSystemShortcutMenuItem, "createAllSystemShortcutMenuItems")
                }
            }
//            mShortcutMenuItem?.method {
//                name = "getShortTitle"
//            }?.giveAll()?.hookAll {
//                after {
//                    if ((this.result as String) == "应用信息") {
//                        this.result = "信息"
//                    }
//                    if ((this.result as String) == "新建窗口") {
//                        this.result = "多开"
//                    }
//                }
//            }
            mActivity.method {
                name = "onCreate"
            }.giveAll().hookAll {
                after {
                    mContext = this.instance as Context
                    if (small_window == 0 || start_new_window == 0) {
                        mContext?.injectModuleAppResources()
                        injectResources = true
                    }
                }
            }
            mAppDetailsShortcutMenuItem?.method {
                name = "getOnClickListener"
            }?.giveAll()?.hookAll {
                before {
                    val mShortTitle = this.instance.current().method {
                        name = "getShortTitle"
                        superClass()
                    }.call() as CharSequence

                    when (mShortTitle) {
//                        mContext?.getString(R.string.share_center) -> {
//                            XposedHelpers.callStaticMethod(mRecentsAndFSGestureUtils, "startWorld", mContext)
//                        }
                        mContext?.getString(adaptiveStringId(R.string.floating_window, small_window)) -> {
                            this.result = getFreeformOnClickListener(this.instance, false)
                        }
                        mContext?.getString(adaptiveStringId(R.string.new_task, start_new_window)) -> {
                            this.result = getFreeformOnClickListener(this.instance, true)
                        }
                    }
                }
            }
            mSystemShortcutMenu?.method {
                name = "getMaxShortcutItemCount"
            }?.giveAll()?.hookAll {
                replaceTo(6)
            }
            mAppShortcutMenu?.method {
                name = "getMaxShortcutItemCount"
            }?.giveAll()?.hookAll {
                replaceTo(6)
            }
            mSystemShortcutMenuItem?.method {
                name = "createAllSystemShortcutMenuItems"
            }?.giveAll()?.hookAll {
                after {
                    val mAllSystemShortcutMenuItems = XposedHelpers.getStaticObjectField(
                        mSystemShortcutMenuItem,
                        "sAllSystemShortcutMenuItems"
                    ) as List<*>
                    val sAllSystemShortcutMenuItems = ArrayList<Any?>()
                    val mNewTasksInstance = XposedHelpers.newInstance(mAppDetailsShortcutMenuItem)
                    val mSmallWindowInstance = XposedHelpers.newInstance(mAppDetailsShortcutMenuItem)
                    if (addInstance) {
                        mNewTasksInstance.current().method {
                            name = "setShortTitle"
                            superClass()
                        }.call(mContext?.getString(adaptiveStringId(R.string.new_task, start_new_window)))
                        mNewTasksInstance.current().method {
                            name = "setIconDrawable"
                            superClass()
                        }.call(
                            ContextCompat.getDrawable(
                                mContext!!,
                                if (ic_start_new_window != 0) ic_start_new_window else ic_task_add_pair
                            )
                        )
                        sAllSystemShortcutMenuItems.add(mNewTasksInstance)
                    }
                    if (addFreeform) {
                        mSmallWindowInstance.current().method {
                            name = "setShortTitle"
                            superClass()
                        }.call(mContext?.getString(adaptiveStringId(R.string.floating_window, small_window)))
                        mSmallWindowInstance.current().method {
                            name = "setIconDrawable"
                            superClass()
                        }.call(
                            ContextCompat.getDrawable(
                                mContext!!,
                                if (!Device.isPad) {
                                    ic_task_small_window
                                } else if (ic_shortcut_menu_small_window_icon != 0) {
                                    ic_shortcut_menu_small_window_icon
                                } else if (ic_task_small_window_pad != 0) {
                                    ic_task_small_window_pad
                                } else ic_task_small_window
                            )
                        )
                        sAllSystemShortcutMenuItems.add(mSmallWindowInstance)
                    }
                    val size = mAllSystemShortcutMenuItems.size
                    if (size >= 8) {
                        sAllSystemShortcutMenuItems.addAll(mAllSystemShortcutMenuItems.subList(2, size))
                    } else {
                        sAllSystemShortcutMenuItems.addAll(mAllSystemShortcutMenuItems)
                    }
                    XposedHelpers.setStaticObjectField(mSystemShortcutMenuItem, "sAllSystemShortcutMenuItems", sAllSystemShortcutMenuItems)
                }
            }
        } catch (e: Exception) {
            YLog.warn(e.toString())
        }
    }
    private fun adaptiveStringId(injectId: Int, hookedId: Int): Int {
        return if (injectResources) injectId else hookedId
    }
    private fun getFreeformOnClickListener(obj: Any, isNewTaskOnClick: Boolean): View.OnClickListener {
        return View.OnClickListener { view: View ->
            val intent = Intent()
            val mContext1 = view.context
            val mComponentName = obj.current().method {
                name = "getComponentName"
                superClass()
            }.call() as ComponentName
            intent.setAction("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.setComponent(mComponentName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (isNewTaskOnClick) {
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            val makeFreeformActivityOptions = XposedHelpers.callStaticMethod(
                mActivityUtilsCompat,
                "makeFreeformActivityOptions",
                mContext1,
                mComponentName.packageName
            ) as ActivityOptions?
            if (makeFreeformActivityOptions != null) {
                mContext1.startActivity(
                    intent,
                    makeFreeformActivityOptions.toBundle()
                )
            }
        }
    }
}