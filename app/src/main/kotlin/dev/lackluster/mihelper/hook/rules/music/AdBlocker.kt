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
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    private val clzShelf by lazy {
        "com.tencent.qqmusiclite.model.shelfcard.Shelf".toClassOrNull()
    }
    private val fldShelfId by lazy {
        clzShelf?.resolve()?.firstFieldOrNull {
            name = "id"
            type = Int::class
        }?.self?.apply {
            isAccessible = true
        }
    }
    private val ctorDialogResult by lazy {
        "com.tencent.qqmusiclite.dialog.DialogResult".toClassOrNull()?.resolve()?.firstConstructor {
            parameterCount = 2
            parameters(String::class, Boolean::class)
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Music.AD_BLOCKER) {
            simplifyHomePage()
            simplifyPlayerPage()
            simplifySearch()
            simplifyMyVIPCard()
            blockPopup()
            "com.tencent.config.AppConfig".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "isNeedAd"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }



    private fun simplifyHomePage() {
        val blacklist = setOf(
            10001, // VIP 信息
            10002, // 顶部推广位
        )
        "com.tencent.qqmusiclite.model.home.RecommendFeed".toClassOrNull()?.apply {
            resolve().firstConstructor {
                parameterCount = 4
            }.hook {
                before {
                    val list = this.args(0).list<Any>().toMutableList()
                    if (list.isNotEmpty() && clzShelf?.isInstance(list[0]) == true) {
                        list.removeAll {
                            fldShelfId?.get(it) in blacklist
                        }
                        this.args(0).set(list)
                    }
                }
            }
        }
//        "com.tencent.qqmusiclite.fragment.home.adapter.HomeAdapter".toClassOrNull()?.apply {
//            resolve().firstMethodOrNull {
//                name = "update"
//            }?.hook {
//                before {
//                    val list = this.args(0).list<Any>().toMutableList()
//                    if (list.isNotEmpty() && clzShelf?.isInstance(list[0]) == true) {
//                        list.removeAll {
//                            shelfIdField?.get(it) in blacklist
//                        }
//                        this.args(0).set(list)
//                    }
//                }
//            }
//        }
        // 右上角 VIP 图标
        "com.tencent.qqmusiclite.business.main.promote.view.HomeVipEntryInfo".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isShow"
            }?.hook {
                replaceToFalse()
            }
        }
    }

    private fun simplifyPlayerPage() {
        "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
            setOf(
                "refreshPlayTipsImpl",          // 播放页封面悬浮标签推广
                "buildingBlocks",               // 点击收藏后推荐
                "setProgressBarFixedEntrance",  // 进度条右侧推广
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    intercept()
                }
            }
        }
    }

    private fun simplifySearch() {
        // 搜素框轮播
        "com.tencent.qqmusiclite.manager.search.SearchManager".toClassOrNull()?.apply {
            setOf(
                "preLoadHintKey",
                "startHintCarousel",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    intercept()
                }
            }
        }
        // 搜索页K歌热榜
        "com.tencent.qqmusiclite.fragment.search.model.SearchViewModel".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "requestKgHotWordNew"
            }?.hook {
                intercept()
            }
        }
        // 搜索页热榜
        "com.tencent.qqmusic.core.find.SearchHotWordRespGson".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getBusinessNameList"
            }?.hook {
                intercept()
            }
        }
    }

    private fun simplifyMyVIPCard() {
        "com.tencent.qqmusiclite.ui.LoginLayoutViewHolder".toClassOrNull()?.apply {
            val vipTextList = resolve().firstFieldOrNull {
                name = "vipTextList"
            }
            val getVipBuyLabel = resolve().firstMethodOrNull {
                name = "getVipBuyLabel"
            }
            resolve().firstMethodOrNull {
                name = "setBackgroundIsVip"
            }?.hook {
                before {
                    this.args(0).setTrue()
                }
            }
            resolve().firstMethodOrNull {
                name = "setAutoPlayFlag"
            }?.hook {
                before {
                    this.args(0).setFalse()
                }
            }
            resolve().firstMethodOrNull {
                name = "setVipTextFirst"
            }?.hook {
                before {
                    val textList = vipTextList?.copy()?.of(this.instance)?.get<List<String>>() ?: return@before
                    this.args(0).set(
                        textList.firstOrNull { it.contains("/") } ?: textList.firstOrNull { it.contains("到期") }
                    )
                }
            }
            resolve().firstMethodOrNull {
                name = "setVipTextSecond"
            }?.hook {
                before {
                    this.args(0).setNull()
                }
            }
            resolve().firstMethodOrNull {
                name = "setVipBuy"
            }?.hook {
                before {
                    getVipBuyLabel?.copy()?.of(this.instance)?.invoke<TextView>()?.visibility = View.GONE
                    this.result = null
                }
            }
            setOf(
                "showNextAction",
                "startShakeAndShimmerAnimation",
                "startVipUpgradeAnimation",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    intercept()
                }
            }
        }
        "com.tencent.qqmusiclite.ui.VipLabelView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setSuffixText"
            }?.hook {
                before {
                    this.args(0).set("")
                }
            }
        }
    }

    private fun blockPopup() {
        // 臻品母带弹窗
        "com.tencent.qqmusiclite.freemode.ExcellentTryStrategy".toClassOrNull()?.apply {
            setOf(
                "tryShowExcellentGuideAlert",
                "checkNeedShowExcellentGuideAlert",
                "checkCanShow",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.self?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = java.lang.Boolean.FALSE
                            }
                        }
                    )
                }
            }
            resolve().firstMethodOrNull {
                name = "tryShowTryEndAlert"
            }?.hook {
                replaceToFalse()
            }
        }
        // DTS 弹窗
        "com.tencent.qqmusiclite.business.supersound.DtsEffectManager".toClassOrNull()?.apply {
            setOf(
                "tryShowDtsPopup",
                "checkCanShow",
                "needShowDtsMinibarBubble",
                "showPlayerDtsPopup",
                "showHsPlayerDtsPopup",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    replaceToFalse()
                }
            }
        }
        // 全局推广弹窗
        "com.tencent.qqmusiclite.activity.main.usecase.operation.OperationDialogLauncher".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "checkAndShowDialog"
            }?.hook {
                replaceTo(ctorDialogResult?.copy()?.create(null, false))
            }
        }
        // 首页悬浮推广
        "com.tencent.qqmusiclite.business.main.promote.data.MainPromoteInfoResponse".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getInfo"
            }?.hook {
                intercept()
            }
        }
        // 播放条推广
        "com.tencent.qqmusiclite.ui.minibar.MiniBarController".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setMinibarBubbleVisibility"
            }?.hook {
                before {
                    this.args(0).setFalse()
                }
            }
        }
        // 蓝牙设备弹窗
        "com.tencent.qqmusiclite.business.bluetooth.headphone.DeviceFrequency".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "canShowHeadPhoneDialog"
            }?.hook {
                replaceToFalse()
            }
        }
    }
}