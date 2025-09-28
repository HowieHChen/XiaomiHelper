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
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
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
    private val dialogResultConstructor by lazy {
        "com.tencent.qqmusiclite.dialog.DialogResult".toClassOrNull()?.constructor {
            paramCount = 2
            param(StringClass, BooleanType)
        }?.give()
    }

    override fun onHook() {
        hasEnable(Pref.Key.Music.AD_BLOCKER) {
            "com.tencent.config.AppConfig".toClassOrNull()?.apply {
                method {
                    name = "isNeedAd"
                }.hook {
                    replaceToFalse()
                }
            }
            simplifyHomePage()
            simplifyPlayerPage()
            simplifySearch()
            simplifyMyVIPCard()
            blockDialog()
        }
    }



    private fun simplifyHomePage() {
        val blacklist = setOf(
            10001, // VIP 信息
            10002, // 顶部推广位
        )
        "com.tencent.qqmusiclite.fragment.home.adapter.HomeAdapter".toClassOrNull()?.apply {
            method {
                name = "update"
            }.hook {
                before {
                    val list = this.args(0).list<Any>().toMutableList()
                    if (list.isNotEmpty() && shelfClass?.isInstance(list[0]) == true) {
                        list.removeAll {
                            shelfIdField?.get(it) in blacklist
                        }
                        this.args(0).set(list)
                    }
                }
            }
        }
        // 右上角 VIP 图标
        "com.tencent.qqmusiclite.business.main.promote.view.HomeVipEntryInfo".toClassOrNull()?.apply {
            method {
                name = "isShow"
            }.hook {
                replaceToFalse()
            }
        }
        // 首页悬浮推广
        "com.tencent.qqmusiclite.business.main.promote.data.MainPromoteInfoResponse".toClassOrNull()?.apply {
            method {
                name = "getInfo"
            }.hook {
                intercept()
            }
        }
        // 播放条推广
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

    private fun simplifyPlayerPage() {
        "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
            // 播放页封面悬浮标签推广
            method {
                name = "refreshPlayTipsImpl"
            }.hook {
                intercept()
            }
            // 点击收藏后推荐
            method {
                name = "buildingBlocks"
            }.hook {
                intercept()
            }
        }
    }

    private fun simplifySearch() {
        // 搜素框轮播
        "com.tencent.qqmusiclite.manager.search.SearchManager".toClassOrNull()?.apply {
            method {
                name = "preLoadHintKey"
            }.hook {
                intercept()
            }
            method {
                name = "startHintCarousel"
            }.hook {
                intercept()
            }
        }
        // 搜索页K歌热榜
        "com.tencent.qqmusiclite.fragment.search.model.SearchViewModel".toClassOrNull()?.apply {
            method {
                name = "requestKgHotWordNew"
            }.hook {
                intercept()
            }
        }
        // 搜索页热榜
        "com.tencent.qqmusic.core.find.SearchHotWordRespGson".toClassOrNull()?.apply {
            method {
                name = "getBusinessNameList"
            }.hook {
                replaceTo(null)
            }
        }
    }

    private fun simplifyMyVIPCard() {
        val loginLayoutClass =
            "com.tencent.qqmusiclite.ui.LoginLayoutViewHolder".toClassOrNull()
                ?: "com.tencent.qqmusiclite.ui.MyAssetsView\$LoginLayoutViewHolder".toClassOrNull()
        loginLayoutClass?.apply {
            method {
                name = "setBackgroundIsVip"
            }.hook {
                before {
                    this.args(0).setTrue()
                }
            }
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
    }

    private fun blockDialog() {
        // 臻品母带弹窗
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
        // 全局推广弹窗
        "com.tencent.qqmusiclite.activity.main.usecase.operation.OperationDialogLauncher".toClassOrNull()?.apply {
            method {
                name = "checkAndShowOperationDialog"
            }.ignored().hook {
                intercept()
            }
            method {
                name = "checkAndShowDialog"
            }.ignored().hook {
                replaceTo(
                    dialogResultConstructor?.newInstance(null, false)
                )
            }
        }
    }
}