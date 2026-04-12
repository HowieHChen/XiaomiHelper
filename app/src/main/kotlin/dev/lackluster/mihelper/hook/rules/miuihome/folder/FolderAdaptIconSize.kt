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
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object FolderAdaptIconSize : StaticHooker() {
    private val clzFolderIcon2x2 by "com.miui.home.folder.FolderIcon2x2".lazyClassOrNull()
    private val clzFolderInfo by "com.miui.home.folder.FolderInfo".lazyClassOrNull()
    private val clzBaseFolderIconPreviewContainer2X2 by "com.miui.home.folder.BaseFolderIconPreviewContainer2X2".lazyClassOrNull()
    private val clzFolderIconPreviewContainer2X2_4 by "com.miui.home.folder.FolderIconPreviewContainer2X2_4".lazyClassOrNull()
    private val clzFolderIconPreviewContainer2X2_9 by "com.miui.home.folder.FolderIconPreviewContainer2X2_9".lazyClassOrNull()

    override fun onInit() {
        updateSelfState(Preferences.MiuiHome.FOLDER_ADAPT_SIZE.get())
    }

    override fun onHook() {
        clzFolderIcon2x2?.apply {
            val fldInfo = resolve().firstFieldOrNull {
                name = "mInfo"
                superclass()
            }?.toTyped<Any>()
            val metGetMPreviewContainer = resolve().firstMethodOrNull {
                name = "getMPreviewContainer"
                superclass()
            }?.toTyped<Any>()
            val metCount = clzFolderInfo?.resolve()?.firstMethodOrNull {
                name = "count"
            }?.toTyped<Int>()
            val metGetMRealPvChildCount = clzBaseFolderIconPreviewContainer2X2?.resolve()?.firstMethodOrNull {
                name = "getMRealPvChildCount"
                superclass()
            }?.toTyped<Int>()
            val metSetMItemsMaxCount = clzBaseFolderIconPreviewContainer2X2?.resolve()?.firstMethodOrNull {
                name = "setMItemsMaxCount"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "createOrRemoveView"
            }?.hook {
                val info = fldInfo?.get(thisObject)
                val container = metGetMPreviewContainer?.invoke(thisObject)
                val infoCount = info?.let { it1 -> metCount?.invoke(it1) }
                val realPvChildCount = container?.let { it1 -> metGetMRealPvChildCount?.invoke(it1) }
                if (infoCount != realPvChildCount && infoCount != null && realPvChildCount != null) {
                    val num = Character.getNumericValue(container::class.java.simpleName.last())
                    if (realPvChildCount - num < 3) {
                        metSetMItemsMaxCount?.invoke(
                            container,
                            if (infoCount <= num) num else num + 3
                        )
                    }
                }
                result(proceed())
            }
            val metSetMLargeIconNum = resolve().firstMethodOrNull {
                name = "setMLargeIconNum"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "addItemOnclickListener"
            }?.hook {
                val container = metGetMPreviewContainer?.invoke(thisObject)
                container?.let { it1 -> metGetMRealPvChildCount?.invoke(it1) }?.let { realPvChildCount ->
                    val num = Character.getNumericValue(container::class.java.simpleName.last())
                    metSetMLargeIconNum?.invoke(
                        thisObject,
                        if (realPvChildCount <= num) num else num - 1
                    )
                }
                result(proceed())
            }
        }
        clzFolderIconPreviewContainer2X2_4?.apply {
            val metGetMRealPvChildCount = resolve().firstMethodOrNull {
                name = "getMRealPvChildCount"
                superclass()
            }?.toTyped<Int>()
            val metSetMLargeIconNum = resolve().firstMethodOrNull {
                name = "setMLargeIconNum"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "preSetup2x2"
            }?.hook {
                metGetMRealPvChildCount?.invoke(thisObject)?.let { realPvChildCount ->
                    metSetMLargeIconNum?.invoke(
                        thisObject,
                        if (realPvChildCount <= 4) 4 else 3
                    )
                }
                result(proceed())
            }
        }
        clzFolderIconPreviewContainer2X2_9?.apply {
            val metGetMRealPvChildCount = resolve().firstMethodOrNull {
                name = "getMRealPvChildCount"
                superclass()
            }?.toTyped<Int>()
            val metSetMLargeIconNum = resolve().firstMethodOrNull {
                name = "setMLargeIconNum"
                superclass()
            }?.toTyped<Unit>()
            resolve().firstMethodOrNull {
                name = "preSetup2x2"
            }?.hook {
                metGetMRealPvChildCount?.invoke(thisObject)?.let { realPvChildCount ->
                    metSetMLargeIconNum?.invoke(
                        thisObject,
                        if (realPvChildCount <= 9) 9 else 8
                    )
                }
                result(proceed())
            }
        }
    }
}