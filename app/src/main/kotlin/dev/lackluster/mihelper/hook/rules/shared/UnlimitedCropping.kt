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

package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object UnlimitedCropping : YukiBaseHooker() {
    private val mScreenCropViewMethodToNew by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("not in bound", StringMatchType.Equals)
                }
                usingNumbers(0.5f, 200)
                returnType = "int"
                modifiers = Modifier.FINAL
            }
        }
    }
    private val mScreenCropViewMethodToOld by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("fixImageBounds %f,%f", StringMatchType.Equals)
                }
                usingNumbers(0.5f, 200)
                returnType = "int"
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Gallery.UNLIMITED_CROP) {
            if (appClassLoader == null) return@hasEnable
            if (mScreenCropViewMethodToNew.isNotEmpty()) {
                val newMethods = mScreenCropViewMethodToNew.map { it.getMethodInstance(appClassLoader!!) }.toList()
                newMethods.hookAll {
                    replaceTo(0)
                }
            }
            val oldMethod = mScreenCropViewMethodToOld.singleOrNull()?.getMethodInstance(appClassLoader!!)
            oldMethod?.hook {
                replaceTo(0)
            }
        }

    }
}