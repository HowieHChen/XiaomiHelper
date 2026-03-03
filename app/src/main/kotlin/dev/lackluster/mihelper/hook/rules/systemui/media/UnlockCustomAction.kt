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

package dev.lackluster.mihelper.hook.rules.systemui.media

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object UnlockCustomAction : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.MediaControl.UNLOCK_ACTION) {
            "com.miui.systemui.notification.NotificationSettingsManager".toClassOrNull()?.apply {
                val fldHiddenCustomActionsList = resolve().firstFieldOrNull {
                    name = "mHiddenCustomActionsList"
                }
                val fldHiddenCustomActionsListLocal = resolve().firstFieldOrNull {
                    name = "mHiddenCustomActionsListLocal"
                }
                resolve().firstConstructor().hook {
                    after {
                        fldHiddenCustomActionsList?.copy()?.of(this.instance)?.set(emptyList<String>())
                        fldHiddenCustomActionsListLocal?.copy()?.of(this.instance)?.set(emptyList<String>())
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onCloudDataUpdated"
                }?.hook {
                    after {
                        fldHiddenCustomActionsList?.copy()?.of(this.instance)?.set(emptyList<String>())
                        fldHiddenCustomActionsListLocal?.copy()?.of(this.instance)?.set(emptyList<String>())
                    }
                }
                resolve().firstMethodOrNull {
                    name = "onLocalDataUsed"
                }?.hook {
                    after {
                        fldHiddenCustomActionsList?.copy()?.of(this.instance)?.set(emptyList<String>())
                        fldHiddenCustomActionsListLocal?.copy()?.of(this.instance)?.set(emptyList<String>())
                    }
                }
            }
        }
    }
}