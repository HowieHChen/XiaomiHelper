package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object WallpaperScaleRatio : YukiBaseHooker() {
    private const val defValue = 1.2f
    private val value by lazy {
        Prefs.getFloat(PrefKey.ANDROID_WALLPAPER_SCALE_RATIO, defValue)
    }
    override fun onHook() {
        if (value != defValue) {
            "com.android.server.wm.WallpaperController".toClass()
                .constructor()
                .hookAll {
                    after {
                        this.instance.current().field {
                            name = "mMaxWallpaperScale"
                        }.set(value)
                    }
                }
        }
    }
}