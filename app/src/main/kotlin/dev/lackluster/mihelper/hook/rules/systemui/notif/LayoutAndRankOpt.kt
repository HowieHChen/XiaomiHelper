/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.notif

import android.service.notification.StatusBarNotification
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object LayoutAndRankOpt : YukiBaseHooker() {
    private const val KEY_PRIORITY = "KEY_PRIORITY"
    private const val KEY_PEOPLE_TYPE = "KEY_PEOPLE_TYPE"
    private const val KEY_PIN_TEMP = "KEY_PIN_TEMP"
    private const val KEY_PIN_TYPE = "KEY_PIN_TYPE"
    private const val VAL_PEOPLE_ALERTING = 2
    private const val VAL_PRIORITY_PEOPLE = 1

    private val enableOpt = Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.LAYOUT_RANK_OPT, false)
    private val hideSectionHeader = Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_HEADER, true)
    private val hideSectionGap = Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_GAP, true)
    private val rerank = Prefs.getBoolean(Pref.Key.SystemUI.NotifCenter.LR_OPT_RERANK, true)

    private val clzPipelineEntry by lazy {
        "com.android.systemui.statusbar.notification.collection.PipelineEntry".toClassOrNull()
    }
    private val metGetPeopleType by lazy {
        "com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator".toClassOrNull()
            ?.resolve()?.firstMethodOrNull {
                name = "getPeopleType"
            }
    }
    private val getRepresentativeEntry by lazy {
        clzPipelineEntry
            ?.resolve()
            ?.firstMethodOrNull {
                name = "getRepresentativeEntry"
            }
            ?.self
    }
    private val mSbn by lazy {
        "com.android.systemui.statusbar.notification.collection.NotificationEntry".toClassOrNull()
            ?.resolve()
            ?.firstFieldOrNull {
                name = "mSbn"
            }
            ?.self
    }
    private val mIsFocusNotification by lazy {
        "com.android.systemui.statusbar.notification.ExpandedNotification".toClassOrNull()
            ?.resolve()
            ?.firstFieldOrNull {
                name = "mIsFocusNotification"
            }
            ?.self
    }
    private val mIsSystemApp by lazy {
        "com.android.systemui.statusbar.notification.ExpandedNotification".toClassOrNull()
            ?.resolve()
            ?.firstFieldOrNull {
                name = "mIsSystemApp"
            }
            ?.self
    }

    override fun onHook() {
        if (!enableOpt) return
        if (hideSectionHeader) {
            "com.android.systemui.statusbar.notification.collection.provider.SectionHeaderVisibilityProvider".toClassOrNull()?.apply {
                val neverShowSectionHeaders = resolve().firstFieldOrNull {
                    name = "neverShowSectionHeaders"
                }
                val sectionHeadersVisible = resolve().firstFieldOrNull {
                    name = "sectionHeadersVisible"
                }
                resolve().firstConstructor().hook {
                    after {
                        neverShowSectionHeaders?.copy()?.of(this.instance)?.set(true)
                        sectionHeadersVisible?.copy()?.of(this.instance)?.set(false)
                    }
                }
            }
            "com.android.systemui.statusbar.notification.collection.coordinator.MiuiNotifCoordinator\$trackNotifUnoccludedState$1$1".toClassOrNull()?.apply {
                resolve().optional().firstMethodOrNull {
                    name = "emit"
                }?.hook {
                    before {
                        this.args(0).set(java.lang.Boolean.valueOf(true))
                    }
                }
            }
        }
        if (hideSectionGap) {
            "com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm".toClassOrNull()?.apply {
                val mGapHeight = resolve().firstFieldOrNull {
                    name = "mGapHeight"
                }
                val mGapHeightOnLockscreen = resolve().firstFieldOrNull {
                    name = "mGapHeightOnLockscreen"
                }
                resolve().firstMethodOrNull {
                    name = "initView"
                }?.hook {
                    after {
                        mGapHeight?.copy()?.of(this.instance)?.set(0.0f)
                        mGapHeightOnLockscreen?.copy()?.of(this.instance)?.set(0.0f)
                    }
                }
            }
        }
        if (rerank) {
            "com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator\$peopleAlertingSectioner$1".toClassOrNull()?.apply {
                val type = resolve().firstField {
                    type(Int::class)
                }
                val conversationCoordinator = resolve().firstField {
                    type("com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator")
                }
                resolve().firstMethodOrNull {
                    name = "isInSection"
                }?.hook {
                    after {
                        val pipelineEntry = this.args(0).any() ?: return@after
                        val isConversation = this.result<Boolean>() ?: false
                        if (isConversation) {
                            val nowType = type.copy().of(this.instance).get<Int>() ?: 0
                            val peopleType = conversationCoordinator.copy().of(this.instance).get()?.let {
                                metGetPeopleType?.copy()?.of(it)?.invoke<Int>(pipelineEntry)
                            } ?: 0
                            XposedHelpers.setAdditionalInstanceField(
                                pipelineEntry,
                                KEY_PRIORITY,
                                if (nowType == 1) VAL_PRIORITY_PEOPLE else VAL_PEOPLE_ALERTING
                            )
                            XposedHelpers.setAdditionalInstanceField(
                                pipelineEntry,
                                KEY_PEOPLE_TYPE,
                                peopleType
                            )
                            this.result = false
                        }
                    }
                }
            }
            "com.android.systemui.statusbar.notification.collection.legacy.NotificationRankingManagerInjectorImplKt\$miuiRankingComparator$1".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "compare"
                }?.hook {
                    replaceAny {
                        val notification1 = this.args(0).any()?.let { pipelineEntry ->
                            getRepresentativeEntry?.invoke(pipelineEntry)?.let { notificationEntry ->
                                mSbn?.get(notificationEntry) as? StatusBarNotification
                            }
                        }
                        val notification2 = this.args(1).any()?.let { pipelineEntry ->
                            getRepresentativeEntry?.invoke(pipelineEntry)?.let { notificationEntry ->
                                mSbn?.get(notificationEntry) as? StatusBarNotification
                            }
                        }
                        val tail1 = notification1?.notification?.extras?.getBoolean("miui.showAtTail", false)
                        val tail2 = notification2?.notification?.extras?.getBoolean("miui.showAtTail", false)
                        val isSystemWarning1 = isSystemWarning(notification1)
                        val isSystemWarning2 = isSystemWarning(notification2)
                        val isFocusNotification1 = isFocusNotification(notification1)
                        val isFocusNotification2 = isFocusNotification(notification2)
                        val priorityMessage1 = priorityMessage(this.args(0).any())
                        val priorityMessage2 = priorityMessage(this.args(1).any())
                        return@replaceAny if (tail1 != tail2) {
                            if (tail2 == true) -1 else 1
                        } else if (isSystemWarning1 != isSystemWarning2) {
                            if (isSystemWarning1) -1 else 1
                        } else if (isFocusNotification1 != isFocusNotification2) {
                            if (isFocusNotification1) -1 else 1
                        } else if (priorityMessage1 != priorityMessage2) {
                            if (priorityMessage1 > priorityMessage2) -1 else 1
                        } else {
                            0
                        }
                    }
                }
            }
        }
    }

    private fun isSystemWarning(notification: Any?): Boolean {
        return notification is StatusBarNotification &&
                mIsSystemApp?.getBoolean(notification) == true &&
                notification.notification?.extras?.getBoolean("miui.systemWarnings", false) == true
    }

    private fun isFocusNotification(notification: Any?): Boolean {
        return notification != null &&
                mIsFocusNotification?.getBoolean(notification) == true
    }

    private fun priorityMessage(pipelineEntry: Any?): Int {
        val priority =
            if (pipelineEntry == null) {
                0
            } else {
                XposedHelpers.getAdditionalInstanceField(
                    pipelineEntry,
                    KEY_PRIORITY
                ) as? Int ?: 0
            }
        val peopleType =
            if (pipelineEntry == null) {
                0
            } else {
                XposedHelpers.getAdditionalInstanceField(
                    pipelineEntry,
                    KEY_PEOPLE_TYPE
                ) as? Int ?: 0
            }
        return priority * 10000 + peopleType
    }
}