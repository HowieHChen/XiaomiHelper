package dev.lackluster.mihelper.hook.rules.screenshot

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
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
        hasEnable(PrefKey.SCREENSHOT_SAVE_AS_PNG) {
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
                        displayName = displayName.replace(".png", "").replace(".jpg", "")
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
                    if (MediaStore.Images.Media.EXTERNAL_CONTENT_URI == imgUri && displayName != null && displayName!!.contains(
                            "Screenshot"
                        )
                    ) {
                        val ext = ".png"
                        displayName = displayName!!.replace(".png", "").replace(".jpg", "")
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

            compressMethods.map { it.getMethodInstance(appClassLoader ?: return@hasEnable) }.toList()
                .hookAll {
                    after {
                        val compress = Bitmap.CompressFormat.PNG
                        this.args(4).set(compress)
                    }
                }

        }
    }
}