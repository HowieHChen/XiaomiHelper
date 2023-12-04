package dev.lackluster.mihelper.hook.rules.systemui

import android.app.WallpaperColors
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import kotlin.math.min
import kotlin.math.sqrt


object MediaControlMonet : YukiBaseHooker() {
    private val autoReverseColor by lazy {
        Prefs.getBoolean(PrefKey.SYSTEMUI_NOTIF_MC_MONET_REVERSE, false)
    }
    private fun reverseColor(bitmap: Bitmap): Boolean {
        val height = bitmap.height
        val width = bitmap.width
        var pixelColor: Int
        var rChannel: Int
        var gChannel: Int
        var bChannel: Int
        var count = 0
        var bright: Long = 0
        for (y in 0 until height step 10) {
            for (x in 0 until width/2 step 5) {
                count++
                pixelColor = bitmap.getPixel(x, y)
                rChannel = Color.red(pixelColor)
                gChannel = Color.green(pixelColor)
                bChannel = Color.blue(pixelColor)
                bright += (minOf(rChannel, gChannel, bChannel) + maxOf(rChannel, gChannel, bChannel))/2
            }
        }
        return bright/count > 127
    }
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_MC_MONET) {
            "com.android.systemui.statusbar.notification.mediacontrol.ProcessArtworkTask".toClass()
                .method {
                    name = "processArtwork"
                }
                .hook {
                    before {
                        val artworkDrawable = this.args(0).any() as Drawable
                        var intrinsicWidth = artworkDrawable.intrinsicWidth
                        var intrinsicHeight = artworkDrawable.intrinsicHeight
                        val i = intrinsicWidth * intrinsicHeight
                        if (i > 62500) {
                            val sqrt = sqrt(62500f / i)
                            intrinsicWidth = (intrinsicWidth * sqrt).toInt()
                            intrinsicHeight = (intrinsicHeight * sqrt).toInt()
                        }
                        val intrinsicMin = min(intrinsicWidth, intrinsicHeight)
                        val wallpaperColors = WallpaperColors.fromDrawable(artworkDrawable)
                        val style = "com.android.systemui.monet.Style".toClass().enumConstants[6]
                        val colorScheme = "com.android.systemui.monet.ColorScheme".toClass().constructor {
                            paramCount = 3
                            param(WallpaperColors::class.java, VagueType, VagueType)
                        }.get().call(wallpaperColors, true, style)
                        val accent1List = colorScheme?.current()?.method {
                            name = "getAccent1"
                        }?.list<Int?>()
                        val accent2List = colorScheme?.current()?.method {
                            name = "getAccent2"
                        }?.list<Int?>()
//                        val neutral1List = colorScheme?.current()?.method {
//                            name = "getNeutral1"
//                        }?.list<Int?>()
//                        val neutral2List = colorScheme?.current()?.method {
//                            name = "getNeutral2"
//                        }?.list<Int?>()
                        val primaryTextColor: Int?
                        val secondaryTextColor: Int?
                        val backgroundColor: Int?
                        val albumCoverColor: Int?
                        if (autoReverseColor) {
                            if (reverseColor(artworkDrawable.toBitmap(intrinsicMin, intrinsicMin))) {
                                primaryTextColor = accent2List?.get(9)
                                secondaryTextColor = accent2List?.get(8)
                                backgroundColor = accent1List?.get(2)
                                albumCoverColor = accent1List?.get(3)
                            }
                            else {
                                primaryTextColor = accent1List?.get(2)
                                secondaryTextColor = accent1List?.get(3)
                                backgroundColor = accent2List?.get(9)
                                albumCoverColor = accent2List?.get(8)
                            }
                        }
                        else {
                            primaryTextColor = accent1List?.get(2)
                            secondaryTextColor = accent1List?.get(3)
                            backgroundColor = accent2List?.get(8)
                            albumCoverColor = accent2List?.get(7)
                        }
                        val direction = this.instance.current().field {
                            name = "direction"
                        }.int()
                        val colorizeArtwork = this.instance.current().field {
                            name = "mColorizer"
                        }.any()?.current()?.method {
                            name = "colorize"
                            paramCount = 3
                        }?.call(artworkDrawable, backgroundColor, direction == 1) as Bitmap

                        val result = "com.android.systemui.statusbar.notification.mediacontrol.ProcessArtworkTask\$Result".toClass()
                            .constructor().get().call() ?: throw Throwable("Failed to create new instance")
                        result.current().field {
                            name = "bitmap"
                        }.set(colorizeArtwork)
                        result.current().field {
                            name = "backgroundColor"
                        }.set(backgroundColor)
                        result.current().field {
                            name = "foregroundColor"
                        }.set(albumCoverColor)
                        result.current().field {
                            name = "primaryTextColor"
                        }.set(primaryTextColor)
                        result.current().field {
                            name = "secondaryTextColor"
                        }.set(secondaryTextColor)
                        this.result = result
                    }
                }
        }
    }
}