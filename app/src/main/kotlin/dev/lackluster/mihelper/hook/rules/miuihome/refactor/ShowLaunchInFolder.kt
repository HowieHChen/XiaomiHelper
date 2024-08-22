/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2024 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import android.view.View
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.FloatType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.utils.Prefs

object ShowLaunchInFolder : YukiBaseHooker() {
    private val launchScale =
        Prefs.getFloat(Refactor.SHOW_LAUNCH_IN_FOLDER_SCALE, Pref.DefValue.HomeRefactor.SHOW_LAUNCH_IN_FOLDER_SCALE)

    override fun onHook() {
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "setScreenContentAlpha", FloatType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val launcher = param?.thisObject ?: return
                    val ratio = param.args[0] as Float
                    val mSearchBar = XposedHelpers.getObjectField(launcher, "mSearchBar") as? FrameLayout ?: return
                    mSearchBar.alpha = ratio
                    param.result = null
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "changeScreenContent", FloatType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val launcher = param?.thisObject ?: return
                    if (BlurRefactorEntry.isInNormalEditing(launcher)) {
                        param.result = null
                        return
                    }
                    val ratio = param.args[0] as Float
                    val mScreenContent = XposedHelpers.getObjectField(launcher, "mScreenContent") as? FrameLayout ?: return
                    val scale = launchScale + ratio * (1 - launchScale)
                    mScreenContent.scaleX = scale
                    mScreenContent.scaleY = scale
                    param.result = null
                }
            }
        )
//        XposedHelpers.findAndHookMethod(
//            "com.miui.home.launcher.Launcher", this.appClassLoader,
//            "onScreenContentHide",
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam?) {
//                    param?.result = null
//                }
//            }
//        )
        XposedHelpers.findAndHookMethod(
            "com.miui.home.launcher.Launcher", this.appClassLoader,
            "setupAnimations",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val launcher = param?.thisObject ?: return
                    val mScreenContentHideAnimator = XposedHelpers.getObjectField(launcher, "mScreenContentHideAnimator")
                    XposedHelpers.setObjectField(mScreenContentHideAnimator, "mAnimatorListenerAdapter", null)
                }
            }
        )
        "com.miui.home.launcher.Folder".toClass().method {
            name = "tellItemIconIsOnAnimation"
        }.hook {
            after {
                val inAnim = this.args(0).boolean()
                if (!inAnim) {
                    val folderInfo = XposedHelpers.getObjectField(this.instance, "mInfo")
                    val opened = XposedHelpers.getObjectField(folderInfo, "opened") as Boolean
                    if (opened) {
                        (XposedHelpers.callMethod(folderInfo, "getBuddyIconView") as View).alpha = 0.0f
                    }
                }
            }
        }
        "com.miui.home.launcher.LauncherState".toClass().apply {
            method {
                name = "getWorkspaceAlpha"
            }.hook {
                before {
                    this.result = 1.0f
                }
            }
            method {
                name = "getHotseatAlpha"
            }.hook {
                before {
                    val launcher = this.args(0).any() ?: return@before
                    this.result = if (BlurRefactorEntry.isInNormalEditing(launcher)) 0.0f else 1.0f
                }
            }
        }
    }
}