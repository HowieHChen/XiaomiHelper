/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references StarVoyager <https://github.com/hosizoraru/StarVoyager/blob/star/app/src/main/kotlin/star/sky/voyager/hook/hooks/screenshot/SaveAsPng.kt>
 * Copyright (C) 2023 hosizoraru

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

package dev.lackluster.mihelper.hook.rules.screenshot

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SaveAsPng : YukiBaseHooker() {
    private val compressMethods by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("context", StringMatchType.Equals)
                addUsingString("bitmap", StringMatchType.Equals)
                addUsingString("uri", StringMatchType.Equals)
                addUsingString("format", StringMatchType.Equals)
                returnType = "boolean"
                paramCount = 7
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Screenshot.SAVE_AS_PNG) {
            if (appClassLoader == null) return@hasEnable
            val contentResolverCls = "android.content.ContentResolver".toClass()
            contentResolverCls.method {
                name = "update"
                paramCount = 4
            }.hookAll {
                before {
                    val contentValues = this.args(1).any() as ContentValues
                    var displayName = contentValues.getAsString("_display_name")
                    if (displayName != null && displayName.contains("Screenshot")) {
                        val ext = ".png"
                        displayName = displayName
                            .replace(".png", "")
                            .replace(".jpg", "")
                            .replace(".webp", "") + ext
                        contentValues.put("_display_name", displayName)
                    }
                }
            }
            contentResolverCls.method {
                name = "insert"
                param(Uri::class.java, ContentValues::class.java)
            }.hookAll {
                before {
                    val imgUri = this.args(0).any() as Uri
                    val contentValues = this.args(1).any() as ContentValues
                    var displayName = contentValues.getAsString("_display_name")
                    if (MediaStore.Images.Media.EXTERNAL_CONTENT_URI == imgUri &&
                        displayName != null &&
                        displayName!!.contains("Screenshot")
                    ) {
                        val ext = ".png"
                        displayName = displayName!!
                            .replace(".png", "")
                            .replace(".jpg", "")
                            .replace(".webp", "") + ext
                        contentValues.put("_display_name", displayName)
                    }
                }
            }
            "android.graphics.Bitmap".toClass()
                .method {
                    name = "compress"
                }
                .hookAll {
                    after {
                        val compress = Bitmap.CompressFormat.PNG
                        this.args(0).set(compress)
                    }
                }
            compressMethods.map { it.getMethodInstance(appClassLoader!!) }.toList().hookAll {
                after {
                    val compress = Bitmap.CompressFormat.PNG
                    this.args(4).set(compress)
                }
            }
        }
    }
}