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

package dev.lackluster.mihelper.hook.rules.music

import android.view.View
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    private val shelfClass by lazy {
        "com.tencent.qqmusiclite.model.shelfcard.Shelf".toClassOrNull()
    }
    private val shelfIdField by lazy {
        shelfClass?.field {
            name = "id"
            type = IntType
        }?.give()
    }

    override fun onHook() {
        hasEnable(Pref.Key.Music.AD_BLOCKER) {
            "com.tencent.qqmusiclite.fragment.home.adapter.HomeAdapter".toClassOrNull()?.apply {
                method {
                    name = "update"
                }.hook {
                    before {
                        val list = this.args(0).list<Any>().toMutableList()
                        if (list.isNotEmpty() && shelfClass?.isInstance(list[0]) == true) {
                            list.firstOrNull {
                                shelfIdField?.get(it) == 10001
                            }?.let {
                                list.remove(it)
                            }
                            this.args(0).set(list)
                        }
                    }
                }
            }
            "com.tencent.qqmusiclite.fragment.my.MyViewModel".toClassOrNull()?.apply {
                method {
                    name = "loadAd"
                }.hook {
                    intercept()
                }
            }
            "com.tencent.qqmusiclite.business.main.promote.data.MainPromoteInfo".toClassOrNull()?.apply {
                method {
                    name = "isVisible"
                }.hook {
                    replaceToFalse()
                }
            }
            "com.tencent.qqmusiclite.business.main.promote.view.HomeVipEntryInfo".toClassOrNull()?.apply {
                method {
                    name = "isShow"
                }.hook {
                    replaceToFalse()
                }
            }
            val loginLayoutClass =
                "com.tencent.qqmusiclite.ui.LoginLayoutViewHolder".toClassOrNull()
                    ?: "com.tencent.qqmusiclite.ui.MyAssetsView\$LoginLayoutViewHolder".toClassOrNull()
            loginLayoutClass?.apply {
                method {
                    name = "setAutoPlayFlag"
                }.hook {
                    before {
                        this.args(0).setFalse()
                    }
                }
                method {
                    name = "setVipTextFirst"
                }.hook {
                    before {
                        val vipTextList = this.instance.current().field {
                            name = "vipTextList"
                        }.list<String>()
                        this.args(0).set(
                            vipTextList.firstOrNull {
                                it.contains("/")
                            } ?: vipTextList.firstOrNull { it.contains("到期") }
                        )
                    }
                }
                method {
                    name = "setVipTextSecond"
                }.hook {
                    before {
                        this.args(0).setNull()
                    }
                }
                method {
                    name = "setVipBuy"
                }.hook {
                    before {
                        this.instance.current().method {
                            name = "getVipBuyLabel"
                        }.invoke<TextView>()?.visibility = View.GONE
                        this.result = null
                    }
                }
                method {
                    name = "startShakeAndShimmerAnimation"
                }.ignored().hook {
                    intercept()
                }
                method {
                    name = "startVipUpgradeAnimation"
                }.ignored().hook {
                    intercept()
                }
            }
            "com.tencent.qqmusiclite.ui.VipLabelView".toClassOrNull()?.apply {
                method {
                    name = "setSuffixText"
                }.hook {
                    before {
                        this.args(0).set("")
                    }
                }
            }
            "com.tencent.qqmusiclite.freemode.ExcellentTryStrategy".toClassOrNull()?.apply {
                method {
                    name = "tryShowExcellentGuideAlert"
                }.give()?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false as Any?
                            }
                        }
                    )
                }
                method {
                    name = "checkNeedShowExcellentGuideAlert"
                }.give()?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false as Any?
                            }
                        }
                    )
                }
                method {
                    name = "checkCanShow"
                }.give()?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false as Any?
                            }
                        }
                    )
                }
                method {
                    name = "tryShowTryEndAlert"
                }.ignored().hook {
                    replaceToFalse()
                }
            }
            "com.tencent.config.AppConfig".toClassOrNull()?.apply {
                method {
                    name = "isNeedAd"
                }.hook {
                    replaceToFalse()
                }
            }
            "com.tencent.qqmusiclite.activity.main.usecase.operation.OperationDialogLauncher".toClassOrNull()?.apply {
                method {
                    name = "checkAndShowOperationDialog"
                }.hook {
                    intercept()
                }
            }
            "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
                method {
                    name = "setNewTipsTextAndShowWithFadeIn"
                }.hook {
                    intercept()
                }
            }
            "com.tencent.qqmusiclite.ui.minibar.MiniBarController".toClassOrNull()?.apply {
                method {
                    name = "setMinibarBubbleVisibility"
                }.hook {
                    before {
                        this.args(0).setFalse()
                    }
                }
            }
        }
    }
}