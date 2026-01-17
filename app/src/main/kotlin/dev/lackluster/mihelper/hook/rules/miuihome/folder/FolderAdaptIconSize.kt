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

package dev.lackluster.mihelper.hook.rules.miuihome.folder

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object FolderAdaptIconSize : YukiBaseHooker() {
    private val clzFolderIcon2x2 by lazy {
        "com.miui.home.folder.FolderIcon2x2".toClassOrNull()
    }
    private val clzFolderInfo by lazy {
        "com.miui.home.folder.FolderInfo".toClassOrNull()
    }
    private val clzBaseFolderIconPreviewContainer2X2 by lazy {
        "com.miui.home.folder.BaseFolderIconPreviewContainer2X2".toClassOrNull()
    }
    private val clzFolderIconPreviewContainer2X2_4 by lazy {
        "com.miui.home.folder.FolderIconPreviewContainer2X2_4".toClassOrNull()
    }
    private val clzFolderIconPreviewContainer2X2_9 by lazy {
        "com.miui.home.folder.FolderIconPreviewContainer2X2_9".toClassOrNull()
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.FOLDER_ADAPT_SIZE) {
            clzFolderIcon2x2?.apply {
                val fldInfo = resolve().firstFieldOrNull {
                    name = "mInfo"
                    superclass()
                }
                val metGetMPreviewContainer = resolve().firstMethodOrNull {
                    name = "getMPreviewContainer"
                    superclass()
                }
                val metCount = clzFolderInfo?.resolve()?.firstMethodOrNull {
                    name = "count"
                }
                val metGetMRealPvChildCount = clzBaseFolderIconPreviewContainer2X2?.resolve()?.firstMethodOrNull {
                    name = "getMRealPvChildCount"
                    superclass()
                }
                val metSetMItemsMaxCount = clzBaseFolderIconPreviewContainer2X2?.resolve()?.firstMethodOrNull {
                    name = "setMItemsMaxCount"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "createOrRemoveView"
                }?.hook {
                    before {
                        val info = fldInfo?.copy()?.of(this.instance)?.get()
                        val container = metGetMPreviewContainer?.copy()?.of(this.instance)?.invoke()
                        val infoCount = info?.let { it1 -> metCount?.copy()?.of(it1)?.invoke<Int>() }
                        val realPvChildCount = container?.let { it1 -> metGetMRealPvChildCount?.copy()?.of(it1)?.invoke<Int>() }
                        if (infoCount == realPvChildCount || infoCount == null || realPvChildCount == null) return@before
                        val num = Character.getNumericValue(container::class.java.simpleName.last())
                        if (realPvChildCount - num >= 3) {
                            return@before
                        }
                        metSetMItemsMaxCount?.copy()?.of(container)?.invoke(
                            if (infoCount <= num) num else num + 3
                        )
                    }
                }
                val metSetMLargeIconNum = resolve().firstMethodOrNull {
                    name = "setMLargeIconNum"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "addItemOnclickListener"
                }?.hook {
                    before {
                        val container = metGetMPreviewContainer?.copy()?.of(this.instance)?.invoke()
                        val realPvChildCount = container?.let { it1 -> metGetMRealPvChildCount?.copy()?.of(it1)?.invoke<Int>() } ?: return@before
                        val num = Character.getNumericValue(container::class.java.simpleName.last())
                        metSetMLargeIconNum?.copy()?.of(this.instance)?.invoke(
                            if (realPvChildCount <= num) num else num - 1
                        )
                    }
                }
            }
            clzFolderIconPreviewContainer2X2_4?.apply {
                val metGetMRealPvChildCount = resolve().firstMethodOrNull {
                    name = "getMRealPvChildCount"
                    superclass()
                }
                val metSetMLargeIconNum = resolve().firstMethodOrNull {
                    name = "setMLargeIconNum"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "preSetup2x2"
                }?.hook {
                    before {
                        val realPvChildCount = metGetMRealPvChildCount?.copy()?.of(this.instance)?.invoke<Int>() ?: return@before
                        metSetMLargeIconNum?.copy()?.of(this.instance)?.invoke(
                            if (realPvChildCount <= 4) 4 else 3
                        )
                    }
                }
            }
            clzFolderIconPreviewContainer2X2_9?.apply {
                val metGetMRealPvChildCount = resolve().firstMethodOrNull {
                    name = "getMRealPvChildCount"
                    superclass()
                }
                val metSetMLargeIconNum = resolve().firstMethodOrNull {
                    name = "setMLargeIconNum"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "preSetup2x2"
                }?.hook {
                    before {
                        val realPvChildCount = metGetMRealPvChildCount?.copy()?.of(this.instance)?.invoke<Int>() ?: return@before
                        metSetMLargeIconNum?.copy()?.of(this.instance)?.invoke(
                            if (realPvChildCount <= 9) 9 else 8
                        )
                    }
                }
            }
        }
    }
}