package dev.lackluster.mihelper.hook.rules.systemui

import android.app.WallpaperColors
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object MediaControlMonet : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_MC_MONET) {
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClassOrNull()
                ?.method {
                    name = "setForegroundColors"
                }
                ?.hook {
                    before {
                        val result = this.args(0).any()
                        val mediaViewHolder = this.instance.current().field {
                            name = "mMediaViewHolder"
                            superClass()
                        }.any()
                        if (result == null || mediaViewHolder == null) {
                            this.result = null
                        }
                        try {
                            val context: Context = (this.instance.current().field {
                                name = "mContext"
                                superClass()
                            }.any() ?: return@before) as Context
                            val isNight = (context.resources.configuration.uiMode and 48) == 32
                            val bitmap = (result?.current()?.field {
                                name = "bitmap"
                            }?.any() ?: return@before) as Bitmap
                            val key = result.current().field {
                                name = "key"
                            }.any()
                            if (key != null) {
                                this.instance.current().field {
                                    name = "mCurrentKey"
                                }.set(key as String)
                            }
                            val wallpaperColors = WallpaperColors.fromBitmap(bitmap)
                            val style = "com.android.systemui.monet.Style".toClass().enumConstants[6]
                            val colorScheme = "com.android.systemui.monet.ColorScheme".toClass().constructor {
                                paramCount = 3
                                param(WallpaperColors::class.java, VagueType, VagueType)
                            }.get().call(wallpaperColors, true, style)
                            val accentList = colorScheme?.current()?.method {
                                name = "getAccent1"
                            }?.call() as? List<Integer>
                            val primaryColor = accentList?.get(2)?.toInt()
                            val seamlessColor =
                                if (isNight) accentList?.get(2)?.toInt()
                                else accentList?.get(3)?.toInt()
                            if (primaryColor != null && seamlessColor != null) {
                                val value: ColorStateList = ColorStateList.valueOf(primaryColor)
                                val valueAlpha1: ColorStateList = value.withAlpha(192)
                                val valueAlpha2: ColorStateList = valueAlpha1.withAlpha(128)
                                (mediaViewHolder?.current()?.method {
                                    name = "getTitleText"
                                }?.call() as? TextView)?.setTextColor(primaryColor)
                                (mediaViewHolder?.current()?.method {
                                    name = "getAppName"
                                }?.call() as? TextView)?.setTextColor(primaryColor)
                                (mediaViewHolder?.current()?.method {
                                    name = "getArtistText"
                                }?.call() as? TextView)?.setTextColor(primaryColor)
                                (mediaViewHolder?.current()?.method {
                                    name = "getElapsedTimeView"
                                }?.call() as? TextView)?.setTextColor(primaryColor)
                                (mediaViewHolder?.current()?.method {
                                    name = "getTotalTimeView"
                                }?.call() as? TextView)?.setTextColor(primaryColor)
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction0"
                                }?.call() as? ImageView)?.imageTintList = value
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction0"
                                }?.call() as? ImageView)?.imageTintList = value
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction1"
                                }?.call() as? ImageView)?.imageTintList = value
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction2"
                                }?.call() as? ImageView)?.imageTintList = value
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction3"
                                }?.call() as? ImageView)?.imageTintList = value
                                (mediaViewHolder?.current()?.method {
                                    name = "getAction4"
                                }?.call() as? ImageView)?.imageTintList = value
                                val seekBar = (mediaViewHolder?.current()?.method {
                                    name = "getSeekBar"
                                }?.call() as? SeekBar)
                                seekBar?.thumbTintList = value
                                seekBar?.progressTintList = valueAlpha1
                                seekBar?.progressBackgroundTintList = valueAlpha2
                                (mediaViewHolder?.current()?.method {
                                    name = "getSeamlessIcon"
                                }?.call() as? ImageView)?.imageTintList = ColorStateList.valueOf(seamlessColor)
                            }
                        }
                        catch (tout: Throwable) {
                            YLog.info("Hook setForegroundColors failed!\\n${tout}")
                        }
                        this.result = null
                    }
                }
        }
    }
}