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

import android.os.SystemClock.uptimeMillis
import android.view.View
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped

object AdBlocker : StaticHooker() {
    private val clzShelf by "com.tencent.qqmusiclite.model.shelfcard.Shelf".lazyClassOrNull()
    private val fldShelfId by lazy {
        clzShelf?.resolve()?.firstFieldOrNull {
            name = "id"
            type = Int::class
        }?.toTyped<Int>()
    }
    private val ctorDialogResult by lazy {
        "com.tencent.qqmusiclite.dialog.DialogResult".toClassOrNull()?.resolve()?.firstConstructor {
            parameterCount = 2
            parameters(String::class, Boolean::class)
        }?.toTyped()
    }
    private var View.lastFixUptime by extraOf<Long>("LAST_FIX_UPTIME")
    private var View.homeFragment by extraOf<Any>("HomeFragment")
    private var View.preloaded by extraOf<Boolean>("PRELOADED")

    override fun onInit() {
        updateSelfState(Preferences.Music.AD_BLOCKER.get())
    }

    override fun onHook() {
        runCatching { handleHomePage() }.exceptionOrNull()?.let { e(it) }
        runCatching { handlePlayerPage() }.exceptionOrNull()?.let { e(it) }
        runCatching { handleSearch() }.exceptionOrNull()?.let { e(it) }
        runCatching { handlePopup() }.exceptionOrNull()?.let { e(it) }
        runCatching { handleVIPCard() }.exceptionOrNull()?.let { e(it) }
        "com.tencent.config.AppConfig".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "isNeedAd"
            }?.hook {
                result(false)
            }
        }
    }

    private fun handleHomePage() {
        // 首页 VIP 入口
        "com.tencent.qqmusiclite.business.main.vipentry.data.HomeVipEntryRepo".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "requestHomeVipEntryInfo"
            }?.hook {
                result(null)
            }
        }
        // 播放条上方推广
        "com.tencent.qqmusiclite.ui.minibar.MinibarBubbleManager".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "tryShowBubble"
            }?.hook {
                result(null)
            }
        }
        // 首页列表推广楼层
        "com.tencent.qqmusiclite.fragment.home.adapter.HomeAdapter".toClassOrNull()?.apply {
            val shelfIdBlacklist = setOf(
                10001, // VIP 信息
                10002, // 顶部推广位
            )
            resolve().firstMethodOrNull {
                name = "update"
                parameters(List::class, Boolean::class, Boolean::class)
            }?.hook {
                val newArgs = args.toTypedArray()
                (newArgs[0] as? List<*>)?.filterNot {
                    clzShelf?.isInstance(it) == true && fldShelfId?.get(it) in shelfIdBlacklist
                }?.let {
                    newArgs[0] = it
                }
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "refreshAd"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = null
                result(proceed(newArgs))
            }
        }
        // 直接触发加载第二页
        val clzHomeFragment = "com.tencent.qqmusiclite.fragment.home.HomeFragment".toClassOrNull()
        clzHomeFragment?.apply {
            val fldFooterView = resolve().firstFieldOrNull {
                name = "mFooterView"
            }?.toTyped<View>()
            resolve().firstMethodOrNull {
                name = "onCreateView"
            }?.hook {
                val ori = proceed()
                fldFooterView?.get(thisObject)?.homeFragment = thisObject
                result(ori)
            }
        }
        "com.tencent.qqmusic.business.timeline.ui.refreshable.LoadMoreFooterView".toClassOrNull()?.apply {
            val fldNoMoreRecommend = clzHomeFragment?.resolve()?.firstFieldOrNull {
                name = "noMoreRecommend"
            }?.toTyped<Boolean>()
            val fldIsLoadingMore = clzHomeFragment?.resolve()?.firstFieldOrNull {
                name = "isLoadingMore"
            }?.toTyped<Boolean>()
            val metGetViewModel = clzHomeFragment?.resolve()?.firstMethodOrNull {
                name = "getViewModel"
            }?.toTyped<Any>()
            val metLoadMore = "com.tencent.qqmusiclite.fragment.home.HomeViewModel".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "loadMore"
            }?.toTyped<Unit>()
            val clzStatus = $$"com.tencent.qqmusic.business.timeline.ui.refreshable.LoadMoreFooterView$Status".toClassOrNull()
            val metValueOf = clzStatus?.resolve()?.firstMethodOrNull {
                name = "valueOf"
                parameters(String::class)
                modifiers(Modifiers.STATIC)
            }?.toTyped<Any>()
            val statusLoadMore = metValueOf?.invoke(null, "LOAD_MORE")
            val statusLoading = metValueOf?.invoke(null, "LOADING")
            val metSetStatus = resolve().firstMethodOrNull {
                name = "setStatus"
                parameterCount = 1
            }?.self?.apply { makeAccessible() }
            metSetStatus?.hook {
                val ori = proceed()
                val footer = thisObject as? View
                val status = getArg(0)
                val homeFragment = footer?.homeFragment
                val preloaded = footer?.preloaded ?: false
                d { "LoadMoreFooterView.setStatus: status $status homeFragment $homeFragment" }
                if (status == statusLoadMore && homeFragment != null && !preloaded) {
                    val noMore = fldNoMoreRecommend?.get(homeFragment) ?: true
                    val loading = fldIsLoadingMore?.get(homeFragment) ?: true
                    d { "LoadMoreFooterView.setStatus: noMore $noMore loading $loading" }
                    if (!noMore && !loading) {
                        footer.post {
                            footer.preloaded = true
                            fldIsLoadingMore.set(homeFragment, true)
                            metSetStatus.invoke(footer, statusLoading)
                            metGetViewModel?.invoke(homeFragment)?.let { vm ->
                                metLoadMore?.invoke(vm)
                                d { "LoadMoreFooterView.setStatus: loadMore invoke" }
                            }
                        }
                    }
                }
                result(ori)
            }
        }
        // 冷启列表卡住
        "com.tencent.qqmusic.business.timeline.ui.refreshable.RefreshableRecyclerViewNew".toClassOrNull()?.apply {
            val clzHomeRecyclerView = "com.tencent.qqmusiclite.fragment.home.view.HomeRecyclerView".toClassOrNull()
            val status = resolve().firstFieldOrNull {
                name = "mStatus"
                superclass()
            }?.toTyped<Int>()
            resolve().firstMethodOrNull {
                name = "setRefreshHeaderContainerHeight"
            }?.hook {
                val ori = proceed()
                val rv = thisObject as View
                val newHeight = args[0] as? Int ?: 0
                val status = status?.get(thisObject)
                if (status == 3 && clzHomeRecyclerView?.isInstance(thisObject) == true) {
                    if (uptimeMillis() - (rv.lastFixUptime ?: 0L) > 32 || newHeight == 0) {
                        var top: View = rv
                        while (top.parent is View) {
                            top = top.parent as View
                        }
                        top.requestLayout()
                        top.invalidate()
                        d { "RefreshableRecyclerViewNew.setRefreshHeaderContainerHeight: force requestLayout" }
                    }
                    rv.lastFixUptime = uptimeMillis()
                }
                result(ori)
            }
        }
    }

    private fun handlePopup() {
        // 部分首页活动、音质引导弹窗由 Hippy 独立的 HybridViewActivity 承载，并非普通 Dialog。
        // dump 中 MainActivity 均被透明的 HybridViewActivity 覆盖；其启动前会经由
        // HippyManager.runHippyActivity，第二个参数的 HybridViewEntry 可用 hippyPageEntry
        // 区分推广弹窗与常规 Hippy 页面（评论、分类页等），因此在此处阻断不会误伤正常跳转。
        val hippyPopupEntries = setOf(
            "PayoffPopup",
            "FeePopup",
            "CommonPopup",
            "FreemodePopup",
            "ReplayPopup",
            "LaunchPopup",
            "DaySignAdDialog",
            // 以下三个 entry 原先由 ExcellentTryStrategy / DtsEffectManager 单独拦截，
            // 最终同样会通过 toEntryHippyImpl 启动 HybridViewActivity。
            "QualityguidePopup",
            "QualityexpiredPopup",
            "DtsPopup",
        )
        val getHippyPageEntry = "com.tencent.qqmusiccommon.hybrid.HybridViewEntry".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getHippyPageEntry"
        }?.toTyped<String>()
        "com.tencent.qqmusiccommon.hippy.HippyManager".toClassOrNull()?.apply {
            // 兼容 runHippyActivity(Context, entry, Bundle) 及带 animation 参数的重载。
            setOf(3, 4).forEach { parameterCount ->
                resolve().firstMethodOrNull {
                    name = "runHippyActivity"
                    this.parameterCount = parameterCount
                }?.hook {
                    val entry = args[1]
                    val pageEntry = getHippyPageEntry?.invoke(entry)
                    if (pageEntry in hippyPopupEntries) {
                        d { "HippyManager.runHippyActivity: block popup, entry=$pageEntry" }
                        return@hook result(null)
                    }
                    result(proceed())
                }
            }
        }
        // 播放器和横屏 DTS 气泡
        "com.tencent.qqmusiclite.business.supersound.DtsEffectManager".toClassOrNull()?.apply {
            setOf(
                "showPlayerDtsPopup",
                "showHsPlayerDtsPopup",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    result(false)
                }
            }
        }
        // 全局推广弹窗
        "com.tencent.qqmusiclite.activity.main.usecase.operation.OperationDialogLauncher".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "checkAndShowDialog"
            }?.hook {
                result(ctorDialogResult?.newInstance(null, false))
            }
        }
        // 播放条推广
        "com.tencent.qqmusiclite.ui.minibar.MiniBarController".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setMinibarBubbleVisibility"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = false
                result(proceed(newArgs))
            }
        }
        // 蓝牙设备弹窗
        "com.tencent.qqmusiclite.business.bluetooth.headphone.DeviceFrequency".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "canShowHeadPhoneDialog"
            }?.hook {
                result(false)
            }
        }
        // 首页悬浮推广数据
        "com.tencent.qqmusiclite.business.main.promote.data.MainPromoteRepo".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "requestMainPromoteInfo"
            }?.hook {
                result(null)
            }
        }
    }

    private fun handlePlayerPage() {
        "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
            setOf(
                "refreshPlayTipsImpl",          // 播放页封面悬浮标签推广
                "buildingBlocks",               // 点击收藏后推荐
                "setProgressBarFixedEntrance",  // 进度条右侧推广
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    result(null)
                }
            }
        }
    }

    private fun handleSearch() {
        // 搜素框轮播
        "com.tencent.qqmusiclite.manager.search.SearchManager".toClassOrNull()?.apply {
            setOf(
                "preLoadHintKey",
                "startHintCarousel",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    result(null)
                }
            }
        }
        // 搜索页K歌热榜
        "com.tencent.qqmusiclite.fragment.search.model.SearchViewModel".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "requestKgHotWordNew"
            }?.hook {
                result(null)
            }
        }
        // 搜索页热榜
        "com.tencent.qqmusic.core.find.SearchHotWordRespGson".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "getBusinessNameList"
            }?.hook {
                result(null)
            }
        }
    }

    private fun handleVIPCard() {
        "com.tencent.qqmusiclite.ui.LoginLayoutViewHolder".toClassOrNull()?.apply {
            val getVipBuyLabel = resolve().firstMethodOrNull {
                name = "getVipBuyLabel"
            }?.toTyped<TextView>()
            resolve().firstMethodOrNull {
                name = "setVipTextList"
            }?.hook {
                val newArgs = args.toTypedArray()
                val textList = newArgs[0] as? List<*>
                val expireDate = textList?.firstOrNull {
                    it is String && it.contains("/")
                } ?: textList?.firstOrNull {
                    it is String && it.contains("到期")
                }
                newArgs[0] = listOfNotNull(expireDate)
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "setBackgroundByVipLevel"
                parameters(Int::class, Boolean::class, Boolean::class)
            }?.hook {
                val newArgs = args.toTypedArray()
                val level = newArgs[0] as? Int ?: 0
                val isSvip = newArgs[1] == true
                val isVip = newArgs[1] == true
                if (!isVip && !isSvip) {
                    newArgs[2] = true
                }
                if (level !in 1..10) {
                    newArgs[0] = 10
                }
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "setAutoPlayFlag"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = false
                result(proceed(newArgs))
            }
            resolve().firstMethodOrNull {
                name = "setVipBuy"
            }?.hook {
                getVipBuyLabel?.invoke(thisObject)?.visibility = View.GONE
                result(null)
            }
            setOf(
                "showNextAction",
                "startShakeAndShimmerAnimation",
                "startVipUpgradeAnimation",
            ).forEach { methodName ->
                resolve().firstMethodOrNull {
                    name = methodName
                }?.hook {
                    result(null)
                }
            }
        }
        "com.tencent.qqmusiclite.ui.VipLabelView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "setSuffixText"
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[0] = ""
                result(proceed(newArgs))
            }
        }
        "com.tencent.qqmusiclite.ui.MyVipDunningView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "show"
                parameters(Boolean::class)
            }?.hook {
                result(null)
            }
        }
    }
}